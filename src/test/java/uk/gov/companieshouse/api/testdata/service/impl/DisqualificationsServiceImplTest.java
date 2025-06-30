package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Disqualifications;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.DisqualificationsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
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
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        DisqualificationsSpec disqSpec = new DisqualificationsSpec();
        disqSpec.setCorporateOfficer(false);
        spec.setDisqualifiedOfficers(List.of(disqSpec));

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
    void createMultipleDisqualifications() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        DisqualificationsSpec disqSpec1 = new DisqualificationsSpec();
        disqSpec1.setCorporateOfficer(false);
        DisqualificationsSpec disqSpec2 = new DisqualificationsSpec();
        disqSpec2.setCorporateOfficer(true);
        spec.setDisqualifiedOfficers(List.of(disqSpec1, disqSpec2));

        Disqualifications savedEntity1 = new Disqualifications();
        savedEntity1.setId("D1");
        savedEntity1.setLinksSelf("/disqualified-officers/natural/D1");
        Disqualifications savedEntity2 = new Disqualifications();
        savedEntity2.setId("D2");
        savedEntity2.setLinksSelf("/disqualified-officers/corporate/D2");

        when(repository.save(any(Disqualifications.class)))
                .thenReturn(savedEntity1)
                .thenReturn(savedEntity2);

        Disqualifications result = service.create(spec);

        assertNotNull(result);
        verify(repository, times(2)).save(any(Disqualifications.class));
    }

    @Test
    void createDisqualificationCorporateOfficer() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        DisqualificationsSpec disqSpec = new DisqualificationsSpec();
        disqSpec.setCorporateOfficer(true);
        spec.setDisqualifiedOfficers(List.of(disqSpec));

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
    void createDisqualificationNoSpecCreatesDefault() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        Disqualifications savedEntity = new Disqualifications();
        savedEntity.setId(ID);

        when(repository.save(any(Disqualifications.class))).thenReturn(savedEntity);

        Disqualifications result = service.create(spec);

        assertNotNull(result);
        verify(repository).save(any(Disqualifications.class));
    }

    @Test
    void getDisqualificationsSuccess() {
        Disqualifications disq = new Disqualifications();
        disq.setId(ID);
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(Optional.of(List.of(disq)));

        List<Disqualifications> result = service.getDisqualifications(COMPANY_NUMBER);

        assertEquals(1, result.size());
        assertEquals(ID, result.get(0).getId());
    }

    @Test
    void getDisqualificationsEmpty() {
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(Optional.empty());

        List<Disqualifications> result = service.getDisqualifications(COMPANY_NUMBER);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteDisqualificationSuccess() {
        Disqualifications disq = new Disqualifications();
        disq.setId(COMPANY_NUMBER);
        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.of(List.of(disq)));

        boolean result = service.delete(COMPANY_NUMBER);

        assertTrue(result);
        verify(repository).delete(disq);
    }

    @Test
    void deleteDisqualificationNotFound() {
        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.empty());

        boolean result = service.delete(COMPANY_NUMBER);

        assertFalse(result);
        verify(repository, never()).delete(any());
    }
}