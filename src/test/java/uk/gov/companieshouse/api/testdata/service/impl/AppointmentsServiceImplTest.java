package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointment;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.model.rest.OfficerRoles;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class AppointmentsServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String ENCODED_VALUE = "ENCODED";
    private static final Long GENERATED_ID = 123456789L;
    private static final String ENCODED_INTERNAL_ID = "ENCODED_2";
    private static final String ETAG = "ETAG";
    private static final int INTERNAL_ID_LENGTH = 9;
    private static final String INTERNAL_ID_PREFIX = "8";

    @Mock
    private AddressService addressService;
    @Mock
    private AppointmentsRepository appointmentsRepository;
    @Mock
    private OfficerRepository officerRepository;
    @Mock
    private RandomService randomService;

    @InjectMocks
    private AppointmentsServiceImpl appointmentsService;

    @Mock
    private Appointment commonAppointment;

    @Test
    void create() {
        final Address mockServiceAddress = new Address("", "", "", "", "", "");
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        when(randomService.getNumber(INTERNAL_ID_LENGTH)).thenReturn(GENERATED_ID);
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(randomService.addSaltAndEncode(INTERNAL_ID_PREFIX + GENERATED_ID, 8))
                .thenReturn(ENCODED_INTERNAL_ID);
        when(randomService.getEtag()).thenReturn(ETAG);

        when(addressService.getAddress(Jurisdiction.ENGLAND_WALES)).thenReturn(mockServiceAddress);
        when(addressService.getCountryOfResidence(Jurisdiction.ENGLAND_WALES))
                .thenReturn("Wales");

        Appointment savedApt = new Appointment();
        when(appointmentsRepository.save(any())).thenReturn(savedApt);

        List<Appointment> returnedApts = appointmentsService.create(spec);

        assertNotNull(returnedApts);
        assertEquals(1, returnedApts.size());
        assertEquals(savedApt, returnedApts.get(0));

        ArgumentCaptor<Appointment> aptCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentsRepository).save(aptCaptor.capture());

        Appointment appointment = aptCaptor.getValue();
        assertNotNull(appointment);
        assertEquals(ENCODED_VALUE, appointment.getId());
        assertEquals(ENCODED_VALUE, appointment.getAppointmentId());
        assertNotNull(appointment.getCreated());

        assertEquals(INTERNAL_ID_PREFIX + GENERATED_ID, appointment.getInternalId());
        assertEquals(ENCODED_INTERNAL_ID, appointment.getOfficerId());
        assertEquals(COMPANY_NUMBER, appointment.getCompanyNumber());
        assertNotNull(appointment.getUpdated());

        assertEquals("Company " + COMPANY_NUMBER, appointment.getCompanyName());
        assertEquals("active", appointment.getCompanyStatus());
        assertEquals("British", appointment.getNationality());
        assertEquals("Director", appointment.getOccupation());
        assertTrue(appointment.isServiceAddressIsSameAsRegisteredOfficeAddress());
        assertEquals("Wales", appointment.getCountryOfResidence());
        assertNotNull(appointment.getUpdatedAt());
        assertTrue(appointment.getForename().startsWith("Test"));
        assertNotNull(appointment.getAppointedOn());
        assertEquals(OfficerRoles.DIRECTOR.getValue(), appointment.getOfficerRole());
        assertEquals(ETAG, appointment.getEtag());
        assertEquals(mockServiceAddress, appointment.getServiceAddress());
        assertEquals(COMPANY_NUMBER, appointment.getDataCompanyNumber());

        Links links = appointment.getLinks();
        assertEquals("/company/" + COMPANY_NUMBER + "/appointments/" + ENCODED_INTERNAL_ID, links.getSelf());
        assertEquals("/officers/" + ENCODED_INTERNAL_ID, links.getOfficerSelf());
        assertEquals("/officers/" + ENCODED_INTERNAL_ID + "/appointments", links.getOfficerAppointments());

        assertEquals("DIRECTOR", appointment.getSurname());
        assertNotNull(appointment.getDateOfBirth());
    }

    @Test
    void createScottish() {
        final Address mockServiceAddress = new Address("", "", "", "", "", "");
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setJurisdiction(Jurisdiction.SCOTLAND);

        when(randomService.getNumber(INTERNAL_ID_LENGTH)).thenReturn(GENERATED_ID);
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(randomService.addSaltAndEncode(INTERNAL_ID_PREFIX + GENERATED_ID, 8))
                .thenReturn(ENCODED_INTERNAL_ID);
        when(randomService.getEtag()).thenReturn(ETAG);

        when(addressService.getAddress(Jurisdiction.SCOTLAND)).thenReturn(mockServiceAddress);
        when(addressService.getCountryOfResidence(Jurisdiction.SCOTLAND))
                .thenReturn("Scotland");

        Appointment savedApt = new Appointment();
        when(appointmentsRepository.save(any())).thenReturn(savedApt);

        List<Appointment> returnedApts = appointmentsService.create(spec);

        assertNotNull(returnedApts);
        assertEquals(1, returnedApts.size());
        Appointment returnedApt = returnedApts.get(0);
        assertEquals(savedApt, returnedApt);

        ArgumentCaptor<Appointment> aptCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentsRepository).save(aptCaptor.capture());

        Appointment appointment = aptCaptor.getValue();
        assertNotNull(appointment);
        assertEquals(ENCODED_VALUE, appointment.getId());
        assertEquals(ENCODED_VALUE, appointment.getAppointmentId());
        assertNotNull(appointment.getCreated());

        assertEquals(INTERNAL_ID_PREFIX + GENERATED_ID, appointment.getInternalId());
        assertEquals(ENCODED_INTERNAL_ID, appointment.getOfficerId());
        assertEquals(COMPANY_NUMBER, appointment.getCompanyNumber());
        assertNotNull(appointment.getUpdated());

        assertEquals("Company " + COMPANY_NUMBER, appointment.getCompanyName());
        assertEquals("active", appointment.getCompanyStatus());
        assertEquals("British", appointment.getNationality());
        assertEquals("Director", appointment.getOccupation());
        assertTrue(appointment.isServiceAddressIsSameAsRegisteredOfficeAddress());
        assertEquals("Scotland", appointment.getCountryOfResidence());
        assertNotNull(appointment.getUpdatedAt());
        assertTrue(appointment.getForename().startsWith("Test"));
        assertNotNull(appointment.getAppointedOn());
        assertEquals(OfficerRoles.DIRECTOR.getValue(), appointment.getOfficerRole());
        assertEquals(ETAG, appointment.getEtag());
        assertEquals(mockServiceAddress, appointment.getServiceAddress());
        assertEquals(COMPANY_NUMBER, appointment.getDataCompanyNumber());

        Links links = appointment.getLinks();
        assertEquals("/company/" + COMPANY_NUMBER + "/appointments/" + ENCODED_INTERNAL_ID, links.getSelf());
        assertEquals("/officers/" + ENCODED_INTERNAL_ID, links.getOfficerSelf());
        assertEquals("/officers/" + ENCODED_INTERNAL_ID + "/appointments", links.getOfficerAppointments());

        assertEquals("DIRECTOR", appointment.getSurname());
        assertNotNull(appointment.getDateOfBirth());
    }

    @Test
    void createWithInvalidOfficerRole() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setOfficerRoles(List.of("invalid_role"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            appointmentsService.create(spec);
        });
        assertEquals("Invalid officer role: invalid_role", exception.getMessage());
    }

    @Test
    void createWithMixedValidAndInvalidOfficerRoles() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setNumberOfAppointments(3);
        spec.setOfficerRoles(List.of(OfficerRoles.DIRECTOR.getValue(), "invalid_role", OfficerRoles.SECRETARY.getValue()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            appointmentsService.create(spec);
        });
        assertEquals("Invalid officer role: invalid_role", exception.getMessage());
    }

    @Test
    void createWithDefaultOfficerRole() {
        final Address mockServiceAddress = new Address("", "", "", "", "", "");
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setNumberOfAppointments(2);

        when(randomService.getNumber(INTERNAL_ID_LENGTH)).thenReturn(GENERATED_ID);
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(randomService.addSaltAndEncode(INTERNAL_ID_PREFIX + GENERATED_ID, 8))
                .thenReturn(ENCODED_INTERNAL_ID);
        when(randomService.getEtag()).thenReturn(ETAG);

        when(addressService.getAddress(Jurisdiction.ENGLAND_WALES)).thenReturn(mockServiceAddress);
        when(addressService.getCountryOfResidence(Jurisdiction.ENGLAND_WALES))
                .thenReturn("Wales");

        Appointment savedApt = new Appointment();
        when(appointmentsRepository.save(any())).thenReturn(savedApt);

        List<Appointment> returnedApts = appointmentsService.create(spec);

        assertNotNull(returnedApts);
        assertEquals(2, returnedApts.size());

        verify(appointmentsRepository, times(2)).save(any(Appointment.class));
    }

    @Test
    void createWithMultipleAppointments() {
        final Address mockServiceAddress = new Address("", "", "", "", "", "");
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setNumberOfAppointments(3);
        spec.setOfficerRoles(List.of(OfficerRoles.DIRECTOR.getValue(), OfficerRoles.SECRETARY.getValue(), OfficerRoles.DIRECTOR.getValue()));

        when(randomService.getNumber(INTERNAL_ID_LENGTH)).thenReturn(GENERATED_ID);
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(randomService.addSaltAndEncode(INTERNAL_ID_PREFIX + GENERATED_ID, 8))
                .thenReturn(ENCODED_INTERNAL_ID);
        when(randomService.getEtag()).thenReturn(ETAG);

        when(addressService.getAddress(Jurisdiction.ENGLAND_WALES)).thenReturn(mockServiceAddress);
        when(addressService.getCountryOfResidence(Jurisdiction.ENGLAND_WALES))
                .thenReturn("Wales");

        Appointment savedApt = new Appointment();
        when(appointmentsRepository.save(any())).thenReturn(savedApt);

        List<Appointment> returnedApts = appointmentsService.create(spec);

        assertNotNull(returnedApts);
        assertEquals(3, returnedApts.size());

        verify(appointmentsRepository, times(3)).save(any(Appointment.class));
    }

    @Test
    void delete() {
        Appointment apt = new Appointment();
        apt.setOfficerId("TEST_OFFICER_ID");

        OfficerAppointment officerAppointment = new OfficerAppointment();
        when(appointmentsRepository.findAllByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(Collections.singletonList(apt));
        when(officerRepository.findById("TEST_OFFICER_ID"))
                .thenReturn(Optional.of(officerAppointment));

        boolean result = appointmentsService.delete(COMPANY_NUMBER);

        assertTrue(result);
        verify(officerRepository).findById("TEST_OFFICER_ID");
        verify(officerRepository).delete(officerAppointment);
        verify(appointmentsRepository).deleteAll(Collections.singletonList(apt));
    }

    @Test
    void deleteNoAppointmentData() {
        when(appointmentsRepository.findAllByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(Collections.emptyList());

        boolean result = appointmentsService.delete(COMPANY_NUMBER);
        assertFalse(result);

        verify(officerRepository, never()).delete(any());
        verify(appointmentsRepository, never()).deleteAll(anyList());
    }

    @Test
    void deleteNoOfficerData() {
        Appointment apt = new Appointment();
        apt.setOfficerId("UNKNOWN_OFFICER_ID");

        when(appointmentsRepository.findAllByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(Collections.singletonList(apt));
        when(officerRepository.findById("UNKNOWN_OFFICER_ID"))
                .thenReturn(Optional.empty());

        boolean result = appointmentsService.delete(COMPANY_NUMBER);
        assertTrue(result);
        verify(officerRepository, never()).delete(any());
        verify(appointmentsRepository).deleteAll(Collections.singletonList(apt));
    }
}
