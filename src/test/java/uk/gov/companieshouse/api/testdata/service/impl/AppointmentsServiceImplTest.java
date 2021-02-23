package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class AppointmentsServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String ENCODED_VALUE = "ENCODED";
    private static final Long GENERATED_ID = 123456789L;
    private static final String ENCODED_INTERNAL_ID = "ENCODED 2";
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

    @Test
    void create() {
        final Address mockServiceAddress = new Address(
                "","","","","",""
        );

        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        
        when(randomService.getNumber(INTERNAL_ID_LENGTH)).thenReturn(GENERATED_ID);
        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.addSaltAndEncode(INTERNAL_ID_PREFIX + GENERATED_ID, 8)).thenReturn(ENCODED_INTERNAL_ID);
        when(this.randomService.getEtag()).thenReturn(ETAG);
        when(this.addressService.getAddress(Jurisdiction.ENGLAND_WALES)).thenReturn(mockServiceAddress);
        when(this.addressService.getCountryOfResidence(Jurisdiction.ENGLAND_WALES)).thenReturn("Wales");
        Appointment savedApt = new Appointment();
        when(this.appointmentsRepository.save(any())).thenReturn(savedApt);
        
        Appointment returnedApt = this.appointmentsService.create(spec);
        
        assertEquals(savedApt, returnedApt);
        
        ArgumentCaptor<Appointment> aptCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentsRepository).save(aptCaptor.capture());

        Appointment appointment = aptCaptor.getValue();
        assertNotNull(appointment);
        assertEquals(ENCODED_VALUE, appointment.getId());
        assertNotNull(appointment.getCreated());
        assertEquals(INTERNAL_ID_PREFIX + GENERATED_ID, appointment.getInternalId());
        assertEquals(ENCODED_VALUE, appointment.getAppointmentId());
        assertEquals("Company " + COMPANY_NUMBER, appointment.getCompanyName());
        assertEquals("active", appointment.getCompanyStatus());
        assertEquals(ENCODED_INTERNAL_ID, appointment.getOfficerId());
        assertEquals(COMPANY_NUMBER, appointment.getCompanyNumber());
        assertNotNull(appointment.getUpdated());

        assertEquals("British", appointment.getNationality());
        assertEquals("Director", appointment.getOccupation());
        assertTrue(appointment.isServiceAddressIsSameAsRegisteredOfficeAddress());
        assertEquals("Wales", appointment.getCountryOfResidence());
        assertNotNull(appointment.getUpdatedAt());
        assertEquals("Test", appointment.getForename());
        assertNotNull(appointment.getAppointedOn());
        assertEquals("director", appointment.getOfficerRole());
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
        final Address mockServiceAddress = new Address(
                "","","","","",""
        );

        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setJurisdiction(Jurisdiction.SCOTLAND);

        when(randomService.getNumber(INTERNAL_ID_LENGTH)).thenReturn(GENERATED_ID);
        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.addSaltAndEncode(INTERNAL_ID_PREFIX + GENERATED_ID, 8)).thenReturn(ENCODED_INTERNAL_ID);
        when(this.randomService.getEtag()).thenReturn(ETAG);
        when(this.addressService.getAddress(Jurisdiction.SCOTLAND)).thenReturn(mockServiceAddress);
        when(this.addressService.getCountryOfResidence(Jurisdiction.SCOTLAND)).thenReturn("Scotland");
        Appointment savedApt = new Appointment();
        when(this.appointmentsRepository.save(any())).thenReturn(savedApt);

        Appointment returnedApt = this.appointmentsService.create(spec);

        assertEquals(savedApt, returnedApt);

        ArgumentCaptor<Appointment> aptCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentsRepository).save(aptCaptor.capture());

        Appointment appointment = aptCaptor.getValue();
        assertNotNull(appointment);
        assertEquals(ENCODED_VALUE, appointment.getId());
        assertNotNull(appointment.getCreated());
        assertEquals(INTERNAL_ID_PREFIX + GENERATED_ID, appointment.getInternalId());
        assertEquals(ENCODED_VALUE, appointment.getAppointmentId());
        assertEquals("Company " + COMPANY_NUMBER, appointment.getCompanyName());
        assertEquals("active", appointment.getCompanyStatus());
        assertEquals(ENCODED_INTERNAL_ID, appointment.getOfficerId());
        assertEquals(COMPANY_NUMBER, appointment.getCompanyNumber());
        assertNotNull(appointment.getUpdated());

        assertEquals("British", appointment.getNationality());
        assertEquals("Director", appointment.getOccupation());
        assertTrue(appointment.isServiceAddressIsSameAsRegisteredOfficeAddress());
        assertEquals("Scotland", appointment.getCountryOfResidence());
        assertNotNull(appointment.getUpdatedAt());
        assertEquals("Test", appointment.getForename());
        assertNotNull(appointment.getAppointedOn());
        assertEquals("director", appointment.getOfficerRole());
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
    void delete() {
        Appointment apt = new Appointment();
        OfficerAppointment officerAppointment = new OfficerAppointment();
        final String officerId = "TEST";
        apt.setOfficerId(officerId);
        when(appointmentsRepository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.of(apt));
        when(officerRepository.findById(officerId)).thenReturn(Optional.of(officerAppointment));

        assertTrue(this.appointmentsService.delete(COMPANY_NUMBER));
        verify(officerRepository).delete(officerAppointment);
        verify(appointmentsRepository).delete(apt);
    }

    @Test
    void deleteNoAppointmentDataException() {
        when(appointmentsRepository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.empty());
        
        assertFalse(this.appointmentsService.delete(COMPANY_NUMBER));
        verify(officerRepository, never()).delete(any());
        verify(appointmentsRepository, never()).delete(any());
    }

    @Test
    void deleteNoOfficerDataException() {
        Appointment apt = new Appointment();
        final String officerId = "TEST";
        apt.setOfficerId(officerId);
        when(appointmentsRepository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.of(apt));
        when(officerRepository.findById(officerId)).thenReturn(Optional.empty());

        assertTrue(this.appointmentsService.delete(COMPANY_NUMBER));
        verify(officerRepository, never()).delete(any());
        verify(appointmentsRepository).delete(apt);
    }

}
