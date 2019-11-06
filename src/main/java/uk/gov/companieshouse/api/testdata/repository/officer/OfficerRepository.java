package uk.gov.companieshouse.api.testdata.repository.officer;

import org.springframework.data.mongodb.repository.MongoRepository;

import uk.gov.companieshouse.api.testdata.model.entity.Officer;

public interface OfficerRepository extends MongoRepository<Officer, String> {

    Officer findByCompanyNumber(String companyId);
}
