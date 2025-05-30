package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.core.util.Json;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.BarcodeServiceException;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AssociatedFiling;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.entity.Resolutions;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.FilingHistorySpec;
import uk.gov.companieshouse.api.testdata.model.rest.ResolutionsSpec;
import uk.gov.companieshouse.api.testdata.repository.FilingHistoryRepository;
import uk.gov.companieshouse.api.testdata.service.BarcodeService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class FilingHistoryServiceImplTest {
    private static final Long UNENCODED_ID = 2345678L;
    private static final String TEST_ID = "test_id";
    private static final String COMPANY_NUMBER = "12345678";
    private static final String BARCODE = "BARCODE";
    private static final int ENTITY_ID_LENGTH = 9;
    private static final String ENTITY_ID_PREFIX = "8";

    @Mock
    private FilingHistoryRepository repository;

    @Mock
    private RandomService randomService;

    @Mock
    private BarcodeService barcodeService;

    @InjectMocks
    private FilingHistoryServiceImpl filingHistoryService;

    private static final String CERTIFICATE_DESCRIPTION
            = "Certificate of incorporation general company details & statements of; "
            + "officers, capital & shareholdings, guarantee, compliance memorandum of association";

    @Test
    void create() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        FilingHistorySpec filingHistorySpec = new FilingHistorySpec();
        spec.setFilingHistoryList(List.of(filingHistorySpec));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UNENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UNENCODED_ID, 8))
                .thenReturn(TEST_ID);
        when(barcodeService.getBarcode()).thenReturn(BARCODE);

        FilingHistory savedHistory = new FilingHistory();
        when(repository.save(Mockito.any())).thenReturn(savedHistory);

        FilingHistory returnedHistory = this.filingHistoryService.create(spec);

        assertEquals(returnedHistory, savedHistory);

        ArgumentCaptor<FilingHistory> filingHistoryCaptor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(repository).save(filingHistoryCaptor.capture());

        FilingHistory filingHistory = filingHistoryCaptor.getValue();

        assertEquals(TEST_ID, filingHistory.getId());
        assertEquals(COMPANY_NUMBER, filingHistory.getCompanyNumber());
        assertNotNull(filingHistory.getLinks());
        assertEquals("incorporation", filingHistory.getCategory());
        assertEquals("incorporation-company", filingHistory.getDescription());
        assertNotNull(filingHistory.getDate());
        assertEquals("NEWINC", filingHistory.getType());
        assertEquals(Integer.valueOf(10), filingHistory.getPages());
        assertEquals(ENTITY_ID_PREFIX + UNENCODED_ID, filingHistory.getEntityId());
        assertEquals(CERTIFICATE_DESCRIPTION, filingHistory.getOriginalDescription());
        assertEquals(BARCODE, filingHistory.getBarcode());

        List<AssociatedFiling> associatedFilings = filingHistory.getAssociatedFilings();
        assertEquals(2, associatedFilings.size());

        AssociatedFiling incorporation = associatedFilings.getFirst();
        assertEquals("incorporation", incorporation.getCategory());
        assertNotNull(incorporation.getDate());
        assertEquals("model-articles-adopted", incorporation.getDescription());
        assertEquals("MODEL ARTICLES", incorporation.getType());

        AssociatedFiling capital = associatedFilings.get(1);
        assertNotNull(capital.getActionDate());
        assertEquals("capital", capital.getCategory());
        assertNotNull(capital.getDate());
        assertEquals("statement-of-capital", capital.getDescription());
        assertNotNull(capital.getDescriptionValues());
        assertEquals("11/09/19 Statement of Capital;GBP 1", capital.getOriginalDescription());
        assertEquals("SH01", capital.getType());
    }

    @Test
    void createBarcodeServiceException() throws BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        final String exceptionMessage = "Barcode error";
        when(barcodeService.getBarcode()).thenThrow(new BarcodeServiceException(exceptionMessage));

        DataException exception = assertThrows(DataException.class, () ->
                this.filingHistoryService.create(spec)
        );
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void delete() {
        FilingHistory filingHistory1 = new FilingHistory();
        FilingHistory filingHistory2 = new FilingHistory();
        List<FilingHistory> filingHistories = List.of(filingHistory1, filingHistory2);

        when(repository.findAllByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.of(filingHistories));

        assertTrue(filingHistoryService.delete(COMPANY_NUMBER));

        verify(repository).delete(filingHistory1);
        verify(repository).delete(filingHistory2);
    }

    @Test
    void deleteNoCompany() {
        when(repository.findAllByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.empty());

        assertFalse(this.filingHistoryService.delete(COMPANY_NUMBER));
        verify(repository, never()).delete(any());
    }

    @Test
    void createWithFilingHistory() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        FilingHistorySpec filingHistorySpec = new FilingHistorySpec();
        filingHistorySpec.setCategory("test-category");
        filingHistorySpec.setDescription("test-description");
        filingHistorySpec.setType("test-type");
        filingHistorySpec.setOriginalDescription("test-original-description");
        spec.setFilingHistoryList(List.of(filingHistorySpec));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UNENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UNENCODED_ID, 8)).thenReturn(TEST_ID);
        when(barcodeService.getBarcode()).thenReturn(BARCODE);

        FilingHistory savedHistory = new FilingHistory();
        when(repository.save(Mockito.any())).thenReturn(savedHistory);

        FilingHistory returnedHistory = this.filingHistoryService.create(spec);

        assertEquals(returnedHistory, savedHistory);

        ArgumentCaptor<FilingHistory> filingHistoryCaptor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(repository).save(filingHistoryCaptor.capture());
        FilingHistory filingHistory = filingHistoryCaptor.getValue();
        assertEquals(TEST_ID, filingHistory.getId());
        assertEquals(COMPANY_NUMBER, filingHistory.getCompanyNumber());
        assertNotNull(filingHistory.getLinks());
        assertEquals("test-category", filingHistory.getCategory());
        assertEquals("test-description", filingHistory.getDescription());
        assertNotNull(filingHistory.getDate());
        assertEquals("test-type", filingHistory.getType());
        assertEquals(Integer.valueOf(10), filingHistory.getPages());
        assertEquals(ENTITY_ID_PREFIX + UNENCODED_ID, filingHistory.getEntityId());
        assertEquals("test-original-description", filingHistory.getOriginalDescription());
        assertEquals(BARCODE, filingHistory.getBarcode());

        List<AssociatedFiling> associatedFilings = filingHistory.getAssociatedFilings();
        assertEquals(2, associatedFilings.size());
        AssociatedFiling incorporation = associatedFilings.get(0);
        assertEquals("incorporation", incorporation.getCategory());
        assertNotNull(incorporation.getDate());
        assertEquals("model-articles-adopted", incorporation.getDescription());
        assertEquals("MODEL ARTICLES", incorporation.getType());

        AssociatedFiling capital = associatedFilings.get(1);
        assertNotNull(capital.getActionDate());
        assertEquals("capital", capital.getCategory());
        assertNotNull(capital.getDate());
        assertEquals("statement-of-capital", capital.getDescription());
        assertNotNull(capital.getDescriptionValues());
        assertEquals("11/09/19 Statement of Capital;GBP 1", capital.getOriginalDescription());
        assertEquals("SH01", capital.getType());
    }

    @Test
    void createWithMultipleFilingHistory() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        FilingHistorySpec filingHistorySpec1 = new FilingHistorySpec();
        filingHistorySpec1.setCategory("test-category-1");
        filingHistorySpec1.setDescription("test-description-1");
        filingHistorySpec1.setType("test-type-1");
        filingHistorySpec1.setOriginalDescription("test-original-description-1");

        FilingHistorySpec filingHistorySpec2 = new FilingHistorySpec();
        filingHistorySpec2.setCategory("test-category-2");
        filingHistorySpec2.setDescription("test-description-2");
        filingHistorySpec2.setType("test-type-2");
        filingHistorySpec2.setOriginalDescription("test-original-description-2");

        spec.setFilingHistoryList(List.of(filingHistorySpec1, filingHistorySpec2));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UNENCODED_ID, UNENCODED_ID + 1);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UNENCODED_ID, 8)).thenReturn(TEST_ID + "_1");
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + (UNENCODED_ID + 1), 8)).thenReturn(TEST_ID + "_2");
        when(barcodeService.getBarcode()).thenReturn(BARCODE);

        FilingHistory savedHistory = new FilingHistory();
        when(repository.save(Mockito.any())).thenReturn(savedHistory);

        FilingHistory returnedHistory = this.filingHistoryService.create(spec);

        assertEquals(savedHistory, returnedHistory);

        ArgumentCaptor<FilingHistory> filingHistoryCaptor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(repository, Mockito.times(2)).save(filingHistoryCaptor.capture());

        List<FilingHistory> capturedHistories = filingHistoryCaptor.getAllValues();
        assertEquals(2, capturedHistories.size());

        FilingHistory first = capturedHistories.get(0);
        assertEquals(TEST_ID + "_1", first.getId());
        assertEquals("test-category-1", first.getCategory());
        assertEquals("test-description-1", first.getDescription());
        assertEquals("test-type-1", first.getType());
        assertEquals("test-original-description-1", first.getOriginalDescription());
        assertEquals(COMPANY_NUMBER, first.getCompanyNumber());
        assertEquals(BARCODE, first.getBarcode());

        FilingHistory second = capturedHistories.get(1);
        assertEquals(TEST_ID + "_2", second.getId());
        assertEquals("test-category-2", second.getCategory());
        assertEquals("test-description-2", second.getDescription());
        assertEquals("test-type-2", second.getType());
        assertEquals("test-original-description-2", second.getOriginalDescription());
        assertEquals(COMPANY_NUMBER, second.getCompanyNumber());
        assertEquals(BARCODE, second.getBarcode());
    }

    @Test
    void createWithNullFilingHistory() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        spec.setFilingHistoryList(Collections.emptyList());

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UNENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UNENCODED_ID, 8)).thenReturn(TEST_ID);
        when(barcodeService.getBarcode()).thenReturn(BARCODE);

        FilingHistory savedHistory = new FilingHistory();
        when(repository.save(Mockito.any())).thenReturn(savedHistory);

        FilingHistory returnedHistory = this.filingHistoryService.create(spec);

        assertEquals(savedHistory, returnedHistory);

        ArgumentCaptor<FilingHistory> filingHistoryCaptor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(repository).save(filingHistoryCaptor.capture());

        FilingHistory filingHistory = filingHistoryCaptor.getValue();
        assertEquals(TEST_ID, filingHistory.getId());
        assertEquals(COMPANY_NUMBER, filingHistory.getCompanyNumber());
        assertNotNull(filingHistory.getLinks());
        assertEquals("incorporation", filingHistory.getCategory());
        assertEquals("incorporation-company", filingHistory.getDescription());
        assertNotNull(filingHistory.getDate());
        assertEquals("NEWINC", filingHistory.getType());
        assertEquals(Integer.valueOf(10), filingHistory.getPages());
        assertEquals(ENTITY_ID_PREFIX + UNENCODED_ID, filingHistory.getEntityId());
        assertEquals(CERTIFICATE_DESCRIPTION, filingHistory.getOriginalDescription());
        assertEquals(BARCODE, filingHistory.getBarcode());

        List<AssociatedFiling> associatedFilings = filingHistory.getAssociatedFilings();
        assertEquals(2, associatedFilings.size());

        AssociatedFiling incorporation = associatedFilings.get(0);
        assertEquals("incorporation", incorporation.getCategory());
        assertNotNull(incorporation.getDate());
        assertEquals("model-articles-adopted", incorporation.getDescription());
        assertEquals("MODEL ARTICLES", incorporation.getType());

        AssociatedFiling capital = associatedFilings.get(1);
        assertNotNull(capital.getActionDate());
        assertEquals("capital", capital.getCategory());
        assertNotNull(capital.getDate());
        assertEquals("statement-of-capital", capital.getDescription());
        assertNotNull(capital.getDescriptionValues());
        assertEquals("11/09/19 Statement of Capital;GBP 1", capital.getOriginalDescription());
        assertEquals("SH01", capital.getType());
    }

    @Test
    void createWhenAccountsDueStatusIsNull() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setAccountsDueStatus(null);

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UNENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UNENCODED_ID, 8))
                .thenReturn(TEST_ID);
        when(barcodeService.getBarcode()).thenReturn(BARCODE);

        FilingHistory savedHistory = new FilingHistory();
        when(repository.save(Mockito.any())).thenReturn(savedHistory);

        FilingHistory returnedHistory = this.filingHistoryService.create(spec);

        assertEquals(returnedHistory, savedHistory);

        ArgumentCaptor<FilingHistory> filingHistoryCaptor
                = ArgumentCaptor.forClass(FilingHistory.class);
        verify(repository).save(filingHistoryCaptor.capture());
        FilingHistory filingHistory = filingHistoryCaptor.getValue();
        assertEquals(TEST_ID, filingHistory.getId());
        assertEquals(COMPANY_NUMBER, filingHistory.getCompanyNumber());
        assertNotNull(filingHistory.getLinks());
        assertEquals("incorporation", filingHistory.getCategory());
        assertEquals("incorporation-company", filingHistory.getDescription());
        assertNotNull(filingHistory.getDate());
        assertEquals("NEWINC", filingHistory.getType());
        assertEquals(Integer.valueOf(10), filingHistory.getPages());
        assertEquals(ENTITY_ID_PREFIX + UNENCODED_ID, filingHistory.getEntityId());
        assertEquals(CERTIFICATE_DESCRIPTION ,filingHistory.getOriginalDescription());
        assertEquals(BARCODE, filingHistory.getBarcode());
    }

    @Test
    void createWhenAccountsDueStatusIsDueSoon() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setAccountsDueStatus("due-soon");

        FilingHistorySpec filingHistorySpec = new FilingHistorySpec();
        spec.setFilingHistoryList(List.of(filingHistorySpec));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UNENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UNENCODED_ID, 8))
                .thenReturn(TEST_ID);
        when(barcodeService.getBarcode()).thenReturn(BARCODE);
        when(randomService.generateAccountsDueDateByStatus("due-soon")).thenReturn(LocalDate.now());

        FilingHistory savedHistory = new FilingHistory();
        when(repository.save(Mockito.any())).thenReturn(savedHistory);

        FilingHistory returnedHistory = this.filingHistoryService.create(spec);

        assertEquals(savedHistory, returnedHistory);

        ArgumentCaptor<FilingHistory> filingHistoryCaptor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(repository).save(filingHistoryCaptor.capture());

        FilingHistory filingHistory = filingHistoryCaptor.getValue();
        assertEquals(TEST_ID, filingHistory.getId());
        assertEquals(COMPANY_NUMBER, filingHistory.getCompanyNumber());
        assertNotNull(filingHistory.getLinks());
        assertEquals("incorporation", filingHistory.getCategory());
        assertEquals("incorporation-company", filingHistory.getDescription());
        assertNotNull(filingHistory.getDate());
        assertEquals("NEWINC", filingHistory.getType());
        assertEquals(Integer.valueOf(10), filingHistory.getPages());
        assertEquals(ENTITY_ID_PREFIX + UNENCODED_ID, filingHistory.getEntityId());
        assertEquals(CERTIFICATE_DESCRIPTION, filingHistory.getOriginalDescription());
        assertEquals(BARCODE, filingHistory.getBarcode());
    }

    @Test
    void createWhenAccountsDueStatusIsOverdue() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setAccountsDueStatus("overdue");

        FilingHistorySpec filingHistorySpec = new FilingHistorySpec();
        spec.setFilingHistoryList(List.of(filingHistorySpec));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UNENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UNENCODED_ID, 8))
                .thenReturn(TEST_ID);
        when(barcodeService.getBarcode()).thenReturn(BARCODE);
        when(randomService.generateAccountsDueDateByStatus("overdue")).thenReturn(LocalDate.now());

        FilingHistory savedHistory = new FilingHistory();
        when(repository.save(Mockito.any())).thenReturn(savedHistory);

        FilingHistory returnedHistory = this.filingHistoryService.create(spec);

        assertEquals(savedHistory, returnedHistory);

        ArgumentCaptor<FilingHistory> filingHistoryCaptor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(repository).save(filingHistoryCaptor.capture());

        FilingHistory filingHistory = filingHistoryCaptor.getValue();
        assertEquals(TEST_ID, filingHistory.getId());
        assertEquals(COMPANY_NUMBER, filingHistory.getCompanyNumber());
        assertNotNull(filingHistory.getLinks());
        assertEquals("incorporation", filingHistory.getCategory());
        assertEquals("incorporation-company", filingHistory.getDescription());
        assertNotNull(filingHistory.getDate());
        assertEquals("NEWINC", filingHistory.getType());
        assertEquals(Integer.valueOf(10), filingHistory.getPages());
        assertEquals(ENTITY_ID_PREFIX + UNENCODED_ID, filingHistory.getEntityId());
        assertEquals(CERTIFICATE_DESCRIPTION, filingHistory.getOriginalDescription());
        assertEquals(BARCODE, filingHistory.getBarcode());
    }

    @Test
    void createWithMultipleFilingHistoryTypes() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        FilingHistorySpec ap01Spec = new FilingHistorySpec();
        ap01Spec.setType("AP01");
        ap01Spec.setCategory("appointment");
        ap01Spec.setDescription("appointment-description");

        FilingHistorySpec mr01Spec = new FilingHistorySpec();
        mr01Spec.setType("MR01");
        mr01Spec.setCategory("mortgage");
        mr01Spec.setDescription("mortgage-description");

        FilingHistorySpec resolutionsSpec = new FilingHistorySpec();
        resolutionsSpec.setType("RESOLUTIONS");
        resolutionsSpec.setCategory("resolution-category");

        ResolutionsSpec resolution1 = new ResolutionsSpec();
        resolution1.setBarcode("res-barcode-1");
        resolution1.setCategory("resolution-cat-1");
        resolution1.setDescription("resolution-desc-1");
        resolution1.setSubCategory("sub-cat-1");
        resolution1.setType("RES1");

        ResolutionsSpec resolution2 = new ResolutionsSpec();
        resolution2.setBarcode("res-barcode-2");
        resolution2.setCategory("resolution-cat-2");
        resolution2.setDescription("resolution-desc-2");
        resolution2.setSubCategory("sub-cat-2");
        resolution2.setType("RES2");

        resolutionsSpec.setResolutions(List.of(resolution1, resolution2));

        spec.setFilingHistoryList(List.of(ap01Spec, mr01Spec, resolutionsSpec));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UNENCODED_ID);
        when(randomService.addSaltAndEncode(Mockito.anyString(), eq(8))).thenReturn(TEST_ID);
        when(barcodeService.getBarcode()).thenReturn(BARCODE);

        when(repository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<FilingHistory> createdHistories = new ArrayList<>();
        for (FilingHistorySpec fhSpec : spec.getFilingHistoryList()) {
            createdHistories.add(filingHistoryService.create(new CompanySpec() {{
                setCompanyNumber(COMPANY_NUMBER);
                setFilingHistoryList(List.of(fhSpec));
            }}));
        }

        // Validate AP01 filing history
        FilingHistory ap01History = createdHistories.get(0);
        assertEquals("AP01", ap01History.getType());
        assertEquals("appointment-description", ap01History.getDescription());
        assertNotNull(ap01History.getDescriptionValues());
        assertNotNull(ap01History.getOriginalValues());
        assertEquals("appointment", ap01History.getCategory());
        assertNotNull(ap01History.getLinks().getDocumentMetadata());

        // Validate MR01 filing history
        FilingHistory mr01History = createdHistories.get(1);
        assertEquals("MR01", mr01History.getType());
        assertEquals("mortgage-description", mr01History.getDescription());
        assertNotNull(mr01History.getDescriptionValues());
        assertTrue(mr01History.isPaperFiled());
        assertEquals(LocalDate.of(2003, 2, 28).atStartOfDay(ZoneOffset.UTC).toInstant(), mr01History.getDate());
        assertEquals("mortgage", mr01History.getCategory());

        // Validate RESOLUTIONS filing history
        FilingHistory resHistory = createdHistories.get(2);
        assertEquals("RESOLUTIONS", resHistory.getType());
        assertEquals("resolution-category", resHistory.getCategory());
        assertNotNull(resHistory.getResolutions());
        assertEquals(2, resHistory.getResolutions().size());

        Resolutions firstResolution = resHistory.getResolutions().get(0);
        assertEquals("res-barcode-1", firstResolution.getBarcode());
        assertEquals("resolution-cat-1", firstResolution.getCategory());
        assertEquals("resolution-desc-1", firstResolution.getDescription());
        assertEquals("sub-cat-1", firstResolution.getSubCategory());
        assertEquals("RES1", firstResolution.getType());
        assertNotNull(firstResolution.getDeltaAt());

        Resolutions secondResolution = resHistory.getResolutions().get(1);
        assertEquals("res-barcode-2", secondResolution.getBarcode());
        assertEquals("resolution-cat-2", secondResolution.getCategory());
        assertEquals("resolution-desc-2", secondResolution.getDescription());
        assertEquals("sub-cat-2", secondResolution.getSubCategory());
        assertEquals("RES2", secondResolution.getType());
        assertNotNull(secondResolution.getDeltaAt());
    }
}