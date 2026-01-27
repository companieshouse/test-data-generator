package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.PscType;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscsRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class CompanyPscsServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String ENCODED_ID = "encoded123";
    private static final String ETAG = "etag123";
    private static final Instant NOW = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant();

    @Mock
    private CompanyPscsRepository repository;
    @Mock
    private RandomService randomService;
    @Mock
    private AddressService addressService;

    @InjectMocks
    private CompanyPscsServiceImpl companyPscsService;

    @Test
    void create_OverseasEntity_CreatesBeneficialOwners() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.REGISTERED_OVERSEAS_ENTITY);
        spec.setNumberOfPscs(3);
        spec.setPscType(List.of(PscType.INDIVIDUAL_BENEFICIAL_OWNER));
        spec.setCombinedTdg(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        List<CompanyPscs> result = companyPscsService.create(spec);

        assertNotNull(result);
        verify(repository, times(3)).save(any(CompanyPscs.class));
    }

    @Test
    void create_SuperSecurePsc_CreatesSuperSecurePsc() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.LTD);
        spec.setHasSuperSecurePscs(true);
        spec.setPscType(null);
        spec.setNumberOfPscs(null);
        spec.setCombinedTdg(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        List<CompanyPscs> result = companyPscsService.create(spec);

        assertNotNull(result);

        ArgumentCaptor<CompanyPscs> captor = ArgumentCaptor.forClass(CompanyPscs.class);
        verify(repository).save(captor.capture());

        CompanyPscs savedPsc = captor.getValue();
        assertEquals("super-secure-person-with-significant-control", savedPsc.getKind());
        assertEquals("super-secure-persons-with-significant-control", savedPsc.getDescription());
    }

    @Test
    void create_SuperSecureBeneficialOwner_CreatesCorrectType() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.REGISTERED_OVERSEAS_ENTITY);
        spec.setHasSuperSecurePscs(true);
        spec.setCombinedTdg(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        List<CompanyPscs> result = companyPscsService.create(spec);

        assertNotNull(result);

        ArgumentCaptor<CompanyPscs> captor = ArgumentCaptor.forClass(CompanyPscs.class);
        verify(repository).save(captor.capture());

        CompanyPscs savedPsc = captor.getValue();
        assertEquals("super-secure-beneficial-owner", savedPsc.getKind());
        assertEquals("super-secure-beneficial-owner", savedPsc.getDescription());
    }

    @Test
    void create_OverseaCompany_ReturnsNull() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.OVERSEA_COMPANY);
        spec.setCombinedTdg(false);

        List<CompanyPscs> result = companyPscsService.create(spec);

        assertNull(result);
        verify(repository, never()).save(any());
    }

    @Test
    void create_WithAccountsDueStatus_SetsCorrectDates() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setAccountsDueStatus("due-soon");
        spec.setNumberOfPscs(1);
        spec.setPscType(List.of(PscType.INDIVIDUAL));
        spec.setCombinedTdg(false);

        LocalDate dueDate = LocalDate.now().plusDays(10);

        when(randomService.generateAccountsDueDateByStatus("due-soon")).thenReturn(dueDate);
        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        companyPscsService.create(spec);

        ArgumentCaptor<CompanyPscs> captor = ArgumentCaptor.forClass(CompanyPscs.class);
        verify(repository, atLeastOnce()).save(captor.capture());

        Instant expectedInstant = dueDate.atStartOfDay(ZoneId.of("UTC")).toInstant();
        assertTrue(captor.getAllValues().stream()
                .anyMatch(psc -> expectedInstant.equals(psc.getCreatedAt())));
    }

    @Test
    void delete_CompanyWithPscs_DeletesAll() {
        List<CompanyPscs> pscs = List.of(new CompanyPscs(), new CompanyPscs());
        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.of(pscs));

        boolean result = companyPscsService.delete(COMPANY_NUMBER);

        assertTrue(result);
        verify(repository).deleteAll(pscs);
    }

    @Test
    void delete_CompanyWithoutPscs_ReturnsFalse() {
        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.empty());

        boolean result = companyPscsService.delete(COMPANY_NUMBER);

        assertFalse(result);
        verify(repository, never()).deleteAll(any());
    }

    @Test
    void create_StandardCompany_VerifyBaseFields() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.LTD);
        spec.setNumberOfPscs(3);
        spec.setCombinedTdg(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        companyPscsService.create(spec);

        ArgumentCaptor<CompanyPscs> captor = ArgumentCaptor.forClass(CompanyPscs.class);
        verify(repository, times(3)).save(captor.capture());


        CompanyPscs savedPsc = captor.getAllValues().get(0);
        assertEquals(COMPANY_NUMBER, savedPsc.getCompanyNumber());
        assertEquals(ENCODED_ID, savedPsc.getId());
        assertEquals(ETAG, savedPsc.getEtag());
        assertEquals("statement type", savedPsc.getStatementType());
        assertNotNull(savedPsc.getCreatedAt());
        assertNotNull(savedPsc.getUpdatedAt());
    }

    @Test
    void create_PscTypeWithoutNumber_ThrowsException() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.LTD);
        spec.setPscType(List.of(PscType.INDIVIDUAL));
        spec.setCombinedTdg(false);

        DataException exception = assertThrows(DataException.class,
                () -> companyPscsService.create(spec));

        assertEquals("psc_type must be accompanied by number_of_psc", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void create_BeneficialOwnerTypeWithNonOverseas_ThrowsException() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.LTD);
        spec.setNumberOfPscs(2);
        spec.setPscType(List.of(PscType.INDIVIDUAL_BENEFICIAL_OWNER));
        spec.setCombinedTdg(false);

        DataException exception = assertThrows(DataException.class,
                () -> companyPscsService.create(spec));

        assertEquals("Beneficial owner type is not allowed for this company type",
                exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void create_ValidPscTypeWithNumber_CreatesPscs() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.LTD);
        spec.setNumberOfPscs(2);
        spec.setPscType(List.of(PscType.INDIVIDUAL));
        spec.setCombinedTdg(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        List<CompanyPscs> result = companyPscsService.create(spec);

        assertNotNull(result);
        verify(repository, times(2)).save(any(CompanyPscs.class));
    }

    @Test
    void create_ValidBeneficialOwnerType_CreatesBeneficialOwners() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.REGISTERED_OVERSEAS_ENTITY);
        spec.setNumberOfPscs(2);
        spec.setPscType(List.of(PscType.CORPORATE_BENEFICIAL_OWNER));
        spec.setCombinedTdg(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        List<CompanyPscs> result = companyPscsService.create(spec);

        assertNotNull(result);
        verify(repository, times(2)).save(any(CompanyPscs.class));
    }

    @Test
    void create_NumberZeroWithPscType_ReturnsNull() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.LTD);
        spec.setNumberOfPscs(0);
        spec.setPscType(List.of(PscType.INDIVIDUAL));
        spec.setCombinedTdg(false);

        assertThrows(DataException.class, () -> companyPscsService.create(spec));
        verify(repository, never()).save(any());
    }

    @Test
    void create_WithNullNumberOfPsc_ReturnsNull() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.LTD);
        spec.setNumberOfPscs(null);
        spec.setCombinedTdg(false);

        List<CompanyPscs> result = companyPscsService.create(spec);

        assertNull(result);
        verify(repository, never()).save(any());
    }

    @Test
    void create_WithMultiplePscTypes_CreatesCorrectNumber() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.LTD);
        spec.setNumberOfPscs(3);
        spec.setPscType(List.of(PscType.INDIVIDUAL, PscType.LEGAL_PERSON));
        spec.setCombinedTdg(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        List<CompanyPscs> result = companyPscsService.create(spec);

        assertNotNull(result);
        verify(repository, times(3)).save(any(CompanyPscs.class));
    }

    @Test
    void create_WithZeroNumberOfPsc_AndNullPscType_ReturnsNull() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.LTD);
        spec.setNumberOfPscs(0);
        spec.setPscType(null); // PscType is null
        spec.setCombinedTdg(false);

        List<CompanyPscs> result = companyPscsService.create(spec);

        assertNull(result);
        verify(repository, never()).save(any());
    }

    @Test
    void create_WithZeroNumberOfPsc_AndEmptyPscType_ReturnsNull() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.LTD);
        spec.setNumberOfPscs(0);
        spec.setPscType(Collections.emptyList()); // PscType is an empty list
        spec.setCombinedTdg(false);

        List<CompanyPscs> result = companyPscsService.create(spec);

        assertNull(result);
        verify(repository, never()).save(any());
    }
}