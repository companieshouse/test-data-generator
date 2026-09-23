package uk.gov.companieshouse.api.testdata.service.impl;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyExemptions;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyExemptionsTimestamp;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyExemptionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyExemptionsResponse;
import uk.gov.companieshouse.api.testdata.repository.CompanyExemptionsRepository;
import uk.gov.companieshouse.api.testdata.service.CompanyExemptionsService;

@Service
public class CompanyExemptionsServiceImpl implements CompanyExemptionsService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final List<String> EXEMPTION_TYPES = List.of(
            "psc_exempt_as_trading_on_regulated_market",
            "psc_exempt_as_shares_admitted_on_market",
            "psc_exempt_as_trading_on_uk_regulated_market",
            "psc_exempt_as_trading_on_eu_regulated_market",
            "disclosure_transparency_rules_chapter_five_applies"
    );

    @Autowired
    private CompanyExemptionsRepository repository;

    @Override
    public CompanyExemptionsResponse createOrUpdate(CompanyExemptionsRequest request) throws DataException {
        Instant now = Instant.now();
        String companyNumber = request.getCompanyNumber();

        CompanyExemptions exemptions = repository.findById(companyNumber)
                .orElseGet(CompanyExemptions::new);

        exemptions.setId(companyNumber);

        if (request.getData() != null && !request.getData().isEmpty()) {
            exemptions.setData(request.getData());
        } else {
            exemptions.setData(buildExemptionData(companyNumber, request.getExemptionType()));
        }
        exemptions.setDeltaAt(now.toString());

        if (exemptions.getCreated() == null || exemptions.getCreated().getAt() == null) {
            exemptions.setCreated(buildTimestamp(now));
        }
        exemptions.setUpdated(buildTimestamp(now));

        try {
            return mapToResponse(repository.save(exemptions));
        } catch (Exception ex) {
            throw new DataException("Failed to create or update company exemptions", ex);
        }
    }

    @Override
    public CompanyExemptionsResponse getByCompanyNumber(String companyNumber) throws NoDataFoundException {
        var exemptions = repository.findById(companyNumber)
                .orElseThrow(() -> new NoDataFoundException("no company exemptions"));
        return mapToResponse(exemptions);
    }

    @Override
    public boolean deleteByCompanyNumber(String companyNumber) {
        Optional<CompanyExemptions> exemptions = repository.findById(companyNumber);
        if (exemptions.isEmpty()) {
            return false;
        }
        repository.delete(exemptions.get());
        return true;
    }

    private Map<String, Object> buildExemptionData(String companyNumber, String exemptionType) {
        String selectedType = (exemptionType == null || exemptionType.isBlank())
                ? EXEMPTION_TYPES.get(SECURE_RANDOM.nextInt(EXEMPTION_TYPES.size()))
                : exemptionType;

        String exemptionTypeValue = selectedType.replace("_", "-");

        Map<String, Object> item = new HashMap<>();
        item.put("exempt_from", LocalDate.now().minusYears(1).toString());
        item.put("exempt_to", LocalDate.now().toString());

        Map<String, Object> exemptionEntry = new HashMap<>();
        exemptionEntry.put("exemption_type", exemptionTypeValue);
        exemptionEntry.put("items", List.of(item));

        Map<String, Object> exemptions = new HashMap<>();
        exemptions.put(selectedType, exemptionEntry);

        Map<String, Object> links = new HashMap<>();
        links.put("self", "/company/" + companyNumber + "/exemptions");

        Map<String, Object> data = new HashMap<>();
        data.put("etag", "etag-" + companyNumber);
        data.put("kind", "exemptions#exemptions");
        data.put("links", links);
        data.put("exemptions", exemptions);

        return data;
    }

    private CompanyExemptionsTimestamp buildTimestamp(Instant at) {
        CompanyExemptionsTimestamp timestamp = new CompanyExemptionsTimestamp();
        timestamp.setAt(at);
        return timestamp;
    }

    private CompanyExemptionsResponse mapToResponse(CompanyExemptions exemptions) {
        CompanyExemptionsResponse response = new CompanyExemptionsResponse();
        response.setCompanyNumber(exemptions.getId());
        response.setDeltaAt(exemptions.getDeltaAt());
        response.setData(exemptions.getData());
        response.setCreatedAt(exemptions.getCreated() == null ? null : exemptions.getCreated().getAt());
        response.setUpdatedAt(exemptions.getUpdated() == null ? null : exemptions.getUpdated().getAt());
        return response;
    }
}