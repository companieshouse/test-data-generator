package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import java.util.Optional;
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
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspMembersRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspProfileRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.AdminPermissionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertificatesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertifiedCopiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CombinedSicActivitiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UserCompanyAssociationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UserRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AccountPenaltiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspMembersResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AdminPermissionsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CertificatesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyWithPopulatedStructureRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CombinedSicActivitiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyAuthCodeResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PopulatedCompanyDetailsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.DeleteAppealsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.DeleteCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.DisqualificationsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityVerificationResponse;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.model.rest.request.MissingImageDeliveriesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.PenaltyResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltySpec;
import uk.gov.companieshouse.api.testdata.model.rest.response.PostcodesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.TransactionsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.TransactionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.UserCompanyAssociationResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.UserResponse;
import uk.gov.companieshouse.api.testdata.service.AccountPenaltiesService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.TestDataService;
import uk.gov.companieshouse.api.testdata.service.VerifiedIdentityService;

@ExtendWith(MockitoExtension.class)
class TestDataControllerTest {

    private static final String PENALTY_ID = "685abc4b9b34c84d4d2f5af6";
    private static final String COMPANY_CODE = "LP";
    private static final String CUSTOMER_CODE = "NI23456";
    private static final String PENALTY_REFERENCE = "A1234567";
    private static final String COMPANY_NUMBER = "TC123456";
    private static final String AUTH_CODE_APPROVAL_ROUTE =
            "auth_code";
    private static final String CONFIRMED_STATUS = "confirmed";
    private static final String USER_ID = "userId";
    private static final String ASSOCIATION_ID = "associationId";
    private static final String SIC_ACTIVITY_ID = "6242bbbbafaaaa93274b2efd";
    private static final String TRANSACTION_ID = "412123-412123-412123";

    @Mock
    private TestDataService testDataService;

    @Mock
    private CompanyAuthCodeService companyAuthCodeService;

    @InjectMocks
    private TestDataController testDataController;

    @Mock
    private AccountPenaltiesService accountPenaltiesService;

    @Mock
    private VerifiedIdentityService<IdentityVerificationResponse> verifiedIdentityService;

    @Captor
    private ArgumentCaptor<CompanyRequest> specCaptor;

    @Captor
    private ArgumentCaptor<PublicCompanyRequest> publicSpecCaptor;

    public static String companyUri = "http://localhost:1234/company/12345678";

    @Test
    void createCompanyInternal() throws Exception {
        CompanyRequest request = new CompanyRequest();
        request.setJurisdiction(JurisdictionType.SCOTLAND);
        CompanyProfileResponse company =
                new CompanyProfileResponse("12345678", "123456", companyUri);

        when(this.testDataService.createCompanyData(request)).thenReturn(company);
        ResponseEntity<CompanyProfileResponse> response = this.testDataController.createCompanyInternal(request);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createCompanyInternalNoRequest() throws Exception {
        CompanyProfileResponse company =
                new CompanyProfileResponse("12345678", "123456", companyUri);

        when(this.testDataService.createCompanyData(any())).thenReturn(company);
        ResponseEntity<CompanyProfileResponse> response = this.testDataController.createCompanyInternal(null);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        verify(testDataService).createCompanyData(specCaptor.capture());
        CompanyRequest usedSpec = specCaptor.getValue();

        assertEquals(JurisdictionType.ENGLAND_WALES, usedSpec.getJurisdiction());
    }

    @Test
    void createCompanyInternalDefaultJurisdiction() throws Exception {
        CompanyRequest request = new CompanyRequest();
        CompanyProfileResponse company =
                new CompanyProfileResponse("12345678", "123456", companyUri);

        when(this.testDataService.createCompanyData(request)).thenReturn(company);
        ResponseEntity<CompanyProfileResponse> response = this.testDataController.createCompanyInternal(request);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // england/wales is the default jurisdiction
        assertEquals(JurisdictionType.ENGLAND_WALES, request.getJurisdiction());
    }

    @Test
    void createCompanyInternalException() throws Exception {
        CompanyRequest request = new CompanyRequest();
        request.setJurisdiction(JurisdictionType.NI);
        Throwable exception = new DataException("Error message");
        when(this.testDataService.createCompanyData(request)).thenThrow(exception);
        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataController.createCompanyInternal(request));
        assertEquals(exception, thrown);
    }

