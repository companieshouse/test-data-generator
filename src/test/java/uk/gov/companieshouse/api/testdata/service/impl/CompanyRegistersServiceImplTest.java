package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import uk.gov.companieshouse.api.testdata.model.entity.RegisterItem;
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
        setRegister();
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
        verify(repository, times(1)).save(any(CompanyRegisters.class));
    }

    @Test
    void testCreateCompanyRegistersWithMultipleRegisters() throws DataException {
        setRegister();
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
    void testGenerateRegisterLinks () throws DataException {
        setRegister();
        CompanyRegisters createdRegisters = service.create(companySpec);
        Map<String, String> links = createdRegisters.getRegisters()
                .get("directors").getLinks();

        assertNotNull(links);
        assertEquals(1, links.size());
        assertEquals("/company/12345678/officers?register_view=true&register_type=directors",
                links.get("directors_register"));
    }

    @Test
    void testDeleteCompanyRegistersExists() {
        CompanyRegisters mockRegister = new CompanyRegisters();
        mockRegister.setCompanyNumber("12345678");
        when(repository.deleteByCompanyNumber("12345678")).thenReturn(Optional.of(mockRegister));

        boolean deleted = service.delete("12345678");

        assertTrue(deleted);
        verify(repository, times(1)).deleteByCompanyNumber("12345678");
    }

    @Test
    void testDeleteCompanyRegistersNotExists() {
        when(repository.deleteByCompanyNumber("99999999")).thenReturn(Optional.empty());
        boolean deleted = service.delete("99999999");
        assertFalse(deleted);
        verify(repository, times(1)).deleteByCompanyNumber("99999999");
    }

    @Test
    void testCreateWithNoRegisters() throws DataException {
        setRegister();
        companySpec.setRegisters(Collections.emptyList());
        CompanyRegisters createdRegisters = service.create(companySpec);
        assertNotNull(createdRegisters);
        assertTrue(createdRegisters.getRegisters().isEmpty());
        verify(repository, times(1)).save(any(CompanyRegisters.class));
    }

    private void setRegister() {
        companySpec = new CompanySpec();
        companySpec.setCompanyNumber("12345678");
        RegistersSpec directorsRegister = new RegistersSpec();
        directorsRegister.setRegisterType("directors");
        directorsRegister.setRegisterMovedTo("Companies House");
        companySpec.setRegisters(List.of(directorsRegister));
        when(randomService.getEtag()).thenReturn("dummy-etag");
        when(repository.save(any(CompanyRegisters.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }
}
