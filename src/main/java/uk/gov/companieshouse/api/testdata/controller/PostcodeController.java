package uk.gov.companieshouse.api.testdata.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.response.PostcodesResponse;
import uk.gov.companieshouse.api.testdata.service.PostcodeService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostcodeController {
    private final PostcodeService postcodeService;
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    public PostcodeController(PostcodeService postcodeService) {
        this.postcodeService = postcodeService;
    }

    @GetMapping("/postcodes")
    public ResponseEntity<PostcodesResponse> getPostcode(
            @RequestParam(value = "country") String country) throws DataException {
        LOG.info("Retrieving postcode for country: " + country);
        var postcode = postcodeService.getPostcodes(country);
        if (postcode == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        LOG.info("Retrieved postcode for country: " + country + " " + postcode.getPostcode());
        return new ResponseEntity<>(postcode, HttpStatus.OK);
    }
}
