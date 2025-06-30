package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.Disqualifications;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface DisqualificationsRepository extends MongoRepository<Disqualifications, String> {
    Optional<List<Disqualifications>> findByCompanyNumber(String companyNumber);
}