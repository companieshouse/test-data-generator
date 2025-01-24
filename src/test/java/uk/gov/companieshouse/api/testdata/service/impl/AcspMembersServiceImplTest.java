package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;
import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class AcspMembersServiceImplTest {

    @Mock
    private AcspMembersRepository repository;

    @Mock
    private RandomService randomService;

    @InjectMocks
    @Spy
    private AcspMembersServiceImpl service;

    @Test
    void createAcspMember() throws DataException {
        AcspMembersSpec spec = new AcspMembersSpec();
        spec.setAcspNumber("acspNumber");
        spec.setUserId("userId");
        spec.setUserRole("role");
        spec.setStatus("active");

        final var createdDate = Instant.now();
        doReturn(createdDate).when(service).getCurrentDateTime();

        when(randomService.getString(12)).thenReturn("randomId");
        when(randomService.getEtag()).thenReturn("etag");

        AcspMembersData result = service.create(spec);

        assertNotNull(result);
        assertEquals("randomId", result.getAcspMemberId());
        assertEquals("acspNumber", result.getAcspNumber());
        assertEquals("userId", result.getUserId());
        assertEquals("active", result.getStatus());
        assertEquals("role", result.getUserRole());

        ArgumentCaptor<AcspMembers> captor = ArgumentCaptor.forClass(AcspMembers.class);
        verify(repository).save(captor.capture());

        AcspMembers captured = captor.getValue();
        assertEquals("randomId", captured.getAcspMemberId());
        assertEquals("acspNumber", captured.getAcspNumber());
        assertEquals("userId", captured.getUserId());
        assertEquals("active", captured.getStatus());
        assertEquals("role", captured.getUserRole());
        assertEquals(createdDate, captured.getCreatedAt());
        assertEquals(createdDate, captured.getAddedAt());
        assertEquals(0L, captured.getVersion());
        assertEquals("etag", captured.getEtag());
    }

    @Test
    void createAcspMemberWithDefaultValues() throws DataException {
        AcspMembersSpec spec = new AcspMembersSpec();
        spec.setAcspNumber("acspNumber");
        spec.setUserId("userId");

        final var createdDate = Instant.now();
        doReturn(createdDate).when(service).getCurrentDateTime();

        when(randomService.getString(12)).thenReturn("randomId");
        when(randomService.getEtag()).thenReturn("etag");

        AcspMembersData result = service.create(spec);

        assertNotNull(result);
        assertEquals("randomId", result.getAcspMemberId());
        assertEquals("acspNumber", result.getAcspNumber());
        assertEquals("userId", result.getUserId());
        assertEquals("active", result.getStatus());
        assertEquals("member", result.getUserRole());

        ArgumentCaptor<AcspMembers> captor = ArgumentCaptor.forClass(AcspMembers.class);
        verify(repository).save(captor.capture());

        AcspMembers captured = captor.getValue();
        assertEquals("randomId", captured.getAcspMemberId());
        assertEquals("acspNumber", captured.getAcspNumber());
        assertEquals("userId", captured.getUserId());
        assertEquals("active", captured.getStatus());
        assertEquals("member", captured.getUserRole());
        assertEquals(createdDate, captured.getCreatedAt());
        assertEquals(createdDate, captured.getAddedAt());
        assertEquals(0L, captured.getVersion());
        assertEquals("etag", captured.getEtag());
    }

    @Test
    void deleteAcspMemberException() {
        AcspMembers acspMember = new AcspMembers();
        when(repository.findById("memberId")).thenReturn(Optional.of(acspMember));
        doThrow(new RuntimeException("Deletion error")).when(repository).delete(acspMember);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.delete("memberId"));
        assertEquals("Deletion error", exception.getMessage());
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