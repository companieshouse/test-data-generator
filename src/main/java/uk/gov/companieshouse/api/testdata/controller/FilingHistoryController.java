package uk.gov.companieshouse.api.testdata.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.service.FilingHistoryService;

@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class FilingHistoryController {
    private final FilingHistoryService filingHistoryService;

    private static final String STATUS = "status";
    private static final String ERROR = "error";

    public FilingHistoryController(FilingHistoryService filingHistoryService) {
        this.filingHistoryService = filingHistoryService;
    }

    @GetMapping("/company-filing-history")
    public ResponseEntity<Object> getCompanyFilingHistory(
            @RequestParam(value = "companyNumber", required = false) String companyNumber,
            @RequestParam(value = "id", required = false) String companyFilingHistoryId) {

        if ((companyNumber == null || companyNumber.isEmpty()) &&
                (companyFilingHistoryId == null || companyFilingHistoryId.isEmpty())) {

            Map<String, Object> error = new HashMap<>();
            error.put(ERROR, "Either companyNumber or id must be provided");
            error.put(STATUS, HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(error);
        }

        if (companyNumber != null && !companyNumber.isEmpty()) {

            List<FilingHistory> results =
                    filingHistoryService.getCompanyFilingHistoryByCompanyNumber(companyNumber);

            if (results.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(results);

        } else {

            Optional<FilingHistory> result =
                    filingHistoryService.getCompanyFilingHistoryById(companyFilingHistoryId);

            if (result.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(result.get());
        }

    }

    @DeleteMapping("/company-filing-history/{companyNumber}")
    public ResponseEntity<Object> deleteCompanyFilingHistory(
            @PathVariable String companyNumber) throws NoDataFoundException {

        boolean deleted = filingHistoryService.deleteCompanyFilingHistory(companyNumber);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("companyNumber", companyNumber);
            response.put(STATUS, HttpStatus.NOT_FOUND.value());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}
