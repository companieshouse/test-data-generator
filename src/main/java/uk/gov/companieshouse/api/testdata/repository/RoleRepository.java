package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.gov.companieshouse.api.testdata.model.entity.Roles;
import uk.gov.companieshouse.api.testdata.model.entity.Users;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Roles, String> {
    Optional<Roles> findByRoleId(String roleId);
}
