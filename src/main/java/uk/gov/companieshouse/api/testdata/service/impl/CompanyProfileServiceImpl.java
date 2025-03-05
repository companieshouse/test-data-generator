package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CompanyProfileServiceImpl implements CompanyProfileService {

    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    private static final String LINK_STEM = "/company/";
    private static final String FILLING_HISTORY_STEM = "/filing-history";
    private static final String OFFICERS_STEM = "/officers";
    private static final String PSC_STATEMENT_STEM = "/persons-with-significant-control-statement";
    private static final String REGISTERS_STEM = "/registers";

    @Autowired
    private RandomService randomService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CompanyProfileRepository repository;

    private boolean hasCompanyRegisters = false;

    @Override
    public CompanyProfile create(CompanySpec spec) {
        final String companyNumber = spec.getCompanyNumber();
        final Jurisdiction jurisdiction = spec.getJurisdiction();
        final String companyStatus = spec.getCompanyStatus();
        final CompanyType companyType = spec.getCompanyType();
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
        if (spec.getRegisters() != null && !spec.getRegisters().isEmpty()) {
            hasCompanyRegisters = true;
        }
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
        profile.setType(companyType != null ? companyType.toString() : "ltd");
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
        links.setFilingHistory(LINK_STEM + companyNumber + FILLING_HISTORY_STEM);
        links.setOfficers(LINK_STEM + companyNumber + OFFICERS_STEM);
        links.setPersonsWithSignificantControlStatement(LINK_STEM + companyNumber
                + PSC_STATEMENT_STEM);
        if (hasCompanyRegisters) {
            links.setRegisters(LINK_STEM + companyNumber + REGISTERS_STEM);
        }
        return links;
    }

}
