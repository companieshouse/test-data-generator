package com.poc.testdata.repository;

import com.poc.testdata.model.FilingHistory.FilingHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FilingHistoryRepository extends MongoRepository<FilingHistory, String> {

    FilingHistory findByCompanyNumber(String companyId);
}
