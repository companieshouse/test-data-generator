package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
import uk.gov.companieshouse.api.testdata.repository.filinghistory.FilingHistoryRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.TestDataHelperService;

@Service
public class FilingHistoryServiceImpl implements DataService<FilingHistory> {

    private static final String FILING_HISTORY_DATA_NOT_FOUND = "filing history data not found";

    private TestDataHelperService testDataHelperService;
    private FilingHistoryRepository filingHistoryRepository;

    private FilingHistory filingHistory;
    private Random rnd = new Random();

    @Autowired
    public FilingHistoryServiceImpl(TestDataHelperService testDataHelperService,
                                    FilingHistoryRepository filingHistoryRepository) {
        this.testDataHelperService = testDataHelperService;
        this.filingHistoryRepository = filingHistoryRepository;
    }

    @Override
    public FilingHistory create(String companyNumber) throws DataException {

        filingHistory = new FilingHistory();

        filingHistory.setId(testDataHelperService.getNewId());

        filingHistory.setCompanyNumber(companyNumber);
        filingHistory.setTotalCount(1);
        filingHistory.setFilingHistoryItems(createItems());

        try {
            filingHistoryRepository.save(filingHistory);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }

        return filingHistory;
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

    private List<FilingHistoryItem> createItems() {

        List<FilingHistoryItem> filingHistoryItems = new ArrayList<>();
        FilingHistoryItem filingHistoryItem = new FilingHistoryItem();
        filingHistoryItem.setType("NEWINC");
        filingHistoryItem.setCategory("incorporation");
        filingHistoryItem.setDate(new Date());
        filingHistoryItem.setDescription("incorporation-company");
        filingHistoryItem.setTransactionId(getNewTransactionId());
        filingHistoryItem.setLinks(createLinks());

        filingHistoryItems.add(filingHistoryItem);

        return filingHistoryItems;
    }

    private Links createLinks() {

        Links links = new Links();
        links.setSelf("/company/" + filingHistory.getCompanyNumber() + "/filing-history/" + filingHistory.getId());

        return links;
    }

    private String getNewTransactionId() {

        String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();

        while (salt.length() < 18) {
            int index = rnd.nextInt(saltChars.length());
            salt.append(saltChars.charAt(index));
        }
        return salt.toString();
    }

}
