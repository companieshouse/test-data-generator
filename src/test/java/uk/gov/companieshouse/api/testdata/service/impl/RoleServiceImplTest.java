package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;
import uk.gov.companieshouse.api.testdata.repository.RoleRepository;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleServiceImpl;

    @Test
    void testCreateRole() throws DataException {
        RoleSpec roleSpec = new RoleSpec();
        roleSpec.setId("role-id");
        roleSpec.setPermissions(List.of("permission1", "permission2"));

        roleServiceImpl.create(roleSpec);

        verify(roleRepository).save(argThat(role -> {
            assertEquals(roleSpec.getId(), role.getId(), "Role ID should match");
            assertEquals(roleSpec.getPermissions(), role.getPermissions(), "Permissions should match");
            return true;
        }));
    }

    @Test
    void testDeleteRole() throws DataException {
        String roleId = "role-id";
        when(roleRepository.existsById(roleId)).thenReturn(true);

        boolean result = roleServiceImpl.delete(roleId);

        assertTrue(result, "Role should be deleted successfully");
        verify(roleRepository).deleteById(roleId);
    }

    @Test
    void testDeleteRoleNotFound() {
        String roleId = "role-id";
        when(roleRepository.existsById(roleId)).thenReturn(false);

        DataException exception = assertThrows(DataException.class, () -> roleServiceImpl.delete(roleId));
        assertEquals("Role not found", exception.getMessage());
    }
}
