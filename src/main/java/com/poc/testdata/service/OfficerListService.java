package com.poc.testdata.service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import com.poc.testdata.constants.ErrorMessageConstants;
import com.poc.testdata.exception.DataException;
import com.poc.testdata.exception.NoDataFoundException;
import com.poc.testdata.model.Address;
import com.poc.testdata.model.DateOfBirth;
import com.poc.testdata.model.Links;
import com.poc.testdata.model.Officer.Officer;
import com.poc.testdata.model.Officer.OfficerItem;
import com.poc.testdata.repository.OfficerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OfficerListService {

    @Autowired
    TestDataHelperService testDataHelperService;
    @Autowired
    OfficerRepository officerRepository;

    Officer officer;

    private final String OFFICER_DATA_NOT_FOUND = "officer data not found";

    public void create(String companyNumber) throws DataException {

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

    }

    public void delete(String companyId) throws NoDataFoundException, DataException {

        Officer officer = officerRepository.findByCompanyNumber(companyId);

        if(officer == null) throw new NoDataFoundException(OFFICER_DATA_NOT_FOUND);

        try {
            officerRepository.delete(officer);
        } catch (MongoException e) {
            throw new DataException(ErrorMessageConstants.FAILED_TO_DELETE);
        }
    }

    private List<OfficerItem> createItems(){
        List<OfficerItem> officerItemList = new ArrayList<>();

        OfficerItem officer = createOfficerItem();
        officerItemList.add(officer);
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
