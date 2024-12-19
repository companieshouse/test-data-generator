package uk.gov.companieshouse.api.testdata.model.rest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;

/**
 * Requirements a new acsp profile must meet
 */
public class AcspSpec {

    @JsonProperty
    @NotNull(message = "invalid jurisdiction")
    private Jurisdiction jurisdiction;

    @JsonIgnore
    private long acspNumber;

    @JsonProperty("status")
    @Pattern(regexp = "active|inactive|closed|dissolved|administration|open|insolvency-proceedings|liquidation|converted-closed|receivership|registered|removed|voluntary-arrangement", message = "Invalid company status")
    private String companyStatus;

    @JsonProperty("type")
    @Pattern(regexp = "assurance-company|charitable-incorporated-organisation|company_delta|converted-or-closed|eeig|eeig-establishment|european-public-limited-liability-company-se|fake-type|further-education-or-sixth-form-college-corporation|icvc-securities|icvc-umbrella|icvc-warrant|industrial-and-provident-society|invalid|investment-company-with-variable-capital|limited-partnership|llp|ltd|northern-ireland|northern-ireland-other|old-public-company|other|oversea-company|plc|private-limited-guarant-nsc|private-limited-guarant-nsc-limited-exemption|private-limited-shares-section-30-exemption|private-unlimited|private-unlimited-nsc|protected-cell-company|registered-overseas-entity|registered-society-non-jurisdictional|royal-charter|scottish-charitable-incorporated-organisation|scottish-partnership|uk-establishment|ukeig|united-kingdom-societas|unregistered-company", message = "Invalid company type")
    private String companyType;

    public AcspSpec() {
        jurisdiction = Jurisdiction.ENGLAND_WALES;
    }

    public Jurisdiction getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(Jurisdiction jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public long getAcspNumber() {
        return acspNumber;
    }

    public void setAcspNumber(long acspNumber) {
        this.acspNumber = acspNumber;
    }

    public String getStatus() {
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
