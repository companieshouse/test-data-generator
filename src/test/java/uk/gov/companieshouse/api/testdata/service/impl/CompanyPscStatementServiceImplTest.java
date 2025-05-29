package uk.gov.companieshouse.api.testdata.service.impl;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Spy;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyType;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscStatementRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class CompanyPscStatementServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String ENCODED_VALUE = "abc123def456";
    private static final String ETAG = "etag";
    private static final String PSC_STATEMENT_1 = "no-individual-or-entity-with-signficant-control";
    private static final String PSC_STATEMENT_2 = "psc-exists-but-not-identified";
    private static final String PSC_STATEMENT_3 = "all-beneficial-owners-identified";

    @Mock
    private RandomService randomService;
    @Mock
    private CompanyPscStatementRepository repository;

    @Spy
    @InjectMocks
    private CompanyPscStatementServiceImpl companyPscStatementService;


    @Test
    void create() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEtag()).thenReturn(ETAG);
        CompanyPscStatement savedStatement = new CompanyPscStatement();
        when(this.repository.save(any())).thenReturn(savedStatement);

        CompanyPscStatement returnedStatement = this.companyPscStatementService.create(spec);

        assertEquals(savedStatement, returnedStatement);
        ArgumentCaptor<CompanyPscStatement> statementCaptor = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository).save(statementCaptor.capture());
        CompanyPscStatement capturedStatement = statementCaptor.getValue();

        assertEquals(ENCODED_VALUE, capturedStatement.getId());
        assertEquals(ENCODED_VALUE, capturedStatement.getPscStatementId());
        assertEquals(COMPANY_NUMBER, capturedStatement.getCompanyNumber());
        assertEquals(ETAG, capturedStatement.getEtag());
        assertEquals("persons-with-significant-control-statement", capturedStatement.getKind());
        assertNotNull(capturedStatement.getUpdatedAt());
        assertNotNull(capturedStatement.getCreatedAt());
        assertNotNull(capturedStatement.getNotifiedOn());

        Links links = capturedStatement.getLinks();
        assertNotNull(links);
        assertEquals("/company/" + COMPANY_NUMBER + "/persons-with-significant-control-statements/" + ENCODED_VALUE, links.getSelf());

        assertEquals(PSC_STATEMENT_1, capturedStatement.getStatement());
    }

    @Test
    void createWhenCompanyTypeIsRegisteredOverseasEntity() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.REGISTERED_OVERSEAS_ENTITY);
        spec.setNumberOfPsc(1);

        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEtag()).thenReturn(ETAG);
        CompanyPscStatement savedStatement = new CompanyPscStatement();
        when(this.repository.save(any())).thenReturn(savedStatement);

        CompanyPscStatement returnedStatement = this.companyPscStatementService.create(spec);

        assertEquals(savedStatement, returnedStatement);
        ArgumentCaptor<CompanyPscStatement> statementCaptor = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository).save(statementCaptor.capture());
        CompanyPscStatement capturedStatement = statementCaptor.getValue();
        assertEquals(PSC_STATEMENT_3, capturedStatement.getStatement());
    }

    @Test
    void createWhenCompanyTypeIsOverseaCompanyWithZeroPsc() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.OVERSEA_COMPANY);
        spec.setNumberOfPsc(0);
        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEtag()).thenReturn(ETAG);
        CompanyPscStatement savedStatement = new CompanyPscStatement();
        when(this.repository.save(any())).thenReturn(savedStatement);

        CompanyPscStatement returnedStatement = this.companyPscStatementService.create(spec);

        assertEquals(savedStatement, returnedStatement);
        ArgumentCaptor<CompanyPscStatement> statementCaptor = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository).save(statementCaptor.capture());
        CompanyPscStatement capturedStatement = statementCaptor.getValue();
        assertEquals(PSC_STATEMENT_1, capturedStatement.getStatement());
    }

    @Test
    void createWhenOtherCompanyTypeWithPsc() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.LTD);
        spec.setNumberOfPsc(1);

        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEtag()).thenReturn(ETAG);
        CompanyPscStatement savedStatement = new CompanyPscStatement();
        when(this.repository.save(any())).thenReturn(savedStatement);

        CompanyPscStatement returnedStatement = this.companyPscStatementService.create(spec);

        assertEquals(savedStatement, returnedStatement);
        ArgumentCaptor<CompanyPscStatement> statementCaptor = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository).save(statementCaptor.capture());
        CompanyPscStatement capturedStatement = statementCaptor.getValue();
        assertEquals(PSC_STATEMENT_2, capturedStatement.getStatement());
    }

    @Test
    void createWhenOtherCompanyTypeWithoutPsc() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.LTD);
        spec.setNumberOfPsc(0);

        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEtag()).thenReturn(ETAG);
        CompanyPscStatement savedStatement = new CompanyPscStatement();
        when(this.repository.save(any())).thenReturn(savedStatement);

        CompanyPscStatement returnedStatement = this.companyPscStatementService.create(spec);

        assertEquals(savedStatement, returnedStatement);
        ArgumentCaptor<CompanyPscStatement> statementCaptor = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository).save(statementCaptor.capture());
        CompanyPscStatement capturedStatement = statementCaptor.getValue();
        assertEquals(PSC_STATEMENT_1, capturedStatement.getStatement());
    }

    @Test
    void delete() {
        CompanyPscStatement statementToDelete = new CompanyPscStatement();
        List<CompanyPscStatement> statements = List.of(statementToDelete);
        when(repository.findAllByCompanyNumber(COMPANY_NUMBER)).thenReturn(statements);

        assertTrue(companyPscStatementService.delete(COMPANY_NUMBER));
        verify(repository).deleteAll(statements);
    }

    @Test
    void deleteNoDataException() {
        when(repository.findAllByCompanyNumber(COMPANY_NUMBER)).thenReturn(List.of());

        assertFalse(companyPscStatementService.delete(COMPANY_NUMBER));
        verify(repository, never()).deleteAll(any());
    }

    @Test
    void createPscStatements_defaultScenario() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        doReturn(new CompanyPscStatement()).when(companyPscStatementService).create(any(CompanySpec.class));

        List<CompanyPscStatement> result = companyPscStatementService.createPscStatements(spec);

        verify(companyPscStatementService, times(1)).create(spec);
        assertEquals(1, result.size());
    }

    @Test
    void createPscStatements_onlyWithdrawn() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setWithdrawnPscStatements(2);
        spec.setActivePscStatements(0);

        doReturn(new CompanyPscStatement()).when(companyPscStatementService).create(any(CompanySpec.class));

        List<CompanyPscStatement> result = companyPscStatementService.createPscStatements(spec);

        verify(companyPscStatementService, times(2)).create(any(CompanySpec.class));
        assertEquals(2, result.size());

        ArgumentCaptor<CompanySpec> specCaptor = ArgumentCaptor.forClass(CompanySpec.class);
        verify(companyPscStatementService, times(2)).create(specCaptor.capture());

        List<CompanySpec> capturedSpecs = specCaptor.getAllValues();
        assertEquals(1, capturedSpecs.get(0).getWithdrawnPscStatements());
        assertEquals(0, capturedSpecs.get(0).getNumberOfPsc());
        assertEquals(COMPANY_NUMBER, capturedSpecs.get(0).getCompanyNumber());
    }

    @Test
    void createPscStatements_onlyActive() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setWithdrawnPscStatements(0);
        spec.setActivePscStatements(3);

        doReturn(new CompanyPscStatement()).when(companyPscStatementService).create(any(CompanySpec.class));

        List<CompanyPscStatement> result = companyPscStatementService.createPscStatements(spec);

        verify(companyPscStatementService, times(3)).create(any(CompanySpec.class));
        assertEquals(3, result.size());

        ArgumentCaptor<CompanySpec> specCaptor = ArgumentCaptor.forClass(CompanySpec.class);
        verify(companyPscStatementService, times(3)).create(specCaptor.capture());

        List<CompanySpec> capturedSpecs = specCaptor.getAllValues();
        assertEquals(0, capturedSpecs.get(0).getWithdrawnPscStatements());
        assertEquals(1, capturedSpecs.get(0).getNumberOfPsc());
        assertEquals(0, capturedSpecs.get(1).getWithdrawnPscStatements());
        assertEquals(1, capturedSpecs.get(1).getNumberOfPsc());
        assertEquals(0, capturedSpecs.get(2).getWithdrawnPscStatements());
        assertEquals(1, capturedSpecs.get(2).getNumberOfPsc());
        assertEquals(COMPANY_NUMBER, capturedSpecs.get(0).getCompanyNumber());
    }

    @Test
    void createPscStatements_bothActiveAndWithdrawn() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setWithdrawnPscStatements(1);
        spec.setActivePscStatements(2);

        doReturn(new CompanyPscStatement()).when(companyPscStatementService).create(any(CompanySpec.class));

        List<CompanyPscStatement> result = companyPscStatementService.createPscStatements(spec);

        verify(companyPscStatementService, times(3)).create(any(CompanySpec.class));
        assertEquals(3, result.size());

        ArgumentCaptor<CompanySpec> specCaptor = ArgumentCaptor.forClass(CompanySpec.class);
        verify(companyPscStatementService, times(3)).create(specCaptor.capture());

        List<CompanySpec> capturedSpecs = specCaptor.getAllValues();

        assertEquals(1, capturedSpecs.get(0).getWithdrawnPscStatements());
        assertEquals(0, capturedSpecs.get(0).getNumberOfPsc());
        assertEquals(COMPANY_NUMBER, capturedSpecs.get(0).getCompanyNumber());

        assertEquals(0, capturedSpecs.get(1).getWithdrawnPscStatements());
        assertEquals(1, capturedSpecs.get(1).getNumberOfPsc());
        assertEquals(COMPANY_NUMBER, capturedSpecs.get(1).getCompanyNumber());

        assertEquals(0, capturedSpecs.get(2).getWithdrawnPscStatements());
        assertEquals(1, capturedSpecs.get(2).getNumberOfPsc());
        assertEquals(COMPANY_NUMBER, capturedSpecs.get(2).getCompanyNumber());
    }

    @Test
    void testRegisteredOverseasEntityStatement() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.REGISTERED_OVERSEAS_ENTITY);

        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEtag()).thenReturn(ETAG);
        CompanyPscStatement savedStatement = new CompanyPscStatement();
        when(this.repository.save(any())).thenReturn(savedStatement);

        CompanyPscStatement returnedStatement = this.companyPscStatementService.create(spec);

        assertEquals(savedStatement, returnedStatement);
        ArgumentCaptor<CompanyPscStatement> statementCaptor = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository).save(statementCaptor.capture());
        CompanyPscStatement capturedStatement = statementCaptor.getValue();
        assertEquals("persons-with-significant-control-statement", capturedStatement.getKind());
        assertEquals("all-beneficial-owners-identified", capturedStatement.getStatement());
    }

    @Test
    void testNumberOfPscExists() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setNumberOfPsc(5);

        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEtag()).thenReturn(ETAG);
        CompanyPscStatement savedStatement = new CompanyPscStatement();
        when(this.repository.save(any())).thenReturn(savedStatement);

        CompanyPscStatement returnedStatement = this.companyPscStatementService.create(spec);

        assertEquals(savedStatement, returnedStatement);
        ArgumentCaptor<CompanyPscStatement> statementCaptor = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository).save(statementCaptor.capture());
        CompanyPscStatement capturedStatement = statementCaptor.getValue();
        assertEquals("persons-with-significant-control-statement", capturedStatement.getKind());
        assertEquals("psc-exists-but-not-identified", capturedStatement.getStatement());
    }
}