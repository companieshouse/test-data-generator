package uk.gov.companieshouse.api.testdata.service.validation;

import java.util.Arrays;
import java.util.Set;

import uk.gov.companieshouse.api.testdata.model.rest.enums.CompanyType;

public enum CompanySubTypeRule {

    PRIVATE_FUND_LIMITED_PARTNERSHIP(
            "private-fund-limited-partnership",
            CompanyType.LIMITED_PARTNERSHIP
    );

    private final String value;
    private final Set<CompanyType> validCompanyTypes;

    CompanySubTypeRule(String value, CompanyType... validCompanyTypes) {
        this.value = value;
        this.validCompanyTypes = Set.of(validCompanyTypes);
    }

    public static boolean isValidForCompanyType(String value, CompanyType companyType) {
        return Arrays.stream(values())
                .filter(companySubType -> companySubType.value.equals(value))
                .findFirst()
                .map(companySubType -> companySubType.validCompanyTypes.contains(companyType))
                .orElse(true);
    }
}
