package uk.gov.companieshouse.api.testdata.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.constants.ErrorMessageConstants;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.AppointmentData;
import uk.gov.companieshouse.api.testdata.model.entity.DateObject;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AppointmentsServiceImpl implements DataService<Appointment> {

    private static final int SALT_LENGTH = 8;
    private static final int ID_LENGTH = 10;
    private static final int INTERNAL_ID_LENGTH = 9;
    private static final String INTERNAL_ID_PREFIX = "8";
    private static final String APPOINTMENT_DATA_NOT_FOUND = "appointment data not found";

    @Autowired
    private RandomService randomService;
    @Autowired
    private AppointmentsRepository repository;

    @Override
    public Appointment create(String companyNumber) throws DataException {
        Appointment appointment = new Appointment();

        String appointmentId = randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH);
        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalDate dateNow = LocalDate.now();

        appointment.setId(appointmentId);
        appointment.setCreated(new DateObject(dateTimeNow));

        String internalId = this.generateInternalId();
        String officerId = randomService.addSaltAndEncode(internalId, SALT_LENGTH);
        appointment.setInternalId(internalId);
        appointment.setAppointmentId(appointmentId);

        AppointmentData appointmentData = new AppointmentData();

        appointmentData.setNationality("British");
        appointmentData.setOccupation("Director");
        appointmentData.setServiceAddressIsSameAsRegisteredOfficeAddress(true);
        appointmentData.setCountryOfResidence("Wales");
        appointmentData.setUpdatedAt(dateTimeNow);
        appointmentData.setForename("Test");
        appointmentData.setAppointedOn(dateNow);
        appointmentData.setOfficerRole("director");
        appointmentData.setEtag(randomService.getEtag());

        Address serviceAddress = new Address();
        serviceAddress.setCountry("United Kingdom");
        serviceAddress.setPostalCode("CF14 3UZ");
        serviceAddress.setAddressLine1("Companies House");
        serviceAddress.setAddressLine2("Crownway");
        serviceAddress.setLocality("Cardiff");

        appointmentData.setServiceAddress(serviceAddress);
        appointmentData.setCompanyNumber(companyNumber);

        Links links = new Links();
        links.setSelf("/company/" + companyNumber + "/appointments/" + officerId);
        Links officer = new Links();
        officer.setSelf("/officers/" + officerId);
        officer.setAppointments("/officers/" + officerId + "/appointments");
        links.setOfficer(officer);
        appointmentData.setLinks(links);

        appointmentData.setSurname("DIRECTOR");
        appointmentData.setDateOfBirth(dateNow);

        appointment.setData(appointmentData);

        appointment.setCompanyName("Company " + companyNumber);
        appointment.setCompanyStatus("active");
        appointment.setOfficerId(officerId);
        appointment.setCompanyNumber(companyNumber);
        appointment.setUpdated(new DateObject(dateTimeNow));

        try {
            return repository.save(appointment);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }
    }

    @Override
    public void delete(String companyNumber) throws NoDataFoundException, DataException {
        Appointment existingAppointment = repository.findByCompanyNumber(companyNumber);

        if (existingAppointment == null) {
            throw new NoDataFoundException(APPOINTMENT_DATA_NOT_FOUND);
        }

        try {
            repository.delete(existingAppointment);
        } catch (MongoException e) {
            throw new DataException(ErrorMessageConstants.FAILED_TO_DELETE);
        }
    }

    private String generateInternalId() {
        return INTERNAL_ID_PREFIX + randomService.getNumber(INTERNAL_ID_LENGTH);
    }
}
