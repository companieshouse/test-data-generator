package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.AppointmentsResultData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;

public interface AppointmentService {
    AppointmentsResultData createAppointment(CompanySpec spec);

    boolean deleteAllAppointments(String companyNumber);
}