package uk.gov.companieshouse.api.testdata.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;

@NoRepositoryBean
public interface AcspMembersRepository extends MongoRepository<AcspMembers, String> {
    Optional<AcspMembers> findByAcspMemberId(ObjectId id);
}
