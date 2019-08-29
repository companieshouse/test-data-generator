package com.poc.testdata.controller;

import com.poc.testdata.exception.DataException;
import com.poc.testdata.exception.NoDataFoundException;
import com.poc.testdata.service.TestDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@RestController
@RequestMapping(value = "/testdata", produces = MediaType.APPLICATION_JSON_VALUE)
public class TestDataController {

    @Autowired
    TestDataService testDataService;

    @PostMapping
    public ResponseEntity create() {

        try {
            testDataService.createCompanyData();
        }catch(DataException e){
            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        ArrayList<String> response = new ArrayList<>();
        response.add(testDataService.getCompanyId());
        response.add(testDataService.getCompanyAuthenticationCode());

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity delete(@PathVariable("companyId") String companyId){

        try {
            testDataService.deleteCompanyData(companyId);
        }catch (NoDataFoundException e){
            return new ResponseEntity<>(
                    e.getMessage() + " for company: " + companyId,
                    HttpStatus.NOT_FOUND
            );
        }catch(DataException e) {
            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        return new ResponseEntity<>(
                companyId,
                HttpStatus.OK
        );
    }

}
