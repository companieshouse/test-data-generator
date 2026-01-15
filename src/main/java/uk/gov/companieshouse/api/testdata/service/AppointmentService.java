package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.entity.AppointmentsData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;

public interface AppointmentService {
    AppointmentsData createAppointment(CompanySpec spec);

    boolean deleteAllAppointments(String companyNumber);
}