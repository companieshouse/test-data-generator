package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyType;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscStatementRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CompanyPscStatementServiceImpl implements
        DataService<CompanyPscStatement, CompanySpec> {

    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");
    private static final int ID_LENGTH = 10;
    private static final int SALT_LENGTH = 8;
    private static final String PSC_SUFFIX = "/persons-with-significant-control-statements/";

    @Autowired
    private CompanyPscStatementRepository repository;

    @Autowired
    private RandomService randomService;

    @Override
    public CompanyPscStatement create(CompanySpec spec) {
        CompanyPscStatement pscStatement = new CompanyPscStatement();

        String pscStatementId = randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH);
        pscStatement.setId(pscStatementId);
        pscStatement.setCompanyNumber(spec.getCompanyNumber());
        pscStatement.setPscStatementId(pscStatementId);
        pscStatement.setEtag(randomService.getEtag());
        pscStatement.setKind("persons-with-significant-control-statement");

        pscStatement.setNotifiedOn(LocalDate.now().atStartOfDay(ZONE_ID_UTC).toInstant());

        if (spec.getCompanyType() == CompanyType.REGISTERED_OVERSEAS_ENTITY) {
            pscStatement.setStatement(PscStatement.ALL_BENEFICIAL_OWNERS_IDENTIFIED.getStatement());
        } else if (spec.getNumberOfPsc() != null && spec.getNumberOfPsc() > 0) {
            pscStatement.setStatement(PscStatement.PSC_EXISTS_BUT_NOT_IDENTIFIED.getStatement());
        } else {
            pscStatement.setStatement(
                    PscStatement.NO_INDIVIDUAL_OR_ENTITY_WITH_SIGNIFICANT_CONTROL.getStatement());
        }

        Links links = new Links();
        links.setSelf("/company/" + spec.getCompanyNumber() + PSC_SUFFIX + pscStatementId);
        pscStatement.setLinks(links);

        pscStatement.setCreatedAt(Instant.now());
        pscStatement.setUpdatedAt(pscStatement.getCreatedAt());

        return repository.save(pscStatement);
    }

    public List<CompanyPscStatement> createPscStatements(CompanySpec spec) {
        List<CompanyPscStatement> createdStatements = new ArrayList<>();

        Integer withdrawnPscStatementsCount = spec.getWithdrawnPscStatements();
        Integer activePscStatementsCount = spec.getActivePscStatements();

        boolean specificWithdrawnRequested = withdrawnPscStatementsCount
                != null && withdrawnPscStatementsCount > 0;
        boolean specificActiveRequested = activePscStatementsCount
                != null && activePscStatementsCount > 0;

        if (specificWithdrawnRequested || specificActiveRequested) {
            if (specificWithdrawnRequested) {
                addWithdrawnPscStatements(spec, withdrawnPscStatementsCount, createdStatements);
            }
            if (specificActiveRequested) {
                addActivePscStatements(spec, activePscStatementsCount, createdStatements);
            }
        } else {
            createdStatements.add(this.create(spec));
        }
        return createdStatements;
    }

    private void addWithdrawnPscStatements(CompanySpec originalSpec,
                                           int count, List<CompanyPscStatement> createdStatements) {
        for (int i = 0; i < count; i++) {
            var tempSpec = new CompanySpec();
            tempSpec.setCompanyNumber(originalSpec.getCompanyNumber());
            tempSpec.setCompanyType(originalSpec.getCompanyType());
            tempSpec.setAccountsDueStatus(originalSpec.getAccountsDueStatus());
            tempSpec.setWithdrawnPscStatements(1);
            tempSpec.setNumberOfPsc(0);
            createdStatements.add(this.create(tempSpec));
        }
    }

    private void addActivePscStatements(CompanySpec originalSpec,
                                        int count, List<CompanyPscStatement> createdStatements) {
        for (int i = 0; i < count; i++) {
            var tempSpec = new CompanySpec();
            tempSpec.setCompanyNumber(originalSpec.getCompanyNumber());
            tempSpec.setCompanyType(originalSpec.getCompanyType());
            tempSpec.setAccountsDueStatus(originalSpec.getAccountsDueStatus());
            tempSpec.setWithdrawnPscStatements(0);
            tempSpec.setNumberOfPsc(1);
            createdStatements.add(this.create(tempSpec));
        }
    }

    @Override
    public boolean delete(String companyNumber) {
        List<CompanyPscStatement> statementsToDelete
                = repository.findAllByCompanyNumber(companyNumber);

        if (!statementsToDelete.isEmpty()) {
            repository.deleteAll(statementsToDelete);
            return true;
        }
        return false;
    }

    public enum PscStatement {
        NO_INDIVIDUAL_OR_ENTITY_WITH_SIGNIFICANT_CONTROL(
                "no-individual-or-entity-with-signficant-control"),
        PSC_EXISTS_BUT_NOT_IDENTIFIED("psc-exists-but-not-identified"),
        ALL_BENEFICIAL_OWNERS_IDENTIFIED("all-beneficial-owners-identified"),
        BENEFICIAL_ACTIVE_OR_CEASED("somebody-has-become-or-ceased-to-be-a-beneficial-owner"),
        NO_ACTIVE_BENEFICIAL_OWNER("nobody-has-become-or-ceased-to-be-a-beneficial-owner"),
        AT_LEAST_ONE_BENEFICIAL_OWNER("at-least-one-beneficial-owner-unidentified");
        private final String statement;

        PscStatement(String statement) {
            this.statement = statement;
        }

        public String getStatement() {
            return statement;
        }
    }
}