package uk.gov.companieshouse.api.testdata.config;

import com.mongodb.client.MongoClients;
import java.io.Serializable;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.Repository;

import uk.gov.companieshouse.api.testdata.repository.*;

@Configuration
@EnableConfigurationProperties(MongoProperties.class)
public class MongoConfig {

    private static final String ACCOUNT_DATABASE = "account";
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
        return getMongoRepositoryBean(CompanyAuthCodeRepository.class, ACCOUNT_DATABASE);
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
        return getMongoRepositoryBean(CompanyPscStatementRepository.class,
                "company_psc_statements");
    }

    @Bean
    public CompanyPscsRepository companyPscsRepository() {
        return getMongoRepositoryBean(CompanyPscsRepository.class, "company_pscs");
    }

    @Bean
    public UserRepository userRepository() {
        return getMongoRepositoryBean(UserRepository.class, ACCOUNT_DATABASE);
    }

    @Bean
    public RoleRepository roleRepository() {
        return getMongoRepositoryBean(RoleRepository.class, ACCOUNT_DATABASE);
    }

    @Bean
    public AcspProfileRepository acspProfileRepository() {
        return getMongoRepositoryBean(AcspProfileRepository.class, "acsp_profile");
    }

    @Bean
    public AcspMembersRepository acspMembersRepository() {
        return getMongoRepositoryBean(AcspMembersRepository.class, "acsp_members");
    }

    @Bean
    public BasketRepository basketRepository() {
        return getMongoRepositoryBean(BasketRepository.class, "orders");
    }

    @Bean
    public IdentityRepository identityRepository() {
        return getMongoRepositoryBean(IdentityRepository.class, "identity_verification");
    }

    @Bean
    public CompanyAuthAllowListRepository companyAuthAllowListRepository() {
        return getMongoRepositoryBean(CompanyAuthAllowListRepository.class,
                "efs_submissions");
    }

    @Bean
    public AppealsRepository appealsRepository() {
        return getMongoRepositoryBean(AppealsRepository.class, "appeals");
    }

    @Bean
    public CompanyRegistersRepository companyRegistersRepository() {
        return getMongoRepositoryBean(CompanyRegistersRepository.class, "company_registers");
    }

    @Bean
    public CertificatesRepository certificatesRepository() {
        return getMongoRepositoryBean(CertificatesRepository.class, "items");
    }

    @Bean
    public AccountPenaltiesRepository accountPenaltiesRepository() {
        return getMongoRepositoryBean(AccountPenaltiesRepository.class, "financial_penalties");
    }

    @Bean
    public PostcodesRepository postCodesRepository() {
        return getMongoRepositoryBean(PostcodesRepository.class, "postcodes");
    }

    private MongoTemplate createMongoTemplate(final String database) {
        var simpleMongoDbFactory = new SimpleMongoClientDatabaseFactory(
                MongoClients.create(this.mongoProperties.getUri()), database);
        var mappingMongoConverter = getMappingMongoConverter(
                simpleMongoDbFactory);
        return new MongoTemplate(simpleMongoDbFactory, mappingMongoConverter);
    }

    private MappingMongoConverter getMappingMongoConverter(MongoDatabaseFactory factory) {
        var dbRefResolver = new DefaultDbRefResolver(factory);
        var mappingContext = new MongoMappingContext();
        var mappingConverter = new MappingMongoConverter(dbRefResolver,
                mappingContext);
        mappingContext.setSimpleTypeHolder(
                mappingConverter.getCustomConversions().getSimpleTypeHolder());
        mappingContext.afterPropertiesSet();
        mappingConverter.afterPropertiesSet();

        // Don't save _class to mongo
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return mappingConverter;
    }

    private <T extends Repository<S, I>, S, I extends Serializable> T getMongoRepositoryBean(
            Class<T> repositoryClass,
            String database) {
        var mongoDbFactoryBean = new MongoRepositoryFactoryBean<>(
                repositoryClass);
        mongoDbFactoryBean.setMongoOperations(createMongoTemplate(database));
        mongoDbFactoryBean.afterPropertiesSet();
        return mongoDbFactoryBean.getObject();
    }

}
