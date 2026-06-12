package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.AdminPermissionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AdminPermissionsResponse;
import uk.gov.companieshouse.api.testdata.service.AdminPermissionsService;

@ExtendWith(MockitoExtension.class)
class AdminPermissionsControllerTest {
    @Mock
    private AdminPermissionsService adminPermissionsService;

    @InjectMocks
    private AdminPermissionsController adminPermissionsController;

    @Test
    void createAdminPermissions_success() throws DataException {
        AdminPermissionsRequest spec = new AdminPermissionsRequest();
        AdminPermissionsResponse data = new AdminPermissionsResponse("permId", "groupName");

        when(adminPermissionsService.create(spec)).thenReturn(data);

        ResponseEntity<AdminPermissionsResponse> response = adminPermissionsController
                .createAdminPermissions(spec);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(data, response.getBody());
        verify(adminPermissionsService, times(1)).create(spec);
    }

    @Test
    void createAdminPermissions_throwsException() throws DataException {
        AdminPermissionsRequest spec = new AdminPermissionsRequest();
        RuntimeException exception = new RuntimeException("Error creating admin permissions");

        when(adminPermissionsService.create(spec)).thenThrow(exception);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                adminPermissionsController.createAdminPermissions(spec)
        );
        assertEquals(exception, thrown);
        verify(adminPermissionsService, times(1)).create(spec);
    }

    @Test
    void deleteAdminPermissions_success() {
        String id = "permId";
        when(adminPermissionsService.delete(id)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = adminPermissionsController
                .deleteAdminPermissions(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(adminPermissionsService, times(1)).delete(id);
    }

    @Test
    void deleteAdminPermissions_notFound() {
        String id = "permId";
        when(adminPermissionsService.delete(id)).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = adminPermissionsController
                .deleteAdminPermissions(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().get("admin-permissions-id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));
        verify(adminPermissionsService, times(1)).delete(id);
    }

    @Test
    void deleteAdminPermissions_throwsException() {
        String id = "permId";
        RuntimeException exception = new RuntimeException("Error deleting admin permissions");
        when(adminPermissionsService.delete(id)).thenThrow(exception);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                adminPermissionsController.deleteAdminPermissions(id)
        );
        assertEquals(exception, thrown);
        verify(adminPermissionsService, times(1)).delete(id);
    }

}
