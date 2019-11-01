package uk.gov.companieshouse.api.testdata;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "uk.gov.companieshouse.api.testdata.repository.filinghistory", mongoTemplateRef = "filingHistoryMongoTemplate")
public class FilingHistoryMongoConfig {

}
