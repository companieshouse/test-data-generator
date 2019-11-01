package uk.gov.companieshouse.api.testdata.service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.psc.PersonsWithSignificantControl;
import uk.gov.companieshouse.api.testdata.repository.PersonsWithSignificantControlRepository;
import uk.gov.companieshouse.api.testdata.service.impl.PSCServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PSCServiceImplTest {

    @Mock
    private TestDataHelperService testDataHelperService;

    @Mock
    private PersonsWithSignificantControlRepository personsWithSignificantControlRepository;

    @InjectMocks
    private PSCServiceImpl pscService;

    @Test
    void createSuccess() throws Exception {
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        PersonsWithSignificantControl returnedPsc = this.pscService.create("12345678");

        assertEquals("test_id", returnedPsc.getId());
        assertEquals("12345678", returnedPsc.getCompanyNumber());
        assertEquals(Integer.valueOf(1), returnedPsc.getActiveCount());
        assertEquals(Integer.valueOf(0), returnedPsc.getCeasedCount());
        assertEquals(1, returnedPsc.getItems().size());
    }

    @Test
    void createDuplicateKeyException() {
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        when(personsWithSignificantControlRepository.save(any())).thenThrow(DuplicateKeyException.class);

        assertThrows(DataException.class, () -> {
            this.pscService.create("12345678");
        });
    }

    @Test
    void createMongoExceptionException() {
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        when(personsWithSignificantControlRepository.save(any())).thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> {
            this.pscService.create("12345678");
        });
    }

    @Test
    void deleteMongoException() {
        when(personsWithSignificantControlRepository.findByCompanyNumber("12345678"))
                .thenReturn(new PersonsWithSignificantControl());
        doThrow(MongoException.class).when(personsWithSignificantControlRepository).delete(any());
        assertThrows(DataException.class, () -> {
            this.pscService.delete("12345678");
        });
    }
}