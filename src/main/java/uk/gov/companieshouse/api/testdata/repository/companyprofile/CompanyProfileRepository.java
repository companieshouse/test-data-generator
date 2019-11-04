package uk.gov.companieshouse.api.testdata.repository.companyprofile;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.testdata.model.companyprofile.Company;

@Repository
public interface CompanyProfileRepository extends MongoRepository<Company, String> {

    Company findByCompanyNumber(String companyNumber);
}
