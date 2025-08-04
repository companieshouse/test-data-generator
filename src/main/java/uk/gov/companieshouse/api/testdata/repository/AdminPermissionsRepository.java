package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.gov.companieshouse.api.testdata.model.rest.AdminPermissions;

public interface AdminPermissionsRepository extends MongoRepository<AdminPermissions, String> {

}
