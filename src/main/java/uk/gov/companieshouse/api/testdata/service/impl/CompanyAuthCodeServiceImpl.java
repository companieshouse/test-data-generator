package uk.gov.companieshouse.api.testdata.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.constants.ErrorMessageConstants;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.companyauthcode.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.repository.CompanyAuthCodeRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import java.util.Date;

@Service
public class CompanyAuthCodeServiceImpl implements DataService<CompanyAuthCode> {

    private static final String COMPANY_AUTH_DATA_NOT_FOUND = "company auth data not found";
    private static final int AUTH_CODE_LENGTH = 6;

    private RandomService randomService;
    private CompanyAuthCodeRepository repository;

    @Autowired
    public CompanyAuthCodeServiceImpl(RandomService randomService, CompanyAuthCodeRepository repository) {
        this.randomService = randomService;
        this.repository = repository;
    }

    @Override
    public CompanyAuthCode create(String companyNumber) throws DataException {

        CompanyAuthCode companyAuthCode = new CompanyAuthCode();

        companyAuthCode.setId(companyNumber);
        companyAuthCode.setValidFrom(new Date());
        companyAuthCode.setAuthCode(this.randomService.getRandomInteger(AUTH_CODE_LENGTH));
        companyAuthCode.setIsActive(true);

        try {
            repository.save(companyAuthCode);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }

        return companyAuthCode;
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
