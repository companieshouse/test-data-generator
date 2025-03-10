package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OfficerRoles {
    CIC_MANAGER("cic-manager"),
    CORPORATE_DIRECTOR("corporate-director"),
    CORPORATE_LLP_DESIGNATED_MEMBER("corporate-llp-designated-member"),
    CORPORATE_LLP_MEMBER("corporate-llp-member"),
    CORPORATE_MANAGER_OF_AN_EEIG("corporate-manager-of-an-eeig"),
    CORPORATE_MANAGING_OFFICER("corporate-managing-officer"),
    CORPORATE_MEMBER_OF_A_MANAGEMENT_ORGAN("corporate-member-of-a-management-organ"),
    CORPORATE_MEMBER_OF_A_SUPERVISORY_ORGAN("corporate-member-of-a-supervisory-organ"),
    CORPORATE_MEMBER_OF_AN_ADMINISTRATIVE_ORGAN("corporate-member-of-an-administrative-organ"),
    CORPORATE_NOMINEE_DIRECTOR("corporate-nominee-director"),
    CORPORATE_NOMINEE_SECRETARY("corporate-nominee-secretary"),
    CORPORATE_SECRETARY("corporate-secretary"),
    DIRECTOR("director"),
    GENERAL_PARTNER_IN_A_LIMITED_PARTNERSHIP("general-partner-in-a-limited-partnership"),
    JUDICIAL_FACTOR("judicial-factor"),
    LIMITED_PARTNER_IN_A_LIMITED_PARTNERSHIP("limited-partner-in-a-limited-partnership"),
    LLP_DESIGNATED_MEMBER("llp-designated-member"),
    LLP_MEMBER("llp-member"),
    MANAGER_OF_AN_EEIG("manager-of-an-eeig"),
    MANAGING_OFFICER("managing-officer"),
    MEMBER_OF_A_MANAGEMENT_ORGAN("member-of-a-management-organ"),
    MEMBER_OF_A_SUPERVISORY_ORGAN("member-of-a-supervisory-organ"),
    MEMBER_OF_AN_ADMINISTRATIVE_ORGAN("member-of-an-administrative-organ"),
    NOMINEE_DIRECTOR("nominee-director"),
    NOMINEE_SECRETARY("nominee-secretary"),
    PERSON_AUTHORISED_TO_ACCEPT("person-authorised-to-accept"),
    PERSON_AUTHORISED_TO_REPRESENT("person-authorised-to-represent"),
    PERSON_AUTHORISED_TO_REPRESENT_AND_ACCEPT("person-authorised-to-represent-and-accept"),
    RECEIVER_AND_MANAGER("receiver-and-manager"),
    SECRETARY("secretary");

    private final String value;

    OfficerRoles(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
