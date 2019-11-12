package uk.gov.companieshouse.api.testdata.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.service.impl.AppointmentsServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AppointmentsServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String ENCODED_VALUE = "ENCODED";

    @Mock
    private AppointmentsRepository repository;
    @Mock
    private RandomService randomService;

    @InjectMocks
    private AppointmentsServiceImpl appointmentsService;

    @Test
    void create() throws DataException {
        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        Appointment savedApt = new Appointment();
        when(this.repository.save(any())).thenReturn(savedApt);
        
        Appointment returnedApt = this.appointmentsService.create(COMPANY_NUMBER);
        
        assertEquals(savedApt, returnedApt);
        
        ArgumentCaptor<Appointment> aptCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(repository).save(aptCaptor.capture());

        Appointment appointment = aptCaptor.getValue();
        assertNotNull(appointment);
        assertEquals(COMPANY_NUMBER, appointment.getCompanyNumber());
        assertEquals(ENCODED_VALUE, appointment.getId());
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
