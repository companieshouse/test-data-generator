package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointment;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointmentItem;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.model.rest.OfficerRoles;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class AppointmentsServiceImpl implements DataService<List<Appointment>, CompanySpec> {

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

    @Autowired
    private AddressService addressService;
    @Autowired
    private RandomService randomService;
    @Autowired
    private AppointmentsRepository appointmentsRepository;
    @Autowired
    private OfficerRepository officerRepository;

    @Override
    public List<Appointment> create(CompanySpec spec) {
        LOG.info("Starting creation of" + APPOINTMENT_MSG + spec.getCompanyNumber());

        final var companyNumber = spec.getCompanyNumber();
        final String countryOfResidence =
                addressService.getCountryOfResidence(spec.getJurisdiction());
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

        LOG.info("Creating " + numberOfAppointments + APPOINTMENT_MSG + companyNumber);

        List<Appointment> createdAppointments = new ArrayList<>();

        for (int i = 0; i < numberOfAppointments; i++) {
            OfficerRoles currentRoleEnum = officerRoleList.get(i);
            if (currentRoleEnum == null) {
                LOG.error("Invalid officer role: null at index " + i);
                throw new IllegalArgumentException("Invalid officer role: null");
            }
            String currentRole = currentRoleEnum.getValue();
            try {
                OfficerRoles.valueOf(currentRole.toUpperCase().replace("-", "_"));
            } catch (IllegalArgumentException ex) {
                LOG.error("Invalid officer role: " + currentRole + ex);
                throw new IllegalArgumentException("Invalid officer role: " + currentRole);
            }

            String roleName = setRoleName(currentRole);
            LOG.debug("Processing appointment {} with role: " + (i + 1) + currentRole);

            Appointment appointment = new Appointment();
            String appointmentId = randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH);
            String internalId = INTERNAL_ID_PREFIX + randomService.getNumber(INTERNAL_ID_LENGTH);
            String officerId = randomService.addSaltAndEncode(internalId, SALT_LENGTH);

            LOG.debug("Generated IDs - Appointment ID: "
                    + appointmentId + ", Internal ID: "
                    + internalId + ", Officer ID: " + officerId);

            LocalDate officerDob = LocalDate.of(1990, 3, 6);
            Instant dateTimeNow = Instant.now();
            var today = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant();
            Instant dob = officerDob.atStartOfDay(ZoneId.of("UTC")).toInstant();

            appointment.setId(appointmentId);
            appointment.setCreated(dateTimeNow);
            appointment.setInternalId(internalId);
            appointment.setAppointmentId(appointmentId);
            appointment.setNationality(NATIONALITY);
            appointment.setOccupation(roleName);
            appointment.setServiceAddressIsSameAsRegisteredOfficeAddress(true);
            appointment.setCountryOfResidence(countryOfResidence);
            appointment.setUpdatedAt(dateTimeNow);
            appointment.setForename(FORENAME + (i + 1));
            appointment.setAppointedOn(today);
            appointment.setOfficerRole(currentRole);
            appointment.setEtag(randomService.getEtag());
            appointment.setServiceAddress(addressService.getAddress(spec.getJurisdiction()));
            appointment.setDataCompanyNumber(companyNumber);

            Links links = new Links();
            links.setSelf(COMPANY_LINK + companyNumber + APPOINTMENT_LINK_STEM + "/"
                    + appointmentId);
            links.setOfficerSelf(OFFICERS_LINK + officerId);
            links.setOfficerAppointments(OFFICERS_LINK + officerId + APPOINTMENT_LINK_STEM);
            appointment.setLinks(links);

            appointment.setSurname(roleName);
            appointment.setDateOfBirth(dob);
            appointment.setCompanyName("Company " + companyNumber);
            appointment.setCompanyStatus(COMPANY_STATUS);
            appointment.setOfficerId(officerId);
            appointment.setCompanyNumber(companyNumber);
            appointment.setUpdated(dateTimeNow);

            LOG.debug("Creating officer appointment for officer ID: " + officerId);
            this.createOfficerAppointment(spec, officerId, appointmentId, currentRole);

            Appointment savedAppointment = appointmentsRepository.save(appointment);
            LOG.info("Appointment saved with ID: " + savedAppointment.getId());
            createdAppointments.add(savedAppointment);
        }

        LOG.info("Successfully created "
                + createdAppointments.size() + APPOINTMENT_MSG + companyNumber);
        return createdAppointments;
    }

    @Override
    public boolean delete(String companyNumber) {
        LOG.info("Starting deletion of" + APPOINTMENT_MSG + companyNumber);

        List<Appointment> foundAppointments
                = appointmentsRepository.findAllByCompanyNumber(companyNumber);

        if (!foundAppointments.isEmpty()) {
            LOG.info("Found " + foundAppointments.size() + APPOINTMENT_MSG + companyNumber);

            for (Appointment ap : foundAppointments) {
                String officerId = ap.getOfficerId();
                officerRepository.findById(officerId).ifPresent(officer -> {
                    LOG.debug("Deleting officer with ID: " + officerId);
                    officerRepository.delete(officer);
                });
            }

            LOG.info("Deleting all" + APPOINTMENT_MSG + companyNumber);
            appointmentsRepository.deleteAll(foundAppointments);
            LOG.info("Successfully deleted all " + APPOINTMENT_MSG + companyNumber);
            return true;
        }

        LOG.info("No appointments found for company number: " + companyNumber);
        return false;
    }

    private void createOfficerAppointment(
            CompanySpec spec, String officerId, String appointmentId, String role) {
        OfficerAppointment officerAppointment = new OfficerAppointment();

        Instant dayTimeNow = Instant.now();
        Instant dayNow = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant();
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
        officerAppointment.setDateOfBirthYear(1990);
        officerAppointment.setDateOfBirthMonth(3);

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
        List<OfficerAppointmentItem> officerAppointmentItemList = new ArrayList<>();

        var companyNumber = companySpec.getCompanyNumber();
        Jurisdiction jurisdiction = companySpec.getJurisdiction();
        String roleName = setRoleName(role);
        OfficerAppointmentItem officerAppointmentItem = new OfficerAppointmentItem();
        officerAppointmentItem.setOccupation(roleName);
        officerAppointmentItem.setAddress(addressService.getAddress(jurisdiction));
        officerAppointmentItem.setForename(FORENAME);
        officerAppointmentItem.setSurname(roleName);
        officerAppointmentItem.setOfficerRole(role);
        officerAppointmentItem.setLinks(
                createOfficerAppointmentItemLinks(companyNumber, appointmentId));
        officerAppointmentItem.setCountryOfResidence(
                addressService.getCountryOfResidence(jurisdiction));
        officerAppointmentItem.setAppointedOn(dayNow);
        officerAppointmentItem.setNationality(NATIONALITY);
        officerAppointmentItem.setUpdatedAt(dayTimeNow);
        officerAppointmentItem.setName(roleName + " " + FORENAME);
        officerAppointmentItem.setCompanyName("Company " + companyNumber);
        officerAppointmentItem.setCompanyNumber(companyNumber);
        officerAppointmentItem.setCompanyStatus(COMPANY_STATUS);
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

        officerAppointmentItemList.add(officerAppointmentItem);

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