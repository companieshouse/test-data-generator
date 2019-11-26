package uk.gov.companieshouse.api.testdata.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyAuthCodeRepository;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CompanyAuthCodeServiceImpl implements CompanyAuthCodeService {

    private static final String COMPANY_AUTH_DATA_NOT_FOUND = "company auth data not found";
    private static final int AUTH_CODE_LENGTH = 6;

    @Autowired
    private RandomService randomService;
    @Autowired
    private CompanyAuthCodeRepository repository;

    private final MessageDigest sha256Digest;

    public CompanyAuthCodeServiceImpl() throws NoSuchAlgorithmException {
        sha256Digest = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256);
    }

    @Override
    public CompanyAuthCode create(CompanySpec spec) throws DataException {
        final String authCode = String.valueOf(randomService.getNumber(AUTH_CODE_LENGTH));

        CompanyAuthCode companyAuthCode = new CompanyAuthCode();

        companyAuthCode.setId(spec.getCompanyNumber());
        companyAuthCode.setAuthCode(authCode);
        companyAuthCode.setEncryptedAuthCode(encrypt(authCode));
        companyAuthCode.setIsActive(true);

        try {
            return repository.save(companyAuthCode);
        } catch (MongoException e) {
            throw new DataException("Failed to save company auth code", e);
        }
    }

    private String encrypt(final String authCode) {
        return BCrypt.hashpw(sha256(authCode), BCrypt.gensalt());
    }

    private byte[] sha256(final String authCode) {
        return sha256Digest.digest(authCode.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void delete(String companyId) throws NoDataFoundException, DataException {

        CompanyAuthCode existingCompanyAuthCode = repository.findById(companyId)
                .orElseThrow(() -> new NoDataFoundException(COMPANY_AUTH_DATA_NOT_FOUND));

        try {
            repository.delete(existingCompanyAuthCode);
        } catch (MongoException e) {
            throw new DataException("Failed to delete company auth code", e);
        }
    }

    @Override
    public boolean verifyAuthCode(String companyNumber, String plainAuthCode) throws NoDataFoundException {
        String encryptedAuthCode = repository.findById(companyNumber)
                .orElseThrow(() -> new NoDataFoundException(COMPANY_AUTH_DATA_NOT_FOUND)).getEncryptedAuthCode();

        // Ideally we would use the following line to verify the encryption:
        //
        // return BCrypt.checkpw(sha256(plainAuthCode), encryptedAuthCode);
        //
        // However, the latest version of Spring Security at the time of developing this
        // (5.2.1.RELEASE) does not provide a checkpw method accepting a byte[] as a
        // password. It only expects a UTF-8 String but our password isn't UTF-8.
        // That is why we need to verify it ourselves by just hashing the authcode using
        // the same salt (present in the encrypted auth code) and then compare the
        // hashed values.
        String encrypted = BCrypt.hashpw(sha256(plainAuthCode), encryptedAuthCode);
        return MessageDigest.isEqual(encryptedAuthCode.getBytes(StandardCharsets.UTF_8),
                encrypted.getBytes(StandardCharsets.UTF_8));
    }
}
