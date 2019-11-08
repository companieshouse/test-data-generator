package uk.gov.companieshouse.api.testdata.service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.service.impl.AppointmentsServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

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
    void createNoException() throws DataException {
        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        Appointment createdAppointment = this.appointmentsService.create(COMPANY_NUMBER);

        assertNotNull(createdAppointment);
        assertEquals(COMPANY_NUMBER, createdAppointment.getCompanyNumber());
        assertEquals(ENCODED_VALUE, createdAppointment.getId());
    }

    @Test
    void createDuplicateKeyException() {
        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(repository.save(any())).thenThrow(DuplicateKeyException.class);

        assertThrows(DataException.class, () ->
                this.appointmentsService.create(COMPANY_NUMBER)
        );
    }

    @Test
    void createMongoExceptionException() {
        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(repository.save(any())).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
                this.appointmentsService.create(COMPANY_NUMBER)
        );
    }

    @Test
    void deleteNoDataException() {
        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(null);
        assertThrows(NoDataFoundException.class, () ->
                this.appointmentsService.delete(COMPANY_NUMBER)
        );
    }

    @Test
    void deleteMongoException() {
        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(new Appointment());
        doThrow(MongoException.class).when(repository).delete(any());
        assertThrows(DataException.class, () ->
                this.appointmentsService.delete(COMPANY_NUMBER)
        );
    }

}
