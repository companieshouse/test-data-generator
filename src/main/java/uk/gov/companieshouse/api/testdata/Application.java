package uk.gov.companieshouse.api.testdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static final String APPLICATION_NAME = "test-data-generator";

    Application() {
        // default empty constructor
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

}
