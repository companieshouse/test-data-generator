package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.DeltaAppointment;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointment;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointmentItem;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.repository.DeltaAppointmentsRepository;
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
    private DeltaAppointmentsRepository deltaAppointmentsRepository;
    @Autowired
    private OfficerRepository officerRepository;

    @Override
    public Appointment create(CompanySpec spec) {
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
        appointment.setOccupation(OCCUPATION);
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

        this.createDeltaAppointment(companyNumber, appointment, dateTimeNow, officerId);
        this.createOfficerAppointment(spec, officerId, appointmentId);

        return appointmentsRepository.save(appointment);
    }

    @Override
    public boolean delete(String companyNumber) {
        Optional<Appointment> existingAppointment = appointmentsRepository.findByCompanyNumber(companyNumber);

        if (existingAppointment.isPresent()) {
            String officerId = existingAppointment.get().getOfficerId();
            officerRepository.findById(officerId).ifPresent(officerRepository::delete);
            deltaAppointmentsRepository.findById(existingAppointment.get().getId()).ifPresent(deltaAppointmentsRepository::delete);
            appointmentsRepository.delete(existingAppointment.get());
            return true;
        }
        return false;
    }

    private DeltaAppointment createDeltaAppointment(String companyNumber, Appointment appointment, Instant dateTimeNow, String officerId) {
        DeltaAppointment deltaAppointment = DeltaAppointment.Builder.builder()
                .fromAppointment(appointment)
                .addressLine1(appointment.getServiceAddress().getAddressLine1())
                .addressLine2(appointment.getServiceAddress().getAddressLine2())
                .country(appointment.getServiceAddress().getCountry())
                .locality(appointment.getServiceAddress().getLocality())
                .postalCode(appointment.getServiceAddress().getPostalCode())
                .premises(appointment.getServiceAddress().getPremise())
                .created(dateTimeNow)
                .updated(dateTimeNow)
                .deltaAt(dateTimeNow)
                .officerRoleSortOrder(20)
                .self("/company/" + companyNumber + "/appointments/" + appointment.getId())
                .officerSelf("/officers/" + officerId)
                .officerAppointments("/officers/" + officerId + "/appointments")
                .build();
        return deltaAppointmentsRepository.save(deltaAppointment);
    }

    private void createOfficerAppointment(CompanySpec spec, String officerId, String appointmentId) {

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

        officerRepository.save(officerAppointment);
    }

    private List<OfficerAppointmentItem> createOfficerAppointmentItems(CompanySpec companySpec, String appointmentId,
                                                                       Instant dayNow, Instant dayTimeNow) {
        List<OfficerAppointmentItem> officerAppointmentItemList = new ArrayList<>();

        String companyNumber = companySpec.getCompanyNumber();
        Jurisdiction jurisdiction = companySpec.getJurisdiction();

        OfficerAppointmentItem officerAppointmentItem = new OfficerAppointmentItem();
        officerAppointmentItem.setOccupation(OCCUPATION);
        officerAppointmentItem.setAddress(addressService.getAddress(jurisdiction));
        officerAppointmentItem.setForename("Test");
        officerAppointmentItem.setSurname(OCCUPATION);
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
