package uk.gov.companieshouse.api.testdata.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.service.impl.TestDataHelperServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TestDataHelperServiceImplTest {

    @InjectMocks
    private TestDataHelperServiceImpl testDataHelperService;

    @Test
    void getNewId() {
        String randomId = this.testDataHelperService.getNewId();
        assertEquals(28, randomId.length());
    }
}