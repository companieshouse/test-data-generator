package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.AppointmentsData;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointment;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointmentItem;
import uk.gov.companieshouse.api.testdata.model.rest.AppointmentCreationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.model.rest.OfficerRoles;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsDataRepository;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.AppointmentService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class AppointmentsServiceImpl implements AppointmentService {

    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(AppointmentsServiceImpl.class));

    private static final int SALT_LENGTH = 8;
    private static final int ID_LENGTH = 10;
    private static final int INTERNAL_ID_LENGTH = 9;
    private static final String NATIONALITY = "British";
    private static final String FORENAME = "Test ";
    private static final String COMPANY_STATUS = "active";
    private static final String INTERNAL_ID_PREFIX = "8";
    private static final String COMPANY_LINK = "/company/";
    private static final String OFFICERS_LINK = "/officers/";
    private static final String APPOINTMENT_LINK_STEM = "/appointments";
    private static final String APPOINTMENT_MSG = " appointments for company number: ";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1951, 3, 4);
    private static final Instant DOB_INSTANT
            = DATE_OF_BIRTH.atStartOfDay(ZoneId.of("UTC")).toInstant();

    @Autowired
    private AddressService addressService;
    @Autowired
    private RandomService randomService;
    @Autowired
    private AppointmentsRepository appointmentsRepository;
    @Autowired
    private AppointmentsDataRepository appointmentsDataRepository;
    @Autowired
    private OfficerRepository officerRepository;

    public void createAppointment(CompanySpec spec) {
        if (Boolean.TRUE.equals(spec.getNoDefaultOfficer())) {
            LOG.info("No default officer request, skipping appointment creation for: "
                    + spec.getCompanyNumber());
            return;
        }

        LOG.info("Starting creation of appointments with matching IDs for company number: "
                + spec.getCompanyNumber());

        final var companyNumber = spec.getCompanyNumber();
        final String countryOfResidence = addressService.getCountryOfResidence(
                spec.getJurisdiction());
        int numberOfAppointments = spec.getNumberOfAppointments();
        if (numberOfAppointments <= 0) {
            LOG.info("Number of appointments is less than or equal to 0. Defaulting to 1.");
            numberOfAppointments = 1;
        }

        List<OfficerRoles> officerRoleList = new ArrayList<>();
        if (spec.getOfficerRoles() != null) {
            officerRoleList.addAll(spec.getOfficerRoles());
            LOG.debug("Officer roles provided: " + spec.getOfficerRoles());
        }
        while (officerRoleList.size() < numberOfAppointments) {
            officerRoleList.add(OfficerRoles.DIRECTOR);
        }

        List<String> appointmentIds = new ArrayList<>();
        for (var i = 0; i < numberOfAppointments; i++) {
            appointmentIds.add(randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH));
        }

        List<Appointment> createdAppointments = new ArrayList<>();
        List<AppointmentsData> createdAppointmentsData = new ArrayList<>();

        for (var i = 0; i < numberOfAppointments; i++) {
            OfficerRoles currentRoleEnum = officerRoleList.get(i);
            if (currentRoleEnum == null) {
                LOG.error("Invalid officer role: null at index " + i);
                throw new IllegalArgumentException("Invalid officer role: null");
            }
            String currentRole = currentRoleEnum.getValue();
            validateOfficerRole(currentRole);

            LOG.debug("Processing appointment {} with role: " + (i + 1) + currentRole);

            String internalId = INTERNAL_ID_PREFIX + randomService.getNumber(INTERNAL_ID_LENGTH);
            String officerId = randomService.addSaltAndEncode(internalId, SALT_LENGTH);

            String appointmentId = appointmentIds.get(i);

            LOG.debug("Generated IDs - Appointment ID: "
                    + appointmentId + ", Internal ID: "
                    + internalId + ", Officer ID: " + officerId);

            Instant dateTimeNow = Instant.now();
            var today = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant();

            String roleName = setRoleName(currentRole);
            var request = AppointmentCreationRequest.builder()
                    .spec(spec)
                    .companyNumber(companyNumber)
                    .countryOfResidence(countryOfResidence)
                    .internalId(internalId)
                    .officerId(officerId)
                    .dateTimeNow(dateTimeNow)
                    .appointedOn(today)
                    .appointmentId(appointmentId)
                    .build();

            var appointment = createBaseAppointment(request);
            appointment.setForename(FORENAME + (i + 1));
            appointment.setSurname(roleName);
            appointment.setOccupation(roleName);
            appointment.setOfficerRole(currentRole);

            var links = createAppointmentLinks(companyNumber, officerId, appointmentId);
            appointment.setLinks(links);

            LOG.debug("Creating officer appointment for officer ID: " + officerId);
            this.createOfficerAppointment(spec, officerId, appointmentId, currentRole);

            Appointment savedAppointment = appointmentsRepository.save(appointment);
            LOG.info("Appointment saved with ID: " + savedAppointment.getId());
            createdAppointments.add(savedAppointment);

            // Create AppointmentsData with same appointmentId
            var appointmentsData = createBaseAppointmentsData(
                    spec, internalId, officerId, dateTimeNow, appointmentId);
            appointmentsData.setForename(FORENAME + (i + 1));
            appointmentsData.setSurname(roleName);
            appointmentsData.setOccupation(roleName);
            appointmentsData.setOfficerRole(currentRole);

            var dataLinks = new AppointmentsData.Links();
            var dataOfficerLinks = new AppointmentsData.OfficerLinks();
            dataOfficerLinks.setAppointments(OFFICERS_LINK + officerId + APPOINTMENT_LINK_STEM);
            dataOfficerLinks.setSelf(OFFICERS_LINK + officerId);
            dataLinks.setOfficer(dataOfficerLinks);
            dataLinks.setSelf(COMPANY_LINK
                    + spec.getCompanyNumber() + "/appointments/" + appointmentId);
            appointmentsData.setLinks(dataLinks);

            var savedData = appointmentsDataRepository.save(appointmentsData);
            createdAppointmentsData.add(savedData);
            LOG.info("AppointmentsData saved with ID: " + savedData.getId());
        }

        LOG.info("Successfully created " + createdAppointments.size() + " appointments and "
                + createdAppointmentsData.size()
                + " appointments data with matching IDs for company number: " + companyNumber);
    }

    @Override
    public boolean deleteAllAppointments(String companyNumber) {
        LOG.info("Starting deletion of all appointments and appointments data for company number: "
                + companyNumber);

        var appointmentsDeleted = false;
        var appointmentsDataDeleted = false;

        List<Appointment> foundAppointments =
                appointmentsRepository.findAllByCompanyNumber(companyNumber);
        if (!foundAppointments.isEmpty()) {
            LOG.info("Found " + foundAppointments.size() + APPOINTMENT_MSG + companyNumber);

            for (Appointment ap : foundAppointments) {
                String officerId = ap.getOfficerId();
                officerRepository.findById(officerId).ifPresent(officer -> {
                    LOG.debug("Deleting officer with ID: " + officerId);
                    officerRepository.delete(officer);
                });
            }

            appointmentsRepository.deleteAll(foundAppointments);
            LOG.info("Successfully deleted all" + APPOINTMENT_MSG + companyNumber);
            appointmentsDeleted = true;
        } else {
            LOG.info("No appointments found for company number: " + companyNumber);
        }

        List<AppointmentsData> foundData =
                appointmentsDataRepository.findAllByCompanyNumber(companyNumber);
        if (!foundData.isEmpty()) {
            appointmentsDataRepository.deleteAll(foundData);
            LOG.info("Successfully deleted all appointments data for company number: "
                    + companyNumber);
            appointmentsDataDeleted = true;
        } else {
            LOG.info("No appointments data found for company number: " + companyNumber);
        }

        return appointmentsDeleted || appointmentsDataDeleted;
    }

    private Appointment createBaseAppointment(AppointmentCreationRequest request) {
        var appointment = new Appointment();

        appointment.setId(request.getAppointmentId());
        appointment.setCreated(request.getDateTimeNow());
        appointment.setInternalId(request.getInternalId());
        appointment.setAppointmentId(request.getAppointmentId());
        appointment.setNationality(NATIONALITY);
        appointment.setServiceAddressIsSameAsRegisteredOfficeAddress(true);
        appointment.setCountryOfResidence(request.getCountryOfResidence());
        appointment.setUpdatedAt(request.getDateTimeNow());
        appointment.setAppointedOn(request.getAppointedOn());
        appointment.setEtag(randomService.getEtag());
        appointment.setServiceAddress(
                addressService.getAddress(request.getSpec().getJurisdiction()));
        appointment.setDataCompanyNumber(request.getCompanyNumber());
        appointment.setDateOfBirth(DOB_INSTANT);
        appointment.setCompanyName("Company " + request.getCompanyNumber());
        appointment.setCompanyStatus(COMPANY_STATUS);
        appointment.setOfficerId(request.getOfficerId());
        appointment.setCompanyNumber(request.getCompanyNumber());
        appointment.setUpdated(request.getDateTimeNow());

        Boolean secureOfficer = request.getSpec().getSecureOfficer();
        appointment.setSecureOfficer(secureOfficer != null && secureOfficer);

        return appointment;
    }

    private AppointmentsData createBaseAppointmentsData(
            CompanySpec spec, String internalId, String officerId,
            Instant now, String appointmentId) {
        var appointmentsData = new AppointmentsData();
        String countryOfResidence = addressService.getCountryOfResidence(spec.getJurisdiction());

        appointmentsData.setId(appointmentId);
        appointmentsData.setCreated(now);
        appointmentsData.setInternalId(internalId);
        appointmentsData.setAppointmentId(appointmentId);
        appointmentsData.setNationality(NATIONALITY);
        appointmentsData.setServiceAddressIsSameAsRegisteredOfficeAddress(true);
        appointmentsData.setCountryOfResidence(countryOfResidence);
        appointmentsData.setUpdatedAt(now);
        appointmentsData.setAppointedOn(now);
        appointmentsData.setEtag(randomService.getEtag());
        appointmentsData.setServiceAddress(addressService.getAddress(spec.getJurisdiction()));
        appointmentsData.setDataCompanyNumber(spec.getCompanyNumber());
        appointmentsData.setDateOfBirth(DOB_INSTANT);
        appointmentsData.setCompanyName("Company" + " " + spec.getCompanyNumber());
        appointmentsData.setCompanyStatus(COMPANY_STATUS);
        appointmentsData.setOfficerId(officerId);
        appointmentsData.setCompanyNumber(spec.getCompanyNumber());
        appointmentsData.setUpdated(now);

        appointmentsData.setSecureOfficer(Boolean.TRUE.equals(spec.getSecureOfficer()));

        return appointmentsData;
    }

    private Links createAppointmentLinks(
            String companyNumber, String officerId, String appointmentId) {
        var links = new Links();
        links.setSelf(COMPANY_LINK + companyNumber + APPOINTMENT_LINK_STEM + "/" + appointmentId);
        links.setOfficerSelf(OFFICERS_LINK + officerId);
        links.setOfficerAppointments(OFFICERS_LINK + officerId + APPOINTMENT_LINK_STEM);
        return links;
    }

    private void validateOfficerRole(String role) {
        try {
            OfficerRoles.valueOf(role.toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException ex) {
            LOG.error("Invalid officer role: " + role + ex);
            throw new IllegalArgumentException("Invalid officer role: " + role);
        }
    }

    private void createOfficerAppointment(
            CompanySpec spec, String officerId, String appointmentId, String role) {
        OfficerAppointment officerAppointment = new OfficerAppointment();

        Instant dayTimeNow = Instant.now();
        String roleName = setRoleName(role);
        officerAppointment.setId(officerId);
        officerAppointment.setCreatedAt(dayTimeNow);
        officerAppointment.setUpdatedAt(dayTimeNow);
        officerAppointment.setTotalResults(1);
        officerAppointment.setActiveCount(1);
        officerAppointment.setInactiveCount(0);
        officerAppointment.setResignedCount(1);
        officerAppointment.setCorporateOfficer(false);
        officerAppointment.setName(FORENAME + " " + roleName);

        Links links = new Links();
        links.setSelf(OFFICERS_LINK + officerId + APPOINTMENT_LINK_STEM);
        officerAppointment.setLinks(links);

        officerAppointment.setEtag(randomService.getEtag());
        officerAppointment.setDateOfBirthYear(DATE_OF_BIRTH.getYear());
        officerAppointment.setDateOfBirthMonth(DATE_OF_BIRTH.getMonthValue());

        var dayNow = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant();
        officerAppointment.setOfficerAppointmentItems(
                createOfficerAppointmentItems(spec, appointmentId, dayNow, dayTimeNow, role)
        );

        officerRepository.save(officerAppointment);
    }

    private List<OfficerAppointmentItem> createOfficerAppointmentItems(
            CompanySpec companySpec,
            String appointmentId,
            Instant dayNow,
            Instant dayTimeNow,
            String role
    ) {
        var companyNumber = companySpec.getCompanyNumber();
        Jurisdiction jurisdiction = companySpec.getJurisdiction();
        String roleName = setRoleName(role);

        OfficerAppointmentItem item = new OfficerAppointmentItem();
        item.setOccupation(roleName);
        item.setAddress(addressService.getAddress(jurisdiction));
        item.setForename(FORENAME);
        item.setSurname(roleName);
        item.setOfficerRole(role);
        item.setLinks(createOfficerAppointmentItemLinks(companyNumber, appointmentId));
        item.setCountryOfResidence(addressService.getCountryOfResidence(jurisdiction));
        item.setAppointedOn(dayNow);
        item.setNationality(NATIONALITY);
        item.setUpdatedAt(dayTimeNow);
        item.setName(FORENAME + " " + roleName);
        item.setCompanyName("Company " + companyNumber);
        item.setCompanyNumber(companyNumber);
        item.setCompanyStatus(COMPANY_STATUS);

        item.setSecureOfficer(Boolean.TRUE.equals(companySpec.getSecureOfficer()));

        List<OfficerAppointmentItem> officerAppointmentItemList = new ArrayList<>();
        officerAppointmentItemList.add(item);

        return officerAppointmentItemList;
    }

    private Links createOfficerAppointmentItemLinks(String companyNumber, String appointmentId) {
        Links links = new Links();
        links.setSelf(COMPANY_LINK + companyNumber + APPOINTMENT_LINK_STEM + "/" + appointmentId);
        links.setCompany(COMPANY_LINK + companyNumber);
        return links;
    }

    private String setRoleName(String role) {
        return role.toLowerCase().replace(role.substring(0, 1),
                role.substring(0, 1).toUpperCase());
    }
}