package com.poc.testdata.repository;

import com.poc.testdata.model.Officer.Officer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OfficerRepository extends MongoRepository<Officer, String> {

    Officer findByCompanyNumber(String companyId);
}
