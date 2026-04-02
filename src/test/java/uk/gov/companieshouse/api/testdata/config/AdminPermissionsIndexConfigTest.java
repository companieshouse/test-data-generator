package uk.gov.companieshouse.api.testdata.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import uk.gov.companieshouse.api.testdata.model.entity.AdminPermissions;

@ExtendWith(MockitoExtension.class)
class AdminPermissionsIndexConfigTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private IndexOperations indexOperations;

    @Test
    void ensureUniqueGroupNameIndex_createsUniqueIndexOnGroupName() {
        when(mongoTemplate.indexOps(AdminPermissions.class)).thenReturn(indexOperations);
        when(indexOperations.createIndex(any(Index.class))).thenReturn("group_name_idx");

        var config = new AdminPermissionsIndexConfig(mongoTemplate);
        config.ensureUniqueGroupNameIndex();

        verify(mongoTemplate).indexOps(AdminPermissions.class);
        verify(indexOperations).createIndex(any(Index.class));
    }
}
