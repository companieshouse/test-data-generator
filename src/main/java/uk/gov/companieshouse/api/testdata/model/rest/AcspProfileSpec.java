package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

public class AcspProfileSpec {
    @JsonIgnore
    private String acspNumber;

    @JsonProperty("status")
    @Pattern(regexp = "active|suspended|ceased")
    private String status;

    @JsonProperty("type")
    @Pattern(regexp = "assurance-company|charitable-incorporated-organisation|company_delta|converted-or-closed|eeig|eeig-establishment|european-public-limited-liability-company-se|fake-type|further-education-or-sixth-form-college-corporation|icvc-securities|icvc-umbrella|icvc-warrant|industrial-and-provident-society|invalid|investment-company-with-variable-capital|limited-partnership|llp|ltd|northern-ireland|northern-ireland-other|old-public-company|other|oversea-company|plc|private-limited-guarant-nsc|private-limited-guarant-nsc-limited-exemption|private-limited-shares-section-30-exemption|private-unlimited|private-unlimited-nsc|protected-cell-company|registered-overseas-entity|registered-society-non-jurisdictional|royal-charter|scottish-charitable-incorporated-organisation|scottish-partnership|uk-establishment|ukeig|united-kingdom-societas|unregistered-company", message = "Invalid company type")
    private String type;

    public String getAcspNumber() {
        return acspNumber;
    }

    public void setAcspNumber(String acspNumber) {
        this.acspNumber = acspNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String companyStatus) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String companyType) {
        this.type = type;
    }
}
