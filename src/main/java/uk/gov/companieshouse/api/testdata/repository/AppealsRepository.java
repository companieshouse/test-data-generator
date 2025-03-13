package uk.gov.companieshouse.api.testdata.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.Appeals;

@NoRepositoryBean
public interface AppealsRepository extends MongoRepository<Appeals, String> {
    Optional<Appeals> deleteByCompanyNumberAndPenaltyReference(String companyNumber, String penaltyReference);
}
