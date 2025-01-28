package uk.gov.companieshouse.api.testdata.model.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.Test;


class CompanySpecTest {

    //Test that the CompanySpec does not accept invalid company status
    @Test
    void testInvalidCompanyStatus() {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyStatus("invalid-company-status");
        validateCompanySpec(spec, "Invalid company status");
    }

    //Test that the CompanySpec does not accept invalid company type
    @Test
    void testInvalidCompanyType() {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyType("invalid-company-type");
        validateCompanySpec(spec, "Invalid company type");
    }

    // Test that the CompanySpec does not accept invalid company subtype
    @Test
    void testInvalidCompanySubtype() {
        CompanySpec spec = new CompanySpec();
        spec.setSubType("invalid-company-subtype");
        validateCompanySpec(spec, "Invalid company subtype");
    }

    private void validateCompanySpec(CompanySpec spec, String expectedViolationMessage) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CompanySpec>> violations = validator.validate(spec);
        assertTrue(violations.stream().anyMatch(v ->
                expectedViolationMessage.equals(v.getMessage())),
                "Expected a violation message for " + expectedViolationMessage);
    }
}