package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.CertifiedCopies;
import uk.gov.companieshouse.api.testdata.model.entity.MissingImageDeliveries;

@NoRepositoryBean
public interface MissingImageDeliveriesRepository extends MongoRepository<MissingImageDeliveries, String> {
}
