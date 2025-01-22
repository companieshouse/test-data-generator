package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityData;
import uk.gov.companieshouse.api.testdata.model.rest.IdentitySpec;
import uk.gov.companieshouse.api.testdata.repository.IdentityRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;


@ExtendWith(MockitoExtension.class)
class IdentityServiceImplTest {

    @Mock
    private IdentityRepository identityRepository;

    @Mock
    private RandomService randomService;

    @InjectMocks
    private IdentityServiceImpl identityServiceImpl;

    @Test
    void testCreateIdentity() {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setUserId("randomised");
        identitySpec.setEmail("test@test.com");
        identitySpec.setVerificationSource("source");

        String generatedIdentityId = "randomised";
        when(randomService.getString(24)).thenReturn(generatedIdentityId);

        IdentityData createdIdentity = identityServiceImpl.create(identitySpec);
        assertEquals(createdIdentity.getId(), generatedIdentityId,
                "ID should match the generated ID");

        ArgumentCaptor<Identity> identityCaptor = ArgumentCaptor.forClass(Identity.class);
        verify(identityRepository).save(identityCaptor.capture());
        Identity savedIdentity = identityCaptor.getValue();

        assertEquals(identitySpec.getEmail(), savedIdentity.getEmail(), "Email should match");
        assertEquals(identitySpec.getUserId(), savedIdentity.getUserId(), "User ID should match");
        assertEquals(identitySpec.getVerificationSource(), savedIdentity.getVerificationSource(),
                "Verification source should match");
        assertEquals(generatedIdentityId, savedIdentity.getId(),
                "ID should match the generated ID");
    }

    @Test
    void testCreateIdentityRepositoryException() {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setUserId("randomised");
        identitySpec.setEmail("test@test.com");
        identitySpec.setVerificationSource("source");

        String generatedIdentityId = "randomised";
        when(randomService.getString(24)).thenReturn(generatedIdentityId);
        doThrow(new RuntimeException("Database error"))
                .when(identityRepository).save(any(Identity.class));

        Exception exception = assertThrows(RuntimeException.class, ()
                -> identityServiceImpl.create(identitySpec));
        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void testDeleteIdentity() {
        Identity mockIdentity = new Identity();
        mockIdentity.setId("identityId");

        when(identityRepository.findById("identityId")).thenReturn(Optional.of(mockIdentity));
        doNothing().when(identityRepository).delete(any(Identity.class));

        boolean result = identityServiceImpl.delete("identityId");

        assertTrue(result, "Identity should be deleted successfully");
        verify(identityRepository, times(1)).delete(mockIdentity);
    }

    @Test
    void testDeleteIdentityNotFound() {
        String identityId = "nonExistentIdentityId";

        when(identityRepository.findById(identityId)).thenReturn(Optional.empty());

        boolean result = identityServiceImpl.delete(identityId);

        assertFalse(result, "Delete should return false when identity does not exist");
        verify(identityRepository, times(1)).findById(identityId);
        verify(identityRepository, times(0)).delete(any(Identity.class));
    }

    @Test
    void testDeleteIdentityRepositoryException() {
        String identityId = "identityId";
        Identity mockIdentity = new Identity();
        mockIdentity.setId(identityId);

        when(identityRepository.findById(identityId)).thenReturn(Optional.of(mockIdentity));
        doThrow(new RuntimeException("Database error")).when(identityRepository)
                .delete(any(Identity.class));

        Exception exception = assertThrows(RuntimeException.class, ()
                -> identityServiceImpl.delete(identityId));

        assertEquals("Database error", exception.getMessage());
    }
}