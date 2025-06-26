package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Disqualifications;
import uk.gov.companieshouse.api.testdata.model.rest.DisqualificationsSpec;
import uk.gov.companieshouse.api.testdata.repository.DisqualificationsRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class DisqualificationsServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String ID = "D12345678";
    private static final Date DATE_OF_BIRTH = Date.from(Instant.now());

    @Mock
    private DisqualificationsRepository repository;

    @Mock
    private RandomService randomService;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private DisqualificationsServiceImpl service;

    @Test
    void createDisqualificationSuccess() throws DataException {
        DisqualificationsSpec spec = new DisqualificationsSpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setIsCorporateOfficer(false);

        Disqualifications savedEntity = new Disqualifications();
        savedEntity.setId(ID);
        savedEntity.setDateOfBirth(DATE_OF_BIRTH);
        savedEntity.setLinksSelf("/disqualified-officers/natural/" + ID);

        when(repository.save(any(Disqualifications.class))).thenReturn(savedEntity);

        Disqualifications result = service.create(spec);

        assertNotNull(result);
        assertEquals(ID, result.getId());
        assertEquals(DATE_OF_BIRTH, result.getDateOfBirth());
        assertTrue(result.getLinksSelf().contains("/natural/"));

        verify(repository).save(any(Disqualifications.class));
    }

    @Test
    void createDisqualificationCorporateOfficer() throws DataException {
        DisqualificationsSpec spec = new DisqualificationsSpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setIsCorporateOfficer(true);

        Disqualifications savedEntity = new Disqualifications();
        savedEntity.setId(ID);
        savedEntity.setLinksSelf("/disqualified-officers/corporate/" + ID);

        when(repository.save(any(Disqualifications.class))).thenReturn(savedEntity);

        Disqualifications result = service.create(spec);

        assertNotNull(result);
        assertTrue(result.getLinksSelf().contains("/corporate/"));
    }

    @Test
    void createDisqualificationNullSpec() {
        assertThrows(IllegalArgumentException.class, () -> service.create(null));
    }

    @Test
    void deleteDisqualificationSuccess() {
        Disqualifications disq = new Disqualifications();
        disq.setId(ID);
        when(repository.findById(ID)).thenReturn(Optional.of(disq));

        boolean result = service.delete(ID);

        assertTrue(result);
        verify(repository).delete(disq);
    }

    @Test
    void deleteDisqualificationNotFound() {
        when(repository.findById(ID)).thenReturn(Optional.empty());

        boolean result = service.delete(ID);

        assertFalse(result);
        verify(repository, never()).deleteById(ID);
    }

    @Test
    void deleteDisqualificationException() {
        Disqualifications disq = new Disqualifications();
        disq.setId(ID);
        when(repository.findById(ID)).thenReturn(Optional.of(disq));
        doThrow(new RuntimeException("DB error")).when(repository).delete(disq);

        assertThrows(RuntimeException.class, () -> service.delete(ID));
    }
}