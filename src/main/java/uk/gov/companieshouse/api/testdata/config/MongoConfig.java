package uk.gov.companieshouse.api.testdata.config;

import java.io.Serializable;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.Repository;

import com.mongodb.client.MongoClients;

import uk.gov.companieshouse.api.testdata.repository.*;

@Configuration
@EnableConfigurationProperties(MongoProperties.class)
public class MongoConfig {

    private final MongoProperties mongoProperties;

    public MongoConfig(MongoProperties mongoProperties) {
        super();
        this.mongoProperties = mongoProperties;
    }

    @Bean
    public CompanyProfileRepository companyProfileRepository() {
        return getMongoRepositoryBean(CompanyProfileRepository.class, "company_profile");
    }

    @Bean
    public CompanyAuthCodeRepository accountRepository() {
        return getMongoRepositoryBean(CompanyAuthCodeRepository.class, "account");
    }

    @Bean
    public FilingHistoryRepository filingHistoryRepository() {
        return getMongoRepositoryBean(FilingHistoryRepository.class, "company_filing_history");
    }

    @Bean
    public OfficerRepository officerRepository() {
        return getMongoRepositoryBean(OfficerRepository.class, "officer_appointments");
    }

    @Bean
    public AppointmentsRepository appointmentsRepository() {
        return getMongoRepositoryBean(AppointmentsRepository.class, "appointments");
    }
    
    @Bean
    public CompanyMetricsRepository companyMetricsRepository() {
        return getMongoRepositoryBean(CompanyMetricsRepository.class, "company_metrics");
    }

    @Bean
    public CompanyPscStatementRepository companyPscStatement() {
        return getMongoRepositoryBean(CompanyPscStatementRepository.class, "company_psc_statements");
    }

    @Bean
    public CompanyPscsRepository companyPscsRepository() {
        return getMongoRepositoryBean(CompanyPscsRepository.class, "company_pscs");
    }

    @Bean
    public UserRepository userRepository() {
        return getMongoRepositoryBean(UserRepository.class, "account");
    }

    @Bean
    public RoleRepository roleRepository() {
        return getMongoRepositoryBean(RoleRepository.class, "account");
    }

    private MongoTemplate createMongoTemplate(final String database) {
        SimpleMongoClientDatabaseFactory simpleMongoDbFactory = new SimpleMongoClientDatabaseFactory(
                MongoClients.create(this.mongoProperties.getUri()), database);
        MappingMongoConverter mappingMongoConverter = getMappingMongoConverter(simpleMongoDbFactory);
        return new MongoTemplate(simpleMongoDbFactory, mappingMongoConverter);
    }

    private MappingMongoConverter getMappingMongoConverter(MongoDatabaseFactory factory) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MongoMappingContext mappingContext = new MongoMappingContext();
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, mappingContext);
        mappingContext.setSimpleTypeHolder(mappingConverter.getCustomConversions().getSimpleTypeHolder());
        mappingContext.afterPropertiesSet();
        mappingConverter.afterPropertiesSet();

        // Don't save _class to mongo
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return mappingConverter;
    }

    private <T extends Repository<S, I>, S, I extends Serializable> T getMongoRepositoryBean(Class<T> repositoryClass,
            String database) {
        MongoRepositoryFactoryBean<T, S, I> mongoDbFactoryBean = new MongoRepositoryFactoryBean<>(repositoryClass);
        mongoDbFactoryBean.setMongoOperations(createMongoTemplate(database));
        mongoDbFactoryBean.afterPropertiesSet();
        return mongoDbFactoryBean.getObject();
    }

}
