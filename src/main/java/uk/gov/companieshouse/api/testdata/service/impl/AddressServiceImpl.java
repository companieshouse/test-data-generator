package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.Locale;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.UsualResidentialAddress;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.service.AddressService;

@Service
public class AddressServiceImpl implements AddressService {

    private static final String UNITED_KINGDOM = "United Kingdom";
    private static final String NETHERLANDS = "Netherlands";
    private static final String PANAMA = "Panama";
    private static final String CANADA = "Canada";
    private static final String AUSTRALIA = "Australia";
    private static final String TEST_DATA_MARKER = "TEST DATA";
    private static final String UK_SENTINEL_POSTCODE = "ZZ1 1ZZ";
    private static final String NETHERLANDS_SENTINEL_POSTCODE = "0000 ZZ";
    private static final String PANAMA_SENTINEL_POSTCODE = "0000-0000";
    private static final String CANADA_SENTINEL_POSTCODE = "Z9Z 9Z9";
    private static final String AUSTRALIA_SENTINEL_POSTCODE = "0000";
    private static final Faker FAKER = new Faker(Locale.UK);
    private static final Faker UK_FAKER = new Faker(Locale.UK);
    private static final Faker NETHERLANDS_FAKER = new Faker(new Locale("nl", "NL"));
    private static final Faker PANAMA_FAKER = new Faker(new Locale("es"));
    private static final Faker CANADA_FAKER = new Faker(Locale.CANADA);
    private static final Faker AUSTRALIA_FAKER = new Faker(new Locale("en", "AU"));

    private static class LocalityCluster {
        final String locality;
        final String area;
        final String region;
        final String[] postcodeOutwards;

        LocalityCluster(String locality, String area, String region, String[] postcodeOutwards) {
            this.locality = locality;
            this.area = area;
            this.region = region;
            this.postcodeOutwards = postcodeOutwards;
        }
    }

    private enum NonEuPostcodeType {
        PANAMA,
        CANADA,
        AUSTRALIA
    }

    private static class NonEuProfile {
        final String country;
        final Faker faker;
        final LocalityCluster[] clusters;
        final NonEuPostcodeType postcodeType;

        NonEuProfile(String country, Faker faker, LocalityCluster[] clusters, NonEuPostcodeType postcodeType) {
            this.country = country;
            this.faker = faker;
            this.clusters = clusters;
            this.postcodeType = postcodeType;
        }
    }

    private static final LocalityCluster[] ENGLAND_CLUSTERS = {
            new LocalityCluster("LONDON", "CAMDEN", "GREATER LONDON", new String[]{"WC1", "NW1", "N1"}),
            new LocalityCluster("LONDON", "ISLINGTON", "GREATER LONDON", new String[]{"N1", "N7", "EC1"}),
            new LocalityCluster("LONDON", "CHELSEA", "GREATER LONDON", new String[]{"SW3", "SW10", "SW1"}),
            new LocalityCluster("MANCHESTER", "DIDSBURY", "GREATER MANCHESTER", new String[]{"M20", "M21", "M1"}),
            new LocalityCluster("MANCHESTER", "CHORLTON", "GREATER MANCHESTER", new String[]{"M16", "M21", "M32"}),
            new LocalityCluster("BIRMINGHAM", "EDGBASTON", "WEST MIDLANDS", new String[]{"B1", "B15", "B16"}),
            new LocalityCluster("BIRMINGHAM", "MOSELEY", "WEST MIDLANDS", new String[]{"B13", "B14", "B15"}),
            new LocalityCluster("BRISTOL", "CLIFTON", "SOUTH WEST ENGLAND", new String[]{"BS1", "BS3", "BS8"}),
            new LocalityCluster("BRISTOL", "REDLAND", "SOUTH WEST ENGLAND", new String[]{"BS6", "BS7", "BS8"}),
            new LocalityCluster("LEEDS", "HEADINGLEY", "WEST YORKSHIRE", new String[]{"LS1", "LS2", "LS6"}),
            new LocalityCluster("LEEDS", "MEANWOOD", "WEST YORKSHIRE", new String[]{"LS6", "LS7", "LS8"}),
            new LocalityCluster("LIVERPOOL", "TOXTETH", "MERSEYSIDE", new String[]{"L1", "L8", "L15"}),
            new LocalityCluster("LIVERPOOL", "WAVERTREE", "MERSEYSIDE", new String[]{"L15", "L16", "L18"}),
            new LocalityCluster("NEWCASTLE", "GOSFORTH", "TYNE AND WEAR", new String[]{"NE1", "NE2", "NE3"}),
            new LocalityCluster("NEWCASTLE", "JESMOND", "TYNE AND WEAR", new String[]{"NE2", "NE3", "NE7"}),
            new LocalityCluster("CAMBRIDGE", "NEWNHAM", "CAMBRIDGESHIRE", new String[]{"CB1", "CB2", "CB3"}),
            new LocalityCluster("CAMBRIDGE", "CHESTERTON", "CAMBRIDGESHIRE", new String[]{"CB4", "CB5", "CB1"})
    };

