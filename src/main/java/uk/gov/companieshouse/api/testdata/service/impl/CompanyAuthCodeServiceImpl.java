package uk.gov.companieshouse.api.testdata.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import uk.gov.companieshouse.api.testdata.constants.ErrorMessageConstants;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.account.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.repository.account.CompanyAuthCodeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.service.ICompanyAuthCodeService;

import java.util.Date;
import java.util.Random;

@Service
public class CompanyAuthCodeServiceImpl implements ICompanyAuthCodeService {

    private CompanyAuthCodeRepository repository;

    private Random rand = new Random();

    private static final String COMPANY_AUTH_DATA_NOT_FOUND = "company auth data not found";

    @Autowired
    public CompanyAuthCodeServiceImpl(CompanyAuthCodeRepository repository) {
        this.repository = repository;
    }

    public String create(String companyNumber) throws DataException {

        CompanyAuthCode companyAuthCode = new CompanyAuthCode();

        companyAuthCode.setId(companyNumber);
        companyAuthCode.setValidFrom(new Date());
        companyAuthCode.setAuthCode(getNewAuthCode());
        companyAuthCode.setIsActive(true);

        try{
            repository.save(companyAuthCode);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }

        return companyAuthCode.getAuthCode();
    }

    public void delete(String companyId) throws NoDataFoundException, DataException {

        CompanyAuthCode existingCompanyAuthCode = repository.findById(companyId).orElseThrow(
                () -> new NoDataFoundException(COMPANY_AUTH_DATA_NOT_FOUND));

        try {
            repository.delete(existingCompanyAuthCode);
        } catch (MongoException e) {
            throw new DataException(ErrorMessageConstants.FAILED_TO_DELETE);
        }
    }

    private String getNewAuthCode(){
        int num = rand.nextInt(900000);
        return String.format("%06d", num);
    }
}
