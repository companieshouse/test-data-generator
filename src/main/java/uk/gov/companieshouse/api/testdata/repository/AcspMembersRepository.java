package uk.gov.companieshouse.api.testdata.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;

@NoRepositoryBean
public interface AcspMembersRepository extends MongoRepository<AcspMembers, String> {
    List<AcspMembers> findAllByUserId(String userId);
}
