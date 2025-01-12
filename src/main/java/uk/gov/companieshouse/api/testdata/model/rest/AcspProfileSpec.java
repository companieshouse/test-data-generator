package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

public class AcspProfileSpec {
    @JsonIgnore
    private String acspNumber;

    @JsonProperty("company_status")
    @Pattern(regexp = "active|inactive|closed|dissolved|administration|open|insolvency-proceedings|liquidation|converted-closed|receivership|registered|removed|voluntary-arrangement", message = "Invalid company status")
    private String companyStatus;

    @JsonProperty("type")
    @Pattern(regexp = "assurance-company|charitable-incorporated-organisation|company_delta|converted-or-closed|eeig|eeig-establishment|european-public-limited-liability-company-se|fake-type|further-education-or-sixth-form-college-corporation|icvc-securities|icvc-umbrella|icvc-warrant|industrial-and-provident-society|invalid|investment-company-with-variable-capital|limited-partnership|llp|ltd|northern-ireland|northern-ireland-other|old-public-company|other|oversea-company|plc|private-limited-guarant-nsc|private-limited-guarant-nsc-limited-exemption|private-limited-shares-section-30-exemption|private-unlimited|private-unlimited-nsc|protected-cell-company|registered-overseas-entity|registered-society-non-jurisdictional|royal-charter|scottish-charitable-incorporated-organisation|scottish-partnership|uk-establishment|ukeig|united-kingdom-societas|unregistered-company", message = "Invalid company type")
    private String companyType;

    public String getAcspNumber() {
        return acspNumber;
    }

    public void setAcspNumber(String acspNumber) {
        this.acspNumber = acspNumber;
    }

    public String getCompanyStatus() {
        return companyStatus;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyStatus(String companyStatus) {
        this.companyStatus = companyStatus;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }
}
