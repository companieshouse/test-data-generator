package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;

public interface AppointmentService {
    void createAppointment(CompanySpec spec);

    boolean deleteAllAppointments(String companyNumber);
}