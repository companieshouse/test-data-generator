package com.poc.testdata.service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import com.poc.testdata.constants.ErrorMessageConstants;
import com.poc.testdata.exception.DataException;
import com.poc.testdata.exception.NoDataFoundException;
import com.poc.testdata.model.Address;
import com.poc.testdata.model.DateOfBirth;
import com.poc.testdata.model.Links;
import com.poc.testdata.model.PersonsWithSignificantControl.PersonsWithSignificantControl;
import com.poc.testdata.model.PersonsWithSignificantControl.PersonsWithSignificantControlItem;
import com.poc.testdata.repository.PersonsWithSignificantControlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PSCService {

    @Autowired
    TestDataHelperService testDataHelperService;
    @Autowired
    PersonsWithSignificantControlRepository repository;

    PersonsWithSignificantControl psc;

    private final String PSC_DATA_NOT_FOUND = "psc data not found";

    public void create(String companyNumber) throws DataException {

        psc = new PersonsWithSignificantControl();

        psc.setId(testDataHelperService.getNewId());
        psc.setCompanyNumber(companyNumber);
        psc.setActiveCount(1);
        psc.setCeasedCount(0);
        psc.setItems(createItems());

        try{
            repository.save(psc);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }
    }

    public void delete(String companyId) throws NoDataFoundException, DataException {

        PersonsWithSignificantControl psc = repository.findByCompanyNumber(companyId);
        if(psc == null) throw new NoDataFoundException(PSC_DATA_NOT_FOUND);

        try {
            repository.delete(psc);
        } catch (MongoException e) {
            throw new DataException(ErrorMessageConstants.FAILED_TO_DELETE);
        }
    }

    private List<PersonsWithSignificantControlItem> createItems(){

        List<PersonsWithSignificantControlItem> pscItemList = new ArrayList<>();
        PersonsWithSignificantControlItem pscItem = new PersonsWithSignificantControlItem();

        pscItem.setAddress(createAddress());
        pscItem.setDateOfBirth(createDOB());
        pscItem.setLinks(createLinks());
        pscItem.setName("full name");
        String [] control = new String[1];
        control[0] = "significant-influence-or-control";
        pscItem.setNaturesOfControl(control);
        pscItem.setNotifiedOn(new Date());

        pscItemList.add(pscItem);

        return pscItemList;
    }

    private Links createLinks(){

        Links links = new Links();
        links.setSelf("/company/"+ psc.getCompanyNumber() + "/persons-with-significant-control/legal-person/" + psc.getId());

        return links;
    }

    private DateOfBirth createDOB(){

        DateOfBirth dateOfBirth = new DateOfBirth();
        dateOfBirth.setMonth(1);
        dateOfBirth.setYear(1992);
        return dateOfBirth;
    }

    private Address createAddress(){

        Address address = new Address();
        address.setPremises("premises");
        address.setAddressLine1("Line1");
        address.setAddressLine2("Line2");
        address.setLocality("Locality");
        address.setCountry("Country");
        address.setPostalCode("postcode");

        return address;
    }
}
