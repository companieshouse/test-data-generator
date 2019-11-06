package uk.gov.companieshouse.api.testdata.repository.companyprofile;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;

@Repository
public interface CompanyProfileRepository extends MongoRepository<CompanyProfile, String> {

    CompanyProfile findByCompanyNumber(String companyNumber);
}
