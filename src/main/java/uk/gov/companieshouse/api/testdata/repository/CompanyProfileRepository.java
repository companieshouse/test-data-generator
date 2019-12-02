package uk.gov.companieshouse.api.testdata.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;

@NoRepositoryBean
public interface CompanyProfileRepository extends MongoRepository<CompanyProfile, String> {

    Optional<CompanyProfile> findByCompanyNumber(String companyNumber);
}
