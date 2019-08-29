package com.poc.testdata.repository;

import com.poc.testdata.model.CompanyProfile.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyProfileRepository extends MongoRepository<Company, String> {

    Company findByCompanyNumber(String companyNumber);
}
