package uk.gov.companieshouse.api.testdata.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.constants.ErrorMessageConstants;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscStatementRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class CompanyPscStatementServiceImpl implements DataService<CompanyPscStatement> {

    private static final String STATEMENT_DATA_NOT_FOUND = "statement data not found";
    private static final int ID_LENGTH = 10;
    private static final int SALT_LENGTH = 8;

    @Autowired
    private RandomService randomService;

    @Autowired
    private CompanyPscStatementRepository repository;

    @Override
    public CompanyPscStatement create(String companyNumber) throws DataException {
        CompanyPscStatement companyPscStatement = new CompanyPscStatement();

        Instant dateTimeNow = Instant.now();
        Instant dateNow = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();

        String id = this.randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH);
        companyPscStatement.setId(id);
        companyPscStatement.setUpdatedAt(dateTimeNow);
        companyPscStatement.setCompanyNumber(companyNumber);
        companyPscStatement.setPscStatementId(id);

        Links links = new Links();
        links.setSelf("/company/" + companyNumber + "/persons-with-significant-control-statements/" + id);

        companyPscStatement.setDataLinks(links);
        companyPscStatement.setDataNotifiedOn(dateNow);

        String etag = this.randomService.getEtag();
        companyPscStatement.setDataEtag(etag);

        companyPscStatement.setDataKind("persons-with-significant-control-statement");
        companyPscStatement.setDataStatement("no-individual-or-entity-with-significant-control");

        companyPscStatement.setCreatedAt(dateTimeNow);

        try {
            return repository.save(companyPscStatement);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }
    }

    @Override
    public void delete(String companyNumber) throws NoDataFoundException, DataException {
        CompanyPscStatement existingStatement = repository.findByCompanyNumber(companyNumber);

        if (existingStatement == null) {
            throw new NoDataFoundException(STATEMENT_DATA_NOT_FOUND);
        }

        try {
            repository.delete(existingStatement);
        } catch (MongoException e) {
            throw new DataException(ErrorMessageConstants.FAILED_TO_DELETE);
        }
    }
}