    private static final LocalityCluster[] WALES_CLUSTERS = {
            new LocalityCluster("CARDIFF", "CATHAYS", "SOUTH GLAMORGAN", new String[]{"CF10", "CF11", "CF24"}),
            new LocalityCluster("CARDIFF", "PONTCANNA", "SOUTH GLAMORGAN", new String[]{"CF5", "CF11", "CF14"}),
            new LocalityCluster("CARDIFF", "GRANGETOWN", "SOUTH GLAMORGAN", new String[]{"CF10", "CF11", "CF24"}),
            new LocalityCluster("SWANSEA", "UPLANDS", "WEST GLAMORGAN", new String[]{"SA1", "SA2", "SA3"}),
            new LocalityCluster("SWANSEA", "MUMBLES", "WEST GLAMORGAN", new String[]{"SA2", "SA3", "SA4"}),
            new LocalityCluster("NEWPORT", "STOW HILL", "MONMOUTHSHIRE", new String[]{"NP19", "NP20", "NP10"}),
            new LocalityCluster("NEWPORT", "ROGERSTONE", "MONMOUTHSHIRE", new String[]{"NP10", "NP18", "NP19"}),
            new LocalityCluster("WREXHAM", "TOWN CENTRE", "CLWYD", new String[]{"LL11", "LL12", "LL13"}),
            new LocalityCluster("WREXHAM", "ACTON", "CLWYD", new String[]{"LL11", "LL12", "LL13"})
    };

    private static final LocalityCluster[] SCOTLAND_CLUSTERS = {
            new LocalityCluster("EDINBURGH", "LEITH", "CITY OF EDINBURGH", new String[]{"EH1", "EH2", "EH6"}),
            new LocalityCluster("EDINBURGH", "MORNINGSIDE", "CITY OF EDINBURGH", new String[]{"EH10", "EH11", "EH16"}),
            new LocalityCluster("EDINBURGH", "STOCKBRIDGE", "CITY OF EDINBURGH", new String[]{"EH3", "EH4", "EH5"}),
            new LocalityCluster("GLASGOW", "PARTICK", "GLASGOW CITY", new String[]{"G1", "G11", "G12"}),
            new LocalityCluster("GLASGOW", "GOVAN", "GLASGOW CITY", new String[]{"G51", "G52", "G41"}),
            new LocalityCluster("GLASGOW", "MERCHANT CITY", "GLASGOW CITY", new String[]{"G1", "G2", "G4"}),
            new LocalityCluster("ABERDEEN", "WEST END", "ABERDEENSHIRE", new String[]{"AB10", "AB11", "AB15"}),
            new LocalityCluster("ABERDEEN", "ROSEMOUNT", "ABERDEENSHIRE", new String[]{"AB15", "AB16", "AB25"}),
            new LocalityCluster("DUNDEE", "BROUGHTY FERRY", "DUNDEE CITY", new String[]{"DD1", "DD2", "DD5"}),
            new LocalityCluster("DUNDEE", "WEST END", "DUNDEE CITY", new String[]{"DD1", "DD2", "DD3"})
    };

    private static final LocalityCluster[] NI_CLUSTERS = {
            new LocalityCluster("BELFAST", "BOTANIC", "COUNTY ANTRIM", new String[]{"BT1", "BT2", "BT9"}),
            new LocalityCluster("BELFAST", "ORMEAU", "COUNTY ANTRIM", new String[]{"BT6", "BT7", "BT8"}),
            new LocalityCluster("BELFAST", "CATHEDRAL QUARTER", "COUNTY ANTRIM", new String[]{"BT1", "BT2", "BT15"}),
            new LocalityCluster("BELFAST", "TITANIC QUARTER", "COUNTY ANTRIM", new String[]{"BT3", "BT4", "BT5"}),
            new LocalityCluster("DERRY", "BOGSIDE", "COUNTY LONDONDERRY", new String[]{"BT47", "BT48", "BT49"}),
            new LocalityCluster("LISBURN", "TOWN CENTRE", "LISBURN AND CASTLEREAGH CITY", new String[]{"BT27", "BT28", "BT29"}),
            new LocalityCluster("LISBURN", "LAMBEG", "LISBURN AND CASTLEREAGH CITY", new String[]{"BT27", "BT28", "BT39"}),
            new LocalityCluster("NEWRY", "HILL STREET", "NEWRY, MOURNE AND DOWN", new String[]{"BT34", "BT35", "BT60"}),
            new LocalityCluster("NEWRY", "WARRENPOINT", "NEWRY, MOURNE AND DOWN", new String[]{"BT34", "BT35", "BT62"})
    };

