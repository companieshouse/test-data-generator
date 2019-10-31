package uk.gov.companieshouse.api.testdata.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.testdata.service.impl.TestDataHelperServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

class TestDataHelperServiceImplTest {

    private ITestDataHelperService testDataHelperService;

    @BeforeEach
    void setUp() {
        this.testDataHelperService = new TestDataHelperServiceImpl();
    }

    @Test
    void testGetNewId() {
        String randomId = this.testDataHelperService.getNewId();
        assertEquals(28, randomId.length());
    }
}