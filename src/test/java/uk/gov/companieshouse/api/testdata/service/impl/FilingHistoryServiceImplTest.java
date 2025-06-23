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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
import uk.gov.companieshouse.api.testdata.model.entity.Capital;
import uk.gov.companieshouse.api.testdata.model.entity.DescriptionValues;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.entity.Resolutions;
import uk.gov.companieshouse.api.testdata.model.rest.CapitalSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.DescriptionValuesSpec;
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
    private FilingHistoryRepository filingHistoryRepository;

    @Mock
    private RandomService randomService;

    @Mock
    private BarcodeService barcodeService;

    @InjectMocks
    private FilingHistoryServiceImpl filingHistoryService;

    private static final String CERTIFICATE_DESCRIPTION
            = "Certificate of incorporation general company details & statements of; "
            + "officers, capital & shareholdings, guarantee, compliance memorandum of association";

    private ResolutionsSpec buildResolution(String barcode, String category, String description, String subCategory, String type) {
        ResolutionsSpec res = new ResolutionsSpec();
        res.setBarcode(barcode);
        res.setCategory(category);
        res.setDescription(description);
        res.setSubCategory(subCategory);
        res.setType(type);
        return res;
    }

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
        when(filingHistoryRepository.save(Mockito.any())).thenReturn(savedHistory);

        FilingHistory returnedHistory = this.filingHistoryService.create(spec);

        assertEquals(returnedHistory, savedHistory);

        ArgumentCaptor<FilingHistory> filingHistoryCaptor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(filingHistoryRepository).save(filingHistoryCaptor.capture());

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

        when(filingHistoryRepository.findAllByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.of(filingHistories));

        assertTrue(filingHistoryService.delete(COMPANY_NUMBER));

        verify(filingHistoryRepository).delete(filingHistory1);
        verify(filingHistoryRepository).delete(filingHistory2);
    }

    @Test
    void deleteNoCompany() {
        when(filingHistoryRepository.findAllByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.empty());

        assertFalse(this.filingHistoryService.delete(COMPANY_NUMBER));
        verify(filingHistoryRepository, never()).delete(any());
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
        when(filingHistoryRepository.save(Mockito.any())).thenReturn(savedHistory);

        FilingHistory returnedHistory = this.filingHistoryService.create(spec);

        assertEquals(returnedHistory, savedHistory);

        ArgumentCaptor<FilingHistory> filingHistoryCaptor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(filingHistoryRepository).save(filingHistoryCaptor.capture());
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
        when(filingHistoryRepository.save(Mockito.any())).thenReturn(savedHistory);

        FilingHistory returnedHistory = this.filingHistoryService.create(spec);

        assertEquals(savedHistory, returnedHistory);

        ArgumentCaptor<FilingHistory> filingHistoryCaptor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(filingHistoryRepository, Mockito.times(2)).save(filingHistoryCaptor.capture());

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
        when(filingHistoryRepository.save(Mockito.any())).thenReturn(savedHistory);

        FilingHistory returnedHistory = this.filingHistoryService.create(spec);

        assertEquals(savedHistory, returnedHistory);

        ArgumentCaptor<FilingHistory> filingHistoryCaptor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(filingHistoryRepository).save(filingHistoryCaptor.capture());

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
    void getBarcodeReturnsValueSuccessfully() throws BarcodeServiceException, DataException {
        String expectedBarcode = "123456789";
        when(barcodeService.getBarcode()).thenReturn(expectedBarcode);

        String actualBarcode = filingHistoryService.getBarcode();

        assertEquals(expectedBarcode, actualBarcode);
    }

    @Test
    void getBarcodeThrowsDataExceptionWhenBarcodeServiceFails() throws BarcodeServiceException {
        when(barcodeService.getBarcode()).thenThrow(new BarcodeServiceException("Barcode error"));

        DataException thrown = assertThrows(DataException.class, () -> {
            filingHistoryService.getBarcode();
        });

        assertEquals("Barcode error", thrown.getMessage());
        assertTrue(thrown.getCause() instanceof BarcodeServiceException);
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
        when(filingHistoryRepository.save(Mockito.any())).thenReturn(savedHistory);

        FilingHistory returnedHistory = this.filingHistoryService.create(spec);

        assertEquals(returnedHistory, savedHistory);

        ArgumentCaptor<FilingHistory> filingHistoryCaptor
                = ArgumentCaptor.forClass(FilingHistory.class);
        verify(filingHistoryRepository).save(filingHistoryCaptor.capture());
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
        when(filingHistoryRepository.save(Mockito.any())).thenReturn(savedHistory);

        FilingHistory returnedHistory = this.filingHistoryService.create(spec);

        assertEquals(savedHistory, returnedHistory);

        ArgumentCaptor<FilingHistory> filingHistoryCaptor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(filingHistoryRepository).save(filingHistoryCaptor.capture());

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
        when(filingHistoryRepository.save(Mockito.any())).thenReturn(savedHistory);

        FilingHistory returnedHistory = this.filingHistoryService.create(spec);

        assertEquals(savedHistory, returnedHistory);

        ArgumentCaptor<FilingHistory> filingHistoryCaptor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(filingHistoryRepository).save(filingHistoryCaptor.capture());

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
        ap01Spec.setDocumentMetadata(true);

        FilingHistorySpec mr01Spec = new FilingHistorySpec();
        mr01Spec.setType("MR01");
        mr01Spec.setCategory("mortgage");
        mr01Spec.setDescription("mortgage-description");

        FilingHistorySpec resolutionsSpec = new FilingHistorySpec();
        resolutionsSpec.setType("RESOLUTIONS");
        resolutionsSpec.setCategory("resolution-category");
        resolutionsSpec.setResolutions(List.of(
                buildResolution("res-barcode-1", "resolution-cat-1", "resolution-desc-1", "sub-cat-1", "RES1"),
                buildResolution("res-barcode-2", "resolution-cat-2", "resolution-desc-2", "sub-cat-2", "RES2")
        ));

        FilingHistorySpec aaSpec = new FilingHistorySpec();
        aaSpec.setType("AA");
        aaSpec.setCategory("accounts");
        aaSpec.setDescription("annual-accounts-description");

        FilingHistorySpec cs01Spec = new FilingHistorySpec();
        cs01Spec.setType("CS01");
        cs01Spec.setCategory("confirmation-statement");
        cs01Spec.setDescription("confirmation-description");

        spec.setFilingHistoryList(List.of(ap01Spec, mr01Spec, resolutionsSpec, aaSpec, cs01Spec));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UNENCODED_ID);
        when(randomService.addSaltAndEncode(Mockito.anyString(), eq(8))).thenReturn(TEST_ID);
        when(barcodeService.getBarcode()).thenReturn(BARCODE);
        when(filingHistoryRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<FilingHistory> createdHistories = new ArrayList<>();
        for (FilingHistorySpec fhSpec : spec.getFilingHistoryList()) {
            createdHistories.add(filingHistoryService.create(new CompanySpec() {{
                setCompanyNumber(COMPANY_NUMBER);
                setFilingHistoryList(List.of(fhSpec));
            }}));
        }

        validateAp01Filing(createdHistories.get(0));
        validateMr01Filing(createdHistories.get(1));
        validateResolutionsFiling(createdHistories.get(2));
        validateAaFiling(createdHistories.get(3));
        validateCs01Filing(createdHistories.get(4));

        FilingHistory resolutionsFiling = createdHistories.get(2);
        assertNotNull(resolutionsFiling.getResolutions(), "Resolutions should not be null");
        assertEquals(2, resolutionsFiling.getResolutions().size(), "Resolutions count should match");
    }

    private void validateAp01Filing(FilingHistory ap01) {
        assertEquals("AP01", ap01.getType());
        assertEquals("appointment-description", ap01.getDescription());
        assertEquals("appointment", ap01.getCategory());
        assertNotNull(ap01.getDescriptionValues());
        assertNotNull(ap01.getOriginalValues());
        assertNotNull(ap01.getLinks().getDocumentMetadata());
    }

    private void validateMr01Filing(FilingHistory mr01) {
        assertEquals("MR01", mr01.getType());
        assertEquals("mortgage-description", mr01.getDescription());
        assertEquals("mortgage", mr01.getCategory());
        assertTrue(mr01.isPaperFiled());
        assertEquals(LocalDate.of(2003, 2, 28).atStartOfDay(ZoneOffset.UTC).toInstant(), mr01.getDate());
        assertNotNull(mr01.getDescriptionValues());
    }

    private void validateResolutionsFiling(FilingHistory res) {
        assertEquals("RESOLUTIONS", res.getType());
        assertEquals("resolution-category", res.getCategory());
        assertNotNull(res.getResolutions());
        assertEquals(2, res.getResolutions().size());

        validateResolution(res.getResolutions().get(0), "res-barcode-1", "resolution-cat-1", "resolution-desc-1", "sub-cat-1", "RES1");
        validateResolution(res.getResolutions().get(1), "res-barcode-2", "resolution-cat-2", "resolution-desc-2", "sub-cat-2", "RES2");
    }

    private void validateResolution(Resolutions r, String barcode, String cat, String desc, String subCat, String type) {
        assertEquals(barcode, r.getBarcode());
        assertEquals(cat, r.getCategory());
        assertEquals(desc, r.getDescription());
        assertEquals(subCat, r.getSubCategory());
        assertEquals(type, r.getType());
        assertNotNull(r.getDeltaAt());
    }

    private void validateAaFiling(FilingHistory aa) {
        assertEquals("AA", aa.getType());
        assertEquals("annual-accounts-description", aa.getDescription());
        assertEquals("accounts", aa.getCategory());
        assertNotNull(aa.getDescriptionValues());
        assertNotNull(aa.getDescriptionValues().getMadeUpDate());
    }

    private void validateCs01Filing(FilingHistory cs01) {
        assertEquals("CS01", cs01.getType());
        assertEquals("confirmation-description", cs01.getDescription());
        assertEquals("confirmation-statement", cs01.getCategory());
        assertNotNull(cs01.getDescriptionValues());
        assertNotNull(cs01.getDescriptionValues().getMadeUpDate());
    }

    @Test
    void getFilingHistories_whenPresentAndNotEmpty_returnsList() {
        List<FilingHistory> mockList = List.of(new FilingHistory());
        when(filingHistoryRepository.findAllByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.of(mockList));

        List<FilingHistory> result = filingHistoryService.getFilingHistories(COMPANY_NUMBER);

        assertFalse(result.isEmpty());
        assertEquals(mockList, result);
        verify(filingHistoryRepository).findAllByCompanyNumber(COMPANY_NUMBER);
    }

    @Test
    void getFilingHistories_whenNotPresent_returnsEmptyList() {
        when(filingHistoryRepository.findAllByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.empty());

        List<FilingHistory> result = filingHistoryService.getFilingHistories(COMPANY_NUMBER);

        assertTrue(result.isEmpty());
        verify(filingHistoryRepository).findAllByCompanyNumber(COMPANY_NUMBER);
    }

    @Test
    void getFilingHistories_whenPresentButEmpty_returnsEmptyList() {
        when(filingHistoryRepository.findAllByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.of(Collections.emptyList()));

        List<FilingHistory> result = filingHistoryService.getFilingHistories(COMPANY_NUMBER);

        assertTrue(result.isEmpty());
        verify(filingHistoryRepository).findAllByCompanyNumber(COMPANY_NUMBER);
    }

    @Test
    void createDescriptionValues_whenTypeIsSH01_setsCapitalAndDate() {
        CapitalSpec capitalSpec = new CapitalSpec();
        capitalSpec.setCurrency("GBP");
        capitalSpec.setFigure("100");

        DescriptionValuesSpec descriptionValuesSpec = new DescriptionValuesSpec();
        descriptionValuesSpec.setCapital(List.of(capitalSpec));

        FilingHistorySpec fhSpec = new FilingHistorySpec();
        fhSpec.setType("SH01");
        fhSpec.setDescriptionValues(descriptionValuesSpec);

        Instant expectedDate = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant();

        DescriptionValues result = filingHistoryService.createDescriptionValues("SH01", expectedDate, fhSpec);

        assertNotNull(result);
        assertEquals(expectedDate, result.getDate());

        List<Capital> capitalList = result.getCapital();
        assertNotNull(capitalList);
        assertEquals(1, capitalList.size());

        Capital capital = capitalList.get(0);
        assertEquals("GBP", capital.getCurrency());
        assertEquals("100", capital.getFigure());
    }
}