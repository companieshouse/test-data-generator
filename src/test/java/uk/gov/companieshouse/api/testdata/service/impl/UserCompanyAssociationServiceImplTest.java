package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
import uk.gov.companieshouse.api.testdata.model.entity.UserCompanyAssociation;
import uk.gov.companieshouse.api.testdata.model.rest.InvitationSpec;
import uk.gov.companieshouse.api.testdata.model.rest.PreviousStateSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserCompanyAssociationData;
import uk.gov.companieshouse.api.testdata.model.rest.UserCompanyAssociationSpec;
import uk.gov.companieshouse.api.testdata.repository.UserCompanyAssociationRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class UserCompanyAssociationServiceImplTest {
    private static final String COMPANY_NUMBER = "TC123456";

    @Mock
    private UserCompanyAssociationRepository repository;

    @Mock
    private RandomService randomService;

    @InjectMocks
    @Spy
    private UserCompanyAssociationServiceImpl service;

    @Test
    void createDefaultAssociation() throws DataException {
        UserCompanyAssociationSpec spec =
                new UserCompanyAssociationSpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setUserId("userId");

        var createdDate = Instant.now();
        doReturn(createdDate).when(service).getCurrentDateTime();

        var id = new ObjectId();
        when(randomService.generateId()).thenReturn(id);

        UserCompanyAssociationData association = service.create(spec);
        assertNotNull(association);
        assertEquals(id.toString(), association.getId());
        assertEquals(COMPANY_NUMBER, association.getCompanyNumber());
        assertEquals("userId", association.getUserId());

        ArgumentCaptor<UserCompanyAssociation> captor =
                ArgumentCaptor.forClass(UserCompanyAssociation.class);
        verify(repository).save(captor.capture());

        UserCompanyAssociation captured = captor.getValue();
        assertEquals(id, captured.getId());
        assertEquals(COMPANY_NUMBER, captured.getCompanyNumber());
        assertEquals("userId", captured.getUserId());
        assertEquals("confirmed", captured.getStatus());
        assertEquals("auth_code", captured.getApprovalRoute());
        assertNull(captured.getUserEmail());
        assertNull(captured.getInvitations());
        assertNull(captured.getApprovalExpiryAt());
        assertNull(captured.getPreviousStates());
        assertEquals(createdDate, captured.getCreatedAt());
    }

    @Test
    void createAssociationWithInvitationAndPreviousState() throws DataException {
        var invitationTime = Instant.now();
        InvitationSpec invitationSpec = new InvitationSpec();
        invitationSpec.setInvitedAt(invitationTime);
        invitationSpec.setInvitedBy("userC");

        PreviousStateSpec previousStateSpec = new PreviousStateSpec();
        previousStateSpec.setStatus("removed");
        previousStateSpec.setChangedBy("userB");
        previousStateSpec.setChangedAt(invitationTime);

        UserCompanyAssociationSpec spec =
                new UserCompanyAssociationSpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setUserId("userA");
        spec.setApprovalRoute("invitation");
        spec.setStatus("awaiting-approval");
        spec.setInvitations(List.of(invitationSpec));
        spec.setPreviousStates(List.of(previousStateSpec));

        var createdDate = Instant.now();
        doReturn(createdDate).when(service).getCurrentDateTime();

        var id = new ObjectId();
        when(randomService.generateId()).thenReturn(id);

        UserCompanyAssociationData association = service.create(spec);
        assertNotNull(association);
        assertEquals(id.toString(), association.getId());
        assertEquals(COMPANY_NUMBER, association.getCompanyNumber());
        assertEquals("userA", association.getUserId());
        assertEquals("awaiting-approval", association.getStatus());
        assertEquals("invitation", association.getApprovalRoute());

        ArgumentCaptor<UserCompanyAssociation> captor =
                ArgumentCaptor.forClass(UserCompanyAssociation.class);
        verify(repository).save(captor.capture());

        UserCompanyAssociation captured = captor.getValue();
        assertEquals(id, captured.getId());
        assertEquals(COMPANY_NUMBER, captured.getCompanyNumber());
        assertEquals("userA", captured.getUserId());
        assertEquals("awaiting-approval", captured.getStatus());
        assertEquals("invitation", captured.getApprovalRoute());
        assertNull(captured.getUserEmail());
        assertEquals(invitationSpec.getInvitedAt(),
                captured.getInvitations().getFirst().getInvitedAt());
        assertEquals(invitationSpec.getInvitedBy(),
                captured.getInvitations().getFirst().getInvitedBy());
        assertEquals(previousStateSpec.getStatus(),
                captured.getPreviousStates().getFirst().getStatus());
        assertEquals(previousStateSpec.getChangedBy(),
                captured.getPreviousStates().getFirst().getChangedBy());
        assertEquals(previousStateSpec.getChangedAt(),
                captured.getPreviousStates().getFirst().getChangedAt());
        assertEquals(createdDate, captured.getCreatedAt());
        assertEquals(createdDate
                .plus(7, ChronoUnit.DAYS), captured.getApprovalExpiryAt());
    }

    @Test
    void createAssociationWithEmail() throws DataException {
        UserCompanyAssociationSpec spec =
                new UserCompanyAssociationSpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setUserEmail("test@example.com");

        var createdDate = Instant.now();
        doReturn(createdDate).when(service).getCurrentDateTime();

        var id = new ObjectId();
        when(randomService.generateId()).thenReturn(id);

        UserCompanyAssociationData association = service.create(spec);
        assertNotNull(association);
        assertEquals(id.toString(), association.getId());
        assertEquals("TC123456", association.getCompanyNumber());
        assertEquals("test@example.com", association.getUserEmail());

        ArgumentCaptor<UserCompanyAssociation> captor =
                ArgumentCaptor.forClass(UserCompanyAssociation.class);
        verify(repository).save(captor.capture());

        UserCompanyAssociation captured = captor.getValue();
        assertEquals(id, captured.getId());
        assertEquals(COMPANY_NUMBER, captured.getCompanyNumber());
        assertEquals("test@example.com", captured.getUserEmail());
        assertEquals("confirmed", captured.getStatus());
        assertEquals("auth_code", captured.getApprovalRoute());
        assertNull(captured.getUserId());
        assertNull(captured.getInvitations());
        assertNull(captured.getApprovalExpiryAt());
        assertNull(captured.getPreviousStates());
        assertEquals(createdDate, captured.getCreatedAt());
    }
    
    @Test
    void deleteAssociation() {
        UserCompanyAssociation userCompanyAssociation =
                new UserCompanyAssociation();
        when(repository.findById("associationId")).thenReturn(Optional.of(userCompanyAssociation));
        boolean result = service.delete("associationId");
        assertTrue(result);
        verify(repository).delete(userCompanyAssociation);
    }

    @Test
    void deleteAssociationNotFound() {
        when(repository.findById("associationId")).thenReturn(Optional.empty());
        boolean result = service.delete("associationId");
        assertFalse(result);
        verify(repository, never()).delete(any(UserCompanyAssociation.class));
    }

    @Test
    void deleteAssociationException() {
        UserCompanyAssociation userCompanyAssociation =
                new UserCompanyAssociation();

        when(repository.findById("associationId")).thenReturn(Optional.of(userCompanyAssociation));
        doThrow(new RuntimeException("Error deleting association"))
                .when(repository).delete(userCompanyAssociation);

        RuntimeException exception =
                assertThrows(RuntimeException.class,
                        () -> service.delete("associationId"));
        assertEquals("Error deleting association", exception.getMessage());
    }
}