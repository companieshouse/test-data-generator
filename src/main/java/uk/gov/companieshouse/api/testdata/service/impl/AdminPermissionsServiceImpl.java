package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.entity.AdminPermissions;
import uk.gov.companieshouse.api.testdata.model.rest.AdminPermissionsData;
import uk.gov.companieshouse.api.testdata.model.rest.AdminPermissionsSpec;
import uk.gov.companieshouse.api.testdata.repository.AdminPermissionsRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class AdminPermissionsServiceImpl implements DataService<
        AdminPermissionsData, AdminPermissionsSpec> {

    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(AdminPermissionsServiceImpl.class));

    @Autowired
    private AdminPermissionsRepository repository;

    @Override
    public AdminPermissionsData create(AdminPermissionsSpec spec) {
        LOG.info("Starting creation of admin permissions for group: " + spec.getGroupName());

        var adminPermissions = new AdminPermissions();
        adminPermissions.setEntraGroupId(spec.getGroupId());
        adminPermissions.setGroupName(spec.getGroupName());
        adminPermissions.setPermissions(spec.getRoles());

        AdminPermissions savedPermissions = repository.save(adminPermissions);

        LOG.info("Successfully created admin permissions with ID: " + savedPermissions.getId());

        return new AdminPermissionsData(
                savedPermissions.getId(),
                savedPermissions.getGroupName());
    }

    public boolean delete(String id) {
        LOG.info("Attempting to delete admin permissions with ID: " + id);

        var existingPermissions = repository.findById(id);

        if (existingPermissions.isPresent()) {
            repository.delete(existingPermissions.get());
            LOG.info("Successfully deleted admin permissions with ID: " + id);
            return true;
        } else {
            LOG.info("No admin permissions found for ID: " + id);
            return false;
        }
    }
}