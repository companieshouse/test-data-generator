package com.poc.testdata.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class TestDataHelperService {

    public String getNewId(){

        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }

}