    private static final LocalityCluster[] NETHERLANDS_CLUSTERS = {
            new LocalityCluster("AMSTERDAM", "CENTRUM", "NOORD-HOLLAND", new String[]{"1012", "1017", "1054"}),
            new LocalityCluster("AMSTERDAM", "DE PIJP", "NOORD-HOLLAND", new String[]{"1072", "1073", "1074"}),
            new LocalityCluster("ROTTERDAM", "KRALINGEN", "ZUID-HOLLAND", new String[]{"3011", "3062", "3072"}),
            new LocalityCluster("ROTTERDAM", "DELFSHAVEN", "ZUID-HOLLAND", new String[]{"3025", "3026", "3027"}),
            new LocalityCluster("UTRECHT", "BINNENSTAD", "UTRECHT PROVINCE", new String[]{"3511", "3512", "3581"}),
            new LocalityCluster("UTRECHT", "LOMBOK", "UTRECHT PROVINCE", new String[]{"3531", "3532", "3521"}),
            new LocalityCluster("EINDHOVEN", "STRIJP", "NOORD-BRABANT", new String[]{"5611", "5612", "5652"}),
            new LocalityCluster("EINDHOVEN", "GESTEL", "NOORD-BRABANT", new String[]{"5614", "5623", "5654"}),
            new LocalityCluster("GRONINGEN", "BINNENSTAD", "GRONINGEN PROVINCE", new String[]{"9711", "9712", "9724"}),
            new LocalityCluster("GRONINGEN", "SELWERD", "GRONINGEN PROVINCE", new String[]{"9716", "9717", "9741"}),
            new LocalityCluster("MAASTRICHT", "WYCK", "LIMBURG", new String[]{"6211", "6212", "6221"}),
            new LocalityCluster("MAASTRICHT", "JEKERKWARTIER", "LIMBURG", new String[]{"6211", "6214", "6216"})
    };

    private static final LocalityCluster[] PANAMA_CLUSTERS = {
            new LocalityCluster("PANAMA CITY", "BELLA VISTA", "PANAMA", new String[]{"0801", "0802", "0804"}),
            new LocalityCluster("PANAMA CITY", "SAN FRANCISCO", "PANAMA", new String[]{"0819", "0820", "0823"}),
            new LocalityCluster("PANAMA CITY", "OBARRIO", "PANAMA", new String[]{"0832", "0833", "0834"}),
            new LocalityCluster("PANAMA CITY", "EL CANGREJO", "PANAMA", new String[]{"0831", "0832", "0835"}),
            new LocalityCluster("COLON", "BARRIO NORTE", "COLON PROVINCE", new String[]{"0301", "0302", "0311"}),
            new LocalityCluster("COLON", "CRISTOBAL", "COLON PROVINCE", new String[]{"0301", "0303", "0305"}),
            new LocalityCluster("DAVID", "CENTRO", "CHIRIQUI", new String[]{"0426", "0427", "0430"}),
            new LocalityCluster("DAVID", "SAN MATEO", "CHIRIQUI", new String[]{"0427", "0428", "0431"}),
            new LocalityCluster("SANTIAGO", "BARRIO SUR", "VERAGUAS", new String[]{"0901", "0902", "0903"}),
            new LocalityCluster("SANTIAGO", "BARRIO CENTRAL", "VERAGUAS", new String[]{"0902", "0903", "0904"}),
            new LocalityCluster("CHITRE", "CENTRO", "HERRERA", new String[]{"0601", "0602", "0603"}),
            new LocalityCluster("CHITRE", "LA ARENA", "HERRERA", new String[]{"0602", "0603", "0604"})
    };

