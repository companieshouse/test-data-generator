package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.Roles;

@NoRepositoryBean
public interface RoleRepository extends MongoRepository<Roles, String> {
}
