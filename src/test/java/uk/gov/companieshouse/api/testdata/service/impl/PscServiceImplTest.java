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
import org.mockito.junit.jupiter.MockitoExtension;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.PersonsWithSignificantControl;
import uk.gov.companieshouse.api.testdata.model.entity.PersonsWithSignificantControlItem;
import uk.gov.companieshouse.api.testdata.repository.PersonsWithSignificantControlRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class PscServiceImplTest {

    private static final String TEST_ID = "test_id";
    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private RandomService randomService;

    @Mock
    private PersonsWithSignificantControlRepository repository;

    @InjectMocks
    private PscServiceImpl pscService;

    @Test
    void createSuccess() throws DataException {
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(TEST_ID);

        PersonsWithSignificantControl savedPsc = new PersonsWithSignificantControl();
        when(repository.save(any())).thenReturn(savedPsc);

        PersonsWithSignificantControl returnedPsc = this.pscService.create(COMPANY_NUMBER);

        assertEquals(savedPsc, returnedPsc);

        ArgumentCaptor<PersonsWithSignificantControl> pscCaptor = ArgumentCaptor
                .forClass(PersonsWithSignificantControl.class);
        verify(repository).save(pscCaptor.capture());
        PersonsWithSignificantControl psc = pscCaptor.getValue();
        assertEquals(TEST_ID, psc.getId());
        assertEquals(COMPANY_NUMBER, psc.getCompanyNumber());
        assertEquals(Integer.valueOf(1), psc.getActiveCount());
        assertEquals(Integer.valueOf(0), psc.getCeasedCount());
        assertEquals(1, psc.getItems().size());

        PersonsWithSignificantControlItem item = psc.getItems().get(0);
        assertEquals("premises", item.getAddress().getPremises());
        assertEquals("Line1", item.getAddress().getAddressLine1());
        assertEquals("Line2", item.getAddress().getAddressLine2());
        assertEquals("Locality", item.getAddress().getLocality());
        assertEquals("Country", item.getAddress().getCountry());
        assertEquals("postcode", item.getAddress().getPostalCode());
        assertNull(item.getDateOfBirth().getDay());
        assertEquals(1, item.getDateOfBirth().getMonth().intValue());
        assertEquals(1992, item.getDateOfBirth().getYear().intValue());
        assertEquals("full name", item.getName());
        assertNotNull(item.getNotifiedOn());
        assertEquals(1, item.getNaturesOfControl().length);
        assertEquals("significant-influence-or-control", item.getNaturesOfControl()[0]);
        assertEquals("/company/" + COMPANY_NUMBER + "/persons-with-significant-control/legal-person/" + TEST_ID,
                item.getLinks().getSelf());
    }

    @Test
    void createDuplicateKeyException() {
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(TEST_ID);
        when(repository.save(any())).thenThrow(DuplicateKeyException.class);

        DataException exception = assertThrows(DataException.class, () ->
            this.pscService.create(COMPANY_NUMBER)
        );
        assertEquals("duplicate key", exception.getMessage());
    }

    @Test
    void createMongoExceptionException() {
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(TEST_ID);
        when(repository.save(any())).thenThrow(MongoException.class);

        DataException exception = assertThrows(DataException.class, () ->
            this.pscService.create(COMPANY_NUMBER)
        );
        assertEquals("failed to insert", exception.getMessage());
    }
    
    @Test
    void delete() throws Exception {
        PersonsWithSignificantControl psc = new PersonsWithSignificantControl();
        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(psc);
        
        pscService.delete(COMPANY_NUMBER);
        
        verify(repository).delete(psc);
    }

    @Test
    void deleteNoCompany() {
        PersonsWithSignificantControl psc = null;
        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(psc);
        NoDataFoundException exception = assertThrows(NoDataFoundException.class, () ->
            this.pscService.delete(COMPANY_NUMBER)
        );
        assertEquals("psc data not found", exception.getMessage());
    }

    @Test
    void deleteMongoException() {
        PersonsWithSignificantControl psc = new PersonsWithSignificantControl();
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(psc);
        doThrow(MongoException.class).when(repository).delete(psc);
        
        DataException exception = assertThrows(DataException.class, () ->
            this.pscService.delete(COMPANY_NUMBER)
        );
        assertEquals("failed to delete", exception.getMessage());
    }
}