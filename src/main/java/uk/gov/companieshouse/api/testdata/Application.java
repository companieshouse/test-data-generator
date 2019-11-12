package uk.gov.companieshouse.api.testdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude=MongoAutoConfiguration.class)
public class Application {
    
    Application() {
        // default empty constructor
    }

    public static void main(String[] args) {
        String[] emptyArgs = new String[0];
        SpringApplication.run(Application.class, emptyArgs);
    }

}
