package uk.gov.companieshouse.api.testdata.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;

@NoRepositoryBean
public interface PostcodesRepository extends MongoRepository<Postcodes, String> {
    @Query(value = "{ '$or': ?0 }")
    List<Postcodes> findByStrippedContaining(List<Object> substrings, Pageable pageable);
}
