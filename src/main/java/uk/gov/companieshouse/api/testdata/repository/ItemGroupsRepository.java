package uk.gov.companieshouse.api.testdata.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.Certificates;
import uk.gov.companieshouse.api.testdata.model.entity.ItemGroups;
import uk.gov.companieshouse.api.testdata.model.entity.Uvid;

@NoRepositoryBean
public interface ItemGroupsRepository extends MongoRepository<ItemGroups, String> {
    Optional<ItemGroups> findByDataOrderNumber(String orderNumber);

    long deleteByDataOrderNumber(String orderNumber);
}
