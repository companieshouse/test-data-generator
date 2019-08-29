package com.poc.testdata.repository;

import com.poc.testdata.model.CompanyAuthCodes.CompanyAuthCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyAuthCodeRepository extends MongoRepository<CompanyAuthCode, String> {
}
