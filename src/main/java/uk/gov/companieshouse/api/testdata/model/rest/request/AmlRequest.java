package uk.gov.companieshouse.api.testdata.model.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

public class AmlRequest {
    @JsonProperty("supervisory_body")
    @Pattern(regexp = "association-of-chartered-certified-accountants-acca|association-of-accounting-technicians-aat|association-of-international-accountants-aia|association-of-taxation-technicians-att|chartered-institute-of-legal-executives-cilex|chartered-institute-of-management-accountants-cima|chartered-institute-of-taxation-ciot|council-for-licensed-conveyors-clc|department-for-the-economy-northern-ireland|faculty-of-advocates|faculty-office-of-the-archbishop-of-canterbury|financial-conduct-authority-fca|gambling-commission|general-council-of-the-bar|general-council-of-the-bar-of-northern-ireland|institute-of-accountants-bookkeepers-iab|insolvency-practitioners-association-ipa|institute-of-certified-bookkeepers-icb|institute-of-chartered-accountants-in-england-and-wales-icaew|institute-of-chartered-accountants-in-ireland-icai|institute-of-chartered-accountants-of-scotland-icas|institute-of-financial-accountants-ifa|law-society-of-northern-ireland|law-society-of-scotland|law-society-ew-solicitors-regulation-authority-sra|hm-revenue-customs-hmrc", message = "Invalid supervisory body")
    private String supervisoryBody;

    @JsonProperty("membership_details")
    private String membershipDetails;

    public String getSupervisoryBody() {
        return supervisoryBody;
    }

    public void setSupervisoryBody(String supervisoryBody) {
        this.supervisoryBody = supervisoryBody;
    }

    public String getMembershipDetails() {
        return membershipDetails;
    }

    public void setMembershipDetails(String membershipDetails) {
        this.membershipDetails = membershipDetails;
    }
}