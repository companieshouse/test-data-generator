package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.AcspApplication;

@NoRepositoryBean
public interface AcspApplicationRepository extends MongoRepository<AcspApplication, String> {
}
