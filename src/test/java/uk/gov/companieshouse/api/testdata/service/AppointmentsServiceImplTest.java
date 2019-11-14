package uk.gov.companieshouse.api.testdata.service;

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

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.service.impl.AppointmentsServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AppointmentsServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String ENCODED_VALUE = "ENCODED";
    private static final Long GENERATED_INTERNAL_ID = 123456789L;
    private static final String ENCODED_INTERNAL_ID = "ENCODED 2";
    private static final String ETAG = "ETAG";

    @Mock
    private AppointmentsRepository repository;
    @Mock
    private RandomService randomService;

    @InjectMocks
    private AppointmentsServiceImpl appointmentsService;

    @Test
    void create() throws DataException {
        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getNumber(9)).thenReturn(GENERATED_INTERNAL_ID);
        when(this.randomService.addSaltAndEncode("8" + GENERATED_INTERNAL_ID, 8)).thenReturn(ENCODED_INTERNAL_ID);
        when(this.randomService.getEtag()).thenReturn(ETAG);
        Appointment savedApt = new Appointment();
        when(this.repository.save(any())).thenReturn(savedApt);
        
        Appointment returnedApt = this.appointmentsService.create(COMPANY_NUMBER);
        
        assertEquals(savedApt, returnedApt);
        
        ArgumentCaptor<Appointment> aptCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(repository).save(aptCaptor.capture());

        Appointment appointment = aptCaptor.getValue();
        assertNotNull(appointment);
        assertEquals(ENCODED_VALUE, appointment.getId());
        assertNotNull(appointment.getCreated());
        assertEquals("8" + GENERATED_INTERNAL_ID, appointment.getInternalId());
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

        Address serviceAddress = appointment.getServiceAddress();
        assertEquals("United Kingdom", serviceAddress.getCountry());
        assertEquals("CF14 3UZ", serviceAddress.getPostalCode());
        assertEquals("Companies House", serviceAddress.getAddressLine1());
        assertEquals("Crownway", serviceAddress.getAddressLine2());
        assertEquals("Cardiff", serviceAddress.getLocality());

        assertEquals(COMPANY_NUMBER, appointment.getDataCompanyNumber());

        Links links = appointment.getLinks();
        assertEquals("/company/" + COMPANY_NUMBER + "/appointments/" + ENCODED_INTERNAL_ID, links.getSelf());
        assertEquals("/officers/" + ENCODED_INTERNAL_ID, links.getOfficerSelf());
        assertEquals("/officers/" + ENCODED_INTERNAL_ID + "/appointments", links.getAppointments());

        assertEquals("DIRECTOR", appointment.getSurname());
        assertNotNull(appointment.getDateOfBirth());
    }

    @Test
    void createDuplicateKeyException() {
        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(repository.save(any())).thenThrow(DuplicateKeyException.class);

        DataException exception = assertThrows(DataException.class, () ->
                this.appointmentsService.create(COMPANY_NUMBER)
        );
        assertEquals("duplicate key", exception.getMessage());
    }

    @Test
    void createMongoExceptionException() {
        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(repository.save(any())).thenThrow(MongoException.class);

        DataException exception = assertThrows(DataException.class, () ->
                this.appointmentsService.create(COMPANY_NUMBER)
        );
        assertEquals("failed to insert", exception.getMessage());
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
        assertEquals("failed to delete", exception.getMessage());
    }

}
