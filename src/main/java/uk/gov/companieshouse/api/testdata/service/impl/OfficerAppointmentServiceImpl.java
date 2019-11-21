package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.constants.ErrorMessageConstants;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointment;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointmentItem;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;
import uk.gov.companieshouse.api.testdata.service.OfficerAppointmentService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class OfficerAppointmentServiceImpl implements OfficerAppointmentService {

    @Autowired
    private RandomService randomService;
    @Autowired
    private OfficerRepository officerRepository;

    @Override
    public OfficerAppointment create(String companyNumber, String officerId, String appointmentId)
            throws DataException {

        OfficerAppointment officerAppointment = new OfficerAppointment();

        Instant dayTimeNow = Instant.now();
        Instant dayNow = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant();

        officerAppointment.setId(officerId);
        officerAppointment.setCreatedAt(dayTimeNow);
        officerAppointment.setUpdatedAt(dayTimeNow);

        officerAppointment.setTotalResults(1);
        officerAppointment.setActiveCount(1);
        officerAppointment.setInactiveCount(0);
        officerAppointment.setResignedCount(1);
        officerAppointment.setCorporateOfficer(false);
        officerAppointment.setName("Test DIRECTOR");

        Links links = new Links();
        links.setSelf("/officers/" + officerId + "/appointments");
        officerAppointment.setLinks(links);

        officerAppointment.setEtag(this.randomService.getEtag());
        officerAppointment.setDateOfBirthYear(1990);
        officerAppointment.setDateOfBirthMonth(3);
        officerAppointment.setOfficerAppointmentItems(createItems(companyNumber, appointmentId, dayNow, dayTimeNow));

        try {
            return officerRepository.save(officerAppointment);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }

    }

    @Override
    public void delete(String officerId) throws NoDataFoundException, DataException {
        //TODO delete Officer Appointment data
    }

    private List<OfficerAppointmentItem> createItems(String companyNumber, String appointmentId,
                                                     Instant dayNow, Instant dayTimeNow) {
        List<OfficerAppointmentItem> officerAppointmentItemList = new ArrayList<>();

        OfficerAppointmentItem officerAppointmentItem = new OfficerAppointmentItem();
        officerAppointmentItem.setOccupation("Director");
        officerAppointmentItem.setAddress(createAddress());
        officerAppointmentItem.setForename("Test");
        officerAppointmentItem.setSurname("Director");
        officerAppointmentItem.setOfficerRole("director");
        officerAppointmentItem.setLinks(createItemLinks(companyNumber, appointmentId));
        officerAppointmentItem.setCountryOfResidence("Wales");
        officerAppointmentItem.setAppointedOn(dayNow);
        officerAppointmentItem.setNationality("British");
        officerAppointmentItem.setUpdatedAt(dayTimeNow);
        officerAppointmentItem.setName("Test DIRECTOR");
        officerAppointmentItem.setCompanyName("Company " + companyNumber);
        officerAppointmentItem.setCompanyNumber(companyNumber);
        officerAppointmentItem.setCompanyStatus("active");

        officerAppointmentItemList.add(officerAppointmentItem);

        return officerAppointmentItemList;
    }

    private Links createItemLinks(String companyNumber, String appointmentId) {

        Links links = new Links();
        links.setSelf("/company/" + companyNumber + "/appointments/" + appointmentId);
        links.setCompany("/company/" + companyNumber);

        return links;
    }

    private Address createAddress() {

        Address address = new Address();
        address.setAddressLine1("Companies House");
        address.setAddressLine2("Crown Way");
        address.setLocality("Cardiff");
        address.setCountry("United Kingdom");
        address.setPostalCode("CF14 3UZ");

        return address;
    }

}
