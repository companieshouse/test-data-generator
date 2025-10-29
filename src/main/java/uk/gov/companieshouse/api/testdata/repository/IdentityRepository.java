package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;

import java.util.Optional;

@NoRepositoryBean
public interface IdentityRepository extends MongoRepository<Identity, String> {

    Optional<Identity> findByEmail(String email);

    Optional<Identity> findByUserId(String userId);
}