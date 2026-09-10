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

        LocalityCluster(String locality, String area, String region) {
            this.locality = locality;
            this.area = area;
            this.region = region;
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
            new LocalityCluster("LONDON", "CAMDEN", "GREATER LONDON"),
            new LocalityCluster("LONDON", "ISLINGTON", "GREATER LONDON"),
            new LocalityCluster("LONDON", "CHELSEA", "GREATER LONDON"),
            new LocalityCluster("MANCHESTER", "DIDSBURY", "GREATER MANCHESTER"),
            new LocalityCluster("MANCHESTER", "CHORLTON", "GREATER MANCHESTER"),
            new LocalityCluster("BIRMINGHAM", "EDGBASTON", "WEST MIDLANDS"),
            new LocalityCluster("BIRMINGHAM", "MOSELEY", "WEST MIDLANDS"),
            new LocalityCluster("BRISTOL", "CLIFTON", "SOUTH WEST ENGLAND"),
            new LocalityCluster("BRISTOL", "REDLAND", "SOUTH WEST ENGLAND"),
            new LocalityCluster("LEEDS", "HEADINGLEY", "WEST YORKSHIRE"),
            new LocalityCluster("LEEDS", "MEANWOOD", "WEST YORKSHIRE"),
            new LocalityCluster("LIVERPOOL", "TOXTETH", "MERSEYSIDE"),
            new LocalityCluster("LIVERPOOL", "WAVERTREE", "MERSEYSIDE"),
            new LocalityCluster("NEWCASTLE", "GOSFORTH", "TYNE AND WEAR"),
            new LocalityCluster("NEWCASTLE", "JESMOND", "TYNE AND WEAR"),
            new LocalityCluster("CAMBRIDGE", "NEWNHAM", "CAMBRIDGESHIRE"),
            new LocalityCluster("CAMBRIDGE", "CHESTERTON", "CAMBRIDGESHIRE"),
            new LocalityCluster("OXFORD", "CITY CENTRE", "OXFORDSHIRE"),
            new LocalityCluster("OXFORD", "JERICHO", "OXFORDSHIRE"),
            new LocalityCluster("YORK", "CITY CENTRE", "NORTH YORKSHIRE"),
            new LocalityCluster("CHESTER", "CITY CENTRE", "CHESHIRE"),
            new LocalityCluster("BATH", "CITY CENTRE", "BATH AND NORTH EAST SOMERSET"),
            new LocalityCluster("NOTTINGHAM", "CITY CENTRE", "NOTTINGHAMSHIRE"),
            new LocalityCluster("LEICESTER", "CITY CENTRE", "LEICESTERSHIRE"),
            new LocalityCluster("COVENTRY", "CITY CENTRE", "WEST MIDLANDS"),
            new LocalityCluster("BRIGHTON", "CITY CENTRE", "EAST SUSSEX"),
            new LocalityCluster("SOUTHAMPTON", "CITY CENTRE", "HAMPSHIRE")
    };

    private static final LocalityCluster[] WALES_CLUSTERS = {
            new LocalityCluster("CARDIFF", "CATHAYS", "SOUTH GLAMORGAN"),
            new LocalityCluster("CARDIFF", "PONTCANNA", "SOUTH GLAMORGAN"),
            new LocalityCluster("CARDIFF", "GRANGETOWN", "SOUTH GLAMORGAN"),
            new LocalityCluster("SWANSEA", "UPLANDS", "WEST GLAMORGAN"),
            new LocalityCluster("SWANSEA", "MUMBLES", "WEST GLAMORGAN"),
            new LocalityCluster("NEWPORT", "STOW HILL", "MONMOUTHSHIRE"),
            new LocalityCluster("NEWPORT", "ROGERSTONE", "MONMOUTHSHIRE"),
            new LocalityCluster("WREXHAM", "TOWN CENTRE", "CLWYD"),
            new LocalityCluster("WREXHAM", "ACTON", "CLWYD"),
            new LocalityCluster("ABERYSTWYTH", "TOWN CENTRE", "CEREDIGION"),
            new LocalityCluster("BANGOR", "TOWN CENTRE", "GWYNEDD"),
            new LocalityCluster("LLANDRINDOD WELLS", "TOWN CENTRE", "POWYS")
    };

    private static final LocalityCluster[] SCOTLAND_CLUSTERS = {
            new LocalityCluster("EDINBURGH", "LEITH", "CITY OF EDINBURGH"),
            new LocalityCluster("EDINBURGH", "MORNINGSIDE", "CITY OF EDINBURGH"),
            new LocalityCluster("EDINBURGH", "STOCKBRIDGE", "CITY OF EDINBURGH"),
            new LocalityCluster("GLASGOW", "PARTICK", "GLASGOW CITY"),
            new LocalityCluster("GLASGOW", "GOVAN", "GLASGOW CITY"),
            new LocalityCluster("GLASGOW", "MERCHANT CITY", "GLASGOW CITY"),
            new LocalityCluster("ABERDEEN", "WEST END", "ABERDEENSHIRE"),
            new LocalityCluster("ABERDEEN", "ROSEMOUNT", "ABERDEENSHIRE"),
            new LocalityCluster("DUNDEE", "BROUGHTY FERRY", "DUNDEE CITY"),
            new LocalityCluster("DUNDEE", "WEST END", "DUNDEE CITY"),
            new LocalityCluster("STIRLING", "CITY CENTRE", "STIRLING"),
            new LocalityCluster("PERTH", "CITY CENTRE", "PERTH AND KINROSS"),
            new LocalityCluster("INVERNESS", "CITY CENTRE", "HIGHLAND")
    };

    private static final LocalityCluster[] NI_CLUSTERS = {
            new LocalityCluster("BELFAST", "BOTANIC", "COUNTY ANTRIM"),
            new LocalityCluster("BELFAST", "ORMEAU", "COUNTY ANTRIM"),
            new LocalityCluster("BELFAST", "CATHEDRAL QUARTER", "COUNTY ANTRIM"),
            new LocalityCluster("BELFAST", "TITANIC QUARTER", "COUNTY ANTRIM"),
            new LocalityCluster("DERRY", "BOGSIDE", "COUNTY LONDONDERRY"),
            new LocalityCluster("LISBURN", "TOWN CENTRE", "LISBURN AND CASTLEREAGH CITY"),
            new LocalityCluster("LISBURN", "LAMBEG", "LISBURN AND CASTLEREAGH CITY"),
            new LocalityCluster("NEWRY", "HILL STREET", "MOURNE AND DOWN"),
            new LocalityCluster("NEWRY", "WARRENPOINT", "MOURNE AND DOWN"),
            new LocalityCluster("ARMAGH", "TOWN CENTRE", "ARMAGH, BANBRIDGE AND CRAIGAVON"),
            new LocalityCluster("OMAGH", "TOWN CENTRE", "FERMANAGH AND OMAGH"),
            new LocalityCluster("STRABANE", "TOWN CENTRE", "FERMANAGH AND OMAGH"),
            new LocalityCluster("BANGOR", "TOWN CENTRE", "NORTH DOWN AND ARDS")
    };

    private static final LocalityCluster[] NETHERLANDS_CLUSTERS = {
            new LocalityCluster("AMSTERDAM", "CENTRUM", "NOORD-HOLLAND"),
            new LocalityCluster("AMSTERDAM", "DE PIJP", "NOORD-HOLLAND"),
            new LocalityCluster("ROTTERDAM", "KRALINGEN", "ZUID-HOLLAND"),
            new LocalityCluster("ROTTERDAM", "DELFSHAVEN", "ZUID-HOLLAND"),
            new LocalityCluster("UTRECHT", "BINNENSTAD", "UTRECHT PROVINCE"),
            new LocalityCluster("UTRECHT", "LOMBOK", "UTRECHT PROVINCE"),
            new LocalityCluster("EINDHOVEN", "STRIJP", "NOORD-BRABANT"),
            new LocalityCluster("EINDHOVEN", "GESTEL", "NOORD-BRABANT"),
            new LocalityCluster("GRONINGEN", "BINNENSTAD", "GRONINGEN PROVINCE"),
            new LocalityCluster("GRONINGEN", "SELWERD", "GRONINGEN PROVINCE"),
            new LocalityCluster("MAASTRICHT", "WYCK", "LIMBURG"),
            new LocalityCluster("MAASTRICHT", "JEKERKWARTIER", "LIMBURG"),
            new LocalityCluster("THE HAGUE", "CENTRUM", "ZUID-HOLLAND"),
            new LocalityCluster("THE HAGUE", "SCHEVENINGEN", "ZUID-HOLLAND"),
            new LocalityCluster("HAARLEM", "CITY CENTRE", "NOORD-HOLLAND"),
            new LocalityCluster("LEIDEN", "CITY CENTRE", "ZUID-HOLLAND"),
            new LocalityCluster("DELFT", "CITY CENTRE", "ZUID-HOLLAND"),
            new LocalityCluster("ARNHEM", "CITY CENTRE", "GELDERLAND"),
            new LocalityCluster("NIJMEGEN", "CITY CENTRE", "GELDERLAND")
    };

    private static final LocalityCluster[] PANAMA_CLUSTERS = {
            new LocalityCluster("PANAMA CITY", "BELLA VISTA", "PANAMA"),
            new LocalityCluster("PANAMA CITY", "SAN FRANCISCO", "PANAMA"),
            new LocalityCluster("PANAMA CITY", "OBARRIO", "PANAMA"),
            new LocalityCluster("PANAMA CITY", "EL CANGREJO", "PANAMA"),
            new LocalityCluster("COLON", "BARRIO NORTE", "COLON PROVINCE"),
            new LocalityCluster("COLON", "CRISTOBAL", "COLON PROVINCE"),
            new LocalityCluster("DAVID", "CENTRO", "CHIRIQUI"),
            new LocalityCluster("DAVID", "SAN MATEO", "CHIRIQUI"),
            new LocalityCluster("SANTIAGO", "BARRIO SUR", "VERAGUAS"),
            new LocalityCluster("SANTIAGO", "BARRIO CENTRAL", "VERAGUAS"),
            new LocalityCluster("CHITRE", "CENTRO", "HERRERA"),
            new LocalityCluster("CHITRE", "LA ARENA", "HERRERA"),
            new LocalityCluster("LA PALMA", "TOWN CENTRE", "DARIEN"),
            new LocalityCluster("BOCAS DEL TORO", "BOCAS TOWN", "BOCAS DEL TORO")
    };

    private static final LocalityCluster[] CANADA_CLUSTERS = {
            new LocalityCluster("TORONTO", "DOWNTOWN", "ONTARIO"),
            new LocalityCluster("TORONTO", "NORTH YORK", "ONTARIO"),
            new LocalityCluster("VANCOUVER", "KITSILANO", "BRITISH COLUMBIA"),
            new LocalityCluster("VANCOUVER", "YALETOWN", "BRITISH COLUMBIA"),
            new LocalityCluster("MONTREAL", "PLATEAU", "QUEBEC"),
            new LocalityCluster("MONTREAL", "VERDUN", "QUEBEC"),
            new LocalityCluster("CALGARY", "BELTLINE", "ALBERTA"),
            new LocalityCluster("OTTAWA", "CENTRETOWN", "ONTARIO"),
            new LocalityCluster("WINNIPEG", "DOWNTOWN", "MANITOBA"),
            new LocalityCluster("EDMONTON", "DOWNTOWN", "ALBERTA"),
            new LocalityCluster("QUEBEC CITY", "VIEUX-QUEBEC", "QUEBEC"),
            new LocalityCluster("HALIFAX", "DOWNTOWN", "NOVA SCOTIA")
    };

    private static final LocalityCluster[] AUSTRALIA_CLUSTERS = {
            new LocalityCluster("SYDNEY", "SURRY HILLS", "NEW SOUTH WALES"),
            new LocalityCluster("SYDNEY", "NEWTOWN", "NEW SOUTH WALES"),
            new LocalityCluster("MELBOURNE", "SOUTH YARRA", "VICTORIA"),
            new LocalityCluster("MELBOURNE", "FITZROY", "VICTORIA"),
            new LocalityCluster("BRISBANE", "FORTITUDE VALLEY", "QUEENSLAND"),
            new LocalityCluster("PERTH", "SUBIACO", "WESTERN AUSTRALIA"),
            new LocalityCluster("ADELAIDE", "NORTH ADELAIDE", "SOUTH AUSTRALIA"),
            new LocalityCluster("CANBERRA", "BRADDON", "AUSTRALIAN CAPITAL TERRITORY"),
            new LocalityCluster("BRISBANE", "SOUTH BANK", "QUEENSLAND"),
            new LocalityCluster("HOBART", "CITY CENTRE", "TASMANIA"),
            new LocalityCluster("DARWIN", "CITY CENTRE", "NORTHERN TERRITORY"),
            new LocalityCluster("PERTH", "CITY CENTRE", "WESTERN AUSTRALIA"),
            new LocalityCluster("ADELAIDE", "CITY CENTRE", "SOUTH AUSTRALIA")
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
        
        address.setAddressLine1(address.getAddressLine1());
        address.setAddressLine2(firstNonBlank(address.getAddressLine2(), address.getAddressLine1(), TEST_DATA_MARKER));
        address.setRegion(firstNonBlank(address.getRegion(), UNITED_KINGDOM));
        return address;
    }
}