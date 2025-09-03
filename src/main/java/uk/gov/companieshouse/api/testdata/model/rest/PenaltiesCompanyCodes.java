package uk.gov.companieshouse.api.testdata.model.rest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public enum PenaltiesCompanyCodes {

    LFP("A", "1", Arrays.asList("EW", "SC", "NI"),
            Arrays.asList("EOCFP", "EOJSD"), null,
            Arrays.asList("C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "EA", "EB", "EC", "ED",
                    "EE", "EF", "EG", "EH", "EI", "EJ", "EK", "EL", "EM", "EN", "EO", "EP",
                    "EQ", "ER", "ES", "ET", "EU", "EV", "EW", "EX", "HU", "HV", "HW", "HX",
                    "NA", "NB", "NC", "ND", "NE", "NF", "NG", "NH", "NI", "NJ", "NK", "NL",
                    "NM", "NN", "NO", "NP", "NQ", "NR", "NS", "NT", "NU", "NV", "NW", "NX",
                    "SA", "SB", "SC", "SD", "SE", "SF", "SG", "SH", "SI", "SJ", "SK", "SL",
                    "SN", "SM", "SO", "SP", "SQ", "SR", "SS", "ST", "SU", "SV", "SW", "SX", "SY")),

    CS_S1("P", "1", Arrays.asList("E1", "S1", "N1"),
            Collections.singletonList("CS01"), "S1",
            Collections.singletonList("CS")),

    CS_A2("U", "1", Collections.singletonList("FU"),
            Collections.singletonList("PENU"), "A2",
            Collections.singletonList("CS"));

    private final String prefix;
    private final String transactionType;
    private final List<String> ledgerCodes;
    private final List<String> typeDescriptions;
    private final String transactionSubType;
    private final List<String> companyCodes;

    private static final Random RANDOM = new Random();

    PenaltiesCompanyCodes(String prefix, String transactionType, List<String> ledgerCodes,
                          List<String> typeDescriptions, String transactionSubType,
                          List<String> companyCodes) {
        this.prefix = prefix;
        this.transactionType = transactionType;
        this.ledgerCodes = ledgerCodes;
        this.typeDescriptions = typeDescriptions;
        this.transactionSubType = transactionSubType;
        this.companyCodes = companyCodes;
    }

    public static Optional<PenaltiesCompanyCodes> fromCompanyAndSubType(
            String companyCode, String transactionSubType) {
        for (PenaltiesCompanyCodes penaltyCode : values()) {
            if (penaltyCode.companyCodes.contains(companyCode)) {
                if (penaltyCode == LFP) {
                    return Optional.of(penaltyCode);
                }
                if (transactionSubType != null
                        && transactionSubType.equals(penaltyCode.transactionSubType)) {
                    return Optional.of(penaltyCode);
                }
            }
        }
        return Optional.empty();
    }

    public String getRandomLedgerCode() {
        return ledgerCodes.get(RANDOM.nextInt(ledgerCodes.size()));
    }

    public String getRandomTypeDescription() {
        return typeDescriptions.get(RANDOM.nextInt(typeDescriptions.size()));
    }

    public String getPrefix() {
        return prefix;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getTransactionSubType() {
        return transactionSubType;
    }

    public List<String> getCompanyCodes() {
        return companyCodes;
    }

    public static boolean isValidCompanyCode(String companyCode) {
        return Arrays.stream(values())
                .anyMatch(penaltyCode -> penaltyCode.companyCodes.contains(companyCode));
    }
}