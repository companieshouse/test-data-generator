package uk.gov.companieshouse.api.testdata.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.AccountPenalties;

@NoRepositoryBean
public interface AccountPenaltiesRepository extends MongoRepository<AccountPenalties, String> {

    @Query("{ 'company_code':  ?0, 'customer_code':  ?1, "
            + "'data': { '$elemMatch':  { 'transaction_reference':  ?2 }} }")
    Optional<AccountPenalties> findPenalty(
            String companyCode, String customerCode, String transactionReference);

    Optional<AccountPenalties> findAllById(String id);
}