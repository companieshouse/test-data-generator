package uk.gov.companieshouse.api.testdata.model.dto;

import uk.gov.companieshouse.api.testdata.service.RandomService;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

public class AccountParameters {

    private String accountsDueStatus;
    private LocalDate accountingReferenceDate;

    public AccountParameters(String accountsDueStatus, RandomService randomService) {
        this.accountsDueStatus = accountsDueStatus;
        this.accountingReferenceDate = LocalDate.now();
        if (StringUtils.hasText(accountsDueStatus)) {
            this.accountingReferenceDate =
                    randomService.generateAccountsDueDateByStatus(accountsDueStatus);
        }
    }

    public String getAccountsDueStatus() {
        return accountsDueStatus;
    }

    public LocalDate getAccountingReferenceDate() {
        return accountingReferenceDate;
    }
}