package uk.gov.companieshouse.api.testdata.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.constants.ErrorMessageConstants;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.Links;
import uk.gov.companieshouse.api.testdata.model.companyprofile.Company;
import uk.gov.companieshouse.api.testdata.model.companyprofile.RegisteredOfficeAddress;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;

@Service
public class CompanyProfileServiceImpl implements DataService<Company> {

    private static final String COMPANY_PROFILE_DATA_NOT_FOUND = "company profile data not found";

    private CompanyProfileRepository repository;

    @Autowired
    public CompanyProfileServiceImpl(CompanyProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public Company create(String companyNumber) throws DataException {

        Company company = new Company();

        company.setCompanyName("Company " + companyNumber);
        company.setId(companyNumber);
        company.setCompanyNumber(companyNumber);
        company.setCompanyStatus("Active");
        company.setJurisdiction("england-wales");
        company.setRegisteredOfficeAddress(createRoa());
        company.setType("ltd");
        company.setLinks(createLinks(company));

        try {
            repository.save(company);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }

        return company;
    }

    @Override
    public void delete(String companyId) throws NoDataFoundException, DataException {

        Company existingCompany = repository.findByCompanyNumber(companyId);

        if (existingCompany == null) throw new NoDataFoundException(COMPANY_PROFILE_DATA_NOT_FOUND);

        try {
            repository.delete(existingCompany);
        } catch (MongoException e) {
            throw new DataException(ErrorMessageConstants.FAILED_TO_DELETE);
        }

    }

    private Links createLinks(Company company) {

        Links links = new Links();
        links.setSelf("/company/" + company.getCompanyNumber());
        links.setFilingHistory("/company/" + company.getCompanyNumber() + "/filing-history");
        links.setOfficers("/company/" + company.getCompanyNumber() + "/officers");
        links.setPersonsWithSignificantControl("/company/" + company.getCompanyNumber() + "/persons-with-significant-control");

        return links;
    }

    private RegisteredOfficeAddress createRoa() {

        RegisteredOfficeAddress registeredOfficeAddress = new RegisteredOfficeAddress();

        registeredOfficeAddress.setAddressLine1("10 Test Street");
        registeredOfficeAddress.setAddressLine2("test 2");
        registeredOfficeAddress.setCareOf("care of");
        registeredOfficeAddress.setCountry("England");
        registeredOfficeAddress.setLocality("Locality");
        registeredOfficeAddress.setPoBox("POBox");
        registeredOfficeAddress.setPostalCode("POSTCODE");
        registeredOfficeAddress.setPremises("premises");
        registeredOfficeAddress.setRegion("region");

        return registeredOfficeAddress;
    }

}
