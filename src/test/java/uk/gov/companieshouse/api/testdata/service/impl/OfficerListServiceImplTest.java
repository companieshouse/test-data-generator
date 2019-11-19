package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Officer;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerItem;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class OfficerListServiceImplTest {

    private static final String TEST_ID = "test_id";
    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private RandomService randomService;

    @Mock
    private OfficerRepository repository;

    @InjectMocks
    private OfficerListServiceImpl officerListService;

    @Test
    void create() throws DataException {
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(TEST_ID);
        Officer savedOfficer = new Officer();
        when(repository.save(Mockito.any())).thenReturn(savedOfficer);
        
        Officer returnedOfficer = this.officerListService.create(COMPANY_NUMBER);
        
        assertEquals(savedOfficer, returnedOfficer);
        
        ArgumentCaptor<Officer> officerCaptor = ArgumentCaptor.forClass(Officer.class);
        verify(repository).save(officerCaptor.capture());
        Officer officer = officerCaptor.getValue();
        assertEquals(TEST_ID, officer.getId());
        assertEquals(COMPANY_NUMBER, officer.getCompanyNumber());
        assertEquals(1, officer.getActiveCount().intValue());
        assertEquals(0, officer.getInactiveCount().intValue());
        assertEquals(1, officer.getResignedCount().intValue());
        assertEquals(2, officer.getOfficerItems().size());
        
        assertOfficerItem(officer.getOfficerItems().get(0), false);
        assertOfficerItem(officer.getOfficerItems().get(1), true);
    }

    private void assertOfficerItem(OfficerItem item, boolean resignedOfficer) {
        assertEquals("full name", item.getName());
        assertEquals("director", item.getOfficerRole());
        assertEquals("10 Test Street", item.getAddress().getAddressLine1());
        assertEquals("line 2", item.getAddress().getAddressLine2());
        assertEquals("locality", item.getAddress().getLocality());
        assertEquals("country", item.getAddress().getCountry());
        assertEquals("postcode", item.getAddress().getPostalCode());
        assertEquals(1, item.getDateOfBirth().getDay().intValue());
        assertEquals(1, item.getDateOfBirth().getMonth().intValue());
        assertEquals(1950, item.getDateOfBirth().getYear().intValue());
        assertEquals("/officers/"+ COMPANY_NUMBER, item.getLinks().getSelf());
        assertEquals("/company/"+ COMPANY_NUMBER + "/officers", item.getLinks().getOfficers());
        assertNotNull(item.getAppointedOn());
        if (resignedOfficer) {
            assertNotNull(item.getResignedOn());
        } else {
            assertNull(item.getResignedOn());
        }
    }

    @Test
    void createDuplicateKeyException() {
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(TEST_ID);
        when(repository.save(any())).thenThrow(DuplicateKeyException.class);

        DataException exception = assertThrows(DataException.class, () ->
            this.officerListService.create(COMPANY_NUMBER)
        );
        assertEquals("duplicate key", exception.getMessage());
    }

    @Test
    void createMongoExceptionException() {
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(TEST_ID);
        when(repository.save(any())).thenThrow(MongoException.class);

        DataException exception = assertThrows(DataException.class, () ->
            this.officerListService.create(COMPANY_NUMBER)
        );
        assertEquals("failed to insert", exception.getMessage());
    }

    @Test
    void delete() throws Exception {
        Officer officer = new Officer();
        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(officer);

        officerListService.delete(COMPANY_NUMBER);

        verify(repository).delete(officer);
    };

    @Test
    void deleteNoOfficer() {
        Officer officer = null;
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(officer);
        NoDataFoundException exception = assertThrows(NoDataFoundException.class, () ->
            this.officerListService.delete(COMPANY_NUMBER)
        );
        assertEquals("officer data not found", exception.getMessage());
    }

    @Test
    void deleteMongoException() {
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(new Officer());
        doThrow(MongoException.class).when(repository).delete(any());
        DataException exception = assertThrows(DataException.class, () ->
            this.officerListService.delete(COMPANY_NUMBER)
        );
        assertEquals("failed to delete", exception.getMessage());
    }
}