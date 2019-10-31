package uk.gov.companieshouse.api.testdata.repository;

import uk.gov.companieshouse.api.testdata.model.filinghistory.FilingHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FilingHistoryRepository extends MongoRepository<FilingHistory, String> {

    FilingHistory findByCompanyNumber(String companyId);
}
