package uk.gov.companieshouse.api.testdata.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.company.*;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.dto.AccountParameters;
import uk.gov.companieshouse.api.testdata.model.dto.CompanyDetailsParameters;
import uk.gov.companieshouse.api.testdata.model.dto.DateParameters;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileInternalService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

@Service
public class CompanyProfileInternalServiceImpl implements CompanyProfileInternalService {
    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(CompanyProfileServiceImpl.class));
    private static final String LINK_STEM = "/company/";
    private static final String FILLING_HISTORY_STEM = "/filing-history";
    private static final String OFFICERS_STEM = "/officers";
    private static final String PSC_STATEMENT_STEM = "/persons-with-significant-control-statement";
    private static final String REGISTERS_STEM = "/registers";
    private static final String COMPANY_STATUS_REGISTERED = "registered";
    private static final String OVERSEAS_ENTITY_TYPE = "registered-overseas-entity";
    private static final String OVERSEA_COMPANY_TYPE = "oversea-company";
    private static final String EXT_REGISTRATION_NUMBER = "124234R34";
    private static final String COMPANY_NAME_PREFIX = "COMPANY ";
    private static final String COMPANY_NAME_SUFFIX = " LIMITED";
    private static final String FCD_COUNTRY = "Barbados";
    private static final String LEGAL_FORM = "Plc";
    private static final String BUSINESS_ACTIVITY = "Trading";
    private static final String ORIGINATING_REGISTRY_NAME = "Barbados Financial Services";
    private static final String UPDATED_TYPE = "psc_delta";
    private static final String FOREIGN_ACCOUNT_TYPE = "ForeignAccountType";
    private static final String TERMS_OF_PUBLICATION = "Terms of Account Publication";
    private static final String GOVERNED_BY = "Federal Government";
    public static final String
            FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY =
            "full-data-available-from-financial-conduct-authority";
    public static final String
            FULL_DATA_AVAILABLE_FROM_THE_COMPANY = "full-data-available-from-the-company";
    public static final String
            FULL_DATA_AVAILABLE_FROM_DEPARTMENT_OF_THE_ECONOMY =
            "full-data-available-from-department-of-the-economy";
    public static final String
            FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY_MUTUALS_PUBLIC_REGISTER =
            "full-data-available-from-financial-conduct-authority-mutuals-public-register";

    private static final String COMPANY_PROFILE_URI = "/company/%s/internal";

    @Autowired
    private RandomService randomService;

    @Autowired
    private AddressService addressService;

    private boolean hasCompanyRegisters = false;

    private boolean isCompanyTypeHasNoFilingHistory = true;

    private final Supplier<InternalApiClient> internalApiClientSupplier;

    public CompanyProfileInternalServiceImpl(Supplier<InternalApiClient> internalApiClientSupplier) {
        this.internalApiClientSupplier = internalApiClientSupplier;
    }

    @Override
    public void createCompanyProfileData(CompanySpec spec, String deltaAt) throws DataException, JsonProcessingException {
        final String companyNumber = spec.getCompanyNumber();
        final Jurisdiction jurisdiction = spec.getJurisdiction();
        final CompanyType companyType = spec.getCompanyType();
        final String subType = spec.getSubType();
        final Boolean hasSuperSecurePscs = spec.getHasSuperSecurePscs();
        final String companyStatusDetail = spec.getCompanyStatusDetail();
        final String companyStatus = spec.getCompanyStatus();
        final String accountsDueStatus = spec.getAccountsDueStatus();
        final Boolean registeredOfficeIsInDispute = spec.getRegisteredOfficeIsInDispute();

        var accountParams = new AccountParameters(accountsDueStatus, randomService);
        var dateParams = new DateParameters(accountParams.getAccountingReferenceDate());
        var companyParams = new CompanyDetailsParameters(
                companyType, hasSuperSecurePscs, companyStatus, subType,
                companyStatusDetail, registeredOfficeIsInDispute);

        String companyTypeValue = companyParams.getCompanyType()
                != null ? companyParams.getCompanyType().getValue() : "ltd";
        String nonJurisdictionType = (jurisdiction != null)
                ? checkNonJurisdictionTypes(jurisdiction, companyTypeValue) : "";

        Data data = new Data();
        data.setAccounts(createAccounts(accountParams,dateParams));
        data.setCanFile(true);
        if (spec.getCompanyName() != null) {
            data.setCompanyName(spec.getCompanyName() + " " + companyNumber);
        } else {
            setCompanyName(data, companyNumber, companyTypeValue);
        }
        data.setCompanyNumber(companyNumber);
        createConfirmationStatement(data, dateParams, accountParams);
        data.setDateOfCreation(dateParams.getDateOneYearAgo()
                .atZone(ZoneId.systemDefault()).toLocalDate());
        data.setEtag(randomService.getEtag());
        data.setHasBeenLiquidated(false);
        data.setHasCharges(false);
        data.setJurisdiction(String.valueOf(jurisdiction));
        setCompanyStatus(data, companyStatus, companyTypeValue);
        setRegisteredAddress(data, jurisdiction, nonJurisdictionType);
        data.setLinks(nonJurisdictionType.isEmpty()
                ? createLinkForSelf(companyNumber) : createLinks(companyNumber));

        data.setType(companyTypeValue);
        data.setUndeliverableRegisteredOfficeAddress(BooleanUtils.isTrue(spec.getUndeliverableRegisteredOfficeAddress()));
        data.setHasSuperSecurePscs(BooleanUtils.isTrue(companyParams.getHasSuperSecurePscs()));

        setPartialDataOptions(data, jurisdiction, companyParams.getCompanyType());
        setSubType(data, companyParams.getSubType());
        setCompanyStatusDetail(data, companyParams.getCompanyStatusDetail(), companyTypeValue);

        CompanyProfile companyProfile = new CompanyProfile();
        companyProfile.setHasMortgages(false);
        companyProfile.setDeltaAt(deltaAt);
        companyProfile.setData(data);
        createCompanyProfile(companyNumber, companyProfile);
    }

    @Override
    public void deleteCompanyProfileData(String deltaAt, String companyNumber) throws DataException {
        try {
            String formattedCompanyProfileUri = formatUri(COMPANY_PROFILE_URI, companyNumber);
            internalApiClientSupplier.get()
                    .privateDeltaResourceHandler()
                    .deleteCompanyProfile(formattedCompanyProfileUri, deltaAt)
                    .execute();
        } catch (ApiErrorResponseException | URIValidationException ex) {
            LOG.error("Failed to delete company profile for company number: " + companyNumber, ex);
            throw new DataException("Failed to delete company profile: " + ex.getMessage(), ex);
        }
    }

    private String checkNonJurisdictionTypes(Jurisdiction jurisdiction, String companyType) {
        Set<String> noJurisdictionTypes = Set.of(
                CompanyType.REGISTERED_SOCIETY_NON_JURISDICTIONAL.getValue(),
                CompanyType.ROYAL_CHARTER.getValue(),
                CompanyType.UK_ESTABLISHMENT.getValue()
        );
        if (jurisdiction == null) {
            return "";
        }
        return (companyType != null && noJurisdictionTypes.contains(companyType))
                ? "" : jurisdiction.toString();
    }

    private void setRegisteredAddress(Data data,
                                      Jurisdiction jurisdiction,
                                      String nonJurisdictionType) {
        if (jurisdiction != null && !nonJurisdictionType.isEmpty()) {
            var address = addressService.getAddress(jurisdiction);
            RegisteredOfficeAddress registeredOfficeAddress = new RegisteredOfficeAddress();
            registeredOfficeAddress.setAddressLine1(address.getAddressLine1());
            registeredOfficeAddress.setAddressLine2(address.getAddressLine2());
            registeredOfficeAddress.setCountry(address.getCountry());
            registeredOfficeAddress.setLocality(address.getLocality());
            registeredOfficeAddress.setPostalCode(address.getPostalCode());
            registeredOfficeAddress.setPremises(address.getPremise());
            data.setRegisteredOfficeAddress(registeredOfficeAddress);
        }
    }

    private void setCompanyName(Data data, String companyNumber, String companyType) {
        if (companyType.equals(CompanyType.UNITED_KINGDOM_SOCIETAS.getValue())) {
            data.setCompanyName(COMPANY_NAME_PREFIX + companyNumber + " UK SOCIETAS");
        } else {
            data.setCompanyName(COMPANY_NAME_PREFIX + companyNumber + COMPANY_NAME_SUFFIX);
        }
    }

    private Accounts createAccounts(AccountParameters accountParams, DateParameters dateParams) {
        Accounts accounts = new Accounts();
        // Set accounting reference date
        AccountingReferenceDate refDate = new AccountingReferenceDate();
        refDate.setDay(String.valueOf(dateParams.getAccountingReferenceDate().getDayOfMonth()));
        refDate.setMonth(String.valueOf(dateParams.getAccountingReferenceDate().getMonthValue()));
        accounts.setAccountingReferenceDate(refDate);

        // Set next accounts
        NextAccounts nextAccounts = new NextAccounts();
        nextAccounts.setDueOn(dateParams.getDateInOneYearNineMonths()
                .atZone(ZoneId.systemDefault()).toLocalDate());
        nextAccounts.setOverdue(false);
        nextAccounts.setPeriodEndOn(dateParams.getDateInOneYear()
                .atZone(ZoneId.systemDefault()).toLocalDate());
        nextAccounts.setPeriodStartOn(dateParams.getDateNow()
                .atZone(ZoneId.systemDefault()).toLocalDate());
        accounts.setNextAccounts(nextAccounts);

        // Set next due and next made up to
        accounts.setNextDue(dateParams.getDateInOneYearNineMonths()
                .atZone(ZoneId.systemDefault()).toLocalDate());
        accounts.setNextMadeUpTo(dateParams.getDateInOneYear()
                .atZone(ZoneId.systemDefault()).toLocalDate());

        // Set overdue
        accounts.setOverdue(false);

        return accounts;
    }

    private void createConfirmationStatement(Data data, DateParameters dateParams, AccountParameters accountParams) {
        ConfirmationStatement confirmationStatement = new ConfirmationStatement();
        if ("due-soon".equalsIgnoreCase(accountParams.getAccountsDueStatus())) {
            confirmationStatement.setLastMadeUpTo(dateParams.getDateInOneYear()
                    .atZone(ZoneId.systemDefault()).toLocalDate());
            confirmationStatement.setNextMadeUpTo(dateParams.getDateInTwoYear()
                    .atZone(ZoneId.systemDefault()).toLocalDate());
            confirmationStatement.setOverdue(false);
            confirmationStatement.setNextDue(dateParams.getDateInTwoYearTwoWeeks()
                    .atZone(ZoneId.systemDefault()).toLocalDate());
        } else {
            confirmationStatement.setNextMadeUpTo(dateParams.getDateInOneYear()
                    .atZone(ZoneId.systemDefault()).toLocalDate());
            confirmationStatement.setOverdue(false);
            confirmationStatement.setNextDue(dateParams.getDateInOneYearTwoWeeks()
                    .atZone(ZoneId.systemDefault()).toLocalDate());
        }
        confirmationStatement.setNextMadeUpTo(dateParams.getDateInOneYear()
                .atZone(ZoneId.systemDefault()).toLocalDate());
        confirmationStatement.setOverdue(false);
        confirmationStatement.setNextDue(dateParams.getDateInOneYearTwoWeeks()
                .atZone(ZoneId.systemDefault()).toLocalDate());
        data.setConfirmationStatement(confirmationStatement);
    }

    private Links createLinks(String companyNumber) {
        Links links = new Links();
        links.setSelf(LINK_STEM + companyNumber);
        if (!isCompanyTypeHasNoFilingHistory) {
            links.setFilingHistory(LINK_STEM + companyNumber + FILLING_HISTORY_STEM);
        }
        links.setOfficers(LINK_STEM + companyNumber + OFFICERS_STEM);
        links.setPersonsWithSignificantControlStatement(
                LINK_STEM + companyNumber + PSC_STATEMENT_STEM);
        if (hasCompanyRegisters) {
            links.setRegisters(LINK_STEM + companyNumber + REGISTERS_STEM);
        }
        return links;
    }

    private Links createLinkForSelf(String companyNumber) {
        var links = new Links();
        links.setSelf(LINK_STEM + companyNumber);
        return links;
    }

    private void setPartialDataOptions(Data data,
                                       Jurisdiction jurisdiction,
                                       CompanyType companyType) {
        Map<CompanyType, String> partialDataOptions = createPartialDataOptionsMap(jurisdiction);
        if (partialDataOptions.containsKey(companyType)) {
            data.setPartialDataAvailable(partialDataOptions.get(companyType));
        }
    }

    private void setSubType(Data data, String subType) {
        if (subType != null) {
            data.setIsCommunityInterestCompany(subType.equals("community-interest-company"));
            data.setSubtype(subType);
        }
    }

    private static Map<CompanyType, String> createPartialDataOptionsMap(
            Jurisdiction companyJurisdiction) {
        Map<CompanyType, String> partialDataOptions = new HashMap<>();
        partialDataOptions.put(CompanyType.INVESTMENT_COMPANY_WITH_VARIABLE_CAPITAL,
                FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY);
        partialDataOptions.put(CompanyType.ASSURANCE_COMPANY,
                FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY);
        partialDataOptions.put(CompanyType.ROYAL_CHARTER, FULL_DATA_AVAILABLE_FROM_THE_COMPANY);
        partialDataOptions.put(CompanyType.REGISTERED_SOCIETY_NON_JURISDICTIONAL,
                FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY_MUTUALS_PUBLIC_REGISTER);
        if (Jurisdiction.NI.equals(companyJurisdiction)) {
            partialDataOptions.put(CompanyType.INDUSTRIAL_AND_PROVIDENT_SOCIETY,
                    FULL_DATA_AVAILABLE_FROM_DEPARTMENT_OF_THE_ECONOMY);
        } else {
            partialDataOptions.put(CompanyType.INDUSTRIAL_AND_PROVIDENT_SOCIETY,
                    FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY_MUTUALS_PUBLIC_REGISTER);
        }
        return partialDataOptions;
    }

    private void setCompanyStatusDetail(
            Data data, String companyStatusDetail, String companyType) {
        if (companyType.equals(CompanyType.UKEIG.getValue())) {
            data.setCompanyStatusDetail("converted-to-ukeig");
        } else if (companyType.equals(CompanyType.UNITED_KINGDOM_SOCIETAS.getValue())) {
            data.setCompanyStatusDetail("converted-to-uk-societas");
        } else if (!Objects.isNull(companyStatusDetail)) {
            data.setCompanyStatusDetail(companyStatusDetail);
        }
    }

    private String formatUri(String template, String value) {
        return String.format(template, value);
    }

    private void setCompanyStatus(
            Data data, String companyStatus, String companyType) {
        if (CompanyType.NORTHERN_IRELAND.getValue().equals(companyType)
                || CompanyType.NORTHERN_IRELAND_OTHER.getValue().equals(companyType)) {
            data.setCompanyStatus("converted-closed");
        } else if (CompanyType.REGISTERED_OVERSEAS_ENTITY.getValue().equals(companyType)) {
            data.setCompanyStatus(COMPANY_STATUS_REGISTERED);
        } else {
            data.setCompanyStatus(Objects.requireNonNullElse(companyStatus, "active"));
        }
    }

    private void createCompanyProfile(String companyNumber, CompanyProfile profile)
            throws DataException, JsonProcessingException {
        String formattedCompanyProfileUri = formatUri(COMPANY_PROFILE_URI, companyNumber);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Ignore nulls
        LOG.info("profile json" + mapper.writeValueAsString(profile));
        try {
            internalApiClientSupplier.get()
                    .privateDeltaResourceHandler()
                    .putCompanyProfile(formattedCompanyProfileUri, profile)
                    .execute();
        } catch (ApiErrorResponseException | URIValidationException ex) {
            LOG.error("Failed to upsert company profile for company number: " + companyNumber, ex);
            throw new DataException("Failed to upsert company profile: " + ex.getMessage(), ex);
        }
    }
}
