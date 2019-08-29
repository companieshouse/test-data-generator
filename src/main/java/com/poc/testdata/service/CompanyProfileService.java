package com.poc.testdata.service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import com.poc.testdata.constants.ErrorMessageConstants;
import com.poc.testdata.exception.DataException;
import com.poc.testdata.exception.NoDataFoundException;
import com.poc.testdata.model.CompanyProfile.Company;
import com.poc.testdata.model.CompanyProfile.RegisteredOfficeAddress;
import com.poc.testdata.model.Links;
import com.poc.testdata.repository.CompanyProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CompanyProfileService {

    @Autowired
    CompanyProfileRepository repository;

    private Company company;

    private final String COMPANY_PROFILE_DATA_NOT_FOUND = "company profile data not found";

    public String create() throws DataException {

        company = new Company();

        String companyNumber = getNewCompanyNumber();
        company.setCompanyName("Company " + companyNumber);
        company.setId(companyNumber);
        company.setCompanyNumber(companyNumber);
        company.setCompanyStatus("Active");
        company.setJurisdiction("england-wales");
        company.setRegisteredOfficeAddress(createRoa());
        company.setType("ltd");
        company.setLinks(createLinks());

        try{
            repository.save(company);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }

        return companyNumber;
    }

    public void delete(String companyId) throws NoDataFoundException, DataException {

        Company company = repository.findByCompanyNumber(companyId);

        if(company == null) throw new NoDataFoundException(COMPANY_PROFILE_DATA_NOT_FOUND);

        try {
            repository.delete(company);
        } catch (MongoException e) {
            throw new DataException(ErrorMessageConstants.FAILED_TO_DELETE);
        }

    }

    public String getCompanyNumber(){

        return company.getCompanyNumber();
    }

    private Links createLinks(){

        Links links = new Links();
        links.setSelf("/company/"+ company.getCompanyNumber());
        links.setFilingHistory("/company/"+ company.getCompanyNumber() + "/filing-history");
        links.setOfficers("/company/"+ company.getCompanyNumber() + "/officers");
        links.setPersonsWithSignificantControl("/company/"+ company.getCompanyNumber() + "/persons-with-significant-control");

        return links;
    }

    private RegisteredOfficeAddress createRoa(){

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

    private String getNewCompanyNumber(){

        Random rand = new Random();
        Integer num = rand.nextInt(90000000);
        return num.toString();
    }

}
