package uk.gov.companieshouse.api.testdata.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;

@NoRepositoryBean
public interface IdentityRepository extends MongoRepository<Identity, String> {

    Optional<Identity> findByEmail(String email);

    Optional<Identity> findByUserId(String userId);

    boolean existsByEmail(String email);
}