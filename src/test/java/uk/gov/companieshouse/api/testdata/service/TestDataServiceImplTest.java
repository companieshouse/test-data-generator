package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.CreatedCompany;
import uk.gov.companieshouse.api.testdata.model.companyprofile.Company;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.companieshouse.api.testdata.service.impl.CompanyAuthCodeServiceImpl;
import uk.gov.companieshouse.api.testdata.service.impl.TestDataServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TestDataServiceImplTest {

    private ITestDataService ITestDataService;

    @Mock
    private ICompanyProfileService ICompanyProfileService;
    @Mock
    private IFilingHistoryService IFilingHistoryService;
    @Mock
    private IOfficerListService IOfficerListService;
    @Mock
    private IPSCService IPSCService;
    @Mock
    private CompanyAuthCodeServiceImpl companyAuthCodeServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.ITestDataService = new TestDataServiceImpl(
                this.ICompanyProfileService,
                this.IFilingHistoryService,
                this.IOfficerListService,
                this.IPSCService,
                this.companyAuthCodeServiceImpl
                );
    }

    @Test
    void testCreateCompanyData() throws DataException {
        Company mockCompany = new Company();
        mockCompany.setCompanyNumber("12345678");

        when(this.ICompanyProfileService.create()).thenReturn(mockCompany);
        when(this.companyAuthCodeServiceImpl.create("12345678")).thenReturn("123456");
        CreatedCompany createdCompany = this.ITestDataService.createCompanyData();

        assertEquals("12345678", createdCompany.getCompanyNumber());
        assertEquals("123456", createdCompany.getAuthCode());
    }

    @Test
    void testDeleteCompanyData() throws Exception{
        try {
            this.ITestDataService.deleteCompanyData("123456");
        } catch (Exception e) {
            fail(e);
        }
    }
}