    @Test
    void createCompanyPublic() throws Exception {
        var request = new PublicCompanyRequest();
        request.setJurisdiction(JurisdictionType.SCOTLAND);
        var company =
                new CompanyProfileResponse("12345678", "123456", companyUri);

        when(this.testDataService.createPublicCompanyData(request)).thenReturn(company);
        ResponseEntity<CompanyProfileResponse> response = this.testDataController.createCompany(request);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createDefaultPublicCompanyWithEmptyRequest() throws Exception {
        var company =
                new CompanyProfileResponse("12345678", "123456", companyUri);

        when(this.testDataService.createPublicCompanyData(any())).thenReturn(company);
        ResponseEntity<CompanyProfileResponse> response = this.testDataController.createCompany(null);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        verify(testDataService).createPublicCompanyData(publicSpecCaptor.capture());
        var usedSpec = publicSpecCaptor.getValue();

        assertEquals(JurisdictionType.ENGLAND_WALES, usedSpec.getJurisdiction());
    }

    @Test
    void createCompanyPublicDefaultJurisdiction() throws Exception {
        var request = new PublicCompanyRequest();
        var company =
                new CompanyProfileResponse("12345678", "123456", companyUri);

        when(this.testDataService.createPublicCompanyData(request)).thenReturn(company);
        ResponseEntity<CompanyProfileResponse> response = this.testDataController.createCompany(request);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // england/wales is the default jurisdiction
        assertEquals(JurisdictionType.ENGLAND_WALES, request.getJurisdiction());
    }

    @Test
    void createCompanyPublicException() throws Exception {
        var request = new PublicCompanyRequest();
        request.setJurisdiction(JurisdictionType.NI);
        Throwable exception = new DataException("Error message");
        when(this.testDataService.createPublicCompanyData(request)).thenThrow(exception);
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
        UserRequest request = new UserRequest();
        request.setPassword("password");
        UserResponse user = new UserResponse("userId", "email@example.com", "Forename", "Surname");

        when(this.testDataService.createUserData(request)).thenReturn(user);
        ResponseEntity<UserResponse> response = this.testDataController.createUser(request);

        assertEquals(user, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createUserException() throws Exception {
        UserRequest request = new UserRequest();
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
    void createAcspMember() throws Exception {
        AcspMembersRequest request = new AcspMembersRequest();
        request.setUserId("rsf3pdwywvse5yz55mfodfx8");
        request.setUserRole("role");
        request.setStatus("active");
        request.setAcspProfile(new AcspProfileRequest());

        AcspMembersResponse acspMember = new AcspMembersResponse(
                new ObjectId(), "acspNumber", "userId", "active", "role");

        when(this.testDataService.createAcspMembersData(request)).thenReturn(acspMember);
        ResponseEntity<AcspMembersResponse> response
                = this.testDataController.createAcspMember(request);

        assertEquals(acspMember, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createAcspMemberException() throws Exception {
        AcspMembersRequest request = new AcspMembersRequest();
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
        CertificatesResponse certificateData = getCertificatesData();

        CertificatesRequest request = new CertificatesRequest();
        request.setCompanyNumber("12345678");

        when(testDataService.createCertificatesData(request)).thenReturn(certificateData);

        ResponseEntity<CertificatesResponse> response = testDataController.createCertificates(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).getCertificates().size());

        assertEquals("CRT-834723-192847", response.getBody().getCertificates().getFirst().getId());
        assertEquals("CRT-912834-238472", response.getBody().getCertificates().get(1).getId());
    }



    @Test
    void createCertificateException() throws Exception {
        CertificatesRequest request = new CertificatesRequest();
        request.setCompanyNumber("12345678");

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
    void deleteCertifiedCopiesSuccess() throws Exception {
        final String certifiedCopiesId = "CCD-834723-192847";

        when(testDataService.deleteCertifiedCopiesData(certifiedCopiesId)).thenReturn(true);
        ResponseEntity<Map<String, Object>> response
            = testDataController.deleteCertifiedCopies(certifiedCopiesId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(testDataService).deleteCertifiedCopiesData(certifiedCopiesId);
    }

    @Test
    void deleteCertifiedCopiesNotFound() throws Exception {
        final String certifiedCopiesId = String.valueOf(1234);

        when(testDataService.deleteCertifiedCopiesData(certifiedCopiesId)).thenReturn(false);
        ResponseEntity<Map<String, Object>>
            response = testDataController.deleteCertifiedCopies(certifiedCopiesId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("1234", Objects.requireNonNull(response.getBody()).get("id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));
        verify(testDataService).deleteCertifiedCopiesData(certifiedCopiesId);
    }

    @Test
    void deleteCertifiedCopiesException() throws Exception {
        final String certifiedCopiesId = "cert123";
        DataException exception = new DataException("Failed to delete certificate");

        when(testDataService.deleteCertifiedCopiesData(certifiedCopiesId)).thenThrow(exception);
        DataException thrown = assertThrows(DataException.class, () ->
            testDataController.deleteCertifiedCopies(certifiedCopiesId));

        assertEquals(exception.getMessage(), thrown.getMessage());
    }

    @Test
    void deleteMissingImageDeliveriesSuccess() throws Exception {
        final String missingImageDeliveriesId = "MID-834723-192847";

        when(testDataService.deleteMissingImageDeliveriesData(missingImageDeliveriesId)).thenReturn(true);
        ResponseEntity<Map<String, Object>> response
            = testDataController.deleteMissingImageDeliveries(missingImageDeliveriesId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(testDataService).deleteMissingImageDeliveriesData(missingImageDeliveriesId);
    }

    @Test
    void deleteMissingImageDeliveriesNotFound() throws Exception {
        final String missingImageDeliveriesId = String.valueOf(1234);

        when(testDataService.deleteMissingImageDeliveriesData(missingImageDeliveriesId)).thenReturn(false);
        ResponseEntity<Map<String, Object>>
            response = testDataController.deleteMissingImageDeliveries(missingImageDeliveriesId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("1234", Objects.requireNonNull(response.getBody()).get("id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));
        verify(testDataService).deleteMissingImageDeliveriesData(missingImageDeliveriesId);
    }

    @Test
    void deleteMissingImageDeliveriesException() throws Exception {
        final String missingImageDeliveriesId = "cert123";
        DataException exception = new DataException("Failed to delete missing image deliveries");

        when(testDataService.deleteMissingImageDeliveriesData(missingImageDeliveriesId)).thenThrow(exception);
        DataException thrown = assertThrows(DataException.class, () ->
            testDataController.deleteMissingImageDeliveries(missingImageDeliveriesId));

        assertEquals(exception.getMessage(), thrown.getMessage());
    }

    @Test
    void getAccountPenaltyNotFound() throws Exception {
        when(this.testDataService.getAccountPenaltiesData(PENALTY_ID))
                .thenThrow(new NoDataFoundException("no account penalties"));

        assertThrows(NoDataFoundException.class, () -> {
            this.testDataController.getAccountPenalties(PENALTY_ID, PENALTY_REFERENCE);
        });
    }

    @Test
    void getAccountPenalties() throws Exception {
        PenaltyRequest request = new PenaltyRequest();
        request.setId(PENALTY_ID);

        AccountPenaltiesResponse accountPenaltiesResponse = new AccountPenaltiesResponse();

        when(this.testDataService.getAccountPenaltiesData(request.getId()))
                .thenReturn(accountPenaltiesResponse);

        ResponseEntity<AccountPenaltiesResponse> response = this.testDataController
                .getAccountPenalties(request.getId(), null);

        assertEquals(accountPenaltiesResponse, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAccountPenaltiesNotFound() throws Exception {
        String penaltyId = PENALTY_ID;

        when(this.testDataService.getAccountPenaltiesData(penaltyId))
                .thenThrow(new NoDataFoundException("Account penalties not found"));

        assertThrows(NoDataFoundException.class, () -> {
            this.testDataController.getAccountPenalties(penaltyId, null);
        });
    }

    @Test
    void getAccountPenaltiesByCustomerCodeAndCompanyCode() throws Exception {
        AccountPenaltiesResponse accountPenaltiesResponse = new AccountPenaltiesResponse();

        when(this.testDataService.getAccountPenaltiesData(CUSTOMER_CODE, COMPANY_CODE))
                .thenReturn(accountPenaltiesResponse);

        ResponseEntity<AccountPenaltiesResponse> response = this.testDataController
                .getAccountPenaltiesByCustomerCodeAndCompanyCode(CUSTOMER_CODE, COMPANY_CODE);

        assertEquals(accountPenaltiesResponse, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAccountPenaltiesByCustomerCodeAndCompanyCodeNotFound() throws Exception {
        when(this.testDataService.getAccountPenaltiesData(CUSTOMER_CODE, COMPANY_CODE))
                .thenThrow(new NoDataFoundException("Account penalties not found"));

        assertThrows(NoDataFoundException.class, () -> {
            this.testDataController.getAccountPenaltiesByCustomerCodeAndCompanyCode(CUSTOMER_CODE, COMPANY_CODE);
        });
    }

    @Test
    void updateAccountPenalties() throws Exception {
        String companyCode = COMPANY_CODE;
        String customerCode = CUSTOMER_CODE;
        Instant now = Instant.now();

        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode(companyCode);
        request.setCustomerCode(customerCode);
        request.setCreatedAt(now);
        request.setClosedAt(now);
        request.setAmount(0.0);
        request.setOutstandingAmount(0.0);
        request.setIsPaid(true);

        PenaltyResponse penalty = createPenaltyData(companyCode,
                customerCode, PENALTY_REFERENCE, 0.0, true);

        AccountPenaltiesResponse accountPenaltiesResponse = new AccountPenaltiesResponse();
        accountPenaltiesResponse.setCompanyCode(companyCode);
        accountPenaltiesResponse.setCreatedAt(now);
        accountPenaltiesResponse.setClosedAt(now);
        accountPenaltiesResponse.setPenalties(Collections.singletonList(penalty));

        when(this.testDataService.updateAccountPenaltiesData(PENALTY_REFERENCE, request))
                .thenReturn(accountPenaltiesResponse);
        ResponseEntity<AccountPenaltiesResponse> response = this.testDataController
                .updateAccountPenalties(PENALTY_REFERENCE, request);

        assertEquals(accountPenaltiesResponse, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void getAccountPenaltiesFiltersByTransactionReference() throws Exception {
        PenaltyResponse penalty1 = new PenaltyResponse();
        penalty1.setTransactionReference(PENALTY_REFERENCE);
        PenaltyResponse penalty2 = new PenaltyResponse();
        penalty2.setTransactionReference("A2345678");

        AccountPenaltiesResponse accountPenaltiesResponse = new AccountPenaltiesResponse();
        accountPenaltiesResponse.setPenalties(List.of(penalty1, penalty2));

        when(testDataService.getAccountPenaltiesData(PENALTY_ID)).thenReturn(accountPenaltiesResponse);

        ResponseEntity<AccountPenaltiesResponse> response =
                testDataController.getAccountPenalties(PENALTY_ID, PENALTY_REFERENCE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getPenalties().size());
        assertEquals(PENALTY_REFERENCE, response.getBody().getPenalties().get(0).getTransactionReference());
        verify(testDataService, times(1)).getAccountPenaltiesData(PENALTY_ID);
    }

    @Test
    void deleteAccountPenaltyByReferenceSuccess() throws Exception {
        PenaltyRequest request = new PenaltyRequest();
        request.setTransactionReference(PENALTY_REFERENCE);

        when(testDataService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REFERENCE))
                .thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<Void> response = testDataController.deleteAccountPenalties(PENALTY_ID, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(testDataService, times(1)).deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REFERENCE);
    }

    @Test
    void deleteAccountPenaltyByReferenceNotFound() throws Exception {
        PenaltyRequest request = new PenaltyRequest();
        request.setTransactionReference(PENALTY_REFERENCE);

        NoDataFoundException exception = new NoDataFoundException("penalty not found");
        when(testDataService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REFERENCE))
                .thenThrow(exception);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                testDataController.deleteAccountPenalties(PENALTY_ID, request));
        assertEquals(exception, thrown);
        verify(testDataService, times(1)).deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REFERENCE);
    }

    @Test
    void deleteAccountPenaltyByReferenceOtherError() throws Exception {
        PenaltyRequest request = new PenaltyRequest();
        request.setTransactionReference(PENALTY_REFERENCE);

        DataException exception = new DataException("error during deletion");
        when(testDataService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REFERENCE))
                .thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.deleteAccountPenalties(PENALTY_ID, request));
        assertEquals(exception, thrown);
        verify(testDataService, times(1)).deleteAccountPenaltyByReference(PENALTY_ID, "A1234567");
    }

    @Test
    void updateAccountPenaltiesNotFound() throws Exception {
        Instant now = Instant.now();

        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);
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
        when(this.testDataService.deleteAccountPenaltiesData(PENALTY_ID))
                .thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<Void> response = testDataController.deleteAccountPenalties(PENALTY_ID, null);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(testDataService, times(1)).deleteAccountPenaltiesData(PENALTY_ID);
    }

    @Test
    void deleteAccountPenaltiesNotFound() throws Exception {
        NoDataFoundException exception = new NoDataFoundException("penalty not found");
        when(this.testDataService.deleteAccountPenaltiesData(PENALTY_ID)).thenThrow(exception);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                this.testDataController.deleteAccountPenalties(PENALTY_ID, null));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteAccountPenaltiesOtherError() throws Exception {
        DataException exception = new DataException("error during deletion");
        when(this.testDataService.deleteAccountPenaltiesData(PENALTY_ID)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataController.deleteAccountPenalties(PENALTY_ID, null));
        assertEquals(exception, thrown);
    }

    private PenaltyResponse createPenaltyData(String companyCode, String customerCode,
                                              String penaltyRef, double amount, boolean paid) {
        PenaltyResponse penalty = new PenaltyResponse();
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
    void createPenaltySuccess() throws Exception {
        PenaltySpec request = new PenaltySpec();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);

        AccountPenaltiesResponse createdPenalties = new AccountPenaltiesResponse();
        createdPenalties.setCompanyCode(COMPANY_CODE);
        createdPenalties.setCustomerCode(CUSTOMER_CODE);

        when(testDataService.createPenaltyData(request)).thenReturn(createdPenalties);

        ResponseEntity<?> response = testDataController.createPenalty(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdPenalties, response.getBody());
        verify(testDataService, times(1)).createPenaltyData(request);
    }

    @Test
    void createPenaltyThrowsDataException() throws Exception {
        PenaltySpec request = new PenaltySpec();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);

        DataException exception = new DataException("Failed to create penalty");
        when(testDataService.createPenaltyData(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.createPenalty(request));
        assertEquals(exception, thrown);
        verify(testDataService, times(1)).createPenaltyData(request);
    }

    @Test
    void createPenaltyDuplicateButNoPenalties() throws Exception {
        PenaltySpec request = new PenaltySpec();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);
        request.setDuplicate(true); // simulate duplicate request

        AccountPenaltiesResponse createdPenalties = new AccountPenaltiesResponse();
        createdPenalties.setPenalties(Collections.emptyList()); // empty list simulates failure

        when(testDataService.createPenaltyData(request)).thenReturn(createdPenalties);

        ResponseEntity<?> response = testDataController.createPenalty(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("number_of_penalties should be greater than 1 for duplicate penalties", body.get("error"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
    }

    @Test
    void createPenaltyDuplicateButNoCreatedPenalties() throws Exception {
        PenaltySpec request = new PenaltySpec();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);
        request.setDuplicate(true);

        when(testDataService.createPenaltyData(request)).thenReturn(null);

        ResponseEntity<?> response = testDataController.createPenalty(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("number_of_penalties should be greater than 1 for duplicate penalties", body.get("error"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
    }

    @Test
    void testPenaltyOutstandingAmountWhenNotPaid() {
        PenaltyResponse penaltyCopy = new PenaltyResponse();
        penaltyCopy.setAmount(100.0);

        boolean isPaid = false;
        penaltyCopy.setOutstandingAmount(isPaid ? 0.0 : penaltyCopy.getAmount());

        assertEquals(100.0, penaltyCopy.getOutstandingAmount());
    }

    @Test
    void getPostcodeSuccess() throws Exception {
        String country = "England";
        PostcodesResponse postcodesResponse =
                new PostcodesResponse(12, "Thoroughfare Name", "Dependent Locality",
                        "Locality Post Town", "ABC 123");

        when(testDataService.getPostcodes(country)).thenReturn(postcodesResponse);

        ResponseEntity<PostcodesResponse> response = testDataController.getPostcode(country);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postcodesResponse, response.getBody());
        verify(testDataService, times(1)).getPostcodes(country);
    }

    @Test
    void getPostcodeNoDataFound() throws Exception {
        String country = "UnknownCountry";

        when(testDataService.getPostcodes(country)).thenReturn(null);

        ResponseEntity<PostcodesResponse> response = testDataController.getPostcode(country);
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
        ResponseEntity<PostcodesResponse> response = testDataController.getPostcode(null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(testDataService, times(0)).getPostcodes(anyString());
    }

    @Test
    void createCompanyInternalWithDisqualifications() throws Exception {
        CompanyRequest request = new CompanyRequest();
        request.setJurisdiction(JurisdictionType.SCOTLAND);
        DisqualificationsRequest disqSpec = new DisqualificationsRequest();
        disqSpec.setCorporateOfficer(false);
        request.setDisqualifiedOfficers(List.of(disqSpec));

        CompanyProfileResponse company = new CompanyProfileResponse("12345678", "123456", "http://localhost:4001/company/12345678");

        when(testDataService.createCompanyData(request)).thenReturn(company);
        ResponseEntity<CompanyProfileResponse> response = testDataController.createCompanyInternal(request);

        assertEquals(company, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testHealthCheck() {
        var response = testDataController.healthCheck();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test-data-generator is alive",response.getBody());
    }

    @Test
    void createCertifiedCopiesSuccess() throws Exception {
        CertificatesResponse certificateData = getCertificatesData();

        CertifiedCopiesRequest request = new CertifiedCopiesRequest();
        request.setCompanyNumber("12345678");

        when(testDataService.createCertifiedCopiesData(request)).thenReturn(certificateData);

        ResponseEntity<CertificatesResponse> response = testDataController.createCertifiedCopies(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).getCertificates().size());

        assertEquals("CRT-834723-192847", response.getBody().getCertificates().getFirst().getId());
        assertEquals("CRT-912834-238472", response.getBody().getCertificates().get(1).getId());
    }

    private static CertificatesResponse getCertificatesData() {
        CertificatesResponse.CertificateEntry entry1 = new CertificatesResponse.CertificateEntry(
            "CRT-834723-192847", "2025-04-14T12:00:00Z", "2025-04-14T12:00:00Z"
        );
        CertificatesResponse.CertificateEntry entry2 = new CertificatesResponse.CertificateEntry(
            "CRT-912834-238472", "2025-04-14T12:05:00Z", "2025-04-14T12:05:00Z"
        );

        List<CertificatesResponse.CertificateEntry> entries = List.of(entry1, entry2);
        return new CertificatesResponse(entries);
    }


    @Test
    void createCertifiedCopiesException() throws Exception {
        CertifiedCopiesRequest request = new CertifiedCopiesRequest();
        request.setCompanyNumber("12345678");

        DataException exception = new DataException("Error creating certificate");
        when(testDataService.createCertifiedCopiesData(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
            testDataController.createCertifiedCopies(request));
        assertEquals(exception.getMessage(), thrown.getMessage());
    }

    @Test
    void createMissingImageDeliveriesSuccess() throws Exception {
        CertificatesResponse certificateData = getCertificatesData();

        MissingImageDeliveriesRequest request = new MissingImageDeliveriesRequest();
        request.setCompanyNumber("12345678");

        when(testDataService.createMissingImageDeliveriesData(request)).thenReturn(certificateData);

        ResponseEntity<CertificatesResponse> response = testDataController.createMissingImageDeliveries(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).getCertificates().size());

        assertEquals("CRT-834723-192847", response.getBody().getCertificates().getFirst().getId());
        assertEquals("CRT-912834-238472", response.getBody().getCertificates().get(1).getId());
    }

    @Test
    void createMissingImageDeliveriesException() throws Exception {
        MissingImageDeliveriesRequest request = new MissingImageDeliveriesRequest();
        request.setCompanyNumber("12345678");

        DataException exception = new DataException("Error creating certificate");
        when(testDataService.createMissingImageDeliveriesData(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
            testDataController.createMissingImageDeliveries(request));
        assertEquals(exception.getMessage(), thrown.getMessage());
    }

    @Test
    void createUserCompanyAssociation() throws Exception {
        UserCompanyAssociationRequest spec =
                new UserCompanyAssociationRequest();
        spec.setUserId(USER_ID);
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setStatus(CONFIRMED_STATUS);
        spec.setApprovalRoute(AUTH_CODE_APPROVAL_ROUTE);

        UserCompanyAssociationResponse association =
                new UserCompanyAssociationResponse(
                new ObjectId(), COMPANY_NUMBER, USER_ID,
                        null, CONFIRMED_STATUS, AUTH_CODE_APPROVAL_ROUTE,
                        null);

        when(this.testDataService.createUserCompanyAssociationData(spec))
                .thenReturn(association);
        ResponseEntity<UserCompanyAssociationResponse> response
                = this.testDataController.createAssociation(spec);

        assertEquals(association, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createUserCompanyAssociationException() throws Exception {
        UserCompanyAssociationRequest spec =
                new UserCompanyAssociationRequest();
        Throwable exception = new DataException("Error creating an "
                + "association");

        when(this.testDataService.createUserCompanyAssociationData(spec))
                .thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataController.createAssociation(spec));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteUserCompanyAssociation() throws Exception {
        when(this.testDataService.deleteUserCompanyAssociationData(ASSOCIATION_ID))
                .thenReturn(true);
        ResponseEntity<Map<String, Object>> response
                = this.testDataController.deleteAssociation(ASSOCIATION_ID);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(testDataService).deleteUserCompanyAssociationData(ASSOCIATION_ID);
    }

    @Test
    void deleteUserCompanyAssociationNotFound() throws Exception {
        when(this.testDataService.deleteUserCompanyAssociationData(ASSOCIATION_ID))
                .thenReturn(false);
        ResponseEntity<Map<String, Object>> response
                = this.testDataController.deleteAssociation(ASSOCIATION_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ASSOCIATION_ID,
                Objects.requireNonNull(response.getBody()).get(
                        "association_id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));

        verify(testDataService, times(1)).deleteUserCompanyAssociationData(ASSOCIATION_ID);
    }

    @Test
    void deleteUserCompanyAssociationException() throws Exception {
        Throwable exception = new DataException("Error deleting "
                + "association");

        when(this.testDataService.deleteUserCompanyAssociationData(ASSOCIATION_ID))
                .thenThrow(exception);

        DataException thrown = assertThrows(
                DataException.class,
                () -> this.testDataController.deleteAssociation(ASSOCIATION_ID));
        assertEquals(exception, thrown);
    }

    @Test
    void createTransaction() throws Exception {
        TransactionsRequest request = new TransactionsRequest();
        request.setUserId("rsf3pdwywvse5yz55mfodfx8");
        request.setReference("ACSP Registration");

        TransactionsResponse txn = new TransactionsResponse("rsf3pdwywvse5yz55mfodfx8","email@email.com" ,"forename","surname","resumeURI","status", "250788-250788-250788");
        when(this.testDataService.createTransactionData(request)).thenReturn(txn);
        ResponseEntity<TransactionsResponse> response
                = this.testDataController.createTransaction(request);

        assertEquals(txn, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createTransactionException() throws Exception {
        TransactionsRequest request = new TransactionsRequest();
        request.setUserId("rsf3pdwywvse5yz55mfodfx8");
        request.setReference("ACSP Registration");
        Throwable exception = new DataException("Error message");

        when(this.testDataService.createTransactionData(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataController.createTransaction(request));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteTransaction() throws Exception {
        when(this.testDataService.deleteTransaction(TRANSACTION_ID))
                .thenReturn(true);

        ResponseEntity<Map<String, Object>> response =
                this.testDataController.deleteTransaction(TRANSACTION_ID);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(testDataService, times(1)).deleteTransaction(TRANSACTION_ID);
    }

    @Test
    void deleteTransactionNotFound() throws Exception {
        when(this.testDataService.deleteTransaction(TRANSACTION_ID))
                .thenReturn(false);

        ResponseEntity<Map<String, Object>> response =
                this.testDataController.deleteTransaction(TRANSACTION_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(TRANSACTION_ID,
                Objects.requireNonNull(response.getBody()).get("transaction_id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));

        verify(testDataService, times(1)).deleteTransaction(TRANSACTION_ID);
    }

    @Test
    void deleteTransactionException() throws Exception {
        Throwable exception = new DataException("Error deleting transaction");

        when(this.testDataService.deleteTransaction(TRANSACTION_ID))
                .thenThrow(exception);

        DataException thrown = assertThrows(
                DataException.class,
                () -> this.testDataController.deleteTransaction(TRANSACTION_ID));

        assertEquals(exception, thrown);
        verify(testDataService, times(1)).deleteTransaction(TRANSACTION_ID);
    }

    @Test
    void createCombinedSicActivities() throws Exception {
        CombinedSicActivitiesRequest spec = new CombinedSicActivitiesRequest();
        spec.setActivityDescription("Braunkohle waschen");
        spec.setSicDescription("Abbau von Braunkohle");
        spec.setIsChActivity(false);
        spec.setActivityDescriptionSearchField("braunkohle waschen");

        CombinedSicActivitiesResponse data =
            new CombinedSicActivitiesResponse(
                new ObjectId().toHexString(),
                "21017",
                "Abbau von Braunkohle");

        when(this.testDataService.createCombinedSicActivitiesData(spec))
            .thenReturn(data);

        ResponseEntity<CombinedSicActivitiesResponse> response =
            this.testDataController.createCombinedSicActivities(spec);

        assertEquals(data, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(testDataService, times(1)).createCombinedSicActivitiesData(spec);
    }

    @Test
    void createCombinedSicActivitiesException() throws Exception {
        CombinedSicActivitiesRequest spec = new CombinedSicActivitiesRequest();
        spec.setActivityDescription("Braunkohle waschen");
        spec.setSicDescription("Abbau von Braunkohle");
        spec.setIsChActivity(false);
        spec.setActivityDescriptionSearchField("braunkohle waschen");

        Throwable exception = new DataException("Error creating combined sic activities");

        when(this.testDataService.createCombinedSicActivitiesData(spec))
            .thenThrow(exception);

        DataException thrown = assertThrows(DataException.class,
            () -> this.testDataController.createCombinedSicActivities(spec));

        assertEquals(exception, thrown);
        verify(testDataService, times(1)).createCombinedSicActivitiesData(spec);
    }

    @Test
    void deleteCombinedSicActivities() throws Exception {
        when(this.testDataService.deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID))
            .thenReturn(true);

        ResponseEntity<Map<String, Object>> response =
            this.testDataController.deleteCombinedSicActivities(SIC_ACTIVITY_ID);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(testDataService, times(1)).deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID);
    }

    @Test
    void deleteCombinedSicActivitiesNotFound() throws Exception {
        when(this.testDataService.deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID))
            .thenReturn(false);

        ResponseEntity<Map<String, Object>> response =
            this.testDataController.deleteCombinedSicActivities(SIC_ACTIVITY_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(SIC_ACTIVITY_ID,
            Objects.requireNonNull(response.getBody()).get("id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));

        verify(testDataService, times(1)).deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID);
    }

    @Test
    void deleteCombinedSicActivitiesException() throws Exception {
        Throwable exception = new DataException("Error deleting combined sic activities");

        when(this.testDataService.deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID))
            .thenThrow(exception);

        DataException thrown = assertThrows(
            DataException.class,
            () -> this.testDataController.deleteCombinedSicActivities(SIC_ACTIVITY_ID));

        assertEquals(exception, thrown);
        verify(testDataService, times(1)).deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID);
    }

    @Test
    void createAdminPermissions_success() throws Exception {
        AdminPermissionsRequest spec = new AdminPermissionsRequest();
        AdminPermissionsResponse data = new AdminPermissionsResponse("permId", "groupName");

        when(testDataService.createAdminPermissionsData(spec)).thenReturn(data);

        ResponseEntity<AdminPermissionsResponse> response = testDataController.createAdminPermissions(spec);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        assertEquals(data, response.getBody());
        verify(testDataService, times(1)).createAdminPermissionsData(spec);
    }

    @Test
    void createAdminPermissions_throwsException() throws Exception {
        AdminPermissionsRequest spec = new AdminPermissionsRequest();
        DataException exception = new DataException("Error creating admin permissions");

        when(testDataService.createAdminPermissionsData(spec)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.createAdminPermissions(spec)
        );
        assertEquals(exception, thrown);
        verify(testDataService, times(1)).createAdminPermissionsData(spec);
    }

    @Test
    void deleteAdminPermissions_success() throws Exception {
        String id = "permId";
        when(testDataService.deleteAdminPermissionsData(id)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = testDataController.deleteAdminPermissions(id);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(testDataService, times(1)).deleteAdminPermissionsData(id);
    }

    @Test
    void deleteAdminPermissions_notFound() throws Exception {
        String id = "permId";
        when(testDataService.deleteAdminPermissionsData(id)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = testDataController.deleteAdminPermissions(id);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().get("admin-permissions-id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));
        verify(testDataService, times(1)).deleteAdminPermissionsData(id);
    }

    @Test
    void deleteAdminPermissions_throwsException() throws Exception {
        String id = "permId";
        DataException exception = new DataException("Error deleting admin permissions");
        when(testDataService.deleteAdminPermissionsData(id)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.deleteAdminPermissions(id)
        );
        assertEquals(exception, thrown);
        verify(testDataService, times(1)).deleteAdminPermissionsData(id);
    }

    @Test
    void getIdentityVerification_serviceReturnsData_returnsOk() throws Exception {
        final String email = "user@example.com";
        var data = new IdentityVerificationResponse("identity-id-123", "UVID-ABC", "Firstname", "Lastname");

        when(this.verifiedIdentityService.getIdentityVerificationData(email))
                .thenReturn(data);

        ResponseEntity<IdentityVerificationResponse> response = this.testDataController.getIdentityVerification(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(data, response.getBody());

        verify(verifiedIdentityService, times(1)).getIdentityVerificationData(email);
    }

    @Test
    void getIdentityVerification_serviceReturnsNull_throwsNoDataFoundException() throws Exception {
        final String email = "missing@example.com";

        when(this.verifiedIdentityService.getIdentityVerificationData(email))
                .thenReturn(null);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                this.testDataController.getIdentityVerification(email));

        assertEquals("No identity verification found for email: " + email, thrown.getMessage());

        verify(verifiedIdentityService, times(1)).getIdentityVerificationData(email);
    }

    @Test
    void findOrCreateCompanyAuthCode_success() throws Exception {
        String companyNumber = "12345678";
        var authCode = new CompanyAuthCode();
        authCode.setId(companyNumber);
        authCode.setAuthCode("CODE123");

        when(testDataService.findOrCreateCompanyAuthCode(companyNumber)).thenReturn(authCode);

        ResponseEntity<CompanyAuthCodeResponse> response =
                testDataController.findOrCreateCompanyAuthCode(companyNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CODE123", response.getBody().getAuthCode());
    }

    @Test
    void findOrCreateCompanyAuthCode_nullCompanyNumber_throwsDataException() {
        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.findOrCreateCompanyAuthCode(null));
        assertEquals("companyNumber query parameter is required", thrown.getMessage());
    }

    @Test
    void findOrCreateCompanyAuthCode_emptyCompanyNumber_throwsDataException() {
        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.findOrCreateCompanyAuthCode(""));
        assertEquals("companyNumber query parameter is required", thrown.getMessage());
    }

    @Test
    void findOrCreateCompanyAuthCode_serviceThrowsDataException() throws Exception {
        String companyNumber = "12345678";
        DataException ex = new DataException("Service error");
        when(testDataService.findOrCreateCompanyAuthCode(companyNumber)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.findOrCreateCompanyAuthCode(companyNumber));
        assertEquals(ex, thrown);
    }

    @Test
    void findOrCreateCompanyAuthCode_serviceThrowsNoDataFoundException() throws Exception {
        String companyNumber = "12345678";
        NoDataFoundException ex = new NoDataFoundException("Not found");
        when(testDataService.findOrCreateCompanyAuthCode(companyNumber)).thenThrow(ex);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                testDataController.findOrCreateCompanyAuthCode(companyNumber));
        assertEquals(ex, thrown);
    }

    @Test
    void getCompanyWithPopulatedStructure_success() throws Exception {
        CompanyRequest request = new CompanyRequest();
        PopulatedCompanyDetailsResponse responseObj = new PopulatedCompanyDetailsResponse();
        when(testDataService.getCompanyDataStructureBeforeSavingInMongoDb(request)).thenReturn(responseObj);

        ResponseEntity<PopulatedCompanyDetailsResponse> response = testDataController.getCompanyWithPopulatedStructure(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseObj, response.getBody());
        verify(testDataService, times(1)).getCompanyDataStructureBeforeSavingInMongoDb(request);
    }

    @Test
    void getCompanyWithPopulatedStructure_nullRequest_usesDefault() throws Exception {
        PopulatedCompanyDetailsResponse responseObj = new PopulatedCompanyDetailsResponse();
        when(testDataService.getCompanyDataStructureBeforeSavingInMongoDb(any(CompanyRequest.class))).thenReturn(responseObj);

        ResponseEntity<PopulatedCompanyDetailsResponse> response = testDataController.getCompanyWithPopulatedStructure(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseObj, response.getBody());
        verify(testDataService, times(1)).getCompanyDataStructureBeforeSavingInMongoDb(any(CompanyRequest.class));
    }

    @Test
    void getCompanyWithPopulatedStructure_throwsDataException() throws Exception {
        CompanyRequest request = new CompanyRequest();
        DataException exception = new DataException("error");
        when(testDataService.getCompanyDataStructureBeforeSavingInMongoDb(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.getCompanyWithPopulatedStructure(request)
        );
        assertEquals(exception, thrown);
    }

    @Test
    void createCompanyWithPopulatedStructure_success() throws Exception {
        CompanyWithPopulatedStructureRequest request = new CompanyWithPopulatedStructureRequest();
        CompanyProfileResponse companyData = new CompanyProfileResponse("12345678", "123456", "http://localhost:4001/company/12345678");
        when(testDataService.createCompanyWithStructure(request)).thenReturn(companyData);

        ResponseEntity<CompanyProfileResponse> response = testDataController.createCompanyWithPopulatedStructure(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(companyData, response.getBody());
        verify(testDataService, times(1)).createCompanyWithStructure(request);
    }

    @Test
    void createCompanyWithPopulatedStructure_nullRequest_usesDefault() throws Exception {
        CompanyProfileResponse companyData = new CompanyProfileResponse("12345678", "123456", "http://localhost:4001/company/12345678");
        when(testDataService.createCompanyWithStructure(any(CompanyWithPopulatedStructureRequest.class))).thenReturn(companyData);

        ResponseEntity<CompanyProfileResponse> response = testDataController.createCompanyWithPopulatedStructure(null);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(companyData, response.getBody());
        verify(testDataService, times(1)).createCompanyWithStructure(any(CompanyWithPopulatedStructureRequest.class));
    }

    @Test
    void createCompanyWithPopulatedStructure_throwsDataException() throws Exception {
        CompanyWithPopulatedStructureRequest request = new CompanyWithPopulatedStructureRequest();
        DataException exception = new DataException("error");
        when(testDataService.createCompanyWithStructure(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataController.createCompanyWithPopulatedStructure(request)
        );
        assertEquals(exception, thrown);
    }

    @Test
    void getAcspProfileFound() throws Exception {
        String acspNumber = "AP000036";

        AcspProfile profile = new AcspProfile();
        profile.setId(acspNumber);
        profile.setAcspNumber(acspNumber);
        profile.setName("Test ACSP Company");
        profile.setStatus("active");

        when(testDataService.getAcspProfileData(acspNumber)).thenReturn(Optional.of(profile));

        ResponseEntity<Optional<AcspProfile>> response =
                testDataController.getAcspProfile(acspNumber);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPresent());

        AcspProfile returnedProfile = response.getBody().get();
        assertEquals(acspNumber, returnedProfile.getId());
        assertEquals(acspNumber, returnedProfile.getAcspNumber());
        assertEquals("Test ACSP Company", returnedProfile.getName());
        assertEquals("active", returnedProfile.getStatus());

        verify(testDataService, times(1)).getAcspProfileData(acspNumber);
    }

    @Test
    void getAcspProfileNotFound() throws Exception {
        String acspNumber = "NON_EXISTENT";

        when(testDataService.getAcspProfileData(acspNumber)).thenReturn(Optional.empty());

        ResponseEntity<Optional<AcspProfile>> response =
                testDataController.getAcspProfile(acspNumber);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(testDataService, times(1)).getAcspProfileData(acspNumber);
    }

}
