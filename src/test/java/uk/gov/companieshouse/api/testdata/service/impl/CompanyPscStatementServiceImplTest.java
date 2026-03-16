package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.enums.CompanyType;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscStatementRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class CompanyPscStatementServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String ENCODED_VALUE = "abc123def456";
    private static final String ETAG = "etag";
    private static final String PSC_STATEMENT_2 = "no-individual-or-entity-with-signficant-control";
    private static final String PSC_STATEMENT_3 = "all-beneficial-owners-identified";
    private static final String PSC_STATEMENT_4 = "psc-exists-but-not-identified";
    private static final String PSC_ID = "PSC1234567";
    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    @Mock
    private CompanyPscStatementRepository repository;

    @Mock
    private RandomService randomService;

    @Spy
    @InjectMocks
    private CompanyPscStatementServiceImpl companyPscStatementService;

    public CompanyRequest spec;

    @BeforeEach
    void setUp() {
        spec = new CompanyRequest();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyWithPopulatedStructureOnly(false);
    }

    @Test
    void create() {
        spec.setWithdrawnStatements(0);
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
        assertNotNull(capturedStatement.getNotifiedOn());

        Links links = capturedStatement.getLinks();
        assertNotNull(links);
        assertEquals("/company/" + COMPANY_NUMBER + "/persons-with-significant-control-statements/" + ENCODED_VALUE, links.getSelf());

        assertEquals(PSC_STATEMENT_2, capturedStatement.getStatement());
    }

    @Test
    void createCompanyPscStatement_registeredOverseasEntity() {
        when(randomService.getEncodedIdWithSalt(any(Integer.class), any(Integer.class)))
                .thenReturn(PSC_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any(CompanyPscStatement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        spec.setCompanyType(CompanyType.REGISTERED_OVERSEAS_ENTITY);

        CompanyPscStatement pscStatement = companyPscStatementService.create(spec);

        ArgumentCaptor<CompanyPscStatement> captor = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository, times(1)).save(captor.capture());
        CompanyPscStatement capturedStatement = captor.getValue();

        assertEquals(PSC_ID, pscStatement.getId());
        assertEquals(PSC_ID, pscStatement.getPscStatementId());
        assertEquals(COMPANY_NUMBER, pscStatement.getCompanyNumber());
        assertEquals(ETAG, pscStatement.getEtag());
        assertEquals("persons-with-significant-control-statement", pscStatement.getKind());
        assertEquals("/company/" + COMPANY_NUMBER + "/persons-with-significant-control-statements/" + PSC_ID, pscStatement.getLinks().getSelf());
        assertEquals(CompanyPscStatementServiceImpl.PscStatement.ALL_BENEFICIAL_OWNERS_IDENTIFIED.getStatement(), pscStatement.getStatement());
        assertNull(pscStatement.getCeasedOn());
        assertNotNull(pscStatement.getNotifiedOn());
        assertNotNull(pscStatement.getCreatedAt());
        assertNotNull(pscStatement.getUpdatedAt());

        assertEquals(pscStatement, capturedStatement);
    }

    @Test
    void createCompanyPscStatement_hasSuperSecurePscs() {
        when(randomService.getEncodedIdWithSalt(any(Integer.class), any(Integer.class)))
                .thenReturn(PSC_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any(CompanyPscStatement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        spec.setCompanyType(CompanyType.LTD);
        spec.setHasSuperSecurePscs(true);

        CompanyPscStatement pscStatement = companyPscStatementService.create(spec);

        ArgumentCaptor<CompanyPscStatement> captor = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository, times(1)).save(captor.capture());
        CompanyPscStatement capturedStatement = captor.getValue();

        assertEquals(CompanyPscStatementServiceImpl.PscStatement.PSC_EXISTS_BUT_NOT_IDENTIFIED.getStatement(), pscStatement.getStatement());
        assertNull(pscStatement.getCeasedOn());
        assertEquals(pscStatement, capturedStatement);
    }

    @Test
    void createCompanyPscStatement_pscActiveIsNull() {
        when(randomService.getEncodedIdWithSalt(any(Integer.class), any(Integer.class)))
                .thenReturn(PSC_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any(CompanyPscStatement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        spec.setCompanyType(CompanyType.LTD);
        spec.setHasSuperSecurePscs(false);
        spec.setWithdrawnStatements(0);
        spec.setPscActive(null);

        CompanyPscStatement pscStatement = companyPscStatementService.create(spec);

        ArgumentCaptor<CompanyPscStatement> captor = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository, times(1)).save(captor.capture());
        CompanyPscStatement capturedStatement = captor.getValue();

        assertEquals(PSC_STATEMENT_2, pscStatement.getStatement());
        assertNull(pscStatement.getCeasedOn());
        assertEquals(pscStatement, capturedStatement);
    }

    @Test
    void createCompanyPscStatement_pscActiveIsTrue() {
        when(randomService.getEncodedIdWithSalt(any(Integer.class), any(Integer.class)))
                .thenReturn(PSC_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any(CompanyPscStatement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        spec.setCompanyType(CompanyType.LTD);
        spec.setHasSuperSecurePscs(false);
        spec.setPscActive(true);

        CompanyPscStatement pscStatement = companyPscStatementService.create(spec);

        ArgumentCaptor<CompanyPscStatement> captor = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository, times(1)).save(captor.capture());
        CompanyPscStatement capturedStatement = captor.getValue();

        assertEquals(CompanyPscStatementServiceImpl.PscStatement.PSC_EXISTS_BUT_NOT_IDENTIFIED.getStatement(), pscStatement.getStatement());
        assertNull(pscStatement.getCeasedOn());
        assertEquals(pscStatement, capturedStatement);
    }

    @Test
    void createCompanyPscStatement_pscActiveIsFalseAndWithdrawnStatementsGreaterThanZero() {
        when(randomService.getEncodedIdWithSalt(any(Integer.class), any(Integer.class)))
                .thenReturn(PSC_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any(CompanyPscStatement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        spec.setCompanyType(CompanyType.LTD);
        spec.setHasSuperSecurePscs(false);
        spec.setPscActive(false);
        spec.setWithdrawnStatements(1);

        CompanyPscStatement pscStatement = companyPscStatementService.create(spec);

        ArgumentCaptor<CompanyPscStatement> captor = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository, times(1)).save(captor.capture());
        CompanyPscStatement capturedStatement = captor.getValue();

        assertEquals(CompanyPscStatementServiceImpl.PscStatement.BENEFICIAL_ACTIVE_OR_CEASED.getStatement(), pscStatement.getStatement());
        assertNotNull(pscStatement.getCeasedOn());
        assertEquals(LocalDate.now().minusDays(30).atStartOfDay(ZONE_ID_UTC).toInstant().truncatedTo(ChronoUnit.SECONDS),
                pscStatement.getCeasedOn().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(pscStatement, capturedStatement);
    }

    @Test
    void createCompanyPscStatement_defaultCase() {
        when(randomService.getEncodedIdWithSalt(any(Integer.class), any(Integer.class)))
                .thenReturn(PSC_ID);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any(CompanyPscStatement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        spec.setCompanyType(CompanyType.LTD);
        spec.setHasSuperSecurePscs(false);
        spec.setPscActive(true);
        spec.setWithdrawnStatements(0);

        CompanyPscStatement pscStatement = companyPscStatementService.create(spec);

        ArgumentCaptor<CompanyPscStatement> captor = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository, times(1)).save(captor.capture());
        CompanyPscStatement capturedStatement = captor.getValue();

        assertEquals(PSC_STATEMENT_4, pscStatement.getStatement());
        assertNull(pscStatement.getCeasedOn());
        assertEquals(pscStatement, capturedStatement);
    }

    @Test
    void createWhenCompanyTypeIsRegisteredOverseasEntity() {
        spec.setCompanyType(CompanyType.REGISTERED_OVERSEAS_ENTITY);
        spec.setNumberOfPscs(1);

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
        spec.setCompanyType(CompanyType.OVERSEA_COMPANY);
        spec.setNumberOfPscs(0);
        spec.setWithdrawnStatements(0);
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
    void createWhenOtherCompanyTypeWithPsc() {
        spec.setCompanyType(CompanyType.LTD);
        spec.setNumberOfPscs(1);
        spec.setWithdrawnStatements(0);

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
        spec.setCompanyType(CompanyType.LTD);
        spec.setNumberOfPscs(0);
        spec.setWithdrawnStatements(0);

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
        List<CompanyPscStatement> result = companyPscStatementService.createPscStatements(spec);

        verify(companyPscStatementService, never()).create(any(CompanyRequest.class));
        assertTrue(result.isEmpty());
    }

    @Test
    void createPscStatements_onlyWithdrawn() {
        spec.setWithdrawnStatements(2);
        spec.setActiveStatements(0);

        doReturn(new CompanyPscStatement()).when(companyPscStatementService).create(any(CompanyRequest.class));

        List<CompanyPscStatement> result = companyPscStatementService.createPscStatements(spec);

        verify(companyPscStatementService, times(2)).create(any(CompanyRequest.class));
        assertEquals(2, result.size());

        ArgumentCaptor<CompanyRequest> specCaptor = ArgumentCaptor.forClass(CompanyRequest.class);
        verify(companyPscStatementService, times(2)).create(specCaptor.capture());

        List<CompanyRequest> capturedSpecs = specCaptor.getAllValues();
        assertEquals(1, capturedSpecs.get(0).getWithdrawnStatements());
        assertEquals(0, capturedSpecs.get(0).getNumberOfPscs());
        assertEquals(COMPANY_NUMBER, capturedSpecs.get(0).getCompanyNumber());

        assertEquals(1, capturedSpecs.get(1).getWithdrawnStatements());
        assertEquals(0, capturedSpecs.get(1).getNumberOfPscs());
        assertEquals(COMPANY_NUMBER, capturedSpecs.get(1).getCompanyNumber());
    }

    @Test
    void createPscStatements_onlyActive() {
        spec.setWithdrawnStatements(0);
        spec.setActiveStatements(3);

        doReturn(new CompanyPscStatement()).when(companyPscStatementService).create(any(CompanyRequest.class));

        List<CompanyPscStatement> result = companyPscStatementService.createPscStatements(spec);

        verify(companyPscStatementService, times(3)).create(any(CompanyRequest.class));
        assertEquals(3, result.size());

        ArgumentCaptor<CompanyRequest> specCaptor = ArgumentCaptor.forClass(CompanyRequest.class);
        verify(companyPscStatementService, times(3)).create(specCaptor.capture());

        List<CompanyRequest> capturedSpecs = specCaptor.getAllValues();
        assertEquals(0, capturedSpecs.get(0).getWithdrawnStatements());
        assertEquals(0, capturedSpecs.get(1).getWithdrawnStatements());
        assertEquals(1, capturedSpecs.get(1).getNumberOfPscs());
        assertEquals(0, capturedSpecs.get(2).getWithdrawnStatements());
        assertEquals(1, capturedSpecs.get(2).getNumberOfPscs());
        assertEquals(COMPANY_NUMBER, capturedSpecs.get(0).getCompanyNumber());
    }

    @Test
    void createPscStatements_bothActiveAndWithdrawn() {
        spec.setWithdrawnStatements(1);
        spec.setActiveStatements(2);

        doReturn(new CompanyPscStatement()).when(companyPscStatementService).create(any(CompanyRequest.class));

        List<CompanyPscStatement> result = companyPscStatementService.createPscStatements(spec);

        verify(companyPscStatementService, times(3)).create(any(CompanyRequest.class));
        assertEquals(3, result.size());

        ArgumentCaptor<CompanyRequest> specCaptor = ArgumentCaptor.forClass(CompanyRequest.class);
        verify(companyPscStatementService, times(3)).create(specCaptor.capture());

        List<CompanyRequest> capturedSpecs = specCaptor.getAllValues();

        assertEquals(1, capturedSpecs.get(0).getWithdrawnStatements());
        assertEquals(0, capturedSpecs.get(0).getNumberOfPscs());
        assertEquals(COMPANY_NUMBER, capturedSpecs.get(0).getCompanyNumber());

        assertEquals(0, capturedSpecs.get(1).getWithdrawnStatements());
        assertEquals(1, capturedSpecs.get(1).getNumberOfPscs());
        assertEquals(COMPANY_NUMBER, capturedSpecs.get(1).getCompanyNumber());

        assertEquals(0, capturedSpecs.get(2).getWithdrawnStatements());
        assertEquals(1, capturedSpecs.get(2).getNumberOfPscs());
        assertEquals(COMPANY_NUMBER, capturedSpecs.get(2).getCompanyNumber());
    }

    @Test
    void testRegisteredOverseasEntityStatement() {
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
        spec.setWithdrawnStatements(0);
        spec.setNumberOfPscs(5);

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
        assertEquals(PSC_STATEMENT_2, capturedStatement.getStatement());
    }

    @Test
    void createPscStatements_hasSuperSecurePscs() {
        spec.setHasSuperSecurePscs(true);
        spec.setNumberOfPscs(5);

        doReturn(new CompanyPscStatement()).when(companyPscStatementService).create(any(CompanyRequest.class));

        List<CompanyPscStatement> result = companyPscStatementService.createPscStatements(spec);

        verify(companyPscStatementService, times(1)).create(any(CompanyRequest.class));
        assertEquals(1, result.size());

        ArgumentCaptor<CompanyRequest> specCaptor = ArgumentCaptor.forClass(CompanyRequest.class);
        verify(companyPscStatementService, times(1)).create(specCaptor.capture());
        CompanyRequest capturedSpec = specCaptor.getValue();
        assertEquals(0, capturedSpec.getWithdrawnStatements());
        assertEquals(1, capturedSpec.getNumberOfPscs());
        assertTrue(capturedSpec.getPscActive());
    }

    @Test
    void createPscStatements_activeStatementsPrioritizesOverNumberOfPsc() {
        spec.setActiveStatements(2);
        spec.setNumberOfPscs(5);

        doReturn(new CompanyPscStatement()).when(companyPscStatementService).create(any(CompanyRequest.class));

        List<CompanyPscStatement> result = companyPscStatementService.createPscStatements(spec);

        verify(companyPscStatementService, times(2)).create(any(CompanyRequest.class));
        assertEquals(2, result.size());

        ArgumentCaptor<CompanyRequest> specCaptor = ArgumentCaptor.forClass(CompanyRequest.class);
        verify(companyPscStatementService, times(2)).create(specCaptor.capture());
        List<CompanyRequest> capturedSpecs = specCaptor.getAllValues();
        assertEquals(2, capturedSpecs.size());
        assertTrue(capturedSpecs.get(0).getPscActive());
        assertEquals(1, capturedSpecs.get(0).getNumberOfPscs());
        assertEquals(0, capturedSpecs.get(0).getWithdrawnStatements());
    }

    @Test
    void createPscStatements_numberOfPscUsedWhenActiveStatementsIsNull() {
        spec.setActiveStatements(null);
        spec.setNumberOfPscs(3);

        doReturn(new CompanyPscStatement()).when(companyPscStatementService).create(any(CompanyRequest.class));

        List<CompanyPscStatement> result = companyPscStatementService.createPscStatements(spec);

        verify(companyPscStatementService, times(3)).create(any(CompanyRequest.class));
        assertEquals(3, result.size());

        ArgumentCaptor<CompanyRequest> specCaptor = ArgumentCaptor.forClass(CompanyRequest.class);
        verify(companyPscStatementService, times(3)).create(specCaptor.capture());
        List<CompanyRequest> capturedSpecs = specCaptor.getAllValues();
        assertEquals(3, capturedSpecs.size());
        assertTrue(capturedSpecs.get(0).getPscActive());
        assertEquals(1, capturedSpecs.get(0).getNumberOfPscs());
        assertEquals(0, capturedSpecs.get(0).getWithdrawnStatements());
    }

    @Test
    void createPscStatements_zeroActiveAndWithdrawnCounts() {
        spec.setWithdrawnStatements(0);
        spec.setActiveStatements(0);
        spec.setNumberOfPscs(0);

        List<CompanyPscStatement> result = companyPscStatementService.createPscStatements(spec);

        verify(companyPscStatementService, never()).create(any(CompanyRequest.class));
        assertTrue(result.isEmpty());
    }

    @Test
    void createPscStatements_nullCountsYieldDefaultScenario() {
        spec.setWithdrawnStatements(null);
        spec.setActiveStatements(null);
        spec.setNumberOfPscs(null);

        List<CompanyPscStatement> result = companyPscStatementService.createPscStatements(spec);

        verify(companyPscStatementService, never()).create(any(CompanyRequest.class));
        assertTrue(result.isEmpty());
    }

    @Test
    void generateWithdrawnPscStatements_nullCount() {

        List<CompanyPscStatement> result = companyPscStatementService.generateWithdrawnPscStatements(spec, null);

        assertTrue(result.isEmpty());
        verify(companyPscStatementService, never()).create(any(CompanyRequest.class));
    }

    @Test
    void generateWithdrawnPscStatements_zeroCount() {

        List<CompanyPscStatement> result = companyPscStatementService.generateWithdrawnPscStatements(spec, 0);

        assertTrue(result.isEmpty());
        verify(companyPscStatementService, never()).create(any(CompanyRequest.class));
    }

    @Test
    void generateWithdrawnPscStatements_negativeCount() {
        List<CompanyPscStatement> result = companyPscStatementService.generateWithdrawnPscStatements(spec, -1);

        assertTrue(result.isEmpty());
        verify(companyPscStatementService, never()).create(any(CompanyRequest.class));
    }

    @Test
    void generateActivePscStatements_nullCount() {
        List<CompanyPscStatement> result = companyPscStatementService.generateActivePscStatements(spec, null);

        assertTrue(result.isEmpty());
        verify(companyPscStatementService, never()).create(any(CompanyRequest.class));
    }

    @Test
    void generateActivePscStatements_zeroCount() {
        List<CompanyPscStatement> result = companyPscStatementService.generateActivePscStatements(spec, 0);

        assertTrue(result.isEmpty());
        verify(companyPscStatementService, never()).create(any(CompanyRequest.class));
    }

    @Test
    void generateActivePscStatements_negativeCount() {
        List<CompanyPscStatement> result = companyPscStatementService.generateActivePscStatements(spec, -1);

        assertTrue(result.isEmpty());
        verify(companyPscStatementService, never()).create(any(CompanyRequest.class));
    }

    @Test
    void createReturnsUnsavedPscStatementWhenCompanyWithDataStructureIsTrue() {
        spec.setWithdrawnStatements(0);
        spec.setCompanyWithPopulatedStructureOnly(true);
        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEtag()).thenReturn(ETAG);

        CompanyPscStatement result = this.companyPscStatementService.create(spec);

        assertNotNull(result);
        assertEquals(COMPANY_NUMBER, result.getCompanyNumber());
        assertEquals(ENCODED_VALUE, result.getId());
        assertEquals(ETAG, result.getEtag());
        verify(repository, never()).save(any());
    }

}