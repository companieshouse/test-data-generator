package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointment;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointmentItem;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;
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
    private static final String OFFICER_APPOINTMENT_DATA_NOT_FOUND = "officer appointment data not found";
    private static final String COMPANY_LINK = "/company/";
    private static final String OFFICERS_LINK = "/officers/";

    @Autowired
    private AddressService addressService;
    @Autowired
    private RandomService randomService;
    @Autowired
    private AppointmentsRepository appointmentsRepository;
    @Autowired
    private OfficerRepository officerRepository;

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
        links.setSelf(COMPANY_LINK + companyNumber + "/appointments/" + officerId);
        links.setOfficerSelf(OFFICERS_LINK + officerId);
        links.setOfficerAppointments(OFFICERS_LINK + officerId + "/appointments");
        appointment.setLinks(links);

        appointment.setSurname("DIRECTOR");
        appointment.setDateOfBirth(dob);

        appointment.setCompanyName("Company " + companyNumber);
        appointment.setCompanyStatus("active");
        appointment.setOfficerId(officerId);
        appointment.setCompanyNumber(companyNumber);
        appointment.setUpdated(dateTimeNow);

        this.createOfficerAppointment(spec, officerId, appointmentId);

        try {
            return appointmentsRepository.save(appointment);
        } catch (MongoException e) {
            throw new DataException("Failed to save appointment", e);
        }
    }

    @Override
    public void delete(String companyNumber) throws NoDataFoundException, DataException {
        Appointment existingAppointment = appointmentsRepository.findByCompanyNumber(companyNumber);

        if (existingAppointment == null) {
            throw new NoDataFoundException(APPOINTMENT_DATA_NOT_FOUND);
        }
        String officerId = existingAppointment.getOfficerId();

        this.deleteOfficerAppointment(officerId);

        try {
            appointmentsRepository.delete(existingAppointment);
        } catch (MongoException e) {
            throw new DataException("Failed to delete appointment", e);
        }
    }



    private void createOfficerAppointment(CompanySpec spec, String officerId, String appointmentId)
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
        links.setSelf(OFFICERS_LINK + officerId + "/appointments");
        officerAppointment.setLinks(links);

        officerAppointment.setEtag(this.randomService.getEtag());
        officerAppointment.setDateOfBirthYear(1990);
        officerAppointment.setDateOfBirthMonth(3);
        officerAppointment.setOfficerAppointmentItems(
                createOfficerAppointmentItems(spec, appointmentId, dayNow, dayTimeNow));

        try {
            officerRepository.save(officerAppointment);
        } catch (MongoException e) {
            throw new DataException("Failed to save officer appointment", e);
        }

    }

    private void deleteOfficerAppointment(String officerId) throws NoDataFoundException, DataException {
        OfficerAppointment existingAppointment = officerRepository.findById(officerId)
                .orElseThrow(() -> new NoDataFoundException(OFFICER_APPOINTMENT_DATA_NOT_FOUND));

        try {
            officerRepository.delete(existingAppointment);
        } catch (MongoException e) {
            throw new DataException("Failed to delete officer appointment", e);
        }
    }

    private List<OfficerAppointmentItem> createOfficerAppointmentItems(CompanySpec companySpec, String appointmentId,
                                                                       Instant dayNow, Instant dayTimeNow) {
        List<OfficerAppointmentItem> officerAppointmentItemList = new ArrayList<>();

        String companyNumber = companySpec.getCompanyNumber();
        Jurisdiction jurisdiction = companySpec.getJurisdiction();

        OfficerAppointmentItem officerAppointmentItem = new OfficerAppointmentItem();
        officerAppointmentItem.setOccupation("Director");
        officerAppointmentItem.setAddress(addressService.getAddress(jurisdiction));
        officerAppointmentItem.setForename("Test");
        officerAppointmentItem.setSurname("Director");
        officerAppointmentItem.setOfficerRole("director");
        officerAppointmentItem.setLinks(createOfficerAppointmentItemLinks(companyNumber, appointmentId));
        officerAppointmentItem.setCountryOfResidence(addressService.getCountryOfResidence(jurisdiction));
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

    private Links createOfficerAppointmentItemLinks(String companyNumber, String appointmentId) {

        Links links = new Links();
        links.setSelf(COMPANY_LINK + companyNumber + "/appointments/" + appointmentId);
        links.setCompany(COMPANY_LINK + companyNumber);

        return links;
    }

}
