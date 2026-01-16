package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.AppointmentsData;
import uk.gov.companieshouse.api.testdata.model.rest.AppointmentsResultData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;

import java.util.List;

public interface AppointmentService {
    AppointmentsResultData createAppointment(CompanySpec spec);

    boolean deleteAllAppointments(String companyNumber);
}