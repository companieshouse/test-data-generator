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
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.AppointmentService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class AppointmentsServiceImpl implements AppointmentService {

    private static final int SALT_LENGTH = 8;
    private static final int ID_LENGTH = 10;
    private static final int INTERNAL_ID_LENGTH = 9;
    private static final String INTERNAL_ID_PREFIX = "8";
    private static final String COMPANY_LINK = "/company/";
    private static final String OFFICERS_LINK = "/officers/";
    private static final String OCCUPATION = "Director";

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
        final String companyNumber = spec.getCompanyNumber();
        final String countryOfResidence = addressService.getCountryOfResidence(spec.getJurisdiction());
        int numberOfAppointments = spec.getNumberOfAppointments();
        if (numberOfAppointments <= 0) {
            numberOfAppointments = 1;
        }

        List<String> officerRoles = spec.getOfficerRoles();
        if (officerRoles == null) {
            officerRoles = new ArrayList<>();
        }

        for (String role : officerRoles) {
            if (!role.equalsIgnoreCase("director") && !role.equalsIgnoreCase("secretary")) {
                throw new IllegalArgumentException("Invalid officer role: " + role);
            }
        }

        while (officerRoles.size() < numberOfAppointments) {
            officerRoles.add("director");
        }

        List<Appointment> createdAppointments = new ArrayList<>();

        for (int i = 0; i < numberOfAppointments; i++) {
            String currentRole = officerRoles.get(i);

            Appointment appointment = new Appointment();

            String appointmentId = randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH);
            String internalId = INTERNAL_ID_PREFIX + this.randomService.getNumber(INTERNAL_ID_LENGTH);
            String officerId = randomService.addSaltAndEncode(internalId, SALT_LENGTH);

            LocalDate officerDob = LocalDate.of(1990, 3, 6);
            Instant dateTimeNow = Instant.now();
            Instant dateNow = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant();
            Instant dob = officerDob.atStartOfDay(ZoneId.of("UTC")).toInstant();

            appointment.setId(appointmentId);
            appointment.setCreated(dateTimeNow);
            appointment.setInternalId(internalId);
            appointment.setAppointmentId(appointmentId);
            appointment.setNationality("British");
            appointment.setOccupation(OCCUPATION);
            appointment.setServiceAddressIsSameAsRegisteredOfficeAddress(true);
            appointment.setCountryOfResidence(countryOfResidence);
            appointment.setUpdatedAt(dateTimeNow);
            appointment.setForename("Test " + (i + 1));
            appointment.setAppointedOn(dateNow);
            appointment.setOfficerRole(currentRole);
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

            this.createOfficerAppointment(spec, officerId, appointmentId, currentRole);

            Appointment savedAppointment = appointmentsRepository.save(appointment);
            createdAppointments.add(savedAppointment);
        }

        return createdAppointments;
    }

    @Override
    public boolean delete(String companyNumber) {
        List<Appointment> foundAppointments =
                appointmentsRepository.findAllByCompanyNumber(companyNumber);

        if (!foundAppointments.isEmpty()) {
            for (Appointment ap : foundAppointments) {
                String officerId = ap.getOfficerId();
                officerRepository.findById(officerId).ifPresent(officerRepository::delete);
            }
            appointmentsRepository.deleteAll(foundAppointments);
            return true;
        }
        return false;
    }

    private void createOfficerAppointment(CompanySpec spec,
                                          String officerId, String appointmentId, String role) {
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

        String companyNumber = companySpec.getCompanyNumber();
        Jurisdiction jurisdiction = companySpec.getJurisdiction();

        OfficerAppointmentItem item = new OfficerAppointmentItem();
        item.setOccupation(OCCUPATION);
        item.setAddress(addressService.getAddress(jurisdiction));
        item.setForename("Test");
        item.setSurname(OCCUPATION);
        item.setOfficerRole(role);
        item.setLinks(createOfficerAppointmentItemLinks(companyNumber, appointmentId));
        item.setCountryOfResidence(addressService.getCountryOfResidence(jurisdiction));
        item.setAppointedOn(dayNow);
        item.setNationality("British");
        item.setUpdatedAt(dayTimeNow);
        item.setName("Test DIRECTOR");
        item.setCompanyName("Company " + companyNumber);
        item.setCompanyNumber(companyNumber);
        item.setCompanyStatus("active");

        officerAppointmentItemList.add(item);

        return officerAppointmentItemList;
    }

    private Links createOfficerAppointmentItemLinks(String companyNumber, String appointmentId) {

        Links links = new Links();
        links.setSelf(COMPANY_LINK + companyNumber + "/appointments/" + appointmentId);
        links.setCompany(COMPANY_LINK + companyNumber);

        return links;
    }

}
