package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.enums.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.model.rest.enums.PscType;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscsRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class CompanyPscServiceImplTest {

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
    private CompanyPscServiceImpl companyPscsService;

    @Test
    void create_OverseasEntity_CreatesBeneficialOwners() throws DataException {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.REGISTERED_OVERSEAS_ENTITY);
        internalCompanyRequest.setNumberOfPscs(3);
        internalCompanyRequest.setPscType(List.of(PscType.INDIVIDUAL_BENEFICIAL_OWNER));
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        List<CompanyPscs> result = companyPscsService.create(internalCompanyRequest);

        assertNotNull(result);
        verify(repository, times(3)).save(any(CompanyPscs.class));
    }

    @Test
    void create_SuperSecurePsc_CreatesSuperSecurePsc() throws DataException {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.LTD);
        internalCompanyRequest.setHasSuperSecurePscs(true);
        internalCompanyRequest.setPscType(null);
        internalCompanyRequest.setNumberOfPscs(null);
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        List<CompanyPscs> result = companyPscsService.create(internalCompanyRequest);

        assertNotNull(result);

        ArgumentCaptor<CompanyPscs> captor = ArgumentCaptor.forClass(CompanyPscs.class);
        verify(repository).save(captor.capture());

        CompanyPscs savedPsc = captor.getValue();
        assertEquals("super-secure-person-with-significant-control", savedPsc.getKind());
        assertEquals("super-secure-persons-with-significant-control", savedPsc.getDescription());
    }

    @Test
    void create_SuperSecureBeneficialOwner_CreatesCorrectType() throws DataException {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.REGISTERED_OVERSEAS_ENTITY);
        internalCompanyRequest.setHasSuperSecurePscs(true);
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        List<CompanyPscs> result = companyPscsService.create(internalCompanyRequest);

        assertNotNull(result);

        ArgumentCaptor<CompanyPscs> captor = ArgumentCaptor.forClass(CompanyPscs.class);
        verify(repository).save(captor.capture());

        CompanyPscs savedPsc = captor.getValue();
        assertEquals("super-secure-beneficial-owner", savedPsc.getKind());
        assertEquals("super-secure-beneficial-owner", savedPsc.getDescription());
    }

    @Test
    void create_OverseaCompany_ReturnsEmptyList() throws DataException {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.OVERSEA_COMPANY);
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        List<CompanyPscs> result = companyPscsService.create(internalCompanyRequest);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, never()).save(any());
    }

    @Test
    void create_WithAccountsDueStatus_SetsCorrectDates() throws DataException {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setAccountsDueStatus("due-soon");
        internalCompanyRequest.setNumberOfPscs(1);
        internalCompanyRequest.setPscType(List.of(PscType.INDIVIDUAL));
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        LocalDate dueDate = LocalDate.now().plusDays(10);

        when(randomService.generateAccountsDueDateByStatus("due-soon")).thenReturn(dueDate);
        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        companyPscsService.create(internalCompanyRequest);

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
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.LTD);
        internalCompanyRequest.setNumberOfPscs(3);
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        companyPscsService.create(internalCompanyRequest);

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
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.LTD);
        internalCompanyRequest.setNumberOfPscs(0);
        internalCompanyRequest.setPscType(List.of(PscType.INDIVIDUAL));
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        DataException exception = assertThrows(DataException.class,
                () -> companyPscsService.create(internalCompanyRequest));

        assertEquals("psc_type must be accompanied by number_of_psc", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void create_BeneficialOwnerTypeWithNonOverseas_ThrowsException() {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.LTD);
        internalCompanyRequest.setNumberOfPscs(2);
        internalCompanyRequest.setPscType(List.of(PscType.INDIVIDUAL_BENEFICIAL_OWNER));
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        DataException exception = assertThrows(DataException.class,
                () -> companyPscsService.create(internalCompanyRequest));

        assertEquals("Beneficial owner type is not allowed for this company type",
                exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void create_ValidPscTypeWithNumber_CreatesPscs() throws DataException {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.LTD);
        internalCompanyRequest.setNumberOfPscs(2);
        internalCompanyRequest.setPscType(List.of(PscType.INDIVIDUAL));
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        List<CompanyPscs> result = companyPscsService.create(internalCompanyRequest);

        assertNotNull(result);
        verify(repository, times(2)).save(any(CompanyPscs.class));
    }

    @Test
    void create_ValidBeneficialOwnerType_CreatesBeneficialOwners() throws DataException {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.REGISTERED_OVERSEAS_ENTITY);
        internalCompanyRequest.setNumberOfPscs(2);
        internalCompanyRequest.setPscType(List.of(PscType.CORPORATE_BENEFICIAL_OWNER));
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        List<CompanyPscs> result = companyPscsService.create(internalCompanyRequest);

        assertNotNull(result);
        verify(repository, times(2)).save(any(CompanyPscs.class));
    }

    @Test
    void create_NumberZeroWithPscType_ReturnsNull() {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.LTD);
        internalCompanyRequest.setNumberOfPscs(0);
        internalCompanyRequest.setPscType(List.of(PscType.INDIVIDUAL));
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        assertThrows(DataException.class, () -> companyPscsService.create(internalCompanyRequest));
        verify(repository, never()).save(any());
    }

    @Test
    void createWithExcludedPscCompanyTypeReturnsEmptyList() throws DataException {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.OVERSEA_COMPANY);
        internalCompanyRequest.setPscType(null);
        internalCompanyRequest.setNumberOfPscs(null);
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        List<CompanyPscs> result = companyPscsService.create(internalCompanyRequest);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, never()).save(any());
    }

    @Test
    void create_WithMultiplePscTypes_CreatesCorrectNumber() throws DataException {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.LTD);
        internalCompanyRequest.setNumberOfPscs(3);
        internalCompanyRequest.setPscType(List.of(PscType.INDIVIDUAL, PscType.LEGAL_PERSON));
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        List<CompanyPscs> result = companyPscsService.create(internalCompanyRequest);

        assertNotNull(result);
        verify(repository, times(3)).save(any(CompanyPscs.class));
    }

    @Test
    void create_LegalPersonPsc_DoesNotSetPlaceRegisteredOrRegistrationNumber() throws DataException {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.LTD);
        internalCompanyRequest.setNumberOfPscs(1);
        internalCompanyRequest.setPscType(List.of(PscType.LEGAL_PERSON));
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        companyPscsService.create(internalCompanyRequest);

        // placeRegistered is only set via getCountryOfResidence(ENGLAND) in buildCorporateEntityPsc
        verify(addressService, never()).getCountryOfResidence(JurisdictionType.ENGLAND);
        verify(repository).save(any(CompanyPscs.class));
    }

    @Test
    void create_CorporateEntityPsc_SetPlaceRegisteredAndRegistrationNumber() throws DataException {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.LTD);
        internalCompanyRequest.setNumberOfPscs(1);
        internalCompanyRequest.setPscType(List.of(PscType.CORPORATE_ENTITY));
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        companyPscsService.create(internalCompanyRequest);

        // placeRegistered is set via getCountryOfResidence(ENGLAND) only in buildCorporateEntityPsc
        verify(addressService).getCountryOfResidence(JurisdictionType.ENGLAND);
        verify(repository).save(any(CompanyPscs.class));
    }

    @Test
    void create_WithZeroNumberOfPsc_AndNullPscType_ReturnsEmptyList() throws DataException {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.LTD);
        internalCompanyRequest.setNumberOfPscs(0);
        internalCompanyRequest.setPscType(null); // PscType is null
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        List<CompanyPscs> result = companyPscsService.create(internalCompanyRequest);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, never()).save(any());
    }

    @Test
    void create_WithZeroNumberOfPsc_AndEmptyPscType_ReturnsEmptyList() throws DataException {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.LTD);
        internalCompanyRequest.setNumberOfPscs(0);
        internalCompanyRequest.setPscType(Collections.emptyList()); // PscType is an empty list
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        List<CompanyPscs> result = companyPscsService.create(internalCompanyRequest);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, never()).save(any());
    }

    @Test
    void createReturnsUnsavedPscsWhenCompanyWithDataStructureIsTrue() throws DataException {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.LTD);
        internalCompanyRequest.setNumberOfPscs(1);
        internalCompanyRequest.setPscType(List.of(PscType.INDIVIDUAL));
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(true);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);

        List<CompanyPscs> result = companyPscsService.create(internalCompanyRequest);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(COMPANY_NUMBER, result.get(0).getCompanyNumber());
        assertEquals(ENCODED_ID, result.get(0).getId());
        verify(repository, never()).save(any());
    }

    @Test
    void create_BeneficialOwner_UsesEuropeanUnionServiceAddress() throws DataException {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.REGISTERED_OVERSEAS_ENTITY);
        internalCompanyRequest.setNumberOfPscs(1);
        internalCompanyRequest.setPscType(List.of(PscType.INDIVIDUAL_BENEFICIAL_OWNER));
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        companyPscsService.create(internalCompanyRequest);

        verify(addressService).getAddress(JurisdictionType.EUROPEAN_UNION);
        verify(addressService, never()).getAddress(JurisdictionType.ENGLAND_WALES);
    }

    @Test
    void create_NonBeneficialOwner_UsesEnglandWalesServiceAddress() throws DataException {
        InternalCompanyRequest internalCompanyRequest = new InternalCompanyRequest();
        internalCompanyRequest.setCompanyNumber(COMPANY_NUMBER);
        internalCompanyRequest.setCompanyType(CompanyType.LTD);
        internalCompanyRequest.setNumberOfPscs(1);
        internalCompanyRequest.setPscType(List.of(PscType.INDIVIDUAL));
        internalCompanyRequest.setCompanyWithPopulatedStructureOnly(false);

        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyPscs());

        companyPscsService.create(internalCompanyRequest);

        verify(addressService).getAddress(JurisdictionType.ENGLAND_WALES);
        verify(addressService, never()).getAddress(JurisdictionType.EUROPEAN_UNION);
    }

}
