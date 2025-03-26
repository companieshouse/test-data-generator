package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyRegisters;
import uk.gov.companieshouse.api.testdata.model.entity.Register;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.RegistersSpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyRegistersRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class CompanyRegistersServiceImplTest {

    @Mock
    private RandomService randomService;

    @Mock
    private CompanyRegistersRepository repository;

    @InjectMocks
    private CompanyRegistersServiceImpl service;

    private CompanySpec companySpec;

    private static final String DIRECTORS_TEXT = "directors";
    private static final String DIRECTORS_REGISTER_TYPE = "directors_register";
    private static final String SECRETARIES_TEXT = "secretaries";
    private static final String SECRETARIES_REGISTER_TYPE = "secretaries_register";
    private static final String PSC_TEXT = "persons-with-significant-control";
    private static final String MEMBERS_TEXT = "members";
    private static final String PSC_REGISTER_TYPE = "persons_with_significant_control_register";
    private static final String PSC_REGISTER_STRING = "persons_with_significant_control";
    private static final String REGISTERS_TEXT = "registers";
    private static final String REGISTERS_MOVE_TO_PUBLIC_REGISTER = "public_register";
    private static final String UNSPECIFIED_LOCATION = "unspecified-location";
    private static final String COMPANY_NUMBER = "12345678";

    @Test
    void testCreateCompanyRegisters() throws DataException {
        setRegister(DIRECTORS_TEXT);
        CompanyRegisters createdRegisters = service.create(companySpec);
        assertNotNull(createdRegisters);
        assertEquals(COMPANY_NUMBER, createdRegisters.getId());
        assertEquals(COMPANY_NUMBER, createdRegisters.getCompanyNumber());
        assertEquals("dummy-etag", createdRegisters.getEtag());
        assertEquals("/company/" + COMPANY_NUMBER + "/registers", createdRegisters.getSelfLink());
        assertEquals(REGISTERS_TEXT, createdRegisters.getKind());
        var now = LocalDate.now();
        assertEquals(now, createdRegisters.getCreatedAt());
        assertEquals(now, createdRegisters.getDeltaAt());
        assertEquals(now, createdRegisters.getUpdatedAt());
        Map<String, Register> registers = createdRegisters.getRegisters();
        assertNotNull(registers);
        assertEquals(1, registers.size());
        assertEquals(DIRECTORS_TEXT, registers.get(DIRECTORS_TEXT).getRegisterType());
        assertEquals("/company/" + COMPANY_NUMBER
                        + "/officers?register_view=true&register_type=directors",
                registers.get(DIRECTORS_TEXT).getLinks().get(DIRECTORS_REGISTER_TYPE));
        verify(repository, times(1)).save(any(CompanyRegisters.class));
    }

    @Test
    void testCreateCompanyRegistersWithMultipleRegisters() throws DataException {
        setRegister(DIRECTORS_TEXT);
        RegistersSpec secretariesRegister = new RegistersSpec();
        secretariesRegister.setRegisterType("secretaries");
        secretariesRegister.setRegisterMovedTo("Companies House");

        companySpec.setRegisters(List.of(
                companySpec.getRegisters().getFirst(),
                secretariesRegister
        ));

        CompanyRegisters createdRegisters = service.create(companySpec);
        assertEquals(2, createdRegisters.getRegisters().size());
        assertNotNull(createdRegisters.getRegisters().get(DIRECTORS_TEXT));
        assertNotNull(createdRegisters.getRegisters().get(SECRETARIES_TEXT));
    }

    @Test
    void testGenerateRegisterLinksForDirectors() throws DataException {
        setRegister(DIRECTORS_TEXT);
        CompanyRegisters createdRegisters = service.create(companySpec);
        Map<String, String> links = createdRegisters.getRegisters()
                .get(DIRECTORS_TEXT).getLinks();

        assertNotNull(links);
        assertEquals(1, links.size());
        assertEquals("/company/" + COMPANY_NUMBER
                        + "/officers?register_view=true&register_type=directors",
                links.get(DIRECTORS_REGISTER_TYPE));
    }

    @Test
    void testGenerateRegisterLinksForSecretaries() throws DataException {
        setRegister(SECRETARIES_TEXT);
        CompanyRegisters createdRegisters = service.create(companySpec);
        Map<String, String> links = createdRegisters.getRegisters()
                .get(SECRETARIES_TEXT).getLinks();

        assertNotNull(links);
        assertEquals(1, links.size());
        assertEquals("/company/" + COMPANY_NUMBER
                        + "/officers?register_view=true&register_type=secretaries",
                links.get(SECRETARIES_REGISTER_TYPE));
    }

    @Test
    void testGenerateRegisterLinksForPsc() throws DataException {
        setRegister(PSC_TEXT);
        CompanyRegisters createdRegisters = service.create(companySpec);
        Map<String, String> links = createdRegisters.getRegisters()
                .get(PSC_REGISTER_STRING).getLinks();

        assertNotNull(links);
        assertEquals(1, links.size());
        assertEquals("/company/" + COMPANY_NUMBER
                        + "/persons-with-significant-control?register_view=true",
                links.get(PSC_REGISTER_TYPE));
    }

    @Test
    void testGenerateRegisterLinksForNull() throws DataException {
        setRegister(MEMBERS_TEXT);
        CompanyRegisters createdRegisters = service.create(companySpec);
        Map<String, String> links = createdRegisters.getRegisters()
                .get(MEMBERS_TEXT).getLinks();
        assertNull(links);
    }

    @Test
    void testCreateWithNoRegisters() throws DataException {
        setRegister(DIRECTORS_TEXT);
        companySpec.setRegisters(Collections.emptyList());
        CompanyRegisters createdRegisters = service.create(companySpec);
        assertNotNull(createdRegisters);
        assertTrue(createdRegisters.getRegisters().isEmpty());
        verify(repository, times(1)).save(any(CompanyRegisters.class));
    }

    @Test
    void testCreateCompanyRegistersWithNullRegisterType() {
        setCompanySpec(null, "Companies House");
        DataException exception = assertThrows(
                DataException.class, () -> service.create(companySpec));
        assertEquals("Register type must be provided", exception.getMessage());
    }

    @Test
    void testCreateCompanyRegistersWithBlankRegisterType() {
        setCompanySpec("", REGISTERS_MOVE_TO_PUBLIC_REGISTER);
        DataException exception = assertThrows(
                DataException.class, () -> service.create(companySpec));
        assertEquals("Register type must be provided", exception.getMessage());
    }

    @Test
    void testCreateCompanyRegistersWithNullRegisterMovedTo() {
        setCompanySpec(DIRECTORS_TEXT, null);
        DataException exception = assertThrows(
                DataException.class, () -> service.create(companySpec));
        assertEquals("Register moved to must be provided", exception.getMessage());
    }

    @Test
    void testCreateCompanyRegistersWithBlankRegisterMovedTo() {
        setCompanySpec(MEMBERS_TEXT, "");
        DataException exception = assertThrows(
                DataException.class, () -> service.create(companySpec));
        assertEquals("Register moved to must be provided", exception.getMessage());
    }

    @Test
    void testDeletedCompanyRegistersExists() {
        CompanyRegisters mockRegister = new CompanyRegisters();
        mockRegister.setCompanyNumber(COMPANY_NUMBER);
        when(repository.deleteByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(Optional.of(mockRegister));
        boolean deleted = service.delete(COMPANY_NUMBER);
        assertTrue(deleted);
        verify(repository, times(1)).deleteByCompanyNumber(COMPANY_NUMBER);
    }

    @Test
    void testDeletedCompanyRegistersNotExists() {
        when(repository.deleteByCompanyNumber(COMPANY_NUMBER)).thenReturn(Optional.empty());
        boolean deleted = service.delete(COMPANY_NUMBER);
        assertFalse(deleted);
        verify(repository, times(1)).deleteByCompanyNumber(COMPANY_NUMBER);
    }

    @Test
    void testDeletedCompanyRegistersThrowsException() {
        when(repository.deleteByCompanyNumber(COMPANY_NUMBER))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> service.delete(COMPANY_NUMBER));
        assertEquals("Database error", exception.getMessage());
        verify(repository, times(1)).deleteByCompanyNumber(COMPANY_NUMBER);
    }

    @Test
    void testGenerateRegisterLinksForUnspecifiedLocation() throws DataException {
        setCompanySpec(DIRECTORS_TEXT, UNSPECIFIED_LOCATION);
        when(randomService.getEtag()).thenReturn("dummy-etag");
        when(repository.save(any(CompanyRegisters.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        CompanyRegisters createdRegisters = service.create(companySpec);
        Map<String, String> links = createdRegisters.getRegisters()
                .get(DIRECTORS_TEXT).getLinks();
        assertNull(links);
    }

    private void setRegister(String registerType) {
        setCompanySpec(registerType, "Companies House");
        when(randomService.getEtag()).thenReturn("dummy-etag");
        when(repository.save(any(CompanyRegisters.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void setCompanySpec(String registerType, String registerMovedTo) {
        companySpec = new CompanySpec();
        companySpec.setCompanyNumber(COMPANY_NUMBER);
        RegistersSpec register = new RegistersSpec();
        register.setRegisterType(registerType);
        register.setRegisterMovedTo(registerMovedTo);
        companySpec.setRegisters(List.of(register));
    }
}
