package uk.gov.companieshouse.api.testdata.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyAuthCodeRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CompanyAuthCodeServiceImpl implements CompanyAuthCodeService {

    private static final String COMPANY_AUTH_DATA_NOT_FOUND = "company auth data not found";
    private static final String COMPANY_PROFILE_NOT_FOUND = "company profile not found";
    private static final String DEFAULT_AUTH_CODE = "222222";

    private static final int AUTH_CODE_LENGTH = 6;

    @Autowired
    private RandomService randomService;

    @Autowired
    private CompanyAuthCodeRepository repository;

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Override
    public CompanyAuthCode create(CompanySpec spec) throws DataException {
        final String authCode = String.valueOf(randomService.getNumber(AUTH_CODE_LENGTH));

        CompanyAuthCode companyAuthCode = new CompanyAuthCode();

        companyAuthCode.setId(spec.getCompanyNumber());
        companyAuthCode.setAuthCode(authCode);
        companyAuthCode.setEncryptedAuthCode(encrypt(authCode));
        companyAuthCode.setIsActive(true);

        return repository.save(companyAuthCode);
    }

    /**
     * Finds a CompanyAuthCode by company number.
     * 1. Checks if the company profile exists.
     * 2. If profile exists, checks for an existing auth code.
     * 3. If auth code exists, returns it.
     * 4. If auth code does not exist, creates a new one with the default "222222".
     *
     * @param companyNumber The company number (ID)
     * @return The existing or newly created default CompanyAuthCode
     * @throws DataException if hashing fails
     */
    @Override
    public CompanyAuthCode findOrCreate(String companyNumber)
            throws DataException, NoDataFoundException {
        if (companyProfileRepository.findById(companyNumber).isEmpty()) {
            throw new NoDataFoundException(COMPANY_PROFILE_NOT_FOUND);
        }

        Optional<CompanyAuthCode> existingAuthCode = repository.findById(companyNumber);
        if (existingAuthCode.isPresent()) {
            return existingAuthCode.get();
        }

        final String authCode = DEFAULT_AUTH_CODE;

        var companyAuthCode = new CompanyAuthCode();

        companyAuthCode.setId(companyNumber);
        companyAuthCode.setAuthCode(authCode);
        companyAuthCode.setEncryptedAuthCode(encrypt(authCode));
        companyAuthCode.setIsActive(true);

        return repository.save(companyAuthCode);
    }

    private String encrypt(final String authCode) throws DataException {
        return BCrypt.hashpw(sha256(authCode), BCrypt.gensalt());
    }

    byte[] sha256(final String authCode) throws DataException {
        try {
            var sha256Digest = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256);
            return sha256Digest.digest(authCode.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException er) {
            throw new DataException("SHA-256 algorithm not found when hashing auth code.");
        }
    }

    @Override
    public boolean delete(String companyId) {
        Optional<CompanyAuthCode> existingCompanyAuthCode = repository.findById(companyId);

        existingCompanyAuthCode.ifPresent(repository::delete);
        return existingCompanyAuthCode.isPresent();
    }

    @Override
    public boolean verifyAuthCode(
            String companyNumber, String plainAuthCode) throws NoDataFoundException, DataException {
        if (companyProfileRepository.findById(companyNumber).isEmpty()) {
            throw new NoDataFoundException(COMPANY_PROFILE_NOT_FOUND);
        }

        Optional<CompanyAuthCode> authCodeOptional = repository.findById(companyNumber);

        if (authCodeOptional.isPresent()) {
            String encryptedAuthCode = authCodeOptional.get().getEncryptedAuthCode();
            return BCrypt.checkpw(sha256(plainAuthCode), encryptedAuthCode);
        } else {
            return DEFAULT_AUTH_CODE.equals(plainAuthCode);
        }
    }
}