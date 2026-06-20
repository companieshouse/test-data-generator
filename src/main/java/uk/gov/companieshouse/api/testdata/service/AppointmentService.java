package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.request.AppointmentCreationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AppointmentsResultResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest;

public interface AppointmentService {
    AppointmentsResultResponse createAppointment(CompanyRequest spec);

    AppointmentsResultResponse createAppointmentFromRequest(AppointmentCreationRequest req);

    boolean deleteAllAppointments(String companyNumber);
}