    private static final LocalityCluster[] CANADA_CLUSTERS = {
            new LocalityCluster("TORONTO", "DOWNTOWN", "ONTARIO", new String[]{"M5V", "M4B", "M6J"}),
            new LocalityCluster("TORONTO", "NORTH YORK", "ONTARIO", new String[]{"M2N", "M3A", "M3H"}),
            new LocalityCluster("VANCOUVER", "KITSILANO", "BRITISH COLUMBIA", new String[]{"V6K", "V6J", "V5N"}),
            new LocalityCluster("VANCOUVER", "YALETOWN", "BRITISH COLUMBIA", new String[]{"V6B", "V6E", "V6Z"}),
            new LocalityCluster("MONTREAL", "PLATEAU", "QUEBEC", new String[]{"H2X", "H2W", "H2T"}),
            new LocalityCluster("MONTREAL", "VERDUN", "QUEBEC", new String[]{"H3E", "H4G", "H4H"}),
            new LocalityCluster("CALGARY", "BELTLINE", "ALBERTA", new String[]{"T2R", "T2P", "T2G"}),
            new LocalityCluster("OTTAWA", "CENTRETOWN", "ONTARIO", new String[]{"K1R", "K1S", "K2P"})
    };

    private static final LocalityCluster[] AUSTRALIA_CLUSTERS = {
            new LocalityCluster("SYDNEY", "SURRY HILLS", "NEW SOUTH WALES", new String[]{"2000", "2010", "2011"}),
            new LocalityCluster("SYDNEY", "NEWTOWN", "NEW SOUTH WALES", new String[]{"2042", "2043", "2050"}),
            new LocalityCluster("MELBOURNE", "SOUTH YARRA", "VICTORIA", new String[]{"3004", "3141", "3142"}),
            new LocalityCluster("MELBOURNE", "FITZROY", "VICTORIA", new String[]{"3065", "3066", "3002"}),
            new LocalityCluster("BRISBANE", "FORTITUDE VALLEY", "QUEENSLAND", new String[]{"4006", "4005", "4000"}),
            new LocalityCluster("PERTH", "SUBIACO", "WESTERN AUSTRALIA", new String[]{"6008", "6009", "6010"}),
            new LocalityCluster("ADELAIDE", "NORTH ADELAIDE", "SOUTH AUSTRALIA", new String[]{"5006", "5000", "5001"}),
            new LocalityCluster("CANBERRA", "BRADDON", "AUSTRALIAN CAPITAL TERRITORY", new String[]{"2612", "2601", "2602"})
    };

    private static final NonEuProfile[] NON_EU_PROFILES = {
            new NonEuProfile(PANAMA, PANAMA_FAKER, PANAMA_CLUSTERS, NonEuPostcodeType.PANAMA),
            new NonEuProfile(CANADA, CANADA_FAKER, CANADA_CLUSTERS, NonEuPostcodeType.CANADA),
            new NonEuProfile(AUSTRALIA, AUSTRALIA_FAKER, AUSTRALIA_CLUSTERS, NonEuPostcodeType.AUSTRALIA)
    };

    @Override
    public Address getAddress(JurisdictionType jurisdiction) {
        UsualResidentialAddress addr = getUsualResidentialAddress(jurisdiction);
        Address address = new Address();
        address.setPremise(addr.getPremises());
        address.setAddressLine1(firstNonBlank(addr.getAddressLine2(), addr.getAddressLine1()));
        address.setAddressLine2(firstNonBlank(addr.getAddressLine1(), addr.getAddressLine2(), addr.getLocality()));
        address.setCountry(addr.getCountry());
        address.setLocality(addr.getLocality());
        address.setPostalCode(addr.getPostalCode());
        address.setRegion(firstNonBlank(addr.getRegion(), addr.getLocality(), UNITED_KINGDOM));
        return address;
    }

    @Override
    public Address getOverseasAddress() {
        return getAddress(JurisdictionType.NON_EU);
    }

    @Override
    public String getCountryOfResidence(JurisdictionType jurisdiction) {
        return switch (jurisdiction) {
            case ENGLAND_WALES, ENGLAND -> "England";
            case WALES -> "Wales";
            case SCOTLAND -> "Scotland";
            case NI -> "Northern Ireland";
            case UNITED_KINGDOM -> "United Kingdom";
            case NON_EU -> "Panama";
            case EUROPEAN_UNION -> "Netherlands";
        };
    }

    @Override
    public UsualResidentialAddress getUsualResidentialAddress() {
        return getUsualResidentialAddress(JurisdictionType.ENGLAND_WALES);
    }

