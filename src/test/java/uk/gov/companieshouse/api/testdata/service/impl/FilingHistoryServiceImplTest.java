package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.Resolutions;

import uk.gov.companieshouse.api.testdata.model.rest.CapitalSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CategoryType;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.DescriptionValuesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.FilingHistoryDescriptionType;
import uk.gov.companieshouse.api.testdata.model.rest.FilingHistorySpec;
import uk.gov.companieshouse.api.testdata.model.rest.ResolutionDescriptionType;
import uk.gov.companieshouse.api.testdata.model.rest.ResolutionType;
import uk.gov.companieshouse.api.testdata.model.rest.ResolutionsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.SubcategoryType;

import uk.gov.companieshouse.api.testdata.repository.FilingHistoryRepository;
import uk.gov.companieshouse.api.testdata.service.BarcodeService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class FilingHistoryServiceImplTest {
    private static final Long UN_ENCODED_ID = 2345678L;
    private static final String TEST_ID = "test_id";
    private static final String COMPANY_NUMBER = "12345678";
    private static final String BARCODE = "BARCODE";
    private static final int ENTITY_ID_LENGTH = 9;
    private static final String ENTITY_ID_PREFIX = "8";
    private static final String NEW_INC = "NEWINC";

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

    private ResolutionsSpec buildResolution(String category, ResolutionDescriptionType description, String subCategory, ResolutionType type) {
        var res = new ResolutionsSpec();
        res.setCategory(category);
        res.setDescription(description);
        res.setSubCategory(subCategory);
        res.setType(type);
        return res;
    }

    @Test
    void create() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyWithDataStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);

        FilingHistorySpec filingHistorySpec = new FilingHistorySpec();
        spec.setFilingHistoryList(List.of(filingHistorySpec));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UN_ENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UN_ENCODED_ID, 8))
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
        assertEquals(NEW_INC, filingHistory.getType());
        assertEquals(Integer.valueOf(10), filingHistory.getPages());
        assertEquals(ENTITY_ID_PREFIX + UN_ENCODED_ID, filingHistory.getEntityId());
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
        spec.setCompanyWithDataStructureOnly(false);
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
        spec.setCompanyWithDataStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);
        FilingHistorySpec filingHistorySpec = new FilingHistorySpec();
        filingHistorySpec.setCategory(CategoryType.INCORPORATION);
        filingHistorySpec.setType("REC1");
        spec.setFilingHistoryList(List.of(filingHistorySpec));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UN_ENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UN_ENCODED_ID, 8)).thenReturn(TEST_ID);
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
        assertEquals(CategoryType.INCORPORATION.getValue(), filingHistory.getCategory());
        assertEquals("incorporation-company", filingHistory.getDescription());
        assertNotNull(filingHistory.getDate());
        assertEquals("REC1", filingHistory.getType());
        assertEquals(Integer.valueOf(10), filingHistory.getPages());
        assertEquals(ENTITY_ID_PREFIX + UN_ENCODED_ID, filingHistory.getEntityId());
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
    void createWithMultipleFilingHistory() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyWithDataStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);

        FilingHistorySpec filingHistorySpec1 = new FilingHistorySpec();
        filingHistorySpec1.setCategory(CategoryType.ACCOUNTS);
        filingHistorySpec1.setType("PSC01");

        FilingHistorySpec filingHistorySpec2 = new FilingHistorySpec();
        filingHistorySpec2.setCategory(CategoryType.ADDRESS);
        filingHistorySpec2.setType("PSC02");

        spec.setFilingHistoryList(List.of(filingHistorySpec1, filingHistorySpec2));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UN_ENCODED_ID, UN_ENCODED_ID + 1);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UN_ENCODED_ID, 8)).thenReturn(TEST_ID + "_1");
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + (UN_ENCODED_ID + 1), 8)).thenReturn(TEST_ID + "_2");
        when(barcodeService.getBarcode()).thenReturn(BARCODE);

        FilingHistory savedHistory = new FilingHistory();
        when(filingHistoryRepository.save(Mockito.any())).thenReturn(savedHistory);

        FilingHistory returnedHistory = this.filingHistoryService.create(spec);

        assertEquals(savedHistory, returnedHistory);

        ArgumentCaptor<FilingHistory> filingHistoryCaptor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(filingHistoryRepository, Mockito.times(2)).save(filingHistoryCaptor.capture());

        List<FilingHistory> capturedHistories = filingHistoryCaptor.getAllValues();
        assertEquals(2, capturedHistories.size());

        FilingHistory first = capturedHistories.getFirst();
        assertEquals(TEST_ID + "_1", first.getId());
        assertEquals(CategoryType.ACCOUNTS.getValue(), first.getCategory());
        assertEquals("incorporation-company", first.getDescription());
        assertEquals("PSC01", first.getType());
        assertEquals(COMPANY_NUMBER, first.getCompanyNumber());
        assertEquals(BARCODE, first.getBarcode());

        FilingHistory second = capturedHistories.get(1);
        assertEquals(TEST_ID + "_2", second.getId());
        assertEquals(CategoryType.ADDRESS.getValue(), second.getCategory());
        assertEquals("incorporation-company", second.getDescription());
        assertEquals("PSC02", second.getType());
        assertEquals("Certificate of incorporation general company details & statements of; "
                + "officers, capital & shareholdings, guarantee, "
                + "compliance memorandum of association", second.getOriginalDescription());
        assertEquals(COMPANY_NUMBER, second.getCompanyNumber());
        assertEquals(BARCODE, second.getBarcode());
    }

    @Test
    void createWithNullFilingHistory() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyWithDataStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);

        spec.setFilingHistoryList(Collections.emptyList());

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UN_ENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UN_ENCODED_ID, 8)).thenReturn(TEST_ID);
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
        assertEquals(NEW_INC, filingHistory.getType());
        assertEquals(Integer.valueOf(10), filingHistory.getPages());
        assertEquals(ENTITY_ID_PREFIX + UN_ENCODED_ID, filingHistory.getEntityId());
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
    void getBarcodeReturnsValueSuccessfully() throws BarcodeServiceException, DataException {
        String expectedBarcode = "123456789";
        when(barcodeService.getBarcode()).thenReturn(expectedBarcode);

        String actualBarcode = filingHistoryService.getBarcode();

        assertEquals(expectedBarcode, actualBarcode);
    }

    @Test
    void getBarcodeThrowsDataExceptionWhenBarcodeServiceFails() throws BarcodeServiceException {
        when(barcodeService.getBarcode()).thenThrow(new BarcodeServiceException("Barcode error"));

        DataException thrown = assertThrows(DataException.class, () -> filingHistoryService.getBarcode());

        assertEquals("Barcode error", thrown.getMessage());
    }

    @Test
    void createWhenAccountsDueStatusIsNull() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyWithDataStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setAccountsDueStatus(null);

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UN_ENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UN_ENCODED_ID, 8))
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
        assertEquals(NEW_INC, filingHistory.getType());
        assertEquals(Integer.valueOf(10), filingHistory.getPages());
        assertEquals(ENTITY_ID_PREFIX + UN_ENCODED_ID, filingHistory.getEntityId());
        assertEquals(CERTIFICATE_DESCRIPTION, filingHistory.getOriginalDescription());
        assertEquals(BARCODE, filingHistory.getBarcode());
    }

    @Test
    void createWhenAccountsDueStatusIsDueSoon() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyWithDataStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setAccountsDueStatus("due-soon");

        FilingHistorySpec filingHistorySpec = new FilingHistorySpec();
        spec.setFilingHistoryList(List.of(filingHistorySpec));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UN_ENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UN_ENCODED_ID, 8))
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
        assertEquals(NEW_INC, filingHistory.getType());
        assertEquals(Integer.valueOf(10), filingHistory.getPages());
        assertEquals(ENTITY_ID_PREFIX + UN_ENCODED_ID, filingHistory.getEntityId());
        assertEquals(CERTIFICATE_DESCRIPTION, filingHistory.getOriginalDescription());
        assertEquals(BARCODE, filingHistory.getBarcode());
    }

    @Test
    void createWhenAccountsDueStatusIsOverdue() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyWithDataStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setAccountsDueStatus("overdue");

        FilingHistorySpec filingHistorySpec = new FilingHistorySpec();
        spec.setFilingHistoryList(List.of(filingHistorySpec));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UN_ENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UN_ENCODED_ID, 8))
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
        assertEquals(NEW_INC, filingHistory.getType());
        assertEquals(Integer.valueOf(10), filingHistory.getPages());
        assertEquals(ENTITY_ID_PREFIX + UN_ENCODED_ID, filingHistory.getEntityId());
        assertEquals(CERTIFICATE_DESCRIPTION, filingHistory.getOriginalDescription());
        assertEquals(BARCODE, filingHistory.getBarcode());
    }

    @Test
    void createWithMultipleFilingHistoryTypes() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyWithDataStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);

        FilingHistorySpec ap01Spec = new FilingHistorySpec();
        ap01Spec.setType("AP01");
        ap01Spec.setCategory(CategoryType.CAPITAL);
        ap01Spec.setDocumentMetadata(true);

        FilingHistorySpec mr01Spec = new FilingHistorySpec();
        mr01Spec.setType("MR01");
        mr01Spec.setCategory(CategoryType.MORTGAGE);

        FilingHistorySpec resolutionsSpec = new FilingHistorySpec();
        resolutionsSpec.setType("RESOLUTIONS");
        resolutionsSpec.setCategory(CategoryType.RESOLUTION);
        resolutionsSpec.setResolutions(List.of(
                buildResolution("resolution-cat-1", ResolutionDescriptionType.ELECTIVE_RESOLUTION, "sub-cat-1", ResolutionType.ELRES),
                buildResolution("resolution-cat-2", ResolutionDescriptionType.LIQUIDATION_SPECIAL_RESOLUTION_TO_WIND_UP_NORTHERN_IRELAND, "sub-cat-2", ResolutionType.RES01)
        ));

        FilingHistorySpec aaSpec = new FilingHistorySpec();
        aaSpec.setType("AA");
        aaSpec.setCategory(CategoryType.ACCOUNTS);

        FilingHistorySpec cs01Spec = new FilingHistorySpec();
        cs01Spec.setType("CS01");
        cs01Spec.setCategory(CategoryType.CONFIRMATION_STATEMENT);

        spec.setFilingHistoryList(List.of(ap01Spec, mr01Spec, resolutionsSpec, aaSpec, cs01Spec));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UN_ENCODED_ID);
        when(randomService.addSaltAndEncode(Mockito.anyString(), eq(8))).thenReturn(TEST_ID);
        when(barcodeService.getBarcode()).thenReturn(BARCODE);
        when(filingHistoryRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<FilingHistory> createdHistories = new ArrayList<>();
        for (FilingHistorySpec fhSpec : spec.getFilingHistoryList()) {
            createdHistories.add(filingHistoryService.create(new CompanySpec() {{
                setCompanyNumber(COMPANY_NUMBER);
                setCompanyWithDataStructureOnly(false);
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
        assertEquals("incorporation-company", ap01.getDescription());
        assertEquals(CategoryType.CAPITAL.getValue(), ap01.getCategory());
        assertNotNull(ap01.getDescriptionValues());
        assertNotNull(ap01.getOriginalValues());
        assertNotNull(ap01.getLinks().getDocumentMetadata());
    }

    private void validateMr01Filing(FilingHistory mr01) {
        assertEquals("MR01", mr01.getType());
        assertEquals("incorporation-company", mr01.getDescription());
        assertEquals(CategoryType.MORTGAGE.getValue(), mr01.getCategory());
        assertTrue(mr01.isPaperFiled());
        assertEquals(LocalDate.of(2003, 2, 28).atStartOfDay(ZoneOffset.UTC).toInstant(), mr01.getDate());
        assertNotNull(mr01.getDescriptionValues());
    }

    private void validateResolutionsFiling(FilingHistory res) {
        assertEquals("RESOLUTIONS", res.getType());
        assertEquals(CategoryType.RESOLUTION.getValue(), res.getCategory());
        assertNotNull(res.getResolutions());
        assertEquals(2, res.getResolutions().size());

        validateResolution(res.getResolutions().get(0), "resolution-cat-1", ResolutionDescriptionType.ELECTIVE_RESOLUTION, "sub-cat-1", ResolutionType.ELRES);
        validateResolution(res.getResolutions().get(1), "resolution-cat-2", ResolutionDescriptionType.LIQUIDATION_SPECIAL_RESOLUTION_TO_WIND_UP_NORTHERN_IRELAND, "sub-cat-2", ResolutionType.RES01);
    }

    private void validateResolution(Resolutions r, String cat, ResolutionDescriptionType desc, String subCat, ResolutionType type) {
        assertEquals(cat, r.getCategory());
        assertEquals(desc.getValue(), r.getDescription());
        assertEquals(subCat, r.getSubCategory());
        assertEquals(type.getValue(), r.getType());
        assertNotNull(r.getDeltaAt());
    }

    private void validateAaFiling(FilingHistory aa) {
        assertEquals("AA", aa.getType());
        assertEquals("incorporation-company", aa.getDescription());
        assertEquals(CategoryType.ACCOUNTS.getValue(), aa.getCategory());
        assertNotNull(aa.getDescriptionValues());
        assertNotNull(aa.getDescriptionValues().getMadeUpDate());
    }

    private void validateCs01Filing(FilingHistory cs01) {
        assertEquals("CS01", cs01.getType());
        assertEquals("incorporation-company", cs01.getDescription());
        assertEquals(CategoryType.CONFIRMATION_STATEMENT.getValue(), cs01.getCategory());
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

        Capital capital = capitalList.getFirst();
        assertEquals("GBP", capital.getCurrency());
        assertEquals("100", capital.getFigure());
    }

    // 1. SubCategory logic
    @Test
    void createWithSubCategory_setsSubCategory() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyWithDataStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);
        FilingHistorySpec fhSpec = new FilingHistorySpec();
        fhSpec.setType("REC1");
        fhSpec.setSubCategory(SubcategoryType.OTHER);
        spec.setFilingHistoryList(List.of(fhSpec));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UN_ENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UN_ENCODED_ID, 8)).thenReturn(TEST_ID);
        when(barcodeService.getBarcode()).thenReturn(BARCODE);

        FilingHistory savedHistory = new FilingHistory();
        when(filingHistoryRepository.save(Mockito.any())).thenReturn(savedHistory);

        filingHistoryService.create(spec);

        ArgumentCaptor<FilingHistory> captor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(filingHistoryRepository).save(captor.capture());
        assertEquals(SubcategoryType.OTHER.getValue(), captor.getValue().getSubCategory());
    }

    // 2. Document metadata in Links
    @Test
    void createLinks_setsDocumentMetadata_whenTrue() {
        Links links = filingHistoryService.createLinks(COMPANY_NUMBER, TEST_ID, true);
        assertEquals("document/" + TEST_ID, links.getDocumentMetadata());
    }

    @Test
    void createLinks_doesNotSetDocumentMetadata_whenFalse() {
        Links links = filingHistoryService.createLinks(COMPANY_NUMBER, TEST_ID, false);
        assertNull(links.getDocumentMetadata());
    }

    // 3. Unknown type handling (default branch)
    @Test
    void createWithUnknownType_setsAssociatedFilings() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyWithDataStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);
        FilingHistorySpec fhSpec = new FilingHistorySpec();
        fhSpec.setType("REC1");
        spec.setFilingHistoryList(List.of(fhSpec));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UN_ENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UN_ENCODED_ID, 8)).thenReturn(TEST_ID);
        when(barcodeService.getBarcode()).thenReturn(BARCODE);

        FilingHistory savedHistory = new FilingHistory();
        when(filingHistoryRepository.save(Mockito.any())).thenReturn(savedHistory);

        filingHistoryService.create(spec);

        ArgumentCaptor<FilingHistory> captor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(filingHistoryRepository).save(captor.capture());
        assertNotNull(captor.getValue().getAssociatedFilings());
        assertEquals(2, captor.getValue().getAssociatedFilings().size());
    }

    // 4. AP01 original values
    @Test
    void createWithAP01_setsOriginalValues() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyWithDataStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);
        FilingHistorySpec fhSpec = new FilingHistorySpec();
        fhSpec.setType("AP01");
        spec.setFilingHistoryList(List.of(fhSpec));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UN_ENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UN_ENCODED_ID, 8)).thenReturn(TEST_ID);
        when(barcodeService.getBarcode()).thenReturn(BARCODE);

        FilingHistory savedHistory = new FilingHistory();
        when(filingHistoryRepository.save(Mockito.any())).thenReturn(savedHistory);

        filingHistoryService.create(spec);

        ArgumentCaptor<FilingHistory> captor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(filingHistoryRepository).save(captor.capture());
        assertNotNull(captor.getValue().getOriginalValues());
        assertEquals("John Test", captor.getValue().getOriginalValues().getOfficerName());
    }

    // 5. convertInstantToDeltaAt static method
    @Test
    void convertInstantToDeltaAt_formatsCorrectly() {
        Instant instant = LocalDateTime.of(2024, 6, 1, 12, 34, 56, 123456000)
                .toInstant(ZoneOffset.UTC);
        String result = FilingHistoryServiceImpl.convertInstantToDeltaAt(instant);
        assertTrue(result.startsWith("20240601123456"));
        assertEquals(20, result.length());
    }

    // 6. AssociatedFilings content
    @Test
    void createAssociatedFilings_setsDescriptionValuesMap() {
        Instant now = Instant.now();
        List<AssociatedFiling> filings = filingHistoryService.createAssociatedFilings(now, now);
        AssociatedFiling capital = filings.stream()
                .filter(f -> "capital".equals(f.getCategory()))
                .findFirst().orElseThrow();
        assertNotNull(capital.getDescriptionValues());
        assertTrue(capital.getDescriptionValues().containsKey("capital"));
        assertTrue(capital.getDescriptionValues().containsKey("date"));
    }

    // 7. Description is set from FilingHistorySpec if present
    @Test
    void createWithDescription_setsCustomDescription() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyWithDataStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);
        FilingHistorySpec fhSpec = new FilingHistorySpec();
        fhSpec.setType("REC1");

        // Mock a description type
        var customDescription = FilingHistoryDescriptionType.APPOINT_PERSON_DIRECTOR_COMPANY_WITH_NAME;
        fhSpec.setDescription(customDescription);

        spec.setFilingHistoryList(List.of(fhSpec));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UN_ENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UN_ENCODED_ID, 8)).thenReturn(TEST_ID);
        when(barcodeService.getBarcode()).thenReturn(BARCODE);

        FilingHistory savedHistory = new FilingHistory();
        when(filingHistoryRepository.save(Mockito.any())).thenReturn(savedHistory);

        filingHistoryService.create(spec);

        ArgumentCaptor<FilingHistory> captor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(filingHistoryRepository).save(captor.capture());
        assertEquals(customDescription.getValue(), captor.getValue().getDescription());
    }

    // 8. Description falls back to default if not set in FilingHistorySpec
    @Test
    void createWithNullDescription_setsDefaultDescription() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyWithDataStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);
        FilingHistorySpec fhSpec = new FilingHistorySpec();
        fhSpec.setType("REC1");
        fhSpec.setDescription(null); // Explicitly null

        spec.setFilingHistoryList(List.of(fhSpec));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UN_ENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UN_ENCODED_ID, 8)).thenReturn(TEST_ID);
        when(barcodeService.getBarcode()).thenReturn(BARCODE);

        FilingHistory savedHistory = new FilingHistory();
        when(filingHistoryRepository.save(Mockito.any())).thenReturn(savedHistory);

        filingHistoryService.create(spec);

        ArgumentCaptor<FilingHistory> captor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(filingHistoryRepository).save(captor.capture());
        assertEquals(FilingHistoryDescriptionType.INCORPORATION_COMPANY.getValue(), captor.getValue().getDescription());
    }

    @Test
    void createReturnsUnsavedFilingHistoryWhenCompanyWithDataStructureIsTrue() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyWithDataStructureOnly(true);

        FilingHistorySpec filingHistorySpec = new FilingHistorySpec();
        spec.setFilingHistoryList(List.of(filingHistorySpec));

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UN_ENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UN_ENCODED_ID, 8)).thenReturn(TEST_ID);
        when(barcodeService.getBarcode()).thenReturn(BARCODE);

        FilingHistory result = filingHistoryService.create(spec);

        assertNotNull(result);
        assertEquals(COMPANY_NUMBER, result.getCompanyNumber());
        assertEquals(TEST_ID, result.getId());
        assertEquals(BARCODE, result.getBarcode());
        verify(filingHistoryRepository, never()).save(any());
    }
}