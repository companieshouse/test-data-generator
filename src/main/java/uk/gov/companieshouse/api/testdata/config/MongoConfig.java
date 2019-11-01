package uk.gov.companieshouse.api.testdata.config;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;

@Configuration
@EnableConfigurationProperties(MongoProperties.class)
public class MongoConfig {

    private final MongoProperties mongoProperties;

    public MongoConfig(MongoProperties mongoProperties) {
        super();
        this.mongoProperties = mongoProperties;
    }

    @Bean(name = "companyProfileMongoTemplate")
    public MongoTemplate companyProfileMongoTemplate() {
        return createMongoTemplate(this.mongoProperties.getHost(), this.mongoProperties.getPort(), "company_profile");
    }

    @Bean(name = "accountMongoTemplate")
    public MongoTemplate accountMongoTemplate() {
        return createMongoTemplate(this.mongoProperties.getHost(), this.mongoProperties.getPort(), "account");
    }

    @Bean(name = "filingHistoryMongoTemplate")
    public MongoTemplate filingHistoryMongoTemplate() {
        return createMongoTemplate(this.mongoProperties.getHost(), this.mongoProperties.getPort(),
                "company_filing_history");
    }

    @Bean(name = "officerMongoTemplate")
    public MongoTemplate officerMongoTemplate() {
        return createMongoTemplate(this.mongoProperties.getHost(), this.mongoProperties.getPort(),
                "officer_appointments");
    }

    @Bean(name = "pscMongoTemplate")
    public MongoTemplate pscMongoTemplate() {
        return createMongoTemplate(this.mongoProperties.getHost(), this.mongoProperties.getPort(), "company_pscs");
    }

    private MongoTemplate createMongoTemplate(final String host, final Integer port, final String database) {
        return new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(host, port), database));
    }

}
