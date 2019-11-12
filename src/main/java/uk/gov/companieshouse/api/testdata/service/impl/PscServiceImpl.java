package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.constants.ErrorMessageConstants;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.DateOfBirth;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.PersonsWithSignificantControl;
import uk.gov.companieshouse.api.testdata.model.entity.PersonsWithSignificantControlItem;
import uk.gov.companieshouse.api.testdata.repository.PersonsWithSignificantControlRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class PscServiceImpl implements DataService<PersonsWithSignificantControl> {
    private static final int SALT_LENGTH = 8;
    private static final int ID_LENGTH = 10;
    private static final String PSC_DATA_NOT_FOUND = "psc data not found";

    @Autowired
    private RandomService randomService;
    @Autowired
    private PersonsWithSignificantControlRepository repository;

    @Override
    public PersonsWithSignificantControl create(String companyNumber) throws DataException {

        PersonsWithSignificantControl psc = new PersonsWithSignificantControl();

        psc.setId(randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH));
        psc.setCompanyNumber(companyNumber);
        psc.setActiveCount(1);
        psc.setCeasedCount(0);
        psc.setItems(createItems(psc));

        try {
            return repository.save(psc);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }
    }

    @Override
    public void delete(String companyId) throws NoDataFoundException, DataException {

        PersonsWithSignificantControl pscToDelete = repository.findByCompanyNumber(companyId);
        if (pscToDelete == null) {
            throw new NoDataFoundException(PSC_DATA_NOT_FOUND);
        }

        try {
            repository.delete(pscToDelete);
        } catch (MongoException e) {
            throw new DataException(ErrorMessageConstants.FAILED_TO_DELETE);
        }
    }

    private List<PersonsWithSignificantControlItem> createItems(PersonsWithSignificantControl psc) {

        List<PersonsWithSignificantControlItem> pscItemList = new ArrayList<>();
        PersonsWithSignificantControlItem pscItem = new PersonsWithSignificantControlItem();

        pscItem.setAddress(createAddress());
        pscItem.setDateOfBirth(createDOB());
        pscItem.setLinks(createLinks(psc));
        pscItem.setName("full name");
        String[] control = new String[1];
        control[0] = "significant-influence-or-control";
        pscItem.setNaturesOfControl(control);
        pscItem.setNotifiedOn(new Date());

        pscItemList.add(pscItem);

        return pscItemList;
    }

    private Links createLinks(PersonsWithSignificantControl psc) {

        Links links = new Links();
        links.setSelf("/company/" + psc.getCompanyNumber() + "/persons-with-significant-control/legal-person/" + psc.getId());

        return links;
    }

    private DateOfBirth createDOB() {

        DateOfBirth dateOfBirth = new DateOfBirth();
        dateOfBirth.setMonth(1);
        dateOfBirth.setYear(1992);
        return dateOfBirth;
    }

    private Address createAddress() {

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
