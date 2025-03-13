package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CompanyType {
    ASSURANCE_COMPANY("assurance-company"),
    CHARITABLE_INCORPORATED_ORGANISATION("charitable-incorporated-organisation"),
    CONVERTED_OR_CLOSED("converted-or-closed"),
    EEIG("eeig"),
    EEIG_ESTABLISHMENT("eeig-establishment"),
    EUROPEAN_PUBLIC_LIMITED_LIABILITY_COMPANY_SE("european-public-limited-liability-company-se"),
    FURTHER_EDUCATION_OR_SIXTH_FORM_COLLEGE_CORPORATION("further-education-or-sixth-form-college-corporation"),
    ICVC_SECURITIES("icvc-securities"),
    ICVC_UMBRELLA("icvc-umbrella"),
    ICVC_WARRANT("icvc-warrant"),
    INDUSTRIAL_AND_PROVIDENT_SOCIETY("industrial-and-provident-society"),
    INVESTMENT_COMPANY_WITH_VARIABLE_CAPITAL("investment-company-with-variable-capital"),
    LIMITED_PARTNERSHIP("limited-partnership"),
    LLP("llp"),
    LTD("ltd"),
    NORTHERN_IRELAND("northern-ireland"),
    NORTHERN_IRELAND_OTHER("northern-ireland-other"),
    OLD_PUBLIC_COMPANY("old-public-company"),
    OTHER("other"),
    OVERSEA_COMPANY("oversea-company"),
    PLC("plc"),
    PRIVATE_LIMITED_GUARANT_NSC("private-limited-guarant-nsc"),
    PRIVATE_LIMITED_GUARANT_NSC_LIMITED_EXEMPTION("private-limited-guarant-nsc-limited-exemption"),
    PRIVATE_LIMITED_SHARES_SECTION_30_EXEMPTION("private-limited-shares-section-30-exemption"),
    PRIVATE_UNLIMITED("private-unlimited"),
    PRIVATE_UNLIMITED_NSC("private-unlimited-nsc"),
    PROTECTED_CELL_COMPANY("protected-cell-company"),
    REGISTERED_OVERSEAS_ENTITY("registered-overseas-entity"),
    REGISTERED_SOCIETY_NON_JURISDICTIONAL("registered-society-non-jurisdictional"),
    ROYAL_CHARTER("royal-charter"),
    SCOTTISH_CHARITABLE_INCORPORATED_ORGANISATION("scottish-charitable-incorporated-organisation"),
    SCOTTISH_PARTNERSHIP("scottish-partnership"),
    UK_ESTABLISHMENT("uk-establishment"),
    UKEIG("ukeig"),
    UNITED_KINGDOM_SOCIETAS("united-kingdom-societas"),
    UNREGISTERED_COMPANY("unregistered-company");

    private final String value;

    CompanyType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
