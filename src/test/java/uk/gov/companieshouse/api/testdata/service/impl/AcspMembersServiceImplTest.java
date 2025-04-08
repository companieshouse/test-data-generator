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

import org.bson.types.ObjectId;
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

        when(randomService.getEtag()).thenReturn("etag");

        AcspMembersData result = service.create(spec);

        assertNotNull(result);
        assertNotNull(result.getAcspMemberId());
        assertEquals("acspNumber", result.getAcspNumber());
        assertEquals("userId", result.getUserId());
        assertEquals("active", result.getStatus());
        assertEquals("role", result.getUserRole());

        ArgumentCaptor<AcspMembers> captor = ArgumentCaptor.forClass(AcspMembers.class);
        verify(repository).save(captor.capture());

        AcspMembers captured = captor.getValue();
        assertNotNull(captured.getAcspMemberId());
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

        when(randomService.getEtag()).thenReturn("etag");

        AcspMembersData result = service.create(spec);

        assertNotNull(result);
        assertNotNull(result.getAcspMemberId());
        assertEquals("acspNumber", result.getAcspNumber());
        assertEquals("userId", result.getUserId());
        assertEquals("active", result.getStatus());
        assertEquals("owner", result.getUserRole());

        ArgumentCaptor<AcspMembers> captor = ArgumentCaptor.forClass(AcspMembers.class);
        verify(repository).save(captor.capture());

        AcspMembers captured = captor.getValue();
        assertNotNull(captured.getAcspMemberId());
        assertEquals("acspNumber", captured.getAcspNumber());
        assertEquals("userId", captured.getUserId());
        assertEquals("active", captured.getStatus());
        assertEquals("owner", captured.getUserRole());
        assertEquals(createdDate, captured.getCreatedAt());
        assertEquals(createdDate, captured.getAddedAt());
        assertEquals(0L, captured.getVersion());
        assertEquals("etag", captured.getEtag());
    }

    @Test
    void deleteAcspMemberException() {
        var id = new ObjectId();
        var stringId = id.toString();
        AcspMembers acspMember = new AcspMembers();
        when(repository.findByAcspMemberId(id)).thenReturn(Optional.of(acspMember));
        doThrow(new RuntimeException("Deletion error")).when(repository).delete(acspMember);

        RuntimeException exception =
                assertThrows(RuntimeException.class,
                        () -> service.delete(stringId));
        assertEquals("Deletion error", exception.getMessage());
    }

    @Test
    void deleteAcspMember() {
        var id = new ObjectId();
        AcspMembers acspMember = new AcspMembers();
        when(repository.findByAcspMemberId(id)).thenReturn(Optional.of(acspMember));

        boolean result = service.delete(String.valueOf(id));

        assertTrue(result);
        verify(repository).delete(acspMember);
    }

    @Test
    void deleteAcspMemberNotFound() {
        var id = new ObjectId();
        when(repository.findByAcspMemberId(id)).thenReturn(Optional.empty());

        boolean result =
                service.delete(String.valueOf(id));

        assertFalse(result);
        verify(repository, never()).delete(any(AcspMembers.class));
    }
}