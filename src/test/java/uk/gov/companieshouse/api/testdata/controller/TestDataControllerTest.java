package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.InvalidAuthCodeException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.AccountPenaltiesData;
import uk.gov.companieshouse.api.testdata.model.rest.AccountPenaltyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesData;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.DeleteAppealsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.DeleteCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityData;
import uk.gov.companieshouse.api.testdata.model.rest.IdentitySpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.model.rest.PenaltyData;
import uk.gov.companieshouse.api.testdata.model.rest.PostcodesData;
import uk.gov.companieshouse.api.testdata.model.rest.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.UserData;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;

import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.TestDataService;

@ExtendWith(MockitoExtension.class)
class TestDataControllerTest {

    @Mock
    private TestDataService testDataService;

    @Mock
    private CompanyAuthCodeService companyAuthCodeService;

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
        assertEquals("userId", response.getBody().get("user id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));

        verify(testDataService).deleteUserData(userId);
    }

    @Test
    void createIdentity() throws Exception {
        IdentitySpec request = new IdentitySpec();
        when(this.testDataService.createIdentityData(request))
                .thenReturn(new IdentityData("identityId"));

        ResponseEntity<Map<String, Object>> response
                = this.testDataController.createIdentity(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("identityId", Objects.requireNonNull(response.getBody()).get("identity id"));
    }

    @Test
    void createIdentityException() throws Exception {
        IdentitySpec request = new IdentitySpec();
        DataException exception = new DataException("Error message");

        when(this.testDataService.createIdentityData(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataController.createIdentity(request));
        assertEquals(exception.getMessage(), thrown.getMessage()); // Match message
    }

    @Test
    void deleteIdentity() throws Exception {
        final String identityId = "identityId";

        when(this.testDataService.deleteIdentityData(identityId)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response
                = this.testDataController.deleteIdentity(identityId);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(testDataService).deleteIdentityData(identityId);
    }

    @Test
    void deleteIdentityException() throws Exception {
        final String identityId = "identityId";
        DataException exception = new DataException("Error message");

        when(this.testDataService.deleteIdentityData(identityId)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataController.deleteIdentity(identityId));
        assertEquals(exception.getMessage(), thrown.getMessage()); // Match message
    }

    @Test
    void deleteIdentityNotFound() throws Exception {
        final String identityId = "identityId";

        when(this.testDataService.deleteIdentityData(identityId)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response
                = this.testDataController.deleteIdentity(identityId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("identityId", response.getBody().get("identity id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));

        verify(testDataService).deleteIdentityData(identityId);
    }

    @Test
    void createAcspMember() throws Exception {
        AcspMembersSpec request = new AcspMembersSpec();
        request.setUserId("rsf3pdwywvse5yz55mfodfx8");
        request.setUserRole("role");
        request.setStatus("active");
        request.setAcspProfile(new AcspProfileSpec());

        AcspMembersData acspMember = new AcspMembersData(
                new ObjectId(), "acspNumber", "userId", "active", "role");

        when(this.testDataService.createAcspMembersData(request)).thenReturn(acspMember);
        ResponseEntity<AcspMembersData> response
                = this.testDataController.createAcspMember(request);

        assertEquals(acspMember, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createAcspMemberException() throws Exception {
        AcspMembersSpec request = new AcspMembersSpec();
        Throwable exception = new DataException("Error message");

        when(this.testDataService.createAcspMembersData(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataController.createAcspMember(request));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteAcspMember() throws Exception {
        final String acspMemberId = "memberId";

        when(this.testDataService.deleteAcspMembersData(acspMemberId)).thenReturn(true);
        ResponseEntity<Map<String, Object>> response
                = this.testDataController.deleteAcspMember(acspMemberId);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(testDataService).deleteAcspMembersData(acspMemberId);
    }

    @Test
    void deleteAcspMemberNotFound() throws Exception {
        final String acspMemberId = "memberId";

        when(this.testDataService.deleteAcspMembersData(acspMemberId)).thenReturn(false);
        ResponseEntity<Map<String, Object>> response
                = this.testDataController.deleteAcspMember(acspMemberId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("memberId", Objects.requireNonNull(response.getBody()).get("acsp-member-id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));

        verify(testDataService).deleteAcspMembersData(acspMemberId);
    }

    @Test
    void deleteAcspMemberException() throws Exception {
        final String acspMemberId = "memberId";
        Throwable exception = new DataException("Error message");

        when(this.testDataService.deleteAcspMembersData(acspMemberId)).thenThrow(exception);

        DataException thrown = assertThrows(
                DataException.class, () -> this.testDataController.deleteAcspMember(acspMemberId));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteAppealSuccess() throws Exception {
        DeleteAppealsRequest request = new DeleteAppealsRequest();
        request.setCompanyNumber("12345678");
        request.setPenaltyReference("PR123");

        when(testDataService.deleteAppealsData(
                request.getCompanyNumber(), request.getPenaltyReference()))
                .thenReturn(true);

        ResponseEntity<Void> response = testDataController.deleteAppeal(request);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(testDataService, times(1))
                .deleteAppealsData(request.getCompanyNumber(), request.getPenaltyReference());
    }

    @Test
    void deleteAppealNotFound() throws Exception {
        DeleteAppealsRequest request = new DeleteAppealsRequest();
        request.setCompanyNumber("12345678");
        request.setPenaltyReference("PR123");

        when(testDataService.deleteAppealsData(
                request.getCompanyNumber(), request.getPenaltyReference()))
                .thenReturn(false);

        ResponseEntity<Void> response = testDataController.deleteAppeal(request);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(testDataService, times(1))
                .deleteAppealsData(request.getCompanyNumber(), request.getPenaltyReference());
    }

    @Test
    void deleteAppealBadRequest() throws Exception {
        ResponseEntity<Void> response = testDataController.deleteAppeal(null);

        assertNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(testDataService, times(0)).deleteAppealsData(anyString(), anyString());
    }

    @Test
    void createCertificateSuccess() throws Exception {
        CertificatesData.CertificateEntry entry1 = new CertificatesData.CertificateEntry(
            "CRT-834723-192847", "2025-04-14T12:00:00Z", "2025-04-14T12:00:00Z"
        );
        CertificatesData.CertificateEntry entry2 = new CertificatesData.CertificateEntry(
            "CRT-912834-238472", "2025-04-14T12:05:00Z", "2025-04-14T12:05:00Z"
        );

        // Use LinkedList to support getFirst()
        List<CertificatesData.CertificateEntry> entries = new java.util.LinkedList<>(List.of(entry1, entry2));
        CertificatesData certificateData = new CertificatesData(entries);

        CertificatesSpec request = new CertificatesSpec();
        request.setCompanyNumber("12345678");
        request.setDescriptionCertificate("incorporation");

        when(testDataService.createCertificatesData(request)).thenReturn(certificateData);

        ResponseEntity<CertificatesData> response = testDataController.createCertificates(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).getCertificates().size());

        assertEquals("CRT-834723-192847", response.getBody().getCertificates().getFirst().getId());
        assertEquals("CRT-912834-238472", response.getBody().getCertificates().get(1).getId());
    }



    @Test
    void createCertificateException() throws Exception {
        CertificatesSpec request = new CertificatesSpec();
        request.setCompanyNumber("12345678");
        request.setDescriptionCertificate("incorporation");

        DataException exception = new DataException("Error creating certificate");
        when(testDataService.createCertificatesData(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.createCertificates(request));
        assertEquals(exception.getMessage(), thrown.getMessage());
    }

    @Test
    void deleteCertificateSuccess() throws Exception {
        final String certificateId = "CRT-834723-192847";

        when(testDataService.deleteCertificatesData(certificateId)).thenReturn(true);
        ResponseEntity<Map<String, Object>> response
                = testDataController.deleteCertificates(certificateId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(testDataService).deleteCertificatesData(certificateId);
    }


    @Test
    void deleteCertificateNotFound() throws Exception {
        final String certificateId = String.valueOf(1234);

        when(testDataService.deleteCertificatesData(certificateId)).thenReturn(false);
        ResponseEntity<Map<String, Object>>
                response = testDataController.deleteCertificates(certificateId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("1234", Objects.requireNonNull(response.getBody()).get("id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));
        verify(testDataService).deleteCertificatesData(certificateId);
    }

    @Test
    void deleteCertificateException() throws Exception {
        final String certificateId = "cert123";
        DataException exception = new DataException("Failed to delete certificate");

        when(testDataService.deleteCertificatesData(certificateId)).thenThrow(exception);
        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.deleteCertificates(certificateId));

        assertEquals(exception.getMessage(), thrown.getMessage());
    }

    @Test
    void getAccountPenalty() throws Exception {
        String penaltyRef = "A1234567";
        String companyCode = "LP";
        String customerCode = "NI23456";
        AccountPenaltyRequest request = new AccountPenaltyRequest();
        request.setCompanyCode(companyCode);
        request.setCustomerCode(customerCode);

        PenaltyData penalty = createPenaltyData(companyCode,
                customerCode, penaltyRef, 250.0, false);

        AccountPenaltiesData accountPenaltiesData = createAccountPenaltiesData(
                companyCode, penalty);

        when(this.testDataService.getAccountPenaltyData(companyCode, customerCode, penaltyRef))
                .thenReturn(accountPenaltiesData);
        ResponseEntity<AccountPenaltiesData> response = this.testDataController
                .getPenalty(penaltyRef, request);

        assertEquals(accountPenaltiesData, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAccountPenaltyNotFound() throws Exception {
        String penaltyRef = "A1234567";
        AccountPenaltyRequest request = new AccountPenaltyRequest();
        request.setCompanyCode("LP");
        request.setCustomerCode("A1234567");

        Throwable exception = new NoDataFoundException("penalty not found");

        when(this.testDataService.getAccountPenaltyData(
                request.getCompanyCode(), request.getCustomerCode(), penaltyRef))
                .thenThrow(exception);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                this.testDataController.getPenalty(penaltyRef, request));
        assertEquals(exception, thrown);
    }

    @Test
    void getAccountPenalties() throws Exception {
        String companyCode = "LP";
        String customerCode = "NI23456";
        AccountPenaltyRequest request = new AccountPenaltyRequest();
        request.setCompanyCode(companyCode);
        request.setCustomerCode(customerCode);

        PenaltyData penalty = createPenaltyData(companyCode,
                customerCode, "A1234567", 250.0, false);

        AccountPenaltiesData accountPenaltiesData = createAccountPenaltiesData(
                companyCode, penalty);

        when(this.testDataService.getAccountPenaltiesData(companyCode, customerCode))
                .thenReturn(accountPenaltiesData);
        ResponseEntity<AccountPenaltiesData> response = this.testDataController
                .getAccountPenalties(request);

        assertEquals(accountPenaltiesData, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void getAccountPenaltiesNotFound() throws Exception {
        AccountPenaltyRequest request = new AccountPenaltyRequest();
        request.setCompanyCode("LP");
        request.setCustomerCode("NI23456");

        Throwable exception = new NoDataFoundException("Account penalties not found");

        when(this.testDataService.getAccountPenaltiesData(
                request.getCompanyCode(), request.getCustomerCode())).thenThrow(exception);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                this.testDataController.getAccountPenalties(request));
        assertEquals(exception, thrown);
    }

    @Test
    void updateAccountPenalties() throws Exception {
        String penaltyRef = "A1234567";
        String companyCode = "LP";
        String customerCode = "NI23456";
        Instant now = Instant.now();

        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode(companyCode);
        request.setCustomerCode(customerCode);
        request.setCreatedAt(now);
        request.setClosedAt(now);
        request.setAmount(0.0);
        request.setOutstandingAmount(0.0);
        request.setIsPaid(true);

        PenaltyData penalty = createPenaltyData(companyCode,
                customerCode, penaltyRef, 0.0, true);

        AccountPenaltiesData accountPenaltiesData = new AccountPenaltiesData();
        accountPenaltiesData.setCompanyCode(companyCode);
        accountPenaltiesData.setCreatedAt(now);
        accountPenaltiesData.setClosedAt(now);
        accountPenaltiesData.setPenalties(Collections.singletonList(penalty));

        when(this.testDataService.updateAccountPenaltiesData(penaltyRef, request))
                .thenReturn(accountPenaltiesData);
        ResponseEntity<AccountPenaltiesData> response = this.testDataController
                .updateAccountPenalties(penaltyRef, request);

        assertEquals(accountPenaltiesData, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void updateAccountPenaltiesNotFound() throws Exception {
        Instant now = Instant.now();

        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode("LP");
        request.setCustomerCode("NI23456");
        request.setCreatedAt(now);
        request.setClosedAt(now);
        request.setAmount(0.0);
        request.setOutstandingAmount(0.0);
        request.setIsPaid(true);

        NoDataFoundException exception = new NoDataFoundException("Account penalty not found");

        String penaltyRef = "A1234567";
        when(this.testDataService.updateAccountPenaltiesData(penaltyRef, request))
                .thenThrow(exception);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                this.testDataController.updateAccountPenalties(penaltyRef, request));
        assertEquals(exception, thrown);
    }


    @Test
    void deleteAccountPenaltiesSuccess() throws Exception {
        AccountPenaltyRequest request = new AccountPenaltyRequest();
        request.setCompanyCode("LP");
        request.setCustomerCode("NI23456");

        when(this.testDataService.deleteAccountPenaltiesData(
                request.getCompanyCode(), request.getCustomerCode()))
                .thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<Void> response = testDataController.deleteAccountPenalties(request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(testDataService, times(1)).deleteAccountPenaltiesData(
                request.getCompanyCode(), request.getCustomerCode());
    }

    @Test
    void deleteAccountPenaltiesNotFound() throws Exception {
        AccountPenaltyRequest request = new AccountPenaltyRequest();
        request.setCompanyCode("NI23456");
        request.setCustomerCode("LP");

        NoDataFoundException exception = new NoDataFoundException("penalty not found");
        when(this.testDataController.deleteAccountPenalties(request)).thenThrow(exception);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                this.testDataController.deleteAccountPenalties(request));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteAccountPenaltiesOtherError() throws Exception {
        AccountPenaltyRequest request = new AccountPenaltyRequest();
        request.setCompanyCode("NI23456");
        request.setCustomerCode("LP");

        DataException exception = new DataException("error during deletion");
        when(this.testDataController.deleteAccountPenalties(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataController.deleteAccountPenalties(request));
        assertEquals(exception, thrown);
    }

    private static AccountPenaltiesData createAccountPenaltiesData(String companyCode,
                                                                   PenaltyData penalty) {
        AccountPenaltiesData accountPenaltiesData = new AccountPenaltiesData();
        accountPenaltiesData.setCreatedAt(Instant.now());
        accountPenaltiesData.setCompanyCode(companyCode);
        accountPenaltiesData.setPenalties(Collections.singletonList(penalty));
        return accountPenaltiesData;
    }

    private PenaltyData createPenaltyData(String companyCode, String customerCode,
                                          String penaltyRef, double amount, boolean paid) {
        PenaltyData penalty = new PenaltyData();
        penalty.setCompanyCode(companyCode);
        penalty.setCustomerCode(customerCode);
        penalty.setTransactionReference(penaltyRef);
        penalty.setTransactionDate("2025-02-25");
        penalty.setMadeUpDate("2025-02-12");
        penalty.setAmount(amount);
        penalty.setOutstandingAmount(amount);
        penalty.setIsPaid(paid);
        penalty.setAccountStatus("CHS");
        penalty.setDunningStatus("PEN1");
        return penalty;
    }

    @Test
    void getPostcodeSuccess() throws Exception {
        String country = "England";
        PostcodesData postcodesData =
                new PostcodesData(12, "Thoroughfare Name", "Dependent Locality",
                        "Locality Post Town", "ABC 123");

        when(testDataService.getPostcodes(country)).thenReturn(postcodesData);

        ResponseEntity<PostcodesData> response = testDataController.getPostcode(country);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postcodesData, response.getBody());
        verify(testDataService, times(1)).getPostcodes(country);
    }

    @Test
    void getPostcodeNoDataFound() throws Exception {
        String country = "UnknownCountry";

        when(testDataService.getPostcodes(country)).thenReturn(null);

        ResponseEntity<PostcodesData> response = testDataController.getPostcode(country);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(testDataService, times(1)).getPostcodes(country);
    }

    @Test
    void getPostcodeDataException() throws Exception {
        String country = "ErrorCountry";

        when(testDataService.getPostcodes(country))
                .thenThrow(new DataException("Error retrieving postcodes"));

        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.getPostcode(country));

        assertEquals("Error retrieving postcodes", thrown.getMessage());
        verify(testDataService, times(1)).getPostcodes(country);
    }

    @Test
    void testGetPostcodeIsNull() throws Exception {
        ResponseEntity<PostcodesData> response = testDataController.getPostcode(null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(testDataService, times(0)).getPostcodes(anyString());
    }
}
