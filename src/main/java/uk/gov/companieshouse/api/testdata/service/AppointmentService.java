package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.response.AppointmentsResultResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequest;

public interface AppointmentService {
    AppointmentsResultResponse createAppointment(InternalCompanyRequest internalCompanyRequest);

    boolean deleteAllAppointments(String companyNumber);
}