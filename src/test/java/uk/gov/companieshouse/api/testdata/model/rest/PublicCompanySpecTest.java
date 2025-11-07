package uk.gov.companieshouse.api.testdata.model.rest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class PublicCompanySpecTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGettersAndSetters() {
        PublicCompanySpec spec = new PublicCompanySpec();
        spec.setCompanyNumber("12345678");
        assertEquals("12345678", spec.getCompanyNumber());

        spec.setCompanyStatus("active");
        assertEquals("active", spec.getCompanyStatus());

        spec.setNumberOfAppointments(5);
        assertEquals(5, spec.getNumberOfAppointments());

        spec.setForeignCompanyLegalForm(true);
        assertEquals(true, spec.getForeignCompanyLegalForm());
    }

    @Test
    void testCompanyStatusPatternValidation() {
        PublicCompanySpec spec = new PublicCompanySpec();
        spec.setCompanyStatus("invalid-status");
        Set<ConstraintViolation<PublicCompanySpec>> violations = validator.validate(spec);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("companyStatus")));
    }

    @Test
    void testFilingHistoryListSizeValidation() {
        PublicCompanySpec spec = new PublicCompanySpec();
        List<FilingHistorySpec> list = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            list.add(new FilingHistorySpec());
        }
        spec.setFilingHistoryList(list);
        Set<ConstraintViolation<PublicCompanySpec>> violations = validator.validate(spec);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("filingHistoryList")));
    }

    @Test
    void testNumberOfAppointmentsMinMaxValidation() {
        PublicCompanySpec spec = new PublicCompanySpec();
        spec.setNumberOfAppointments(0);
        Set<ConstraintViolation<PublicCompanySpec>> violations = validator.validate(spec);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("numberOfAppointments")));

        spec.setNumberOfAppointments(21);
        violations = validator.validate(spec);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("numberOfAppointments")));
    }

    @Test
    void testNumberOfAppointmentsValid() {
        PublicCompanySpec spec = new PublicCompanySpec();
        spec.setNumberOfAppointments(10);
        Set<ConstraintViolation<PublicCompanySpec>> violations = validator.validate(spec);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testAccountsDueStatusPatternValidation() {
        PublicCompanySpec spec = new PublicCompanySpec();
        spec.setAccountsDueStatus("invalid-status");
        Set<ConstraintViolation<PublicCompanySpec>> violations = validator.validate(spec);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("accountsDueStatus")));
    }

    @Test
    void testWithdrawnStatementsMinMaxValidation() {
        PublicCompanySpec spec = new PublicCompanySpec();
        spec.setWithdrawnStatements(-1);
        Set<ConstraintViolation<PublicCompanySpec>> violations = validator.validate(spec);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("withdrawnStatements")));

        spec.setWithdrawnStatements(21);
        violations = validator.validate(spec);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("withdrawnStatements")));
    }

    @Test
    void testActiveStatementsMinMaxValidation() {
        PublicCompanySpec spec = new PublicCompanySpec();
        spec.setActiveStatements(-1);
        Set<ConstraintViolation<PublicCompanySpec>> violations = validator.validate(spec);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("activeStatements")));

        spec.setActiveStatements(21);
        violations = validator.validate(spec);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("activeStatements")));
    }
}
