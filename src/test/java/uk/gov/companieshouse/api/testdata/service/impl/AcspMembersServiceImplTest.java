package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;
import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class AcspMembersServiceImplTest {

    @Mock
    private AcspMembersRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RandomService randomService;

    @InjectMocks
    private AcspMembersServiceImpl service;

    @Test
    void createAcspMember() throws DataException {
        AcspMembersSpec spec = new AcspMembersSpec();
        spec.setAcspNumber("acspNumber");
        spec.setUserId("userId");
        spec.setUserRole("role");
        spec.setStatus("active");

        when(randomService.getString(12)).thenReturn("randomId");
        when(randomService.getEtag()).thenReturn("etag");

        AcspMembersData result = service.create(spec);

        assertNotNull(result);
        assertEquals("randomId", result.getAcspMemberId());
        assertEquals("acspNumber", result.getAcspNumber());
        assertEquals("userId", result.getUserId());
        assertEquals("active", result.getStatus());
        assertEquals("role", result.getUserRole());

        verify(repository).save(any(AcspMembers.class));
    }

    @Test
    void createAcspMemberNullSpec() {
        DataException exception = assertThrows(DataException.class, () -> service.create(null));
        assertEquals("AcspMembersSpec cannot be null", exception.getMessage());
    }

    @Test
    void deleteAcspMember() {
        AcspMembers acspMember = new AcspMembers();
        when(repository.findById("memberId")).thenReturn(Optional.of(acspMember));

        boolean result = service.delete("memberId");

        assertTrue(result);
        verify(repository).delete(acspMember);
    }

    @Test
    void deleteAcspMemberNotFound() {
        when(repository.findById("memberId")).thenReturn(Optional.empty());

        boolean result = service.delete("memberId");

        assertFalse(result);
        verify(repository, never()).delete(any(AcspMembers.class));
    }
}