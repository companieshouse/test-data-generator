package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Clock;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.entity.Identification;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.NameElements;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscsRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class CompanyPscsServiceImpl implements DataService<CompanyPscs> {

    protected static final String[] NATURES_OF_CONTROL = {"ownership-of-shares-25-to-50-percent", "ownership-of-shares-50-to-75-percent",
        "ownership-of-shares-75-to-100-percent", "ownership-of-shares-25-to-50-percent-as-trust", "ownership-of-shares-50-to-75-percent-as-trust"};
    private static final int ID_LENGTH = 10;
    private static final int SALT_LENGTH = 8;
    public static final String WALES = "Wales";

    private RandomService randomService;

    private CompanyPscsRepository repository;

    private Clock clock;

    @Autowired
    public CompanyPscsServiceImpl(RandomService randomService, CompanyPscsRepository repository, Clock clock) {
        this.randomService = randomService;
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    public CompanyPscs create(CompanySpec spec) throws DataException {

        CompanyPscs companyPsc = new CompanyPscs();
        final String companyNumber = spec.getCompanyNumber();
        companyPsc.setCompanyNumber(companyNumber);

        Instant dateNow = clock.instant();

        String id = this.randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH);
        companyPsc.setId(id);

        companyPsc.setAddressLine1("34 Silver Street");
        companyPsc.setAddressLine2("Silverstone");
        companyPsc.setCareOf("Care of");
        companyPsc.setCountry(WALES);
        companyPsc.setLocality("Cardiff");
        companyPsc.setPoBox("Po Box");
        companyPsc.setPostalCode("CF14 3UZ");
        companyPsc.setPremises("1");
        companyPsc.setRegion("UK");
        companyPsc.setAddressSameAsRegisteredOfficeAddress(true);

        companyPsc.setCeasedOn(dateNow);
        companyPsc.setCreatedAt(dateNow);

        String etag = this.randomService.getEtag();
        companyPsc.setEtag(etag);

        List<String> naturesOfControl = new ArrayList<>();
        for (long i = randomService.getNumberInRange(1,5).orElse(0); i > 0; i--) {
            naturesOfControl.add(
                    NATURES_OF_CONTROL[Math.toIntExact(randomService.getNumberInRange(0,NATURES_OF_CONTROL.length).orElse(0))]);
        }
        companyPsc.setNaturesOfControl(naturesOfControl);

        companyPsc.setNotifiedOn(dateNow);
        companyPsc.setReferenceEtag("reference etag");
        companyPsc.setReferencePscId("reference psc id");
        companyPsc.setRegisterEntryDate(dateNow);
        companyPsc.setUpdatedAt(dateNow);

        companyPsc.setStatementActionDate(dateNow);
        companyPsc.setStatementType("statement type");

        return repository.save(differentiatePsc(companyPsc));
    }

    @Override
    public boolean delete(String companyNumber) {
        Optional<List<CompanyPscs>> existingPscs = repository.findByCompanyNumber(companyNumber);
        existingPscs.ifPresent(repository::deleteAll);
        return existingPscs.isPresent();
    }

    private CompanyPscs differentiatePsc(CompanyPscs companyPsc) {

        String pscType;
        String linkType;
        Optional<List<CompanyPscs>> existingPscs = repository.findByCompanyNumber(companyPsc.getCompanyNumber());

        switch (existingPscs.orElse(new ArrayList<>()).size()){
            case 0:
                pscType = "individual-person-with-significant-control";
                linkType = "individual";
                buildIndividualPsc(companyPsc, pscType, linkType);
                break;
            case 1:
                pscType = "legal-person-person-with-significant-control";
                linkType = "legal-person";
                buildWithIdentificationPsc(companyPsc, pscType, linkType);
                break;
            default:
                pscType = "corporate-entity-person-with-significant-control";
                linkType = "corporate-entity";
                buildWithIdentificationPsc(companyPsc, pscType, linkType);
        }

        return companyPsc;
    }

    private CompanyPscs buildIndividualPsc(CompanyPscs companyPsc, String pscType, String linkType) {

        companyPsc.setKind(pscType);

        companyPsc.setCountryOfResidence(WALES);
        companyPsc.setNationality("British");
        companyPsc.setDateOfBirth(Instant.now().minus( 69, ChronoUnit.DAYS));

        NameElements nameElements = new NameElements();

        nameElements.setTitle("Mr");
        nameElements.setForename("Test");
        nameElements.setOtherForenames("Tester");
        nameElements.setSurname("Testington");
        companyPsc.setNameElements(nameElements);

        companyPsc.setName(nameElements.getTitle() + " " + nameElements.getForename()
                + " " + nameElements.getOtherForenames() + " " + nameElements.getSurname());

        companyPsc.setKind(pscType);

        Links links = new Links();
        links.setSelf("/company/" + companyPsc.getCompanyNumber() + "/persons-with-significant-control/" + linkType +"/" + companyPsc.getId());
        companyPsc.setLinks(links);

        return companyPsc;
    }

    private CompanyPscs buildWithIdentificationPsc(CompanyPscs companyPsc, String pscType, String linkType) {

        companyPsc.setKind(pscType);

        Identification identification = new Identification();
        identification.setCountryRegistered("UK");
        identification.setLegalAuthority("Legal Authority");
        identification.setLegalForm("Legal Form");
        identification.setPlaceRegistered(WALES);
        identification.setRegistrationNumber("123456");
        companyPsc.setIdentification(identification);

        companyPsc.setName("Mr A Jones");

        Links links = new Links();
        links.setSelf("/company/" + companyPsc.getCompanyNumber() + "/persons-with-significant-control/" + linkType +"/" + companyPsc.getId());
        companyPsc.setLinks(links);

        return companyPsc;
    }
}
