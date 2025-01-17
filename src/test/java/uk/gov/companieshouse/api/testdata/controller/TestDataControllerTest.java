package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.InvalidAuthCodeException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.DeleteCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.model.rest.UserData;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;
import uk.gov.companieshouse.api.testdata.service.AcspMembersService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.TestDataService;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;
import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;


@ExtendWith(MockitoExtension.class)
class TestDataControllerTest {

    @Mock
    private TestDataService testDataService;

    @Mock
    private CompanyAuthCodeService companyAuthCodeService;

    @Mock
    private AcspMembersService acspMembersService;

    @InjectMocks
    private TestDataController testDataController;

    @Captor
    private ArgumentCaptor<CompanySpec> specCaptor;

    @Test
    void createCompany() throws Exception {
        CompanySpec request = new CompanySpec();
        request.setJurisdiction(Jurisdiction.SCOTLAND);
        CompanyData company =
                new CompanyData("12345678", "123456", "http://localhost:4001/company/12345678");

        when(this.testDataService.createCompanyData(request)).thenReturn(company);
        ResponseEntity<CompanyData> response = this.testDataController.createCompany(request);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createCompanyNoRequest() throws Exception {
        CompanyData company =
                new CompanyData("12345678", "123456", "http://localhost:4001/company/12345678");

        when(this.testDataService.createCompanyData(any())).thenReturn(company);
        ResponseEntity<CompanyData> response = this.testDataController.createCompany(null);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        verify(testDataService).createCompanyData(specCaptor.capture());
        CompanySpec usedSpec = specCaptor.getValue();

        assertEquals(Jurisdiction.ENGLAND_WALES, usedSpec.getJurisdiction());
    }

    @Test
    void createCompanyDefaultJurisdiction() throws Exception {
        CompanySpec request = new CompanySpec();
        CompanyData company =
                new CompanyData("12345678", "123456", "http://localhost:4001/company/12345678");

        when(this.testDataService.createCompanyData(request)).thenReturn(company);
        ResponseEntity<CompanyData> response = this.testDataController.createCompany(request);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // england/wales is the default jurisdiction
        assertEquals(Jurisdiction.ENGLAND_WALES, request.getJurisdiction());
    }

    @Test
    void createCompanyException() throws Exception {
        CompanySpec request = new CompanySpec();
        request.setJurisdiction(Jurisdiction.NI);
        Throwable exception = new DataException("Error message");
        when(this.testDataService.createCompanyData(request)).thenThrow(exception);
        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataController.createCompany(request));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteCompany() throws Exception {
        final String companyNumber = "123456";
        final DeleteCompanyRequest request = new DeleteCompanyRequest();
        request.setAuthCode("222222");
        final boolean validAuthCode = true;

        when(companyAuthCodeService.verifyAuthCode(companyNumber, request.getAuthCode()))
                .thenReturn(validAuthCode);

        ResponseEntity<Void> response =
                this.testDataController.deleteCompany(companyNumber, request);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(testDataService).deleteCompanyData(companyNumber);
    }

    @Test
    void deleteCompanyDataException() throws Exception {
        final String companyNumber = "123456";
        final DeleteCompanyRequest request = new DeleteCompanyRequest();
        request.setAuthCode("222222");
        final boolean validAuthCode = true;

        when(companyAuthCodeService.verifyAuthCode(companyNumber, request.getAuthCode()))
                .thenReturn(validAuthCode);

        DataException ex = new DataException("Error message");
        doThrow(ex).when(this.testDataService).deleteCompanyData(companyNumber);

        DataException thrown =
                assertThrows(
                        DataException.class,
                        () -> this.testDataController.deleteCompany(companyNumber, request));
        assertEquals(ex, thrown);
    }

    @Test
    void deleteCompanyInvalidAuthCode() throws Exception {
        final String companyNumber = "123456";
        final DeleteCompanyRequest request = new DeleteCompanyRequest();
        request.setAuthCode("222222");
        final boolean validAuthCode = false;

        when(companyAuthCodeService.verifyAuthCode(companyNumber, request.getAuthCode()))
                .thenReturn(validAuthCode);

        InvalidAuthCodeException thrown =
                assertThrows(
                        InvalidAuthCodeException.class,
                        () -> this.testDataController.deleteCompany(companyNumber, request));
        assertEquals(companyNumber, thrown.getCompanyNumber());
    }

    @Test
    void deleteCompanyNoAuthCodeFound() throws Exception {
        final String companyNumber = "123456";
        final DeleteCompanyRequest request = new DeleteCompanyRequest();
        request.setAuthCode("222222");
        NoDataFoundException ex = new NoDataFoundException("no auth code");

        when(companyAuthCodeService.verifyAuthCode(
                companyNumber, request.getAuthCode())).thenThrow(ex);

        NoDataFoundException thrown =
                assertThrows(
                        NoDataFoundException.class,
                        () -> this.testDataController.deleteCompany(companyNumber, request));
        assertEquals(ex, thrown);
    }

    @Test
    void createUser() throws Exception {
        UserSpec request = new UserSpec();
        request.setPassword("password");
        UserData user = new UserData("userId", "email@example.com", "Forename", "Surname");

        when(this.testDataService.createUserData(request)).thenReturn(user);
        ResponseEntity<UserData> response = this.testDataController.createUser(request);

        assertEquals(user, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createUserException() throws Exception {
        UserSpec request = new UserSpec();
        request.setPassword("password");
        Throwable exception = new DataException("Error message");

        when(this.testDataService.createUserData(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataController.createUser(request));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteUser() throws Exception {
        final String userId = "userId";

        when(this.testDataService.deleteUserData(userId)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = this.testDataController.deleteUser(userId);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(testDataService).deleteUserData(userId);
    }

    @Test
    void deleteUserException() throws Exception {
        final String userId = "userId";
        Throwable exception = new DataException("Error message");

        when(this.testDataService.deleteUserData(userId)).thenThrow(exception);

        DataException thrown =
                assertThrows(DataException.class, () -> this.testDataController.deleteUser(userId));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteUserNotFound() throws Exception {
        final String userId = "userId";

        when(this.testDataService.deleteUserData(userId)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = this.testDataController.deleteUser(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("userId", Objects.requireNonNull(response.getBody()).get("user id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));

        verify(testDataService).deleteUserData(userId);
    }

    @Test
    void createAcspMember_Success() throws DataException {
        // Given
        AcspMembersSpec request = new AcspMembersSpec();
        request.setUserId("USER123");
        request.setAcspNumber("Playwright12345678");
        request.setAcspMemberId("ACSPM9999");
        request.setUserRole("member");
        request.setStatus("active");

        AcspMembersData mockResponse =
                new AcspMembersData("Playwright12345678","USER123", "ACSPM9999", "member", "active");
        // Mock the service call to return mockResponse
        when(testDataService.createAcspMembersData(request)).thenReturn(mockResponse);

        ResponseEntity<AcspMembersData> response =
                testDataController.createAcspMemeber(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("USER123", response.getBody().getUserId());
        assertEquals("ACSPM9999", response.getBody().getAcspMemberId());
        assertEquals("Playwright12345678", response.getBody().getAcspNumber());
        assertEquals("active", response.getBody().getStatus());
        assertEquals("member", response.getBody().getUserRole());

        verify(testDataService, times(1)).createAcspMembersData(request);
    }

    @Test
    void createAcspMember_ExceptionThrown() throws DataException {
        // Given
        AcspMembersSpec request = new AcspMembersSpec();
        request.setUserId("USER123");
        DataException ex = new DataException("Error creating ACSP member");

        when(testDataService.createAcspMembersData(request)).thenThrow(ex);

        // When & Then
        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.createAcspMemeber(request));
        assertEquals("Error creating ACSP member", thrown.getMessage());

        verify(testDataService, times(1)).createAcspMembersData(request);
    }

    // You can optionally test "no request" scenarios or validation logic
    // if your code handles them. For example:
    @Test
    void createAcspMember_NullRequest() throws DataException {
        // If your code will throw an error or handle a null request, test accordingly
        DataException ex = new DataException("AcspMembersSpec cannot be null");
        when(testDataService.createAcspMembersData(null)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.createAcspMemeber(null));
        assertEquals("AcspMembersSpec cannot be null", thrown.getMessage());

        verify(testDataService, times(1)).createAcspMembersData(null);
    }

    @Test
    void deleteAcspMember_Found() throws DataException {
        // Given
        String acspMemberId = "ACSPM001";

        AcspMembers mockMember = new AcspMembers();
        mockMember.setAcspMemberId(acspMemberId);
        mockMember.setAcspNumber("PlaywrightACSP12345678");

        when(acspMembersService.getAcspMembersById(acspMemberId))
                .thenReturn(Optional.of(mockMember));

        when(testDataService.deleteAcspProfileData("PlaywrightACSP12345678")).thenReturn(true);
        when(testDataService.deleteAcspMembersData(acspMemberId)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response =
                testDataController.deleteAcspMember(acspMemberId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(acspMembersService).getAcspMembersById(acspMemberId);
        verify(testDataService).deleteAcspProfileData("PlaywrightACSP12345678");
        verify(testDataService).deleteAcspMembersData(acspMemberId);
    }

    @Test
    void deleteAcspMember_AssociatedProfileNotFoundButMemberDeleted() throws DataException {
        // Given
        String acspMemberId = "ACSPM001";

        AcspMembers mockMember = new AcspMembers();
        mockMember.setAcspMemberId(acspMemberId);
        mockMember.setAcspNumber("PlaywrightACSP987654"); // or any valid string
        when(acspMembersService.getAcspMembersById(acspMemberId))
                .thenReturn(Optional.of(mockMember));

        when(testDataService.deleteAcspProfileData("PlaywrightACSP987654")).thenReturn(false);
        when(testDataService.deleteAcspMembersData(acspMemberId)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = testDataController.deleteAcspMember(acspMemberId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(acspMembersService, times(1)).getAcspMembersById(acspMemberId);
        verify(testDataService, times(1)).deleteAcspProfileData("PlaywrightACSP987654");
        verify(testDataService, times(1)).deleteAcspMembersData(acspMemberId);
    }


    @Test
    void deleteAcspMember_NotFound() throws DataException {
        String acspMemberId = "ACSPM999";

        when(acspMembersService.getAcspMembersById(acspMemberId))
                .thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> response =
                testDataController.deleteAcspMember(acspMemberId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(acspMemberId, response.getBody().get("Acsp Member Id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));

        verify(testDataService, never()).deleteAcspProfileData(any());
        verify(testDataService, never()).deleteAcspMembersData(acspMemberId);
    }


    @Test
    void deleteAcspMember_Exception() throws DataException {

        String acspMemberId = "ACSPM001";
        DataException ex = new DataException("Error deleting acsp member");

        AcspMembers mockMember = new AcspMembers();
        mockMember.setAcspMemberId(acspMemberId);
        mockMember.setAcspNumber("PlaywrightACSP123456");
        when(acspMembersService.getAcspMembersById(acspMemberId))
                .thenReturn(Optional.of(mockMember));

        doThrow(ex).when(testDataService).deleteAcspProfileData("PlaywrightACSP123456");

        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.deleteAcspMember(acspMemberId));
        assertEquals("Error deleting acsp member", thrown.getMessage());

        verify(acspMembersService, times(1)).getAcspMembersById(acspMemberId);
        verify(testDataService, times(1)).deleteAcspProfileData("PlaywrightACSP123456");
    }


}
