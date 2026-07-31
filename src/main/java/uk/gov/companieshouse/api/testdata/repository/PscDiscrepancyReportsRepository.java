package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.PscDiscrepancyReports;

@NoRepositoryBean
public interface PscDiscrepancyReportsRepository extends MongoRepository<PscDiscrepancyReports, String> {
}