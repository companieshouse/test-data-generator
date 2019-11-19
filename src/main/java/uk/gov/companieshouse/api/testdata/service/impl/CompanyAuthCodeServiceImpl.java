package uk.gov.companieshouse.api.testdata.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.constants.ErrorMessageConstants;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.repository.CompanyAuthCodeRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CompanyAuthCodeServiceImpl implements DataService<CompanyAuthCode> {

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
    public CompanyAuthCode create(String companyNumber) throws DataException {
        final String authCode = String.valueOf(randomService.getNumber(AUTH_CODE_LENGTH));

        CompanyAuthCode companyAuthCode = new CompanyAuthCode();

        companyAuthCode.setId(companyNumber);
        companyAuthCode.setAuthCode(encrypt(authCode));
        companyAuthCode.setIsActive(true);

        try {
            return repository.save(companyAuthCode);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }
    }

    private String encrypt(final String authCode) {
        byte[] password = sha256Digest.digest(authCode.getBytes(StandardCharsets.UTF_8));
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public void delete(String companyId) throws NoDataFoundException, DataException {

        CompanyAuthCode existingCompanyAuthCode = repository.findById(companyId).orElseThrow(
                () -> new NoDataFoundException(COMPANY_AUTH_DATA_NOT_FOUND));

        try {
            repository.delete(existingCompanyAuthCode);
        } catch (MongoException e) {
            throw new DataException(ErrorMessageConstants.FAILED_TO_DELETE);
        }
    }
}
