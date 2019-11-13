package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.constants.ErrorMessageConstants;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistoryItem;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.repository.FilingHistoryRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class FilingHistoryServiceImpl implements DataService<FilingHistory> {

    private static final int SALT_LENGTH = 8;
    private static final int ID_LENGTH = 10;
    private static final String FILING_HISTORY_DATA_NOT_FOUND = "filing history data not found";

    @Autowired
    private FilingHistoryRepository filingHistoryRepository;
    @Autowired
    private RandomService randomService;

    @Override
    public FilingHistory create(String companyNumber) throws DataException {

        FilingHistory filingHistory = new FilingHistory();

        filingHistory.setId(randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH));

        filingHistory.setCompanyNumber(companyNumber);
        filingHistory.setTotalCount(1);
        filingHistory.setFilingHistoryItems(createItems(filingHistory));

        try {
            return filingHistoryRepository.save(filingHistory);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }
    }

    @Override
    public void delete(String companyId) throws NoDataFoundException, DataException {

        FilingHistory filingHistoryToDelete = filingHistoryRepository.findByCompanyNumber(companyId);

        if (filingHistoryToDelete == null) {
            throw new NoDataFoundException(FILING_HISTORY_DATA_NOT_FOUND);
        }

        try {
            filingHistoryRepository.delete(filingHistoryToDelete);
        } catch (MongoException e) {
            throw new DataException(ErrorMessageConstants.FAILED_TO_DELETE);
        }
    }

    private List<FilingHistoryItem> createItems(FilingHistory filingHistory) {

        List<FilingHistoryItem> filingHistoryItems = new ArrayList<>();
        FilingHistoryItem filingHistoryItem = new FilingHistoryItem();
        filingHistoryItem.setType("NEWINC");
        filingHistoryItem.setCategory("incorporation");
        filingHistoryItem.setDate(new Date());
        filingHistoryItem.setDescription("incorporation-company");
        filingHistoryItem.setTransactionId(getNewTransactionId());
        filingHistoryItem.setLinks(createLinks(filingHistory));

        filingHistoryItems.add(filingHistoryItem);

        return filingHistoryItems;
    }

    private Links createLinks(FilingHistory filingHistory) {

        Links links = new Links();
        links.setSelf("/company/" + filingHistory.getCompanyNumber() + "/filing-history/" + filingHistory.getId());

        return links;
    }

    private String getNewTransactionId() {
        return randomService.getString(18);
    }

}
