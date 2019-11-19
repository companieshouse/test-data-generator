package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.constants.ErrorMessageConstants;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;


@Service
public class CompanyProfileServiceImpl implements DataService<CompanyProfile> {

    private static final String COMPANY_PROFILE_DATA_NOT_FOUND = "company profile data not found";
    private static final String LINK_STEM = "/company/";

    @Autowired
    private RandomService randomService;

    @Autowired
    private CompanyProfileRepository repository;

    @Override
    public CompanyProfile create(String companyNumber) throws DataException {

        LocalDate now = LocalDate.now();
        Instant dateNow = now.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant dateInOneYear = now.plusDays(365L).atStartOfDay(ZoneId.systemDefault()).toInstant();

        CompanyProfile profile = new CompanyProfile();

        profile.setId(companyNumber);
        profile.setLinks(createLinks(companyNumber));
        profile.setAccountsNextDue(dateInOneYear);
        profile.setPeriodStart(dateNow);
        profile.setPeriodEnd(dateInOneYear);
        profile.setNextAccountsDueOn(dateInOneYear);
        profile.setNextAccountsOverdue(false);
        profile.setAccountsNextMadeUpTo(dateInOneYear);
        profile.setAccountingReferenceDateDay(String.valueOf(now.getDayOfMonth()));
        profile.setAccountingReferenceDateMonth(String.valueOf(now.getMonthValue()));
        profile.setCompanyNumber(companyNumber);
        profile.setDateOfCreation(dateNow);
        profile.setType("ltd");
        profile.setUndeliverableRegisteredOfficeAddress(false);
        profile.setCompanyName("Company " + companyNumber + " LIMITED");
        profile.setSicCodes(Collections.singletonList("71200"));
        profile.setConfirmationStatementNextMadeUpTo(dateInOneYear);
        profile.setConfirmationStatementOverdue(false);
        profile.setConfirmationStatementNextDue(dateInOneYear);
        profile.setRegisteredOfficeIsInDispute(false);
        profile.setCompanyStatus("active");
        profile.setEtag(this.randomService.getEtag());
        profile.setHasInsolvencyHistory(false);
        profile.setRegisteredOfficeAddress(createRoa());
        profile.setJurisdiction("england-wales");
        profile.setHasCharges(false);
        profile.setCanFile(true);

        try {
            return repository.save(profile);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }
    }

    @Override
    public void delete(String companyId) throws NoDataFoundException, DataException {

        CompanyProfile existingCompany = repository.findByCompanyNumber(companyId);

        if (existingCompany == null) {
            throw new NoDataFoundException(COMPANY_PROFILE_DATA_NOT_FOUND);
        }

        try {
            repository.delete(existingCompany);
        } catch (MongoException e) {
            throw new DataException(ErrorMessageConstants.FAILED_TO_DELETE);
        }

    }

    private Links createLinks(String companyNumber) {

        Links links = new Links();
        links.setSelf(LINK_STEM + companyNumber);
        links.setFilingHistory(LINK_STEM + companyNumber + "/filing-history");
        links.setOfficers(LINK_STEM + companyNumber + "/officers");
        links.setPersonsWithSignificantControlStatement(LINK_STEM + companyNumber +
                "/persons-with-significant-control-statement");

        return links;
    }

    private Address createRoa() {

        Address registeredOfficeAddress = new Address();

        registeredOfficeAddress.setAddressLine1("Crown Way");
        registeredOfficeAddress.setCountry("United Kingdom");
        registeredOfficeAddress.setLocality("Cardiff");
        registeredOfficeAddress.setPostalCode("CF14 3UZ");

        return registeredOfficeAddress;
    }

}
