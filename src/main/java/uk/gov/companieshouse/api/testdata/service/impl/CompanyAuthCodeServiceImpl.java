package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public CompanyAuthCode create(String companyNumber) throws DataException {

        CompanyAuthCode companyAuthCode = new CompanyAuthCode();

        companyAuthCode.setId(companyNumber);
        companyAuthCode.setValidFrom(new Date());
        companyAuthCode.setAuthCode(String.valueOf(randomService.getRandomNumber(AUTH_CODE_LENGTH)));
        companyAuthCode.setIsActive(true);

        try {
            return repository.save(companyAuthCode);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }
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
