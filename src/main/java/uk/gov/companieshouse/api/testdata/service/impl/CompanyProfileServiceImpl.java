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
import uk.gov.companieshouse.api.testdata.model.entity.OverseasEntity;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CompanyProfileServiceImpl implements CompanyProfileService {

    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");
    private static final String LINK_STEM = "/company/";
    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(CompanyProfileServiceImpl.class));

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
        var dateInOneYearTwoWeeks =
                now.plusYears(1L).plusDays(14L).atStartOfDay(ZONE_ID_UTC).toInstant();
        var dateInOneYearNineMonths =
                now.plusYears(1L).plusMonths(9L).atStartOfDay(ZONE_ID_UTC).toInstant();

        if ("oversea-company".equals(companyType)) {
            var overseaJurisdiction = Jurisdiction.UNITED_KINGDOM;
            LOG.info("Creating oversea-company for " + companyNumber);

            var overseaCompany = new OverseasEntity();
            overseaCompany.setId(companyNumber);
            overseaCompany.setCompanyNumber(companyNumber);
            overseaCompany.setVersion(3L);
            overseaCompany.setHasMortgages(false);
            overseaCompany.setCompanyStatus("active");
            overseaCompany.setType("oversea-company");
            overseaCompany.setHasSuperSecurePscs(hasSuperSecurePscs);
            overseaCompany.setHasCharges(false);
            overseaCompany.setHasInsolvencyHistory(false);
            overseaCompany.setJurisdiction(overseaJurisdiction.toString());

            overseaCompany.getConfirmationStatement().setNextDue(dateInOneYearNineMonths);
            overseaCompany.getConfirmationStatement().setNextMadeUpTo(dateInOneYear);
            overseaCompany.setExternalRegistrationNumber("397377");

            overseaCompany.setUndeliverableRegisteredOfficeAddress(false);
            overseaCompany.setCompanyName("COMPANY " + companyNumber + " LIMITED");
            overseaCompany.setRegisteredOfficeIsInDispute(false);
            overseaCompany.setEtag(randomService.getEtag());
            overseaCompany.setSuperSecureManagingOfficerCount(0);
            overseaCompany.setRegisteredOfficeAddress(
                    addressService.getAddress(overseaJurisdiction));
            overseaCompany.setServiceAddress(addressService.getAddress(overseaJurisdiction));

            var foreignDetails = overseaCompany.new ForeignCompanyDetails();
            var accountingReq = foreignDetails.new AccountingRequirement();
            accountingReq.setTermsOfAccountPublication(
                    "accounts-publication-date-supplied-by-company");
            accountingReq.setForeignAccountType(
                    "accounting-requirements-of-originating-country-apply");

            var registry = foreignDetails.new OriginatingRegistry();
            registry.setName("Companies Registration Office Barbados");
            registry.setCountry("BARBADOS");

            foreignDetails.setGovernedBy("Barbados");
            foreignDetails.setLegalForm("Test Limited (Private Limited Company)");
            foreignDetails.setRegistrationNumber("123456");
            foreignDetails.setIsACreditFinancialInstitution(false);
            foreignDetails.setBusinessActivity("Manufacturer And Seller Of Cable Harnesses");
            foreignDetails.setAccountingRequirement(accountingReq);
            foreignDetails.setOriginatingRegistry(registry);

            var foreignAccounts = foreignDetails.new Accounts();
            var mustFileWithin = foreignAccounts.new MustFileWithin();
            mustFileWithin.setMonths("6");
            foreignAccounts.setMustFileWithin(mustFileWithin);

            var accountPeriodTo = foreignAccounts.new AccountPeriod();
            accountPeriodTo.setMonth("3");
            accountPeriodTo.setDay("31");
            foreignAccounts.setAccountPeriodTo(accountPeriodTo);

            var accountPeriodFrom = foreignAccounts.new AccountPeriod();
            accountPeriodFrom.setMonth("4");
            accountPeriodFrom.setDay("1");
            foreignAccounts.setAccountPeriodFrom(accountPeriodFrom);

            foreignDetails.setAccounts(foreignAccounts);
            overseaCompany.setForeignCompanyDetails(foreignDetails);

            var links = new Links();
            links.setSelf(LINK_STEM + companyNumber);
            overseaCompany.setLinks(links);

            var updated = overseaCompany.new Updated();
            updated.setAt(Instant.now());
            updated.setBy("1234hgfd8du7s");
            updated.setType("company_delta");
            overseaCompany.setUpdated(updated);
            overseaCompany.setDeltaAt(Instant.now());

            var accounts = overseaCompany.getAccounts();
            accounts.setNextDue(dateInOneYearNineMonths);
            accounts.setPeriodStart(dateNow);
            accounts.setPeriodEnd(dateInOneYear);
            accounts.setNextAccountsDueOn(dateInOneYearNineMonths);
            accounts.setNextAccountsOverdue(false);
            accounts.setNextMadeUpTo(dateInOneYear);
            accounts.setAccountingReferenceDateDay(String.valueOf(now.getDayOfMonth()));
            accounts.setAccountingReferenceDateMonth(String.valueOf(now.getMonthValue()));
            accounts.setOverdue(false);
            accounts.setLastAccountsMadeUpTo(dateOneYearAgo);
            accounts.setLastAccountsPeriodEndOn(dateOneYearAgo);
            accounts.setLastAccountsType("full");

            return repository.save(overseaCompany);
        }

        // Normal CompanyProfile branch
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
        profile.setUndeliverableRegisteredOfficeAddress(false);
        if (hasSuperSecurePscs != null) {
            profile.setHasSuperSecurePscs(hasSuperSecurePscs);
        }
        profile.setCompanyName("COMPANY " + companyNumber + " LIMITED");
        profile.setSicCodes(Collections.singletonList("71200"));

        var confirmationStatement = profile.getConfirmationStatement();
        confirmationStatement.setNextMadeUpTo(dateInOneYear);
        confirmationStatement.setOverdue(false);
        confirmationStatement.setNextDue(dateInOneYearTwoWeeks);

        profile.setRegisteredOfficeIsInDispute(false);
        profile.setCompanyStatus(Objects.requireNonNullElse(companyStatus, "active"));
        profile.setHasInsolvencyHistory(
                "dissolved".equals(Objects.requireNonNullElse(companyStatus, "")));
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
        links.setFilingHistory(LINK_STEM + companyNumber + "/filing-history");
        links.setOfficers(LINK_STEM + companyNumber + "/officers");
        links.setPersonsWithSignificantControlStatement(LINK_STEM + companyNumber
                + "/persons-with-significant-control-statement");
        return links;
    }
}
