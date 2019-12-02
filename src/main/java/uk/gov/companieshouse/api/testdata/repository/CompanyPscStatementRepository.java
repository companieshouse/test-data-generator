package uk.gov.companieshouse.api.testdata.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;

@NoRepositoryBean
public interface CompanyPscStatementRepository extends MongoRepository<CompanyPscStatement, String> {
    Optional<CompanyPscStatement> findByCompanyNumber(String companyNumber);
}
