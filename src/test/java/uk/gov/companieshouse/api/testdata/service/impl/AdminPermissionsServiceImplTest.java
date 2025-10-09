package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.gov.companieshouse.api.testdata.model.entity.AdminPermissions;
import uk.gov.companieshouse.api.testdata.model.rest.AdminPermissionsData;
import uk.gov.companieshouse.api.testdata.model.rest.AdminPermissionsSpec;
import uk.gov.companieshouse.api.testdata.repository.AdminPermissionsRepository;

class AdminPermissionsServiceImplTest {

    @Mock
    private AdminPermissionsRepository repository;

    @InjectMocks
    private AdminPermissionsServiceImpl adminPermissionsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_shouldSaveAndReturnData() {
        AdminPermissionsSpec spec = new AdminPermissionsSpec();
        spec.setGroupId("group123");
        spec.setGroupName("Test Group");
        spec.setRoles(List.of("role1", "role2"));

        AdminPermissions saved = new AdminPermissions();
        saved.setId("id123");
        saved.setEntraGroupId("group123");
        saved.setGroupName("Test Group");
        saved.setPermissions(List.of("role1", "role2"));

        when(repository.save(any(AdminPermissions.class))).thenReturn(saved);

        AdminPermissionsData result = adminPermissionsService.create(spec);

        ArgumentCaptor<AdminPermissions> captor = ArgumentCaptor.forClass(AdminPermissions.class);
        verify(repository).save(captor.capture());
        AdminPermissions toSave = captor.getValue();

        assertEquals("group123", toSave.getEntraGroupId());
        assertEquals("Test Group", toSave.getGroupName());
        assertEquals(List.of("role1", "role2"), toSave.getPermissions());

        assertEquals("id123", result.getId());
        assertEquals("Test Group", result.getGroupName());
    }

    @Test
    void delete_shouldDeleteAndReturnTrue_whenExists() {
        AdminPermissions entity = new AdminPermissions();
        entity.setId("id123");
        when(repository.findById("id123")).thenReturn(Optional.of(entity));

        boolean result = adminPermissionsService.delete("id123");

        verify(repository).delete(entity);
        assertTrue(result);
    }

    @Test
    void delete_shouldReturnFalse_whenNotExists() {
        when(repository.findById("id123")).thenReturn(Optional.empty());

        boolean result = adminPermissionsService.delete("id123");

        verify(repository, never()).delete(any());
        assertFalse(result);
    }
}