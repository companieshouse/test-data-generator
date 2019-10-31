package uk.gov.companieshouse.api.testdata.controller;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.CreatedCompany;
import uk.gov.companieshouse.api.testdata.service.ITestDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class TestDataControllerTest {

    private TestDataController testDataController;

    @Mock
    private ITestDataService ITestDataService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
        testDataController = new TestDataController(ITestDataService);
    }

    @Test
    void testCreateWorking() throws DataException {
        CreatedCompany mockCompany = new CreatedCompany("12345678", "123456");

        when(this.ITestDataService.createCompanyData()).thenReturn(mockCompany);
        ResponseEntity response = this.testDataController.create();

        assertEquals(mockCompany, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateException() throws DataException{
        Throwable exception = new DataException("Error message");
        when(this.ITestDataService.createCompanyData()).thenThrow(exception);

        ResponseEntity response = this.testDataController.create();
        assertEquals(exception.getMessage(), response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testDeleteWorking() {
        ResponseEntity response = this.testDataController.delete("123456");

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteNoData() throws Exception{
        Throwable throwable = new NoDataFoundException("Error message");
        doThrow(throwable).when(this.ITestDataService).deleteCompanyData("123456");
        ResponseEntity response = this.testDataController.delete("123456");

        assertEquals(throwable.getMessage() + " for company: 123456", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteDataException() throws Exception{
        Throwable throwable = new DataException("Error message");
        doThrow(throwable).when(this.ITestDataService).deleteCompanyData("123456");
        ResponseEntity response = this.testDataController.delete("123456");

        assertEquals(throwable.getMessage(), response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}