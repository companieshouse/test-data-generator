package uk.gov.companieshouse.api.testdata.service.address.impl;

import java.util.Locale;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.UsualResidentialAddress;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.service.address.AddressService;
import uk.gov.companieshouse.api.testdata.service.address.AddressProfileContext;
import uk.gov.companieshouse.api.testdata.service.address.profile.EuProfile;
import uk.gov.companieshouse.api.testdata.service.address.profile.EuProfile.EuPostcodeType;
import uk.gov.companieshouse.api.testdata.service.address.profile.LocalityCluster;
import uk.gov.companieshouse.api.testdata.service.address.profile.NonEuProfile;
import uk.gov.companieshouse.api.testdata.service.address.profile.NonEuProfile.NonEuPostcodeType;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressProfileContext addressProfileContext;

    @Autowired
    private RandomService randomService;

    private static final String UNITED_KINGDOM = "United Kingdom";
    private static final String NETHERLANDS = "Netherlands";
    private static final String GERMANY = "Germany";
    private static final String FRANCE = "France";
    private static final String SPAIN = "Spain";
    private static final String ITALY = "Italy";
    private static final String POLAND = "Poland";
    private static final String PANAMA = "Panama";
    private static final String CANADA = "Canada";
    private static final String AUSTRALIA = "Australia";
    private static final String JERSEY = "Jersey";
    private static final String MALTA = "Malta";
    private static final String CYPRUS = "Cyprus";
    private static final String BERMUDA = "Bermuda";
    private static final String TEST_DATA_MARKER = "TEST DATA";
    private static final String UK_SENTINEL_POSTCODE = "ZZ1 1ZZ";
    private static final String NETHERLANDS_SENTINEL_POSTCODE = "0000 ZZ";
    private static final String GERMANY_SENTINEL_POSTCODE = "00000";
    private static final String FRANCE_SENTINEL_POSTCODE = "75000";
    private static final String SPAIN_SENTINEL_POSTCODE = "28000";
    private static final String ITALY_SENTINEL_POSTCODE = "00000";
    private static final String POLAND_SENTINEL_POSTCODE = "00-000";
    private static final String PANAMA_SENTINEL_POSTCODE = "0000-0000";
    private static final String CANADA_SENTINEL_POSTCODE = "Z9Z 9Z9";
    private static final String AUSTRALIA_SENTINEL_POSTCODE = "0000";
    private static final String JERSEY_SENTINEL_POSTCODE = "JE1 1AA";
    private static final String MALTA_SENTINEL_POSTCODE = "VLT 1000";
    private static final String CYPRUS_SENTINEL_POSTCODE = "1000";
    private static final String BERMUDA_SENTINEL_POSTCODE = "HM 11";
    private static final Faker FAKER = new Faker(Locale.UK);
    private static final Faker UK_FAKER = new Faker(Locale.UK);
    private static final Faker NETHERLANDS_FAKER = new Faker(new Locale("nl", "NL"));
    private static final Faker GERMANY_FAKER = new Faker(Locale.GERMANY);
    private static final Faker FRANCE_FAKER = new Faker(Locale.FRANCE);
    private static final Faker SPAIN_FAKER = new Faker(new Locale("es", "ES"));
    private static final Faker ITALY_FAKER = new Faker(Locale.ITALY);
    private static final Faker POLAND_FAKER = new Faker(new Locale("pl"));
    private static final Faker PANAMA_FAKER = new Faker(new Locale("es"));
    private static final Faker CANADA_FAKER = new Faker(Locale.CANADA);
    private static final Faker AUSTRALIA_FAKER = new Faker(new Locale("en", "AU"));

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
            new LocalityCluster("SOUTHAMPTON", "CITY CENTRE", "HAMPSHIRE"),
            new LocalityCluster("EXETER", "ST. LEONARDS", "DEVON"),
            new LocalityCluster("READING", "CAVERSHAM", "BERKSHIRE"),
            new LocalityCluster("NORWICH", "TOMBLAND", "NORFOLK")
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
            new LocalityCluster("LLANDRINDOD WELLS", "TOWN CENTRE", "POWYS"),
            new LocalityCluster("CAERPHILLY", "CASTLE QUARTER", "CAERPHILLY"),
            new LocalityCluster("CARMARTHEN", "JOHN STREET", "CARMARTHENSHIRE"),
            new LocalityCluster("HOLYHEAD", "PORT AREA", "ISLE OF ANGLESEY")
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
            new LocalityCluster("INVERNESS", "CITY CENTRE", "HIGHLAND"),
            new LocalityCluster("PAISLEY", "GLENBURN", "RENFREWSHIRE"),
            new LocalityCluster("FALKIRK", "GRAHAMSTON", "FALKIRK"),
            new LocalityCluster("AYR", "KINCALDRA", "SOUTH AYRSHIRE")
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
            new LocalityCluster("BANGOR", "TOWN CENTRE", "NORTH DOWN AND ARDS"),
            new LocalityCluster("COLERAINE", "LONG COMMON", "COUNTY LONDONDERRY"),
            new LocalityCluster("ENNISKILLEN", "TOWN CENTRE", "FERMANAGH AND OMAGH"),
            new LocalityCluster("DOWNPATRICK", "MARKET STREET", "DOWN")
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
            new LocalityCluster("NIJMEGEN", "CITY CENTRE", "GELDERLAND"),
            new LocalityCluster("ALMERE", "STADSCENTRUM", "FLEVOLAND"),
            new LocalityCluster("BREDA", "GINNEKEN", "NOORD-BRABANT"),
            new LocalityCluster("TILBURG", "OUDE MARKT", "NOORD-BRABANT")
    };

    private static final LocalityCluster[] GERMANY_CLUSTERS = {
            new LocalityCluster("BERLIN", "MITTE", "BERLIN STATE"),
            new LocalityCluster("BERLIN", "CHARLOTTENBURG", "BERLIN STATE"),
            new LocalityCluster("MUNICH", "ALTSTADT", "BAVARIA"),
            new LocalityCluster("MUNICH", "SCHWABING", "BAVARIA"),
            new LocalityCluster("HAMBURG", "ALTSTADT", "HAMBURG STATE"),
            new LocalityCluster("HAMBURG", "NEUSTADT", "HAMBURG STATE"),
            new LocalityCluster("COLOGNE", "ALTSTADT", "NORTH RHINE-WESTPHALIA"),
            new LocalityCluster("FRANKFURT", "SACHSENHAUSEN", "HESSE"),
            new LocalityCluster("FRANKFURT", "WESTEND", "HESSE"),
            new LocalityCluster("LEIPZIG", "MITTE", "SAXONY"),
            new LocalityCluster("DRESDEN", "ALTSTADT", "SAXONY"),
            new LocalityCluster("HEIDELBERG", "ALTSTADT", "BADEN-WÜRTTEMBERG"),
            new LocalityCluster("STUTTGART", "MITTE", "BADEN-WÜRTTEMBERG"),
            new LocalityCluster("DÜSSELDORF", "ALTSTADT", "NORTH RHINE-WESTPHALIA"),
            new LocalityCluster("BREMEN", "SCHNOOR", "BREMEN STATE")
    };

    private static final LocalityCluster[] FRANCE_CLUSTERS = {
            new LocalityCluster("PARIS", "1ST ARRONDISSEMENT", "ILE-DE-FRANCE"),
            new LocalityCluster("PARIS", "4TH ARRONDISSEMENT", "ILE-DE-FRANCE"),
            new LocalityCluster("PARIS", "6TH ARRONDISSEMENT", "ILE-DE-FRANCE"),
            new LocalityCluster("LYON", "PRESQU'ILE", "AUVERGNE-RHÔNE-ALPES"),
            new LocalityCluster("LYON", "VIEUX LYON", "AUVERGNE-RHÔNE-ALPES"),
            new LocalityCluster("MARSEILLE", "LE PANIER", "PROVENCE-ALPES-CÔTE D'AZUR"),
            new LocalityCluster("MARSEILLE", "ENDOUME", "PROVENCE-ALPES-CÔTE D'AZUR"),
            new LocalityCluster("TOULOUSE", "SAINT-CYPRIEN", "OCCITANIE"),
            new LocalityCluster("NICE", "VIEUX NICE", "PROVENCE-ALPES-CÔTE D'AZUR"),
            new LocalityCluster("NANTES", "VIEILLE VILLE", "PAYS DE LA LOIRE"),
            new LocalityCluster("BORDEAUX", "CHARTRONS", "NOUVELLE-AQUITAINE"),
            new LocalityCluster("LILLE", "VIEUX-LILLE", "HAUTS-DE-FRANCE"),
            new LocalityCluster("MONTPELLIER", "ECUSSON", "OCCITANIE"),
            new LocalityCluster("STRASBOURG", "PETITE FRANCE", "GRAND EST"),
            new LocalityCluster("RENNES", "CENTRE", "BRITTANY")
    };

    private static final LocalityCluster[] SPAIN_CLUSTERS = {
            new LocalityCluster("MADRID", "SOL", "COMMUNITY OF MADRID"),
            new LocalityCluster("MADRID", "GRAN VÍA", "COMMUNITY OF MADRID"),
            new LocalityCluster("MADRID", "MALASAÑA", "COMMUNITY OF MADRID"),
            new LocalityCluster("BARCELONA", "GÒTIC", "CATALONIA"),
            new LocalityCluster("BARCELONA", "EIXAMPLE", "CATALONIA"),
            new LocalityCluster("BARCELONA", "GRACIA", "CATALONIA"),
            new LocalityCluster("VALENCIA", "CIUDAD VELLA", "VALENCIA"),
            new LocalityCluster("SEVILLE", "SANTA CRUZ", "ANDALUSIA"),
            new LocalityCluster("SEVILLE", "TRIANA", "ANDALUSIA"),
            new LocalityCluster("BILBAO", "CASCO VIEJO", "BASQUE COUNTRY"),
            new LocalityCluster("MALAGA", "CENTRO", "ANDALUSIA"),
            new LocalityCluster("PALMA", "CASCO ANTIGUO", "BALEARIC ISLANDS"),
            new LocalityCluster("ZARAGOZA", "EL GANCHO", "ARAGON"),
            new LocalityCluster("ALICANTE", "SANTA CRUZ", "VALENCIA"),
            new LocalityCluster("GRANADA", "ALBAYZIN", "ANDALUSIA")
    };

    private static final LocalityCluster[] ITALY_CLUSTERS = {
            new LocalityCluster("ROME", "CENTRO STORICO", "LAZIO"),
            new LocalityCluster("ROME", "TRASTEVERE", "LAZIO"),
            new LocalityCluster("ROME", "TESTACCIO", "LAZIO"),
            new LocalityCluster("MILAN", "DUOMO", "LOMBARDY"),
            new LocalityCluster("MILAN", "BRERA", "LOMBARDY"),
            new LocalityCluster("MILAN", "NAVIGLI", "LOMBARDY"),
            new LocalityCluster("FLORENCE", "CENTRO", "TUSCANY"),
            new LocalityCluster("FLORENCE", "OLTRARNO", "TUSCANY"),
            new LocalityCluster("VENICE", "SAN MARCO", "VENETO"),
            new LocalityCluster("VENICE", "CANNAREGIO", "VENETO"),
            new LocalityCluster("TURIN", "CENTRO", "PIEDMONT"),
            new LocalityCluster("NAPLES", "CENTRO", "CAMPANIA"),
            new LocalityCluster("BOLOGNA", "SANTO STEFANO", "EMILIA-ROMAGNA"),
            new LocalityCluster("GENOA", "BASSI", "LIGURIA"),
            new LocalityCluster("PALERMO", "KALSA", "SICILY")
    };

    private static final LocalityCluster[] POLAND_CLUSTERS = {
            new LocalityCluster("WARSAW", "STARE MIASTO", "MAZOVIA"),
            new LocalityCluster("WARSAW", "POWIŚLE", "MAZOVIA"),
            new LocalityCluster("WARSAW", "PRAGA", "MAZOVIA"),
            new LocalityCluster("KRAKOW", "STARE MIASTO", "MALOPOLSKIE"),
            new LocalityCluster("KRAKOW", "KAZIMIERZ", "MALOPOLSKIE"),
            new LocalityCluster("KRAKOW", "PODGÓRZE", "MALOPOLSKIE"),
            new LocalityCluster("WROCLAW", "RYNEK", "LOWER SILESIA"),
            new LocalityCluster("WROCLAW", "OSTRÓW TUMSKI", "LOWER SILESIA"),
            new LocalityCluster("GDANSK", "STARE MIASTO", "POMERANIA"),
            new LocalityCluster("GDANSK", "WRZESZCZ", "POMERANIA"),
            new LocalityCluster("POZNAŃ", "STARE MIASTO", "GREATER POLAND"),
            new LocalityCluster("ŁÓDŹ", "PIOTRKOWSKA", "ŁÓDŹ VOIVODESHIP"),
            new LocalityCluster("SZCZECIN", "PODMURZE", "WEST POMERANIA"),
            new LocalityCluster("LUBLIN", "WÓLKA", "LUBLIN VOIVODESHIP"),
            new LocalityCluster("TORUŃ", "CHEŁMIŃSKIE", "KUYAVIA-POMERANIA")
    };

    private static final LocalityCluster[] JERSEY_CLUSTERS = {
            new LocalityCluster("ST. HELIER", "TOWN CENTRE", "JERSEY ISLAND"),
            new LocalityCluster("ST. HELIER", "WATERFRONT", "JERSEY ISLAND"),
            new LocalityCluster("ST. BRELADE", "BAY AREA", "JERSEY ISLAND"),
            new LocalityCluster("ST. CLEMENT", "SAMARES", "JERSEY ISLAND"),
            new LocalityCluster("ST. LAWRENCE", "BEAUMONT", "JERSEY ISLAND"),
            new LocalityCluster("ST. MARTIN", "GOREY VILLAGE", "JERSEY ISLAND"),
            new LocalityCluster("ST. OUEN", "LE BRAYE", "JERSEY ISLAND"),
            new LocalityCluster("ST. PETER", "SAINT PETER'S VALLEY", "JERSEY ISLAND"),
            new LocalityCluster("ST. HELIER", "LIBERATION SQUARE", "JERSEY ISLAND"),
            new LocalityCluster("ST. SAVIOUR", "MILLBROOK", "JERSEY ISLAND")
    };

    private static final LocalityCluster[] GUERNSEY_CLUSTERS = {
            new LocalityCluster("ST. PETER PORT", "TOWN CENTRE", "GUERNSEY ISLAND"),
            new LocalityCluster("ST. PETER PORT", "SOUTH ESPLANADE", "GUERNSEY ISLAND"),
            new LocalityCluster("ST. SAMPSON", "BRIDGE", "GUERNSEY ISLAND"),
            new LocalityCluster("VALE", "L'ANCRESSE", "GUERNSEY ISLAND"),
            new LocalityCluster("CASTEL", "COBO", "GUERNSEY ISLAND"),
            new LocalityCluster("ST. MARTIN", "SAUMAREZ PARK", "GUERNSEY ISLAND"),
            new LocalityCluster("FOREST", "TORTEVAL ROAD", "GUERNSEY ISLAND"),
            new LocalityCluster("TORTEVAL", "PLEINMONT", "GUERNSEY ISLAND"),
            new LocalityCluster("ST. ANDREW", "PERYGROVE", "GUERNSEY ISLAND"),
            new LocalityCluster("ST. MARTIN", "ROUTE DE LA HOUMETTE", "GUERNSEY ISLAND")
    };

    private static final LocalityCluster[] ISLE_OF_MAN_CLUSTERS = {
            new LocalityCluster("DOUGLAS", "TOWN CENTRE", "ISLE OF MAN"),
            new LocalityCluster("DOUGLAS", "ONCHAN", "ISLE OF MAN"),
            new LocalityCluster("RAMSEY", "MOONEY'S TERRACE", "ISLE OF MAN"),
            new LocalityCluster("PEEL", "GLENFABA", "ISLE OF MAN"),
            new LocalityCluster("CASTLETOWN", "MALEW STREET", "ISLE OF MAN"),
            new LocalityCluster("PORT ERIN", "STATION ROAD", "ISLE OF MAN"),
            new LocalityCluster("PORT ST. MARY", "CHURCH ROAD", "ISLE OF MAN"),
            new LocalityCluster("LAXEY", "GLEN ROAD", "ISLE OF MAN"),
            new LocalityCluster("KIRK MICHAEL", "BALLAUGH", "ISLE OF MAN"),
            new LocalityCluster("RAMSEY", "NORTH SHORE", "ISLE OF MAN")
    };

    private static final LocalityCluster[] MALTA_CLUSTERS = {
            new LocalityCluster("VALLETTA", "ST. ELMO", "MALTA ISLAND"),
            new LocalityCluster("SLIEMA", "TIGNE", "MALTA ISLAND"),
            new LocalityCluster("MOSTA", "IT-TARGA", "MALTA ISLAND"),
            new LocalityCluster("BIRKIRKARA", "SANTA VENERA", "MALTA ISLAND"),
            new LocalityCluster("QORMI", "MRIEHEL", "MALTA ISLAND"),
            new LocalityCluster("NAXXAR", "SALINA", "MALTA ISLAND"),
            new LocalityCluster("MELLIEHA", "MARFA", "MALTA ISLAND"),
            new LocalityCluster("BIRGU", "COTTONERA", "MALTA ISLAND"),
            new LocalityCluster("MARSASKALA", "ZONQOR", "MALTA ISLAND"),
            new LocalityCluster("RABAT", "HOWARD GARDENS", "MALTA ISLAND")
    };

    private static final LocalityCluster[] CYPRUS_CLUSTERS = {
            new LocalityCluster("NICOSIA", "ENGOMI", "CYPRUS ISLAND"),
            new LocalityCluster("LIMASSOL", "AGIOS NIKOLAOS", "CYPRUS ISLAND"),
            new LocalityCluster("LARNACA", "FINIKOUDES", "CYPRUS ISLAND"),
            new LocalityCluster("PAPHOS", "KATO PAFOS", "CYPRUS ISLAND"),
            new LocalityCluster("FAMAGUSTA", "VAROSHA", "CYPRUS ISLAND"),
            new LocalityCluster("KYRENIA", "KARAKUM", "CYPRUS ISLAND"),
            new LocalityCluster("MORFOU", "TILLYRIA", "CYPRUS ISLAND"),
            new LocalityCluster("AYIA NAPA", "PROTARAS", "CYPRUS ISLAND"),
            new LocalityCluster("PARALIMNI", "KAPPARIS", "CYPRUS ISLAND"),
            new LocalityCluster("POLIS", "AKAMAS", "CYPRUS ISLAND")
    };

    private static final LocalityCluster[] BERMUDA_CLUSTERS = {
            new LocalityCluster("HAMILTON", "FRONT STREET", "PEMBROKE"),
            new LocalityCluster("ST. GEORGE", "OLD TOWN", "ST. GEORGE"),
            new LocalityCluster("DOCKYARD", "IRELAND ISLAND", "SANDYS"),
            new LocalityCluster("SOMERSET", "MANGROVE BAY", "SANDYS"),
            new LocalityCluster("DEVONSHIRE", "DEVONSHIRE MARSH", "DEVONSHIRE"),
            new LocalityCluster("WARWICK", "BELMONT", "WARWICK"),
            new LocalityCluster("SOUTHAMPTON", "WHALE BAY", "SOUTHAMPTON"),
            new LocalityCluster("SMITHS", "SPITTAL POND", "SMITHS"),
            new LocalityCluster("PAGET", "ELBOW BEACH", "PAGET"),
            new LocalityCluster("PEMBROKE", "PAR-LA-VILLE", "PEMBROKE")
    };

    private static final LocalityCluster[] PANAMA_CLUSTERS = {
            new LocalityCluster("PANAMA CITY", "BELLA VISTA", "PANAMA PROVINCE"),
            new LocalityCluster("PANAMA CITY", "SAN FRANCISCO", "PANAMA PROVINCE"),
            new LocalityCluster("PANAMA CITY", "OBARRIO", "PANAMA PROVINCE"),
            new LocalityCluster("PANAMA CITY", "EL CANGREJO", "PANAMA PROVINCE"),
            new LocalityCluster("COLON", "BARRIO NORTE", "COLON PROVINCE"),
            new LocalityCluster("COLON", "CRISTOBAL", "COLON PROVINCE"),
            new LocalityCluster("DAVID", "CENTRO", "CHIRIQUI"),
            new LocalityCluster("DAVID", "SAN MATEO", "CHIRIQUI"),
            new LocalityCluster("SANTIAGO", "BARRIO SUR", "VERAGUAS"),
            new LocalityCluster("SANTIAGO", "BARRIO CENTRAL", "VERAGUAS"),
            new LocalityCluster("CHITRE", "CENTRO", "HERRERA"),
            new LocalityCluster("CHITRE", "LA ARENA", "HERRERA"),
            new LocalityCluster("LA PALMA", "TOWN CENTRE", "DARIEN"),
            new LocalityCluster("BOCAS DEL TORO", "BOCAS TOWN", "BOCAS DEL TORO"),
            new LocalityCluster("PENONOME", "LOS CERRITOS", "COCLE"),
            new LocalityCluster("LAS TABLAS", "LA ERMITA", "LOS SANTOS")
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
            new LocalityCluster("HALIFAX", "DOWNTOWN", "NOVA SCOTIA"),
            new LocalityCluster("KITCHENER", "UPTOWN", "ONTARIO"),
            new LocalityCluster("VICTORIA", "JAMES BAY", "BRITISH COLUMBIA")
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
            new LocalityCluster("ADELAIDE", "CITY CENTRE", "SOUTH AUSTRALIA"),
            new LocalityCluster("GEELONG", "NEW TOWN", "VICTORIA"),
            new LocalityCluster("WOLLONGONG", "NORTH BEACH", "NEW SOUTH WALES")
    };

    private static final EuProfile[] EU_PROFILES = {
            new EuProfile("Netherlands", NETHERLANDS_FAKER, NETHERLANDS_CLUSTERS, EuPostcodeType.NETHERLANDS),
            new EuProfile("Germany", GERMANY_FAKER, GERMANY_CLUSTERS, EuPostcodeType.GERMANY),
            new EuProfile("France", FRANCE_FAKER, FRANCE_CLUSTERS, EuPostcodeType.FRANCE),
            new EuProfile("Spain", SPAIN_FAKER, SPAIN_CLUSTERS, EuPostcodeType.SPAIN),
            new EuProfile("Italy", ITALY_FAKER, ITALY_CLUSTERS, EuPostcodeType.ITALY),
            new EuProfile("Poland", POLAND_FAKER, POLAND_CLUSTERS, EuPostcodeType.POLAND),
            // Malta and Cyprus are EU member states (joined 2004), not non-EU jurisdictions.
            new EuProfile("Malta", FAKER, MALTA_CLUSTERS, EuPostcodeType.MALTA),
            new EuProfile("Cyprus", FAKER, CYPRUS_CLUSTERS, EuPostcodeType.CYPRUS)
    };

    private static final NonEuProfile[] NON_EU_PROFILES = {
            new NonEuProfile("Panama", PANAMA_FAKER, PANAMA_CLUSTERS, NonEuPostcodeType.PANAMA),
            new NonEuProfile("Canada", CANADA_FAKER, CANADA_CLUSTERS, NonEuPostcodeType.CANADA),
            new NonEuProfile("Australia", AUSTRALIA_FAKER, AUSTRALIA_CLUSTERS, NonEuPostcodeType.AUSTRALIA),
            new NonEuProfile("Jersey", FAKER, JERSEY_CLUSTERS, NonEuPostcodeType.JERSEY),
            new NonEuProfile("Guernsey", FAKER, GUERNSEY_CLUSTERS, NonEuPostcodeType.GUERNSEY),
            new NonEuProfile("Isle of Man", FAKER, ISLE_OF_MAN_CLUSTERS, NonEuPostcodeType.ISLE_OF_MAN),
            new NonEuProfile("Bermuda", FAKER, BERMUDA_CLUSTERS, NonEuPostcodeType.BERMUDA)
    };

    @Override
    public Address getAddress(JurisdictionType jurisdiction) {
        UsualResidentialAddress addr = getUsualResidentialAddress(jurisdiction);
        Address address = new Address();
        address.setPremise(addr.getPremises());
        address.setAddressLine1(addr.getAddressLine2());
        address.setAddressLine2(addr.getAddressLine1());
        address.setCountry(addr.getCountry());
        address.setLocality(addr.getLocality());
        address.setPostalCode(addr.getPostalCode());
        address.setRegion(addr.getRegion());
        return address;
    }

    @Override
    public Address getOverseasAddress() {
        return getAddress(JurisdictionType.NON_EU);
    }

    @Override
    public String getCountryFromSelectedProfile(JurisdictionType jurisdiction) {
        if (jurisdiction == JurisdictionType.EUROPEAN_UNION) {
            EuProfile euProfile = getOrSelectEuProfile();
            return euProfile.country;
        }
        if (jurisdiction == JurisdictionType.NON_EU
                || jurisdiction == JurisdictionType.UNITED_KINGDOM) {
            if (jurisdiction == JurisdictionType.UNITED_KINGDOM
                    && getOrSelectUnitedKingdomProfileType() == JurisdictionType.EUROPEAN_UNION) {
                return getOrSelectEuProfile().country;
            }
            NonEuProfile nonEuProfile = getOrSelectNonEuProfile();
            return nonEuProfile.country;
        }
        return getOrSelectUkCountry(jurisdiction);
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

        if (effectiveJurisdiction == JurisdictionType.UNITED_KINGDOM) {
            if (getOrSelectUnitedKingdomProfileType() == JurisdictionType.EUROPEAN_UNION) {
                return buildAddressFromEuProfile(residentialAddress, getOrSelectEuProfile());
            }
            return buildAddressFromNonEuProfile(residentialAddress, getOrSelectNonEuProfile());
        }

        if (isUkJurisdiction(effectiveJurisdiction)) {
            LocalityCluster cluster = getUkClusterForJurisdiction(effectiveJurisdiction);
            residentialAddress.setAddressLine1(cluster.area);
            residentialAddress.setAddressLine2(
                    UK_FAKER.address().streetName().toUpperCase(Locale.UK) + " " + TEST_DATA_MARKER);
            residentialAddress.setCountry(getOrSelectUkCountry(effectiveJurisdiction));
            residentialAddress.setLocality(cluster.locality);
            residentialAddress.setPostalCode(generateUkPostcode());
            residentialAddress.setRegion(cluster.region);
            return residentialAddress;
        }

        if (effectiveJurisdiction == JurisdictionType.EUROPEAN_UNION) {
            EuProfile euProfile = getOrSelectEuProfile();
            return buildAddressFromEuProfile(residentialAddress, euProfile);
        }

        NonEuProfile nonEuProfile = getOrSelectNonEuProfile();
        return buildAddressFromNonEuProfile(residentialAddress, nonEuProfile);
    }

    private EuProfile getOrSelectEuProfile() {
        if (addressProfileContext == null) {
            return FAKER.options().option(EU_PROFILES);
        }
        EuProfile euProfile = addressProfileContext.getEuProfile();
        if (euProfile == null) {
            euProfile = FAKER.options().option(EU_PROFILES);
            addressProfileContext.setEuProfile(euProfile);
        }
        return euProfile;
    }

    private NonEuProfile getOrSelectNonEuProfile() {
        if (addressProfileContext == null) {
            return FAKER.options().option(NON_EU_PROFILES);
        }
        NonEuProfile nonEuProfile = addressProfileContext.getNonEuProfile();
        if (nonEuProfile == null) {
            nonEuProfile = FAKER.options().option(NON_EU_PROFILES);
            addressProfileContext.setNonEuProfile(nonEuProfile);
        }
        return nonEuProfile;
    }

    private JurisdictionType getOrSelectUnitedKingdomProfileType() {
        if (addressProfileContext == null) {
            return FAKER.options().option(JurisdictionType.EUROPEAN_UNION, JurisdictionType.NON_EU);
        }
        JurisdictionType selectedProfileType = addressProfileContext.getSelectedUnitedKingdomProfileType();
        if (selectedProfileType == null) {
            selectedProfileType = FAKER.options().option(
                    JurisdictionType.EUROPEAN_UNION, JurisdictionType.NON_EU);
            addressProfileContext.setSelectedUnitedKingdomProfileType(selectedProfileType);
        }
        return selectedProfileType;
    }

    private String getOrSelectUkCountry(JurisdictionType jurisdiction) {
        if (addressProfileContext == null) {
            return resolveUkCountry(jurisdiction);
        }
        String selectedCountry = addressProfileContext.getSelectedUkCountry();
        if (selectedCountry == null) {
            selectedCountry = resolveUkCountry(jurisdiction);
            addressProfileContext.setSelectedUkCountry(selectedCountry);
        }
        return selectedCountry;
    }

    private String resolveUkCountry(JurisdictionType jurisdiction) {
        return switch (jurisdiction) {
            case ENGLAND_WALES -> {
                // Randomly pick between England and Wales
                JurisdictionType selected = FAKER.options().option(
                        JurisdictionType.ENGLAND,
                        JurisdictionType.WALES);
                yield selected == JurisdictionType.ENGLAND ? "England" : "Wales";
            }
            case ENGLAND -> "England";
            case WALES -> "Wales";
            case SCOTLAND -> "Scotland";
            case NI -> "Northern Ireland";
            default -> "United Kingdom";
        };
    }

    private UsualResidentialAddress buildAddressFromEuProfile(UsualResidentialAddress addr, EuProfile euProfile) {
        LocalityCluster cluster = FAKER.options().option(euProfile.clusters);
        addr.setAddressLine1(euProfile.faker.address().streetName().toUpperCase(Locale.UK));
        addr.setAddressLine2(cluster.area + " " + TEST_DATA_MARKER);
        addr.setCountry(euProfile.country);
        addr.setLocality(cluster.locality);
        addr.setPostalCode(generateEuPostcode(euProfile.postcodeType));
        addr.setRegion(cluster.region);
        return addr;
    }

    private UsualResidentialAddress buildAddressFromNonEuProfile(UsualResidentialAddress addr, NonEuProfile nonEuProfile) {
        LocalityCluster cluster = FAKER.options().option(nonEuProfile.clusters);
        addr.setAddressLine1(nonEuProfile.faker.address().streetName().toUpperCase(Locale.UK));
        addr.setAddressLine2(cluster.area + " " + TEST_DATA_MARKER);
        addr.setCountry(nonEuProfile.country);
        addr.setLocality(cluster.locality);
        addr.setPostalCode(generateNonEuPostcode(nonEuProfile.postcodeType));
        addr.setRegion(cluster.region);
        return addr;
    }

    private LocalityCluster getUkClusterForJurisdiction(JurisdictionType jurisdiction) {
        return switch (jurisdiction) {
            case ENGLAND_WALES -> {
                String selectedCountry = getOrSelectUkCountry(jurisdiction);
                LocalityCluster[] clusters = selectedCountry.equals("Wales") ? WALES_CLUSTERS : ENGLAND_CLUSTERS;
                yield FAKER.options().option(clusters);
            }
            case ENGLAND -> FAKER.options().option(ENGLAND_CLUSTERS);
            case WALES -> FAKER.options().option(WALES_CLUSTERS);
            case SCOTLAND -> FAKER.options().option(SCOTLAND_CLUSTERS);
            case NI -> FAKER.options().option(NI_CLUSTERS);
            case EUROPEAN_UNION, NON_EU, UNITED_KINGDOM ->
                    throw new IllegalStateException("Unexpected non-UK jurisdiction");
        };
    }

    private boolean isUkJurisdiction(JurisdictionType jurisdiction) {
        return switch (jurisdiction) {
            case ENGLAND_WALES, WALES, SCOTLAND, NI, ENGLAND -> true;
            case EUROPEAN_UNION, NON_EU, UNITED_KINGDOM -> false;
        };
    }

    private String generateUkPostcode() {
        return UK_SENTINEL_POSTCODE;
    }

    private String generateDutchPostcode() {
        return NETHERLANDS_SENTINEL_POSTCODE;
    }

    private String generateEuPostcode(EuPostcodeType postcodeType) {
        return switch (postcodeType) {
            case NETHERLANDS -> NETHERLANDS_SENTINEL_POSTCODE;
            case GERMANY -> "00000";
            case FRANCE -> "75000";
            case SPAIN -> "28000";
            case ITALY -> "00000";
            case POLAND -> "00-000";
            case MALTA -> generateMaltaPostcode();
            case CYPRUS -> generateCyprusPostcode();
        };
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

    private String generateJerseyPostcode() {
        return "JE1 1AA";
    }

    private String generateGuernseyPostcode() {
        return "GY1 1AA";
    }

    private String generateIsleOfManPostcode() {
        return "IM1 1AA";
    }

    private String generateMaltaPostcode() {
        return "VLT 1000";
    }

    private String generateCyprusPostcode() {
        return "1000";
    }

    private String generateBermudaPostcode() {
        return "HM 11";
    }

    private String generateNonEuPostcode(NonEuPostcodeType postcodeType) {
        return switch (postcodeType) {
            case PANAMA -> generatePanamaPostcode();
            case CANADA -> generateCanadaPostcode();
            case AUSTRALIA -> generateAustraliaPostcode();
            case JERSEY -> generateJerseyPostcode();
            case GUERNSEY -> generateGuernseyPostcode();
            case ISLE_OF_MAN -> generateIsleOfManPostcode();
            case BERMUDA -> generateBermudaPostcode();
        };
    }

}