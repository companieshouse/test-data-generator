package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import uk.gov.companieshouse.api.testdata.model.entity.Officer;

@NoRepositoryBean
public interface OfficerRepository extends MongoRepository<Officer, String> {

    Officer findByCompanyNumber(String companyId);
}
