package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;

@NoRepositoryBean
public interface CompanyPscStatementRepository extends MongoRepository<CompanyPscStatement, String> {
    CompanyPscStatement findByCompanyNumber(String companyNumber);
}
