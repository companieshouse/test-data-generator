package uk.gov.companieshouse.api.testdata.config;

import java.io.Serializable;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.Repository;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyAuthCodeRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyMetricsRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.repository.FilingHistoryRepository;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;
import uk.gov.companieshouse.api.testdata.repository.PersonsWithSignificantControlRepository;

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
    public PersonsWithSignificantControlRepository pscRepository() {
        return getMongoRepositoryBean(PersonsWithSignificantControlRepository.class, "company_pscs");
    }

    @Bean
    public AppointmentsRepository appointmentsRepository() {
        return getMongoRepositoryBean(AppointmentsRepository.class, "appointments");
    }
    
    @Bean
    public CompanyMetricsRepository companyMetricsRepository() {
        return getMongoRepositoryBean(CompanyMetricsRepository.class, "company_metrics");
    }

    private MongoTemplate createMongoTemplate(final String database) {
        return new MongoTemplate(
                new SimpleMongoDbFactory(new MongoClient(new MongoClientURI(this.mongoProperties.getUri())), database));
    }

    private <T extends Repository<S, I>, S, I extends Serializable> T getMongoRepositoryBean(Class<T> repositoryClass,
            String database) {
        MongoRepositoryFactoryBean<T, S, I> mongoDbFactoryBean = new MongoRepositoryFactoryBean<>(repositoryClass);
        mongoDbFactoryBean.setMongoOperations(createMongoTemplate(database));
        mongoDbFactoryBean.afterPropertiesSet();
        return mongoDbFactoryBean.getObject();
    }

}
