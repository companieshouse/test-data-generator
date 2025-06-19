package uk.gov.companieshouse.api.testdata.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.PostCodes;



@NoRepositoryBean
public interface PostCodesRepository extends MongoRepository<PostCodes, String> {
    List<PostCodes> findByCountryContaining(String country);
}
