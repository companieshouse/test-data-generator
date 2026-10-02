package uk.gov.companieshouse.api.testdata.service;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import uk.gov.companieshouse.api.testdata.model.rest.enums.CompanyType;

public final class CompanySubTypeValidator {

    public static final String PRIVATE_FUND_LIMITED_PARTNERSHIP =
            "private-fund-limited-partnership";
    public static final String COMMUNITY_INTEREST_COMPANY = "community-interest-company";
    public static final String LIMITED_PARTNERSHIP = "lp";
    public static final String SCOTTISH_LIMITED_PARTNERSHIP = "slp";
    public static final String PRIVATE_FUND_LIMITED_PARTNERSHIP_SHORT = "pflp";
    public static final String SCOTTISH_PRIVATE_FUND_LIMITED_PARTNERSHIP = "spflp";
    private static final String INVALID_REQUEST = "invalid request";

    private CompanySubTypeValidator() {
    }

    public static void validate(String subType, CompanyType companyType) {
        if (PRIVATE_FUND_LIMITED_PARTNERSHIP.equals(subType)
                && !CompanyType.LIMITED_PARTNERSHIP.equals(companyType)) {
            throw new HttpMessageNotReadableException(INVALID_REQUEST, (HttpInputMessage) null);
        }
        if (COMMUNITY_INTEREST_COMPANY.equals(subType)
                && !allowsCommunityInterestCompanySubType(companyType)) {
            throw new HttpMessageNotReadableException(INVALID_REQUEST, (HttpInputMessage) null);
        }
        // Validate LP subtypes - only allowed for limited-partnership company type
        String[] lpSubTypes = {LIMITED_PARTNERSHIP, SCOTTISH_LIMITED_PARTNERSHIP, 
                PRIVATE_FUND_LIMITED_PARTNERSHIP_SHORT, SCOTTISH_PRIVATE_FUND_LIMITED_PARTNERSHIP};
        for (String lpSubType : lpSubTypes) {
            if (lpSubType.equals(subType) && !CompanyType.LIMITED_PARTNERSHIP.equals(companyType)) {
                throw new HttpMessageNotReadableException(INVALID_REQUEST, (HttpInputMessage) null);
            }
        }
    }

    private static boolean allowsCommunityInterestCompanySubType(CompanyType companyType) {
        return companyType == null || companyType.allowsCommunityInterestCompanySubType();
    }
}
