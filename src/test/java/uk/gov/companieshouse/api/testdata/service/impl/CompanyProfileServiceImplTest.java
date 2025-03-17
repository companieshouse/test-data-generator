package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.OverseasEntity;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.model.rest.RegistersSpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class CompanyProfileServiceImplTest {

    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");
    private static final String COMPANY_NUMBER = "12345678";
    private static final String OVERSEAS_COMPANY_NUMBER = "OE001234";
    private static final String ETAG = "ETAG";
    private static final String COMPANY_STATUS_DISSOLVED = "dissolved";
    private static final String COMPANY_STATUS_ACTIVE = "active";
    private static final String OVERSEAS_STATUS_REGISTERED = "registered";
    private static final CompanyType OVERSEAS_ENTITY_TYPE = CompanyType.REGISTERED_OVERSEAS_ENTITY;
    private static final String COMPANY_STATUS_ADMINISTRATION = "administration";
    private static final CompanyType COMPANY_TYPE_LTD = CompanyType.LTD;
    private static final CompanyType COMPANY_TYPE_PLC = CompanyType.PLC;
    private static final CompanyType COMPANY_TYPE_ROYAL_CHARTER = CompanyType.ROYAL_CHARTER;
    private static final CompanyType COMPANY_TYPE_INDUSTRIAL_AND_PROVIDENT_SOCIETY = CompanyType.INDUSTRIAL_AND_PROVIDENT_SOCIETY;
    public static final String FULL_DATA_AVAILABLE_FROM_THE_COMPANY = "full-data-available-from-the-company";
    public static final String FULL_DATA_AVAILABLE_FROM_DEPARTMENT_OF_THE_ECONOMY = "full-data-available-from-department-of-the-economy";
    public static final String FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY_MUTUALS_PUBLIC_REGISTER = "full-data-available-from-financial-conduct-authority-mutuals-public-register";


    @Mock
    private RandomService randomService;
    @Mock
    private AddressService addressService;
    @Mock
    private CompanyProfileRepository repository;

    @InjectMocks
    private CompanyProfileServiceImpl companyProfileService;

    private CompanySpec spec;
    private CompanyProfile savedProfile;
    private CompanySpec overseasSpec;

    @BeforeEach
    void setUp() {
        spec = new CompanySpec();
        overseasSpec = new CompanySpec();
        overseasSpec.setCompanyNumber(OVERSEAS_COMPANY_NUMBER);
        overseasSpec.setCompanyType(OVERSEAS_ENTITY_TYPE);
        overseasSpec.setJurisdiction(Jurisdiction.UNITED_KINGDOM);
        overseasSpec.setCompanyStatus(OVERSEAS_STATUS_REGISTERED);
        overseasSpec.setHasSuperSecurePscs(Boolean.TRUE);
        spec.setCompanyNumber(COMPANY_NUMBER);
        savedProfile = new CompanyProfile();
    }

    // Helper method to set up common mocks for create tests.
    private void setupCommonMocks(CompanySpec spec, Address mockAddress) {
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(savedProfile);
        when(addressService.getAddress(spec.getJurisdiction())).thenReturn(mockAddress);
    }

    // Helper method that performs create() and captures the saved CompanyProfile.
    private CompanyProfile createAndCapture(CompanySpec spec) {
        Address mockAddress = new Address("", "", "", "", "", "");
        setupCommonMocks(spec, mockAddress);
        CompanyProfile returnedProfile = companyProfileService.create(spec);
        ArgumentCaptor<CompanyProfile> captor = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(captor.capture());
        return captor.getValue();
    }

    // Common assertions for a created CompanyProfile.
    private void assertCreatedProfile(CompanyProfile profile, String companyStatus, String jurisdiction,
                                      String companyType, Boolean hasInsolvencyHistory) {
        assertEquals(COMPANY_NUMBER, profile.getId());
        assertEquals(COMPANY_NUMBER, profile.getCompanyNumber());
        assertEquals("COMPANY " + COMPANY_NUMBER + " LIMITED", profile.getCompanyName());
        assertEquals(companyStatus, profile.getCompanyStatus());
        assertEquals(jurisdiction, profile.getJurisdiction());
        assertEquals(companyType.toLowerCase(), profile.getType());
        if (jurisdiction != null) {
            assertNotNull(profile.getRegisteredOfficeAddress());
        }
        assertFalse(profile.getUndeliverableRegisteredOfficeAddress());
        assertNotNull(profile.getSicCodes());
        assertOnConfirmationStatement(profile.getConfirmationStatement());
        assertFalse(profile.getRegisteredOfficeIsInDispute());
        assertEquals(hasInsolvencyHistory, profile.getHasInsolvencyHistory());
        assertFalse(profile.getHasCharges());
        assertTrue(profile.getCanFile());
        assertEquals(ETAG, profile.getEtag());
        assertOnAccounts(profile.getAccounts());
        assertOnDateOfCreation(profile.getDateOfCreation());
        if (jurisdiction != null) {
            assertEquals("/company/" + COMPANY_NUMBER, profile.getLinks().getSelf());
            assertEquals("/company/" + COMPANY_NUMBER
                    + "/filing-history", profile.getLinks().getFilingHistory());
            assertEquals("/company/" + COMPANY_NUMBER
                    + "/officers", profile.getLinks().getOfficers());
            assertEquals("/company/" + COMPANY_NUMBER
                    + "/persons-with-significant-control-statement",
                    profile.getLinks().getPersonsWithSignificantControlStatement());
        }

    }

    private void assertOnConfirmationStatement(CompanyProfile.ConfirmationStatement cs) {
        assertNotNull(cs.getNextMadeUpTo());
        assertFalse(cs.getOverdue());
        assertNotNull(cs.getNextDue());
    }

    private void assertOnAccounts(CompanyProfile.Accounts accounts) {
        assertNotNull(accounts);
        assertNotNull(accounts.getNextDue());
        assertNotNull(accounts.getPeriodStart());
        assertNotNull(accounts.getPeriodEnd());
        assertNotNull(accounts.getNextAccountsDueOn());
        assertFalse(accounts.getNextAccountsOverdue());
        assertNotNull(accounts.getNextMadeUpTo());
        assertNotNull(accounts.getAccountingReferenceDateDay());
        assertNotNull(accounts.getAccountingReferenceDateMonth());
    }

    private void assertOnDateOfCreation(Instant dateOfCreation) {
        assertNotNull(dateOfCreation);
        Instant now = Instant.now();
        assertTrue(now.isAfter(dateOfCreation));

        LocalDateTime t1 = LocalDateTime.ofInstant(dateOfCreation, ZONE_ID_UTC);
        LocalDateTime t2 = LocalDateTime.ofInstant(now, ZONE_ID_UTC);
        long days = Duration.between(t1, t2).toDays();
        assertTrue(days == 365 || days == 366); // cater for leap years
    }

    @Test
    void createCompanyWithoutCompanyTypeAndWithEnglandWales() {
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyStatus(COMPANY_STATUS_ADMINISTRATION);
        CompanyProfile profile = createAndCapture(spec);
        assertCreatedProfile(profile, spec.getCompanyStatus(),
                spec.getJurisdiction().toString(), COMPANY_TYPE_LTD.getValue(), false);
    }

    @Test
    void createCompanyWithoutCompanyStatusAndWithScotland() {
        setCompanyJurisdictionAndType(Jurisdiction.SCOTLAND,CompanyType.LTD);
        CompanyProfile profile = createAndCapture(spec);
        assertCreatedProfile(profile, COMPANY_STATUS_ACTIVE,
                spec.getJurisdiction().toString(), spec.getCompanyType().getValue(), false);
    }

    @Test
    void createDissolvedCompany() {
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyStatus(COMPANY_STATUS_DISSOLVED);
        CompanyProfile profile = createAndCapture(spec);
        assertCreatedProfile(profile, spec.getCompanyStatus(),
                spec.getJurisdiction().toString(), COMPANY_TYPE_LTD.getValue(), true);
    }

    @Test
    void createPlcCompany() {
        setCompanyJurisdictionAndType(Jurisdiction.ENGLAND_WALES,CompanyType.PLC);
        CompanyProfile profile = createAndCapture(spec);
        assertCreatedProfile(profile, COMPANY_STATUS_ACTIVE,
                spec.getJurisdiction().toString(), spec.getCompanyType().getValue(), false);
    }

    @Test
    void delete() {
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(Optional.of(savedProfile));

        assertTrue(companyProfileService.delete(COMPANY_NUMBER));
        verify(repository).delete(savedProfile);
    }

    @Test
    void deleteNoCompanyProfile() {
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(Optional.empty());

        assertFalse(companyProfileService.delete(COMPANY_NUMBER));
        verify(repository, never()).delete(any());
    }

    @Test
    void createCompanyWithSubType() {
        setCompanyJurisdictionAndType(Jurisdiction.ENGLAND_WALES,CompanyType.LTD);
        spec.setSubType("community-interest-company");
        CompanyProfile profile = createAndCapture(spec);
        assertEquals("community-interest-company", profile.getSubtype());
        assertTrue(profile.getIsCommunityInterestCompany());
    }

    @Test
    void createCompanyWithoutSubType() {
        setCompanyJurisdictionAndType(Jurisdiction.ENGLAND_WALES,CompanyType.LTD);
        spec.setSubType(null);
        CompanyProfile profile = createAndCapture(spec);
        assertNull(profile.getSubtype());
        assertNull(profile.getIsCommunityInterestCompany());
    }

    @Test
    void createCompanyWithNonCicSubType() {
        setCompanyJurisdictionAndType(Jurisdiction.ENGLAND_WALES,CompanyType.LTD);
        spec.setSubType("private-fund-limited-partnership");
        CompanyProfile profile = createAndCapture(spec);
        assertEquals("private-fund-limited-partnership", profile.getSubtype());
        assertFalse(profile.getIsCommunityInterestCompany());
    }

    @Test
    void createCompanyWithSuperSecurePscsTrue() {
        spec.setHasSuperSecurePscs(true);
        CompanyProfile profile = createAndCapture(spec);
        assertTrue(profile.getHasSuperSecurePscs());
    }

    @Test
    void createCompanyWithSuperSecurePscsFalse() {
        spec.setHasSuperSecurePscs(false);
        CompanyProfile profile = createAndCapture(spec);
        assertFalse(profile.getHasSuperSecurePscs());
    }

    @Test
    void createCompanyWithSuperSecurePscsNull() {
        spec.setHasSuperSecurePscs(null);
        CompanyProfile profile = createAndCapture(spec);
        assertNull(profile.getHasSuperSecurePscs());
    }

    @Test
    void createCompanyWithAccountsDueSoon() {
        setCompanyJurisdictionAndType(Jurisdiction.ENGLAND_WALES,CompanyType.LTD);
        spec.setAccountsDueStatus("due-soon");

        Address mockRegisteredAddress = new Address("", "", "", "", "", "");
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(savedProfile);
        when(addressService.getAddress(spec.getJurisdiction())).thenReturn(mockRegisteredAddress);
        when(randomService.generateAccountsDueDateByStatus("due-soon")).thenReturn(LocalDate.now());
        CompanyProfile returnedProfile = this.companyProfileService.create(spec);
        assertEquals(savedProfile, returnedProfile);
        ArgumentCaptor<CompanyProfile> companyProfileCaptor
                = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(companyProfileCaptor.capture());

        CompanyProfile profile = companyProfileCaptor.getValue();
        assertNotNull(profile.getAccounts());
        assertEquals(LocalDate.now().plusYears(1).plusMonths(9)
                .atStartOfDay(ZONE_ID_UTC).toInstant(), profile.getAccounts().getNextDue());
    }

    @Test
    void createCompanyWithAccountsOverdue() {
        setCompanyJurisdictionAndType(Jurisdiction.ENGLAND_WALES,CompanyType.LTD);
        spec.setAccountsDueStatus("overdue");
        Address mockRegisteredAddress = new Address("", "", "", "", "", "");
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(savedProfile);
        when(addressService.getAddress(spec.getJurisdiction())).thenReturn(mockRegisteredAddress);
        when(randomService.generateAccountsDueDateByStatus("overdue")).thenReturn(LocalDate.now());
        CompanyProfile returnedProfile = this.companyProfileService.create(spec);
        assertEquals(savedProfile, returnedProfile);
        ArgumentCaptor<CompanyProfile> companyProfileCaptor
                = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(companyProfileCaptor.capture());

        CompanyProfile profile = companyProfileCaptor.getValue();
        assertNotNull(profile.getAccounts());
        assertEquals(LocalDate.now().plusYears(1).plusMonths(9)
                .atStartOfDay(ZONE_ID_UTC).toInstant(), profile.getAccounts().getNextDue());
    }

    @Test
    void createCompanyWithAccountsDueStatusNull() {
        setCompanyJurisdictionAndType(Jurisdiction.ENGLAND_WALES,CompanyType.LTD);
        spec.setAccountsDueStatus(null);

        Address mockRegisteredAddress = new Address("", "", "", "", "", "");
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(savedProfile);
        when(addressService.getAddress(spec.getJurisdiction())).thenReturn(mockRegisteredAddress);

        CompanyProfile returnedProfile = this.companyProfileService.create(spec);
        assertEquals(savedProfile, returnedProfile);
        ArgumentCaptor<CompanyProfile> companyProfileCaptor
                = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(companyProfileCaptor.capture());

        CompanyProfile profile = companyProfileCaptor.getValue();
        assertNotNull(profile.getAccounts());
        assertNotNull(profile.getAccounts().getNextDue());
    }

    @Test
    void createCompanyWithRegisters() {
        spec.setCompanyNumber(COMPANY_NUMBER);

        RegistersSpec directorsRegister = new RegistersSpec();
        directorsRegister.setRegisterType("directors");
        directorsRegister.setRegisterMovedTo("Companies House");
        spec.setRegisters(List.of(directorsRegister));
        CompanyProfile profile = createAndCapture(spec);
        assertNotNull(profile.getLinks().getRegisters());
    }

    @Test
    void createCompanyWithoutRegisters() {
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setRegisters(Collections.emptyList());
        CompanyProfile profile = createAndCapture(spec);
        assertNull(profile.getLinks().getRegisters());
    }

    @Test
    void testCreate_overseasEntity() {
        when(addressService.getAddress(overseasSpec.getJurisdiction()))
                .thenReturn(new Address("", "", "", "", "", ""));
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(savedProfile);

        CompanyProfile result = companyProfileService.create(overseasSpec);
        ArgumentCaptor<CompanyProfile> captor = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(captor.capture());
        CompanyProfile savedEntity = captor.getValue();
        assertTrue(savedEntity instanceof OverseasEntity, "Expected an instance of OverseasEntity");
        assertEquals(OVERSEAS_COMPANY_NUMBER, result.getId());
        assertEquals(OVERSEAS_COMPANY_NUMBER, result.getCompanyNumber());
        assertEquals(OVERSEAS_STATUS_REGISTERED, result.getCompanyStatus());
        assertEquals(OVERSEAS_ENTITY_TYPE.getValue(), savedEntity.getType());
        assertNotNull(result.getConfirmationStatement().getNextDue());
        assertNotNull(result.getConfirmationStatement().getNextMadeUpTo());
        assertNotNull(result.getRegisteredOfficeAddress());
    }

    @Test
    void createCompanyWithCompanyStatusDetail() {
        setCompanyJurisdictionAndType(Jurisdiction.ENGLAND_WALES,CompanyType.LTD);
        spec.setCompanyStatusDetail("status-detail");
        ArgumentCaptor<CompanyProfile> companyProfileCaptor = createCompanyProfile();
        CompanyProfile profile = companyProfileCaptor.getValue();
        assertEquals("status-detail", profile.getCompanyStatusDetail());
    }

    @Test
    void createCompanyWithoutCompanyStatusDetail() {
        setCompanyJurisdictionAndType(Jurisdiction.ENGLAND_WALES,CompanyType.LTD);
        spec.setCompanyStatusDetail(null);
        ArgumentCaptor<CompanyProfile> companyProfileCaptor = createCompanyProfile();
        CompanyProfile profile = companyProfileCaptor.getValue();
        assertNull(profile.getCompanyStatusDetail());
    }

    @Test
    void createRoyalCharterCompanyAndVerifyPartialData() {
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyType(COMPANY_TYPE_ROYAL_CHARTER);
        spec.setCompanyStatusDetail(null);

        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(savedProfile);

        CompanyProfile returnedProfile = this.companyProfileService.create(spec);
        assertEquals(savedProfile, returnedProfile);
        ArgumentCaptor<CompanyProfile> companyProfileCaptor = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(companyProfileCaptor.capture());

        CompanyProfile profile = companyProfileCaptor.getValue();
        assertEquals(COMPANY_TYPE_ROYAL_CHARTER.getValue(), profile.getType());
        assertEquals(FULL_DATA_AVAILABLE_FROM_THE_COMPANY, profile.getPartialDataAvailable());
    }

    @Test
    void createEnglandWalesIndustrialAndProvidentCompanyAndVerifyPartialData() {
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyType(CompanyType.INDUSTRIAL_AND_PROVIDENT_SOCIETY);
        spec.setSubType(null);

        ArgumentCaptor<CompanyProfile> companyProfileCaptor = createCompanyProfile();

        CompanyProfile profile = companyProfileCaptor.getValue();
        assertEquals(COMPANY_TYPE_INDUSTRIAL_AND_PROVIDENT_SOCIETY.getValue(), profile.getType());
        assertEquals(FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY_MUTUALS_PUBLIC_REGISTER, profile.getPartialDataAvailable());
    }

    @Test
    void createNortherIrelandIndustrialAndProvidentCompanyAndVerifyPartialData() {
        spec.setJurisdiction(Jurisdiction.NI);
        spec.setCompanyType(CompanyType.INDUSTRIAL_AND_PROVIDENT_SOCIETY);
        spec.setSubType(null);

        ArgumentCaptor<CompanyProfile> companyProfileCaptor = createCompanyProfile();

        CompanyProfile profile = companyProfileCaptor.getValue();
        assertEquals(COMPANY_TYPE_INDUSTRIAL_AND_PROVIDENT_SOCIETY.getValue(), profile.getType());
        assertEquals(FULL_DATA_AVAILABLE_FROM_DEPARTMENT_OF_THE_ECONOMY, profile.getPartialDataAvailable());
    }

    @Test
    void createLtdCompanyAndVerifyNoPartialData() {
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyType(COMPANY_TYPE_LTD);
        spec.setSubType(null);

        ArgumentCaptor<CompanyProfile> companyProfileCaptor = createCompanyProfile();

        CompanyProfile profile = companyProfileCaptor.getValue();
        assertEquals(COMPANY_TYPE_LTD.getValue(), profile.getType());
        assertNull(profile.getPartialDataAvailable());
    }

    private ArgumentCaptor<CompanyProfile> createCompanyProfile() {
        Address mockRegisteredAddress = new Address("", "", "", "", "", "");
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(savedProfile);
        when(addressService.getAddress(spec.getJurisdiction())).thenReturn(mockRegisteredAddress);

        CompanyProfile returnedProfile = this.companyProfileService.create(spec);
        assertEquals(savedProfile, returnedProfile);
        ArgumentCaptor<CompanyProfile> companyProfileCaptor = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(companyProfileCaptor.capture());
        return companyProfileCaptor;
    }

    @Test
    void createCompanyWithEmptyJurisdiction() {
        setCompanyJurisdictionAndType(null,CompanyType.UK_ESTABLISHMENT);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(savedProfile);
        companyProfileService.create(spec);
        ArgumentCaptor<CompanyProfile> captor = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(captor.capture());
        var profile = captor.getValue();
        assertCreatedProfile(profile, COMPANY_STATUS_ACTIVE, null, CompanyType.UK_ESTABLISHMENT.getValue(), false);
        assertEquals(COMPANY_NUMBER, profile.getCompanyNumber());
        assertNotNull(profile.getLinks().getSelf());
        assertNull(profile.getLinks().getFilingHistory());
        assertNull(profile.getLinks().getOfficers());
        assertNull(profile.getLinks().getPersonsWithSignificantControlStatement());
    }

    @Test
    void createCompanyWithJurisdictionAndCompanyType() {
        setCompanyJurisdictionAndType(Jurisdiction.ENGLAND_WALES,COMPANY_TYPE_LTD);
        CompanyProfile profile = createAndCapture(spec);
        assertEquals(COMPANY_NUMBER, profile.getCompanyNumber());
        assertNotNull(profile.getLinks().getSelf());
        assertNotNull(profile.getLinks().getFilingHistory());
        assertNotNull(profile.getLinks().getOfficers());
        assertNotNull(profile.getLinks().getPersonsWithSignificantControlStatement());
    }

    @Test
    void createCompanyWithJurisdictionAndNoCompanyType() {
        setCompanyJurisdictionAndType(Jurisdiction.ENGLAND_WALES,null);
        CompanyProfile profile = createAndCapture(spec);
        assertEquals(COMPANY_NUMBER, profile.getCompanyNumber());
        assertNotNull(profile.getLinks().getSelf());
        assertNotNull(profile.getLinks().getFilingHistory());
        assertNotNull(profile.getLinks().getOfficers());
        assertNotNull(profile.getLinks().getPersonsWithSignificantControlStatement());
    }

    @Test
    void createCompanyWithNoJurisdictionAndNoCompanyType() {
        setCompanyJurisdictionAndType(null,null);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(savedProfile);
        companyProfileService.create(spec);
        ArgumentCaptor<CompanyProfile> captor = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(captor.capture());
        var profile = captor.getValue();
        assertEquals(COMPANY_NUMBER, profile.getCompanyNumber());
        assertNotNull(profile.getLinks().getSelf());
        assertNull(profile.getLinks().getFilingHistory());
        assertNull(profile.getLinks().getOfficers());
        assertNull(profile.getLinks().getPersonsWithSignificantControlStatement());
    }

    @Test
    void testGetCompanyNumberPrefixEnglandWales() {
        setCompanyJurisdictionAndType(Jurisdiction.ENGLAND_WALES,CompanyType.LTD);
        String prefix = spec.getJurisdiction().getCompanyNumberPrefix(spec);
        assertEquals("", prefix);
    }

    @Test
    void testGetCompanyNumberPrefixScotland() {
        setCompanyJurisdictionAndType(Jurisdiction.SCOTLAND,CompanyType.INDUSTRIAL_AND_PROVIDENT_SOCIETY);
        String prefix = spec.getJurisdiction().getCompanyNumberPrefix(spec);
        assertEquals("SP", prefix);
    }

    @Test
    void testGetCompanyNumberPrefixNorthernIreland() {
        setCompanyJurisdictionAndType(Jurisdiction.NI,CompanyType.LLP);
        String prefix = spec.getJurisdiction().getCompanyNumberPrefix(spec);
        assertEquals("NC", prefix);
    }

    @Test
    void testGetCompanyNumberPrefixUnitedKingdom() {
        setCompanyJurisdictionAndType(Jurisdiction.UNITED_KINGDOM,CompanyType.OVERSEA_COMPANY);
        String prefix = spec.getJurisdiction().getCompanyNumberPrefix(spec);
        assertEquals("FC", prefix);
    }

    @Test
    void testGetCompanyNumberPrefixSpecialTypes() {
        setCompanyJurisdictionAndType(Jurisdiction.ENGLAND_WALES,CompanyType.ROYAL_CHARTER);
        String prefix = spec.getJurisdiction().getCompanyNumberPrefix(spec);
        assertEquals("RC", prefix);

        spec.setCompanyType(CompanyType.UK_ESTABLISHMENT);
        prefix = spec.getJurisdiction().getCompanyNumberPrefix(spec);
        assertEquals("BR", prefix);

        spec.setCompanyType(CompanyType.REGISTERED_SOCIETY_NON_JURISDICTIONAL);
        prefix = spec.getJurisdiction().getCompanyNumberPrefix(spec);
        assertEquals("RS", prefix);
    }

    @Test
    void testGetCompanyNumberPrefixNoCompanyType() {
        setCompanyJurisdictionAndType(Jurisdiction.ENGLAND_WALES,null);
        String prefix = spec.getJurisdiction().getCompanyNumberPrefix(spec);
        assertEquals("", prefix);
    }

    @Test
    void testGetCompanyNumberPrefixNoJurisdiction() {
        spec.setCompanyType(CompanyType.UK_ESTABLISHMENT);
        String prefix = spec.getJurisdiction().getCompanyNumberPrefix(spec);
        assertEquals("BR", prefix);
    }

    @Test
    void testCreateOverseasEntityWithOutType() {
        when(addressService.getAddress(overseasSpec.getJurisdiction()))
                .thenReturn(new Address("", "", "", "", "", ""));
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(savedProfile);

        CompanyProfile result = companyProfileService.create(overseasSpec);
        ArgumentCaptor<CompanyProfile> captor = ArgumentCaptor.forClass(CompanyProfile.class);
        verify(repository).save(captor.capture());
        CompanyProfile savedEntity = captor.getValue();

        assertInstanceOf(OverseasEntity.class, savedEntity, "Expected an instance of OverseasEntity");
        assertEquals(OVERSEAS_COMPANY_NUMBER, result.getId());
        assertEquals(OVERSEAS_COMPANY_NUMBER, result.getCompanyNumber());
        assertEquals("registered", result.getCompanyStatus());
        assertEquals(OVERSEAS_ENTITY_TYPE.getValue(), savedEntity.getType());
    }

    @Test
    void testCompanyExistsWhenCompanyExists() {
        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.of(savedProfile));
        assertTrue(companyProfileService.companyExists(COMPANY_NUMBER));
    }

    @Test
    void testCompanyExistsWhenCompanyDoesNotExist() {
        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.empty());
        assertFalse(companyProfileService.companyExists(COMPANY_NUMBER));
    }

    @Test
    void createCompanyWithNorthernIrelandType() {
        setCompanyJurisdictionAndType(Jurisdiction.NI,CompanyType.NORTHERN_IRELAND);
        CompanyProfile profile = createAndCapture(spec);
        assertEquals("converted-closed", profile.getCompanyStatus());
    }

    @Test
    void createCompanyWithNorthernIrelandOtherType() {
        setCompanyJurisdictionAndType(Jurisdiction.NI,CompanyType.NORTHERN_IRELAND_OTHER);
        CompanyProfile profile = createAndCapture(spec);
        assertEquals("converted-closed", profile.getCompanyStatus());
    }

    @Test
    void createCompanyWithRegisteredOverseasEntityType() {
        setCompanyJurisdictionAndType(Jurisdiction.UNITED_KINGDOM,CompanyType.REGISTERED_OVERSEAS_ENTITY);
        CompanyProfile profile = createAndCapture(spec);
        assertEquals(OVERSEAS_STATUS_REGISTERED, profile.getCompanyStatus());
    }

    @Test
    void createCompanyWithOtherType() {
        setCompanyJurisdictionAndType(Jurisdiction.ENGLAND_WALES,CompanyType.LTD);
        CompanyProfile profile = createAndCapture(spec);
        assertEquals("active", profile.getCompanyStatus());
    }

    private void setCompanyJurisdictionAndType(Jurisdiction jurisdiction, CompanyType companyType) {
        spec.setJurisdiction(jurisdiction);
        spec.setCompanyType(companyType);
    }
}