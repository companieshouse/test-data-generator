package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FilingHistoryDescriptionType {

    ACCOUNTS_BALANCE_SHEET("accounts-balance-sheet"),
    ACCOUNTS_WITH_ACCOUNTS_TYPE_FULL("accounts-with-accounts-type-full"),
    ACCOUNTS_WITH_ACCOUNTS_TYPE_GROUP("accounts-with-accounts-type-group"),
    ACCOUNTS_WITH_ACCOUNTS_TYPE_MICRO_ENTITY("accounts-with-accounts-type-micro-entity"),
    ACCOUNTS_WITH_ACCOUNTS_TYPE_SMALL("accounts-with-accounts-type-small"),
    ACCOUNTS_WITH_ACCOUNTS_TYPE_TOTAL_EXEMPTION_FULL("accounts-with-accounts-type-total-exemption-full"),
    ACCOUNTS_WITH_ACCOUNTS_TYPE_UNAUDITED_ABRIDGED("accounts-with-accounts-type-unaudited-abridged"),
    ANNUAL_RETURN_COMPANY_WITH_MADE_UP_DATE_FULL_LIST_SHAREHOLDERS("annual-return-company-with-made-up-date-full-list-shareholders"),
    ANNUAL_UPDATE("annual-update"),
    ANNUAL_UPDATE_WITH_MADE_UP_DATE("annual-update-with-made-up-date"),
    APPOINT_CORPORATE_DIRECTOR_COMPANY_WITH_NAME_DATE("appoint-corporate-director-company-with-name-date"),
    APPOINT_CORPORATE_MEMBER_LIMITED_LIABILITY_PARTNERSHIP_WITH_APPOINTMENT_DATE("appoint-corporate-member-limited-liability-partnership-with-appointment-date"),
    APPOINT_JUDICIAL_FACTOR_WITH_NAME("appoint-judicial-factor-with-name"),
    APPOINT_PERSON_DIRECTOR_COMPANY_WITH_NAME("appoint-person-director-company-with-name"),
    APPOINT_PERSON_DIRECTOR_COMPANY_WITH_NAME_DATE("appoint-person-director-company-with-name-date"),
    CAPITAL_ALLOTMENT_SHARES("capital-allotment-shares"),
    CAPITAL_STATEMENT_CAPITAL_COMPANY_WITH_DATE_CURRENCY_FIGURE("capital-statement-capital-company-with-date-currency-figure"),
    CERTIFICATE_CHANGE_OF_NAME_COMPANY("certificate-change-of-name-company"),
    CESSATION_OF_A_PERSON_WITH_SIGNIFICANT_CONTROL("cessation-of-a-person-with-significant-control"),
    CESSATION_OF_A_PERSON_WITH_SIGNIFICANT_CONTROL_LIMITED_LIABILITY_PARTNERSHIP("cessation-of-a-person-with-significant-control-limited-liability-partnership"),
    CHANGE_ACCOUNT_REFERENCE_DATE_COMPANY_CURRENT_EXTENDED("change-account-reference-date-company-current-extended"),
    CHANGE_CONSTITUTIONAL_DOCUMENTS_OVERSEAS_COMPANY_WITH_DATE("change-constitutional-documents-overseas-company-with-date"),
    CHANGE_PERSON_DIRECTOR_COMPANY_WITH_CHANGE_DATE("change-person-director-company-with-change-date"),
    CHANGE_REGISTERED_OFFICE_ADDRESS_COMPANY_WITH_DATE_OLD_ADDRESS_NEW_ADDRESS("change-registered-office-address-company-with-date-old-address-new-address"),
    CHANGE_REGISTERED_OFFICE_ADDRESS_LIMITED_LIABILITY_PARTNERSHIP_WITH_DATE_OLD_ADDRESS_NEW_ADDRESS("change-registered-office-address-limited-liability-partnership-with-date-old-address-new-address"),
    CHANGE_SAIL_ADDRESS_LIMITED_LIABILITY_PARTNERSHIP_WITH_OLD_ADDRESS_NEW_ADDRESS("change-sail-address-limited-liability-partnership-with-old-address-new-address"),
    CHANGE_TO_A_PERSON_WITH_SIGNIFICANT_CONTROL("change-to-a-person-with-significant-control"),
    CONFIRMATION_STATEMENT("confirmation-statement"),
    CONFIRMATION_STATEMENT_WITH_NO_UPDATES("confirmation-statement-with-no-updates"),
    CONFIRMATION_STATEMENT_WITH_UPDATES("confirmation-statement-with-updates"),
    DEFAULT_COMPANIES_HOUSE_REGISTERED_OFFICE_ADDRESS_APPLIED("default-companies-house-registered-office-address-applied"),
    DEFAULT_COMPANIES_HOUSE_SERVICE_ADDRESS_APPLIED_MEMBER("default-companies-house-service-address-applied-member"),
    DEFAULT_COMPANIES_HOUSE_SERVICE_ADDRESS_APPLIED_OFFICER("default-companies-house-service-address-applied-officer"),
    DEFAULT_COMPANIES_HOUSE_SERVICE_ADDRESS_APPLIED_PSC("default-companies-house-service-address-applied-psc"),
    DISSOLUTION_APPLICATION_STRIKE_OFF_COMPANY("dissolution-application-strike-off-company"),
    ELECT_TO_KEEP_THE_DIRECTORS_REGISTER_INFORMATION_ON_THE_PUBLIC_REGISTER("elect-to-keep-the-directors-register-information-on-the-public-register"),
    ELECT_TO_KEEP_THE_LIMITED_LIABILITY_PARTNERSHIP_MEMBERS_RESIDENTIAL_ADDRESS_REGISTER_INFORMATION_ON_THE_PUBLIC_REGISTER("elect-to-keep-the-limited-liability-partnership-members-residential-address-register-information-on-the-public-register"),
    GAZETTE_DISSOLVED_LIQUIDATION("gazette-dissolved-liquidation"),
    GAZETTE_NOTICE_VOLUNTARY("gazette-notice-voluntary"),
    INCORPORATION_COMPANY("incorporation-company"),
    LEGACY("legacy"),
    LIQUIDATION_APPOINTMENT_OF_LIQUIDATOR("liquidation-appointment-of-liquidator"),
    LIQUIDATION_CEASE_TO_ACT_AS_LIQUIDATOR_NORTHERN_IRELAND("liquidation-cease-to-act-as-liquidator-northern-ireland"),
    LIQUIDATION_IN_ADMINISTRATION_APPOINTMENT_OF_ADMINISTRATOR("liquidation-in-administration-appointment-of-administrator"),
    LIQUIDATION_IN_ADMINISTRATION_NOTICE_ADMINISTRATORS_PROPOSALS_SCOTLAND("liquidation-in-administration-notice-administrators-proposals-scotland"),
    LIQUIDATION_IN_ADMINISTRATION_PROGRESS_REPORT("liquidation-in-administration-progress-report"),
    LIQUIDATION_IN_ADMINISTRATION_REVISION_ADMINISTRATORS_PROPOSALS("liquidation-in-administration-revision-administrators-proposals"),
    LIQUIDATION_MORATORIUM_COMMENCEMENT_OF_MORATORIUM("liquidation-moratorium-commencement-of-moratorium"),
    LIQUIDATION_MORATORIUM_END_OF_MORATORIUM_BY_MONITOR("liquidation-moratorium-end-of-moratorium-by-monitor"),
    LIQUIDATION_MORATORIUM_END_OF_MORATORIUM_FOLLOWING_DISPOSAL_OF_APPLICATION_FOR_EXTENSION_BY_COURT_OR_FOLLOWING_CVA_PROPOSAL_TAKING_EFFECT_OR_BEING_WITHDRAWN("liquidation-moratorium-end-of-moratorium-following-disposal-of-application-for-extension-by-court-or-following-cva-proposal-taking-effect-or-being-withdrawn"),
    LIQUIDATION_MORATORIUM_EXTENSION_OF_MORATORIUM("liquidation-moratorium-extension-of-moratorium"),
    LIQUIDATION_RECEIVER_APPOINTMENT_OF_RECEIVER("liquidation-receiver-appointment-of-receiver"),
    LIQUIDATION_VOLUNTARY_APPOINTMENT_OF_LIQUIDATOR("liquidation-voluntary-appointment-of-liquidator"),
    LIQUIDATION_VOLUNTARY_CREDITORS_PAID_IN_FULL("liquidation-voluntary-creditors-paid-in-full"),
    LIQUIDATION_VOLUNTARY_CREDITORS_RETURN_OF_FINAL_MEETING("liquidation-voluntary-creditors-return-of-final-meeting"),
    LIQUIDATION_VOLUNTARY_MEMBERS_RETURN_OF_FINAL_MEETING("liquidation-voluntary-members-return-of-final-meeting"),
    LIQUIDATION_VOLUNTARY_STATEMENT_OF_AFFAIRS("liquidation-voluntary-statement-of-affairs"),
    LIQUIDATION_VOLUNTARY_STATEMENT_OF_RECEIPTS_AND_PAYMENTS_WITH_BROUGHT_DOWN_DATE("liquidation-voluntary-statement-of-receipts-and-payments-with-brought-down-date"),
    MEMORANDUM_ARTICLES("memorandum-articles"),
    MORTGAGE_CREATE_WITH_DEED_WITH_CHARGE_NUMBER("mortgage-create-with-deed-with-charge-number"),
    MORTGAGE_CREATE_WITH_DEED_WITH_CHARGE_NUMBER_CHARGE_CREATION_DATE("mortgage-create-with-deed-with-charge-number-charge-creation-date"),
    MOVE_REGISTERS_TO_SAIL_LIMITED_LIABILITY_PARTNERSHIP_WITH_NEW_ADDRESS("move-registers-to-sail-limited-liability-partnership-with-new-address"),
    NOTIFICATION_OF_A_PERSON_WITH_SIGNIFICANT_CONTROL("notification-of-a-person-with-significant-control"),
    NOTIFICATION_OF_A_PERSON_WITH_SIGNIFICANT_CONTROL_LIMITED_LIABILITY_PARTNERSHIP("notification-of-a-person-with-significant-control-limited-liability-partnership"),
    NOTIFICATION_OF_A_PERSON_WITH_SIGNIFICANT_CONTROL_STATEMENT("notification-of-a-person-with-significant-control-statement"),
    NOTIFICATION_OF_A_PERSON_WITH_SIGNIFICANT_CONTROL_STATEMENT_SCOTTISH_LIMITED_PARTNERSHIP("notification-of-a-person-with-significant-control-statement-scottish-limited-partnership"),
    NOTIFICATION_OF_A_PERSON_WITH_SIGNIFICANT_CONTROL_WITHOUT_NAME_DATE("notification-of-a-person-with-significant-control-without-name-date"),
    REGISTRATION_OF_A_LIMITED_PARTNERSHIP("registration-of-a-limited-partnership"),
    REGISTRATION_OVERSEAS_ENTITY("registration-overseas-entity"),
    REMOVAL_OVERSEAS_ENTITY("removal-overseas-entity"),
    RESOLUTION("resolution"),
    SELECTION_OF_DOCUMENTS_REGISTERED_BEFORE_APRIL_2014("selection-of-documents-registered-before-April-2014"),
    TERMINATION_DIRECTOR_COMPANY_WITH_NAME("termination-director-company-with-name"),
    TERMINATION_DIRECTOR_COMPANY_WITH_NAME_TERMINATION_DATE("termination-director-company-with-name-termination-date"),
    TERMINATION_MEMBER_LIMITED_LIABILITY_PARTNERSHIP_WITH_NAME_TERMINATION_DATE("termination-member-limited-liability-partnership-with-name-termination-date"),
    TERMINATION_SECRETARY_COMPANY_WITH_NAME_TERMINATION_DATE("termination-secretary-company-with-name-termination-date"),
    WITHDRAWAL_OF_A_PERSON_WITH_SIGNIFICANT_CONTROL_STATEMENT_SCOTTISH_LIMITED_PARTNERSHIP("withdrawal-of-a-person-with-significant-control-statement-scottish-limited-partnership");

    private final String value;

    FilingHistoryDescriptionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
