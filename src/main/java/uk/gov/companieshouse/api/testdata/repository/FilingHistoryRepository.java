package uk.gov.companieshouse.api.testdata.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;

@NoRepositoryBean
public interface FilingHistoryRepository extends MongoRepository<FilingHistory, String> {

    Optional<FilingHistory> findByCompanyNumber(String companyId);
}
