package uk.gov.companieshouse.api.testdata.model.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class DateParameters {
    private final Instant dateOneYearAgo;
    private final Instant dateNow;
    private final Instant dateInOneYear;
    private final Instant dateInOneYearTwoWeeks;
    private final Instant dateInOneYearNineMonths;
    private final Instant dateInTwoYear;
    private final Instant dateInTwoYearTwoWeeks;
    private final LocalDate accountingReferenceDate;

    public DateParameters(LocalDate accountingReferenceDate) {
        this.accountingReferenceDate = accountingReferenceDate;
        this.dateOneYearAgo = accountingReferenceDate.minusYears(1L)
                .atStartOfDay(ZoneId.of("UTC")).toInstant();
        this.dateNow = accountingReferenceDate.atStartOfDay(ZoneId.of("UTC")).toInstant();
        this.dateInOneYear = accountingReferenceDate.plusYears(1L)
                .atStartOfDay(ZoneId.of("UTC")).toInstant();
        this.dateInOneYearTwoWeeks = accountingReferenceDate.plusYears(1L)
                .plusDays(14L).atStartOfDay(ZoneId.of("UTC")).toInstant();
        this.dateInOneYearNineMonths = accountingReferenceDate.plusYears(1L)
                .plusMonths(9L).atStartOfDay(ZoneId.of("UTC")).toInstant();
        this.dateInTwoYear = accountingReferenceDate.plusYears(2L)
                .atStartOfDay(ZoneId.of("UTC")).toInstant();
        this.dateInTwoYearTwoWeeks = accountingReferenceDate.plusYears(2L)
                .plusDays(14L).atStartOfDay(ZoneId.of("UTC")).toInstant();
    }

    public Instant getDateOneYearAgo() {
        return dateOneYearAgo;
    }

    public Instant getDateNow() {
        return dateNow;
    }

    public Instant getDateInOneYear() {
        return dateInOneYear;
    }

    public Instant getDateInOneYearTwoWeeks() {
        return dateInOneYearTwoWeeks;
    }

    public Instant getDateInOneYearNineMonths() {
        return dateInOneYearNineMonths;
    }

    public Instant getDateInTwoYear() {
        return dateInTwoYear;
    }

    public Instant getDateInTwoYearTwoWeeks() {
        return dateInTwoYearTwoWeeks;
    }

    public LocalDate getAccountingReferenceDate() {
        return accountingReferenceDate;
    }
}
