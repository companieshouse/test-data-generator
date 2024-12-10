package uk.gov.companieshouse.api.testdata.spec;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

@ExtendWith(MockitoExtension.class)
class CompanySpecTest {

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
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyStatus("invalid-company-status");
        validateCompanySpec(spec, "Invalid company status");
    }

    @Test
    void testInvalidCompanyType() {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyType("invalid-company-type");
        validateCompanySpec(spec, "Invalid company type");
    }

    private void validateCompanySpec(CompanySpec spec, String expectedViolationMessage) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CompanySpec>> violations = validator.validate(spec);
        assertTrue(violations.stream().anyMatch(v -> expectedViolationMessage.equals(v.getMessage())), "Expected a violation message for " + expectedViolationMessage);
    }
}