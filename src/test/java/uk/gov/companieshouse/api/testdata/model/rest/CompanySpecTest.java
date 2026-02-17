package uk.gov.companieshouse.api.testdata.model.rest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class CompanySpecTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

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

        String invalidCompanyType = "invalid-company-type";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            CompanyType.valueOf(invalidCompanyType));

        assertTrue(exception.getMessage().contains("No enum constant"));
    }

    // Test that the CompanySpec does not accept invalid company subtype
    @Test
    void testInvalidCompanySubtype() {
        CompanySpec spec = new CompanySpec();
        spec.setSubType("invalid-company-subtype");
        validateCompanySpec(spec, "Invalid company subtype");
    }

    private void validateCompanySpec(CompanySpec spec, String expectedViolationMessage) {
        Set<ConstraintViolation<CompanySpec>> violations = validator.validate(spec);
        assertTrue(violations.stream().anyMatch(v ->
                expectedViolationMessage.equals(v.getMessage())),
                "Expected a violation message for " + expectedViolationMessage);
    }

    @Test
    void testNumberOfAppointmentsMinValid() {
        CompanySpec spec = new CompanySpec();
        spec.setNumberOfAppointments(1);
        assertNoViolations(spec);
    }

    @Test
    void testNumberOfAppointmentsMaxValid() {
        CompanySpec spec = new CompanySpec();
        spec.setNumberOfAppointments(20);
        assertNoViolations(spec);
    }

    @Test
    void testNumberOfAppointmentsTooLow() {
        CompanySpec spec = new CompanySpec();
        spec.setNumberOfAppointments(0);
        validateCompanySpec(spec, "Number of appointments must be at least 1");
    }

    @Test
    void testNumberOfAppointmentsTooHigh() {
        CompanySpec spec = new CompanySpec();
        spec.setNumberOfAppointments(21);
        validateCompanySpec(spec, "Number of appointments must not exceed 20");
    }

    @Test
    void testNumberOfPscMinValid() {
        CompanySpec spec = new CompanySpec();
        spec.setNumberOfPscs(1);
        assertNoViolations(spec);
    }

    @Test
    void testNumberOfPscMaxValid() {
        CompanySpec spec = new CompanySpec();
        spec.setNumberOfPscs(20);
        assertNoViolations(spec);
    }

    @Test
    void testNumberOfPscTooLow() {
        CompanySpec spec = new CompanySpec();
        spec.setNumberOfPscs(0);
        validateCompanySpec(spec, "Number of PSCs must be at least 1");
    }

    @Test
    void testNumberOfPscTooHigh() {
        CompanySpec spec = new CompanySpec();
        spec.setNumberOfPscs(21);
        validateCompanySpec(spec, "Number of PSCs must not exceed 20");
    }

    @Test
    void testWithdrawnStatementsMinValid() {
        CompanySpec spec = new CompanySpec();
        spec.setWithdrawnStatements(0);
        assertNoViolations(spec);
    }

    @Test
    void testWithdrawnStatementsMaxValid() {
        CompanySpec spec = new CompanySpec();
        spec.setWithdrawnStatements(20);
        assertNoViolations(spec);
    }

    @Test
    void testWithdrawnStatementsTooLow() {
        CompanySpec spec = new CompanySpec();
        spec.setWithdrawnStatements(-1);
        validateCompanySpec(spec, "Withdrawn statements must be at least 0");
    }

    @Test
    void testWithdrawnStatementsTooHigh() {
        CompanySpec spec = new CompanySpec();
        spec.setWithdrawnStatements(21);
        validateCompanySpec(spec, "Withdrawn statements must not exceed 20");
    }

    @Test
    void testActiveStatementsMinValid() {
        CompanySpec spec = new CompanySpec();
        spec.setActiveStatements(0);
        assertNoViolations(spec);
    }

    @Test
    void testActiveStatementsMaxValid() {
        CompanySpec spec = new CompanySpec();
        spec.setActiveStatements(20);
        assertNoViolations(spec);
    }

    @Test
    void testActiveStatementsTooLow() {
        CompanySpec spec = new CompanySpec();
        spec.setActiveStatements(-1);
        validateCompanySpec(spec, "Active statements must be at least 0");
    }

    @Test
    void testActiveStatementsTooHigh() {
        CompanySpec spec = new CompanySpec();
        spec.setActiveStatements(21);
        validateCompanySpec(spec, "Active statements must not exceed 20");
    }

    private void assertNoViolations(CompanySpec spec) {
        Set<ConstraintViolation<CompanySpec>> violations = validator.validate(spec);
        assertTrue(violations.isEmpty(), "Expected no violations, but found: " + violations);
    }
}