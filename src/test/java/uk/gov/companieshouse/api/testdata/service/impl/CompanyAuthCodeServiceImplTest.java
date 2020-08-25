package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;

import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyAuthCodeRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

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
    void create() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        when(this.randomService.getNumber(6)).thenReturn(COMPANY_AUTH_CODE);

        CompanyAuthCode savedAuthCode = new CompanyAuthCode();
        when(repository.save(any())).thenReturn(savedAuthCode);
        final String password = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256).
                .digest(String.valueOf(COMPANY_AUTH_CODE));
        CompanyAuthCode returnedAuthCode = this.companyAuthCodeServiceImpl.create(spec);

        assertEquals(savedAuthCode, returnedAuthCode);
        
        ArgumentCaptor<CompanyAuthCode> authCodeCaptor = ArgumentCaptor.forClass(CompanyAuthCode.class);
        verify(repository).save(authCodeCaptor.capture());
        CompanyAuthCode authCode = authCodeCaptor.getValue();

        assertNotNull(authCode);
        assertEquals(String.valueOf(COMPANY_AUTH_CODE), authCode.getAuthCode());
        assertTrue(authCode.getIsActive());
        assertEquals(COMPANY_NUMBER, authCode.getId());

         assertTrue(BCrypt.checkpw(password, authCode.getEncryptedAuthCode()));
    }

    @Test
    void verifyAuthCodeCorrect() throws NoSuchAlgorithmException, NoDataFoundException {
        final String plainCode = "222";
        
        // Create a valid encrypted auth code
        final String encryptedAuthCode = BCrypt.hashpw(MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256)
                .digest(plainCode.getBytes(StandardCharsets.UTF_8)), BCrypt.gensalt());

        CompanyAuthCode authCode = new CompanyAuthCode();
        authCode.setAuthCode(plainCode);
        authCode.setEncryptedAuthCode(encryptedAuthCode);
        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.ofNullable(authCode));

        assertTrue(companyAuthCodeServiceImpl.verifyAuthCode(COMPANY_NUMBER, plainCode));
    }
    
    @Test
    void verifyAuthCodeIncorrect() throws NoDataFoundException {
        final String plainCode = "222";
        
        final String encryptedAuthCode = "$2a$10$randomrandomrandomrandomrandomrandomrandomrandom12345";

        CompanyAuthCode authCode = new CompanyAuthCode();
        authCode.setAuthCode(plainCode);
        authCode.setEncryptedAuthCode(encryptedAuthCode);
        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.ofNullable(authCode));

        assertFalse(companyAuthCodeServiceImpl.verifyAuthCode(COMPANY_NUMBER, plainCode));

    }

    @Test
    void verifyAuthCodeNotFound() {
        final String plainCode = "222";

        CompanyAuthCode authCode = null;
        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.ofNullable(authCode));

        assertThrows(NoDataFoundException.class,
                () -> companyAuthCodeServiceImpl.verifyAuthCode(COMPANY_NUMBER, plainCode));
    }
    
    @Test
    void delete() {
        CompanyAuthCode authCode = new CompanyAuthCode();
        when(repository.findById(COMPANY_NUMBER))
                .thenReturn(Optional.of(authCode));
        
        assertTrue(this.companyAuthCodeServiceImpl.delete(COMPANY_NUMBER));
        verify(repository).delete(authCode);
    }

    @Test
    void deleteNoDataException() {
        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.empty());
        assertFalse(this.companyAuthCodeServiceImpl.delete(COMPANY_NUMBER));
        verify(repository, never()).delete(any());
    }

}