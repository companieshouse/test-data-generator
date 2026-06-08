package uk.gov.companieshouse.api.testdata.service.impl;

import jakarta.validation.ConstraintViolationException;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.entity.AdminPermissions;
import uk.gov.companieshouse.api.testdata.model.entity.Certificates;
import uk.gov.companieshouse.api.testdata.model.entity.CertifiedCopies;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.MissingImageDeliveries;
import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;
import uk.gov.companieshouse.api.testdata.model.entity.User;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspMembersRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspProfileRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.AmlRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertificatesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertifiedCopiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CombinedSicActivitiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyAuthAllowListRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.MissingImageDeliveriesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.TransactionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UserCompanyAssociationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UserRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AccountPenaltiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspMembersResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CertificatesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CombinedSicActivitiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PostcodesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.TransactionsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.UserCompanyAssociationResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.UserResponse;
import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.repository.AdminPermissionsRepository;
import uk.gov.companieshouse.api.testdata.service.AccountPenaltiesService;
import uk.gov.companieshouse.api.testdata.service.AcspProfileService;
import uk.gov.companieshouse.api.testdata.service.AppealsService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthAllowListService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.ItemGroupsService;
import uk.gov.companieshouse.api.testdata.service.PostcodeService;
import uk.gov.companieshouse.api.testdata.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestDataServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String COMPANY_CODE = "LP";
    private static final String CUSTOMER_CODE = "12345678";
    private static final String PENALTY_ID = "685abc4b9b34c84d4d2f5af6";
    private static final String PENALTY_REF = "A1234567";
    private static final String USER_ID = "sZJQcNxzPvcwcqDwpUyRKNvVbcq";
    private static final String CERTIFICATES_ID = "CRT-123456-789012";
    private static final String AUTH_CODE_APPROVAL_ROUTE =
            "auth_code";
    private static final String CONFIRMED_STATUS = "confirmed";
    private static final String ASSOCIATION_ID = "associationId";
    private static final String CERTIFIED_COPIES_ID = "CCD-123456-789012";
    private static final String MISSING_IMAGE_DELIVERIES_ID = "MID-123456-789012";
    private static final String SIC_ACTIVITY_ID = "6242bbbbafaaaa93274b2efd";
    private static final String TRANSACTION_ID = "903085-903085-903085";

    @Mock
    private CompanyProfileService companyProfileService;
    @Mock
    private CompanyAuthCodeService companyAuthCodeService;
    @Mock
    private UserService userService;
    @Mock
    private AdminPermissionsRepository adminPermissionsRepository;
    @Mock
    private DataService<AcspMembersResponse, AcspMembersRequest> acspMembersService;
    @Mock
    private AcspMembersRepository acspMembersRepository;
    @Mock
    private AcspProfileService acspProfileService;
    @Mock
    private CompanyAuthAllowListService companyAuthAllowListService;
    @Mock
    private AppealsService appealsService;
    @Mock
    private DataService<CombinedSicActivitiesResponse, CombinedSicActivitiesRequest> combinedSicActivitiesService;
    @Mock
    private DataService<CertificatesResponse, CertificatesRequest> certificatesService;
    @Mock
    private DataService<CertificatesResponse, CertifiedCopiesRequest> certifiedCopiesService;
    @Mock
    private DataService<CertificatesResponse, MissingImageDeliveriesRequest> missingImageDeliveriesService;
    @Mock
    private AccountPenaltiesService accountPenaltiesService;
    @Mock
    private PostcodeService postcodeService;
    @Mock private DataService<TransactionsResponse, TransactionsRequest> transactionService;
    @Mock
    private DataService<UserCompanyAssociationResponse,
            UserCompanyAssociationRequest> userCompanyAssociationService;
    @Mock
    private ItemGroupsService itemGroupsService;

    @InjectMocks
    private TestDataServiceImpl testDataService;

    /**
     * Helper to create ACSP members data.
     *
     * @param userId      the user id to set on the spec
     * @param profileData the ACSP profile data to be returned by the profile service
     * @param membersData the ACSP members data to be returned by the members service
     * @return the result of testDataService.createAcspMembersData(...)
     * @throws DataException if creation fails
     */
    private AcspMembersResponse createAcspMembersDataHelper(String userId,
                                                            AcspProfileResponse profileData,
                                                            AcspMembersResponse membersData, AcspProfileRequest profileSpec) throws DataException {
        AcspMembersRequest spec = new AcspMembersRequest();
        spec.setUserId(userId);
        spec.setAcspProfile(profileSpec);
        when(acspProfileService.create(any(AcspProfileRequest.class))).thenReturn(profileData);
        when(acspMembersService.create(any(AcspMembersRequest.class))).thenReturn(membersData);
        return testDataService.createAcspMembersData(spec);
    }

    private void verifyAcspMembersData(AcspMembersResponse data,
                                       String expectedMemberId,
                                       String expectedAcspNumber,
                                       String expectedUserId,
                                       String expectedStatus,
                                       String expectedUserRole) {
        assertNotNull(data);
        assertEquals(expectedMemberId, data.getAcspMemberId());
        assertEquals(expectedAcspNumber, data.getAcspNumber());
        assertEquals(expectedUserId, data.getUserId());
        assertEquals(expectedStatus, data.getStatus());
        assertEquals(expectedUserRole, data.getUserRole());
    }

    /**
     * Helper to perform deletion of ACSP member data.
     *
     * @param acspMemberId   the member id to delete
     * @param memberOptional an Optional containing the AcspMembers if found
     * @return the result of testDataService.deleteAcspMembersData(...)
     * @throws DataException if deletion fails
     */
    private boolean deleteAcspMembersDataHelper(String acspMemberId,
                                                Optional<AcspMembers> memberOptional)
            throws DataException {
        when(acspMembersRepository.findById(acspMemberId)).thenReturn(memberOptional);
        return testDataService.deleteAcspMembersData(acspMemberId);
    }

    @Test
    void updateCompanyDataSuccess() throws Exception {
        UpdateCompanyRequest request = new UpdateCompanyRequest();
        request.setCompanyNumber(COMPANY_NUMBER);

        CompanyProfile expectedProfile = new CompanyProfile();
        expectedProfile.setCompanyNumber(COMPANY_NUMBER);

        when(companyProfileService.updateCompanyProfile(request)).thenReturn(expectedProfile);

        CompanyProfile actualProfile = testDataService.updateCompanyData(request);

        assertEquals(expectedProfile, actualProfile);
        verify(companyProfileService, times(1)).updateCompanyProfile(request);
    }

    @Test
    void updateCompanyDataNotFound() throws Exception {
        UpdateCompanyRequest request = new UpdateCompanyRequest();
        request.setCompanyNumber(COMPANY_NUMBER);

        String errorMessage = "Company not found";
        when(companyProfileService.updateCompanyProfile(request))
                .thenThrow(new NoDataFoundException(errorMessage));

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                testDataService.updateCompanyData(request));

        assertEquals(errorMessage, thrown.getMessage());
        verify(companyProfileService, times(1)).updateCompanyProfile(request);
    }

    @Test
    void updateCompanyDataException() throws Exception {
        UpdateCompanyRequest request = new UpdateCompanyRequest();
        request.setCompanyNumber(COMPANY_NUMBER);

        String errorMessage = "Database error";
        when(companyProfileService.updateCompanyProfile(request))
                .thenThrow(new DataException(errorMessage));

        DataException thrown = assertThrows(DataException.class, () ->
                testDataService.updateCompanyData(request));

        assertEquals(errorMessage, thrown.getMessage());
        verify(companyProfileService, times(1)).updateCompanyProfile(request);
    }

    @Test
    void createUserDataThrowsExceptionWhenPasswordIsNull() {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword(null);

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.createUserData(userRequest));
        assertEquals("Password is required to create a user", exception.getMessage());
    }

    @Test
    void createUserDataThrowsExceptionWhenPasswordIsEmpty() {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("");

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.createUserData(userRequest));
        assertEquals("Password is required to create a user", exception.getMessage());
    }

    @Test
    void createUserDataWithNullRoles() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setRoles(null);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        UserResponse result = testDataService.createUserData(userRequest);

        assertEquals(userResponse, result);
        verify(userService).create(userRequest);
        verify(companyAuthAllowListService, never()).create(any());
    }

    @Test
    void createUserDataWithEmptyRoles() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setRoles(new ArrayList<>());

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        UserResponse result = testDataService.createUserData(userRequest);

        assertEquals(userResponse, result);
        verify(userService).create(userRequest);
        verify(companyAuthAllowListService, never()).create(any());
    }

    @Test
    void createUserDataWithRolesAndPermissions() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setRoles(List.of("group1"));

        var entity = new AdminPermissions();
        entity.setPermissions(List.of("perm1", "perm2"));
        when(adminPermissionsRepository.findByGroupName("group1")).thenReturn(entity);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        UserResponse result = testDataService.createUserData(userRequest);

        assertEquals(userResponse, result);
        assertEquals(List.of("perm1", "perm2"), userRequest.getRoles());
        verify(userService).create(userRequest);
        verify(companyAuthAllowListService, never()).create(any());
    }

    @Test
    void createUserDataWithRolesAndNoPermissions() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setRoles(List.of("group1"));

        when(adminPermissionsRepository.findByGroupName("group1")).thenReturn(null);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        UserResponse result = testDataService.createUserData(userRequest);

        assertEquals(userResponse, result);
        assertEquals(List.of("group1"), userRequest.getRoles());
        verify(userService).create(userRequest);
        verify(companyAuthAllowListService, never()).create(any());
    }

    @Test
    void createUserData_addsPermissionsWhenEntityAndPermissionsExist() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setRoles(List.of("group1"));

        AdminPermissions entity = new AdminPermissions();
        entity.setPermissions(List.of("perm1", "perm2"));
        when(adminPermissionsRepository.findByGroupName("group1")).thenReturn(entity);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        testDataService.createUserData(userRequest);

        assertEquals(List.of("perm1", "perm2"), userRequest.getRoles());
    }

    @Test
    void createUserData_doesNotAddPermissionsWhenEntityIsNull() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setRoles(List.of("group1"));

        when(adminPermissionsRepository.findByGroupName("group1")).thenReturn(null);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        testDataService.createUserData(userRequest);

        assertEquals(List.of("group1"), userRequest.getRoles());
    }

    @Test
    void createUserData_doesNotAddPermissionsWhenPermissionsAreNull() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setRoles(List.of("group1"));

        AdminPermissions entity = new AdminPermissions();
        entity.setPermissions(null);
        when(adminPermissionsRepository.findByGroupName("group1")).thenReturn(entity);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        testDataService.createUserData(userRequest);

        assertEquals(List.of("group1"), userRequest.getRoles());
    }

    @Test
    void createUserData_handlesMultipleGroupNamesWithMixedEntities() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setRoles(List.of("group1", "group2", "group3"));

        AdminPermissions entity1 = new AdminPermissions();
        entity1.setPermissions(List.of("perm1"));
        AdminPermissions entity2 = new AdminPermissions();
        entity2.setPermissions(null);

        when(adminPermissionsRepository.findByGroupName("group1")).thenReturn(entity1);
        when(adminPermissionsRepository.findByGroupName("group2")).thenReturn(null);
        when(adminPermissionsRepository.findByGroupName("group3")).thenReturn(entity2);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        testDataService.createUserData(userRequest);

        assertEquals(List.of("perm1"), userRequest.getRoles());
    }

    @Test
    void createUserDataWithCompanyAuthAllowListTrue() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setIsCompanyAuthAllowList(true);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        UserResponse result = testDataService.createUserData(userRequest);

        assertEquals(userResponse, result);
        verify(companyAuthAllowListService, times(1)).create(any(CompanyAuthAllowListRequest.class));
    }

    @Test
    void createUserDataWithCompanyAuthAllowListFalse() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setIsCompanyAuthAllowList(false);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        UserResponse result = testDataService.createUserData(userRequest);

        assertEquals(userResponse, result);
        verify(companyAuthAllowListService, never()).create(any());
    }

    @Test
    void createAcspMembersData() throws DataException {
        AcspProfileRequest profileSpec = new AcspProfileRequest();
        profileSpec.setAcspNumber("acspNumber");
        profileSpec.setStatus("active");
        profileSpec.setType("limited-company");

        AcspProfile profileEntity = new AcspProfile();
        profileEntity.setAcspNumber(profileSpec.getAcspNumber());
        profileEntity.setName(profileSpec.getName());
        profileEntity.setVersion(1L);

        AcspProfileResponse acspProfileResponse =
                new AcspProfileResponse(profileEntity);
        AcspMembersResponse expectedMembersData =
                new AcspMembersResponse(new ObjectId(),
                        profileSpec.getAcspNumber(), "userId", "active", "role");
        AcspMembersResponse result = createAcspMembersDataHelper(
                "userId", acspProfileResponse, expectedMembersData, profileSpec);
        verifyAcspMembersData(result,
                String.valueOf(expectedMembersData.getAcspMemberId()),
                acspProfileResponse.getAcspNumber(), expectedMembersData.getUserId(),
                expectedMembersData.getStatus(), expectedMembersData.getUserRole());
        verify(acspMembersService).create(any(AcspMembersRequest.class));
        verify(acspProfileService).create(argThat(profile -> profile.getAmlDetails() == null));
    }

    @Test
    void createAcspMembersDataNullUserId() {
        AcspMembersRequest spec = new AcspMembersRequest();
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createAcspMembersData(spec));
        assertEquals("User ID is required to create an ACSP member", exception.getMessage());
    }

    @Test
    void createAcspMembersDataException() throws DataException {
        AcspMembersRequest spec = new AcspMembersRequest();
        spec.setUserId("userId");

        when(acspProfileService.create(any(AcspProfileRequest.class)))
                .thenThrow(new DataException("Error creating ACSP profile"));
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createAcspMembersData(spec));
        assertEquals(
                "uk.gov.companieshouse.api.testdata.exception.DataException: Error creating ACSP profile",
                exception.getMessage());
    }

    @Test
    void createAcspMembersDataWhenProfileIsNotNull() throws DataException {
        AcspMembersRequest spec = new AcspMembersRequest();
        spec.setUserId("userId");
        AcspProfile profileEntity = new AcspProfile();
        profileEntity.setAcspNumber("acspNumber");
        profileEntity.setName("name");
        profileEntity.setVersion(1L);

        AcspProfileResponse acspProfileResponse = new AcspProfileResponse(profileEntity);
        AcspMembersResponse acspMembersResponse =
                new AcspMembersResponse(new ObjectId(), acspProfileResponse.getAcspNumber(),"userId",
                        "active", "role");
        var acspStatus = "active";
        var acspType = "ltd";
        var supervisoryBody = "financial-conduct-authority-fca";
        var membershipDetails = "Membership ID: FCA654321";
        AcspProfileRequest acspProfile = new AcspProfileRequest();
        acspProfile.setStatus(acspStatus);
        acspProfile.setType(acspType);
        AmlRequest amlRequest = new AmlRequest();
        amlRequest.setSupervisoryBody(supervisoryBody);
        amlRequest.setMembershipDetails(membershipDetails);

        acspProfile.setAmlDetails(Collections.singletonList(amlRequest));

        spec.setAcspProfile(acspProfile);

        when(acspProfileService.create(any(AcspProfileRequest.class))).thenReturn(acspProfileResponse);
        when(acspMembersService.create(any(AcspMembersRequest.class))).thenReturn(acspMembersResponse);
        AcspMembersResponse result = testDataService.createAcspMembersData(spec);

        verifyAcspMembersData(result,
                String.valueOf(acspMembersResponse.getAcspMemberId()),
                acspProfileResponse.getAcspNumber(), acspMembersResponse.getUserId(), acspMembersResponse.getStatus(), acspMembersResponse.getUserRole());
        acspProfileResponse.getAcspNumber();

        verify(acspProfileService).create(acspProfile);

        verify(acspMembersService).create(argThat(membersSpec ->
                acspMembersResponse.getUserId().equals(membersSpec.getUserId())
                        && acspMembersResponse.getAcspNumber().equals(membersSpec.getAcspNumber())
        ));
    }

    @Test
    void createAcspMembersDataWhenProfileIsNull() throws DataException {
        AcspMembersRequest spec = new AcspMembersRequest();
        spec.setUserId("userId");

        AcspProfile profileEntity = new AcspProfile();
        profileEntity.setAcspNumber("acspNumber");
        profileEntity.setName("name");
        profileEntity.setVersion(1L);

        AcspProfileResponse acspProfileResponse = new AcspProfileResponse(profileEntity);
        AcspMembersResponse acspMembersResponse = new AcspMembersResponse(new ObjectId(),
                "acspNumber", "userId", "active", "role");
        spec.setAcspProfile(null);

        when(acspProfileService.create(any(AcspProfileRequest.class))).thenReturn(acspProfileResponse);
        when(acspMembersService.create(any(AcspMembersRequest.class))).thenReturn(acspMembersResponse);

        AcspMembersResponse result = testDataService.createAcspMembersData(spec);

        verifyAcspMembersData(result,
                String.valueOf(acspMembersResponse.getAcspMemberId()),
                acspProfileResponse.getAcspNumber(), acspMembersResponse.getUserId(), acspMembersResponse.getStatus(), acspMembersResponse.getUserRole());

        verify(acspProfileService).create(argThat(profile ->
                profile.getStatus() == null
                        && profile.getType() == null
                        && profile.getAmlDetails() == null));
        verify(acspMembersService).create(argThat(membersSpec ->
                acspMembersResponse.getUserId().equals(membersSpec.getUserId())
                        && acspMembersResponse.getAcspNumber().equals(membersSpec.getAcspNumber())
        ));
    }

    @Test
    void createAcspMembersDataProfileCreationException() throws DataException {
        AcspMembersRequest spec = new AcspMembersRequest();
        spec.setUserId("userId");
        AcspProfileRequest profileRequest = new AcspProfileRequest();
        profileRequest.setStatus("active");
        profileRequest.setType("limited-company");
        spec.setAcspProfile(profileRequest);

        when(acspProfileService.create(any(AcspProfileRequest.class)))
                .thenThrow(new DataException("Error creating ACSP profile"));
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createAcspMembersData(spec));
        assertEquals(
                "uk.gov.companieshouse.api.testdata.exception.DataException: Error creating ACSP profile",
                exception.getMessage());
        verify(acspProfileService).create(profileRequest);
        verify(acspMembersService, never()).create(any(AcspMembersRequest.class));
    }

    @Test
    void createAcspMembersDataMemberCreationException() throws DataException {
        AcspMembersRequest spec = new AcspMembersRequest();
        spec.setUserId("userId");

        AcspProfile profileEntity = new AcspProfile();
        profileEntity.setAcspNumber("acspNumber");
        profileEntity.setName("name");
        profileEntity.setVersion(1L);

        AcspProfileResponse acspProfileResponse = new AcspProfileResponse(profileEntity);
        when(acspProfileService.create(any(AcspProfileRequest.class))).thenReturn(acspProfileResponse);
        when(acspMembersService.create(any(AcspMembersRequest.class)))
                .thenThrow(new DataException("Error creating ACSP member"));
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createAcspMembersData(spec));
        assertEquals(
                "uk.gov.companieshouse.api.testdata.exception.DataException: Error creating ACSP member",
                exception.getMessage());
    }

    @Test
    void deleteAcspMembersData() throws DataException {
        String acspMemberId = "memberId";
        AcspMembers member = new AcspMembers();
        member.setAcspNumber("acspNumber");

        boolean result = deleteAcspMembersDataHelper(acspMemberId, Optional.of(member));

        assertTrue(result);
        verify(acspMembersService).delete(acspMemberId);
        verify(acspProfileService).delete("acspNumber");
    }

    @Test
    void deleteAcspMembersDataNotFound() throws DataException {
        String acspMemberId = "memberId";
        boolean result = deleteAcspMembersDataHelper(acspMemberId, Optional.empty());

        assertFalse(result);
        verify(acspMembersService, never()).delete(anyString());
        verify(acspProfileService, never()).delete(anyString());
    }

    @Test
    void deleteAcspMembersDataException() {
        String acspMemberId = "memberId";
        AcspMembers member = new AcspMembers();
        member.setAcspNumber("acspNumber");

        when(acspMembersRepository.findById(acspMemberId)).thenReturn(Optional.of(member));
        doThrow(new RuntimeException(new DataException("Error")))
                .when(acspMembersService).delete(acspMemberId);
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.deleteAcspMembersData(acspMemberId));
        assertEquals("Error deleting acsp member's data", exception.getMessage());
    }

    @Test
    void createUserDataWithCompanyAuthAllowList() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setIsCompanyAuthAllowList(true);

        UserResponse mockUserResponse = new UserResponse("userId", "email@example.com", "Forename", "Surname");

        when(userService.create(userRequest)).thenReturn(mockUserResponse);

        UserResponse createdUserResponse = testDataService.createUserData(userRequest);

        assertEquals("userId", createdUserResponse.getId());
        assertEquals("email@example.com", createdUserResponse.getEmail());
        assertEquals("Forename", createdUserResponse.getForename());
        assertEquals("Surname", createdUserResponse.getSurname());
        assertTrue(userRequest.getIsCompanyAuthAllowList());

        verify(companyAuthAllowListService, times(1)).create(any(CompanyAuthAllowListRequest.class));
    }

    @Test
    void createUserDataWithOutCompanyAuthAllow() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");

        UserResponse mockUserResponse = new UserResponse("userId", "email@example.com", "Forename", "Surname");

        when(userService.create(userRequest)).thenReturn(mockUserResponse);

        UserResponse createdUserResponse = testDataService.createUserData(userRequest);

        assertEquals("userId", createdUserResponse.getId());
        assertEquals("email@example.com", createdUserResponse.getEmail());
        assertEquals("Forename", createdUserResponse.getForename());
        assertEquals("Surname", createdUserResponse.getSurname());
        assertNull(userRequest.getIsCompanyAuthAllowList());

        verify(companyAuthAllowListService, times(0)).create(any(CompanyAuthAllowListRequest.class));
    }

    @Test
    void createUserDataWithNullCompanyAuthAllowList() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setIsCompanyAuthAllowList(null);

        UserResponse userResponse = new UserResponse("userId", "test@example.com", "Forename", "Surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        UserResponse result = testDataService.createUserData(userRequest);

        assertEquals("test@example.com", result.getEmail());
        assertEquals("Forename", result.getForename());
        assertEquals("Surname", result.getSurname());
        assertEquals("userId", result.getId());
        assertNull(userRequest.getIsCompanyAuthAllowList());
        verify(userService, times(1)).create(userRequest);
        verify(companyAuthAllowListService, never()).create(any(CompanyAuthAllowListRequest.class));
    }

    @Test
    void deleteCompanyAuthAllowList() {
        String userId = "userId";
        User user = new User();
        user.setEmail("email@example.com");

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(userService.delete(userId)).thenReturn(true);
        when(companyAuthAllowListService.getAuthId(user.getEmail())).thenReturn("authId");

        boolean result = testDataService.deleteUserData(userId);

        assertTrue(result);
        verify(userService, times(1)).delete(userId);
        verify(companyAuthAllowListService, times(1)).delete("authId");
    }

    @Test
    void deleteCompanyAuthAllowListWhenNull() {
        String userId = "userId";
        User user = new User();
        user.setEmail("email@example.com");

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(userService.delete(userId)).thenReturn(true);
        when(companyAuthAllowListService.getAuthId(user.getEmail())).thenReturn(null);

        boolean result = testDataService.deleteUserData(userId);

        assertTrue(result);
        verify(userService, times(1)).delete(userId);
        verify(companyAuthAllowListService, never()).delete(anyString());
    }

    @Test
    void deleteUserDataWithEmailAndAllowListId() {
        String userId = "user-id-with-auth";
        User user = new User();
        user.setEmail("allow@example.com");

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(userService.delete(userId)).thenReturn(true);
        when(companyAuthAllowListService.getAuthId(user.getEmail())).thenReturn("allow-list-id");

        boolean result = testDataService.deleteUserData(userId);

        assertTrue(result);
        verify(userService, times(1)).delete(userId);
        verify(companyAuthAllowListService, times(1)).getAuthId("allow@example.com");
        verify(companyAuthAllowListService, times(1)).delete("allow-list-id");
    }

    @Test
    void deleteUserDataWithEmailAndNoAllowListId() {
        String userId = "user-id-without-auth";
        User user = new User();
        user.setEmail("no-allow@example.com");

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(userService.delete(userId)).thenReturn(true);
        when(companyAuthAllowListService.getAuthId(user.getEmail())).thenReturn(null);

        boolean result = testDataService.deleteUserData(userId);

        assertTrue(result);
        verify(userService, times(1)).delete(userId);
        verify(companyAuthAllowListService, times(1)).getAuthId("no-allow@example.com");
        verify(companyAuthAllowListService, never()).delete(anyString());
    }

    @Test
    void deleteUserDataWithNullEmail() {
        String userId = "userId";
        User user = new User();
        user.setEmail(null);

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(userService.delete(userId)).thenReturn(true);

        boolean result = testDataService.deleteUserData(userId);

        assertTrue(result);
        verify(userService, times(1)).delete(userId);
        verify(companyAuthAllowListService, never()).delete(anyString());
    }

    @Test
    void deleteUserDataWithEmptyEmail() {
        String userId = "userId";
        User user = new User();
        user.setEmail("");

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(userService.delete(userId)).thenReturn(true);

        boolean result = testDataService.deleteUserData(userId);

        assertTrue(result);
        verify(userService, times(1)).delete(userId);
        verify(companyAuthAllowListService, never()).delete(anyString());
    }

    @Test
    void deleteAppealsDataSuccess() throws DataException {
        String companyNumber = "12345678";
        String penaltyReference = "penaltyRef";

        when(appealsService.delete(companyNumber, penaltyReference)).thenReturn(true);

        boolean result = testDataService.deleteAppealsData(companyNumber, penaltyReference);

        assertTrue(result);
        verify(appealsService, times(1)).delete(companyNumber, penaltyReference);
    }

    @Test
    void deleteAppealsDataFailure() throws DataException {
        String companyNumber = "12345678";
        String penaltyReference = "penaltyRef";

        when(appealsService.delete(companyNumber, penaltyReference)).thenReturn(false);

        boolean result = testDataService.deleteAppealsData(companyNumber, penaltyReference);

        assertFalse(result);
        verify(appealsService, times(1)).delete(companyNumber, penaltyReference);
    }

    @Test
    void deleteAppealsDataThrowsException() {
        String companyNumber = "12345678";
        String penaltyReference = "penaltyRef";
        RuntimeException ex = new RuntimeException("error");

        when(appealsService.delete(companyNumber, penaltyReference)).thenThrow(ex);
        DataException exception = assertThrows(DataException.class, () ->
                testDataService.deleteAppealsData(companyNumber, penaltyReference));
        assertEquals("Error deleting appeals data", exception.getMessage());
        assertEquals(ex, exception.getCause());
        verify(appealsService, times(1)).delete(companyNumber, penaltyReference);
    }

    @Test
    void createCertificatesData() throws DataException {
        CertificatesRequest spec = new CertificatesRequest();
        spec.setUserId(USER_ID);

        CertificatesResponse.CertificateEntry entry1 = new CertificatesResponse.CertificateEntry(
                "CRT-111111-222222", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );
        CertificatesResponse.CertificateEntry entry2 = new CertificatesResponse.CertificateEntry(
                "CRT-333333-444444", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );

        List<CertificatesResponse.CertificateEntry> entries = List.of(entry1, entry2);
        CertificatesResponse expectedCertificatesResponse = new CertificatesResponse(entries);

        when(certificatesService.create(any(CertificatesRequest.class))).thenReturn(expectedCertificatesResponse);
        CertificatesResponse result = testDataService.createCertificatesData(spec);

        assertNotNull(result);
        assertEquals(2, result.getCertificates().size());
        assertEquals("CRT-111111-222222", result.getCertificates().get(0).getId());
        assertEquals("CRT-333333-444444", result.getCertificates().get(1).getId());

        verify(certificatesService).create(spec);
    }

    @Test
    void createCertificatesDataNullUserId() {
        CertificatesRequest spec = new CertificatesRequest();
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createCertificatesData(spec));
        assertEquals("User ID is required to create certificates", exception.getMessage());
    }

    @Test
    void createCertificatesDataException() throws DataException {
        CertificatesRequest spec = new CertificatesRequest();
        spec.setUserId(USER_ID);

        when(certificatesService.create(any(CertificatesRequest.class)))
                .thenThrow(new DataException("Error creating certificates"));
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createCertificatesData(spec));
        assertEquals("Error creating certificates", exception.getMessage());
    }

    @Test
    void deleteCertificatesData() throws DataException {
        Certificates certificates = new Certificates();
        certificates.setId(CERTIFICATES_ID);

        when(certificatesService.delete(CERTIFICATES_ID)).thenReturn(true);
        boolean result = testDataService.deleteCertificatesData(CERTIFICATES_ID);

        assertTrue(result);
        verify(certificatesService).delete("CRT-123456-789012");
    }

    @Test
    void deleteCertificatesDataFailure() {
        when(certificatesService.delete(CERTIFICATES_ID)).thenReturn(false);
        boolean result;
        try {
            result = testDataService.deleteCertificatesData(CERTIFICATES_ID);
        } catch (DataException e) {
            throw new RuntimeException(e);
        }

        assertFalse(result);
        verify(certificatesService, times(1)).delete(CERTIFICATES_ID);
    }

    @Test
    void deleteCertificatesThrowsException() {
        RuntimeException ex = new RuntimeException("error");
        when(certificatesService.delete(CERTIFICATES_ID)).thenThrow(ex);

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.deleteCertificatesData(CERTIFICATES_ID));
        assertEquals("Error deleting certificates", exception.getMessage());
        assertEquals(ex, exception.getCause());
        verify(certificatesService, times(1)).delete(CERTIFICATES_ID);
    }

    @Test
    void createCertifiedCopiesData() throws DataException {
        CertifiedCopiesRequest spec = new CertifiedCopiesRequest();
        spec.setUserId(USER_ID);

        CertificatesResponse.CertificateEntry entry1 = new CertificatesResponse.CertificateEntry(
                "CCD-111111-222222", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );
        CertificatesResponse.CertificateEntry entry2 = new CertificatesResponse.CertificateEntry(
                "CCD-333333-444444", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );

        List<CertificatesResponse.CertificateEntry> entries = List.of(entry1, entry2);
        CertificatesResponse expectedCertificatesResponse = new CertificatesResponse(entries);

        when(certifiedCopiesService.create(any(CertifiedCopiesRequest.class))).thenReturn(expectedCertificatesResponse);
        CertificatesResponse result = testDataService.createCertifiedCopiesData(spec);

        assertNotNull(result);
        assertEquals(2, result.getCertificates().size());
        assertEquals("CCD-111111-222222", result.getCertificates().get(0).getId());
        assertEquals("CCD-333333-444444", result.getCertificates().get(1).getId());

        verify(certifiedCopiesService).create(spec);
    }

    @Test
    void createCertifiedCopiesDataNullUserId() {
        CertifiedCopiesRequest spec = new CertifiedCopiesRequest();
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createCertifiedCopiesData(spec));
        assertEquals("User ID is required to create certified copies", exception.getMessage());
    }

    @Test
    void createCertifiedCopiesDataException() throws DataException {
        CertifiedCopiesRequest spec = new CertifiedCopiesRequest();
        spec.setUserId(USER_ID);

        when(certifiedCopiesService.create(any(CertifiedCopiesRequest.class)))
                .thenThrow(new DataException("Error creating certified copies"));
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createCertifiedCopiesData(spec));
        assertEquals("Error creating certified copies", exception.getMessage());
    }

    @Test
    void deleteCertifiedCopiesData() throws DataException {
        CertifiedCopies certifiedCopies = new CertifiedCopies();
        certifiedCopies.setId(CERTIFIED_COPIES_ID);

        when(certifiedCopiesService.delete(CERTIFIED_COPIES_ID)).thenReturn(true);
        boolean result = testDataService.deleteCertifiedCopiesData(CERTIFIED_COPIES_ID);

        assertTrue(result);
        verify(certifiedCopiesService).delete(CERTIFIED_COPIES_ID);
    }

    @Test
    void deleteCertifiedCopiesDataFailure() {
        when(certifiedCopiesService.delete(CERTIFIED_COPIES_ID)).thenReturn(false);
        boolean result = false;
        try {
            result = testDataService.deleteCertifiedCopiesData(CERTIFIED_COPIES_ID);
        } catch (DataException e) {
            throw new RuntimeException(e);
        }

        assertFalse(result);
        verify(certifiedCopiesService, times(1)).delete(CERTIFIED_COPIES_ID);
    }

    @Test
    void deleteCertifiedCopiesThrowsException() {
        RuntimeException ex = new RuntimeException("error");
        when(certifiedCopiesService.delete(CERTIFIED_COPIES_ID)).thenThrow(ex);

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.deleteCertifiedCopiesData(CERTIFIED_COPIES_ID));
        assertEquals("Error deleting certified copies", exception.getMessage());
        assertEquals(ex, exception.getCause());
        verify(certifiedCopiesService, times(1)).delete(CERTIFIED_COPIES_ID);
    }

    @Test
    void createMissingImageDeliveriesData() throws DataException {
        MissingImageDeliveriesRequest spec = new MissingImageDeliveriesRequest();
        spec.setUserId(USER_ID);

        CertificatesResponse.CertificateEntry entry1 = new CertificatesResponse.CertificateEntry(
                "MID-111111-222222", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );
        CertificatesResponse.CertificateEntry entry2 = new CertificatesResponse.CertificateEntry(
                "MID-333333-444444", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );

        List<CertificatesResponse.CertificateEntry> entries = List.of(entry1, entry2);
        CertificatesResponse expectedCertificatesResponse = new CertificatesResponse(entries);

        when(missingImageDeliveriesService.create(any(MissingImageDeliveriesRequest.class))).thenReturn(expectedCertificatesResponse);
        CertificatesResponse result = testDataService.createMissingImageDeliveriesData(spec);

        assertNotNull(result);
        assertEquals(2, result.getCertificates().size());
        assertEquals("MID-111111-222222", result.getCertificates().get(0).getId());
        assertEquals("MID-333333-444444", result.getCertificates().get(1).getId());

        verify(missingImageDeliveriesService).create(spec);
    }

    @Test
    void createMissingImageDeliveriesDataNullUserId() {
        MissingImageDeliveriesRequest spec = new MissingImageDeliveriesRequest();
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createMissingImageDeliveriesData(spec));
        assertEquals("User ID is required to create missing image deliveries", exception.getMessage());
    }

    @Test
    void createMissingImageDeliveriesDataException() throws DataException {
        MissingImageDeliveriesRequest spec = new MissingImageDeliveriesRequest();
        spec.setUserId(USER_ID);

        when(missingImageDeliveriesService.create(any(MissingImageDeliveriesRequest.class)))
                .thenThrow(new DataException("Error creating missing image deliveries"));
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createMissingImageDeliveriesData(spec));
        assertEquals("Error creating missing image deliveries", exception.getMessage());
    }

    @Test
    void deleteMissingImageDeliveriesData() throws DataException {
        MissingImageDeliveries missingImageDeliveries = new MissingImageDeliveries();
        missingImageDeliveries.setId(MISSING_IMAGE_DELIVERIES_ID);

        when(missingImageDeliveriesService.delete(MISSING_IMAGE_DELIVERIES_ID)).thenReturn(true);
        boolean result = testDataService.deleteMissingImageDeliveriesData(MISSING_IMAGE_DELIVERIES_ID);

        assertTrue(result);
        verify(missingImageDeliveriesService).delete(MISSING_IMAGE_DELIVERIES_ID);
    }

    @Test
    void deleteMissingImageDeliveriesDataFailure() {
        when(missingImageDeliveriesService.delete(MISSING_IMAGE_DELIVERIES_ID)).thenReturn(false);
        boolean result = false;
        try {
            result = testDataService.deleteMissingImageDeliveriesData(MISSING_IMAGE_DELIVERIES_ID);
        } catch (DataException e) {
            throw new RuntimeException(e);
        }

        assertFalse(result);
        verify(missingImageDeliveriesService, times(1)).delete(MISSING_IMAGE_DELIVERIES_ID);
    }

    @Test
    void deleteMissingImageDeliveriesThrowsException() {
        RuntimeException ex = new RuntimeException("error");
        when(missingImageDeliveriesService.delete(MISSING_IMAGE_DELIVERIES_ID)).thenThrow(ex);

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.deleteMissingImageDeliveriesData(MISSING_IMAGE_DELIVERIES_ID));
        assertEquals("Error deleting missing image deliveries", exception.getMessage());
        assertEquals(ex, exception.getCause());
        verify(missingImageDeliveriesService, times(1)).delete(MISSING_IMAGE_DELIVERIES_ID);
    }

    @Test
    void getAccountPenaltiesData() throws Exception {
        testDataService.getAccountPenaltiesData(PENALTY_ID);
        verify(accountPenaltiesService).getAccountPenalties(PENALTY_ID);
    }

    @Test
    void getAccountPenaltiesDataNotFoundException() throws NoDataFoundException {
        NoDataFoundException ex = new NoDataFoundException(
                "Error retrieving account penalties - not found");
        when(accountPenaltiesService.getAccountPenalties(PENALTY_ID))
                .thenThrow(ex);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                testDataService.getAccountPenaltiesData(PENALTY_ID));
        assertEquals(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void getAccountPenaltiesDataByCustomerCodeAndCompanyCode() throws Exception {
        testDataService.getAccountPenaltiesData(CUSTOMER_CODE, COMPANY_CODE);
        verify(accountPenaltiesService).getAccountPenalties(CUSTOMER_CODE, COMPANY_CODE);
    }

    @Test
    void getAccountPenaltiesDataByCustomerCodeAndCompanyCodeNotFoundException() throws NoDataFoundException {
        NoDataFoundException ex = new NoDataFoundException(
                "Error retrieving account penalties - not found");
        when(accountPenaltiesService.getAccountPenalties(CUSTOMER_CODE, COMPANY_CODE))
                .thenThrow(ex);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                testDataService.getAccountPenaltiesData(CUSTOMER_CODE, COMPANY_CODE));
        assertEquals(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void updateAccountPenaltiesData() throws Exception {
        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);
        testDataService.updateAccountPenaltiesData(PENALTY_REF, request);
        verify(accountPenaltiesService).updateAccountPenalties(PENALTY_REF, request);
    }

    @Test
    void updateAccountPenaltiesDataNotFoundException() throws NoDataFoundException, DataException {
        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);

        NoDataFoundException ex = new NoDataFoundException(
                "Error updating account penalties - not found");
        when(accountPenaltiesService.updateAccountPenalties(PENALTY_REF, request))
                .thenThrow(ex);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                testDataService.updateAccountPenaltiesData(PENALTY_REF, request));
        assertEquals(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void updateAccountPenaltiesDataDataException() throws NoDataFoundException, DataException {
        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);

        DataException ex = new DataException("Error updating account penalties");
        when(accountPenaltiesService.updateAccountPenalties(PENALTY_REF, request))
                .thenThrow(ex);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataService.updateAccountPenaltiesData(PENALTY_REF, request));
        assertEquals(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void deleteAccountPenaltiesData() throws Exception {
        testDataService.deleteAccountPenaltiesData(PENALTY_ID);
        verify(accountPenaltiesService).deleteAccountPenalties(PENALTY_ID);
    }

    @Test
    void deleteAccountPenaltiesDataNotFoundException() throws NoDataFoundException {
        NoDataFoundException ex = new NoDataFoundException(
                "Error deleting account penalties - not found");
        when(accountPenaltiesService.deleteAccountPenalties(PENALTY_ID))
                .thenThrow(ex);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                testDataService.deleteAccountPenaltiesData(PENALTY_ID));
        assertEquals(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void deleteAccountPenaltiesDataException() throws NoDataFoundException {
        DataException ex = new DataException("Error deleting account penalties");
        when(accountPenaltiesService.deleteAccountPenalties(PENALTY_ID))
                .thenThrow(ConstraintViolationException.class);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataService.deleteAccountPenaltiesData(PENALTY_ID));
        assertEquals(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void createPenaltyDataSuccess() throws DataException {
        PenaltyRequest penaltyRequest = new PenaltyRequest();
        penaltyRequest.setCompanyCode("LP");
        penaltyRequest.setCustomerCode("NI23456");

        AccountPenaltiesResponse expectedData = new AccountPenaltiesResponse();
        expectedData.setCompanyCode("LP");
        expectedData.setCustomerCode("NI23456");

        when(accountPenaltiesService.createAccountPenalties(penaltyRequest)).thenReturn(expectedData);

        AccountPenaltiesResponse result = testDataService.createPenaltyData(penaltyRequest);

        assertEquals(expectedData, result);
        verify(accountPenaltiesService, times(1)).createAccountPenalties(penaltyRequest);
    }

    @Test
    void createPenaltyDataThrowsException() throws DataException {
        PenaltyRequest penaltyRequest = new PenaltyRequest();
        penaltyRequest.setCompanyCode("LP");
        penaltyRequest.setCustomerCode("NI23456");

        DataException ex = new DataException("creation failed");
        when(accountPenaltiesService.createAccountPenalties(penaltyRequest)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataService.createPenaltyData(penaltyRequest));
        assertEquals("Error creating account penalties", thrown.getMessage());
        assertEquals(ex, thrown.getCause());
        verify(accountPenaltiesService, times(1)).createAccountPenalties(penaltyRequest);
    }

    @Test
    void getAccountPenaltiesDataDelegatesToService() throws Exception {
        when(accountPenaltiesService.getAccountPenalties(PENALTY_ID))
                .thenReturn(new AccountPenaltiesResponse());
        AccountPenaltiesResponse result = testDataService.getAccountPenaltiesData(PENALTY_ID);
        assertNotNull(result);
        verify(accountPenaltiesService).getAccountPenalties(PENALTY_ID);
    }

    @Test
    void getAccountPenaltiesDataThrowsNoDataFoundException() throws Exception {
        when(accountPenaltiesService.getAccountPenalties(PENALTY_ID))
                .thenThrow(new NoDataFoundException("not found"));
        NoDataFoundException ex = assertThrows(NoDataFoundException.class, () ->
                testDataService.getAccountPenaltiesData(PENALTY_ID));
        assertEquals("Error retrieving account penalties - not found", ex.getMessage());
    }

    @Test
    void createPenaltyDataDelegatesToService() throws Exception {
        PenaltyRequest spec = new PenaltyRequest();
        AccountPenaltiesResponse data = new AccountPenaltiesResponse();
        when(accountPenaltiesService.createAccountPenalties(spec)).thenReturn(data);
        AccountPenaltiesResponse result = testDataService.createPenaltyData(spec);
        assertEquals(data, result);
        verify(accountPenaltiesService).createAccountPenalties(spec);
    }

    @Test
    void createPenaltyDataThrowsDataException() throws Exception {
        PenaltyRequest spec = new PenaltyRequest();
        when(accountPenaltiesService.createAccountPenalties(spec))
                .thenThrow(new DataException("fail"));
        DataException ex = assertThrows(DataException.class, () ->
                testDataService.createPenaltyData(spec));
        assertEquals("Error creating account penalties", ex.getMessage());
    }

    @Test
    void deleteAccountPenaltiesDataDelegatesToService() throws Exception {
        when(accountPenaltiesService.deleteAccountPenalties(PENALTY_ID))
                .thenReturn(ResponseEntity.ok().build());
        ResponseEntity<Void> result = testDataService.deleteAccountPenaltiesData(PENALTY_ID);
        assertNotNull(result);
        verify(accountPenaltiesService).deleteAccountPenalties(PENALTY_ID);
    }

    @Test
    void deleteAccountPenaltiesDataThrowsNoDataFoundException() throws Exception {
        when(accountPenaltiesService.deleteAccountPenalties(PENALTY_ID))
                .thenThrow(new NoDataFoundException("not found"));
        NoDataFoundException ex = assertThrows(NoDataFoundException.class, () ->
                testDataService.deleteAccountPenaltiesData(PENALTY_ID));
        assertEquals("Error deleting account penalties - not found", ex.getMessage());
    }

    @Test
    void deleteAccountPenaltiesDataThrowsDataException() throws Exception {
        when(accountPenaltiesService.deleteAccountPenalties(PENALTY_ID))
                .thenThrow(new RuntimeException("fail"));
        DataException ex = assertThrows(DataException.class, () ->
                testDataService.deleteAccountPenaltiesData(PENALTY_ID));
        assertEquals("Error deleting account penalties", ex.getMessage());
    }

    @Test
    void deleteAccountPenaltyByReferenceDelegatesToService() throws Exception {
        when(accountPenaltiesService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF))
                .thenReturn(ResponseEntity.ok().build());
        ResponseEntity<Void> result = testDataService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF);
        assertNotNull(result);
        verify(accountPenaltiesService).deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF);
    }

    @Test
    void deleteAccountPenaltyByReferenceThrowsNoDataFoundException() throws Exception {
        when(accountPenaltiesService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF))
                .thenThrow(new NoDataFoundException("not found"));
        NoDataFoundException ex = assertThrows(NoDataFoundException.class, () ->
                testDataService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF));
        assertEquals("Error deleting account penalty - not found", ex.getMessage());
    }

    @Test
    void deleteAccountPenaltyByReferenceThrowsDataException() throws Exception {
        when(accountPenaltiesService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF))
                .thenThrow(new RuntimeException("fail"));
        DataException ex = assertThrows(DataException.class, () ->
                testDataService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF));
        assertEquals("Error deleting account penalty", ex.getMessage());
    }

    @Test
    void testGetPostcodesValidCountry() throws DataException {
        var country = "England";
        var streetName = "First Avenue";
        var streetDescriptor = "High Street";
        var dependentLocality = "London Road";
        var postTown = "London";
        var postcodePretty = "EC1 1BB";
        var buildingNumber = 12;
        Postcodes mockPostcode = new Postcodes();
        mockPostcode.setBuildingNumber(buildingNumber);
        Postcodes.Thoroughfare thoroughfare = new Postcodes.Thoroughfare();
        thoroughfare.setName(streetName);
        thoroughfare.setDescriptor(streetDescriptor);
        mockPostcode.setThoroughfare(thoroughfare);
        Postcodes.Locality locality = new Postcodes.Locality();
        locality.setDependentLocality(dependentLocality);
        locality.setPostTown(postTown);
        mockPostcode.setLocality(locality);
        Postcodes.PostcodeDetails postcodeDetails = new Postcodes.PostcodeDetails();
        postcodeDetails.setPretty(postcodePretty);
        mockPostcode.setPostcode(postcodeDetails);
        mockPostcode.setCountry(country);

        when(postcodeService.getPostcodeByCountry(country)).thenReturn(List.of(mockPostcode));
        PostcodesResponse result = testDataService.getPostcodes(country);
        assertEquals(buildingNumber, result.getBuildingNumber());
        assertEquals(streetName + " " + streetDescriptor, result.getFirstLine());
        assertEquals(dependentLocality, result.getDependentLocality());
        assertEquals(postTown, result.getPostTown());
        assertEquals(postcodePretty, result.getPostcode());
        verify(postcodeService, times(1)).getPostcodeByCountry(country);
    }

    @Test
    void testGetPostcodesInvalidCountry() throws DataException {
        String country = "InvalidCountry";

        when(postcodeService.getPostcodeByCountry(country)).thenReturn(List.of());

        PostcodesResponse result = testDataService.getPostcodes(country);

        assertNull(result);
        verify(postcodeService, times(1)).getPostcodeByCountry(country);
    }

    @Test
    void testGetPostcodesThrowsException() {
        String country = "ErrorCountry";

        when(postcodeService.getPostcodeByCountry(country)).thenThrow(new RuntimeException("Error retrieving postcodes"));

        DataException exception = assertThrows(DataException.class, () -> testDataService.getPostcodes(country));

        assertEquals("Error retrieving postcodes", exception.getMessage());
        verify(postcodeService, times(1)).getPostcodeByCountry(country);
    }

    @Test
    void createUserCompanyAssociationData() throws DataException {
        var id = new ObjectId();
        UserCompanyAssociationRequest spec =
                new UserCompanyAssociationRequest();
        spec.setUserId(USER_ID);
        spec.setCompanyNumber(COMPANY_NUMBER);

        UserCompanyAssociationResponse associationData =
                new UserCompanyAssociationResponse(id, spec.getCompanyNumber(),
                        spec.getUserId(), null, CONFIRMED_STATUS,
                        AUTH_CODE_APPROVAL_ROUTE, null);

        when(userCompanyAssociationService.create(spec)).thenReturn(associationData);

        UserCompanyAssociationResponse createdAssociation =
                testDataService.createUserCompanyAssociationData(spec);

        assertNotNull(createdAssociation);
        assertEquals(id.toString(), createdAssociation.getId());
        assertEquals(USER_ID, createdAssociation.getUserId());
        assertEquals(COMPANY_NUMBER, createdAssociation.getCompanyNumber());
        assertEquals(CONFIRMED_STATUS, createdAssociation.getStatus());
        assertEquals(AUTH_CODE_APPROVAL_ROUTE,
                createdAssociation.getApprovalRoute());
        assertNull(createdAssociation.getInvitations());
        assertNull(createdAssociation.getUserEmail());

        verify(userCompanyAssociationService, times(1)).create(spec);
    }

    @Test
    void createUserCompanyAssociationDataNoUserIdOrUserEmail() {
        UserCompanyAssociationRequest spec =
                new UserCompanyAssociationRequest();

        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createUserCompanyAssociationData(spec));
        assertEquals("A user_id or a user_email is required to create "
                + "an association", exception.getMessage());
    }

    @Test
    void createUserCompanyAssociationDataNoCompanyNumber() {
        UserCompanyAssociationRequest spec =
                new UserCompanyAssociationRequest();
        spec.setUserId(USER_ID);

        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createUserCompanyAssociationData(spec));
        assertEquals("Company number is required to create an "
                + "association", exception.getMessage());
    }

    @Test
    void createUserCompanyAssociationDataException() throws DataException {
        UserCompanyAssociationRequest spec =
                new UserCompanyAssociationRequest();
        spec.setUserId(USER_ID);
        spec.setCompanyNumber(COMPANY_NUMBER);

        when(userCompanyAssociationService.create(spec))
                .thenThrow(new RuntimeException("Error creating the "
                        + "association"));

        DataException exception =
                assertThrows(DataException.class,
                        () -> testDataService.createUserCompanyAssociationData(spec));

        assertEquals("Error creating the association",
                exception.getMessage());
        verify(userCompanyAssociationService, times(1)).create(spec);
    }

    @Test
    void deleteUserCompanyAssociation() throws DataException {
        when(userCompanyAssociationService.delete(ASSOCIATION_ID))
                .thenReturn(true);

        boolean result =
                testDataService.deleteUserCompanyAssociationData(ASSOCIATION_ID);

        assertTrue(result);
        verify(userCompanyAssociationService).delete(ASSOCIATION_ID);
    }

    @Test
    void deleteUserCompanyAssociationNotFound() throws DataException {
        when(userCompanyAssociationService.delete(ASSOCIATION_ID))
                .thenReturn(false);

        boolean result =
                testDataService.deleteUserCompanyAssociationData(ASSOCIATION_ID);

        assertFalse(result);
        verify(userCompanyAssociationService, times(1)).delete(ASSOCIATION_ID);
    }

    @Test
    void deleteUserCompanyAssociationException() {
        RuntimeException ex = new RuntimeException("Error deleting "
                + "association");
        when(userCompanyAssociationService.delete(ASSOCIATION_ID))
                .thenThrow(ex);

        DataException exception = assertThrows(DataException.class,
                () -> testDataService.deleteUserCompanyAssociationData(ASSOCIATION_ID));

        assertEquals("Error deleting association",
                exception.getMessage());
        verify(userCompanyAssociationService, times(1)).delete(ASSOCIATION_ID);
    }

    @Test
    void createTransactionData() throws DataException {
        TransactionsRequest transactionsRequest = new TransactionsRequest();
        transactionsRequest.setUserId("Test12454");
        transactionsRequest.setReference("ACSP Registration");
        TransactionsResponse txn = new TransactionsResponse("Test12454","email@email.com" ,"forename","surname","resumeURI","status", "250788-250788-250788");
        when(transactionService.create(transactionsRequest)).thenReturn(txn);
        TransactionsResponse result = testDataService.createTransactionData(transactionsRequest);
        assertEquals(txn, result);
    }

    @Test
    void createTransactionDataException() throws DataException {
        TransactionsRequest transactionsRequest = new TransactionsRequest();
        transactionsRequest.setUserId("Test12454");
        transactionsRequest.setReference("ACSP Registration");
        DataException ex = new DataException("creation failed");
        when(transactionService.create(transactionsRequest)).thenThrow(ex);
        DataException thrown = assertThrows(DataException.class, () ->
                testDataService.createTransactionData(transactionsRequest));
        assertEquals("Error creating transaction", thrown.getMessage());
        assertEquals(ex, thrown.getCause());
    }

    @Test
    void deleteTransactionData() throws DataException {
        when(transactionService.delete(TRANSACTION_ID)).thenReturn(true);

        boolean result = testDataService.deleteTransaction(TRANSACTION_ID);

        assertTrue(result);
        verify(transactionService).delete(TRANSACTION_ID);
    }

    @Test
    void deleteTransactionDataFailure() throws DataException {
        when(transactionService.delete(TRANSACTION_ID)).thenReturn(false);

        boolean result = testDataService.deleteTransaction(TRANSACTION_ID);

        assertFalse(result);
        verify(transactionService, times(1)).delete(TRANSACTION_ID);
    }

    @Test
    void deleteTransactionThrowsException() {
        RuntimeException ex = new RuntimeException("error");
        when(transactionService.delete(TRANSACTION_ID)).thenThrow(ex);

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.deleteTransaction(TRANSACTION_ID));

        assertEquals("Error deleting transaction", exception.getMessage());
        assertEquals(ex, exception.getCause());
        verify(transactionService, times(1)).delete(TRANSACTION_ID);
    }

    @Test
    void createCombinedSicActivitiesData() throws DataException {
        CombinedSicActivitiesRequest spec = new CombinedSicActivitiesRequest();
        spec.setActivityDescription("Braunkohle waschen");
        spec.setSicDescription("Abbau von Braunkohle");
        spec.setIsChActivity(false);
        spec.setActivityDescriptionSearchField("braunkohle waschen");

        CombinedSicActivitiesResponse expectedData =
                new CombinedSicActivitiesResponse(
                        new ObjectId().toHexString(),
                        "12345",
                        "Abbau von Braunkohle"
                );

        when(combinedSicActivitiesService.create(any(CombinedSicActivitiesRequest.class)))
                .thenReturn(expectedData);

        CombinedSicActivitiesResponse result =
                testDataService.createCombinedSicActivitiesData(spec);

        assertNotNull(result);
        assertEquals("12345", result.getSicCode());
        assertEquals("Abbau von Braunkohle", result.getSicDescription());

        verify(combinedSicActivitiesService).create(spec);
    }

    @Test
    void createCombinedSicActivitiesDataException() throws DataException {
        CombinedSicActivitiesRequest spec = new CombinedSicActivitiesRequest();
        spec.setSicDescription("Test Sic Description");

        when(combinedSicActivitiesService.create(any(CombinedSicActivitiesRequest.class)))
                .thenThrow(new DataException("Error creating Sic code and keyword"));

        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createCombinedSicActivitiesData(spec));

        assertEquals("Error creating Sic code and keyword", exception.getMessage());
    }

    @Test
    void deleteCombinedSicActivitiesData() throws DataException {
        when(combinedSicActivitiesService.delete(SIC_ACTIVITY_ID)).thenReturn(true);

        boolean result = testDataService.deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID);

        assertTrue(result);
        verify(combinedSicActivitiesService).delete(SIC_ACTIVITY_ID);
    }

    @Test
    void deleteCombinedSicActivitiesDataFailure() throws DataException {
        when(combinedSicActivitiesService.delete(SIC_ACTIVITY_ID)).thenReturn(false);

        boolean result = testDataService.deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID);

        assertFalse(result);
        verify(combinedSicActivitiesService, times(1)).delete(SIC_ACTIVITY_ID);
    }

    @Test
    void deleteCombinedSicActivitiesThrowsException() {
        RuntimeException ex = new RuntimeException("error");
        when(combinedSicActivitiesService.delete(SIC_ACTIVITY_ID)).thenThrow(ex);

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID));

        assertEquals("Error deleting appeals data", exception.getMessage());
        assertEquals(ex, exception.getCause());
        verify(combinedSicActivitiesService, times(1)).delete(SIC_ACTIVITY_ID);
    }

    @Test
    void findOrCreateCompanyAuthCode_successReturnsAuthCode() throws Exception {
        CompanyAuthCode expected = new CompanyAuthCode();
        expected.setId(COMPANY_NUMBER);
        expected.setAuthCode("999999");

        when(companyAuthCodeService.findOrCreate(COMPANY_NUMBER)).thenReturn(expected);

        CompanyAuthCode actual = testDataService.findOrCreateCompanyAuthCode(COMPANY_NUMBER);

        assertSame(expected, actual);
    }

    @Test
    void findOrCreateCompanyAuthCode_profileNotFoundIsMappedToNoDataFoundException() throws Exception {
        when(companyAuthCodeService.findOrCreate(COMPANY_NUMBER))
                .thenThrow(new NoDataFoundException("profile missing"));

        NoDataFoundException ex = assertThrows(NoDataFoundException.class,
                () -> testDataService.findOrCreateCompanyAuthCode(COMPANY_NUMBER));

        assertEquals("Company profile not found when finding or creating auth code", ex.getMessage());
    }

    @Test
    void findOrCreateCompanyAuthCode_otherExceptionIsWrappedInDataException() throws Exception {
        RuntimeException cause = new RuntimeException("boom");
        when(companyAuthCodeService.findOrCreate(COMPANY_NUMBER)).thenThrow(cause);

        DataException ex = assertThrows(DataException.class,
                () -> testDataService.findOrCreateCompanyAuthCode(COMPANY_NUMBER));

        assertEquals("Error finding or creating company auth code", ex.getMessage());
        // ensure original cause is preserved
        assertSame(cause, ex.getCause());
    }

    @Test
    void getAcspProfileDataReturnsProfile() throws NoDataFoundException {
        String acspNumber = "AP000036";

        AcspProfile profile = new AcspProfile();
        profile.setId(acspNumber);
        profile.setAcspNumber(acspNumber);
        profile.setName("Test ACSP Company");
        profile.setStatus("active");

        when(acspProfileService.getAcspProfile(acspNumber)).thenReturn(Optional.of(profile));

        Optional<AcspProfile> result = testDataService.getAcspProfileData(acspNumber);

        assertNotNull(result);
        assertTrue(result.isPresent());

        AcspProfile returnedProfile = result.get();
        assertEquals(acspNumber, returnedProfile.getId());
        assertEquals(acspNumber, returnedProfile.getAcspNumber());
        assertEquals("Test ACSP Company", returnedProfile.getName());
        assertEquals("active", returnedProfile.getStatus());

        verify(acspProfileService, times(1)).getAcspProfile(acspNumber);
    }

    @Test
    void getAcspProfileDataReturnsEmpty() throws NoDataFoundException {
        String acspNumber = "NON_EXISTENT";

        when(acspProfileService.getAcspProfile(acspNumber)).thenReturn(Optional.empty());

        Optional<AcspProfile> result = testDataService.getAcspProfileData(acspNumber);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(acspProfileService, times(1)).getAcspProfile(acspNumber);
    }

    @Test
    void deleteItemGroupsDataSuccess() throws DataException {
        String orderNumber = "ORD-1234-5678";

        when(itemGroupsService.deleteItemGroups(orderNumber)).thenReturn(true);

        boolean result = testDataService.deleteItemGroupsData(orderNumber);

        assertTrue(result);
        verify(itemGroupsService, times(1)).deleteItemGroups(orderNumber);
    }

    @Test
    void deleteItemGroupsDataReturnsFalse() throws DataException {
        String orderNumber = "ORD-0000-0000";

        when(itemGroupsService.deleteItemGroups(orderNumber)).thenReturn(false);

        boolean result = testDataService.deleteItemGroupsData(orderNumber);

        assertFalse(result);
        verify(itemGroupsService, times(1)).deleteItemGroups(orderNumber);
    }

    @Test
    void deleteItemGroupsDataThrowsException() {
        String orderNumber = "ORD-ERROR-1234";
        RuntimeException cause = new RuntimeException("Mongo failure");

        when(itemGroupsService.deleteItemGroups(orderNumber)).thenThrow(cause);

        DataException exception = assertThrows(
                DataException.class,
                () -> testDataService.deleteItemGroupsData(orderNumber)
        );

        assertEquals("Error deleting Item Groups", exception.getMessage());
        assertSame(cause, exception.getCause());
        verify(itemGroupsService, times(1)).deleteItemGroups(orderNumber);
    }

}
