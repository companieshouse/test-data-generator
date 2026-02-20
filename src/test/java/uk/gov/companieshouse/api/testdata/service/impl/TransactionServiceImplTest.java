package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;

import uk.gov.companieshouse.api.testdata.model.entity.AcspApplication;
import uk.gov.companieshouse.api.testdata.model.entity.Transactions;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspApplicationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.TransactionsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.TransactionsRequest;
import uk.gov.companieshouse.api.testdata.repository.AcspApplicationRepository;
import uk.gov.companieshouse.api.testdata.repository.TransactionsRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

    private AcspApplicationRequest acspSpec;
    private TransactionsRequest txnSpec;
    @Mock

    private RandomService randomService;


    @BeforeEach
    void setUp() {
        transactions = new Transactions();
        txnSpec = new TransactionsRequest();
        acspApplication = new AcspApplication();
        acspSpec = new AcspApplicationRequest();

    }

    @Test
    void createTransaction() throws DataException {
        txnSpec.setUserId("test123");
        txnSpec.setReference("ACSP Registration");
        txnSpec.setId(randomService.getTransactionId());
        when(transactionsRepository.save(any(Transactions.class))).thenReturn(transactions);
        when(acspApplicationRepository.save(any(AcspApplication.class))).thenReturn(acspApplication);

        TransactionsResponse result = transactionServiceImpl.create(txnSpec);

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

        TransactionsResponse result = transactionServiceImpl.create(txnSpec);

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
    @Test
    void deleteTransactionSuccessfully() {
        String txnId = "903085-903085-903085";
        String acspId = "acsp-456";

        Transactions entity = new Transactions();
        entity.setId(txnId);
        entity.setResumeUri("/register-as-companies-house-authorised-agent/resume?transactionId="
                + txnId + "&acspId=" + acspId);

        AcspApplication acspEntity = new AcspApplication();
        acspEntity.setId(acspId);

        when(transactionsRepository.findById(txnId)).thenReturn(Optional.of(entity));
        when(acspApplicationRepository.findById(acspId)).thenReturn(Optional.of(acspEntity));

        boolean result = transactionServiceImpl.delete(txnId);

        assertTrue(result);
        verify(acspApplicationRepository, times(1)).findById(acspId);
        verify(acspApplicationRepository, times(1)).delete(acspEntity);
        verify(transactionsRepository, times(1)).delete(entity);
    }

    @Test
    void deleteTransactionNotFound() {
        String id = "nonexistent";
        when(transactionsRepository.findById(id)).thenReturn(Optional.empty());

        boolean result = transactionServiceImpl.delete(id);

        assertFalse(result);
        verify(transactionsRepository, never()).delete(any());
        verify(acspApplicationRepository, never()).findById(any());
    }

    @Test
    void deleteTransactionWithNullResumeUri() {
        String id = "txn-null-uri";
        Transactions entity = new Transactions();
        entity.setId(id);
        entity.setResumeUri(null);

        when(transactionsRepository.findById(id)).thenReturn(Optional.of(entity));

        boolean result = transactionServiceImpl.delete(id);

        assertTrue(result);
        verify(acspApplicationRepository, never()).findById(any());
        verify(acspApplicationRepository, never()).delete(any());
        verify(transactionsRepository).delete(entity);
    }

    @Test
    void deleteTransactionWithResumeUriWithoutAcspId() {
        String txnId = "txn-no-acsp";
        Transactions entity = new Transactions();
        entity.setId(txnId);
        entity.setResumeUri("/resume?transactionId=" + txnId); // no acspId param

        when(transactionsRepository.findById(txnId)).thenReturn(Optional.of(entity));

        boolean result = transactionServiceImpl.delete(txnId);

        assertTrue(result);
        verify(acspApplicationRepository, never()).findById(any());
        verify(acspApplicationRepository, never()).delete(any());
        verify(transactionsRepository).delete(entity);
    }

    @Test
    void deleteTransactionAcspIdNotFoundInRepository() {
        String txnId = "txn-789";
        String acspId = "acsp-999";

        Transactions entity = new Transactions();
        entity.setId(txnId);
        entity.setResumeUri("/resume?transactionId=" + txnId + "&acspId=" + acspId);

        when(transactionsRepository.findById(txnId)).thenReturn(Optional.of(entity));
        when(acspApplicationRepository.findById(acspId)).thenReturn(Optional.empty());

        boolean result = transactionServiceImpl.delete(txnId);

        assertTrue(result);
        verify(acspApplicationRepository).findById(acspId);
        verify(acspApplicationRepository, never()).delete(any());
        verify(transactionsRepository).delete(entity);
    }
}