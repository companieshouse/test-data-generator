package uk.gov.companieshouse.api.testdata.model.rest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidCompanyCodeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCompanyCode {
    String message() default "Invalid company code";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}