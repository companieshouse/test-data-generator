package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.AdminPermissionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AdminPermissionsResponse;
import uk.gov.companieshouse.api.testdata.service.AdminPermissionsService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminPermissionsController {
    private final AdminPermissionsService adminPermissionsService;

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String STATUS = "status";

    public AdminPermissionsController(AdminPermissionsService adminPermissionsService) {
        this.adminPermissionsService = adminPermissionsService;
    }

    @PostMapping("/admin-permissions")
    public ResponseEntity<AdminPermissionsResponse> createAdminPermissions(
            @Valid @RequestBody AdminPermissionsRequest request) throws DataException {

        var createdAdminPermissions = adminPermissionsService.create(request);

        Map<String, Object> data = new HashMap<>();
        data.put("admin-permissions-id", createdAdminPermissions.getId());
        data.put("group-name", createdAdminPermissions.getGroupName());
        LOG.info("New admin permissions created", data);
        return new ResponseEntity<>(createdAdminPermissions, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin-permissions/{id}")
    public ResponseEntity<Map<String, Object>> deleteAdminPermissions(
            @PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();
        response.put("admin-permissions-id", id);
        boolean deleted = adminPermissionsService.delete(id);

        if (deleted) {
            LOG.info("Admin permissions deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("Admin permissions not found", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

}
