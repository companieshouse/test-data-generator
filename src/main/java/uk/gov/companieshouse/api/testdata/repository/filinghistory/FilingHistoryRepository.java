package uk.gov.companieshouse.api.testdata.repository.filinghistory;

import org.springframework.data.mongodb.repository.MongoRepository;

import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;

public interface FilingHistoryRepository extends MongoRepository<FilingHistory, String> {

    FilingHistory findByCompanyNumber(String companyId);
}
