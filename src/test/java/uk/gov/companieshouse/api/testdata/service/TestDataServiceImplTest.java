package uk.gov.companieshouse.api.testdata.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.CreatedCompany;
import uk.gov.companieshouse.api.testdata.model.companyauthcode.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.companyprofile.Company;
import uk.gov.companieshouse.api.testdata.model.filinghistory.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.officer.Officer;
import uk.gov.companieshouse.api.testdata.model.psc.PersonsWithSignificantControl;
import uk.gov.companieshouse.api.testdata.service.impl.TestDataServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestDataServiceImplTest {

    @Mock
    private DataService<Company> companyProfileService;
    @Mock
    private DataService<FilingHistory> filingHistoryService;
    @Mock
    private DataService<Officer> officerListService;
    @Mock
    private DataService<PersonsWithSignificantControl> pscService;
    @Mock
    private DataService<CompanyAuthCode> companyAuthCodeService;
    @Mock
    private RandomService randomService;

    private TestDataServiceImpl testDataService;

    @BeforeEach
    void setUp() {
        testDataService = new TestDataServiceImpl(companyProfileService, filingHistoryService, officerListService,
                pscService, companyAuthCodeService, randomService);
    }

    @Test
    void createCompanyData() throws DataException {
        Company mockCompany = new Company();
        mockCompany.setCompanyNumber("12345678");

        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode("123456");

        when(this.randomService.getRandomInteger(8)).thenReturn("12345678");
        when(this.companyAuthCodeService.create("12345678")).thenReturn(mockAuthCode);
        CreatedCompany createdCompany = this.testDataService.createCompanyData();

        assertEquals("12345678", createdCompany.getCompanyNumber());
        assertEquals("123456", createdCompany.getAuthCode());
    }

    @Test
    void deleteCompanyData() throws NoDataFoundException, DataException {
        this.testDataService.deleteCompanyData("123456");

        verify(companyProfileService, times(1)).delete("123456");
        verify(filingHistoryService, times(1)).delete("123456");
        verify(officerListService, times(1)).delete("123456");
        verify(pscService, times(1)).delete("123456");
        verify(companyAuthCodeService, times(1)).delete("123456");
    }
}