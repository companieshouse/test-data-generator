package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import uk.gov.companieshouse.api.testdata.model.entity.PersonsWithSignificantControl;

@NoRepositoryBean
public interface PersonsWithSignificantControlRepository extends MongoRepository<PersonsWithSignificantControl, String> {

    PersonsWithSignificantControl findByCompanyNumber(String companyId);
}
