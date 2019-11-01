package uk.gov.companieshouse.api.testdata.repository.officer;

import uk.gov.companieshouse.api.testdata.model.officer.Officer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OfficerRepository extends MongoRepository<Officer, String> {

    Officer findByCompanyNumber(String companyId);
}
