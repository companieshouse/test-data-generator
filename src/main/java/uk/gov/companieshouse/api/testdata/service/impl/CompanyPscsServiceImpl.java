package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.entity.DateOfBirth;
import uk.gov.companieshouse.api.testdata.model.entity.Identification;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.NameElements;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.model.rest.PscType;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscsRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CompanyPscsServiceImpl implements DataService<CompanyPscs, CompanySpec> {

    private static final Logger LOG = LoggerFactory.getLogger(String.valueOf(CompanyPscsServiceImpl.class));

    static final String[] NATURES_OF_CONTROL = {"ownership-of-shares-25-to-50-percent",
            "ownership-of-shares-50-to-75-percent",
            "ownership-of-shares-75-to-100-percent",
            "ownership-of-shares-25-to-50-percent-as-trust",
            "ownership-of-shares-50-to-75-percent-as-trust"};
    private static final int ID_LENGTH = 10;
    private static final int SALT_LENGTH = 8;
    public static final String NATIONALITY = "BRITISH";
    public static final String REGISTRATION_NUMBER = "12345678";
    public static final String LEGAL_FORM = "Legal Form";
    public static final String LEGAL_AUTHORITY = "Legal Authority";
    public static final String SUPER_SECURE_BO = "super-secure-beneficial-owner";
    public static final String URL_PREFIX = "/company/";
    public static final String PSC_SUFFIX = "/persons-with-significant-control/";
    public static final String TITLE = "Dr.";
    public static final String FIRST_NAME = "First";
    public static final String LAST_NAME = "Last";
    private static final int DEFAULT_NUMBER_OF_PSC = 0;
    private static final String PSC_ERROR_MESSAGE = "psc_type must be accompanied by number_of_psc";
    private static final String BENEFICIAL_OWNER_ERROR =
            "Beneficial owner type is not allowed for this company type";

    private final RandomService randomService;
    private final CompanyPscsRepository repository;
    private final AddressService addressService;

    @Autowired
    public CompanyPscsServiceImpl(RandomService randomService,
                                  CompanyPscsRepository repository,
                                  AddressService addressService) {
        this.randomService = randomService;
        this.repository = repository;
        this.addressService = addressService;
    }

    @Override
    public CompanyPscs create(CompanySpec spec) throws DataException {
        LOG.info("Starting creation of PSCs for company number: " + spec.getCompanyNumber());

        if (shouldReturnNullForOverseaCompany(spec)) {
            LOG.info("Company type is OVERSEA_COMPANY. No PSCs will be created.");
            return null;
        }

        if (shouldCreateSuperSecurePsc(spec)) {
            LOG.info("Company has super secure PSCs. Creating super secure PSC.");
            return createAppropriateSuperSecurePsc(spec);
        }

        validatePscTypeAndCount(spec);

        int numberOfPsc = getNumberOfPsc(spec);
        if (numberOfPsc <= 0) {
            LOG.info("Number of PSCs is less than or equal to 0. No PSCs will be created.");
            return null;
        }

        LOG.info("Creating " + numberOfPsc + " PSCs for company number: " + spec.getCompanyNumber());
        return createPscsBasedOnCompanyType(spec, numberOfPsc);
    }

    private boolean shouldReturnNullForOverseaCompany(CompanySpec spec) {
        boolean result = CompanyType.OVERSEA_COMPANY.equals(spec.getCompanyType());
        LOG.debug("shouldReturnNullForOverseaCompany: " + result);
        return result;
    }

    private boolean shouldCreateSuperSecurePsc(CompanySpec spec) {
        boolean result = Boolean.TRUE.equals(spec.getHasSuperSecurePscs());
        LOG.debug("shouldCreateSuperSecurePsc: " + result);
        return result;
    }

    private CompanyPscs createAppropriateSuperSecurePsc(CompanySpec spec) {
        return CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(spec.getCompanyType())
                ? createSuperSecureBeneficialOwner(spec)
                : createSuperSecurePsc(spec);
    }

    private void validatePscTypeAndCount(CompanySpec spec) throws DataException {
        if (hasPscTypesWithoutCount(spec)) {
            LOG.error("Validation failed: " +  PSC_ERROR_MESSAGE);
            throw new DataException(PSC_ERROR_MESSAGE);
        }
        if (hasInvalidBeneficialOwnerType(spec)) {
            LOG.error("Validation failed: " + BENEFICIAL_OWNER_ERROR);
            throw new DataException(BENEFICIAL_OWNER_ERROR);
        }
        LOG.debug("Validation passed for PSC type and count.");
    }

    private boolean hasPscTypesWithoutCount(CompanySpec spec) {
        return spec.getPscType() != null
                && !spec.getPscType().isEmpty()
                && (spec.getNumberOfPscs() == null || spec.getNumberOfPscs() <= 0);
    }

    private boolean hasInvalidBeneficialOwnerType(CompanySpec spec) {
        return spec.getPscType() != null
                && spec.getPscType().stream().anyMatch(this::isBeneficialOwnerType)
                && !CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(spec.getCompanyType());
    }

    private boolean isBeneficialOwnerType(PscType type) {
        return type == PscType.INDIVIDUAL_BENEFICIAL_OWNER || type == PscType.CORPORATE_BENEFICIAL_OWNER;
    }

    private int getNumberOfPsc(CompanySpec spec) {
        int numberOfPsc = Optional.ofNullable(spec.getNumberOfPscs()).orElse(DEFAULT_NUMBER_OF_PSC);
        LOG.debug("Number of PSCs determined: " + numberOfPsc);
        return numberOfPsc;
    }

    private CompanyPscs createPscsBasedOnCompanyType(CompanySpec spec, int numberOfPsc) {
        LOG.info("Creating PSCs based on company type: " + spec.getCompanyType());

        if (numberOfPsc <= 0) {
            LOG.info("No PSCs requested for company number: " + spec.getCompanyNumber() + ". Returning null.");
            return null;
        }

        CompanyPscs firstPsc = null;
        boolean isOverseasEntity = CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(spec.getCompanyType());

        boolean ceaseFirstPsc = spec.getPscActive() != null && !spec.getPscActive();

        for (var i = 0; i < numberOfPsc; i++) {
            LOG.debug("Creating PSC " + (i + 1) + " of " + numberOfPsc);

            boolean isActive = !(ceaseFirstPsc && i == 0);

            CompanyPscs psc = isOverseasEntity
                    ? createBeneficialOwner(spec, getBeneficialOwnerType(spec.getPscType(), i), isActive)
                    : createPsc(spec, getRegularPscType(spec.getPscType(), i), isActive);

            if (firstPsc == null) {
                firstPsc = psc;
            }
        }

        LOG.info("Successfully created PSCs for company number: " + spec.getCompanyNumber());
        return firstPsc;
    }

    private PscType getBeneficialOwnerType(List<PscType> requestedPscTypes, int index) {
        if (requestedPscTypes != null && !requestedPscTypes.isEmpty()) {
            return requestedPscTypes.get(index % requestedPscTypes.size());
        }
        return index % 2 == 0 ? PscType.INDIVIDUAL_BENEFICIAL_OWNER
                : PscType.CORPORATE_BENEFICIAL_OWNER;
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

    private CompanyPscs createSuperSecureBeneficialOwner(CompanySpec spec) {
        CompanyPscs superSecureBo = createBasePsc(spec, true);
        buildSuperSecureBeneficialOwner(superSecureBo);
        return repository.save(superSecureBo);
    }

    private CompanyPscs createSuperSecurePsc(CompanySpec spec) {
        CompanyPscs superSecurePsc = createBasePsc(spec, true);
        buildSuperSecurePsc(superSecurePsc);
        return repository.save(superSecurePsc);
    }

    CompanyPscs createBasePsc(CompanySpec spec, boolean isActive) {
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
        companyPsc.setAddress(addressService.getAddress(Jurisdiction.ENGLAND_WALES));

        setNaturesOfControl(companyPsc);

        return companyPsc;
    }

    private Instant getCreatedUpdatedAt(CompanySpec spec) {
        if (StringUtils.hasText(spec.getAccountsDueStatus())) {
            LocalDate dueDateNow = randomService.generateAccountsDueDateByStatus(
                    spec.getAccountsDueStatus());
            return dueDateNow.atStartOfDay(ZoneId.of("UTC")).toInstant();
        }
        return LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant();
    }

    private void setNaturesOfControl(CompanyPscs companyPsc) {
        List<String> nocList = Arrays.asList(NATURES_OF_CONTROL);
        Collections.shuffle(nocList);
        long num = randomService.getNumberInRange(0, NATURES_OF_CONTROL.length).orElse(0);
        companyPsc.setNaturesOfControl(new ArrayList<>(nocList.subList(0, (int) num + 1)));
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

    private CompanyPscs createPsc(CompanySpec spec, PscType pscType, boolean isActive) {
        var companyPscs = createBasePsc(spec, isActive);
        switch (pscType) {
            case INDIVIDUAL:
                buildIndividualPsc(companyPscs, pscType.getKind(), pscType.getLinkType());
                break;
            case LEGAL_PERSON, CORPORATE_ENTITY:
                buildWithIdentificationPsc(companyPscs, pscType.getKind(), pscType.getLinkType());
                break;
            default:
                throw new IllegalArgumentException("Unsupported PSC type: " + pscType);
        }
        return repository.save(companyPscs);
    }

    private CompanyPscs createBeneficialOwner(CompanySpec spec, PscType pscType, boolean isActive) {
        var beneficialOwner = createBasePsc(spec, isActive);
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
        return repository.save(beneficialOwner);
    }

    private void buildIndividualBeneficialOwner(CompanyPscs beneficialOwner) {
        beneficialOwner.setKind(PscType.INDIVIDUAL_BENEFICIAL_OWNER.getKind());
        beneficialOwner.setCountryOfResidence(addressService
                .getCountryOfResidence(Jurisdiction.WALES));
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

        beneficialOwner.setUsualResidentialAddress(addressService.getAddress(Jurisdiction.NON_EU));
        beneficialOwner.setResidentialAddressSameAsServiceAddress(false);
    }

    private void buildCorporateBeneficialOwner(CompanyPscs beneficialOwner) {
        beneficialOwner.setKind(PscType.CORPORATE_BENEFICIAL_OWNER.getKind());
        beneficialOwner.setName(FIRST_NAME + " " + LAST_NAME);

        var links = new Links();
        links.setSelf(URL_PREFIX + beneficialOwner.getCompanyNumber()
                + PSC_SUFFIX + PscType.CORPORATE_BENEFICIAL_OWNER.getLinkType()
                + "/" + beneficialOwner.getId());

        beneficialOwner.setSanctioned(false);
        beneficialOwner.setIsSanctioned(false);

        beneficialOwner.setPrincipalOfficeAddress(addressService.getAddress(Jurisdiction.NON_EU));

        var identification = new Identification();
        identification.setLegalAuthority(LEGAL_AUTHORITY);
        identification.setLegalForm(LEGAL_FORM);
        beneficialOwner.setIdentification(identification);
    }

    private void buildIndividualPsc(CompanyPscs companyPsc, String pscType, String linkType) {
        companyPsc.setKind(pscType);
        companyPsc.setCountryOfResidence(addressService.getCountryOfResidence(Jurisdiction.WALES));
        companyPsc.setAddress(addressService.getAddress(Jurisdiction.SCOTLAND));
        companyPsc.setUsualResidentialAddress(addressService.getAddress(Jurisdiction.WALES));
        companyPsc.setResidentialAddressSameAsServiceAddress(false);
        companyPsc.setNationality(NATIONALITY);
        companyPsc.setDateOfBirth(new DateOfBirth(20, 9, 1975));

        NameElements nameElements = new NameElements();
        nameElements.setTitle("Mr");
        nameElements.setForename("Test");
        nameElements.setOtherForenames("Tester");
        nameElements.setSurname("Testington");
        companyPsc.setNameElements(nameElements);
        companyPsc.setName(nameElements.getTitle() + " " + nameElements.getForename()
                + " " + nameElements.getOtherForenames() + " " + nameElements.getSurname());

        Links links = new Links();
        links.setSelf(URL_PREFIX + companyPsc.getCompanyNumber()
                + PSC_SUFFIX + linkType + "/" + companyPsc.getId());
        companyPsc.setLinks(links);
    }

    private void buildWithIdentificationPsc(
            CompanyPscs companyPsc, String pscType, String linkType) {
        companyPsc.setKind(pscType);

        Identification identification = new Identification();
        identification.setCountryRegistered(
                addressService.getCountryOfResidence(Jurisdiction.UNITED_KINGDOM));
        identification.setLegalAuthority(LEGAL_AUTHORITY);
        identification.setLegalForm(LEGAL_FORM);
        identification.setPlaceRegistered(addressService.getCountryOfResidence(Jurisdiction.WALES));
        identification.setRegistrationNumber(REGISTRATION_NUMBER);
        companyPsc.setIdentification(identification);

        companyPsc.setName("Mr A Jones");

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