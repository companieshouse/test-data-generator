package uk.gov.companieshouse.api.testdata.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.Disqualifications;

@NoRepositoryBean
public interface DisqualificationsRepository extends MongoRepository<Disqualifications, String> {
    Optional<List<Disqualifications>> findByCompanyNumber(String companyNumber);
}