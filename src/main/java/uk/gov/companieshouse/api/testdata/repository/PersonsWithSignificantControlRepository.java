package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.gov.companieshouse.api.testdata.model.psc.PersonsWithSignificantControl;

public interface PersonsWithSignificantControlRepository extends MongoRepository<PersonsWithSignificantControl, String> {

    PersonsWithSignificantControl findByCompanyNumber(String companyId);
}
