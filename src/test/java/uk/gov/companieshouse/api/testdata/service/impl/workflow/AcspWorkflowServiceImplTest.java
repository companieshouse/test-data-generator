package uk.gov.companieshouse.api.testdata.service.impl.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspMembersRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspProfileRequest;

import uk.gov.companieshouse.api.testdata.model.rest.request.AmlRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspMembersResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspProfileResponse;
import uk.gov.companieshouse.api.testdata.repository.AcspMemberRepository;

import uk.gov.companieshouse.api.testdata.service.AcspMemberService;
import uk.gov.companieshouse.api.testdata.service.AcspProfileService;

@ExtendWith(MockitoExtension.class)
class AcspWorkflowServiceImplTest {

    @Mock
    private AcspMemberService acspMemberService;
    @Mock
    private AcspMemberRepository acspMemberRepository;
    @Mock
    private AcspProfileService acspProfileService;

    private AcspWorkflowServiceImpl acspWorkflowService;

    @BeforeEach
    void setUp() {
        acspWorkflowService = new AcspWorkflowServiceImpl(
                acspProfileService,
                acspMemberRepository,
                acspMemberService);
    }

    /**
     * Helper to create ACSP members data.
     *
     * @param userId      the user id to set on the spec
     * @param profileData the ACSP profile data to be returned by the profile service
     * @param membersData the ACSP members data to be returned by the members service
     * @return the result of testDataService.createAcspMemberRecord(...)
     * @throws DataException if creation fails
     */
    private AcspMembersResponse createAcspMemberHelperRecord(String userId,
                                                             AcspProfileResponse profileData,
                                                             AcspMembersResponse membersData, AcspProfileRequest profileSpec) throws DataException {
        AcspMembersRequest spec = new AcspMembersRequest();
        spec.setUserId(userId);
        spec.setAcspProfile(profileSpec);
        when(acspProfileService.create(any(AcspProfileRequest.class))).thenReturn(profileData);
        when(acspMemberService.create(any(AcspMembersRequest.class))).thenReturn(membersData);
        return acspWorkflowService.createAcspMember(spec);
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
     * @return the result of testDataService.deleteAcspMember(...)
     * @throws DataException if deletion fails
     */
    private boolean deleteAcspMemberHelper(String acspMemberId,
                                           Optional<AcspMembers> memberOptional)
            throws DataException {
        when(acspMemberRepository.findById(acspMemberId)).thenReturn(memberOptional);
        return acspWorkflowService.deleteAcspMember(acspMemberId);
    }

    @Test
    void createAcspMemberRecord() throws DataException {
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
        AcspMembersResponse result = createAcspMemberHelperRecord(
                "userId", acspProfileResponse, expectedMembersData, profileSpec);
        verifyAcspMembersData(result,
                String.valueOf(expectedMembersData.getAcspMemberId()),
                acspProfileResponse.getAcspNumber(), expectedMembersData.getUserId(),
                expectedMembersData.getStatus(), expectedMembersData.getUserRole());
        verify(acspMemberService).create(any(AcspMembersRequest.class));
        verify(acspProfileService).create(argThat(profile -> profile.getAmlDetails() == null));
    }

    @Test
    void createAcspMemberNullUserIdRecord() {
        AcspMembersRequest spec = new AcspMembersRequest();
        DataException exception = assertThrows(DataException.class,
                () -> acspWorkflowService.createAcspMember(spec));
        assertEquals("User ID is required to create an ACSP member", exception.getMessage());
    }

    @Test
    void createAcspMemberExceptionRecord() throws DataException {
        AcspMembersRequest spec = new AcspMembersRequest();
        spec.setUserId("userId");

        when(acspProfileService.create(any(AcspProfileRequest.class)))
                .thenThrow(new DataException("Error creating ACSP profile"));
        DataException exception = assertThrows(DataException.class,
                () -> acspWorkflowService.createAcspMember(spec));
        assertEquals(
                "uk.gov.companieshouse.api.testdata.exception.DataException: Error creating ACSP profile",
                exception.getMessage());
    }

    @Test
    void createAcspMemberWhenProfileIsNotNullRecord() throws DataException {
        AcspMembersRequest spec = new AcspMembersRequest();
        spec.setUserId("userId");
        AcspProfile profileEntity = new AcspProfile();
        profileEntity.setAcspNumber("acspNumber");
        profileEntity.setName("name");
        profileEntity.setVersion(1L);

        AcspProfileResponse acspProfileResponse = new AcspProfileResponse(profileEntity);
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
        AcspMembersResponse acspMembersResponse =
                new AcspMembersResponse(new ObjectId(), acspProfileResponse.getAcspNumber(),"userId",
                        "active", "role");
        when(acspProfileService.create(any(AcspProfileRequest.class))).thenReturn(acspProfileResponse);
        when(acspMemberService.create(any(AcspMembersRequest.class))).thenReturn(acspMembersResponse);
        AcspMembersResponse result = acspWorkflowService.createAcspMember(spec);

        verifyAcspMembersData(result,
                String.valueOf(acspMembersResponse.getAcspMemberId()),
                acspProfileResponse.getAcspNumber(), acspMembersResponse.getUserId(), acspMembersResponse.getStatus(), acspMembersResponse.getUserRole());
        acspProfileResponse.getAcspNumber();

        verify(acspProfileService).create(acspProfile);

        verify(acspMemberService).create(argThat(membersSpec ->
                acspMembersResponse.getUserId().equals(membersSpec.getUserId())
                        && acspMembersResponse.getAcspNumber().equals(membersSpec.getAcspNumber())
        ));
    }

    @Test
    void createAcspMemberWhenProfileIsNullRecord() throws DataException {
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
        when(acspMemberService.create(any(AcspMembersRequest.class))).thenReturn(acspMembersResponse);

        AcspMembersResponse result = acspWorkflowService.createAcspMember(spec);

        verifyAcspMembersData(result,
                String.valueOf(acspMembersResponse.getAcspMemberId()),
                acspProfileResponse.getAcspNumber(), acspMembersResponse.getUserId(), acspMembersResponse.getStatus(), acspMembersResponse.getUserRole());

        verify(acspProfileService).create(argThat(profile ->
                profile.getStatus() == null
                        && profile.getType() == null
                        && profile.getAmlDetails() == null));
        verify(acspMemberService).create(argThat(membersSpec ->
                acspMembersResponse.getUserId().equals(membersSpec.getUserId())
                        && acspMembersResponse.getAcspNumber().equals(membersSpec.getAcspNumber())
        ));
    }

    @Test
    void createAcspMemberProfileCreationExceptionRecord() throws DataException {
        AcspMembersRequest spec = new AcspMembersRequest();
        spec.setUserId("userId");
        AcspProfileRequest profileRequest = new AcspProfileRequest();
        profileRequest.setStatus("active");
        profileRequest.setType("limited-company");
        spec.setAcspProfile(profileRequest);

        when(acspProfileService.create(any(AcspProfileRequest.class)))
                .thenThrow(new DataException("Error creating ACSP profile"));
        DataException exception = assertThrows(DataException.class,
                () -> acspWorkflowService.createAcspMember(spec));
        assertEquals(
                "uk.gov.companieshouse.api.testdata.exception.DataException: Error creating ACSP profile",
                exception.getMessage());
        verify(acspProfileService).create(profileRequest);
        verify(acspMemberService, never()).create(any(AcspMembersRequest.class));
    }

    @Test
    void createAcspMemberMemberRecordCreationException() throws DataException {
        AcspMembersRequest spec = new AcspMembersRequest();
        spec.setUserId("userId");

        AcspProfile profileEntity = new AcspProfile();
        profileEntity.setAcspNumber("acspNumber");
        profileEntity.setName("name");
        profileEntity.setVersion(1L);

        AcspProfileResponse acspProfileResponse = new AcspProfileResponse(profileEntity);
        when(acspProfileService.create(any(AcspProfileRequest.class))).thenReturn(acspProfileResponse);
        when(acspMemberService.create(any(AcspMembersRequest.class)))
                .thenThrow(new DataException("Error creating ACSP member"));
        DataException exception = assertThrows(DataException.class,
                () -> acspWorkflowService.createAcspMember(spec));
        assertEquals(
                "uk.gov.companieshouse.api.testdata.exception.DataException: Error creating ACSP member",
                exception.getMessage());
    }

    @Test
    void deleteAcspMember() throws DataException {
        String acspMemberId = "memberId";
        AcspMembers member = new AcspMembers();
        member.setAcspNumber("acspNumber");

        boolean result = deleteAcspMemberHelper(acspMemberId, Optional.of(member));

        assertTrue(result);
        verify(acspMemberService).delete(acspMemberId);
        verify(acspProfileService).delete("acspNumber");
    }

    @Test
    void deleteAcspMemberNotFound() throws DataException {
        String acspMemberId = "memberId";
        boolean result = deleteAcspMemberHelper(acspMemberId, Optional.empty());

        assertFalse(result);
        verify(acspMemberService, never()).delete(anyString());
        verify(acspProfileService, never()).delete(anyString());
    }

    @Test
    void deleteAcspMemberException() {
        String acspMemberId = "memberId";
        AcspMembers member = new AcspMembers();
        member.setAcspNumber("acspNumber");

        when(acspMemberRepository.findById(acspMemberId)).thenReturn(Optional.of(member));
        doThrow(new RuntimeException(new DataException("Error")))
                .when(acspMemberService).delete(acspMemberId);
        DataException exception = assertThrows(DataException.class,
                () -> acspWorkflowService.deleteAcspMember(acspMemberId));
        assertEquals("Error deleting acsp member's data", exception.getMessage());
    }
}
