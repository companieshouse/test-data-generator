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
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

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

        LocalDate officerDob = LocalDate.of(1990, 3, 6);

        Instant dateTimeNow = Instant.now();
        Instant dateNow = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant dob = officerDob.atStartOfDay(ZoneId.systemDefault()).toInstant();

        appointment.setId(appointmentId);
        appointment.setCreated(dateTimeNow);

        String internalId = this.generateInternalId();
        String officerId = randomService.addSaltAndEncode(internalId, SALT_LENGTH);
        appointment.setInternalId(internalId);
        appointment.setAppointmentId(appointmentId);

        appointment.setNationality("British");
        appointment.setOccupation("Director");
        appointment.setServiceAddressIsSameAsRegisteredOfficeAddress(true);
        appointment.setCountryOfResidence("Wales");
        appointment.setUpdatedAt(dateTimeNow);
        appointment.setForename("Test");
        appointment.setAppointedOn(dateNow);
        appointment.setOfficerRole("director");
        appointment.setEtag(randomService.getEtag());

        Address serviceAddress = new Address();
        serviceAddress.setCountry("United Kingdom");
        serviceAddress.setPostalCode("CF14 3UZ");
        serviceAddress.setAddressLine1("Companies House");
        serviceAddress.setAddressLine2("Crownway");
        serviceAddress.setLocality("Cardiff");

        appointment.setServiceAddress(serviceAddress);
        appointment.setDataCompanyNumber(companyNumber);

        Links links = new Links();
        links.setSelf("/company/" + companyNumber + "/appointments/" + officerId);
        Links officer = new Links();
        officer.setSelf("/officers/" + officerId);
        officer.setAppointments("/officers/" + officerId + "/appointments");
        links.setOfficer(officer);
        appointment.setLinks(links);

        appointment.setSurname("DIRECTOR");
        appointment.setDateOfBirth(dob);

        appointment.setCompanyName("Company " + companyNumber);
        appointment.setCompanyStatus("active");
        appointment.setOfficerId(officerId);
        appointment.setCompanyNumber(companyNumber);
        appointment.setUpdated(dateTimeNow);

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
