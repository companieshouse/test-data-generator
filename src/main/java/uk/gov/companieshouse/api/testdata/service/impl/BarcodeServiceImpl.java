package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.companieshouse.api.testdata.exception.BarcodeServiceException;
import uk.gov.companieshouse.api.testdata.service.BarcodeService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class BarcodeServiceImpl implements BarcodeService {

    private RestTemplate template;
    private static final Logger LOG = LoggerFactory.getLogger(String.valueOf(BarcodeServiceImpl.class));


    @Value("${barcode.url}")
    private String barcodeUrl;

    void setTemplate(RestTemplate template) {
        this.template = template;
    }

    void setBarcodeUrl(String barcodeUrl) {
        this.barcodeUrl = barcodeUrl;
    }

    @Override
    public String getBarcode() throws BarcodeServiceException {

        if (this.template == null) {
            this.template = new RestTemplate();
        }

        try {
            LOG.info("Starting barcode generation process. Target URI: " + barcodeUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("text", "json"));

            Instant instant = Instant.now();
            LocalDateTime datetime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
            String barcodeServiceDate = datetime.format(DateTimeFormatter.BASIC_ISO_DATE);

            String jsonRequestString = "{\"datereceived\":" + barcodeServiceDate + "}";
            LOG.debug("Request payload: " + jsonRequestString);

            HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequestString, headers);
            ResponseEntity<String> response = template.exchange(barcodeUrl, HttpMethod.POST, requestEntity, String.class);

            LOG.debug("Response received: " + response.getBody());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            String barcode = rootNode.path("barcode").asText();

            LOG.info("Barcode successfully generated: " + barcode);
            return barcode;
        } catch (Exception ex) {
            LOG.error("Error occurred while generating barcode. URI: " + barcodeUrl, ex);
            throw new BarcodeServiceException("Error creating barcode", ex);
        }
    }
}
