package uk.gov.companieshouse.api.testdata.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.CreatedCompany;
import uk.gov.companieshouse.api.testdata.service.TestDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestDataControllerTest {

    @Mock
    private TestDataService testDataService;

    @InjectMocks
    private TestDataController testDataController;

    @Test
    void createWorking() throws DataException {
        CreatedCompany mockCompany = new CreatedCompany("12345678", "123456");

        when(this.testDataService.createCompanyData()).thenReturn(mockCompany);
        ResponseEntity response = this.testDataController.create();

        assertEquals(mockCompany, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createException() throws DataException {
        Throwable exception = new DataException("Error message");
        when(this.testDataService.createCompanyData()).thenThrow(exception);

        ResponseEntity response = this.testDataController.create();
        assertEquals(exception.getMessage(), response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void deleteWorking() {
        ResponseEntity response = this.testDataController.delete("123456");

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteNoData() throws Exception {
        Throwable throwable = new NoDataFoundException("Error message");
        doThrow(throwable).when(this.testDataService).deleteCompanyData("123456");
        ResponseEntity response = this.testDataController.delete("123456");

        assertEquals(throwable.getMessage() + " for company: 123456", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteDataException() throws Exception {
        Throwable throwable = new DataException("Error message");
        doThrow(throwable).when(this.testDataService).deleteCompanyData("123456");
        ResponseEntity response = this.testDataController.delete("123456");

        assertEquals(throwable.getMessage(), response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}