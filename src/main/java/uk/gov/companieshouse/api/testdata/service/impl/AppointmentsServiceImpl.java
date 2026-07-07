package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.AppointmentsData;
import uk.gov.companieshouse.api.testdata.model.entity.FormerName;
import uk.gov.companieshouse.api.testdata.model.entity.Identification;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointment;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointmentItem;
import uk.gov.companieshouse.api.testdata.model.rest.enums.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.request.AppointmentCreationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AppointmentsResultResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.model.rest.enums.OfficerType;
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

    public AppointmentsResultResponse createAppointment(InternalCompanyRequest internalCompanyRequest) {
        if (Boolean.TRUE.equals(internalCompanyRequest.getNoDefaultOfficer())) {
            LOG.info("No default officer request, skipping appointment creation for: "
                    + internalCompanyRequest.getCompanyNumber());
            return null;
        }

        LOG.info("Starting creation of appointments with matching IDs for company number: "
                + internalCompanyRequest.getCompanyNumber());

        final var companyNumber = internalCompanyRequest.getCompanyNumber();
        final String countryOfResidence = addressService.getCountryOfResidence(
                internalCompanyRequest.getJurisdiction());
        Integer numberOfAppointments = internalCompanyRequest.getNumberOfAppointments();
        boolean explicitlySet = payloadExplicitlySetNumberOfAppointments(internalCompanyRequest);

        if (internalCompanyRequest.getCompanyType() == CompanyType.PLC) {
            // Always ensure at least 2 directors and 1 secretary for PLC
            if (!explicitlySet || numberOfAppointments == null || numberOfAppointments < 3) {
                LOG.info("PLC company type and numberOfAppointments not set or less than 3. Defaulting to 2 directors and 1 secretary");
                numberOfAppointments = 3;
            }
        } else {
            if (!explicitlySet || numberOfAppointments == null || numberOfAppointments <= 0) {
                LOG.info("Number of appointments not set or <= 0. Defaulting to 1.");
                numberOfAppointments = 1;
            }
        }

        List<OfficerType> officerRoleList = new ArrayList<>();
        List<OfficerType> providedRoles = internalCompanyRequest.getOfficerRoles();
        int providedCount = (providedRoles != null) ? providedRoles.size() : 0;
        if (providedCount > 0) {
            officerRoleList.addAll(providedRoles);
            LOG.debug("Officer roles provided: " + providedRoles);
        }
        if (internalCompanyRequest.getCompanyType() == CompanyType.PLC) {
            for (int i = providedCount; i < numberOfAppointments; i++) {
                OfficerType officerType = (i == 2) ? OfficerType.SECRETARY : OfficerType.DIRECTOR;
                officerRoleList.add(officerType);
            }
        } else {
            for (int i = providedCount; i < numberOfAppointments; i++) {
                officerRoleList.add(OfficerType.DIRECTOR);
            }
        }

        List<String> appointmentIds = new ArrayList<>();
        for (var i = 0; i < numberOfAppointments; i++) {
            appointmentIds.add(randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH));
        }

        List<Appointment> createdAppointments = new ArrayList<>();
        List<AppointmentsData> createdAppointmentsData = new ArrayList<>();
        List<OfficerAppointment> createdOfficerAppointments = new ArrayList<>();

        for (var i = 0; i < numberOfAppointments; i++) {
            OfficerType currentRoleEnum = officerRoleList.get(i);
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
                    .spec(internalCompanyRequest)
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
            var officerAppointment = this.createOfficerAppointment(internalCompanyRequest, officerId, appointmentId, currentRole);
            createdOfficerAppointments.add(officerAppointment);
            if (Boolean.FALSE.equals(internalCompanyRequest.getCompanyWithPopulatedStructureOnly())) {
                Appointment savedAppointment = appointmentsRepository.save(appointment);
                LOG.info("Appointment saved with ID: " + savedAppointment.getId());
            }
            createdAppointments.add(appointment);

            // Create AppointmentsData with same appointmentId
            var appointmentsData = createBaseAppointmentsData(
                    internalCompanyRequest, internalId, officerId, dateTimeNow, appointmentId);
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
                    + internalCompanyRequest.getCompanyNumber() + "/appointments/" + appointmentId);
            appointmentsData.setLinks(dataLinks);
            if (Boolean.FALSE.equals(internalCompanyRequest.getCompanyWithPopulatedStructureOnly())) {
                var savedData = appointmentsDataRepository.save(appointmentsData);
                LOG.info("AppointmentsData saved with ID: " + savedData.getId());
            }
            createdAppointmentsData.add(appointmentsData);
        }
        var appointmentsResultData = new AppointmentsResultResponse();
        appointmentsResultData.setAppointment(createdAppointments);
        appointmentsResultData.setAppointmentsData(createdAppointmentsData);
        appointmentsResultData.setOfficerAppointment(createdOfficerAppointments);
        if (Boolean.TRUE.equals(internalCompanyRequest.getCompanyWithPopulatedStructureOnly())) {
            return appointmentsResultData;
        }
        LOG.info("Successfully created " + createdAppointments.size() + " appointments and "
                + createdAppointmentsData.size()
                + " appointments data with matching IDs for company number: " + companyNumber);
        return appointmentsResultData;
    }

    @Override
    public AppointmentsResultResponse createAppointment(AppointmentCreationRequest spec) {

        LOG.info("Starting bulk appointment creation via API for company: {}");

        String companyNumber = spec.getCompanyNumber();
        List<String> officerRoles = spec.getOfficerRoles();
        List<String> identificationTypes = spec.getIdentificationType();

        if (officerRoles == null || officerRoles.isEmpty()) {
            throw new IllegalArgumentException("officer_roles must not be empty");
        }

        Instant now = Instant.now();
        Instant appointedOn = (spec.getAppointedOn() != null) ? spec.getAppointedOn() : now;

        AppointmentAccumulator accumulator = new AppointmentAccumulator();

        for (String role : officerRoles) {
            validateOfficerRole(role);

            boolean isCorporate = role.contains("corporate");

            if (isCorporate && identificationTypes != null && !identificationTypes.isEmpty()) {
                for (String identificationType : identificationTypes) {
                    SingleAppointmentContext ctx = new SingleAppointmentContext(
                            spec, role, identificationType, companyNumber, now, appointedOn);
                    createSingleAppointment(ctx, accumulator);
                }
            } else {
                SingleAppointmentContext ctx = new SingleAppointmentContext(
                        spec, role, null, companyNumber, now, appointedOn);
                createSingleAppointment(ctx, accumulator);
            }
        }

        AppointmentsResultResponse response = new AppointmentsResultResponse();
        accumulator.applyTo(response);

        LOG.info("Created {} appointments via API for company {}");

        return response;
    }

    private void createSingleAppointment(
            SingleAppointmentContext ctx,
            AppointmentAccumulator accumulator) {

        String internalId = INTERNAL_ID_PREFIX + randomService.getNumber(INTERNAL_ID_LENGTH);
        String officerId = randomService.addSaltAndEncode(internalId, SALT_LENGTH);
        String appointmentId = randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH);

        InternalCompanyRequest safeSpec = ctx.spec.getSpec();
        if (safeSpec == null) {
            safeSpec = new InternalCompanyRequest();
            safeSpec.setCompanyNumber(ctx.companyNumber);
            safeSpec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
            safeSpec.setSecureOfficer(false);
        }

        String countryOfResidence = (ctx.spec.getCountryOfResidence() != null)
                ? ctx.spec.getCountryOfResidence()
                : "united-kingdom";

        Instant appointedOnAdjusted = ctx.appointedOn;
        Instant resignedOnAdjusted = null;

        if (Boolean.TRUE.equals(ctx.spec.getResignedOn())) {
            appointedOnAdjusted = LocalDate.now()
                    .minusYears(1)
                    .atStartOfDay(ZoneId.of("UTC"))
                    .toInstant();
            resignedOnAdjusted = LocalDate.now()
                    .minusMonths(1)
                    .atStartOfDay(ZoneId.of("UTC"))
                    .toInstant();
        }

        boolean isCorporate = ctx.role.contains("corporate");

        AppointmentCreationRequest request = AppointmentCreationRequest.builder()
                .spec(safeSpec)
                .companyNumber(ctx.companyNumber)
                .countryOfResidence(countryOfResidence)
                .internalId(internalId)
                .officerId(officerId)
                .dateTimeNow(ctx.now)
                .appointedOn(appointedOnAdjusted)
                .appointmentId(appointmentId)
                .build();

        Appointment appointment = createBaseAppointment(request);
        String roleName = setRoleName(ctx.role);

        appointment.setForename(FORENAME);
        appointment.setSurname(roleName);
        appointment.setOccupation(roleName);
        appointment.setOfficerRole(ctx.role);

        if (!isCorporate) {
            if (request.getCountryOfResidence() != null){
                appointment.setCountryOfResidence("United Kingdom");
            } else {
                appointment.setCountryOfResidence(request.getCountryOfResidence());
            }

            appointment.setForename("John");
            appointment.setOtherForeNames("Michael");
            appointment.setSurname("Smith");
            appointment.setNationality("British");

            appointment.setOccupation("Lawyer");

            appointment.setTitle("Mr");

            appointment.setDateOfBirth( LocalDate.now().minusYears(40).atStartOfDay(ZoneId.of("UTC")).toInstant());

            FormerName formerName = new FormerName();
            formerName.setForenames("David");
            formerName.setSurname("Brown");

            appointment.setFormerNames(List.of(formerName));

            Address residentialAddress = new Address();
            residentialAddress.setAddressLine1("ura_line1");
            residentialAddress.setAddressLine2("ura_line2");
            residentialAddress.setCountry("United Kingdom");
            residentialAddress.setPoBox("ura_po");
            residentialAddress.setPostalCode("CF2 1B6");
            residentialAddress.setPremise("URA");
            residentialAddress.setRegion("ura_region");

            appointment.setUsualResidentialAddress(residentialAddress);
        }


        if (Boolean.TRUE.equals(ctx.spec.getResignedOn())) {
            appointment.setResignedOn(resignedOnAdjusted);
        }

        if (Boolean.TRUE.equals(ctx.spec.getIsPre1992Appointment())) {
            appointment.setIsPre1992Appoinment(true);
        }

        if (ctx.identificationType != null) {
            Identification identification = new Identification();
            identification.setIdentificationType(ctx.identificationType);
            identification.setLegalAuthority("Chapter 32");
            identification.setLegalForm("Hong Kong");
            identification.setPlaceRegistered("United Kingdom");
            identification.setRegistrationNumber("38298");
            appointment.setIdentification(identification);
        }

        appointment.setLinks(createAppointmentLinks(ctx.companyNumber, officerId, appointmentId));

        Appointment saved = appointmentsRepository.save(appointment);
        accumulator.appointments.add(saved);

        AppointmentsData data = createBaseAppointmentsData(
                safeSpec, internalId, officerId, ctx.now, appointmentId);

        data.setForename(FORENAME);
        data.setSurname(roleName);
        data.setOccupation(roleName);
        data.setOfficerRole(ctx.role);

        AppointmentsData.Links dataLinks = new AppointmentsData.Links();
        AppointmentsData.OfficerLinks officerLinks = new AppointmentsData.OfficerLinks();
        officerLinks.setAppointments(OFFICERS_LINK + officerId + APPOINTMENT_LINK_STEM);
        officerLinks.setSelf(OFFICERS_LINK + officerId);
        dataLinks.setOfficer(officerLinks);
        dataLinks.setSelf(COMPANY_LINK + ctx.companyNumber + "/appointments/" + appointmentId);
        data.setLinks(dataLinks);

        AppointmentsData savedData = appointmentsDataRepository.save(data);
        accumulator.appointmentsData.add(savedData);

        OfficerAppointment officerAppointment =
                createOfficerAppointment(safeSpec, officerId, appointmentId, ctx.role);
        accumulator.officerAppointments.add(officerAppointment);
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
            InternalCompanyRequest spec, String internalId, String officerId,
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
            OfficerType.valueOf(role.toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException ex) {
            LOG.error("Invalid officer role: " + role + ex);
            throw new IllegalArgumentException("Invalid officer role: " + role);
        }
    }

    private OfficerAppointment createOfficerAppointment(
            InternalCompanyRequest spec, String officerId, String appointmentId, String role) {
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
        if (Boolean.TRUE.equals(spec.getCompanyWithPopulatedStructureOnly())) {
            return officerAppointment;
        }
        officerRepository.save(officerAppointment);
        return officerAppointment;
    }

    private List<OfficerAppointmentItem> createOfficerAppointmentItems(
            InternalCompanyRequest companySpec,
            String appointmentId,
            Instant dayNow,
            Instant dayTimeNow,
            String role
    ) {

        String companyNumber = (companySpec != null && companySpec.getCompanyNumber() != null)
                ? companySpec.getCompanyNumber()
                : "UNKNOWN";

        JurisdictionType jurisdiction = (companySpec != null && companySpec.getJurisdiction() != null)
                ? companySpec.getJurisdiction()
                : JurisdictionType.ENGLAND_WALES;

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

        item.setSecureOfficer(
                companySpec != null && Boolean.TRUE.equals(companySpec.getSecureOfficer())
        );


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

    private boolean payloadExplicitlySetNumberOfAppointments(InternalCompanyRequest spec) {
        return spec.isNumberOfAppointmentsSet();
    }

    /**
     * Immutable context object grouping all inputs needed to create a single appointment.
     * Replaces the 6 individual parameters on createSingleAppointment, resolving Sonar java:S107.
     */
    private static final class SingleAppointmentContext {

        private final AppointmentCreationRequest spec;
        private final String role;
        private final String identificationType; // null for non-corporate roles
        private final String companyNumber;
        private final Instant now;
        private final Instant appointedOn;

        private SingleAppointmentContext(
                AppointmentCreationRequest spec,
                String role,
                String identificationType,
                String companyNumber,
                Instant now,
                Instant appointedOn) {
            this.spec = spec;
            this.role = role;
            this.identificationType = identificationType;
            this.companyNumber = companyNumber;
            this.now = now;
            this.appointedOn = appointedOn;
        }
    }

    /**
     * Mutable accumulator that collects results across multiple createSingleAppointment calls.
     * Replaces the 3 List<> parameters passed into createSingleAppointment.
     */
    private static final class AppointmentAccumulator {

        private final List<Appointment> appointments = new ArrayList<>();
        private final List<AppointmentsData> appointmentsData = new ArrayList<>();
        private final List<OfficerAppointment> officerAppointments = new ArrayList<>();

        private void applyTo(AppointmentsResultResponse response) {
            response.setAppointment(appointments);
            response.setAppointmentsData(appointmentsData);
            response.setOfficerAppointment(officerAppointments);
        }
    }
}
