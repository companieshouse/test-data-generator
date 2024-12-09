package uk.gov.companieshouse.api.testdata.spec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CompanySpecTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private Validator validator;
    private final CompanySpec spec = new CompanySpec();

    // Test that a spec is not accepting field attributes that are not defined in the CompanySpec class
    @Test
    void testDeserializationFailsOnUnknownProperties() {
        ObjectMapper objectMapper = new ObjectMapper();
        String invalidJson = "{ \"jurisdiction\": \"england-wales\", \"companystatus\": \"active\" }";
        assertThrows(UnrecognizedPropertyException.class, () -> objectMapper.readValue(invalidJson, CompanySpec.class));
    }

    // Test that a spec is accepting field attributes that are defined in the CompanySpec class
    @Test
    void testDeserializationSucceedsWithValidProperties() {
        ObjectMapper objectMapper = new ObjectMapper();
        String validJson = "{ \"jurisdiction\": \"england-wales\", \"company_status\": \"dissolved\" }";
        assertDoesNotThrow(() -> {
            objectMapper.readValue(validJson, CompanySpec.class);
        });
    }

    @Test
    void testInvalidCompanyStatus() {
        validator = factory.getValidator();
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyStatus("invalid-company-status");
        Set<ConstraintViolation<CompanySpec>> violations = validator.validate(spec);
        assertTrue(violations.stream().anyMatch(v -> "Invalid company status".equals(v.getMessage())), "Expected a violation message for invalid company status");
    }

    @Test
    void testInvalidCompanyType() {
        validator = factory.getValidator();
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyType("invalid-company-type");
        Set<ConstraintViolation<CompanySpec>> violations = validator.validate(spec);
        assertTrue(violations.stream().anyMatch(v -> "Invalid company type".equals(v.getMessage())), "Expected a violation message for invalid company type");
    }

}