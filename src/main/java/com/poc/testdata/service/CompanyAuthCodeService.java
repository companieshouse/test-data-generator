package com.poc.testdata.service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import com.poc.testdata.constants.ErrorMessageConstants;
import com.poc.testdata.exception.DataException;
import com.poc.testdata.exception.NoDataFoundException;
import com.poc.testdata.model.CompanyAuthCodes.CompanyAuthCode;
import com.poc.testdata.repository.CompanyAuthCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Service
public class CompanyAuthCodeService {

    @Autowired
    CompanyAuthCodeRepository repository;

    CompanyAuthCode companyAuthCode;

    private final String COMPANY_AUTH_DATA_NOT_FOUND = "company auth data not found";

    public void create(String companyNumber) throws DataException {

        companyAuthCode = new CompanyAuthCode();

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
    }

    public void delete(String companyId) throws NoDataFoundException, DataException {

        CompanyAuthCode companyAuthCode = repository.findById(companyId).orElse(null);

        if(companyAuthCode == null) throw new NoDataFoundException(COMPANY_AUTH_DATA_NOT_FOUND);

        try {
            repository.delete(companyAuthCode);
        } catch (MongoException e) {
            throw new DataException(ErrorMessageConstants.FAILED_TO_DELETE);
        }
    }

    public String getAuthenticationCode() {

        return companyAuthCode.getAuthCode();
    }

    private String getNewAuthCode(){

        Random rand = new Random();
        Integer num = rand.nextInt(900000);
        return num.toString();
    }
}
