package uk.gov.companieshouse.api.testdata.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.CreatedCompany;
import uk.gov.companieshouse.api.testdata.model.account.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.companyprofile.Company;
import uk.gov.companieshouse.api.testdata.model.filinghistory.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.officer.Officer;
import uk.gov.companieshouse.api.testdata.model.psc.PersonsWithSignificantControl;
import uk.gov.companieshouse.api.testdata.service.impl.TestDataServiceImpl;

@ExtendWith(MockitoExtension.class)
class TestDataServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String AUTH_CODE = "123456";

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
        //Inject Mocks does not function correctly with generic interfaces
        this.testDataService = new TestDataServiceImpl(this.companyProfileService, this.filingHistoryService,
                this.officerListService, this.pscService, this.companyAuthCodeService, this.randomService);
    }

    @Test
    void createCompanyData() throws DataException {
        Company mockCompany = new Company();
        mockCompany.setCompanyNumber(COMPANY_NUMBER);

        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);

        when(this.randomService.getRandomInteger(8)).thenReturn(COMPANY_NUMBER);
        when(this.companyAuthCodeService.create(COMPANY_NUMBER)).thenReturn(mockAuthCode);
        CreatedCompany createdCompany = this.testDataService.createCompanyData();

        assertEquals(COMPANY_NUMBER, createdCompany.getCompanyNumber());
        assertEquals(AUTH_CODE, createdCompany.getAuthCode());
    }

    @Test
    void deleteCompanyData() throws NoDataFoundException, DataException {
        this.testDataService.deleteCompanyData(COMPANY_NUMBER);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(officerListService, times(1)).delete(COMPANY_NUMBER);
        verify(pscService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
    }
}