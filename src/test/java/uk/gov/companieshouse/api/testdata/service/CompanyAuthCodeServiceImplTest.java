package uk.gov.companieshouse.api.testdata.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.repository.CompanyAuthCodeRepository;
import uk.gov.companieshouse.api.testdata.service.impl.CompanyAuthCodeServiceImpl;

@ExtendWith(MockitoExtension.class)
class CompanyAuthCodeServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final Long COMPANY_AUTH_CODE = 123456L;

    @Mock
    private CompanyAuthCodeRepository repository;
    @Mock
    private RandomService randomService;

    @InjectMocks
    private CompanyAuthCodeServiceImpl companyAuthCodeServiceImpl;

    @Test
    void create() throws DataException {
        when(this.randomService.getNumber(6)).thenReturn(COMPANY_AUTH_CODE);
        CompanyAuthCode savedAuthCode = new CompanyAuthCode();
        when(repository.save(any())).thenReturn(savedAuthCode);
        
        CompanyAuthCode returnedAuthCode = this.companyAuthCodeServiceImpl.create(COMPANY_NUMBER);

        assertEquals(savedAuthCode, returnedAuthCode);
        
        ArgumentCaptor<CompanyAuthCode> authCodeCaptor = ArgumentCaptor.forClass(CompanyAuthCode.class);
        verify(repository).save(authCodeCaptor.capture());
        CompanyAuthCode authCode = authCodeCaptor.getValue();

        assertNotNull(authCode);
        assertEquals(String.valueOf(COMPANY_AUTH_CODE), authCode.getAuthCode());
        assertTrue(authCode.getIsActive());
        assertEquals(COMPANY_NUMBER, authCode.getId());
    }

    @Test
    void createDuplicateKeyException() {
        when(this.randomService.getNumber(6)).thenReturn(COMPANY_AUTH_CODE);
        when(repository.save(any())).thenThrow(DuplicateKeyException.class);

        DataException exception = assertThrows(DataException.class, () ->
            this.companyAuthCodeServiceImpl.create(COMPANY_NUMBER)
        );
        assertEquals("duplicate key", exception.getMessage());
    }

    @Test
    void createMongoExceptionException() {
        when(this.randomService.getNumber(6)).thenReturn(COMPANY_AUTH_CODE);
        when(repository.save(any())).thenThrow(MongoException.class);

        DataException exception = assertThrows(DataException.class, () ->
            this.companyAuthCodeServiceImpl.create(COMPANY_NUMBER)
        );
        assertEquals("failed to insert", exception.getMessage());
    }
    
    @Test
    void delete() throws Exception {
        CompanyAuthCode authCode = new CompanyAuthCode();
        when(repository.findById(COMPANY_NUMBER))
                .thenReturn(Optional.of(authCode));
        
        this.companyAuthCodeServiceImpl.delete(COMPANY_NUMBER);

        verify(repository).delete(authCode);
    }

    @Test
    void deleteNoDataException() {
        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.empty());
        NoDataFoundException exception = assertThrows(NoDataFoundException.class, () ->
            this.companyAuthCodeServiceImpl.delete(COMPANY_NUMBER)
        );
        assertEquals("company auth data not found", exception.getMessage());
    }

    @Test
    void deleteMongoException() {
        CompanyAuthCode authCode = new CompanyAuthCode();
        when(repository.findById(COMPANY_NUMBER))
                .thenReturn(Optional.of(authCode));
        doThrow(MongoException.class).when(repository).delete(authCode);
        
        DataException exception = assertThrows(DataException.class, () ->
            this.companyAuthCodeServiceImpl.delete(COMPANY_NUMBER)
        );
        assertEquals("failed to delete", exception.getMessage());
    }

}