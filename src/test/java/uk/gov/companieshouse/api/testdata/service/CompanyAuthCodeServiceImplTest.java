package uk.gov.companieshouse.api.testdata.service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.account.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.repository.account.CompanyAuthCodeRepository;
import uk.gov.companieshouse.api.testdata.service.impl.CompanyAuthCodeServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class CompanyAuthCodeServiceImplTest {

    @Mock
    private CompanyAuthCodeRepository companyAuthCodeRepository;

    private CompanyAuthCodeServiceImpl companyAuthCodeServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.companyAuthCodeServiceImpl = new CompanyAuthCodeServiceImpl(companyAuthCodeRepository);
    }

    @Test
    void testCreateNoException() throws DataException {
        String createdAuthCode = this.companyAuthCodeServiceImpl.create("12345678");

        assertEquals(6, createdAuthCode.length());
    }

    @Test
    void testCreateDuplicateKeyException() {
        when(companyAuthCodeRepository.save(any())).thenThrow(DuplicateKeyException.class);

        assertThrows(DataException.class, () -> {
            this.companyAuthCodeServiceImpl.create("12345678");
        });
    }

    @Test
    void testCreateMongoExceptionException() {
        when(companyAuthCodeRepository.save(any())).thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> {
            this.companyAuthCodeServiceImpl.create("12345678");
        });
    }

    @Test
    void testDeleteNoDateException() {
        when(companyAuthCodeRepository.findById("12345678")).thenReturn(Optional.empty());
        doThrow(MongoException.class).when(companyAuthCodeRepository).delete(any());
        assertThrows(NoDataFoundException.class, () -> {
            this.companyAuthCodeServiceImpl.delete("12345678");
        });
    }

    @Test
    void testDeleteMongoException() {
        when(companyAuthCodeRepository.findById("12345678"))
                .thenReturn(Optional.of(new CompanyAuthCode()));
        doThrow(MongoException.class).when(companyAuthCodeRepository).delete(any());
        assertThrows(DataException.class, () -> {
            this.companyAuthCodeServiceImpl.delete("12345678");
        });
    }

}