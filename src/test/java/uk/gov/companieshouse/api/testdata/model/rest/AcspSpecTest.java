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
    void testDefaultJurisdiction() {
        AcspSpec spec = new AcspSpec();
        assertEquals(Jurisdiction.ENGLAND_WALES, spec.getJurisdiction(),
                "Default jurisdiction should be ENGLAND_WALES");
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
    void testSetAndGetAmlDetails() {
        AcspSpec spec = new AcspSpec();
        AcspProfile.AmlDetail amlDetail = new AcspProfile.AmlDetail();
        amlDetail.setSupervisoryBody("financial-conduct-authority-fca");
        amlDetail.setMembershipDetails("Membership ID: FCA654321");

        spec.setAmlDetails(Collections.singletonList(amlDetail));
        assertNotNull(spec.getAmlDetails());
        assertEquals(1, spec.getAmlDetails().size());
        assertEquals("financial-conduct-authority-fca", spec.getAmlDetails().get(0).getSupervisoryBody());
    }

    @Test
    void testSetAndGetEmail() {
        AcspSpec spec = new AcspSpec();
        spec.setEmail("test@example.com");
        assertEquals("test@example.com", spec.getEmail());
    }

    @Test
    void testJurisdictionNotNull() {
        AcspSpec spec = new AcspSpec();
        spec.setJurisdiction(null); // invalid scenario

        Set<ConstraintViolation<AcspSpec>> violations = validator.validate(spec);
        assertFalse(violations.isEmpty(), "Expecting violation due to null jurisdiction");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("invalid jurisdiction")));
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
