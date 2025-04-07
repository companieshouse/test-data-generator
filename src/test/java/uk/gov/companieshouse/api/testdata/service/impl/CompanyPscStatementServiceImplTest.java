package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyType;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscStatementRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class CompanyPscStatementServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String ENCODED_VALUE = "ENCODED";
    private static final String ETAG = "ETAG";
    private static final String PSC_STATEMENT_KIND = "persons-with-significant-control-statement";
    private static final String PSC_STATEMENT = "psc-exists-but-not-identified";

    @Mock
    private CompanyPscStatementRepository repository;
    @Mock
    private RandomService randomService;

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
        ArgumentCaptor<CompanyPscStatement> statementCaptor
                = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository).save(statementCaptor.capture());
        CompanyPscStatement statement = statementCaptor.getValue();
        assertNotNull(statement);
        assertEquals(ENCODED_VALUE, statement.getId());
        assertNotNull(statement.getUpdatedAt());
        assertEquals(COMPANY_NUMBER, statement.getCompanyNumber());
        assertEquals(ENCODED_VALUE, statement.getPscStatementId());

        Links links = statement.getLinks();
        assertEquals("/company/" + COMPANY_NUMBER + "/persons-with-significant-control-statements/"
                + ENCODED_VALUE, links.getSelf());
        assertNotNull(statement.getNotifiedOn());
        assertEquals(ETAG, statement.getEtag());
        assertEquals(PSC_STATEMENT_KIND, statement.getKind());
        assertEquals(PSC_STATEMENT, statement.getStatement());

        assertNotNull(statement.getCreatedAt());

    }

    @Test
    void delete() {
        CompanyPscStatement companyPscStatement = new CompanyPscStatement();
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(Optional.of(companyPscStatement));
        assertTrue(this.companyPscStatementService.delete(COMPANY_NUMBER));
        verify(repository).delete(companyPscStatement);
    }

    @Test
    void deleteNoDataException() {
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(Optional.empty());
        assertFalse(this.companyPscStatementService.delete(COMPANY_NUMBER));
        verify(repository, never()).delete(any());
    }

    @Test
    void createWhenAccountsDueStatusIsNull() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setAccountsDueStatus(null);

        CompanyPscStatement savedStatement = new CompanyPscStatement();
        when(this.repository.save(any())).thenReturn(savedStatement);

        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEtag()).thenReturn(ETAG);

        CompanyPscStatement returnedStatement = this.companyPscStatementService.create(spec);

        assertEquals(savedStatement, returnedStatement);
        ArgumentCaptor<CompanyPscStatement> statementCaptor
                = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository).save(statementCaptor.capture());
        CompanyPscStatement capturedStatement = statementCaptor.getValue();
        assertNotNull(capturedStatement);
        assertEquals(ENCODED_VALUE, capturedStatement.getId());
        assertNotNull(capturedStatement.getUpdatedAt());
        assertEquals(COMPANY_NUMBER, capturedStatement.getCompanyNumber());
        assertEquals(ENCODED_VALUE, capturedStatement.getPscStatementId());

        Links links = capturedStatement.getLinks();
        assertEquals("/company/" + COMPANY_NUMBER + "/persons-with-significant-control-statements/"
                + ENCODED_VALUE, links.getSelf());
        assertNotNull(capturedStatement.getNotifiedOn());
        assertEquals(ETAG, capturedStatement.getEtag());
        assertEquals(PSC_STATEMENT_KIND, capturedStatement.getKind());
        assertEquals(PSC_STATEMENT,
                capturedStatement.getStatement());
        assertNotNull(capturedStatement.getCreatedAt());
    }

    @Test
    void createWhenAccountsDueStatusIsDueSoon() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setAccountsDueStatus("due-soon");

        CompanyPscStatement savedStatement = new CompanyPscStatement();
        when(this.repository.save(any())).thenReturn(savedStatement);

        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEtag()).thenReturn(ETAG);
        when(randomService.generateAccountsDueDateByStatus("due-soon")).thenReturn(LocalDate.now());

        CompanyPscStatement returnedStatement = this.companyPscStatementService.create(spec);

        assertEquals(savedStatement, returnedStatement);
        ArgumentCaptor<CompanyPscStatement> statementCaptor
                = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository).save(statementCaptor.capture());
        CompanyPscStatement capturedStatement = statementCaptor.getValue();
        assertNotNull(capturedStatement);
        assertEquals(ENCODED_VALUE, capturedStatement.getId());
        assertNotNull(capturedStatement.getUpdatedAt());
        assertEquals(COMPANY_NUMBER, capturedStatement.getCompanyNumber());
        assertEquals(ENCODED_VALUE, capturedStatement.getPscStatementId());

        Links links = capturedStatement.getLinks();
        assertEquals("/company/" + COMPANY_NUMBER + "/persons-with-significant-control-statements/"
                + ENCODED_VALUE, links.getSelf());
        assertNotNull(capturedStatement.getNotifiedOn());
        assertEquals(ETAG, capturedStatement.getEtag());
        assertEquals(PSC_STATEMENT_KIND, capturedStatement.getKind());
        assertEquals(PSC_STATEMENT,
                capturedStatement.getStatement());
        assertNotNull(capturedStatement.getCreatedAt());
    }

    @Test
    void createWhenAccountsDueStatusIsOverDue() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setAccountsDueStatus("overdue");

        CompanyPscStatement savedStatement = new CompanyPscStatement();
        when(this.repository.save(any())).thenReturn(savedStatement);

        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEtag()).thenReturn(ETAG);
        when(randomService.generateAccountsDueDateByStatus("overdue")).thenReturn(LocalDate.now());

        CompanyPscStatement returnedStatement = this.companyPscStatementService.create(spec);

        assertEquals(savedStatement, returnedStatement);
        ArgumentCaptor<CompanyPscStatement> statementCaptor
                = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository).save(statementCaptor.capture());
        CompanyPscStatement capturedStatement = statementCaptor.getValue();
        assertNotNull(capturedStatement);
        assertEquals(ENCODED_VALUE, capturedStatement.getId());
        assertNotNull(capturedStatement.getUpdatedAt());
        assertEquals(COMPANY_NUMBER, capturedStatement.getCompanyNumber());
        assertEquals(ENCODED_VALUE, capturedStatement.getPscStatementId());

        Links links = capturedStatement.getLinks();
        assertEquals("/company/" + COMPANY_NUMBER + "/persons-with-significant-control-statements/"
                + ENCODED_VALUE, links.getSelf());
        assertNotNull(capturedStatement.getNotifiedOn());
        assertEquals(ETAG, capturedStatement.getEtag());
        assertEquals(PSC_STATEMENT_KIND, capturedStatement.getKind());
        assertEquals(PSC_STATEMENT,
                capturedStatement.getStatement());
        assertNotNull(capturedStatement.getCreatedAt());
    }

    @Test
    void createWhenCompanyTypeIsRegisteredOverseasEntity() {
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
        assertEquals("all-beneficial-owners-identified", capturedStatement.getStatement());
    }

    @Test
    void createWhenCompanyTypeIsOverseaCompany() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.OVERSEA_COMPANY);

        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEtag()).thenReturn(ETAG);
        CompanyPscStatement savedStatement = new CompanyPscStatement();
        when(this.repository.save(any())).thenReturn(savedStatement);

        CompanyPscStatement returnedStatement = this.companyPscStatementService.create(spec);

        assertEquals(savedStatement, returnedStatement);
        ArgumentCaptor<CompanyPscStatement> statementCaptor = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository).save(statementCaptor.capture());
        CompanyPscStatement capturedStatement = statementCaptor.getValue();
        assertEquals("no-individual-or-entity-with-signficant-control", capturedStatement.getStatement());
    }

    @Test
    void createWhenCompanyTypeIsOther() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.LTD);

        when(this.randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(this.randomService.getEtag()).thenReturn(ETAG);
        CompanyPscStatement savedStatement = new CompanyPscStatement();
        when(this.repository.save(any())).thenReturn(savedStatement);

        CompanyPscStatement returnedStatement = this.companyPscStatementService.create(spec);

        assertEquals(savedStatement, returnedStatement);
        ArgumentCaptor<CompanyPscStatement> statementCaptor = ArgumentCaptor.forClass(CompanyPscStatement.class);
        verify(repository).save(statementCaptor.capture());
        CompanyPscStatement capturedStatement = statementCaptor.getValue();
        assertEquals(PSC_STATEMENT, capturedStatement.getStatement());
    }
}