package com.poc.testdata.repository;

import com.poc.testdata.model.PersonsWithSignificantControl.PersonsWithSignificantControl;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PersonsWithSignificantControlRepository extends MongoRepository<PersonsWithSignificantControl, String> {

    PersonsWithSignificantControl findByCompanyNumber(String companyId);
}
