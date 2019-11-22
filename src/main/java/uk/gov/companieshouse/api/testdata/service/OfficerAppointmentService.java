package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointment;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;

public interface OfficerAppointmentService {
    OfficerAppointment create(CompanySpec spec, String officerId, String appointmentId) throws DataException;

    void delete(String companyNumber) throws NoDataFoundException, DataException;
}
