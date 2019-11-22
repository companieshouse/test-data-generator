package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
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
    private AppointmentsRepository repository;
    @Mock
    private RandomService randomService;

    @InjectMocks
    private AppointmentsServiceImpl appointmentsService;

    @Test
    void create() throws DataException {
        final Address mockServiceAddress = new Address();

        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        
        when(randomService.getNumber(INTERNAL_ID_LENGTH)).thenReturn(GENERATED_ID);
        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.addSaltAndEncode(INTERNAL_ID_PREFIX + GENERATED_ID, 8)).thenReturn(ENCODED_INTERNAL_ID);
        when(this.randomService.getEtag()).thenReturn(ETAG);
        when(this.addressService.getAddress(Jurisdiction.ENGLAND_WALES)).thenReturn(mockServiceAddress);
        Appointment savedApt = new Appointment();
        when(this.repository.save(any())).thenReturn(savedApt);
        
        Appointment returnedApt = this.appointmentsService.create(spec);
        
        assertEquals(savedApt, returnedApt);
        
        ArgumentCaptor<Appointment> aptCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(repository).save(aptCaptor.capture());

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
    void createScottish() throws DataException {
        final Address mockServiceAddress = new Address();

        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setJurisdiction(Jurisdiction.SCOTLAND);

        when(randomService.getNumber(INTERNAL_ID_LENGTH)).thenReturn(GENERATED_ID);
        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.addSaltAndEncode(INTERNAL_ID_PREFIX + GENERATED_ID, 8)).thenReturn(ENCODED_INTERNAL_ID);
        when(this.randomService.getEtag()).thenReturn(ETAG);
        when(this.addressService.getAddress(Jurisdiction.SCOTLAND)).thenReturn(mockServiceAddress);
        Appointment savedApt = new Appointment();
        when(this.repository.save(any())).thenReturn(savedApt);

        Appointment returnedApt = this.appointmentsService.create(spec);

        assertEquals(savedApt, returnedApt);

        ArgumentCaptor<Appointment> aptCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(repository).save(aptCaptor.capture());

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
    void createMongoException() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(repository.save(any())).thenThrow(MongoException.class);

        DataException exception = assertThrows(DataException.class, () ->
                this.appointmentsService.create(spec)
        );
        assertEquals("Failed to save appointment", exception.getMessage());
    }
    
    @Test
    void delete() throws Exception {
        Appointment apt = new Appointment();
        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(apt);

        this.appointmentsService.delete(COMPANY_NUMBER);
        verify(repository).delete(apt);
    }

    @Test
    void deleteNoDataException() {
        Appointment apt = null;
        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(apt);
        
        NoDataFoundException exception = assertThrows(NoDataFoundException.class, () ->
                this.appointmentsService.delete(COMPANY_NUMBER)
        );
        assertEquals("appointment data not found", exception.getMessage());
    }

    @Test
    void deleteMongoException() {
        Appointment apt = new Appointment();
        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(apt);
        doThrow(MongoException.class).when(repository).delete(apt);
        
        DataException exception = assertThrows(DataException.class, () ->
                this.appointmentsService.delete(COMPANY_NUMBER)
        );
        assertEquals("Failed to delete appointment", exception.getMessage());
    }

}
