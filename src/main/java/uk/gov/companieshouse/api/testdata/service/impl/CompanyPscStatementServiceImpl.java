package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscStatementRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CompanyPscStatementServiceImpl implements DataService<CompanyPscStatement> {

    private static final int ID_LENGTH = 10;
    private static final int SALT_LENGTH = 8;

    @Autowired
    private RandomService randomService;

    @Autowired
    private CompanyPscStatementRepository repository;

    @Override
    public CompanyPscStatement create(CompanySpec spec) throws DataException {
        final String companyNumber = spec.getCompanyNumber();
        CompanyPscStatement companyPscStatement = new CompanyPscStatement();

        Instant dateTimeNow = Instant.now();
        Instant dateNow = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant();

        String id = this.randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH);
        companyPscStatement.setId(id);
        companyPscStatement.setUpdatedAt(dateTimeNow);
        companyPscStatement.setCompanyNumber(companyNumber);
        companyPscStatement.setPscStatementId(id);

        Links links = new Links();
        links.setSelf("/company/" + companyNumber + "/persons-with-significant-control-statements/" + id);

        companyPscStatement.setLinks(links);
        companyPscStatement.setNotifiedOn(dateNow);

        String etag = this.randomService.getEtag();
        companyPscStatement.setEtag(etag);

        companyPscStatement.setKind("persons-with-significant-control-statement");
        companyPscStatement.setStatement("no-individual-or-entity-with-significant-control");

        companyPscStatement.setCreatedAt(dateTimeNow);

        try {
            return repository.save(companyPscStatement);
        } catch (MongoException e) {
            throw new DataException("Failed to save PSC statement", e);
        }
    }

    @Override
    public boolean delete(String companyNumber) throws DataException {
        try {
            Optional<CompanyPscStatement> existingStatement = repository.findByCompanyNumber(companyNumber);
            existingStatement.ifPresent(repository::delete);
            return existingStatement.isPresent();
        } catch (MongoException e) {
            throw new DataException("Failed to delete PSC statement", e);
        }
    }
}
