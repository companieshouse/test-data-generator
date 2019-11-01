package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.gov.companieshouse.api.testdata.model.filinghistory.FilingHistory;

public interface FilingHistoryRepository extends MongoRepository<FilingHistory, String> {

    FilingHistory findByCompanyNumber(String companyId);
}
