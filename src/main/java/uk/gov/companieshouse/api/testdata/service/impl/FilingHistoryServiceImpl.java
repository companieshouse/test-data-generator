package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import uk.gov.companieshouse.api.testdata.exception.BarcodeServiceException;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.DescriptionValues;
import uk.gov.companieshouse.api.testdata.model.entity.OriginalValues;
import uk.gov.companieshouse.api.testdata.model.entity.AssociatedFiling;
import uk.gov.companieshouse.api.testdata.model.entity.Resolutions;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.FilingHistorySpec;
import uk.gov.companieshouse.api.testdata.model.rest.ResolutionsSpec;
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
    private static final Instant FIXED_MR01_DATE = LocalDate.of(2003, 2, 28).atStartOfDay(ZoneOffset.UTC).toInstant();
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
        List<FilingHistorySpec> filingHistorySpecs = spec.getFilingHistoryList(); // New array-style getter
        List<FilingHistory> savedHistories = new ArrayList<>();
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

        Instant dayTimeNow = Instant.now();
        Instant dayNow = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant();

        if (StringUtils.hasText(accountsDueStatus)) {
            LOG.debug("Generating accounts due date for status: " + accountsDueStatus);
            var dueDateNow = randomService.generateAccountsDueDateByStatus(accountsDueStatus);
            dayTimeNow = dueDateNow.atTime(LocalTime.now()).atZone(ZoneId.of("UTC")).toInstant();
            dayNow = dueDateNow.atStartOfDay(ZoneId.of("UTC")).toInstant();
        }

        if (filingHistorySpecs != null && !filingHistorySpecs.isEmpty()) {
            for (FilingHistorySpec fhSpec : filingHistorySpecs) {
                savedHistories.add(createFilingHistoryFromSpec(spec, fhSpec, dayNow, dayTimeNow));
            }
        } else {
            savedHistories.add(createFilingHistoryFromSpec(spec, null, dayNow, dayTimeNow));
        }

        return savedHistories.get(savedHistories.size() - 1);
    }

    private FilingHistory createFilingHistoryFromSpec(CompanySpec spec, FilingHistorySpec fhSpec, Instant dayNow, Instant dayTimeNow) throws DataException {
        String barcode = getBarcode();
        String entityId = ENTITY_ID_PREFIX + randomService.getNumber(ENTITY_ID_LENGTH);
        LOG.debug("Generated entity ID: " + entityId);

        String type = (fhSpec != null && fhSpec.getType() != null) ? fhSpec.getType() : TYPE;

        FilingHistory filingHistory = new FilingHistory();
        filingHistory.setId(randomService.addSaltAndEncode(entityId, SALT_LENGTH));
        filingHistory.setCompanyNumber(spec.getCompanyNumber());
        filingHistory.setLinks(createLinks(type, spec.getCompanyNumber(), entityId));
        filingHistory.setEntityId(entityId);
        filingHistory.setCategory(getOrDefault(fhSpec, FilingHistorySpec::getCategory, CATEGORY));
        filingHistory.setType(type);
        filingHistory.setSubCategory(getOrDefault(fhSpec, FilingHistorySpec::getSubCategory, null));
        filingHistory.setOriginalDescription(getOrDefault(fhSpec, FilingHistorySpec::getOriginalDescription, ORIGINAL_DESCRIPTION));
        filingHistory.setBarcode(barcode);
        filingHistory.setDescription(getOrDefault(fhSpec, FilingHistorySpec::getDescription, DESCRIPTION));

        applyTypeSpecificLogic(filingHistory, fhSpec, type, dayNow, dayTimeNow);

        if (!"MR01".equals(type)) {
            filingHistory.setActionDate(dayTimeNow);
            filingHistory.setPages(10);
            filingHistory.setDate(dayTimeNow);
        }

        LOG.info("FilingHistory object created for company number: " + spec.getCompanyNumber());
        FilingHistory savedFilingHistory = filingHistoryRepository.save(filingHistory);
        LOG.info("FilingHistory successfully saved with ID: " + savedFilingHistory.getId());

        return savedFilingHistory;
    }

    private String getBarcode() throws DataException {
        try {
            return barcodeService.getBarcode();
        } catch (BarcodeServiceException ex) {
            throw new DataException(ex.getMessage(), ex);
        }
    }

    private <T> String getOrDefault(FilingHistorySpec spec, Function<FilingHistorySpec, String> getter, String defaultValue) {
        return (spec != null && StringUtils.hasText(getter.apply(spec))) ? getter.apply(spec) : defaultValue;
    }

    private void applyTypeSpecificLogic(FilingHistory filingHistory, FilingHistorySpec fhSpec, String type, Instant dayNow, Instant dayTimeNow) {
        switch (type) {
            case "AP01" -> {
                filingHistory.setDescriptionValues(createDescriptionValues(type, dayNow));
                filingHistory.setOriginalValues(createOriginalValues(dayNow));
            }
            case "MR01" -> {
                filingHistory.setDescriptionValues(createDescriptionValues(type, dayNow));
                filingHistory.setPaperFiled(true);
                filingHistory.setDate(FIXED_MR01_DATE);
            }
            case "RESOLUTIONS" -> {
                filingHistory.setResolutions(fhSpec != null ? createResolutions(fhSpec, dayTimeNow) : null);
            }
            default -> {
                filingHistory.setAssociatedFilings(createAssociatedFilings(dayTimeNow, dayNow));
            }
        }
    }

    @Override
    public boolean delete(String companyId) {
        LOG.info("Attempting to delete FilingHistory for company number: " + companyId);

        Optional<List<FilingHistory>> filingHistoriesOpt = filingHistoryRepository.findAllByCompanyNumber(companyId);
        if (filingHistoriesOpt.isPresent() && !filingHistoriesOpt.get().isEmpty()) {
            LOG.info("FilingHistory found for company number: "
                    + companyId + ". Proceeding with deletion.");
            filingHistoriesOpt.get().forEach(filingHistoryRepository::delete);
            LOG.info("Successfully deleted FilingHistory for company number: " + companyId);
            return true;
        }
        LOG.info("No FilingHistory found for company number: " + companyId);
        return false;
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
        ArrayList<HashMap<String, String>> capitalValues = new ArrayList<>();
        HashMap<String, String> capitalEntry = new HashMap<>();
        capitalEntry.put("currency", "GBP");
        capitalEntry.put("figure", "1");
        capitalValues.add(capitalEntry);

        descriptionValues.put("capital", capitalValues);
        descriptionValues.put("date", dayNow);

        capital.setDescriptionValues(descriptionValues);
        capital.setOriginalDescription("11/09/19 Statement of Capital;GBP 1");
        capital.setType("SH01");

        associatedFilings.add(capital);
        return associatedFilings;
    }

    private Links createLinks(String fhType, String companyNumber, String id) {
        Links links = new Links();
        links.setSelf("/company/" + companyNumber + "/filing-history/" + id);
        if ("AP01".equals(fhType) || TYPE.equals(fhType)) {
            links.setDocumentMetadata("document/" + id);
        }
        return links;
    }

    private DescriptionValues createDescriptionValues(String type , Instant dayNow) {
        var descriptionValues = new DescriptionValues();
        if("AP01".equals(type)) {
            descriptionValues.setAppointmentDate(dayNow);
            descriptionValues.setOfficerName("Mr John Test");
        } else {
            descriptionValues.setChargeNumber(String.valueOf(randomService.getNumber(12)));
        }
        return descriptionValues;
    }

    private OriginalValues createOriginalValues( Instant dayNow) {
        var originalValues = new OriginalValues();
        originalValues.setAppointmentDate(dayNow);
        originalValues.setOfficerName("John Test");
        return originalValues;
    }

    public static String convertInstantToDeltaAt(Instant instant) {
        var dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);

        var formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String base = dateTime.format(formatter);

        var microseconds = String.format("%09d", dateTime.getNano()).substring(0, 6);

        return base + microseconds;
    }

    private List<Resolutions> createResolutions(FilingHistorySpec fhSpec, Instant dayTimeNow) {
        List<Resolutions> resolutionsList = new ArrayList<>();

        if (fhSpec.getResolutions() != null) {
            for (ResolutionsSpec resSpec : fhSpec.getResolutions()) {
                var resolution = new Resolutions();
                resolution.setBarcode(resSpec.getBarcode());
                resolution.setCategory(resSpec.getCategory());
                resolution.setDescription(resSpec.getDescription());
                resolution.setSubCategory(resSpec.getSubCategory());
                resolution.setType(resSpec.getType());
                resolution.setDeltaAt(convertInstantToDeltaAt(dayTimeNow));

                resolutionsList.add(resolution);
            }
        }

        return resolutionsList;
    }
}
