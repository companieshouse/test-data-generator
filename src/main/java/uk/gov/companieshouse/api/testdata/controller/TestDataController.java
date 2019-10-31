package uk.gov.companieshouse.api.testdata.controller;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.CreatedCompany;
import uk.gov.companieshouse.api.testdata.service.ITestDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/testdata", produces = MediaType.APPLICATION_JSON_VALUE)
public class TestDataController {
    
    private ITestDataService testDataService;

    @Autowired
    public TestDataController(ITestDataService testDataService) {
        this.testDataService = testDataService;
    }

    @PostMapping
    public ResponseEntity create() {

        CreatedCompany createdCompany;

        try {
            createdCompany = testDataService.createCompanyData();
        }catch(DataException e){
            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        return new ResponseEntity<>(
                createdCompany,
                HttpStatus.CREATED
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
                null,
                HttpStatus.NO_CONTENT
        );
    }

}
