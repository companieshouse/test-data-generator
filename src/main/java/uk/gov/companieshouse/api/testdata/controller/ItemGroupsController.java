package uk.gov.companieshouse.api.testdata.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.service.ItemGroupsService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemGroupsController {
    private final ItemGroupsService itemGroupsService;
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String STATUS = "status";

    public ItemGroupsController(ItemGroupsService itemGroupsService) {
        this.itemGroupsService = itemGroupsService;
    }

    @DeleteMapping("/item-groups/{orderNumber}")
    public ResponseEntity<Map<String, Object>> deleteItemGroups(@PathVariable("orderNumber")
                                                                String orderNumber) {
        Map<String, Object> response = new HashMap<>();
        response.put("orderNumber", orderNumber);
        boolean deleteItemGroups = itemGroupsService.deleteItemGroups(orderNumber);

        if (deleteItemGroups) {
            LOG.info("Item Groups is deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("Item Groups Not Found", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
