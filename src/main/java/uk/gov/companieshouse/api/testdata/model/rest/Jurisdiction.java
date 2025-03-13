package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public enum Jurisdiction {

    ENGLAND_WALES("england-wales", ""),
    SCOTLAND("scotland", "SC"),
    NI("northern-ireland", "NI"),
    UNITED_KINGDOM("united-kingdom", "OE"),
    WALES("wales", ""),
    ENGLAND("england", ""),
    EUROPEAN_UNION("european-union", ""),
    NON_EU("non-eu", "");

    @JsonValue
    private final String jurisdictionString;
    public final String companyNumberPrefix;

    private static final Map<String, Map<Jurisdiction, String>> COMPANY_PREFIX_MAP = new HashMap<>();

    Jurisdiction(String jurisdictionString, String companyNumberPrefix) {
        this.jurisdictionString = jurisdictionString;
        this.companyNumberPrefix = companyNumberPrefix;
    }

    @Override
    public String toString() {
        return jurisdictionString;
    }

    static {
        registerCompanyPrefixes();
    }

    public String getCompanyNumberPrefix(CompanySpec spec) {
        if (spec == null) {
            return "";
        }
        String companyType = spec.getCompanyType() != null ? spec.getCompanyType().getValue() : "ltd";
        var jurisdiction = spec.getJurisdiction() != null ? spec.getJurisdiction() : ENGLAND_WALES;
        if ("registered-society-non-jurisdictional".equals(companyType)) {
            return "RS";
        }
        if ("royal-charter".equals(companyType)) {
            return "RC";
        }
        if ("uk-establishment".equals(companyType)) {
            return "BR";
        }
        return COMPANY_PREFIX_MAP.getOrDefault(companyType, new EnumMap<>(Jurisdiction.class))
                .getOrDefault(jurisdiction, this.companyNumberPrefix);
    }

    private static void registerCompanyPrefixes() {
        registerPrefixes("assurance-company", Map.of(
                ENGLAND_WALES, "AC",
                SCOTLAND, "SA"
        ));
        registerPrefixes("charitable-incorporated-organisation", Map.of(
                ENGLAND_WALES, "CE"
        ));
        registerPrefixes("eeig", Map.of(
                ENGLAND_WALES, "GE"
        ));
        registerPrefixes("eeig-establishment", Map.of(
                ENGLAND_WALES, "GE"
        ));
        registerPrefixes("ukeig", Map.of(
                ENGLAND_WALES, "GE"
        ));
        registerPrefixes("european-public-limited-liability-company-se", Map.of(
                UNITED_KINGDOM, "SE"
        ));
        registerPrefixes("further-education-or-sixth-form-college-corporation", Map.of(
                ENGLAND_WALES, "FE"
        ));
        registerPrefixes("icvc-securities", Map.of(
                ENGLAND_WALES, "IC",
                SCOTLAND, "SI"
        ));
        registerPrefixes("icvc-umbrella", Map.of(
                ENGLAND_WALES, "IC",
                SCOTLAND, "SI"
        ));
        registerPrefixes("icvc-warrant", Map.of(
                ENGLAND_WALES, "IC",
                SCOTLAND, "SI"
        ));
        registerPrefixes("investment-company-with-variable-capital", Map.of(
                ENGLAND_WALES, "IC",
                SCOTLAND, "SI"
        ));
        registerPrefixes("industrial-and-provident-society", Map.of(
                ENGLAND_WALES, "IP",
                SCOTLAND, "SP",
                NI, "NP"
        ));
        registerPrefixes("limited-partnership", Map.of(
                ENGLAND_WALES, "LP",
                SCOTLAND, "SL",
                NI, "NL"
        ));
        registerPrefixes("llp", Map.of(
                ENGLAND_WALES, "OC",
                SCOTLAND, "SO",
                NI, "NC"
        ));
        registerPrefixes("northern-ireland", Map.of(
                NI, "NI"
        ));
        registerPrefixes("northern-ireland-other", Map.of(
                NI, "OC"
        ));
        registerPrefixes("oversea-company", Map.of(
                UNITED_KINGDOM, "FC"
        ));
        registerPrefixes("protected-cell-company", Map.of(
                ENGLAND_WALES, "PC"
        ));
        registerPrefixes("registered-overseas-entity", Map.of(
                UNITED_KINGDOM, "OE"
        ));
        registerPrefixes("scottish-charitable-incorporated-organisation", Map.of(
                SCOTLAND, "CS"
        ));
        registerPrefixes("scottish-partnership", Map.of(
                SCOTLAND, "SG"
        ));
        registerPrefixes("unregistered-company", Map.of(
                ENGLAND_WALES, "ZC",
                SCOTLAND, "SZ"
        ));
        registerPrefixes("united-kingdom-societas", Map.of(
                UNITED_KINGDOM, "SE"
        ));
    }

    private static void registerPrefixes(String companyType, Map<Jurisdiction, String> prefixes) {
        COMPANY_PREFIX_MAP.computeIfAbsent(companyType, k -> new EnumMap<>(Jurisdiction.class))
                .putAll(prefixes);
    }
}