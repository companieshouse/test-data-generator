package com.poc.testdata.service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import com.poc.testdata.constants.ErrorMessageConstants;
import com.poc.testdata.exception.DataException;
import com.poc.testdata.exception.NoDataFoundException;
import com.poc.testdata.model.FilingHistory.FilingHistory;
import com.poc.testdata.model.FilingHistory.FilingHistoryItem;
import com.poc.testdata.model.Links;
import com.poc.testdata.repository.FilingHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class FilingHistoryService {

    @Autowired
    TestDataHelperService testDataHelperService;
    @Autowired
    FilingHistoryRepository filingHistoryRepository;

    FilingHistory filingHistory;

    private final String FILING_HISTORY_DATA_NOT_FOUND = "filing history data not found";

    public void create(String companyNumber) throws DataException {

        filingHistory = new FilingHistory();

        filingHistory.setId(testDataHelperService.getNewId());

        filingHistory.setCompanyNumber(companyNumber);
        filingHistory.setTotalCount(1);
        filingHistory.setFilingHistoryItems(createItems());

        try{
            filingHistoryRepository.save(filingHistory);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }

    }

    public void delete(String companyId) throws NoDataFoundException, DataException {

        FilingHistory filingHistory = filingHistoryRepository.findByCompanyNumber(companyId);

        if(filingHistory == null) throw new NoDataFoundException(FILING_HISTORY_DATA_NOT_FOUND);

        try {
            filingHistoryRepository.delete(filingHistory);
        } catch (MongoException e) {
            throw new DataException(ErrorMessageConstants.FAILED_TO_DELETE);
        }
    }

    private List<FilingHistoryItem> createItems(){

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

    private Links createLinks(){

        Links links = new Links();
        links.setSelf("/company/"+ filingHistory.getCompanyNumber() + "/filing-history/" + filingHistory.getId());

        return links;
    }

    private String getNewTransactionId(){

        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

}
