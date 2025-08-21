package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;

public interface AppointmentService {
    void createAppointmentsWithMatchingIds(CompanySpec spec);

    boolean deleteAppointments(String companyNumber);

    boolean deleteAppointmentsData(String companyNumber);
}