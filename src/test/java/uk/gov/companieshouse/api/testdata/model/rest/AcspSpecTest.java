package uk.gov.companieshouse.api.testdata.model.rest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AcspSpecTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSetAndGetAcspNumber() {
        AcspSpec spec = new AcspSpec();
        spec.setAcspNumber(123456L);
        assertEquals(123456L, spec.getAcspNumber());
    }

    @Test
    void testSetAndGetStatus() {
        AcspSpec spec = new AcspSpec();
        spec.setCompanyStatus("active");
        assertEquals("active", spec.getStatus());
    }

    @Test
    void testSetAndGetCompanyType() {
        AcspSpec spec = new AcspSpec();
        spec.setCompanyType("ltd");
        assertEquals("ltd", spec.getCompanyType());
    }


    @Test
    void testStatusPatternValid() {
        AcspSpec spec = new AcspSpec();
        spec.setCompanyStatus("active"); // valid value

        Set<ConstraintViolation<AcspSpec>> violations = validator.validate(spec);
        assertTrue(violations.isEmpty(), "No violations expected for a valid status");
    }

    @Test
    void testStatusPatternInvalid() {
        AcspSpec spec = new AcspSpec();
        spec.setCompanyStatus("invalid-status");

        Set<ConstraintViolation<AcspSpec>> violations = validator.validate(spec);
        assertFalse(violations.isEmpty(), "Expecting violation due to invalid status");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Invalid company status")));
    }

    @Test
    void testCompanyTypePatternValid() {
        AcspSpec spec = new AcspSpec();
        spec.setCompanyType("ltd"); // valid

        Set<ConstraintViolation<AcspSpec>> violations = validator.validate(spec);
        assertTrue(violations.isEmpty(), "No violations expected for a valid company type");
    }

    @Test
    void testCompanyTypePatternInvalid() {
        AcspSpec spec = new AcspSpec();
        spec.setCompanyType("not-a-valid-type");

        Set<ConstraintViolation<AcspSpec>> violations = validator.validate(spec);
        assertFalse(violations.isEmpty(), "Expecting violation due to invalid company type");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Invalid company type")));
    }
}