    @Override
    public UsualResidentialAddress getUsualResidentialAddress(JurisdictionType jurisdiction) {
        JurisdictionType effectiveJurisdiction =
                jurisdiction != null ? jurisdiction : JurisdictionType.ENGLAND_WALES;
        UsualResidentialAddress residentialAddress = new UsualResidentialAddress();
        residentialAddress.setPremises(String.valueOf(FAKER.number().numberBetween(1, 300)));

        if (isUkJurisdiction(effectiveJurisdiction)) {
            LocalityCluster cluster = getUkClusterForJurisdiction(effectiveJurisdiction);
            residentialAddress.setAddressLine1(cluster.area);
            residentialAddress.setAddressLine2(
                    UK_FAKER.address().streetName().toUpperCase(Locale.UK) + " " + TEST_DATA_MARKER);
            residentialAddress.setCountry(UNITED_KINGDOM);
            residentialAddress.setLocality(cluster.locality);
            residentialAddress.setPostalCode(generateUkPostcode());
            residentialAddress.setRegion(cluster.region);
            return normalizeResidentialAddress(residentialAddress);
        }

        if (effectiveJurisdiction == JurisdictionType.EUROPEAN_UNION) {
            LocalityCluster cluster = FAKER.options().option(NETHERLANDS_CLUSTERS);
            residentialAddress.setAddressLine1(NETHERLANDS_FAKER.address().streetName());
            residentialAddress.setAddressLine2(cluster.area + " " + TEST_DATA_MARKER);
            residentialAddress.setCountry(NETHERLANDS);
            residentialAddress.setLocality(cluster.locality);
            residentialAddress.setPostalCode(generateDutchPostcode());
            residentialAddress.setRegion(cluster.region);
            return normalizeResidentialAddress(residentialAddress);
        }

        NonEuProfile profile = FAKER.options().option(NON_EU_PROFILES);
        LocalityCluster cluster = FAKER.options().option(profile.clusters);
        residentialAddress.setAddressLine1(profile.faker.address().streetName());
        residentialAddress.setAddressLine2(cluster.area + " " + TEST_DATA_MARKER);
        residentialAddress.setCountry(profile.country);
        residentialAddress.setLocality(cluster.locality);
        residentialAddress.setPostalCode(generateNonEuPostcode(profile.postcodeType));
        residentialAddress.setRegion(cluster.region);

        return normalizeResidentialAddress(residentialAddress);
    }

    private LocalityCluster getUkClusterForJurisdiction(JurisdictionType jurisdiction) {
        return switch (jurisdiction) {
            case ENGLAND_WALES -> FAKER.options().option(
                    FAKER.options().option(ENGLAND_CLUSTERS),
                    FAKER.options().option(WALES_CLUSTERS));
            case ENGLAND -> FAKER.options().option(ENGLAND_CLUSTERS);
            case WALES -> FAKER.options().option(WALES_CLUSTERS);
            case SCOTLAND -> FAKER.options().option(SCOTLAND_CLUSTERS);
            case NI -> FAKER.options().option(NI_CLUSTERS);
            case UNITED_KINGDOM -> getUkClusterForJurisdiction(
                    FAKER.options().option(
                            JurisdictionType.ENGLAND,
                            JurisdictionType.WALES,
                            JurisdictionType.SCOTLAND,
                            JurisdictionType.NI));
            case EUROPEAN_UNION, NON_EU -> throw new IllegalStateException("Unexpected non-UK jurisdiction");
        };
    }

    private boolean isUkJurisdiction(JurisdictionType jurisdiction) {
        return switch (jurisdiction) {
            case ENGLAND_WALES, WALES, SCOTLAND, NI, ENGLAND, UNITED_KINGDOM -> true;
            case EUROPEAN_UNION, NON_EU -> false;
        };
    }

    private String generateUkPostcode() {
        return UK_SENTINEL_POSTCODE;
    }

    private String generateDutchPostcode() {
        return NETHERLANDS_SENTINEL_POSTCODE;
    }

    private String generatePanamaPostcode() {
        return PANAMA_SENTINEL_POSTCODE;
    }

    private String generateCanadaPostcode() {
        return CANADA_SENTINEL_POSTCODE;
    }

    private String generateAustraliaPostcode() {
        return AUSTRALIA_SENTINEL_POSTCODE;
    }

    private String generateNonEuPostcode(NonEuPostcodeType postcodeType) {
        return switch (postcodeType) {
            case PANAMA -> generatePanamaPostcode();
            case CANADA -> generateCanadaPostcode();
            case AUSTRALIA -> generateAustraliaPostcode();
        };
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private UsualResidentialAddress normalizeResidentialAddress(UsualResidentialAddress address) {
        String locality = firstNonBlank(address.getLocality(), "TEST LOCALITY");
        address.setAddressLine1(firstNonBlank(address.getAddressLine1(), locality));
        address.setAddressLine2(firstNonBlank(address.getAddressLine2(), address.getAddressLine1(), TEST_DATA_MARKER));
        address.setRegion(firstNonBlank(address.getRegion(), locality, UNITED_KINGDOM));
        return address;
    }
}