package uk.gov.companieshouse.api.testdata.repository;

import uk.gov.companieshouse.api.testdata.model.companyprofile.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyProfileRepository extends MongoRepository<Company, String> {

    Company findByCompanyNumber(String companyNumber);
}
