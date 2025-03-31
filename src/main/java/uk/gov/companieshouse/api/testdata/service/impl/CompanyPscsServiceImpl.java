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
import uk.gov.companieshouse.api.testdata.repository.CompanyPscsRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CompanyPscsServiceImpl implements DataService<CompanyPscs, CompanySpec> {

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

    private final RandomService randomService;
    private final CompanyPscsRepository repository;
    private final AddressService addressService;

    @Autowired
    public CompanyPscsServiceImpl(RandomService randomService,
                                  CompanyPscsRepository repository, AddressService addressService) {
        this.randomService = randomService;
        this.repository = repository;
        this.addressService = addressService;
    }

    private enum PscType {
        INDIVIDUAL("individual-person-with-significant-control", "individual"),
        LEGAL_PERSON("legal-person-person-with-significant-control", "legal-person"),
        CORPORATE_ENTITY("corporate-entity-person-with-significant-control", "corporate-entity"),
        INDIVIDUAL_BENEFICIAL_OWNER("individual-beneficial-owner", "individual-beneficial-owner"),
        CORPORATE_BENEFICIAL_OWNER(
                "corporate-entity-beneficial-owner", "corporate-entity-beneficial-owner"),
        SUPER_SECURE_BENEFICIAL_OWNER(
                SUPER_SECURE_BO, SUPER_SECURE_BO),
        SUPER_SECURE_PSC("super-secure-person-with-significant-control", "super-secure");

        private final String kind;
        private final String linkType;

        PscType(String kind, String linkType) {
            this.kind = kind;
            this.linkType = linkType;
        }

        public String getKind() {
            return kind;
        }

        public String getLinkType() {
            return linkType;
        }
    }

    @Override
    public CompanyPscs create(CompanySpec spec) throws DataException {
        if (CompanyType.OVERSEA_COMPANY.equals(spec.getCompanyType())) {
            return null;
        }

        Boolean hasSuperSecurePscs = spec.getHasSuperSecurePscs();
        if (Boolean.TRUE.equals(hasSuperSecurePscs)) {
            if (CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(spec.getCompanyType())) {
                return createSuperSecureBeneficialOwner(spec);
            } else {
                return createSuperSecurePsc(spec);
            }
        } else if (CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(spec.getCompanyType())) {
            CompanyPscs individualBeneficialOwner = createBeneficialOwner(spec,
                    PscType.INDIVIDUAL_BENEFICIAL_OWNER);
            createBeneficialOwner(spec, PscType.CORPORATE_BENEFICIAL_OWNER);
            return individualBeneficialOwner;
        } else {
            CompanyPscs individualPsc = createPsc(spec, PscType.INDIVIDUAL);
            createPsc(spec, PscType.LEGAL_PERSON);
            createPsc(spec, PscType.CORPORATE_ENTITY);
            return individualPsc;
        }
    }

    private CompanyPscs createSuperSecureBeneficialOwner(CompanySpec spec) {
        CompanyPscs superSecureBo = createBasePsc(spec);
        buildSuperSecureBeneficialOwner(superSecureBo);
        return repository.save(superSecureBo);
    }

    private CompanyPscs createSuperSecurePsc(CompanySpec spec) {
        CompanyPscs superSecurePsc = createBasePsc(spec);
        buildSuperSecurePsc(superSecurePsc);
        return repository.save(superSecurePsc);
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

    private CompanyPscs createPsc(CompanySpec spec, PscType pscType) {
        var companyPscs = createBasePsc(spec);
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

    private CompanyPscs createBeneficialOwner(CompanySpec spec, PscType pscType) {
        var beneficialOwner = createBasePsc(spec);
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

    private CompanyPscs createBasePsc(CompanySpec spec) {
        var companyPsc = new CompanyPscs();
        companyPsc.setCompanyNumber(spec.getCompanyNumber());

        Instant createdUpdatedAt = getCreatedUpdatedAt(spec);
        companyPsc.setCreatedAt(createdUpdatedAt);
        companyPsc.setUpdatedAt(createdUpdatedAt);
        companyPsc.setNotifiedOn(createdUpdatedAt);
        companyPsc.setRegisterEntryDate(createdUpdatedAt);

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

    private void buildIndividualBeneficialOwner(CompanyPscs beneficialOwner) {
        beneficialOwner.setKind(PscType.INDIVIDUAL_BENEFICIAL_OWNER.getKind());
        beneficialOwner.setCountryOfResidence(addressService.getCountryOfResidence(
                Jurisdiction.WALES));
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

    private void setNaturesOfControl(CompanyPscs companyPsc) {
        List<String> nocList = Arrays.asList(NATURES_OF_CONTROL);
        Collections.shuffle(nocList);
        long num = randomService.getNumberInRange(0, NATURES_OF_CONTROL.length).orElse(0);
        companyPsc.setNaturesOfControl(new ArrayList<>(nocList.subList(0, ((int) num + 1))));
    }

    @Override
    public boolean delete(String companyNumber) {
        Optional<List<CompanyPscs>> existingPscs = repository.findByCompanyNumber(companyNumber);
        existingPscs.ifPresent(repository::deleteAll);
        return existingPscs.isPresent();
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
        identification.setCountryRegistered(addressService.getCountryOfResidence(
                Jurisdiction.UNITED_KINGDOM));
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
}