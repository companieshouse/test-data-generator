package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

@Service
public class FilingHistoryServiceImpl implements DataService<FilingHistory,CompanySpec> {

    private static final int SALT_LENGTH = 8;
    private static final int ENTITY_ID_LENGTH = 9;
    private static final String ENTITY_ID_PREFIX = "8";
 
    @Autowired
    private FilingHistoryRepository filingHistoryRepository;
    @Autowired
    private RandomService randomService;
    @Autowired
    private BarcodeService barcodeService;

    @Override
    public FilingHistory create(CompanySpec spec) throws DataException {
        String barcode;
        boolean hasFilingHistory = false;

        try {
            barcode = barcodeService.getBarcode();
        } catch (BarcodeServiceException ex) {
            throw new DataException(ex.getMessage(), ex);
        }

        if (spec.getFilingHistory() != null) {
            hasFilingHistory = true;
        }

        FilingHistory filingHistory = new FilingHistory();
        Instant dayTimeNow = Instant.now();
        Instant dayNow = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant();

        String entityId = ENTITY_ID_PREFIX + this.randomService.getNumber(ENTITY_ID_LENGTH);

        filingHistory.setId(randomService.addSaltAndEncode(entityId, SALT_LENGTH));
        filingHistory.setCompanyNumber(spec.getCompanyNumber());
        filingHistory.setLinks(createLinks(filingHistory));
        filingHistory.setAssociatedFilings(createAssociatedFilings(dayTimeNow, dayNow));
        filingHistory.setCategory(hasFilingHistory ? spec.getFilingHistory().getCategory() :"incorporation");
        filingHistory.setDescription(hasFilingHistory ? spec.getFilingHistory().getDescription() :"incorporation-company");
        filingHistory.setDate(dayTimeNow);
        filingHistory.setType(hasFilingHistory ? spec.getFilingHistory().getType() :"NEWINC");
        filingHistory.setPages(10);
        filingHistory.setEntityId(entityId);
        filingHistory.setOriginalDescription(hasFilingHistory ? spec.getFilingHistory().getOriginalDescription() : "Certificate of incorporation general company details & statements of; officers, capital & shareholdings, guarantee, compliance memorandum of association");

        filingHistory.setBarcode(barcode);

        return filingHistoryRepository.save(filingHistory);
    }

    @Override
    public boolean delete(String companyId) {
        Optional<FilingHistory> filingHistory = filingHistoryRepository.findByCompanyNumber(companyId);

        filingHistory.ifPresent(filingHistoryRepository::delete);
        return filingHistory.isPresent();
    }

    private List<AssociatedFiling> createAssociatedFilings(Instant dayTimeNow, Instant dayNow){

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
        links.setSelf("/company/" + filingHistory.getCompanyNumber() + "/filing-history/" + filingHistory.getId());

        return links;
    }
}
