package uk.gov.companieshouse.api.testdata.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.Backlog;

@NoRepositoryBean
public interface BacklogRepository extends MongoRepository<Backlog, String> {
    Optional<Backlog> findByUserId(String userId);

    long deleteByUserId(String userId);
}