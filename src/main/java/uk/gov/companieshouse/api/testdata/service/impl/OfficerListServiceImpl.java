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
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointment;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointmentItem;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class OfficerListServiceImpl implements DataService<OfficerAppointment> {
    private static final int SALT_LENGTH = 8;
    private static final int ID_LENGTH = 10;
    private static final String OFFICER_DATA_NOT_FOUND = "officer data not found";

    @Autowired
    private RandomService randomService;
    @Autowired
    private OfficerRepository officerRepository;

    @Override
    public OfficerAppointment create(String companyNumber) throws DataException {

        OfficerAppointment officerAppointment = new OfficerAppointment();

        officerAppointment.setId(randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH));
        officerAppointment.setCompanyNumber(companyNumber);
        officerAppointment.setActiveCount(1);
        officerAppointment.setInactiveCount(0);
        officerAppointment.setOfficerAppointmentItems(createItems(officerAppointment));
        officerAppointment.setResignedCount(1);

        try {
            return officerRepository.save(officerAppointment);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }

    }

    @Override
    public void delete(String companyId) throws NoDataFoundException, DataException {

        OfficerAppointment officerAppointmentToDelete = officerRepository.findByCompanyNumber(companyId);

        if (officerAppointmentToDelete == null) {
            throw new NoDataFoundException(OFFICER_DATA_NOT_FOUND);
        }

        try {
            officerRepository.delete(officerAppointmentToDelete);
        } catch (MongoException e) {
            throw new DataException(ErrorMessageConstants.FAILED_TO_DELETE);
        }
    }

    private List<OfficerAppointmentItem> createItems(OfficerAppointment officerAppointment) {
        List<OfficerAppointmentItem> officerAppointmentItemList = new ArrayList<>();

        OfficerAppointmentItem officerAppointmentItem = createOfficerItem(officerAppointment);
        officerAppointmentItemList.add(officerAppointmentItem);
        OfficerAppointmentItem resignedOfficer = createOfficerItem(officerAppointment);
        resignedOfficer.setResignedOn(new Date());
        officerAppointmentItemList.add(resignedOfficer);

        return officerAppointmentItemList;
    }

    private OfficerAppointmentItem createOfficerItem(OfficerAppointment officerAppointment) {

        OfficerAppointmentItem officerAppointmentItem = new OfficerAppointmentItem();
        officerAppointmentItem.setAddress(createAddress());
        officerAppointmentItem.setAppointedOn(new Date());
        officerAppointmentItem.setDateOfBirth(createDateOfBirth());
        officerAppointmentItem.setLinks(createLinks(officerAppointment));
        officerAppointmentItem.setName("full name");
        officerAppointmentItem.setAppointedOn(new Date());
        officerAppointmentItem.setOfficerRole("director");

        return officerAppointmentItem;
    }

    private Links createLinks(OfficerAppointment officerAppointment) {

        Links links = new Links();
        links.setSelf("/officers/" + officerAppointment.getCompanyNumber());
        links.setOfficers("/company/" + officerAppointment.getCompanyNumber() + "/officers");

        return links;
    }

    private DateOfBirth createDateOfBirth() {

        DateOfBirth dateOfBirth = new DateOfBirth();

        dateOfBirth.setDay(1);
        dateOfBirth.setMonth(1);
        dateOfBirth.setYear(1950);

        return dateOfBirth;
    }


    private Address createAddress() {

        Address address = new Address();
        address.setAddressLine1("10 Test Street");
        address.setAddressLine2("line 2");
        address.setLocality("locality");
        address.setCountry("country");
        address.setPostalCode("postcode");

        return address;
    }

}
