package uk.gov.companieshouse.api.testdata.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "uk.gov.companieshouse.api.testdata.repository.officer", mongoTemplateRef = "officerMongoTemplate")
public class OfficerMongoConfig {

}
