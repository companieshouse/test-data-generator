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
import uk.gov.companieshouse.api.testdata.service.address.profile.LocalityCluster;
import uk.gov.companieshouse.api.testdata.service.address.profile.NonEuProfile;
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
    private static final String GUERNSEY = "Guernsey";
    private static final String ISLE_OF_MAN = "Isle of Man";
    private static final String MALTA = "Malta";
    private static final String CYPRUS = "Cyprus";
    private static final String BERMUDA = "Bermuda";
    private static final String TEST_DATA_MARKER = "TEST DATA";
    private static final String CANADIAN_POSTCODE_LETTERS = "ABCEGHJKLMNPRSTVWXYZ";
    private static final String DUTCH_POSTCODE_LETTERS = "ABCDEFGHJKLMNPRSTVWXZ";
    private static final String UK_POSTCODE_LETTERS = "ABDEFGHJLNPQRSTUWXYZ";
    private static final String[] BERMUDA_POSTCODE_PREFIXES = {
            "CR", "DD", "DV", "FL", "GE", "HA", "HM", "HS", "MA", "PG", "SB", "SN", "WK"
    };
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
            new EuProfile("Netherlands", NETHERLANDS_FAKER, NETHERLANDS_CLUSTERS),
            new EuProfile("Germany", GERMANY_FAKER, GERMANY_CLUSTERS),
            new EuProfile("France", FRANCE_FAKER, FRANCE_CLUSTERS),
            new EuProfile("Spain", SPAIN_FAKER, SPAIN_CLUSTERS),
            new EuProfile("Italy", ITALY_FAKER, ITALY_CLUSTERS),
            new EuProfile("Poland", POLAND_FAKER, POLAND_CLUSTERS),
            // Malta and Cyprus are EU member states (joined 2004), not non-EU jurisdictions.
            new EuProfile("Malta", FAKER, MALTA_CLUSTERS),
            new EuProfile("Cyprus", FAKER, CYPRUS_CLUSTERS)
    };

    private static final NonEuProfile[] NON_EU_PROFILES = {
            new NonEuProfile("Panama", PANAMA_FAKER, PANAMA_CLUSTERS),
            new NonEuProfile("Canada", CANADA_FAKER, CANADA_CLUSTERS),
            new NonEuProfile("Australia", AUSTRALIA_FAKER, AUSTRALIA_CLUSTERS),
            new NonEuProfile("Jersey", FAKER, JERSEY_CLUSTERS),
            new NonEuProfile("Guernsey", FAKER, GUERNSEY_CLUSTERS),
            new NonEuProfile("Isle of Man", FAKER, ISLE_OF_MAN_CLUSTERS),
            new NonEuProfile("Bermuda", FAKER, BERMUDA_CLUSTERS)
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
            residentialAddress.setPostalCode(generateUkPostcode(cluster, effectiveJurisdiction));
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
        addr.setPostalCode(generatePostcode(euProfile.country, cluster));
        addr.setRegion(cluster.region);
        return addr;
    }

    private UsualResidentialAddress buildAddressFromNonEuProfile(UsualResidentialAddress addr, NonEuProfile nonEuProfile) {
        LocalityCluster cluster = FAKER.options().option(nonEuProfile.clusters);
        addr.setAddressLine1(nonEuProfile.faker.address().streetName().toUpperCase(Locale.UK));
        addr.setAddressLine2(cluster.area + " " + TEST_DATA_MARKER);
        addr.setCountry(nonEuProfile.country);
        addr.setLocality(cluster.locality);
        addr.setPostalCode(generatePostcode(nonEuProfile.country, cluster));
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

    private String generatePostcode(String country, LocalityCluster cluster) {
        return switch (country) {
            case NETHERLANDS -> generateDutchPostcode(cluster.locality);
            case GERMANY -> generateGermanPostcode(cluster.locality);
            case FRANCE -> generateFrenchPostcode(cluster.locality);
            case SPAIN -> generateSpanishPostcode(cluster.locality);
            case ITALY -> generateItalianPostcode(cluster.locality);
            case POLAND -> generatePolishPostcode(cluster.locality);
            case MALTA -> getMaltaPostcodePrefix(cluster.locality) + " "
                    + FAKER.number().numberBetween(1000, 10000);
            case CYPRUS -> generateCyprusPostcode(cluster.locality);
            case PANAMA -> generatePanamaPostcode(cluster.locality);
            case CANADA -> generateCanadianPostcode(cluster.locality);
            case AUSTRALIA -> generateAustralianPostcode(cluster.locality);
            case JERSEY -> generateCrownDependencyPostcode(
                    getJerseyPostcodeArea(cluster.locality));
            case GUERNSEY -> generateCrownDependencyPostcode(
                    getGuernseyPostcodeArea(cluster.locality));
            case ISLE_OF_MAN -> generateCrownDependencyPostcode(
                    getIsleOfManPostcodeArea(cluster.locality));
            case BERMUDA -> generateBermudaPostcode(cluster.locality);
            default -> FAKER.address().zipCode();
        };
    }

    private String generateUkPostcode(
            LocalityCluster cluster, JurisdictionType jurisdiction) {
        String outwardCode = switch (cluster.locality) {
            case "LONDON" -> switch (cluster.area) {
                case "CAMDEN" -> "NW1";
                case "ISLINGTON" -> "N1";
                case "CHELSEA" -> "SW3";
                default -> FAKER.options().option("E1", "EC1", "N1", "NW1", "SE1", "SW1", "W1", "WC1");
            };
            case "MANCHESTER" -> switch (cluster.area) {
                case "DIDSBURY" -> "M20";
                case "CHORLTON" -> "M21";
                default -> "M1";
            };
            case "BIRMINGHAM" -> "EDGBASTON".equals(cluster.area) ? "B15" : "B13";
            case "BRISTOL" -> "CLIFTON".equals(cluster.area) ? "BS8" : "BS6";
            case "LEEDS" -> "LS6";
            case "LIVERPOOL" -> "TOXTETH".equals(cluster.area) ? "L8" : "L15";
            case "NEWCASTLE" -> "GOSFORTH".equals(cluster.area) ? "NE3" : "NE2";
            case "CAMBRIDGE" -> "NEWNHAM".equals(cluster.area) ? "CB3" : "CB4";
            case "OXFORD" -> "CITY CENTRE".equals(cluster.area) ? "OX1" : "OX2";
            case "YORK" -> "YO1";
            case "CHESTER" -> "CH1";
            case "BATH" -> "BA1";
            case "NOTTINGHAM" -> "NG1";
            case "LEICESTER" -> "LE1";
            case "COVENTRY" -> "CV1";
            case "BRIGHTON" -> "BN1";
            case "SOUTHAMPTON" -> "SO14";
            case "EXETER" -> "EX1";
            case "READING" -> "RG4";
            case "NORWICH" -> "NR1";
            case "CARDIFF" -> switch (cluster.area) {
                case "CATHAYS" -> "CF24";
                case "PONTCANNA", "GRANGETOWN" -> "CF11";
                default -> "CF10";
            };
            case "CAERPHILLY" -> "CF83";
            case "SWANSEA" -> "MUMBLES".equals(cluster.area) ? "SA3" : "SA2";
            case "CARMARTHEN" -> "SA31";
            case "NEWPORT" -> "ROGERSTONE".equals(cluster.area) ? "NP10" : "NP20";
            case "WREXHAM" -> "LL11";
            case "BANGOR" -> jurisdiction == JurisdictionType.NI ? "BT19" : "LL57";
            case "HOLYHEAD" -> "LL65";
            case "ABERYSTWYTH" -> "SY23";
            case "LLANDRINDOD WELLS" -> "LD1";
            case "EDINBURGH" -> switch (cluster.area) {
                case "LEITH" -> "EH6";
                case "MORNINGSIDE" -> "EH10";
                case "STOCKBRIDGE" -> "EH3";
                default -> "EH1";
            };
            case "GLASGOW" -> switch (cluster.area) {
                case "PARTICK" -> "G11";
                case "GOVAN" -> "G51";
                default -> "G1";
            };
            case "ABERDEEN" -> "AB10";
            case "DUNDEE" -> "BROUGHTY FERRY".equals(cluster.area) ? "DD5" : "DD1";
            case "STIRLING" -> "FK8";
            case "FALKIRK" -> "FK1";
            case "PERTH" -> "PH1";
            case "INVERNESS" -> "IV1";
            case "PAISLEY" -> "PA2";
            case "AYR" -> "KA7";
            case "BELFAST" -> switch (cluster.area) {
                case "BOTANIC", "ORMEAU" -> "BT7";
                case "TITANIC QUARTER" -> "BT3";
                default -> "BT1";
            };
            case "DERRY" -> FAKER.options().option("BT47", "BT48");
            case "LISBURN" -> "LAMBEG".equals(cluster.area) ? "BT27" : "BT28";
            case "NEWRY" -> "WARRENPOINT".equals(cluster.area) ? "BT34" : "BT35";
            case "ARMAGH" -> "BT61";
            case "OMAGH" -> "BT78";
            case "STRABANE" -> "BT82";
            case "COLERAINE" -> "BT52";
            case "ENNISKILLEN" -> "BT74";
            case "DOWNPATRICK" -> "BT30";
            default -> jurisdiction == JurisdictionType.NI
                    ? "BT1"
                    : FAKER.options().option(
                            "B1", "BS1", "CB1", "L1", "LE1", "LS1", "M1",
                            "NE1", "NG1", "OX1", "RG1", "SO14", "YO1");
        };
        return outwardCode + " "
                + FAKER.number().digit()
                + randomLetters(UK_POSTCODE_LETTERS, 2);
    }

    private String generateDutchPostcode(String locality) {
        int base = switch (locality) {
            case "AMSTERDAM" -> 1000;
            case "ALMERE" -> 1300;
            case "HAARLEM" -> 2000;
            case "LEIDEN" -> 2300;
            case "THE HAGUE" -> 2500;
            case "DELFT" -> 2600;
            case "ROTTERDAM" -> 3000;
            case "UTRECHT" -> 3500;
            case "BREDA" -> 4800;
            case "TILBURG" -> 5000;
            case "EINDHOVEN" -> 5600;
            case "MAASTRICHT" -> 6200;
            case "NIJMEGEN" -> 6500;
            case "ARNHEM" -> 6800;
            case "GRONINGEN" -> 9700;
            default -> FAKER.number().numberBetween(1000, 9900);
        };
        return (base + FAKER.number().numberBetween(0, 100))
                + " " + randomLetters(DUTCH_POSTCODE_LETTERS, 2);
    }

    private String generateGermanPostcode(String locality) {
        int[] range = switch (locality) {
            case "DRESDEN" -> new int[]{1067, 1329};
            case "LEIPZIG" -> new int[]{4100, 4400};
            case "BERLIN" -> new int[]{10115, 14200};
            case "HAMBURG" -> new int[]{20095, 22770};
            case "BREMEN" -> new int[]{28195, 28780};
            case "DÜSSELDORF" -> new int[]{40200, 40700};
            case "COLOGNE" -> new int[]{50600, 51200};
            case "FRANKFURT" -> new int[]{60300, 60600};
            case "HEIDELBERG" -> new int[]{69100, 69200};
            case "STUTTGART" -> new int[]{70100, 70700};
            case "MUNICH" -> new int[]{80300, 82000};
            default -> new int[]{1000, 100000};
        };
        return formatFiveDigitPostcode(randomInRange(range));
    }

    private String generateFrenchPostcode(String locality) {
        int[] range = switch (locality) {
            case "NICE" -> new int[]{6000, 6300};
            case "MARSEILLE" -> new int[]{13001, 13017};
            case "TOULOUSE" -> new int[]{31000, 31600};
            case "BORDEAUX" -> new int[]{33000, 33900};
            case "MONTPELLIER" -> new int[]{34000, 34900};
            case "RENNES" -> new int[]{35000, 35900};
            case "NANTES" -> new int[]{44000, 44900};
            case "LILLE" -> new int[]{59000, 59900};
            case "STRASBOURG" -> new int[]{67000, 67900};
            case "LYON" -> new int[]{69001, 69010};
            case "PARIS" -> new int[]{75001, 75021};
            default -> new int[]{1000, 96000};
        };
        return formatFiveDigitPostcode(randomInRange(range));
    }

    private String generateSpanishPostcode(String locality) {
        String province = switch (locality) {
            case "ALICANTE" -> "03";
            case "PALMA" -> "07";
            case "BARCELONA" -> "08";
            case "GRANADA" -> "18";
            case "MADRID" -> "28";
            case "MALAGA" -> "29";
            case "SEVILLE" -> "41";
            case "VALENCIA" -> "46";
            case "BILBAO" -> "48";
            case "ZARAGOZA" -> "50";
            default -> String.format(
                    Locale.UK, "%02d", FAKER.number().numberBetween(1, 53));
        };
        return province + FAKER.numerify("###");
    }

    private String generateItalianPostcode(String locality) {
        String prefix = switch (locality) {
            case "ROME" -> "001";
            case "TURIN" -> "101";
            case "GENOA" -> "161";
            case "MILAN" -> "201";
            case "VENICE" -> "301";
            case "BOLOGNA" -> "401";
            case "FLORENCE" -> "501";
            case "NAPLES" -> "801";
            case "PALERMO" -> "901";
            default -> FAKER.numerify("###");
        };
        return prefix + FAKER.numerify("##");
    }

    private String generatePolishPostcode(String locality) {
        String area = switch (locality) {
            case "WARSAW" -> FAKER.options().option("00", "01", "02");
            case "LUBLIN" -> "20";
            case "KRAKOW" -> FAKER.options().option("30", "31");
            case "WROCLAW" -> FAKER.options().option("50", "51", "52", "53", "54");
            case "POZNAŃ" -> FAKER.options().option("60", "61");
            case "SZCZECIN" -> FAKER.options().option("70", "71");
            case "GDANSK" -> "80";
            case "TORUŃ" -> "87";
            case "ŁÓDŹ" -> FAKER.options().option("90", "91", "92", "93", "94");
            default -> FAKER.numerify("##");
        };
        return area + "-" + FAKER.numerify("###");
    }

    private String generateCyprusPostcode(String locality) {
        int[] range = switch (locality) {
            case "NICOSIA" -> new int[]{1000, 3000};
            case "LIMASSOL" -> new int[]{3000, 5000};
            case "FAMAGUSTA", "AYIA NAPA", "PARALIMNI" -> new int[]{5000, 6000};
            case "LARNACA" -> new int[]{6000, 8000};
            case "PAPHOS", "POLIS" -> new int[]{8000, 9000};
            case "KYRENIA", "MORFOU" -> new int[]{9000, 10000};
            default -> new int[]{1000, 10000};
        };
        return String.valueOf(randomInRange(range));
    }

    private String generatePanamaPostcode(String locality) {
        int base = switch (locality) {
            case "BOCAS DEL TORO" -> 100;
            case "PENONOME" -> 200;
            case "COLON" -> 300;
            case "DAVID" -> 400;
            case "LA PALMA" -> 500;
            case "CHITRE" -> 600;
            case "LAS TABLAS" -> 700;
            case "PANAMA CITY" -> 800;
            case "SANTIAGO" -> 900;
            default -> 0;
        };
        return String.format(
                Locale.UK, "%04d", base + FAKER.number().numberBetween(1, 100));
    }

    private int randomInRange(int[] range) {
        return FAKER.number().numberBetween(range[0], range[1]);
    }

    private String formatFiveDigitPostcode(int postcode) {
        return String.format(Locale.UK, "%05d", postcode);
    }

    private String generateCanadianPostcode(String locality) {
        String forwardSortationArea = switch (locality) {
            case "TORONTO" -> "M" + FAKER.number().numberBetween(1, 10);
            case "OTTAWA" -> "K" + FAKER.number().numberBetween(1, 3);
            case "HAMILTON" -> "L" + FAKER.number().numberBetween(8, 10);
            case "KITCHENER" -> "N2";
            case "LONDON" -> "N" + FAKER.number().numberBetween(5, 7);
            case "WINDSOR" -> "N" + FAKER.number().numberBetween(8, 10);
            case "VANCOUVER" -> "V" + FAKER.number().numberBetween(5, 7);
            case "VICTORIA" -> "V" + FAKER.number().numberBetween(8, 10);
            case "SURREY" -> "V3";
            case "BURNABY" -> "V5";
            case "RICHMOND" -> "V" + FAKER.number().numberBetween(6, 8);
            case "KELOWNA" -> "V1";
            case "CALGARY" -> "T" + FAKER.number().numberBetween(1, 4);
            case "EDMONTON" -> "T" + FAKER.number().numberBetween(5, 7);
            case "WINNIPEG" -> "R" + FAKER.number().numberBetween(2, 4);
            case "SASKATOON" -> "S7";
            case "REGINA" -> "S4";
            case "HALIFAX" -> "B3";
            case "SYDNEY" -> "B1";
            case "DARTMOUTH" -> "B2";
            case "MONTREAL" -> "H" + FAKER.number().numberBetween(1, 6);
            case "QUEBEC CITY" -> "G" + FAKER.number().numberBetween(1, 3);
            case "LAVAL" -> "H7";
            case "GATINEAU" -> "J" + FAKER.number().numberBetween(8, 10);
            case "SHERBROOKE" -> "J1";
            case "TROIS-RIVIERES" -> "G" + FAKER.number().numberBetween(8, 10);
            default -> randomLetters(CANADIAN_POSTCODE_LETTERS, 1)
                    + FAKER.number().digit();
        };
        return forwardSortationArea
                + randomLetters(CANADIAN_POSTCODE_LETTERS, 1)
                + " "
                + FAKER.number().digit()
                + randomLetters(CANADIAN_POSTCODE_LETTERS, 1)
                + FAKER.number().digit();
    }

    private String generateAustralianPostcode(String locality) {
        int[] range = switch (locality) {
            case "SYDNEY" -> new int[]{2000, 2240};
            case "NEWCASTLE" -> new int[]{2280, 2320};
            case "WOLLONGONG" -> new int[]{2500, 2531};
            case "WAGGA WAGGA" -> new int[]{2650, 2670};
            case "MELBOURNE" -> new int[]{3000, 3210};
            case "GEELONG" -> new int[]{3211, 3221};
            case "BALLARAT" -> new int[]{3350, 3361};
            case "BENDIGO" -> new int[]{3550, 3561};
            case "BRISBANE" -> new int[]{4000, 4180};
            case "GOLD COAST" -> new int[]{4200, 4231};
            case "SUNSHINE COAST" -> new int[]{4550, 4582};
            case "TOWNSVILLE" -> new int[]{4810, 4821};
            case "ADELAIDE" -> new int[]{5000, 5200};
            case "MOUNT GAMBIER" -> new int[]{5290, 5301};
            case "PORT AUGUSTA" -> new int[]{5700, 5711};
            case "PERTH" -> new int[]{6000, 6210};
            case "FREMANTLE" -> new int[]{6157, 6164};
            case "BUNBURY" -> new int[]{6230, 6241};
            case "ALBANY" -> new int[]{6330, 6341};
            case "HOBART" -> new int[]{7000, 7110};
            case "LAUNCESTON" -> new int[]{7248, 7260};
            case "DEVONPORT" -> new int[]{7310, 7321};
            case "BURNIE" -> new int[]{7320, 7331};
            case "DARWIN" -> new int[]{800, 900};
            case "PALMERSTON" -> new int[]{830, 841};
            case "ALICE SPRINGS" -> new int[]{870, 880};
            case "CANBERRA" -> new int[]{2600, 2620};
            case "TUGGERANONG" -> new int[]{2900, 2915};
            default -> new int[]{800, 10000};
        };
        return String.format(
                Locale.UK, "%04d", FAKER.number().numberBetween(range[0], range[1]));
    }

    private String getMaltaPostcodePrefix(String locality) {
        return switch (locality) {
            case "VALLETTA" -> "VLT";
            case "SLIEMA" -> "SLM";
            case "MOSTA" -> "MST";
            case "BIRKIRKARA" -> "BKR";
            case "QORMI" -> "QRM";
            case "NAXXAR" -> "NXR";
            case "MELLIEHA" -> "MLH";
            case "BIRGU" -> "BRG";
            case "MARSASKALA" -> "MSK";
            case "RABAT" -> "RBT";
            default -> randomLetters(UK_POSTCODE_LETTERS, 3);
        };
    }

    private String generateCrownDependencyPostcode(String outwardCode) {
        return outwardCode + " "
                + FAKER.number().digit()
                + randomLetters(UK_POSTCODE_LETTERS, 2);
    }

    private String getJerseyPostcodeArea(String locality) {
        return switch (locality) {
            case "ST. HELIER" -> FAKER.options().option("JE1", "JE2");
            case "ST. CLEMENT", "ST. SAVIOUR" -> "JE2";
            case "ST. BRELADE", "ST. LAWRENCE", "ST. MARTIN", "ST. OUEN", "ST. PETER" -> "JE3";
            default -> "JE1";
        };
    }

    private String getGuernseyPostcodeArea(String locality) {
        return switch (locality) {
            case "ST. PETER PORT" -> "GY1";
            case "ST. SAMPSON" -> "GY2";
            case "VALE" -> "GY3";
            case "ST. MARTIN" -> "GY4";
            case "CASTEL" -> "GY5";
            case "ST. ANDREW" -> "GY6";
            case "FOREST", "TORTEVAL" -> "GY8";
            default -> "GY1";
        };
    }

    private String getIsleOfManPostcodeArea(String locality) {
        return switch (locality) {
            case "DOUGLAS" -> FAKER.options().option("IM1", "IM2");
            case "LAXEY" -> "IM4";
            case "PEEL" -> "IM5";
            case "KIRK MICHAEL" -> "IM6";
            case "RAMSEY" -> "IM8";
            case "CASTLETOWN", "PORT ERIN", "PORT ST. MARY" -> "IM9";
            default -> "IM1";
        };
    }

    private String generateBermudaPostcode(String locality) {
        String prefix = switch (locality) {
            case "HAMILTON", "PEMBROKE" -> "HM";
            case "ST. GEORGE" -> "GE";
            case "DOCKYARD", "SOMERSET" -> FAKER.options().option("MA", "SB");
            case "DEVONSHIRE" -> FAKER.options().option("DD", "DV");
            case "WARWICK" -> "WK";
            case "SOUTHAMPTON" -> "SN";
            case "SMITHS" -> "FL";
            case "PAGET" -> "PG";
            default -> FAKER.options().option(BERMUDA_POSTCODE_PREFIXES);
        };
        return String.format(
                Locale.UK, "%s %02d", prefix, FAKER.number().numberBetween(1, 100));
    }

    private String randomLetters(String allowedLetters, int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(allowedLetters.charAt(
                    FAKER.number().numberBetween(0, allowedLetters.length())));
        }
        return result.toString();
    }

}