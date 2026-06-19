package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.service.AddressService;

@Service
public class AddressServiceImpl implements AddressService {

    private static final String UNITED_KINGDOM = "United Kingdom";

    private static final Address ENGLAND_WALES_ADDRESS = new Address(
            "House Name",
            "Old Admiralty Building",
            "Admiralty Place",
            UNITED_KINGDOM,
            "London",
            "SW1A 2DY"
    );

    private static final Address SCOTLAND_ADDRESS = new Address(
            "Building Name",
            "Queen Elizabeth House",
            "1 Sibbald Walk",
            UNITED_KINGDOM,
            "Edinburgh",
            "EH8 8FT"
    );

    private static final Address NI_ADDRESS = new Address(
            "Office Name",
            "Erskine House",
            "20-32 Chichester Street",
            UNITED_KINGDOM,
            "Belfast",
            "BT1 4GF"
    );

    private static final Address ENGLAND_ADDRESS = new Address(
            "Business Centre",
            "4th Floor, The Linen Hall",
            "162-168 Regent Street",
            UNITED_KINGDOM,
            "London",
            "W1B 5TF"
    );

    private static final Address WALES_ADDRESS = new Address(
            "Business Hub",
            "Tŷ William Morgan",
            "6 Central Square",
            UNITED_KINGDOM,
            "Cardiff",
            "CF10 1EP"
    );

    private static final Address EUROPEAN_UNION_ADDRESS = new Address(
            "Business Place",
            "Schiphol Boulevard Tower 403 Tower C-4",
            "1118bk Schiphol",
            "Netherlands",
            "Amsterdam",
            "123123"
    );

    private static final Address NON_EU_ADDRESS = new Address(
            "Business House",
            "Edificio Salduba Tercer Piso",
            "Calle 53 Este",
            "Panama",
            "Marbella",
            "123124"
    );

    private static final Address OVERSEAS_ADDRESS = new Address(
            "Gordon's Centre",
            "Gordon Cummins Hwy",
            "Grantley Adams International Airport",
            "Barbados",
            "Christ Church",
            "123125"
    );

    public Address getOverseasAddress() {
        return OVERSEAS_ADDRESS;
    }

    @Override
    public Address getAddress(JurisdictionType jurisdiction) {
        return switch (jurisdiction) {
            case ENGLAND_WALES -> ENGLAND_WALES_ADDRESS;
            case WALES -> WALES_ADDRESS;
            case SCOTLAND -> SCOTLAND_ADDRESS;
            case NI -> NI_ADDRESS;
            case UNITED_KINGDOM -> OVERSEAS_ADDRESS;
            case ENGLAND -> ENGLAND_ADDRESS;
            case EUROPEAN_UNION -> EUROPEAN_UNION_ADDRESS;
            case NON_EU -> NON_EU_ADDRESS;
        };
    }

    @Override
    public String getCountryOfResidence(JurisdictionType jurisdiction) {
        return switch (jurisdiction) {
            case ENGLAND_WALES, ENGLAND -> "England";
            case WALES -> "Wales";
            case SCOTLAND -> "Scotland";
            case NI -> "Northern Ireland";
            case UNITED_KINGDOM -> "Barbados";
            case NON_EU -> "Panama";
            case EUROPEAN_UNION -> "Netherlands";
        };
    }
}