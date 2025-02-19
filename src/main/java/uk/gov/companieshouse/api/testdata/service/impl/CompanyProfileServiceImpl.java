package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CompanyProfileServiceImpl implements CompanyProfileService {

    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    private static final String LINK_STEM = "/company/";

    @Autowired
    private RandomService randomService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CompanyProfileRepository repository;

    @Override
    public CompanyProfile create(CompanySpec spec) {
        final String companyNumber = spec.getCompanyNumber();
        final Jurisdiction jurisdiction = spec.getJurisdiction();
        final String companyStatus = spec.getCompanyStatus();
        final String companyType = spec.getCompanyType();
        final String subType = spec.getSubType();
        final Boolean hasSuperSecurePscs = spec.getHasSuperSecurePscs();

        LocalDate now = LocalDate.now();
        Instant dateOneYearAgo = now.minusYears(1L).atStartOfDay(ZONE_ID_UTC).toInstant();
        Instant dateNow = now.atStartOfDay(ZONE_ID_UTC).toInstant();
        Instant dateInOneYear = now.plusYears(1L).atStartOfDay(ZONE_ID_UTC).toInstant();
        Instant dateInOneYearTwoWeeks = now.plusYears(1L).plusDays(14L).atStartOfDay(ZONE_ID_UTC).toInstant();
        Instant dateInOneYearNineMonths = now.plusYears(1L).plusMonths(9L).atStartOfDay(ZONE_ID_UTC).toInstant();

        CompanyProfile profile = new CompanyProfile();

        profile.setId(companyNumber);
        profile.setLinks(createLinks(companyNumber));

        CompanyProfile.Accounts accounts = profile.getAccounts();
        accounts.setNextDue(dateInOneYearNineMonths);
        accounts.setPeriodStart(dateNow);
        accounts.setPeriodEnd(dateInOneYear);
        accounts.setNextAccountsDueOn(dateInOneYearNineMonths);
        accounts.setNextAccountsOverdue(false);
        accounts.setNextMadeUpTo(dateInOneYear);
        accounts.setAccountingReferenceDateDay(String.valueOf(now.getDayOfMonth()));
        accounts.setAccountingReferenceDateMonth(String.valueOf(now.getMonthValue()));

        profile.setCompanyNumber(companyNumber);
        profile.setDateOfCreation(dateOneYearAgo);
        profile.setType(Objects.requireNonNullElse(companyType, "ltd"));

        Map<String, String> partialDataOptions = createPartialDataCompanies();
        if (partialDataOptions.containsValue(companyType)) {
            profile.setPartialDataAvailable(partialDataOptions.get(companyType));
        }
        
        profile.setUndeliverableRegisteredOfficeAddress(false);

        if (hasSuperSecurePscs != null) {
            profile.setHasSuperSecurePscs(hasSuperSecurePscs);
        }
        profile.setCompanyName("COMPANY " + companyNumber + " LIMITED");
        profile.setSicCodes(Collections.singletonList("71200"));

        CompanyProfile.ConfirmationStatement confirmationStatement = profile.getConfirmationStatement();
        confirmationStatement.setNextMadeUpTo(dateInOneYear);
        confirmationStatement.setOverdue(false);
        confirmationStatement.setNextDue(dateInOneYearTwoWeeks);

        profile.setRegisteredOfficeIsInDispute(false);

        profile.setCompanyStatus(Objects.requireNonNullElse(companyStatus, "active"));
        profile.setHasInsolvencyHistory("dissolved".equals(Objects.requireNonNullElse(companyStatus, "")));

        profile.setEtag(this.randomService.getEtag());
        profile.setRegisteredOfficeAddress(addressService.getAddress(jurisdiction));
        profile.setJurisdiction(jurisdiction.toString());
        profile.setHasCharges(false);
        profile.setCanFile(true);

        if (subType != null) {
            profile.setIsCommunityInterestCompany(subType.equals("community-interest-company"));
            profile.setSubtype(subType);
        }

        return repository.save(profile);
    }

    private static Map<String, String> createPartialDataCompanies() {
        Map<String, String> partialDataOptions = new HashMap<>();
        partialDataOptions.put("investment-company-with-variable-capital",
                "full-data-available-from-financial-conduct-authority");
        partialDataOptions.put("assurance-company", "full-data-available-from-financial-conduct-authority");
        partialDataOptions.put("royal-charter", "full-data-available-from-the-company");
        partialDataOptions.put("industrial-and-provident-society",
                "full-data-available-from-financial-conduct-authority-mutuals-public-register");
        return partialDataOptions;
    }

    @Override
    public boolean delete(String companyId) {
        Optional<CompanyProfile> profile = repository.findByCompanyNumber(companyId);
        profile.ifPresent(repository::delete);
        return profile.isPresent();
    }

    @Override
    public boolean companyExists(String companyNumber) {
        return repository.findById(companyNumber).isPresent();
    }

    private Links createLinks(String companyNumber) {
        Links links = new Links();
        links.setSelf(LINK_STEM + companyNumber);
        links.setFilingHistory(LINK_STEM + companyNumber + "/filing-history");
        links.setOfficers(LINK_STEM + companyNumber + "/officers");
        links.setPersonsWithSignificantControlStatement(LINK_STEM + companyNumber
                + "/persons-with-significant-control-statement");
        return links;
    }

}
