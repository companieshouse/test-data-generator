package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.Certificates;
import uk.gov.companieshouse.api.testdata.model.entity.CombinedSicActivities;

@NoRepositoryBean
public interface CombinedSicActivitiesRepository extends MongoRepository<CombinedSicActivities, String> {
}
