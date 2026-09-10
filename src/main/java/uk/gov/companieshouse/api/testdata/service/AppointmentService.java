package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.request.AppointmentCreationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AppointmentsResultResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.entity.Address;

public interface AppointmentService {
    AppointmentsResultResponse createAppointment(InternalCompanyRequest internalCompanyRequest, Address registeredOfficeAddress);
    AppointmentsResultResponse createAppointment(AppointmentCreationRequest request);

    boolean deleteAllAppointments(String companyNumber);
}