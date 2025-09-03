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

import uk.gov.companieshouse.api.testdata.repository.AccountPenaltiesRepository;
import uk.gov.companieshouse.api.testdata.repository.AcspApplicationRepository;
import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.repository.AcspProfileRepository;
import uk.gov.companieshouse.api.testdata.repository.AppealsRepository;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.repository.BasketRepository;
import uk.gov.companieshouse.api.testdata.repository.CertificatesRepository;
import uk.gov.companieshouse.api.testdata.repository.CertifiedCopiesRepository;
import uk.gov.companieshouse.api.testdata.repository.CombinedSicActivitiesRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyAuthAllowListRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyAuthCodeRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyMetricsRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscStatementRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscsRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyRegistersRepository;
import uk.gov.companieshouse.api.testdata.repository.DisqualificationsRepository;
import uk.gov.companieshouse.api.testdata.repository.FilingHistoryRepository;
import uk.gov.companieshouse.api.testdata.repository.IdentityRepository;
import uk.gov.companieshouse.api.testdata.repository.MissingImageDeliveriesRepository;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;
import uk.gov.companieshouse.api.testdata.repository.PostcodesRepository;
import uk.gov.companieshouse.api.testdata.repository.RoleRepository;
import uk.gov.companieshouse.api.testdata.repository.TransactionsRepository;
import uk.gov.companieshouse.api.testdata.repository.UserCompanyAssociationRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;

@Configuration
@EnableConfigurationProperties(MongoProperties.class)
public class MongoConfig {

    private static final String ACCOUNT_DATABASE = "account";
    private static final String ITEMS_DATABASE = "items";
    private static final String SIC_CODE_DATABASE = "sic_code";

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
    public MongoTemplate mongoTemplate() {
        return createMongoTemplate("appointments");
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
        return getMongoRepositoryBean(CertificatesRepository.class, ITEMS_DATABASE);
    }

    @Bean
    public CertifiedCopiesRepository certifiedCopiesRepository() {
        return getMongoRepositoryBean(CertifiedCopiesRepository.class, ITEMS_DATABASE);
    }

    @Bean
    public MissingImageDeliveriesRepository missingImageDeliveriesRepository() {
        return getMongoRepositoryBean(MissingImageDeliveriesRepository.class, ITEMS_DATABASE);
    }

    @Bean
    public AccountPenaltiesRepository accountPenaltiesRepository() {
        return getMongoRepositoryBean(AccountPenaltiesRepository.class, "financial_penalties");
    }

    @Bean
    public PostcodesRepository postcodesRepository() {
        return getMongoRepositoryBean(PostcodesRepository.class, "postcodes");
    }

    @Bean
    public TransactionsRepository transactionsRepository() {
        return getMongoRepositoryBean(TransactionsRepository.class, "transactions");
    }

    @Bean
    public AcspApplicationRepository acspapplicationRepository() {
        return getMongoRepositoryBean(AcspApplicationRepository.class, "acsp_application");
    }


    @Bean
    public DisqualificationsRepository disqualificationsRepository() {
        return getMongoRepositoryBean(DisqualificationsRepository.class, "disqualifications");
    }
  
    @Bean
    public UserCompanyAssociationRepository userCompanyAssociationRepository() {
        return getMongoRepositoryBean(UserCompanyAssociationRepository.class,
                "user_company_associations");
    }

    @Bean
    public CombinedSicActivitiesRepository combinedSicActivitiesRepository() {
        return getMongoRepositoryBean(CombinedSicActivitiesRepository.class, SIC_CODE_DATABASE);
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
