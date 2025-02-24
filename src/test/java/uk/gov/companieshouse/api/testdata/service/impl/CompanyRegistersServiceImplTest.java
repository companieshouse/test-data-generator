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


    @Test
    void testCreateCompanyRegisters() throws DataException {
        setRegister("directors");
        CompanyRegisters createdRegisters = service.create(companySpec);
        assertNotNull(createdRegisters);
        assertEquals("12345678", createdRegisters.getCompanyNumber());
        assertEquals("dummy-etag", createdRegisters.getEtag());
        assertEquals("/company/12345678/registers", createdRegisters.getSelfLink());
        assertEquals("registers", createdRegisters.getKind());
        var now = LocalDate.now();
        assertEquals(now, createdRegisters.getCreatedAt());
        assertEquals(now, createdRegisters.getDeltaAt());
        assertEquals(now, createdRegisters.getUpdatedAt());
        Map<String, Register> registers = createdRegisters.getRegisters();
        assertNotNull(registers);
        assertEquals(1, registers.size());
        assertEquals("directors", registers.get("directors").getRegisterType());
        assertEquals("/company/12345678/officers?register_view=true&register_type=directors",
                registers.get("directors").getLinks().get("directors_register"));
        verify(repository, times(1)).save(any(CompanyRegisters.class));
    }

    @Test
    void testCreateCompanyRegistersWithMultipleRegisters() throws DataException {
        setRegister("directors");
        RegistersSpec secretariesRegister = new RegistersSpec();
        secretariesRegister.setRegisterType("secretaries");
        secretariesRegister.setRegisterMovedTo("Companies House");

        companySpec.setRegisters(List.of(
                companySpec.getRegisters().getFirst(),
                secretariesRegister
        ));

        CompanyRegisters createdRegisters = service.create(companySpec);
        assertEquals(2, createdRegisters.getRegisters().size());
        assertNotNull(createdRegisters.getRegisters().get("directors"));
        assertNotNull(createdRegisters.getRegisters().get("secretaries"));
    }

    @Test
    void testGenerateRegisterLinksForDirectors() throws DataException {
        setRegister("directors");
        CompanyRegisters createdRegisters = service.create(companySpec);
        Map<String, String> links = createdRegisters.getRegisters()
                .get("directors").getLinks();

        assertNotNull(links);
        assertEquals(1, links.size());
        assertEquals("/company/12345678/officers?register_view=true&register_type=directors",
                links.get("directors_register"));
    }

    @Test
    void testGenerateRegisterLinksForSecretaries() throws DataException {
        setRegister("secretaries");
        CompanyRegisters createdRegisters = service.create(companySpec);
        Map<String, String> links = createdRegisters.getRegisters()
                .get("secretaries").getLinks();

        assertNotNull(links);
        assertEquals(1, links.size());
        assertEquals("/company/12345678/officers?register_view=true&register_type=secretaries",
                links.get("secretaries_register"));
    }

    @Test
    void testGenerateRegisterLinksForPsc() throws DataException {
        setRegister("persons-with-significant-control");
        CompanyRegisters createdRegisters = service.create(companySpec);
        Map<String, String> links = createdRegisters.getRegisters()
                .get("persons_with_significant_control").getLinks();

        assertNotNull(links);
        assertEquals(1, links.size());
        assertEquals("/company/12345678/persons-with-significant-control?register_view=true",
                links.get("persons_with_significant_control_register"));
    }

    @Test
    void testGenerateRegisterLinksForNull() throws DataException {
        setRegister("members");
        CompanyRegisters createdRegisters = service.create(companySpec);
        Map<String, String> links = createdRegisters.getRegisters()
                .get("members").getLinks();
        assertNull(links);
    }

    @Test
    void testCreateWithNoRegisters() throws DataException {
        setRegister("directors");
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
        setCompanySpec("", "public_register");
        DataException exception = assertThrows(
                DataException.class, () -> service.create(companySpec));
        assertEquals("Register type must be provided", exception.getMessage());
    }

    @Test
    void testCreateCompanyRegistersWithNullRegisterMovedTo() {
        setCompanySpec("directors", null);
        DataException exception = assertThrows(
                DataException.class, () -> service.create(companySpec));
        assertEquals("Register moved to must be provided", exception.getMessage());
    }

    @Test
    void testCreateCompanyRegistersWithBlankRegisterMovedTo() {
        setCompanySpec("members", "");
        DataException exception = assertThrows(
                DataException.class, () -> service.create(companySpec));
        assertEquals("Register moved to must be provided", exception.getMessage());
    }

    @Test
    void testDeletedCompanyRegistersExists() {
        CompanyRegisters mockRegister = new CompanyRegisters();
        mockRegister.setCompanyNumber("12345678");
        when(repository.deleteByCompanyNumber("12345678")).thenReturn(Optional.of(mockRegister));

        boolean deleted = service.delete("12345678");

        assertTrue(deleted);
        verify(repository, times(1)).deleteByCompanyNumber("12345678");
    }

    @Test
    void testDeletedCompanyRegistersNotExists() {
        when(repository.deleteByCompanyNumber("99999999")).thenReturn(Optional.empty());
        boolean deleted = service.delete("99999999");
        assertFalse(deleted);
        verify(repository, times(1)).deleteByCompanyNumber("99999999");
    }

    @Test
    void testDeletedCompanyRegistersThrowsException() {
        when(repository.deleteByCompanyNumber("12345678"))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class, () -> service.delete("12345678"));
        assertEquals("Database error", exception.getMessage());
        verify(repository, times(1)).deleteByCompanyNumber("12345678");
    }

    private void setRegister(String registerType) {
        setCompanySpec(registerType, "Companies House");
        when(randomService.getEtag()).thenReturn("dummy-etag");
        when(repository.save(any(CompanyRegisters.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void setCompanySpec(String registerType, String registerMovedTo) {
        companySpec = new CompanySpec();
        companySpec.setCompanyNumber("12345678");
        RegistersSpec register = new RegistersSpec();
        register.setRegisterType(registerType);
        register.setRegisterMovedTo(registerMovedTo);
        companySpec.setRegisters(List.of(register));
    }
}
