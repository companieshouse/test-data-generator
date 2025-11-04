package uk.gov.companieshouse.api.testdata.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.Uvid;

@NoRepositoryBean
public interface UvidRepository extends MongoRepository<Uvid, String> {
    Optional<Uvid> findByIdentityId(String identityId);

    long deleteByIdentityId(String identityId);
}