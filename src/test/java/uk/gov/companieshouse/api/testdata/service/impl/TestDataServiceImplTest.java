package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.entity.Officer;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class TestDataServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String AUTH_CODE = "123456";

    @Mock
    private DataService<CompanyProfile> companyProfileService;
    @Mock
    private DataService<FilingHistory> filingHistoryService;
    @Mock
    private DataService<Officer> officerListService;
    @Mock
    private DataService<CompanyAuthCode> companyAuthCodeService;
    @Mock
    private DataService<Appointment> appointmentService;
    @Mock
    private DataService<CompanyMetrics> metricsService;
    @Mock
    private DataService<CompanyPscStatement> companyPscStatementService;
    @Mock
    private RandomService randomService;
    @InjectMocks
    private TestDataServiceImpl testDataService;

    @Test
    void createCompanyData() throws DataException {
        CompanyProfile mockCompany = new CompanyProfile();
        mockCompany.setCompanyNumber(COMPANY_NUMBER);

        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);

        when(this.randomService.getNumber(8)).thenReturn(Long.valueOf(COMPANY_NUMBER));
        when(this.companyAuthCodeService.create(COMPANY_NUMBER)).thenReturn(mockAuthCode);
        CompanyData createdCompany = this.testDataService.createCompanyData();

        verify(companyProfileService, times(1)).create(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).create(COMPANY_NUMBER);
        verify(officerListService, times(1)).create(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).create(COMPANY_NUMBER);
        verify(appointmentService, times(1)).create(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).create(COMPANY_NUMBER);
        verify(metricsService, times(1)).create(COMPANY_NUMBER);

        assertEquals(COMPANY_NUMBER, createdCompany.getCompanyNumber());
        assertEquals("/company/" + COMPANY_NUMBER, createdCompany.getCompanyUri());
        assertEquals(AUTH_CODE, createdCompany.getAuthCode());
    }

    @Test
    void deleteCompanyData() throws NoDataFoundException, DataException {
        this.testDataService.deleteCompanyData(COMPANY_NUMBER);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(officerListService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
    }
}