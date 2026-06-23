package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.OverseasEntity;
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.enums.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.enums.CompanyNameEnding;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.repository.OverseasEntityRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CompanyProfileServiceImpl implements CompanyProfileService {

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

    private final RandomService randomService;

    private final AddressService addressService;

    private final CompanyProfileRepository companyProfileRepository;

    private final OverseasEntityRepository overseasEntityRepository;

    public CompanyProfileServiceImpl(RandomService randomService,
                                     AddressService addressService,
                                     CompanyProfileRepository companyProfileRepository,
                                     OverseasEntityRepository overseasEntityRepository) {
        this.randomService = randomService;
        this.addressService = addressService;
        this.companyProfileRepository = companyProfileRepository;
        this.overseasEntityRepository = overseasEntityRepository;
    }

    private boolean hasCompanyRegisters = false;

    private boolean isCompanyTypeHasNoFilingHistory = true;


    @Override
    public CompanyProfile create(InternalCompanyRequest internalCompanyRequest) {
        final String companyNumber = internalCompanyRequest.getCompanyNumber();
        final JurisdictionType jurisdiction = internalCompanyRequest.getJurisdiction();
        final CompanyType companyType = internalCompanyRequest.getCompanyType();
        final String subType = internalCompanyRequest.getSubType();
        final Boolean hasSuperSecurePscs = internalCompanyRequest.getHasSuperSecurePscs();
        final String companyStatusDetail = internalCompanyRequest.getCompanyStatusDetail();
        final String companyStatus = internalCompanyRequest.getCompanyStatus();
        final String accountsDueStatus = internalCompanyRequest.getAccountsDueStatus();
        final Boolean registeredOfficeIsInDispute = internalCompanyRequest.getRegisteredOfficeIsInDispute();

        final LocalDate accountingReferenceDate = resolveAccountingReferenceDate(accountsDueStatus);

        if (CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(companyType)) {
            return createOverseasEntity(companyNumber, jurisdiction, internalCompanyRequest, accountingReferenceDate,
                    OVERSEAS_ENTITY_TYPE, companyType, registeredOfficeIsInDispute);
        } else if (CompanyType.OVERSEA_COMPANY.equals(companyType)) {
            return createOverseasEntity(companyNumber, jurisdiction, internalCompanyRequest, accountingReferenceDate,
                    OVERSEA_COMPANY_TYPE, companyType, registeredOfficeIsInDispute);
        } else {
            DefaultCompanyProfileParams params = new DefaultCompanyProfileParams();
            params.companyNumber = companyNumber;
            params.jurisdiction = jurisdiction;
            params.spec = internalCompanyRequest;
            params.accountingReferenceDate = accountingReferenceDate;
            params.companyType = companyType;
            params.hasSuperSecurePscs = hasSuperSecurePscs;
            params.companyStatus = companyStatus;
            params.subType = subType;
            params.companyStatusDetail = companyStatusDetail;
            params.registeredOfficeIsInDispute = registeredOfficeIsInDispute;
            params.accountsDueStatus = accountsDueStatus;
            return createDefaultCompanyProfile(params);
        }
    }

    private CompanyProfile createDefaultCompanyProfile(DefaultCompanyProfileParams params) {
        final String companyNumber = params.companyNumber;
        final JurisdictionType jurisdiction = params.jurisdiction;
        final InternalCompanyRequest internalCompanyRequest = params.spec;
        final LocalDate accountingReferenceDate = params.accountingReferenceDate;
        final CompanyType companyType = params.companyType;
        final Boolean hasSuperSecurePscs = params.hasSuperSecurePscs;
        final String companyStatus = params.companyStatus;
        final String subType = params.subType;
        final String companyStatusDetail = params.companyStatusDetail;
        final Boolean registeredOfficeIsInDispute = params.registeredOfficeIsInDispute;
        final String accountsDueStatus = params.accountsDueStatus;

        LOG.info("Creating a default CompanyProfile. " + companyNumber);

        final Instant dateOneYearAgo = dateOneYearAgo(accountingReferenceDate);
        final Instant dateNow = dateNow(accountingReferenceDate);
        final Instant dateInOneYear = dateInOneYear(accountingReferenceDate);
        final Instant dateInOneYearTwoWeeks = dateInOneYearTwoWeeks(accountingReferenceDate);
        final Instant dateInOneYearNineMonths = dateInOneYearNineMonths(accountingReferenceDate);
        final Instant dateInTwoYear = dateInTwoYear(accountingReferenceDate);
        final Instant dateInTwoYearTwoWeeks = dateInTwoYearTwoWeeks(accountingReferenceDate);

        CompanyProfile profile = new CompanyProfile();
        profile.setId(companyNumber);
        isCompanyTypeHasNoFilingHistory = hasNoFilingHistory(companyType);
        String companyTypeValue = companyType != null ? companyType.getValue() : "ltd";
        setCompanyHasRegisters(internalCompanyRequest);
        profile.setCompanyNumber(companyNumber);
        String nonJurisdictionType = (jurisdiction != null)
                ? checkNonJurisdictionTypes(jurisdiction, companyTypeValue) : "";
        profile.setLinks(nonJurisdictionType.isEmpty()
                ? createLinkForSelf(companyNumber) : createLinks(companyNumber));

        var accounts = profile.getAccounts();
        accounts.setNextDue(dateInOneYearNineMonths);
        accounts.setPeriodStart(dateNow);
        accounts.setPeriodEnd(dateInOneYear);
        accounts.setNextAccountsDueOn(dateInOneYearNineMonths);
        accounts.setNextAccountsOverdue(false);
        accounts.setNextMadeUpTo(dateInOneYear);
        accounts.setAccountingReferenceDateDay(
                String.valueOf(accountingReferenceDate.getDayOfMonth()));
        accounts.setAccountingReferenceDateMonth(
                String.valueOf(accountingReferenceDate.getMonthValue()));

        profile.setDateOfCreation(dateOneYearAgo);
        profile.setType(companyTypeValue);
        profile.setUndeliverableRegisteredOfficeAddress(BooleanUtils.isTrue(internalCompanyRequest.getUndeliverableRegisteredOfficeAddress()));

        profile.setHasSuperSecurePscs(BooleanUtils.isTrue(hasSuperSecurePscs));
        if (internalCompanyRequest.getCompanyName() != null) {
            profile.setCompanyName(internalCompanyRequest.getCompanyName() + " " + companyNumber);
        } else {
            setCompanyName(profile, companyNumber, companyTypeValue);
        }
        profile.setSicCodes(Collections.singletonList("71200"));

        var confirmationStatement = profile.getConfirmationStatement();
        if ("due-soon".equalsIgnoreCase(accountsDueStatus)) {
            confirmationStatement.setLastMadeUpTo(dateInOneYear);
            confirmationStatement.setNextMadeUpTo(dateInTwoYear);
            confirmationStatement.setOverdue(false);
            confirmationStatement.setNextDue(dateInTwoYearTwoWeeks);
        } else {
            confirmationStatement.setNextMadeUpTo(dateInOneYear);
            confirmationStatement.setOverdue(false);
            confirmationStatement.setNextDue(dateInOneYearTwoWeeks);
            setRegisteredOfficeAddressIsInDispute(profile,
                    registeredOfficeIsInDispute);
        }
        confirmationStatement.setNextMadeUpTo(dateInOneYear);
        confirmationStatement.setOverdue(false);
        confirmationStatement.setNextDue(dateInOneYearTwoWeeks);

        setRegisteredOfficeAddressIsInDispute(profile,
                registeredOfficeIsInDispute);
        setCompanyStatus(profile, companyStatus, companyTypeValue);
        profile.setHasInsolvencyHistory(
                "dissolved".equals(Objects.requireNonNullElse(
                        companyStatus, "")));
        profile.setEtag(this.randomService.getEtag());
        setJurisdictionAndAddress(profile, jurisdiction, nonJurisdictionType);
        profile.setHasCharges(false);
        profile.setCanFile(true);
        setPartialDataOptions(profile, jurisdiction, companyType);
        setSubType(profile, subType);
        setCompanyStatusDetail(profile, companyStatusDetail, companyTypeValue);

        if (Boolean.TRUE.equals(internalCompanyRequest.getCompanyWithPopulatedStructureOnly())) {
            return profile;
        }
        return companyProfileRepository.save(profile);
    }

    private OverseasEntity createOverseasEntity(String companyNumber,
                                                JurisdictionType jurisdiction, InternalCompanyRequest spec,
                                                LocalDate accountingReferenceDate,
                                                String entityType, CompanyType companyType,
                                                Boolean isRegisteredOfficeIsInDispute) {
        LOG.info("Creating " + entityType + " for " + companyNumber);
        final Instant dateOneYearAgo = dateOneYearAgo(accountingReferenceDate);
        final Instant dateNow = dateNow(accountingReferenceDate);
        final Instant dateInOneYear = dateInOneYear(accountingReferenceDate);
        final Instant dateInOneYearTwoWeeks = dateInOneYearTwoWeeks(accountingReferenceDate);

        var overseasEntity = new OverseasEntity();
        overseasEntity.setId(companyNumber);
        overseasEntity.setCompanyNumber(companyNumber);

        if (CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(companyType)) {
            overseasEntity.setHasMortgages(true);
            overseasEntity.setTestData(true);
        }

        overseasEntity.setHasSuperSecurePscs(BooleanUtils.isTrue(spec.getHasSuperSecurePscs()));

        setCompanyStatus(overseasEntity, spec.getCompanyStatus(), entityType);
        overseasEntity.setType(entityType);
        overseasEntity.setHasCharges(false);
        overseasEntity.setHasInsolvencyHistory(false);
        overseasEntity.setJurisdiction(jurisdiction.toString());

        overseasEntity.setDateOfCreation(dateOneYearAgo);

        // Confirmation Statement
        overseasEntity.getConfirmationStatement().setNextMadeUpTo(dateInOneYear);
        overseasEntity.getConfirmationStatement().setNextDue(dateInOneYearTwoWeeks);
        overseasEntity.getConfirmationStatement().setOverdue(false);

        // Company Details
        overseasEntity.setUndeliverableRegisteredOfficeAddress(BooleanUtils.isTrue(spec.getUndeliverableRegisteredOfficeAddress()));

        if (spec.getCompanyName() != null) {
            overseasEntity.setCompanyName(spec.getCompanyName() + " " + companyNumber);
        } else {
            setCompanyName(overseasEntity, companyNumber, companyType.getValue());
        }
        overseasEntity.setRegisteredOfficeIsInDispute(false);
        setRegisteredOfficeAddressIsInDispute(overseasEntity, isRegisteredOfficeIsInDispute);
        overseasEntity.setEtag(randomService.getEtag());
        overseasEntity.setSuperSecureManagingOfficerCount(0);

        // Addresses
        overseasEntity.setRegisteredOfficeAddress(addressService.getOverseasAddress());
        overseasEntity.setServiceAddress(addressService.getOverseasAddress());

        // Foreign Company Details
        OverseasEntity.IForeignCompanyDetails foreignCompanyDetails =
                OverseasEntity.createForeignCompanyDetails();
        foreignCompanyDetails.setGovernedBy(GOVERNED_BY);
        foreignCompanyDetails.setCreditFinancialInstitution(true);
        foreignCompanyDetails.setBusinessActivity(BUSINESS_ACTIVITY);

        var legalForm = (spec.getForeignCompanyLegalForm() == null
                || spec.getForeignCompanyLegalForm().isBlank())
                ? LEGAL_FORM
                : spec.getForeignCompanyLegalForm();
        foreignCompanyDetails.setLegalForm(legalForm);

        // Originating Registry
        OverseasEntity.IOriginatingRegistry originatingRegistry =
                OverseasEntity.createOriginatingRegistry();
        originatingRegistry.setCountry(FCD_COUNTRY);
        originatingRegistry.setName(ORIGINATING_REGISTRY_NAME);
        foreignCompanyDetails.setOriginatingRegistry(originatingRegistry);

        // Accounting Requirement
        OverseasEntity.IAccountingRequirement
                accountingRequirement = OverseasEntity.createAccountingRequirement();
        accountingRequirement.setForeignAccountType(FOREIGN_ACCOUNT_TYPE);
        accountingRequirement.setTermsOfAccountPublication(TERMS_OF_PUBLICATION);
        foreignCompanyDetails.setAccountingRequirement(accountingRequirement);

        // Accounts Details
        OverseasEntity.IAccountsDetails accountsDetails = OverseasEntity.createAccountsDetails();
        accountsDetails.setAccountPeriodFrom("1", "January");
        accountsDetails.setAccountPeriodTo("31", "December");

        OverseasEntity.IMustFileWithin mustFileWithin = OverseasEntity.createMustFileWithin();
        mustFileWithin.setMonths(12);
        accountsDetails.setMustFileWithin(mustFileWithin);

        foreignCompanyDetails.setAccounts(accountsDetails);

        overseasEntity.setForeignCompanyDetails(foreignCompanyDetails);

        // Accounts
        var accounts = overseasEntity.getAccounts();
        accounts.setOverdue(false);
        accounts.setNextMadeUpTo(dateInOneYear);
        accounts.setNextDue(dateInOneYearTwoWeeks);
        accounts.setNextAccountsOverdue(false);
        accounts.setNextAccountsDueOn(dateInOneYearTwoWeeks);
        accounts.setPeriodStart(dateNow);
        accounts.setPeriodEnd(dateInOneYear);
        accounts.setAccountingReferenceDateDay("9");
        accounts.setAccountingReferenceDateMonth("9");
        accounts.setLastAccountsType("aa");
        accounts.setLastAccountsPeriodStartOn(dateOneYearAgo);
        accounts.setLastAccountsPeriodEndOn(dateNow);
        accounts.setLastAccountsMadeUpTo(dateInOneYear);

        if (CompanyType.OVERSEA_COMPANY.equals(companyType)) {
            foreignCompanyDetails.setRegistrationNumber(EXT_REGISTRATION_NUMBER);
            overseasEntity.setDeltaAt(Instant.now());
            OverseasEntity.IUpdated updated = OverseasEntity.createUpdated();
            updated.setAt(Instant.now());
            updated.setBy(randomService.getString(16));
            updated.setType(UPDATED_TYPE);
            overseasEntity.setUpdated(updated);
        }

        overseasEntity.setLinks(createOverseaLinks(
                companyNumber, companyType, spec, jurisdiction, accountingReferenceDate));

        if (Boolean.TRUE.equals(spec.getCompanyWithPopulatedStructureOnly())) {
            return overseasEntity;
        }

        overseasEntityRepository.save(overseasEntity);
        LOG.info("Returning a CompanyProfile view for " + entityType + ". " + companyNumber);
        return overseasEntity;
    }

    protected String createUkEstablishment(String parentCompanyNumber,
                                           JurisdictionType jurisdiction,
                                           LocalDate accountingReferenceDate) {
        String ukEstablishmentNumber = "BR" + this.randomService.getNumber(6);
        LOG.info("Creating UK establishment for parent company " + parentCompanyNumber);

        var ukEstablishment = new CompanyProfile();
        ukEstablishment.setId(ukEstablishmentNumber);
        ukEstablishment.setCompanyNumber(ukEstablishmentNumber);
        ukEstablishment.setType(CompanyType.UK_ESTABLISHMENT.getValue());

        ukEstablishment.setParentCompanyNumber(parentCompanyNumber);

        var branchDetails = new CompanyProfile.BranchCompanyDetails();
        branchDetails.setBusinessActivity(BUSINESS_ACTIVITY);
        branchDetails.setParentCompanyName(COMPANY_NAME_PREFIX
                + parentCompanyNumber + getCompanyNameEnding(CompanyType.UK_ESTABLISHMENT));
        branchDetails.setParentCompanyNumber(parentCompanyNumber);
        ukEstablishment.setBranchCompanyDetails(branchDetails);

        ukEstablishment.setCompanyName(COMPANY_NAME_PREFIX
                + ukEstablishmentNumber + getCompanyNameEnding(CompanyType.UK_ESTABLISHMENT));
        ukEstablishment.setDateOfCreation(dateNow(accountingReferenceDate));
        ukEstablishment.setRegisteredOfficeAddress(addressService.getAddress(jurisdiction));

        ukEstablishment.setCompanyStatus("open");
        ukEstablishment.setEtag(this.randomService.getEtag());
        ukEstablishment.setHasCharges(false);
        ukEstablishment.setHasSuperSecurePscs(false);

        var links = new Links();
        links.setSelf(LINK_STEM + ukEstablishmentNumber);
        links.setOverseas(LINK_STEM + parentCompanyNumber);
        ukEstablishment.setLinks(links);

        companyProfileRepository.save(ukEstablishment);
        LOG.info("Created UK establishment " + ukEstablishmentNumber);

        return ukEstablishmentNumber;
    }

    private static Map<CompanyType, String> createPartialDataOptionsMap(
            JurisdictionType companyJurisdiction) {
        Map<CompanyType, String> partialDataOptions = new HashMap<>();
        partialDataOptions.put(CompanyType.INVESTMENT_COMPANY_WITH_VARIABLE_CAPITAL,
                FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY);
        partialDataOptions.put(CompanyType.ASSURANCE_COMPANY,
                FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY);
        partialDataOptions.put(CompanyType.ROYAL_CHARTER, FULL_DATA_AVAILABLE_FROM_THE_COMPANY);
        partialDataOptions.put(CompanyType.REGISTERED_SOCIETY_NON_JURISDICTIONAL,
                FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY_MUTUALS_PUBLIC_REGISTER);
        if (JurisdictionType.NI.equals(companyJurisdiction)) {
            partialDataOptions.put(CompanyType.INDUSTRIAL_AND_PROVIDENT_SOCIETY,
                    FULL_DATA_AVAILABLE_FROM_DEPARTMENT_OF_THE_ECONOMY);
        } else {
            partialDataOptions.put(CompanyType.INDUSTRIAL_AND_PROVIDENT_SOCIETY,
                    FULL_DATA_AVAILABLE_FROM_FINANCIAL_CONDUCT_AUTHORITY_MUTUALS_PUBLIC_REGISTER);
        }
        return partialDataOptions;
    }

    @Override
    public boolean delete(String companyId) {
        Optional<CompanyProfile> profile = companyProfileRepository.findByCompanyNumber(companyId)
                .or(() -> companyProfileRepository.findById(companyId));

        if (profile.isPresent()) {
            companyProfileRepository.delete(profile.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean companyExists(String companyNumber) {
        return companyProfileRepository.findById(companyNumber).isPresent();
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

    private Links createOverseaLinks(String companyNumber,
                                     CompanyType companyType, InternalCompanyRequest spec,
                                     JurisdictionType jurisdiction,
                                     LocalDate accountingReferenceDate) {
        var links = new Links();
        links.setSelf(LINK_STEM + companyNumber);

        if (CompanyType.OVERSEA_COMPANY.equals(companyType)
                && BooleanUtils.isTrue(spec.getHasUkEstablishment())) {
            createUkEstablishment(companyNumber, jurisdiction, accountingReferenceDate);
            links.setUkEstablishments(LINK_STEM + companyNumber + "/uk-establishments");
        }

        if (CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(companyType)) {
            links.setFilingHistory(LINK_STEM + companyNumber + FILLING_HISTORY_STEM);
            links.setOfficers(LINK_STEM + companyNumber + OFFICERS_STEM);
            links.setPersonsWithSignificantControlStatement(
                    LINK_STEM + companyNumber + PSC_STATEMENT_STEM);
        }

        return links;
    }

    private String checkNonJurisdictionTypes(JurisdictionType jurisdiction, String companyType) {
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

    private void setPartialDataOptions(CompanyProfile profile,
                                       JurisdictionType jurisdiction,
                                       CompanyType companyType) {
        Map<CompanyType, String> partialDataOptions = createPartialDataOptionsMap(jurisdiction);
        if (partialDataOptions.containsKey(companyType)) {
            profile.setPartialDataAvailable(partialDataOptions.get(companyType));
        }
    }

    private void setSubType(CompanyProfile profile, String subType) {
        if (subType != null) {
            profile.setIsCommunityInterestCompany(subType.equals("community-interest-company"));
            profile.setSubtype(subType);
        }
    }

    private void setCompanyStatusDetail(
            CompanyProfile profile, String companyStatusDetail, String companyType) {
        if (companyType.equals(CompanyType.UKEIG.getValue())) {
            profile.setCompanyStatusDetail("converted-to-ukeig");
        } else if (companyType.equals(CompanyType.UNITED_KINGDOM_SOCIETAS.getValue())) {
            profile.setCompanyStatusDetail("converted-to-uk-societas");
        } else if (!Objects.isNull(companyStatusDetail)) {
            profile.setCompanyStatusDetail(companyStatusDetail);
        }
    }

    private void setJurisdictionAndAddress(CompanyProfile profile,
                                           JurisdictionType jurisdiction,
                                           String nonJurisdictionType) {
        if (jurisdiction != null && !nonJurisdictionType.isEmpty()) {
            profile.setJurisdiction(jurisdiction.toString());
            profile.setRegisteredOfficeAddress(addressService.getAddress(jurisdiction));
        }
    }

    private void setCompanyHasRegisters(InternalCompanyRequest spec) {
        hasCompanyRegisters = spec.getRegisters() != null && !spec.getRegisters().isEmpty();
    }

    private void setCompanyStatus(
            CompanyProfile profile, String companyStatus, String companyType) {
        if (CompanyType.NORTHERN_IRELAND.getValue().equals(companyType)
                || CompanyType.NORTHERN_IRELAND_OTHER.getValue().equals(companyType)) {
            profile.setCompanyStatus("converted-closed");
        } else if (CompanyType.REGISTERED_OVERSEAS_ENTITY.getValue().equals(companyType)) {
            profile.setCompanyStatus(COMPANY_STATUS_REGISTERED);
        } else {
            profile.setCompanyStatus(Objects.requireNonNullElse(companyStatus, "active"));
        }
    }

    private void setCompanyName(CompanyProfile profile, String companyNumber, String companyType) {
        if (companyType.equals(CompanyType.UNITED_KINGDOM_SOCIETAS.getValue())) {
            profile.setCompanyName(COMPANY_NAME_PREFIX + companyNumber + " UK SOCIETAS");
        } else {
            profile.setCompanyName(COMPANY_NAME_PREFIX + companyNumber + getCompanyNameEnding(CompanyType.fromValue(companyType)));
        }
    }

    private boolean hasNoFilingHistory(CompanyType companyType) {
        if (companyType == null) {
            return false;
        }

        Set<String> noFilingHistoryCompanyTypes = Set.of(
                CompanyType.ASSURANCE_COMPANY.getValue(),
                CompanyType.CHARITABLE_INCORPORATED_ORGANISATION.getValue(),
                CompanyType.ICVC_SECURITIES.getValue(),
                CompanyType.ICVC_UMBRELLA.getValue(),
                CompanyType.INDUSTRIAL_AND_PROVIDENT_SOCIETY.getValue(),
                CompanyType.INVESTMENT_COMPANY_WITH_VARIABLE_CAPITAL.getValue(),
                CompanyType.PROTECTED_CELL_COMPANY.getValue(),
                CompanyType.ROYAL_CHARTER.getValue(),
                CompanyType.SCOTTISH_CHARITABLE_INCORPORATED_ORGANISATION.getValue(),
                CompanyType.UK_ESTABLISHMENT.getValue()
        );

        return noFilingHistoryCompanyTypes.contains(companyType.getValue());
    }

    @Override
    public List<String> findUkEstablishmentsByParent(String parentCompanyNumber) {
        return companyProfileRepository.findByBranchCompanyDetailsParentCompanyNumber(parentCompanyNumber)
                .stream()
                .map(CompanyProfile::getCompanyNumber)
                .toList();
    }

    @Override
    public Optional<CompanyProfile> getCompanyProfile(String companyNumber) {
        return companyProfileRepository.findByCompanyNumber(companyNumber);
    }

    @Override
    public CompanyProfile updateCompanyProfile(UpdateCompanyRequest request)
            throws NoDataFoundException, DataException {

        if (request.getCompanyNumber() == null || request.getCompanyNumber().isBlank()) {
            throw new DataException("companyNumber is required");
        }

        CompanyProfile company = companyProfileRepository.findByCompanyNumber(request.getCompanyNumber())
                .orElseThrow(() -> new NoDataFoundException(
                        "Company not found for number: " + request.getCompanyNumber()));

        if (request.getEtag() != null && !request.getEtag().isBlank()) {
            company.setEtag(request.getEtag());
        }

        if (!request.getUnknownFields().isEmpty()) {
            throw new DataException(
                    "Invalid field(s): " + request.getUnknownFields().keySet()
            );
        }

        try {
            return companyProfileRepository.save(company);
        } catch (Exception ex) {
            throw new DataException("Failed to update company profile", ex);
        }
    }

    private void setRegisteredOfficeAddressIsInDispute(
            CompanyProfile profile, Boolean registeredOfficeIsInDispute) {
        profile.setRegisteredOfficeIsInDispute(Objects.requireNonNullElse(
                registeredOfficeIsInDispute, false));
    }

    private LocalDate resolveAccountingReferenceDate(String accountsDueStatus) {
        if (StringUtils.hasText(accountsDueStatus)) {
            return randomService.generateAccountsDueDateByStatus(accountsDueStatus);
        }
        return LocalDate.now();
    }

    private Instant dateOneYearAgo(LocalDate accountingReferenceDate) {
        return toUtcStartOfDay(accountingReferenceDate.minusYears(1));
    }

    private Instant dateNow(LocalDate accountingReferenceDate) {
        return toUtcStartOfDay(accountingReferenceDate);
    }

    private Instant dateInOneYear(LocalDate accountingReferenceDate) {
        return toUtcStartOfDay(accountingReferenceDate.plusYears(1));
    }

    private Instant dateInOneYearTwoWeeks(LocalDate accountingReferenceDate) {
        return toUtcStartOfDay(accountingReferenceDate.plusYears(1).plusDays(14));
    }

    private Instant dateInOneYearNineMonths(LocalDate accountingReferenceDate) {
        return toUtcStartOfDay(accountingReferenceDate.plusYears(1).plusMonths(9));
    }

    private Instant dateInTwoYear(LocalDate accountingReferenceDate) {
        return toUtcStartOfDay(accountingReferenceDate.plusYears(2));
    }

    private Instant dateInTwoYearTwoWeeks(LocalDate accountingReferenceDate) {
        return toUtcStartOfDay(accountingReferenceDate.plusYears(2).plusDays(14));
    }

    private Instant toUtcStartOfDay(LocalDate date) {
        return date.atStartOfDay(ZoneId.of("UTC")).toInstant();
    }

    private static final class DefaultCompanyProfileParams {
        private String companyNumber;
        private JurisdictionType jurisdiction;
        private InternalCompanyRequest spec;
        private LocalDate accountingReferenceDate;
        private CompanyType companyType;
        private Boolean hasSuperSecurePscs;
        private String companyStatus;
        private String subType;
        private String companyStatusDetail;
        private Boolean registeredOfficeIsInDispute;
        private String accountsDueStatus;
    }

    private String getCompanyNameEnding(CompanyType companyType) {
        if (companyType == null) {
            LOG.info("getCompanyNameEnding called with null companyType");
            return "";
        }
        try {
            String ending = CompanyNameEnding.fromTypeEnum(companyType).getEnding();
            return ending.isEmpty() ? "" : " " + ending;
        } catch (IllegalArgumentException ex) {
            LOG.error("No CompanyNameEnding mapping for CompanyType:}" + companyType, ex);
            return "";
        }
    }
    
}
