package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.service.ITestDataHelperService;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class TestDataHelperServiceImpl implements ITestDataHelperService {

    @Override
    public String getNewId(){

        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }

}
