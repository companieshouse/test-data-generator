package uk.gov.companieshouse.api.testdata.service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.officer.Officer;
import uk.gov.companieshouse.api.testdata.repository.officer.OfficerRepository;
import uk.gov.companieshouse.api.testdata.service.impl.OfficerListServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfficerListServiceImplTest {

    @Mock
    private TestDataHelperService testDataHelperService;

    @Mock
    private OfficerRepository officerRepository;

    @InjectMocks
    private OfficerListServiceImpl officerListService;

    @Test
    void createNoException() throws DataException {
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        Officer createdOfficer = this.officerListService.create("12345678");

        assertEquals("test_id", createdOfficer.getId());
        assertEquals("12345678", createdOfficer.getCompanyNumber());
        assertEquals(Integer.valueOf(1), createdOfficer.getActiveCount());
        assertEquals(Integer.valueOf(0), createdOfficer.getInactiveCount());
        assertEquals(Integer.valueOf(1), createdOfficer.getResignedCount());
        assertEquals(2, createdOfficer.getOfficerItems().size());
    }

    @Test
    void createDuplicateKeyException() {
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        when(officerRepository.save(any())).thenThrow(DuplicateKeyException.class);

        assertThrows(DataException.class, () -> {
            this.officerListService.create("12345678");
        });
    }

    @Test
    void createMongoExceptionException() {
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        when(officerRepository.save(any())).thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> {
            this.officerListService.create("12345678");
        });
    }

    @Test
    void deleteMongoException() {
        when(officerRepository.findByCompanyNumber("12345678"))
                .thenReturn(new Officer());
        doThrow(MongoException.class).when(officerRepository).delete(any());
        assertThrows(DataException.class, () -> {
            this.officerListService.delete("12345678");
        });
    }
}