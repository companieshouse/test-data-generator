package uk.gov.companieshouse.api.testdata.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.testdata.exception.BarcodeServiceException;
import uk.gov.companieshouse.api.testdata.service.BarcodeService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static uk.gov.companieshouse.api.testdata.constants.ErrorMessageConstants.BARCODE_ERROR;

@Service
public class BarcodeServiceImpl implements BarcodeService {

    private RestTemplate template;

    @Value("${barcode.url}")
    private String barcodeUrl;

    public void setTemplate(RestTemplate template) {
        this.template = template;
    }

    public void setBarcodeUrl(String barcodeUrl) {
        this.barcodeUrl = barcodeUrl;
    }

    @Override
    public String getBarcode() throws BarcodeServiceException{

        if(this.template == null){
            this.template = new RestTemplate();
        }

        ResponseEntity<String> response;
        String generatedBarcode;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("text", "json"));

            Instant instant = Instant.now();
            LocalDateTime datetime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
            String barcodeServiceDate = datetime.format(DateTimeFormatter.BASIC_ISO_DATE);

            String jsonRequestString = "{\"datereceived\":" + barcodeServiceDate + "}";
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequestString, headers);
            response = template.exchange(barcodeUrl, HttpMethod.POST, requestEntity, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            generatedBarcode = rootNode.path("barcode").asText();
        } catch(Exception ex) {
            throw new BarcodeServiceException(BARCODE_ERROR);
        }

        return generatedBarcode;
    }
}
