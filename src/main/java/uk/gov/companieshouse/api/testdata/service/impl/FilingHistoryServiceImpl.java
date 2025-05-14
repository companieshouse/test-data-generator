package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;
import uk.gov.companieshouse.api.testdata.exception.BarcodeServiceException;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AssociatedFiling;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.repository.FilingHistoryRepository;
import uk.gov.companieshouse.api.testdata.service.BarcodeService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class FilingHistoryServiceImpl implements DataService<FilingHistory, CompanySpec> {

    private static final int SALT_LENGTH = 8;
    private static final int ENTITY_ID_LENGTH = 9;
    private static final String ENTITY_ID_PREFIX = "8";
    private static final String CATEGORY = "incorporation";
    private static final String DESCRIPTION = "incorporation-company";
    private static final String TYPE = "NEWINC";
    private static final String ORIGINAL_DESCRIPTION =
            "Certificate of incorporation general company details & statements of; "
                    + "officers, capital & shareholdings, guarantee, "
                    + "compliance memorandum of association";
    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(FilingHistoryServiceImpl.class));

    @Autowired
    private FilingHistoryRepository filingHistoryRepository;
    @Autowired
    private RandomService randomService;
    @Autowired
    private BarcodeService barcodeService;

    @Override
    public FilingHistory create(CompanySpec spec) throws DataException {
        LOG.info("Starting creation of FilingHistory for company number: "
                + spec.getCompanyNumber());

        String barcode;
        final String accountsDueStatus = spec.getAccountsDueStatus();
        try {
            LOG.debug("Attempting to retrieve barcode for company number: "
                    + spec.getCompanyNumber());
            barcode = barcodeService.getBarcode();
            LOG.debug("Successfully retrieved barcode: " + barcode);
        } catch (BarcodeServiceException ex) {
            LOG.error("Failed to retrieve barcode for company number: "
                    + spec.getCompanyNumber(), ex);
            throw new DataException(ex.getMessage(), ex);
        }

        FilingHistory filingHistory = new FilingHistory();
        Instant dayTimeNow = Instant.now();
        Instant dayNow = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant();

        if (StringUtils.hasText(accountsDueStatus)) {
            LOG.debug("Generating accounts due date for status: " + accountsDueStatus);
            var dueDateNow = randomService.generateAccountsDueDateByStatus(accountsDueStatus);
            dayTimeNow = dueDateNow.atTime(LocalTime.now()).atZone(ZoneId.of("UTC")).toInstant();
            dayNow = dueDateNow.atStartOfDay(ZoneId.of("UTC")).toInstant();
        }

        String entityId = ENTITY_ID_PREFIX + this.randomService.getNumber(ENTITY_ID_LENGTH);
        LOG.debug("Generated entity ID: " + entityId);

        var hasFilingHistory = spec.getFilingHistory() != null;

        filingHistory.setId(randomService.addSaltAndEncode(entityId, SALT_LENGTH));
        filingHistory.setCompanyNumber(spec.getCompanyNumber());
        filingHistory.setLinks(createLinks(filingHistory));
        filingHistory.setAssociatedFilings(createAssociatedFilings(dayTimeNow, dayNow));
        filingHistory.setCategory(hasFilingHistory
                ? spec.getFilingHistory().getCategory() : CATEGORY);
        filingHistory.setDescription(hasFilingHistory
                ? spec.getFilingHistory().getDescription() : DESCRIPTION);
        filingHistory.setDate(dayTimeNow);
        filingHistory.setType(hasFilingHistory ? spec.getFilingHistory().getType() : TYPE);
        filingHistory.setPages(10);
        filingHistory.setEntityId(entityId);
        filingHistory.setOriginalDescription(hasFilingHistory
                ? spec.getFilingHistory().getOriginalDescription() : ORIGINAL_DESCRIPTION);
        filingHistory.setBarcode(barcode);

        LOG.info("FilingHistory object created for company number: " + spec.getCompanyNumber());

        var savedFilingHistory = filingHistoryRepository.save(filingHistory);
        LOG.info("FilingHistory successfully saved with ID: " + savedFilingHistory.getId());

        return savedFilingHistory;
    }

    @Override
    public boolean delete(String companyId) {
        LOG.info("Attempting to delete FilingHistory for company number: " + companyId);

        Optional<FilingHistory> filingHistory =
                filingHistoryRepository.findByCompanyNumber(companyId);

        if (filingHistory.isPresent()) {
            LOG.info("FilingHistory found for company number: "
                    + companyId + ". Proceeding with deletion.");
            filingHistoryRepository.delete(filingHistory.get());
            LOG.info("Successfully deleted FilingHistory for company number: " + companyId);
            return true;
        } else {
            LOG.info("No FilingHistory found for company number: " + companyId);
            return false;
        }
    }

    private List<AssociatedFiling> createAssociatedFilings(Instant dayTimeNow, Instant dayNow) {

        ArrayList<AssociatedFiling> associatedFilings = new ArrayList<>();

        AssociatedFiling incorporation = new AssociatedFiling();
        incorporation.setCategory("incorporation");
        incorporation.setDate(dayTimeNow);
        incorporation.setDescription("model-articles-adopted");
        incorporation.setType("MODEL ARTICLES");
        associatedFilings.add(incorporation);

        AssociatedFiling capital = new AssociatedFiling();
        capital.setActionDate(dayNow);
        capital.setCategory("capital");
        capital.setDate(dayTimeNow);
        capital.setDescription("statement-of-capital");

        HashMap<String, Object> descriptionValues = new HashMap<>();

        ArrayList<HashMap<String, String>> descriptionValueCapital = new ArrayList<>();
        HashMap<String, String> descriptionValueCapitalHashMap = new HashMap<>();
        descriptionValueCapitalHashMap.put("currency", "GBP");
        descriptionValueCapitalHashMap.put("figure", "1");
        descriptionValueCapital.add(descriptionValueCapitalHashMap);

        descriptionValues.put("capital", descriptionValueCapital);
        descriptionValues.put("date", dayNow);

        capital.setDescriptionValues(descriptionValues);
        capital.setOriginalDescription("11/09/19 Statement of Capital;GBP 1");
        capital.setType("SH01");

        associatedFilings.add(capital);

        return associatedFilings;

    }

    private Links createLinks(FilingHistory filingHistory) {

        Links links = new Links();
        links.setSelf("/company/"
                + filingHistory.getCompanyNumber() + "/filing-history/" + filingHistory.getId());

        return links;
    }
}
