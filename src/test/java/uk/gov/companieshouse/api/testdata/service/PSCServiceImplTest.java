package uk.gov.companieshouse.api.testdata.service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.psc.PersonsWithSignificantControl;
import uk.gov.companieshouse.api.testdata.repository.psc.PersonsWithSignificantControlRepository;
import uk.gov.companieshouse.api.testdata.service.impl.PSCServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class PSCServiceImplTest {

    private IPSCService IPSCService;

    @Mock
    private ITestDataHelperService testDataHelperService;

    @Mock
    private PersonsWithSignificantControlRepository personsWithSignificantControlRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.IPSCService = new PSCServiceImpl(testDataHelperService, personsWithSignificantControlRepository);
    }

    @Test
    void testCreateSuccess() throws Exception{
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        PersonsWithSignificantControl returnedPsc = this.IPSCService.create("12345678");

        assertEquals("test_id", returnedPsc.getId());
        assertEquals("12345678", returnedPsc.getCompanyNumber());
        assertEquals(new Integer(1), returnedPsc.getActiveCount());
        assertEquals(new Integer(0), returnedPsc.getCeasedCount());
        assertEquals(1, returnedPsc.getItems().size());
    }

    @Test
    void testCreateDuplicateKeyException() {
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        when(personsWithSignificantControlRepository.save(any())).thenThrow(DuplicateKeyException.class);

        assertThrows(DataException.class, () -> {
            this.IPSCService.create("12345678");
        });
    }

    @Test
    void testCreateMongoExceptionException() {
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        when(personsWithSignificantControlRepository.save(any())).thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> {
            this.IPSCService.create("12345678");
        });
    }

    @Test
    void testDeleteMongoException() {
        when(personsWithSignificantControlRepository.findByCompanyNumber("12345678"))
                .thenReturn(new PersonsWithSignificantControl());
        doThrow(MongoException.class).when(personsWithSignificantControlRepository).delete(any());
        assertThrows(DataException.class, () -> {
            this.IPSCService.delete("12345678");
        });
    }
}