package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.repository.BacklogRepository;
import uk.gov.companieshouse.api.testdata.service.BacklogService;

@Service
public class BacklogServiceImpl implements BacklogService {

    private final BacklogRepository backlogRepository;

    @Autowired
    public BacklogServiceImpl(BacklogRepository backlogRepository) {
        this.backlogRepository = backlogRepository;
    }

    @Override
    public void updateBacklogCaseId(String backlogId, String caseId)
            throws NoDataFoundException, DataException {

        var backlog = backlogRepository.findById(backlogId)
                .orElseThrow(() -> new NoDataFoundException("backlog not found"));

        backlog.setCaseId(caseId);

        try {
            backlogRepository.save(backlog);
        } catch (Exception ex) {
            throw new DataException("failed to update backlog", ex);
        }
    }
}