package uk.gov.companieshouse.api.testdata.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;

import java.util.Optional;

@NoRepositoryBean
public interface CompanyPscsRepository extends MongoRepository<CompanyPscs, String> {
    Optional<List<CompanyPscs>> findByCompanyNumber(String companyNumber);
}
