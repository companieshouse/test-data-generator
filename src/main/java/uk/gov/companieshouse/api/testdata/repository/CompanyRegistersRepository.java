package uk.gov.companieshouse.api.testdata.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyRegisters;

@NoRepositoryBean
public interface CompanyRegistersRepository extends MongoRepository<CompanyRegisters, String> {
    Optional<CompanyRegisters> deleteByCompanyNumber(String companyNumber);
}
