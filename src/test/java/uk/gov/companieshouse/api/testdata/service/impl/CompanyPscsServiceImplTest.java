package uk.gov.companieshouse.api.testdata.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscsRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyPscsServiceImplTest {

    private static final int ID_LENGTH = 10;
    private static final int SALT_LENGTH = 8;
    private static final String COMPANY_NUMBER = "12345678";
    private static final String ENCODED_VALUE = "ENCODED";
    private static final String ETAG = "ETAG";

    @Mock
    private CompanyPscsRepository repository;
    @Mock
    private RandomService randomService;

    @InjectMocks
    private CompanyPscsServiceImpl companyPscsService;

    @Test
    void create() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        when(this.randomService.getString(30)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH)).thenReturn(ENCODED_VALUE);

        when(this.randomService.getEtag()).thenReturn(ETAG);
        CompanyPscs savedPsc = new CompanyPscs();
        when(this.repository.save(any())).thenReturn(savedPsc);

        CompanyPscs returnedPsc = this.companyPscsService.create(spec);

        assertEquals(savedPsc, returnedPsc);

        ArgumentCaptor<CompanyPscs> pscCaptor = ArgumentCaptor.forClass(CompanyPscs.class);
        verify(repository).save(pscCaptor.capture());

        CompanyPscs companyPsc = pscCaptor.getValue();
        assertNotNull(companyPsc);
        assertEquals(ENCODED_VALUE, companyPsc.getId());
        assertNotNull(companyPsc.getCreatedAt());
        assertNotNull(companyPsc.getUpdatedAt());
        assertEquals(COMPANY_NUMBER, companyPsc.getCompanyNumber());

        assertEquals("voting-rights-75-to-100-percent-as-trust", companyPsc.getNaturesOfControl().get(0));
        assertEquals("Mrs forename middle individual-person-with-significant-control", companyPsc.getName());
        assertEquals("British", companyPsc.getNationality());
        assertNotNull(companyPsc.getDateOfBirth());
        assertEquals("individual-person-with-significant-control", companyPsc.getKind());

        assertEquals("CF14 3UZ", companyPsc.getPostalCode());
        assertEquals("1", companyPsc.getPremises());
        assertEquals("Cardiff",companyPsc.getLocality());
        assertEquals("Wales",companyPsc.getCountry());
        assertEquals("34 Silver Street", companyPsc.getAddressLine1());
        assertEquals("Wales", companyPsc.getCountryOfResidence());

        Links links = companyPsc.getSelf();
        assertEquals("/company/" + COMPANY_NUMBER + "/persons-with-significant-control/individual/" + ENCODED_VALUE,
                links.getSelf());

        assertNotNull(companyPsc.getNotifiedOn());
        assertEquals(ETAG, companyPsc.getEtag());
        assertEquals(ENCODED_VALUE, companyPsc.getPscId());
        assertEquals(ENCODED_VALUE, companyPsc.getNotificationId());
    }

    @Test
    void createWhenThereIsAlreadyASinglePsc() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        when(this.randomService.getString(30)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH)).thenReturn(ENCODED_VALUE);

        when(this.randomService.getEtag()).thenReturn(ETAG);
        CompanyPscs savedPsc = new CompanyPscs();
        when(this.repository.save(any())).thenReturn(savedPsc);

        when(repository.count()).thenReturn(1L);
        CompanyPscs returnedPsc = this.companyPscsService.create(spec);

        assertEquals(savedPsc, returnedPsc);

        ArgumentCaptor<CompanyPscs> pscCaptor = ArgumentCaptor.forClass(CompanyPscs.class);
        verify(repository).save(pscCaptor.capture());

        CompanyPscs companyPsc = pscCaptor.getValue();
        assertNotNull(companyPsc);


        assertEquals("Mrs forename middle legal-person-person-with-significant-control", companyPsc.getName());
        assertEquals("legal-person-person-with-significant-control", companyPsc.getKind());

        Links links = companyPsc.getSelf();
        assertEquals("/company/" + COMPANY_NUMBER + "/persons-with-significant-control/legal-person/" + ENCODED_VALUE,
                links.getSelf());
    }

    @Test
    void createWhenThereAreAlreadyTwoPscs() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        when(this.randomService.getString(30)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH)).thenReturn(ENCODED_VALUE);

        when(this.randomService.getEtag()).thenReturn(ETAG);
        CompanyPscs savedPsc = new CompanyPscs();
        when(this.repository.save(any())).thenReturn(savedPsc);

        when(repository.count()).thenReturn(2L);
        CompanyPscs returnedPsc = this.companyPscsService.create(spec);

        assertEquals(savedPsc, returnedPsc);

        ArgumentCaptor<CompanyPscs> pscCaptor = ArgumentCaptor.forClass(CompanyPscs.class);
        verify(repository).save(pscCaptor.capture());

        CompanyPscs companyPsc = pscCaptor.getValue();
        assertNotNull(companyPsc);


        assertEquals("Mrs forename middle corporate-entity-person-with-significant-control", companyPsc.getName());
        assertEquals("corporate-entity-person-with-significant-control", companyPsc.getKind());

        Links links = companyPsc.getSelf();
        assertEquals("/company/" + COMPANY_NUMBER + "/persons-with-significant-control/corporate-entity/" + ENCODED_VALUE,
                links.getSelf());
    }

    @Test
    void delete() {
        CompanyPscs companyPscs = new CompanyPscs();
        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.of(companyPscs));

        assertTrue(this.companyPscsService.delete(COMPANY_NUMBER));
        verify(repository).delete(companyPscs);
    }

    @Test
    void deleteNoDataException() {
        CompanyPscs companyPscs = null;
        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.ofNullable(companyPscs));

        assertFalse(this.companyPscsService.delete(COMPANY_NUMBER));
        verify(repository, never()).delete(companyPscs);
    }

}
