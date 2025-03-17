package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public enum Jurisdiction {

    ENGLAND_WALES("england-wales", ""),
    SCOTLAND("scotland", Prefix.SC.value),
    NI("northern-ireland", Prefix.NI.value),
    UNITED_KINGDOM("united-kingdom", Prefix.OE.value),
    WALES("wales", ""),
    ENGLAND("england", ""),
    EUROPEAN_UNION("european-union", ""),
    NON_EU("non-eu", "");

    @JsonValue
    private final String jurisdictionString;
    public final String companyNumberPrefix;

    private static final Map<String, Map<Jurisdiction, String>> COMPANY_PREFIX_MAP
            = new HashMap<>();

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
        if (CompanyType.REGISTERED_SOCIETY_NON_JURISDICTIONAL.getValue().equals(companyType)) {
            return Prefix.RS.value;
        }
        if (CompanyType.ROYAL_CHARTER.getValue().equals(companyType)) {
            return Prefix.RC.value;
        }
        if (CompanyType.UK_ESTABLISHMENT.getValue().equals(companyType)) {
            return Prefix.BR.value;
        }
        return COMPANY_PREFIX_MAP.getOrDefault(companyType, new EnumMap<>(Jurisdiction.class))
                .getOrDefault(jurisdiction, this.companyNumberPrefix);
    }

    private static void registerCompanyPrefixes() {
        registerPrefixes(CompanyType.ASSURANCE_COMPANY, Map.of(
                ENGLAND_WALES, Prefix.AC.value,
                SCOTLAND, Prefix.SA.value
        ));
        registerPrefixes(CompanyType.CHARITABLE_INCORPORATED_ORGANISATION, Map.of(
                ENGLAND_WALES, Prefix.CE.value
        ));
        registerPrefixes(CompanyType.EEIG, Map.of(
                ENGLAND_WALES, Prefix.GE.value
        ));
        registerPrefixes(CompanyType.EEIG_ESTABLISHMENT, Map.of(
                ENGLAND_WALES, Prefix.GE.value
        ));
        registerPrefixes(CompanyType.UKEIG, Map.of(
                ENGLAND_WALES, Prefix.GE.value
        ));
        registerPrefixes(CompanyType.EUROPEAN_PUBLIC_LIMITED_LIABILITY_COMPANY_SE, Map.of(
                UNITED_KINGDOM, Prefix.SE.value
        ));
        registerPrefixes(CompanyType.FURTHER_EDUCATION_OR_SIXTH_FORM_COLLEGE_CORPORATION, Map.of(
                ENGLAND_WALES, Prefix.FE.value
        ));
        registerPrefixes(CompanyType.ICVC_SECURITIES, Map.of(
                ENGLAND_WALES, Prefix.IC.value,
                SCOTLAND, Prefix.SI.value
        ));
        registerPrefixes(CompanyType.ICVC_UMBRELLA, Map.of(
                ENGLAND_WALES, Prefix.IC.value,
                SCOTLAND, Prefix.SI.value
        ));
        registerPrefixes(CompanyType.ICVC_WARRANT, Map.of(
                ENGLAND_WALES, Prefix.IC.value,
                SCOTLAND, Prefix.SI.value
        ));
        registerPrefixes(CompanyType.INVESTMENT_COMPANY_WITH_VARIABLE_CAPITAL, Map.of(
                ENGLAND_WALES, Prefix.IC.value,
                SCOTLAND, Prefix.SI.value
        ));
        registerPrefixes(CompanyType.INDUSTRIAL_AND_PROVIDENT_SOCIETY, Map.of(
                ENGLAND_WALES, Prefix.IP.value,
                SCOTLAND, Prefix.SP.value,
                NI, Prefix.NP.value
        ));
        registerPrefixes(CompanyType.LIMITED_PARTNERSHIP, Map.of(
                ENGLAND_WALES, Prefix.LP.value,
                SCOTLAND, Prefix.SL.value,
                NI, Prefix.NL.value
        ));
        registerPrefixes(CompanyType.LLP, Map.of(
                ENGLAND_WALES, Prefix.OC.value,
                SCOTLAND, Prefix.SO.value,
                NI, Prefix.NC.value
        ));
        registerPrefixes(CompanyType.NORTHERN_IRELAND, Map.of(
                NI, Prefix.NI.value
        ));
        registerPrefixes(CompanyType.NORTHERN_IRELAND_OTHER, Map.of(
                NI, Prefix.OC.value
        ));
        registerPrefixes(CompanyType.OVERSEA_COMPANY, Map.of(
                UNITED_KINGDOM, Prefix.FC.value
        ));
        registerPrefixes(CompanyType.PROTECTED_CELL_COMPANY, Map.of(
                ENGLAND_WALES, Prefix.PC.value
        ));
        registerPrefixes(CompanyType.REGISTERED_OVERSEAS_ENTITY, Map.of(
                UNITED_KINGDOM, Prefix.OE.value
        ));
        registerPrefixes(CompanyType.SCOTTISH_CHARITABLE_INCORPORATED_ORGANISATION, Map.of(
                SCOTLAND, Prefix.CS.value
        ));
        registerPrefixes(CompanyType.SCOTTISH_PARTNERSHIP, Map.of(
                SCOTLAND, Prefix.SG.value
        ));
        registerPrefixes(CompanyType.UNREGISTERED_COMPANY, Map.of(
                ENGLAND_WALES, Prefix.ZC.value,
                SCOTLAND, Prefix.SZ.value
        ));
        registerPrefixes(CompanyType.UNITED_KINGDOM_SOCIETAS, Map.of(
                UNITED_KINGDOM, Prefix.SE.value
        ));
    }

    private static void registerPrefixes(CompanyType companyType,
                                         Map<Jurisdiction, String> prefixes) {
        COMPANY_PREFIX_MAP.computeIfAbsent(
                companyType.getValue(), k -> new EnumMap<>(Jurisdiction.class)).putAll(prefixes);
    }

    private enum Prefix {
        AC("AC"), SA("SA"), CE("CE"), GE("GE"), SE("SE"), FE("FE"), IC("IC"), SI("SI"), IP("IP"),
        SP("SP"), NP("NP"), LP("LP"), SL("SL"), NL("NL"), OC("OC"), SO("SO"), NC("NC"), NI("NI"),
        FC("FC"), PC("PC"), OE("OE"), CS("CS"), SG("SG"), ZC("ZC"), SZ("SZ"), RS("RS"), RC("RC"),
        BR("BR"), SC("SC");

        public final String value;

        Prefix(String value) {
            this.value = value;
        }
    }
}