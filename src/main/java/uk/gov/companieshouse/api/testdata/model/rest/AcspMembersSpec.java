package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AcspMembersSpec {
    @JsonProperty
    private String acspNumber;

    @JsonProperty
    private String acspMemberId;

    @JsonProperty
    @NotNull(message = "User not present")
    private String userId;

    @JsonProperty("status")
    @Pattern(regexp = "active|suspended|ceased")
    private String status;

    @JsonProperty("type")
    @Pattern(regexp = "assurance-company|charitable-incorporated-organisation|company_delta|converted-or-closed|eeig|eeig-establishment|european-public-limited-liability-company-se|fake-type|further-education-or-sixth-form-college-corporation|icvc-securities|icvc-umbrella|icvc-warrant|industrial-and-provident-society|invalid|investment-company-with-variable-capital|limited-partnership|llp|ltd|northern-ireland|northern-ireland-other|old-public-company|other|oversea-company|plc|private-limited-guarant-nsc|private-limited-guarant-nsc-limited-exemption|private-limited-shares-section-30-exemption|private-unlimited|private-unlimited-nsc|protected-cell-company|registered-overseas-entity|registered-society-non-jurisdictional|royal-charter|scottish-charitable-incorporated-organisation|scottish-partnership|uk-establishment|ukeig|united-kingdom-societas|unregistered-company", message = "Invalid company type")
    private String type;

    @JsonProperty("userRole")
    @Pattern(regexp = "owner|admin|standard")
    private String userRole;

    public String getAcspNumber() {
        return acspNumber;
    }

    public void setAcspNumber(String acspNumber) {
        this.acspNumber = acspNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAcspMemberId() {
        return acspMemberId;
    }

    public void setAcspMemberId(String acspMemberId) {
        this.acspMemberId = acspMemberId;
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

    public void setType(String type) {
        this.type = type;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String role) {
        this.type = userRole;
    }
}
