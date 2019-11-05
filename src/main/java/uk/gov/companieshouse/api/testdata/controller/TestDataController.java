package uk.gov.companieshouse.api.testdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.CreatedCompany;
import uk.gov.companieshouse.api.testdata.service.TestDataService;

@RestController
@RequestMapping(value = "/test-data-generator", produces = MediaType.APPLICATION_JSON_VALUE)
public class TestDataController {

    private TestDataService testDataService;

    @Autowired
    public TestDataController(TestDataService testDataService) {
        this.testDataService = testDataService;
    }

    @PostMapping("/testdata")
    public ResponseEntity<CreatedCompany> create() throws DataException {

        CreatedCompany createdCompany = testDataService.createCompanyData();

        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> delete(@PathVariable("companyId") String companyId)
            throws NoDataFoundException, DataException {

        testDataService.deleteCompanyData(companyId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
