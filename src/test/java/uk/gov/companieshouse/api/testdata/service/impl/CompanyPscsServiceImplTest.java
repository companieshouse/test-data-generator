package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Clock;
import java.time.Instant;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.entity.Identification;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscsRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.gov.companieshouse.api.testdata.service.impl.CompanyPscsServiceImpl.NATURES_OF_CONTROL;

@ExtendWith(MockitoExtension.class)
class CompanyPscsServiceImplTest {

    private static final int ID_LENGTH = 10;
    private static final int SALT_LENGTH = 8;
    private static final String COMPANY_NUMBER = "12345678";
    private static final String ENCODED_VALUE = "ENCODED";
    private static final String ETAG = "ETAG";
    private static final String INDIVIDUAL = "individual-person-with-significant-control";
    private static final Instant dateNowInstant = Instant.parse("2023-06-01T00:00:00Z");

    @Mock
    private CompanyPscsRepository repository;
    @Mock
    private RandomService randomService;
    @Mock
    private Clock clock;

    @InjectMocks
    private CompanyPscsServiceImpl companyPscsService;

    @Test
    void create() throws DataException {

        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        CompanyPscs savedPsc = new CompanyPscs();
        when(this.repository.save(any())).thenReturn(savedPsc);

        when(this.clock.instant()).thenReturn(dateNowInstant);
        when(this.randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEtag()).thenReturn(ETAG);
        when(randomService.getNumberInRange(1, 5)).thenReturn(OptionalLong.of(3L));
        when(randomService.getNumberInRange(0, NATURES_OF_CONTROL.length))
                .thenReturn(OptionalLong.of(2L), OptionalLong.of(1L), OptionalLong.of(4L));

        CompanyPscs returnedPsc = this.companyPscsService.create(spec);

        assertEquals(savedPsc, returnedPsc);

        verify(repository).save(argThat(psc -> hasExpectedValues(psc)));
    }

    private boolean hasExpectedValues(CompanyPscs companyPsc) {

        assertNotNull(companyPsc);

        assertEquals(ENCODED_VALUE, companyPsc.getId());

        assertEquals(COMPANY_NUMBER, companyPsc.getCompanyNumber());

        assertEquals("34 Silver Street", companyPsc.getAddressLine1());
        assertEquals("Silverstone", companyPsc.getAddressLine2());
        assertEquals("Care of", companyPsc.getCareOf());
        assertEquals("Wales",companyPsc.getCountry());
        assertEquals("Cardiff",companyPsc.getLocality());
        assertEquals("Po Box",companyPsc.getPoBox());
        assertEquals("CF14 3UZ", companyPsc.getPostalCode());
        assertEquals("1", companyPsc.getPremises());
        assertEquals("UK", companyPsc.getRegion());
        assertEquals(true, companyPsc.getAddressSameAsRegisteredOfficeAddress());

        assertNotNull(companyPsc.getCeasedOn());
        assertEquals(dateNowInstant, companyPsc.getCeasedOn());
        assertNotNull(companyPsc.getCreatedAt());
        assertEquals(dateNowInstant, companyPsc.getCreatedAt());

        assertEquals(Arrays.asList(NATURES_OF_CONTROL[2], NATURES_OF_CONTROL[1], NATURES_OF_CONTROL[4]), companyPsc.getNaturesOfControl());

        assertEquals(dateNowInstant, companyPsc.getNotifiedOn());
        assertEquals("reference etag", companyPsc.getReferenceEtag());
        assertEquals("reference psc id", companyPsc.getReferencePscId());
        assertEquals(dateNowInstant, companyPsc.getRegisterEntryDate());
        assertNotNull(companyPsc.getUpdatedAt());
        assertEquals(dateNowInstant, companyPsc.getUpdatedAt());

        assertNotNull(companyPsc.getStatementActionDate());
        assertEquals(dateNowInstant, companyPsc.getStatementActionDate());
        assertEquals("statement type", companyPsc.getStatementType());

        if (companyPsc.getKind().equals(INDIVIDUAL)) {

            assertEquals("Mr Test Tester Testington", companyPsc.getNameElements().toString());
            assertEquals("British", companyPsc.getNationality());
            assertNotNull(companyPsc.getDateOfBirth());
            assertEquals("Wales", companyPsc.getCountryOfResidence());

        } else {
            Identification identification = companyPsc.getIdentification();
            assertEquals("UK", identification.getCountryRegistered());
            assertEquals("Legal Authority", identification.getLegalAuthority());
            assertEquals("Legal Form", identification.getLegalForm());
            assertEquals("Wales", identification.getPlaceRegistered());
            assertEquals("123456", identification.getRegistrationNumber());
        }

        assertEquals("individual-person-with-significant-control", companyPsc.getKind());

        Links links = companyPsc.getLinks();
        assertEquals("/company/" + COMPANY_NUMBER + "/persons-with-significant-control/individual/" + ENCODED_VALUE,
                links.getSelf());

        return true;
    }

