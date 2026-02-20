package uk.gov.companieshouse.api.testdata.model.dto;

import uk.gov.companieshouse.api.testdata.model.rest.enums.CompanyType;

public class CompanyDetailsParameters {
    private final CompanyType companyType;
    private final Boolean hasSuperSecurePscs;
    private final String companyStatus;
    private final String subType;
    private final String companyStatusDetail;
    private final Boolean registeredOfficeIsInDispute;

    public CompanyDetailsParameters(CompanyType companyType, Boolean hasSuperSecurePscs, String companyStatus, String subType, String companyStatusDetail, Boolean registeredOfficeIsInDispute) {
        this.companyType = companyType;
        this.hasSuperSecurePscs = hasSuperSecurePscs;
        this.companyStatus = companyStatus;
        this.subType = subType;
        this.companyStatusDetail = companyStatusDetail;
        this.registeredOfficeIsInDispute = registeredOfficeIsInDispute;
    }

    public CompanyType getCompanyType() {
        return companyType;
    }

    public Boolean getHasSuperSecurePscs() {
        return hasSuperSecurePscs;
    }

    public String getCompanyStatus() {
        return companyStatus;
    }

    public String getSubType() {
        return subType;
    }

    public String getCompanyStatusDetail() {
        return companyStatusDetail;
    }

    public Boolean getRegisteredOfficeIsInDispute() {
        return registeredOfficeIsInDispute;
    }
}