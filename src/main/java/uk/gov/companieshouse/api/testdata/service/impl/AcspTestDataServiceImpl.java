package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.AcspData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspSpec;
import uk.gov.companieshouse.api.testdata.service.AcspProfileService;
import uk.gov.companieshouse.api.testdata.service.AcspTestDataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class AcspTestDataServiceImpl implements AcspTestDataService {
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    private static final int ACSP_NUMBER_LENGTH = 8;

    @Autowired
    private AcspProfileService acspProfileService;
    @Autowired
    private RandomService randomService;

    @Value("${api.url}")
    private String apiUrl;

    void setAPIUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public AcspData createAcspData(final AcspSpec spec) throws DataException {
        if (spec == null) {
            throw new IllegalArgumentException("AcspSpec can not be null");
        }

        do {
            // acsp number format: 123456
            spec.setAcspNumber(randomService.getNumber(ACSP_NUMBER_LENGTH ));
        } while (acspProfileService.acspProfileExists(spec.getAcspNumber()));

        try {
            this.acspProfileService.create(spec);

            String acspUri = this.apiUrl + "/acsp/" + spec.getAcspNumber();
            return new AcspData(spec.getAcspNumber(), acspUri);
        } catch (Exception ex) {
            Map<String, Object> data = new HashMap<>();
            data.put("acsp number", spec.getAcspNumber());
            LOG.error("Rolling back creation of acsp", data);
            // Rollback all successful insertions
            deleteAcspData(spec.getAcspNumber());
            throw new DataException(ex);
        }
    }

    @Override
    public void deleteAcspData(long acspNumber) throws DataException {
        List<Exception> suppressedExceptions = new ArrayList<>();

        try {
            this.acspProfileService.delete(acspNumber);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }


        if (!suppressedExceptions.isEmpty()) {
            DataException ex = new DataException("Error deleting acsp data");
            suppressedExceptions.forEach(ex::addSuppressed);
            throw ex;
        }
    }

}
