package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class AppointmentsServiceImpl implements DataService<Appointment> {

    private static final int SALT_LENGTH = 8;
    private static final int ID_LENGTH = 10;
    private static final int INTERNAL_ID_LENGTH = 9;
    private static final String INTERNAL_ID_PREFIX = "8";
    private static final String APPOINTMENT_DATA_NOT_FOUND = "appointment data not found";

    @Autowired
    private AddressService addressService;
    @Autowired
    private RandomService randomService;
    @Autowired
    private AppointmentsRepository repository;

    @Override
    public Appointment create(CompanySpec spec) throws DataException {
        final String companyNumber = spec.getCompanyNumber();

        final String countryOfResidence = addressService.getCountryOfResidence(spec.getJurisdiction());

        Appointment appointment = new Appointment();

        String appointmentId = randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH);

        LocalDate officerDob = LocalDate.of(1990, 3, 6);

        Instant dateTimeNow = Instant.now();
        Instant dateNow = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant();
        Instant dob = officerDob.atStartOfDay(ZoneId.of("UTC")).toInstant();

        appointment.setId(appointmentId);
        appointment.setCreated(dateTimeNow);

        String internalId = INTERNAL_ID_PREFIX + this.randomService.getNumber(INTERNAL_ID_LENGTH);
        String officerId = randomService.addSaltAndEncode(internalId, SALT_LENGTH);
        appointment.setInternalId(internalId);
        appointment.setAppointmentId(appointmentId);

        appointment.setNationality("British");
        appointment.setOccupation("Director");
        appointment.setServiceAddressIsSameAsRegisteredOfficeAddress(true);
        appointment.setCountryOfResidence(countryOfResidence);
        appointment.setUpdatedAt(dateTimeNow);
        appointment.setForename("Test");
        appointment.setAppointedOn(dateNow);
        appointment.setOfficerRole("director");
        appointment.setEtag(randomService.getEtag());

        appointment.setServiceAddress(addressService.getAddress(spec.getJurisdiction()));
        appointment.setDataCompanyNumber(companyNumber);

        Links links = new Links();
        links.setSelf("/company/" + companyNumber + "/appointments/" + officerId);
        links.setOfficerSelf("/officers/" + officerId);
        links.setOfficerAppointments("/officers/" + officerId + "/appointments");
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
        } catch (MongoException e) {
            throw new DataException("Failed to save appointment", e);
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
            throw new DataException("Failed to delete appointment", e);
        }
    }

}
