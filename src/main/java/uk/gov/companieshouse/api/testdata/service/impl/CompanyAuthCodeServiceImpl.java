package uk.gov.companieshouse.api.testdata.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

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

    private final RandomService randomService;

    private final CompanyAuthCodeRepository repository;

    private final MessageDigest sha256Digest;

    @Autowired
    public CompanyAuthCodeServiceImpl(RandomService randomService, CompanyAuthCodeRepository repository) throws NoSuchAlgorithmException {
        super();
        this.randomService = randomService;
        this.repository = repository;
        sha256Digest = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256);
    }

    @Override
    public CompanyAuthCode create(CompanySpec spec) {
        final String authCode = String.valueOf(randomService.getNumber(AUTH_CODE_LENGTH));

        CompanyAuthCode companyAuthCode = new CompanyAuthCode();

        companyAuthCode.setId(spec.getCompanyNumber());
        companyAuthCode.setAuthCode(authCode);
        companyAuthCode.setEncryptedAuthCode(encrypt(authCode));
        companyAuthCode.setIsActive(true);

        return repository.save(companyAuthCode);
    }

    private String encrypt(final String authCode) {
        return BCrypt.hashpw(sha256(authCode), BCrypt.gensalt());
    }

    private byte[] sha256(final String authCode) {
        return sha256Digest.digest(authCode.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean delete(String companyId) {

        Optional<CompanyAuthCode> existingCompanyAuthCode = repository.findById(companyId);

        existingCompanyAuthCode.ifPresent(repository::delete);
        return existingCompanyAuthCode.isPresent();
    }

    @Override
    public boolean verifyAuthCode(
            String companyNumber, String plainAuthCode) throws NoDataFoundException {
        String encryptedAuthCode = repository.findById(companyNumber)
                .orElseThrow(() -> new NoDataFoundException(
                        COMPANY_AUTH_DATA_NOT_FOUND)).getEncryptedAuthCode();

        return BCrypt.checkpw(sha256(plainAuthCode), encryptedAuthCode);
    }
}
