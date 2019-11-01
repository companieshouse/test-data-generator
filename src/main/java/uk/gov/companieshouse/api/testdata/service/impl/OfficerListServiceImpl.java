package uk.gov.companieshouse.api.testdata.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import uk.gov.companieshouse.api.testdata.constants.ErrorMessageConstants;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.Address;
import uk.gov.companieshouse.api.testdata.model.DateOfBirth;
import uk.gov.companieshouse.api.testdata.model.Links;
import uk.gov.companieshouse.api.testdata.model.officer.Officer;
import uk.gov.companieshouse.api.testdata.model.officer.OfficerItem;
import uk.gov.companieshouse.api.testdata.repository.officer.OfficerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.service.IOfficerListService;
import uk.gov.companieshouse.api.testdata.service.ITestDataHelperService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OfficerListServiceImpl implements IOfficerListService {

    private ITestDataHelperService testDataHelperService;
    private OfficerRepository officerRepository;

    private Officer officer;

    private static final String OFFICER_DATA_NOT_FOUND = "officer data not found";

    @Autowired
    public OfficerListServiceImpl(ITestDataHelperService testDataHelperService, OfficerRepository officerRepository) {
        this.testDataHelperService = testDataHelperService;
        this.officerRepository = officerRepository;
    }

    @Override
    public Officer create(String companyNumber) throws DataException {

        officer = new Officer();

        officer.setId(testDataHelperService.getNewId());
        officer.setCompanyNumber(companyNumber);
        officer.setActiveCount(1);
        officer.setInactiveCount(0);
        officer.setOfficerItems(createItems());
        officer.setResignedCount(1);

        try{
            officerRepository.save(officer);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }

        return officer;

    }

    @Override
    public void delete(String companyId) throws NoDataFoundException, DataException {

        Officer officerToDelete = officerRepository.findByCompanyNumber(companyId);

        if(officerToDelete == null) throw new NoDataFoundException(OFFICER_DATA_NOT_FOUND);

        try {
            officerRepository.delete(officerToDelete);
        } catch (MongoException e) {
            throw new DataException(ErrorMessageConstants.FAILED_TO_DELETE);
        }
    }

    private List<OfficerItem> createItems(){
        List<OfficerItem> officerItemList = new ArrayList<>();

        OfficerItem officerItem = createOfficerItem();
        officerItemList.add(officerItem);
        OfficerItem resignedOfficer = createOfficerItem();
        resignedOfficer.setResignedOn(new Date());
        officerItemList.add(resignedOfficer);

        return officerItemList;
    }

    private OfficerItem createOfficerItem(){

        OfficerItem officerItem = new OfficerItem();
        officerItem.setAddress(createAddress());
        officerItem.setAppointedOn(new Date());
        officerItem.setDateOfBirth(createDateOfBirth());
        officerItem.setLinks(createLinks());
        officerItem.setName("full name");
        officerItem.setAppointedOn(new Date());
        officerItem.setOfficerRole("director");

        return officerItem;
    }

    private Links createLinks(){

        Links links = new Links();
        links.setSelf("/officers/"+ officer.getCompanyNumber());
        links.setOfficers("/company/"+ officer.getCompanyNumber() + "/officers");

        return links;
    }

    private DateOfBirth createDateOfBirth(){

        DateOfBirth dateOfBirth = new DateOfBirth();

        dateOfBirth.setDay(1);
        dateOfBirth.setMonth(1);
        dateOfBirth.setYear(1950);

        return dateOfBirth;
    }


    private Address createAddress(){

        Address address = new Address();
        address.setAddressLine1("10 Test Street");
        address.setAddressLine2("line 2");
        address.setLocality("locality");
        address.setCountry("country");
        address.setPostalCode("postcode");

        return address;
    }

}
