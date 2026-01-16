package uk.gov.companieshouse.api.testdata.model.rest;

import java.util.List;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.AppointmentsData;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointment;


public class AppointmentsResultData {
    private List<AppointmentsData> appointmentsData;
    private List<Appointment> appointment;
    private List<OfficerAppointment> officerAppointment;

    public List<AppointmentsData> getAppointmentsData() {
        return appointmentsData;
    }

    public void setAppointmentsData(List<AppointmentsData> appointmentsDataList) {
        this.appointmentsData = appointmentsDataList;
    }

    public List<Appointment> getAppointment() {
        return appointment;
    }

    public void setAppointment(List<Appointment> appointmentList) {
        this.appointment = appointmentList;
    }

    public List<OfficerAppointment> getOfficerAppointment() {
        return officerAppointment;
    }

    public void setOfficerAppointment(List<OfficerAppointment> officerAppointment) {
        this.officerAppointment = officerAppointment;
    }

}


