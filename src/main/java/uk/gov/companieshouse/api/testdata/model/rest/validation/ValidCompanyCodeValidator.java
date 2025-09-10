package uk.gov.companieshouse.api.testdata.model.rest.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.gov.companieshouse.api.testdata.model.rest.PenaltiesCompanyCodes;

public class ValidCompanyCodeValidator implements ConstraintValidator<ValidCompanyCode, String> {

    @Override
    public void initialize(ValidCompanyCode constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(String companyCode, ConstraintValidatorContext context) {
        if (companyCode == null || companyCode.isBlank()) {
            return true; // Let @NotNull handle blank values
        }
        return PenaltiesCompanyCodes.isValidCompanyCode(companyCode);
    }
}