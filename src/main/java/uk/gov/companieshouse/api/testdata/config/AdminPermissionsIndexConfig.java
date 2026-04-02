package uk.gov.companieshouse.api.testdata.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import uk.gov.companieshouse.api.testdata.model.entity.AdminPermissions;

@Configuration
public class AdminPermissionsIndexConfig {

    private final MongoTemplate mongoTemplate;

    public AdminPermissionsIndexConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void ensureUniqueGroupNameIndex() {
        mongoTemplate.indexOps(AdminPermissions.class)
                .createIndex(new Index().on("group_name", Sort.Direction.ASC).unique());
    }
}
