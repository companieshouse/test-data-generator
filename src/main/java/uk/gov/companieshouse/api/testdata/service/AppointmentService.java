package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;

import java.util.List;

public interface AppointmentService {

    /**
     * Creates one or more Appointment entities for the given CompanySpec,
     * and returns them all in a list.
     */
    List<Appointment> create(CompanySpec spec);

    /**
     * Deletes an appointment by its company number.
     *
     * @return true if found and deleted, false otherwise
     */
    boolean delete(String companyNumber);
}
