package uk.gov.companieshouse.api.testdata.repository;

import uk.gov.companieshouse.api.testdata.model.companyauthcode.CompanyAuthCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyAuthCodeRepository extends MongoRepository<CompanyAuthCode, String> {
}
