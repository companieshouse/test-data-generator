package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.model.entity.Role;
import uk.gov.companieshouse.api.testdata.model.rest.RoleData;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;
import uk.gov.companieshouse.api.testdata.repository.RoleRepository;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleServiceImpl;

    @Test
    void testCreateRole() {
        RoleSpec roleSpec = new RoleSpec();
        roleSpec.setId("role-id");
        roleSpec.setPermissions(List.of("permission1", "permission2"));

        RoleData roleData=roleServiceImpl.create(roleSpec);
        assertEquals(roleSpec.getId(), roleData.getId(), "Role ID should match");

        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(roleCaptor.capture());
        Role capturedRole = roleCaptor.getValue();
        assertEquals(roleSpec.getId(), capturedRole.getId(), "Role ID should match");
        assertEquals(roleSpec.getPermissions(), capturedRole.getPermissions(), "Permissions should match");
    }

    @Test
    void testCreateRoleWithEmptyPermissions() {
        RoleSpec roleSpec = new RoleSpec();
        roleSpec.setId("role-id");
        roleSpec.setPermissions(List.of());

        RoleData roleData = roleServiceImpl.create(roleSpec);
        assertEquals(roleSpec.getId(), roleData.getId(), "Role ID should match");

        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(roleCaptor.capture());
        Role capturedRole = roleCaptor.getValue();
        assertEquals(roleSpec.getId(), capturedRole.getId(), "Role ID should match");
        assertTrue(capturedRole.getPermissions().isEmpty(), "Permissions should be empty");
    }

    @Test
    void testCreateRoleWithNullPermissions() {
        RoleSpec roleSpec = new RoleSpec();
        roleSpec.setId("role-id");
        roleSpec.setPermissions(null);

        RoleData roleData = roleServiceImpl.create(roleSpec);
        assertEquals(roleSpec.getId(), roleData.getId(), "Role ID should match");

        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(roleCaptor.capture());
        Role capturedRole = roleCaptor.getValue();
        assertEquals(roleSpec.getId(), capturedRole.getId(), "Role ID should match");
        assertNull(capturedRole.getPermissions(), "Permissions should be null");
    }

    @Test
    void testDeleteRoleWithNullId() {
        boolean result = roleServiceImpl.delete(null);
        assertFalse(result, "Role should not be found and thus not deleted");
        verify(roleRepository, never()).delete(any());
    }

    @Test
    void testDeleteRoleWithEmptyId() {
        String roleId = "";
        boolean result = roleServiceImpl.delete(roleId);
        assertFalse(result, "Role should not be found and thus not deleted");
        verify(roleRepository, never()).delete(any());
    }

    @Test
    void testDeleteRole() {
        String roleId = "role-id";
        Role role = new Role();
        role.setId(roleId);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        boolean result = roleServiceImpl.delete(roleId);

        assertTrue(result, "Role should be deleted successfully");
        verify(roleRepository).delete(role);
    }

    @Test
    void testDeleteRoleNotFound() {
        String roleId = "role-id";
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        boolean result = roleServiceImpl.delete(roleId);

        assertFalse(result, "Role should not be found and thus not deleted");
        verify(roleRepository, never()).delete(any());
    }
}
