// CompanyDetailsParameters.java
package uk.gov.companieshouse.api.testdata.model.dto;

import uk.gov.companieshouse.api.testdata.model.rest.CompanyType;

public class CompanyDetailsParameters {
    private CompanyType companyType;
    private Boolean hasSuperSecurePscs;
    private String companyStatus;
    private String subType;
    private String companyStatusDetail;

    public CompanyDetailsParameters(CompanyType companyType, Boolean hasSuperSecurePscs, String companyStatus, String subType, String companyStatusDetail) {
        this.companyType = companyType;
        this.hasSuperSecurePscs = hasSuperSecurePscs;
        this.companyStatus = companyStatus;
        this.subType = subType;
        this.companyStatusDetail = companyStatusDetail;
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
}