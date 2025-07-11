package uk.gov.companieshouse.api.testdata.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;

import uk.gov.companieshouse.api.testdata.model.entity.AcspApplication;
import uk.gov.companieshouse.api.testdata.model.entity.Transactions;
import uk.gov.companieshouse.api.testdata.model.rest.AcspApplicationSpec;
import uk.gov.companieshouse.api.testdata.model.rest.TransactionsData;
import uk.gov.companieshouse.api.testdata.model.rest.TransactionsSpec;
import uk.gov.companieshouse.api.testdata.repository.AcspApplicationRepository;
import uk.gov.companieshouse.api.testdata.repository.TransactionsRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {
    @Mock
    private TransactionsRepository transactionsRepository;

    @Mock
    private AcspApplicationRepository acspApplicationRepository;

    @Spy
    @InjectMocks
    private TransactionServiceImpl transactionServiceImpl;
    @Captor
    private ArgumentCaptor<Transactions> txnCaptor;

    @Captor
    private ArgumentCaptor<AcspApplication> acspApplicationCaptor;

    private Transactions transactions;

    private AcspApplication acspApplication;

    private AcspApplicationSpec acspSpec;
    private TransactionsSpec txnSpec;
    @Mock

    private RandomService randomService;


    @BeforeEach
    void setUp() {
        transactions = new Transactions();
        txnSpec = new TransactionsSpec();
        acspApplication = new AcspApplication();
        acspSpec = new AcspApplicationSpec();

    }


    @Test
    void createTransaction() throws DataException {
        txnSpec.setUserId("test123");
        txnSpec.setReference("ACSP Registration");
        txnSpec.setId(randomService.getTransactionId());
        when(transactionsRepository.save(any(Transactions.class))).thenReturn(transactions);
        when(acspApplicationRepository.save(any(AcspApplication.class))).thenReturn(acspApplication);
        TransactionsData result = transactionServiceImpl.create(txnSpec);
        assertNotNull(result);
        verify(transactionsRepository).save(any(Transactions.class));
        verify(transactionsRepository).save(txnCaptor.capture());
        verify(acspApplicationRepository).save(any(AcspApplication.class));
        verify(acspApplicationRepository).save(acspApplicationCaptor.capture());
        Transactions captured = txnCaptor.getValue();
        AcspApplication acspAdded = acspApplicationCaptor.getValue();
        assertNotNull(captured);
        assertNotNull(acspAdded);
        assertEquals("test123", captured.getUserId());
        assertEquals("ACSP Registration", captured.getReference());
        assertEquals("open", captured.getStatus());
        assertEquals("Create an ACSP registration transaction", captured.getDescription());
        assertNotNull(captured.getResumeUri());
        assertEquals("test123", acspAdded.getUserId());
        assertNotNull(acspAdded.getSelf());
        assertEquals("limited-company", acspAdded.getTypeOfBusiness());
    }

    @Test
    void createTransactionWithEmail() throws DataException {
        txnSpec.setUserId("test123");
        txnSpec.setReference("ACSP Registration");
        txnSpec.setEmail("testuser@test.com");
        when(transactionsRepository.save(any(Transactions.class))).thenReturn(transactions);
        when(acspApplicationRepository.save(any(AcspApplication.class))).thenReturn(acspApplication);
        TransactionsData result = transactionServiceImpl.create(txnSpec);
        assertNotNull(result);
        verify(transactionsRepository).save(any(Transactions.class));
        verify(transactionsRepository).save(txnCaptor.capture());
        verify(acspApplicationRepository).save(any(AcspApplication.class));
        verify(acspApplicationRepository).save(acspApplicationCaptor.capture());
        Transactions captured = txnCaptor.getValue();
        AcspApplication acspAdded = acspApplicationCaptor.getValue();
        assertNotNull(captured);
        assertNotNull(acspAdded);
        assertEquals("test123", captured.getUserId());
        assertEquals("ACSP Registration", captured.getReference());
        assertEquals("open", captured.getStatus());
        assertEquals("testuser@test.com", captured.getEmail());
        assertEquals("Create an ACSP registration transaction", captured.getDescription());
        assertNotNull(captured.getResumeUri());
        assertEquals("test123", acspAdded.getUserId());
        assertNotNull(acspAdded.getSelf());
        assertEquals("limited-company", acspAdded.getTypeOfBusiness());
    }

}