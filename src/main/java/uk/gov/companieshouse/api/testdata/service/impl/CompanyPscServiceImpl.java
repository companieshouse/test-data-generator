package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.entity.DateOfBirth;
import uk.gov.companieshouse.api.testdata.model.entity.Identification;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.NameElements;
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.enums.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.model.rest.enums.PscType;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscsRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.CompanyPscService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CompanyPscServiceImpl implements CompanyPscService {

    private static final Logger LOG = LoggerFactory.getLogger(String.valueOf(CompanyPscServiceImpl.class));

    static final String[] NATURES_OF_CONTROL_SHARES = {"ownership-of-shares-25-to-50-percent",
            "ownership-of-shares-50-to-75-percent",
            "ownership-of-shares-75-to-100-percent",
            "ownership-of-shares-25-to-50-percent-as-trust",
            "ownership-of-shares-50-to-75-percent-as-trust"};
    static final String[] NATURES_OF_CONTROL_VOTING_RIGHTS = {"voting-rights-25-to-50-percent",
            "voting-rights-50-to-75-percent",
            "voting-rights-75-to-100-percent",};
    static final String[] NATURES_OF_CONTROL_RIGHT_TO_APPOINT = {"right-to-appoint-and-remove-directors",
            "right-to-appoint-and-remove-directors-as-trust",
            "right-to-appoint-and-remove-directors-as-firm",};
    static final String[] NATURES_OF_CONTROL_SHARES_ROE = {
            "ownership-of-shares-more-than-25-percent-registered-overseas-entity",
            "ownership-of-shares-more-than-25-percent-as-trust-registered-overseas-entity",
            "ownership-of-shares-more-than-25-percent-as-firm-registered-overseas-entity"};
    static final String[] NATURES_OF_CONTROL_VOTING_RIGHTS_ROE = {
            "voting-rights-more-than-25-percent-registered-overseas-entity",
            "voting-rights-more-than-25-percent-as-trust-registered-overseas-entity",
            "voting-rights-more-than-25-percent-as-firm-registered-overseas-entity"};
    static final String[] NATURES_OF_CONTROL_RIGHT_TO_APPOINT_ROE = {
            "right-to-appoint-and-remove-directors-registered-overseas-entity",
            "right-to-appoint-and-remove-directors-as-trust-registered-overseas-entity",
            "right-to-appoint-and-remove-directors-as-firm-registered-overseas-entity"};
    private static final int ID_LENGTH = 10;
    private static final int SALT_LENGTH = 8;
    public static final String NATIONALITY = "British";
    public static final String REGISTRATION_NUMBER = "12345678";
    public static final String RLE_LEGAL_FORM = "Private Limited Company";
    public static final String ORP_LEGAL_FORM = "Local Authority";
    public static final String ROE_LEGAL_FORM = "S.R.L Overseas Company";
    public static final String ROE_LEGAL_AUTHORITY = "Cayman Islands";
    public static final String LEGAL_AUTHORITY = "England & Wales";
    public static final String SUPER_SECURE_BO = "super-secure-beneficial-owner";
    public static final String URL_PREFIX = "/company/";
    public static final String PSC_SUFFIX = "/persons-with-significant-control/";
    public static final String TITLE = "Dr.";
    public static final String FIRST_NAME = "Individual";
    public static final String LAST_NAME = "Beneficial Owner";
    private static final int DEFAULT_NUMBER_OF_PSCS = 1;
    private static final String PSC_ERROR_MESSAGE = "psc_type must be accompanied by number_of_psc";
    private static final String BENEFICIAL_OWNER_ERROR =
            "Beneficial owner type is not allowed for this company type";

    /**
     * Company types that do not maintain a PSC register and therefore require no PSC data.
     * This includes:
     * <ul>
     *   <li>FCA-regulated investment vehicles (ICVC, assurance, protected cell, investment companies)</li>
     *   <li>Charities regulated outside Companies House (CIOs, SCIOs, further education corporations)</li>
     *   <li>European and UK economic interest groupings (EEIG, EEIG Establishment, UKEIG)</li>
     *   <li>Overseas and establishment companies (oversea-company, UK establishment)</li>
     *   <li>Entities exempt by structure (limited partnerships, registered societies, royal charter)</li>
     *   <li>Historical company types (old public company)</li>
     * </ul>
     */
    private static final Set<CompanyType> EXCLUDED_COMPANY_TYPES = Set.of(
            CompanyType.ASSURANCE_COMPANY,
            CompanyType.CHARITABLE_INCORPORATED_ORGANISATION,
            CompanyType.EEIG,
            CompanyType.EEIG_ESTABLISHMENT,
            CompanyType.FURTHER_EDUCATION_OR_SIXTH_FORM_COLLEGE_CORPORATION,
            CompanyType.ICVC_SECURITIES,
            CompanyType.ICVC_UMBRELLA,
            CompanyType.ICVC_WARRANT,
            CompanyType.INVESTMENT_COMPANY_WITH_VARIABLE_CAPITAL,
            CompanyType.LIMITED_PARTNERSHIP,
            CompanyType.OLD_PUBLIC_COMPANY,
            CompanyType.OVERSEA_COMPANY,
            CompanyType.PROTECTED_CELL_COMPANY,
            CompanyType.REGISTERED_SOCIETY_NON_JURISDICTIONAL,
            CompanyType.ROYAL_CHARTER,
            CompanyType.UK_ESTABLISHMENT,
            CompanyType.UKEIG,
            CompanyType.SCOTTISH_CHARITABLE_INCORPORATED_ORGANISATION
    );

    private final RandomService randomService;
    private final CompanyPscsRepository repository;
    private final AddressService addressService;

    @Autowired
    public CompanyPscServiceImpl(RandomService randomService,
                                 CompanyPscsRepository repository,
                                 AddressService addressService) {
        this.randomService = randomService;
        this.repository = repository;
        this.addressService = addressService;
    }

    @Override
    public List<CompanyPscs> create(InternalCompanyRequest internalCompanyRequest) throws DataException {
        LOG.info("Starting creation of PSCs for company number: " + internalCompanyRequest.getCompanyNumber());

        if (CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(internalCompanyRequest.getCompanyType()) &&
            (internalCompanyRequest.getPscType() == null || internalCompanyRequest.getPscType().isEmpty() ||
             internalCompanyRequest.getPscType().stream().anyMatch(type -> type == PscType.INDIVIDUAL))) {
             internalCompanyRequest.setPscType(List.of(PscType.INDIVIDUAL_BENEFICIAL_OWNER));
        }

        if (shouldSkipPscCreation(internalCompanyRequest)) {
            LOG.info("Company type does not require a PSC. No PSCs will be created.");
            internalCompanyRequest.setNumberOfPscs(0);
            return Collections.emptyList();
        }

        if (shouldCreateSuperSecurePsc(internalCompanyRequest)) {
            List<CompanyPscs> superSecurePscList = new ArrayList<>();
            superSecurePscList.add(createAppropriateSuperSecurePsc(internalCompanyRequest));
            LOG.info("Company has super secure PSCs. Creating super secure PSC.");
            return superSecurePscList;
        }

        validatePscTypeAndCount(internalCompanyRequest);

        int numberOfPsc = getNumberOfPsc(internalCompanyRequest);
        if (numberOfPsc <= 0) {
            LOG.info("Number of PSCs is less than or equal to 0. No PSCs will be created.");
            return Collections.emptyList();
        }

        LOG.info("Creating " + numberOfPsc + " PSCs for company number: " + internalCompanyRequest.getCompanyNumber());
        return createPscsBasedOnCompanyType(internalCompanyRequest, numberOfPsc);
    }

    private boolean shouldSkipPscCreation(InternalCompanyRequest internalCompanyRequest) {
        if (internalCompanyRequest == null || internalCompanyRequest.getCompanyType() == null) {
            return false;
        }
        boolean result = EXCLUDED_COMPANY_TYPES.contains(internalCompanyRequest.getCompanyType());
        LOG.debug("shouldSkipPscCreation: " + result);
        return result;
    }

    private boolean shouldCreateSuperSecurePsc(InternalCompanyRequest internalCompanyRequest) {
        boolean result = Boolean.TRUE.equals(internalCompanyRequest.getHasSuperSecurePscs());
        LOG.debug("shouldCreateSuperSecurePsc: " + result);
        return result;
    }

    private CompanyPscs createAppropriateSuperSecurePsc(InternalCompanyRequest internalCompanyRequest) {
        return CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(internalCompanyRequest.getCompanyType())
                ? createSuperSecureBeneficialOwner(internalCompanyRequest)
                : createSuperSecurePsc(internalCompanyRequest);
    }

    private void validatePscTypeAndCount(InternalCompanyRequest internalCompanyRequest) throws DataException {
        if (hasPscTypesWithoutCount(internalCompanyRequest)) {
            LOG.error("Validation failed: " +  PSC_ERROR_MESSAGE);
            throw new DataException(PSC_ERROR_MESSAGE);
        }
        if (hasInvalidBeneficialOwnerType(internalCompanyRequest)) {
            LOG.error("Validation failed: " + BENEFICIAL_OWNER_ERROR);
            throw new DataException(BENEFICIAL_OWNER_ERROR);
        }
        LOG.debug("Validation passed for PSC type and count.");
    }

    private boolean hasPscTypesWithoutCount(InternalCompanyRequest internalCompanyRequest) {
        return internalCompanyRequest.getPscType() != null
                && !internalCompanyRequest.getPscType().isEmpty()
                && (internalCompanyRequest.getNumberOfPscs() == null || internalCompanyRequest.getNumberOfPscs() <= 0);
    }

    private boolean hasInvalidBeneficialOwnerType(InternalCompanyRequest internalCompanyRequest) {
        return internalCompanyRequest.getPscType() != null
                && internalCompanyRequest.getPscType().stream().anyMatch(this::isBeneficialOwnerType)
                && !CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(internalCompanyRequest.getCompanyType());
    }

    private boolean isBeneficialOwnerType(PscType type) {
        return type == PscType.INDIVIDUAL_BENEFICIAL_OWNER || type == PscType.CORPORATE_BENEFICIAL_OWNER;
    }

    private int getNumberOfPsc(InternalCompanyRequest spec) {
        int numberOfPsc = Optional.ofNullable(spec.getNumberOfPscs()).orElse(DEFAULT_NUMBER_OF_PSCS);
        LOG.debug("Number of PSCs determined: " + numberOfPsc);
        return numberOfPsc;
    }

    private List<CompanyPscs> createPscsBasedOnCompanyType(InternalCompanyRequest internalCompanyRequest, int numberOfPsc) {
        LOG.info("Creating PSCs based on company type: " + internalCompanyRequest.getCompanyType());

        if (numberOfPsc <= 0) {
            LOG.info("No PSCs requested for company number: " + internalCompanyRequest.getCompanyNumber()
                    + ". Returning empty list.");
            return Collections.emptyList();
        }

        boolean isOverseasEntity = CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(internalCompanyRequest.getCompanyType());

        boolean ceaseFirstPsc = internalCompanyRequest.getPscActive() != null && !internalCompanyRequest.getPscActive();
        List<CompanyPscs> listOfCommonPscs = new ArrayList<>();
        for (var i = 0; i < numberOfPsc; i++) {
            LOG.debug("Creating PSC " + (i + 1) + " of " + numberOfPsc);

            boolean isActive = !(ceaseFirstPsc && i == 0);

            CompanyPscs psc = isOverseasEntity
                    ? createBeneficialOwner(internalCompanyRequest, getBeneficialOwnerType(internalCompanyRequest.getPscType(), i), isActive)
                    : createPsc(internalCompanyRequest, getRegularPscType(internalCompanyRequest.getPscType(), i), isActive);

            listOfCommonPscs.add(psc);
        }

        LOG.info("Successfully created PSCs for company number: " + internalCompanyRequest.getCompanyNumber());
        return listOfCommonPscs;
    }

    private PscType getBeneficialOwnerType(List<PscType> requestedPscTypes, int index) {
        // If PSC types are provided, cycle through them
        if (requestedPscTypes != null && !requestedPscTypes.isEmpty()) {
            return requestedPscTypes.get(index % requestedPscTypes.size());
        }
        // Default to INDIVIDUAL_BENEFICIAL_OWNER for registered overseas entities
        return PscType.INDIVIDUAL_BENEFICIAL_OWNER;
    }

    private PscType getRegularPscType(List<PscType> requestedPscTypes, int index) {
        if (requestedPscTypes != null && !requestedPscTypes.isEmpty()) {
            return requestedPscTypes.get(index % requestedPscTypes.size());
        }
        return getRandomPscType();
    }

    private PscType getRandomPscType() {
        var regularTypes = new PscType[] {
                PscType.INDIVIDUAL,
                PscType.LEGAL_PERSON,
                PscType.CORPORATE_ENTITY
        };
        return regularTypes[(int) randomService.getNumberInRange(0, regularTypes.length - 1)
                .orElse(0)];
    }

    private CompanyPscs createSuperSecureBeneficialOwner(InternalCompanyRequest spec) {
        CompanyPscs superSecureBo = createBasePsc(spec, true, true);
        buildSuperSecureBeneficialOwner(superSecureBo);
        if (Boolean.TRUE.equals(spec.getCompanyWithPopulatedStructureOnly())) {
            return superSecureBo;
        }
        return repository.save(superSecureBo);
    }

    private CompanyPscs createSuperSecurePsc(InternalCompanyRequest spec) {
        CompanyPscs superSecurePsc = createBasePsc(spec, true, false);
        buildSuperSecurePsc(superSecurePsc);
        if (Boolean.TRUE.equals(spec.getCompanyWithPopulatedStructureOnly())) {
            return superSecurePsc;
        }
        return repository.save(superSecurePsc);
    }

    CompanyPscs createBasePsc(InternalCompanyRequest spec, boolean isActive, boolean isBeneficialOwner) {
        var companyPsc = new CompanyPscs();
        companyPsc.setCompanyNumber(spec.getCompanyNumber());

        Instant createdUpdatedAt = getCreatedUpdatedAt(spec);
        companyPsc.setCreatedAt(createdUpdatedAt);
        companyPsc.setUpdatedAt(createdUpdatedAt);
        companyPsc.setNotifiedOn(createdUpdatedAt);
        companyPsc.setRegisterEntryDate(createdUpdatedAt);

        companyPsc.setCeased(!isActive);
        if (!isActive) {
            var ceasedDate = LocalDate.now().minusDays(30).atStartOfDay(ZoneId.of("UTC")).toInstant();
            companyPsc.setCeasedOn(ceasedDate);
        }

        companyPsc.setId(this.randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH));
        companyPsc.setEtag(this.randomService.getEtag());
        companyPsc.setStatementType("statement type");

        JurisdictionType serviceAddressJurisdiction = isBeneficialOwner
                ? JurisdictionType.EUROPEAN_UNION
                : JurisdictionType.ENGLAND_WALES;
        companyPsc.setAddress(addressService.getAddress(serviceAddressJurisdiction));

        setNaturesOfControl(companyPsc, isBeneficialOwner);

        return companyPsc;
    }

    private Instant getCreatedUpdatedAt(InternalCompanyRequest spec) {
        if (StringUtils.hasText(spec.getAccountsDueStatus())) {
            LocalDate dueDateNow = randomService.generateAccountsDueDateByStatus(
                    spec.getAccountsDueStatus());
            return dueDateNow.atStartOfDay(ZoneId.of("UTC")).toInstant();
        }
        return LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant();
    }

    /**
     * Sets the natures of control for a given CompanyPscs object.
     * This method selects one random nature of control from each of the predefined categories:
     * ownership of shares, voting rights, and the right to appoint and remove directors.
     * Registered overseas entity beneficial owners use ROE-specific nature of control values.
     *
     * @param companyPsc         the CompanyPscs object to which the natures of control will be assigned
     * @param isBeneficialOwner  true if this PSC is a beneficial owner (ROE context)
     */
    private void setNaturesOfControl(CompanyPscs companyPsc, boolean isBeneficialOwner) {
        List<String> sharesList = new ArrayList<>(Arrays.asList(
                isBeneficialOwner ? NATURES_OF_CONTROL_SHARES_ROE : NATURES_OF_CONTROL_SHARES));
        List<String> votingRightsList = new ArrayList<>(Arrays.asList(
                isBeneficialOwner ? NATURES_OF_CONTROL_VOTING_RIGHTS_ROE : NATURES_OF_CONTROL_VOTING_RIGHTS));
        List<String> rightToAppointList = new ArrayList<>(Arrays.asList(
                isBeneficialOwner ? NATURES_OF_CONTROL_RIGHT_TO_APPOINT_ROE : NATURES_OF_CONTROL_RIGHT_TO_APPOINT));
        Collections.shuffle(sharesList);
        Collections.shuffle(votingRightsList);
        Collections.shuffle(rightToAppointList);
        String randomShares = sharesList.get(0);
        String randomVotingRights = votingRightsList.get(0);
        String randomRightToAppoint = rightToAppointList.get(0);
        companyPsc.setNaturesOfControl(List.of(randomShares, randomVotingRights, randomRightToAppoint));
    }


    private void buildSuperSecureBeneficialOwner(CompanyPscs companyPscs) {
        companyPscs.setKind(PscType.SUPER_SECURE_BENEFICIAL_OWNER.getKind());
        companyPscs.setDescription(SUPER_SECURE_BO);
        companyPscs.setCeased(false);

        var links = new Links();
        links.setSelf(URL_PREFIX + companyPscs.getCompanyNumber()
                + PSC_SUFFIX
                + PscType.SUPER_SECURE_BENEFICIAL_OWNER.getLinkType() + "/" + companyPscs.getId());
        companyPscs.setLinks(links);
    }

    private void buildSuperSecurePsc(CompanyPscs companyPscs) {
        companyPscs.setKind(PscType.SUPER_SECURE_PSC.getKind());
        companyPscs.setDescription("super-secure-persons-with-significant-control");
        companyPscs.setCeased(false);

        var links = new Links();
        links.setSelf(URL_PREFIX + companyPscs.getCompanyNumber()
                + PSC_SUFFIX + PscType.SUPER_SECURE_PSC.getLinkType() + "/" + companyPscs.getId());
        companyPscs.setLinks(links);
    }

    private CompanyPscs createPsc(InternalCompanyRequest spec, PscType pscType, boolean isActive) {
        var companyPscs = createBasePsc(spec, isActive, false);
        switch (pscType) {
            case INDIVIDUAL:
                buildIndividualPsc(companyPscs, pscType.getKind(), pscType.getLinkType());
                break;
            case LEGAL_PERSON:
                buildLegalPersonPsc(companyPscs, pscType.getKind(), pscType.getLinkType());
                break;
            case CORPORATE_ENTITY:
                buildCorporateEntityPsc(companyPscs, pscType.getKind(), pscType.getLinkType());
                break;
            default:
                throw new IllegalArgumentException("Unsupported PSC type: " + pscType);
        }
        if (Boolean.TRUE.equals(spec.getCompanyWithPopulatedStructureOnly())) {
            return companyPscs;
        }
        return repository.save(companyPscs);
    }

    private CompanyPscs createBeneficialOwner(InternalCompanyRequest spec, PscType pscType, boolean isActive) {
        var beneficialOwner = createBasePsc(spec, isActive, true);
        switch (pscType) {
            case INDIVIDUAL_BENEFICIAL_OWNER:
                buildIndividualBeneficialOwner(beneficialOwner);
                break;
            case CORPORATE_BENEFICIAL_OWNER:
                buildCorporateBeneficialOwner(beneficialOwner);
                break;
            default:
                throw new IllegalArgumentException("Unsupported beneficial owner type: " + pscType);
        }
        if (Boolean.TRUE.equals(spec.getCompanyWithPopulatedStructureOnly())) {
            return beneficialOwner;
        }
        return repository.save(beneficialOwner);
    }

    private void buildIndividualBeneficialOwner(CompanyPscs beneficialOwner) {
        beneficialOwner.setKind(PscType.INDIVIDUAL_BENEFICIAL_OWNER.getKind());
        beneficialOwner.setCountryOfResidence(addressService
                .getCountryOfResidence(JurisdictionType.NON_EU));
        beneficialOwner.setNationality(NATIONALITY);
        beneficialOwner.setDateOfBirth(new DateOfBirth(20, 9, 1975));

        beneficialOwner.setNameTitle(TITLE);
        beneficialOwner.setNameForename(FIRST_NAME);
        beneficialOwner.setNameSurname(LAST_NAME);
        beneficialOwner.setName(TITLE + " " + FIRST_NAME + " " + LAST_NAME);

        beneficialOwner.setSanctioned(false);
        beneficialOwner.setIsSanctioned(false);

        var links = new Links();
        links.setSelf(URL_PREFIX + beneficialOwner.getCompanyNumber()
                + PSC_SUFFIX + PscType.INDIVIDUAL_BENEFICIAL_OWNER.getLinkType()
                + "/" + beneficialOwner.getId());

        beneficialOwner.setUsualResidentialAddress(addressService.getAddress(JurisdictionType.NON_EU));
        beneficialOwner.setResidentialAddressSameAsServiceAddress(false);
    }

    private void buildCorporateBeneficialOwner(CompanyPscs beneficialOwner) {
        beneficialOwner.setKind(PscType.CORPORATE_BENEFICIAL_OWNER.getKind());
        beneficialOwner.setName("Overseas Corporate Company");

        var links = new Links();
        links.setSelf(URL_PREFIX + beneficialOwner.getCompanyNumber()
                + PSC_SUFFIX + PscType.CORPORATE_BENEFICIAL_OWNER.getLinkType()
                + "/" + beneficialOwner.getId());

        beneficialOwner.setSanctioned(false);
        beneficialOwner.setIsSanctioned(false);

        beneficialOwner.setPrincipalOfficeAddress(addressService.getAddress(JurisdictionType.NON_EU));

        var identification = new Identification();
        identification.setLegalAuthority(ROE_LEGAL_AUTHORITY);
        identification.setLegalForm(ROE_LEGAL_FORM);
        beneficialOwner.setIdentification(identification);
    }

    /**
     * Builds an individual PSC (filing name - PSC01).
     */
    private void buildIndividualPsc(CompanyPscs companyPsc, String pscType, String linkType) {
        companyPsc.setKind(pscType);
        companyPsc.setCountryOfResidence(addressService.getCountryOfResidence(JurisdictionType.WALES));
        companyPsc.setAddress(addressService.getAddress(JurisdictionType.WALES));
        companyPsc.setUsualResidentialAddress(addressService.getAddress(JurisdictionType.WALES));
        companyPsc.setResidentialAddressSameAsServiceAddress(false);
        companyPsc.setNationality(NATIONALITY);
        companyPsc.setDateOfBirth(new DateOfBirth(20, 9, 1975));

        NameElements nameElements = new NameElements();
        nameElements.setTitle("Mr");
        nameElements.setForename("Person");
        nameElements.setOtherForenames(FIRST_NAME);
        nameElements.setSurname("PSC");
        companyPsc.setNameElements(nameElements);
        companyPsc.setName(nameElements.getTitle() + " " + nameElements.getForename()
                + " " + nameElements.getOtherForenames() + " " + nameElements.getSurname());

        Links links = new Links();
        links.setSelf(URL_PREFIX + companyPsc.getCompanyNumber()
                + PSC_SUFFIX + linkType + "/" + companyPsc.getId());
        companyPsc.setLinks(links);
    }

    /**
     * Builds a Relevant Legal Entity PSC (filing name - PSC02).
     */
    private void buildCorporateEntityPsc(
           CompanyPscs companyPsc, String pscType, String linkType) {
       companyPsc.setKind(pscType);

       Identification identification = new Identification();
       identification.setCountryRegistered(
               addressService.getCountryOfResidence(JurisdictionType.ENGLAND_WALES));
       identification.setLegalAuthority(LEGAL_AUTHORITY);
       identification.setLegalForm(RLE_LEGAL_FORM);
       identification.setPlaceRegistered(addressService.getCountryOfResidence(JurisdictionType.ENGLAND));
       identification.setRegistrationNumber(REGISTRATION_NUMBER);
       companyPsc.setIdentification(identification);

       companyPsc.setName("Relevant Legal Entity (RLE) PSC");

       Links links = new Links();
       links.setSelf(URL_PREFIX + companyPsc.getCompanyNumber()
               + PSC_SUFFIX
               + linkType + "/" + companyPsc.getId());
       companyPsc.setLinks(links);
    }

    /**
     * Builds a Legal Person also known as Other Registrable Person (filing name - PSC03).
     */
    private void buildLegalPersonPsc(
            CompanyPscs companyPsc, String pscType, String linkType) {
        companyPsc.setKind(pscType);

        Identification identification = new Identification();
        identification.setCountryRegistered(
                addressService.getCountryOfResidence(JurisdictionType.SCOTLAND));
        identification.setLegalAuthority(LEGAL_AUTHORITY);
        identification.setLegalForm(ORP_LEGAL_FORM);
        companyPsc.setIdentification(identification);

        companyPsc.setName("Other Registrable Person (ORP) PSC");

        Links links = new Links();
        links.setSelf(URL_PREFIX + companyPsc.getCompanyNumber()
                + PSC_SUFFIX
                + linkType + "/" + companyPsc.getId());
        companyPsc.setLinks(links);
    }

    @Override
    public boolean delete(String companyNumber) {
        LOG.info("Attempting to delete PSCs for company number: " + companyNumber);

        Optional<List<CompanyPscs>> existingPscs = repository.findByCompanyNumber(companyNumber);
        if (existingPscs.isPresent()) {
            LOG.info("Found " + existingPscs.get().size() + " PSCs for company number: "+ companyNumber + ". Proceeding with deletion.");
            repository.deleteAll(existingPscs.get());
            LOG.info("Successfully deleted all PSCs for company number: "+  companyNumber);
            return true;
        } else {
            LOG.info("No PSCs found for company number: " + companyNumber);
            return false;
        }
    }
}
