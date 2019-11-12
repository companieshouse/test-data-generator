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
import uk.gov.companieshouse.api.testdata.model.entity.PersonsWithSignificantControl;
import uk.gov.companieshouse.api.testdata.repository.PersonsWithSignificantControlRepository;
import uk.gov.companieshouse.api.testdata.service.impl.PscServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PscServiceImplTest {

    private static final String TEST_ID = "test_id";
    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private RandomService randomService;

    @Mock
    private PersonsWithSignificantControlRepository personsWithSignificantControlRepository;

    @InjectMocks
    private PscServiceImpl pscService;

    @Test
    void createSuccess() throws DataException {
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(TEST_ID);
        PersonsWithSignificantControl returnedPsc = this.pscService.create(COMPANY_NUMBER);

        assertEquals(TEST_ID, returnedPsc.getId());
        assertEquals(COMPANY_NUMBER, returnedPsc.getCompanyNumber());
        assertEquals(Integer.valueOf(1), returnedPsc.getActiveCount());
        assertEquals(Integer.valueOf(0), returnedPsc.getCeasedCount());
        assertEquals(1, returnedPsc.getItems().size());
    }

    @Test
    void createDuplicateKeyException() {
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(TEST_ID);
        when(personsWithSignificantControlRepository.save(any())).thenThrow(DuplicateKeyException.class);

        assertThrows(DataException.class, () ->
            this.pscService.create(COMPANY_NUMBER)
        );
    }

    @Test
    void createMongoExceptionException() {
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(TEST_ID);
        when(personsWithSignificantControlRepository.save(any())).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
            this.pscService.create(COMPANY_NUMBER)
        );
    }

    @Test
    void deleteNoCompany() {
        when(personsWithSignificantControlRepository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(null);
        assertThrows(NoDataFoundException.class, () ->
            this.pscService.delete(COMPANY_NUMBER)
        );
    }

    @Test
    void deleteMongoException() {
        when(personsWithSignificantControlRepository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(new PersonsWithSignificantControl());
        doThrow(MongoException.class).when(personsWithSignificantControlRepository).delete(any());
        assertThrows(DataException.class, () ->
            this.pscService.delete(COMPANY_NUMBER)
        );
    }
}