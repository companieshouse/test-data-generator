package uk.gov.companieshouse.api.testdata.config;

import java.io.Serializable;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.Repository;

import com.mongodb.client.MongoClients;

import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyAuthCodeRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyMetricsRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscStatementRepository;
import uk.gov.companieshouse.api.testdata.repository.FilingHistoryRepository;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;

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

    private MongoTemplate createMongoTemplate(final String database) {
        SimpleMongoClientDbFactory simpleMongoDbFactory = new SimpleMongoClientDbFactory(
                MongoClients.create(this.mongoProperties.getUri()), database);
        MappingMongoConverter mappingMongoConverter = getMappingMongoConverter(simpleMongoDbFactory);
        return new MongoTemplate(simpleMongoDbFactory, mappingMongoConverter);
    }

    private MappingMongoConverter getMappingMongoConverter(MongoDbFactory factory) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, new MongoMappingContext());

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
