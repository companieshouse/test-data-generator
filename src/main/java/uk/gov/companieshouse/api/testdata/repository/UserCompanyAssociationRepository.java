package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.testdata.model.entity.UserCompanyAssociation;

@Repository
public interface UserCompanyAssociationRepository extends MongoRepository<UserCompanyAssociation, String> {

}