    @Test
    void createWhenThereIsAlreadyASinglePsc() throws DataException {

        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        CompanyPscs savedPsc = new CompanyPscs();
        when(this.repository.save(any())).thenReturn(savedPsc);

        when(this.randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEtag()).thenReturn(ETAG);

        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.of(buildListOfCompanyPscs(1)));

        CompanyPscs returnedPsc = this.companyPscsService.create(spec);

        assertEquals(savedPsc, returnedPsc);

        ArgumentCaptor<CompanyPscs> pscCaptor = ArgumentCaptor.forClass(CompanyPscs.class);
        verify(repository).save(pscCaptor.capture());

        CompanyPscs companyPsc = pscCaptor.getValue();
        assertNotNull(companyPsc);

        assertEquals("Mr A Jones", companyPsc.getName());
        assertEquals("legal-person-person-with-significant-control", companyPsc.getKind());

        Links links = companyPsc.getLinks();
        assertEquals("/company/" + COMPANY_NUMBER + "/persons-with-significant-control/legal-person/" + ENCODED_VALUE,
                links.getSelf());
    }

    @Test
    void createWhenThereAreAlreadyTwoPscs() throws DataException {

        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        CompanyPscs savedPsc = new CompanyPscs();
        when(this.repository.save(any())).thenReturn(savedPsc);

        when(this.randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEtag()).thenReturn(ETAG);

        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.of(buildListOfCompanyPscs(2)));

        CompanyPscs returnedPsc = this.companyPscsService.create(spec);

        assertEquals(savedPsc, returnedPsc);

        ArgumentCaptor<CompanyPscs> pscCaptor = ArgumentCaptor.forClass(CompanyPscs.class);
        verify(repository).save(pscCaptor.capture());

        CompanyPscs companyPsc = pscCaptor.getValue();
        assertNotNull(companyPsc);


        assertEquals("Mr A Jones", companyPsc.getName());
        assertEquals("corporate-entity-person-with-significant-control", companyPsc.getKind());

        Links links = companyPsc.getLinks();
        assertEquals("/company/" + COMPANY_NUMBER + "/persons-with-significant-control/corporate-entity/" + ENCODED_VALUE,
                links.getSelf());
    }

    @Test
    void delete() {
        List<CompanyPscs> companyPscs = new ArrayList<>();
        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.of(companyPscs));

        assertTrue(this.companyPscsService.delete(COMPANY_NUMBER));
        verify(repository).deleteAll(companyPscs);
    }

    @Test
    void deleteNoDataException() {
        List<CompanyPscs> companyPscs = null;
        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.ofNullable(companyPscs));

        assertFalse(this.companyPscsService.delete(COMPANY_NUMBER));
        verify(repository, never()).deleteAll(companyPscs);
    }

    private List<CompanyPscs> buildListOfCompanyPscs(int howManyPscs) {

        List<CompanyPscs> listOfCompanyPscs = new ArrayList<>();
        CompanyPscs aCompanyPsc = new CompanyPscs();

        for(int i = 0; i < howManyPscs; i++) {
        	listOfCompanyPscs.add(aCompanyPsc);
        }
        
        return listOfCompanyPscs;
    }
}
