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
import uk.gov.companieshouse.api.testdata.model.entity.Officer;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;
import uk.gov.companieshouse.api.testdata.service.impl.OfficerListServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfficerListServiceImplTest {

    private static final String TEST_ID = "test_id";
    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private RandomService randomService;

    @Mock
    private OfficerRepository officerRepository;

    @InjectMocks
    private OfficerListServiceImpl officerListService;

    @Test
    void createNoException() throws DataException {
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(TEST_ID);
        Officer createdOfficer = this.officerListService.create(COMPANY_NUMBER);

        assertEquals(TEST_ID, createdOfficer.getId());
        assertEquals(COMPANY_NUMBER, createdOfficer.getCompanyNumber());
        assertEquals(Integer.valueOf(1), createdOfficer.getActiveCount());
        assertEquals(Integer.valueOf(0), createdOfficer.getInactiveCount());
        assertEquals(Integer.valueOf(1), createdOfficer.getResignedCount());
        assertEquals(2, createdOfficer.getOfficerItems().size());
    }

    @Test
    void createDuplicateKeyException() {
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(TEST_ID);
        when(officerRepository.save(any())).thenThrow(DuplicateKeyException.class);

        assertThrows(DataException.class, () ->
            this.officerListService.create(COMPANY_NUMBER)
        );
    }

    @Test
    void createMongoExceptionException() {
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(TEST_ID);
        when(officerRepository.save(any())).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
            this.officerListService.create(COMPANY_NUMBER)
        );
    }

    @Test
    void deleteNoCompany() {
        when(officerRepository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(null);
        assertThrows(NoDataFoundException.class, () ->
            this.officerListService.delete(COMPANY_NUMBER)
        );
    }

    @Test
    void deleteMongoException() {
        when(officerRepository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(new Officer());
        doThrow(MongoException.class).when(officerRepository).delete(any());
        assertThrows(DataException.class, () ->
            this.officerListService.delete(COMPANY_NUMBER)
        );
    }
}