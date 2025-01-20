//package uk.gov.companieshouse.api.testdata.service.impl;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//import java.util.Optional;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import uk.gov.companieshouse.api.testdata.exception.DataException;
//import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;
//import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
//import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;
//import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
//import uk.gov.companieshouse.api.testdata.repository.UserRepository;
//import uk.gov.companieshouse.api.testdata.service.RandomService;
//
//@ExtendWith(MockitoExtension.class)
//class AcspMembersServiceImplTest {
//
//    @Mock
//    private AcspMembersRepository repository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private RandomService randomService;
//
//    @InjectMocks
//    private AcspMembersServiceImpl acspMembersService;
//
//    @Captor
//    private ArgumentCaptor<AcspMembers> acspMembersCaptor;
//
//    @Test
//    void testCreateSuccess() throws DataException {
//        // Given
//        AcspMembersSpec spec = new AcspMembersSpec();
//        spec.setUserId("user123");
//        spec.setUserRole("admin");
//        spec.setStatus("invited");
//
//        // Mock user existence
//        when(userRepository.findById("user123")).thenReturn(Optional.of(/* some user object or empty object */ null));
//
//        // Mock AcspProfile creation
//        AcspProfileSpec dummyProfileSpec = new AcspProfileSpec();
//        // The code sets default type & status if needed, so we won't fill them here
//        AcspProfileData acspProfileData = new AcspProfileData("PlaywrightACSP999999");
//        when(acspProfileService.create(any(AcspProfileSpec.class))).thenReturn(acspProfileData);
//
//        // Mock random ID
//        when(randomService.getNumber(4)).thenReturn(1234L);
//        // Mock an etag
//        when(randomService.getEtag()).thenReturn("randomEtag");
//
//        // When
//        AcspMembersData result = acspMembersService.create(spec);
//
//        // Then
//        // Verify repository call
//        verify(repository).save(acspMembersCaptor.capture());
//        AcspMembers saved = acspMembersCaptor.getValue();
//
//        // Assertions on the saved entity
//        assertEquals("ACSPM1234", saved.getAcspMemberId(), "Should prefix ID with ACSPM");
//        assertEquals("PlaywrightACSP999999", saved.getAcspNumber());
//        assertEquals("user123", saved.getUserId());
//        assertEquals("admin", saved.getUserRole());
//        assertEquals("invited", saved.getStatus());
//        assertNotNull(saved.getCreatedAt(), "createdAt should be set");
//        assertNotNull(saved.getAddedAt(), "addedAt should be set");
//        assertNull(saved.getRemovedAt(), "removedAt should be null initially");
//        assertNull(saved.getRemovedBy(), "removedBy should be null initially");
//        assertEquals("randomEtag", saved.getEtag());
//        assertEquals(0, saved.getVersion());
//
//        // Assertions on the returned DTO
//        assertEquals("PlaywrightACSP999999", result.getAcspNumber());
//        assertEquals("user123", result.getUserId());
//        assertEquals("ACSPM1234", result.getAcspMemberId());
//        assertEquals("invited", result.getStatus());
//        assertEquals("admin", result.getUserRole());
//    }
//
//    @Test
//    void testCreateNullSpecThrowsException() {
//        // Expect a DataException if the spec is null
//        DataException thrown = assertThrows(DataException.class,
//                () -> acspMembersService.create(null));
//        assertEquals("AcspMembersSpec cannot be null", thrown.getMessage());
//        verify(repository, never()).save(any());
//    }
//
//    @Test
//    void testCreateUserIdMissingThrowsException() {
//        // Given
//        AcspMembersSpec spec = new AcspMembersSpec();
//        // userId is null/empty
//        spec.setUserId("");
//
//        // When & Then
//        DataException thrown = assertThrows(DataException.class,
//                () -> acspMembersService.create(spec));
//        assertEquals("User ID must be provided", thrown.getMessage());
//        verify(repository, never()).save(any());
//    }
//
//    @Test
//    void testCreateUserNotFound() {
//        // Given
//        AcspMembersSpec spec = new AcspMembersSpec();
//        spec.setUserId("nonExistent");
//        // Mock user doesn't exist
//        when(userRepository.findById("nonExistent")).thenReturn(Optional.empty());
//
//        // Mock the acspProfile creation calls, if needed
//        // but it won't matter if we fail earlier
//
//        // When & Then
//        DataException thrown = assertThrows(DataException.class,
//                () -> acspMembersService.create(spec));
//        assertEquals("User ID 'nonExistent' not found in users collection", thrown.getMessage());
//        verify(repository, never()).save(any());
//    }
//
//    @Test
//    void testCreateAcspProfileThrowsException() throws DataException {
//        // Given
//        AcspMembersSpec spec = new AcspMembersSpec();
//        spec.setUserId("user123");
//        // Mock user found
//        when(userRepository.findById("user123")).thenReturn(Optional.of(/*some object*/null));
//
//        // Mock an exception from acspProfileService
//        DataException ex = new DataException("Profile creation error");
//        when(acspProfileService.create(any(AcspProfileSpec.class))).thenThrow(ex);
//
//        // When & Then
//        DataException thrown = assertThrows(DataException.class,
//                () -> acspMembersService.create(spec));
//        assertEquals("Profile creation error", thrown.getMessage());
//        verify(repository, never()).save(any());
//    }
//
//    @Test
//    void testDeleteFound() {
//        // Given
//        String memberId = "ACSPM1234";
//        AcspMembers member = new AcspMembers();
//        member.setAcspMemberId(memberId);
//
//        when(repository.findById(memberId)).thenReturn(Optional.of(member));
//
//        // When
//        boolean deleted = acspMembersService.delete(memberId);
//
//        // Then
//        assertTrue(deleted, "Should return true if AcspMembers found and deleted");
//        verify(repository).delete(member);
//    }
//
//    @Test
//    void testDeleteNotFound() {
//        // Given
//        String memberId = "ACSPM9999";
//        when(repository.findById(memberId)).thenReturn(Optional.empty());
//
//        // When
//        boolean result = acspMembersService.delete(memberId);
//
//        // Then
//        assertFalse(result, "Should return false if no AcspMembers found");
//        verify(repository, never()).delete(any());
//    }
//
//    @Test
//    void testGetAcspMembersByIdFound() {
//        // Given
//        String memberId = "ACSPM1234";
//        AcspMembers mockMember = new AcspMembers();
//        mockMember.setAcspMemberId(memberId);
//
//        when(repository.findById(memberId)).thenReturn(Optional.of(mockMember));
//
//        // When
//        Optional<AcspMembers> result = acspMembersService.getAcspMembersById(memberId);
//
//        // Then
//        assertTrue(result.isPresent(), "Should return an Optional with AcspMembers if found");
//        assertEquals(memberId, result.get().getAcspMemberId());
//        verify(repository).findById(memberId);
//    }
//
//    @Test
//    void testGetAcspMembersByIdNotFound() {
//        // Given
//        String memberId = "NoSuchMember";
//        when(repository.findById(memberId)).thenReturn(Optional.empty());
//
//        // When
//        Optional<AcspMembers> result = acspMembersService.getAcspMembersById(memberId);
//
//        // Then
//        assertTrue(result.isEmpty(), "Should return Optional.empty() if not found");
//        verify(repository).findById(memberId);
//    }
//}
