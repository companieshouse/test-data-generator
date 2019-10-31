package uk.gov.companieshouse.api.testdata.service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.officer.Officer;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;
import uk.gov.companieshouse.api.testdata.service.impl.OfficerListServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class OfficerListServiceImplTest {

    @Mock
    private ITestDataHelperService testDataHelperService;

    @Mock
    private OfficerRepository officerRepository;

    private IOfficerListService IOfficerListService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.IOfficerListService = new OfficerListServiceImpl(testDataHelperService, officerRepository);
    }

    @Test
    void testCreateNoException() throws DataException {
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        Officer createdOfficer = this.IOfficerListService.create("12345678");

        assertEquals("test_id", createdOfficer.getId());
        assertEquals("12345678", createdOfficer.getCompanyNumber());
        assertEquals(new Integer(1), createdOfficer.getActiveCount());
        assertEquals(new Integer(0), createdOfficer.getInactiveCount());
        assertEquals(new Integer(1), createdOfficer.getResignedCount());
        assertEquals(2, createdOfficer.getOfficerItems().size());
    }

    @Test
    void testCreateDuplicateKeyException() {
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        when(officerRepository.save(any())).thenThrow(DuplicateKeyException.class);

        assertThrows(DataException.class, () -> {
            this.IOfficerListService.create("12345678");
        });
    }

    @Test
    void testCreateMongoExceptionException() {
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        when(officerRepository.save(any())).thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> {
            this.IOfficerListService.create("12345678");
        });
    }

    @Test
    void testDeleteMongoException() {
        when(officerRepository.findByCompanyNumber("12345678"))
                .thenReturn(new Officer());
        doThrow(MongoException.class).when(officerRepository).delete(any());
        assertThrows(DataException.class, () -> {
            this.IOfficerListService.delete("12345678");
        });
    }
}