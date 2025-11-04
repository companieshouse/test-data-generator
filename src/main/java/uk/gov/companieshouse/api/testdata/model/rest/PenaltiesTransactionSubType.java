package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonValue;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

public enum PenaltiesTransactionSubType {
    C1("C1", "1", List.of("E1", "S1", "N1"), List.of("CS01")),
    C2("C2", "1", List.of(), List.of()),
    C3("C3", "1", List.of(), List.of()),
    C4("C4", "1", List.of(), List.of()),
    C5("C5", "1", List.of(), List.of()),
    C6("C6", "1", List.of(), List.of()),
    C7("C7", "1", List.of(), List.of()),
    C8("C8", "1", List.of(), List.of()),
    EA("EA", "1", List.of(), List.of()),
    EB("EB", "1", List.of(), List.of()),
    EC("EC", "1", List.of(), List.of()),
    ED("ED", "1", List.of(), List.of()),
    EE("EE", "1", List.of(), List.of()),
    EF("EF", "1", List.of(), List.of()),
    EG("EG", "1", List.of(), List.of()),
    EH("EH", "1", List.of(), List.of()),
    EI("EI", "1", List.of(), List.of()),
    EJ("EJ", "1", List.of(), List.of()),
    EK("EK", "1", List.of(), List.of()),
    EL("EL", "1", List.of(), List.of()),
    EM("EM", "1", List.of(), List.of()),
    EN("EN", "1", List.of(), List.of()),
    EO("EO", "1", List.of(), List.of()),
    EP("EP", "1", List.of(), List.of()),
    EQ("EQ", "1", List.of(), List.of()),
    ER("ER", "1", List.of(), List.of()),
    ES("ES", "1", List.of(), List.of()),
    ET("ET", "1", List.of(), List.of()),
    EU("EU", "1", List.of(), List.of()),
    EV("EV", "1", List.of(), List.of()),
    EW("EW", "1", List.of(), List.of()),
    EX("EX", "1", List.of(), List.of()),
    HU("HU", "1", List.of(), List.of()),
    HV("HV", "1", List.of(), List.of()),
    HW("HW", "1", List.of(), List.of()),
    HX("HX", "1", List.of(), List.of()),
    NA("NA", "1", List.of(), List.of()),
    NB("NB", "1", List.of(), List.of()),
    NC("NC", "1", List.of(), List.of()),
    ND("ND", "1", List.of(), List.of()),
    NE("NE", "1", List.of(), List.of()),
    NF("NF", "1", List.of(), List.of()),
    NG("NG", "1", List.of(), List.of()),
    NH("NH", "1", List.of(), List.of()),
    NI("NI", "1", List.of(), List.of()),
    NJ("NJ", "1", List.of(), List.of()),
    NK("NK", "1", List.of(), List.of()),
    NL("NL", "1", List.of(), List.of()),
    NM("NM", "1", List.of(), List.of()),
    NN("NN", "1", List.of(), List.of()),
    NO("NO", "1", List.of(), List.of()),
    NP("NP", "1", List.of(), List.of()),
    NQ("NQ", "1", List.of(), List.of()),
    NR("NR", "1", List.of(), List.of()),
    NS("NS", "1", List.of(), List.of()),
    NT("NT", "1", List.of(), List.of()),
    NU("NU", "1", List.of(), List.of()),
    NV("NV", "1", List.of(), List.of()),
    NW("NW", "1", List.of(), List.of()),
    NX("NX", "1", List.of(), List.of()),
    SA("SA", "1", List.of(), List.of()),
    SB("SB", "1", List.of(), List.of()),
    SC("SC", "1", List.of(), List.of()),
    SD("SD", "1", List.of(), List.of()),
    SE("SE", "1", List.of(), List.of()),
    SF("SF", "1", List.of(), List.of()),
    SG("SG", "1", List.of(), List.of()),
    SH("SH", "1", List.of(), List.of()),
    SI("SI", "1", List.of(), List.of()),
    SJ("SJ", "1", List.of(), List.of()),
    SK("SK", "1", List.of(), List.of()),
    SL("SL", "1", List.of(), List.of()),
    SN("SN", "1", List.of(), List.of()),
    SM("SM", "1", List.of(), List.of()),
    SO("SO", "1", List.of(), List.of()),
    SP("SP", "1", List.of(), List.of()),
    SQ("SQ", "1", List.of(), List.of()),
    SR("SR", "1", List.of(), List.of()),
    SS("SS", "1", List.of(), List.of()),
    ST("ST", "1", List.of(), List.of()),
    SU("SU", "1", List.of(), List.of()),
    SV("SV", "1", List.of(), List.of()),
    SW("SW", "1", List.of(), List.of()),
    SX("SX", "1", List.of(), List.of()),
    SY("SY", "1", List.of(), List.of()),
    S1("S1", "1", List.of("E1", "S1", "N1"), List.of("CS01")),
    S3("S3", "1", List.of("E1", "S1", "N1"), List.of("CS01")),
    A2("A2", "1", List.of("FU"), List.of("PENU"));

    private final String value;
    private final String transactionType;
    private final List<String> ledgerCodes;
    private final List<String> typeDescriptions;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    PenaltiesTransactionSubType(
            String value, String transactionType,
            List<String> ledgerCodes, List<String> typeDescriptions) {
        this.value = value;
        this.transactionType = transactionType;
        this.ledgerCodes = ledgerCodes;
        this.typeDescriptions = typeDescriptions;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getRandomLedgerCode() {
        if (ledgerCodes == null || ledgerCodes.isEmpty()) return "";
        return ledgerCodes.get(SECURE_RANDOM.nextInt(ledgerCodes.size()));
    }

    public String getRandomTypeDescription() {
        if (typeDescriptions == null || typeDescriptions.isEmpty()) return "";
        return typeDescriptions.get(SECURE_RANDOM.nextInt(typeDescriptions.size()));
    }

    public static Optional<PenaltiesTransactionSubType>
    fromCompanyAndSubType(String transactionSubType) {
        if (transactionSubType == null) return Optional.empty();
        for (PenaltiesTransactionSubType subType : PenaltiesTransactionSubType.values()) {
            if (subType.getValue().equals(transactionSubType)) {
                return Optional.of(subType);
            }
        }
        return Optional.empty();
    }
}