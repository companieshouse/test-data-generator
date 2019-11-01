package uk.gov.companieshouse.api.testdata.repository.psc;

import uk.gov.companieshouse.api.testdata.model.psc.PersonsWithSignificantControl;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PersonsWithSignificantControlRepository extends MongoRepository<PersonsWithSignificantControl, String> {

    PersonsWithSignificantControl findByCompanyNumber(String companyId);
}
