package uk.gov.companieshouse.api.testdata.model.rest;

import java.util.List;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.AppointmentsData;


public class AppointmentsResultData {
    private List<AppointmentsData> appointmentsData;
    private List<Appointment> appointment;

    public List<AppointmentsData> getAppointmentsDataList() {
        return appointmentsData;
    }

    public void setAppointmentsDataList(List<AppointmentsData> appointmentsDataList) {
        this.appointmentsData = appointmentsDataList;
    }

    public List<Appointment> getAppointmentList() {
        return appointment;
    }

    public void setAppointmentList(List<Appointment> appointmentList) {
        this.appointment = appointmentList;
    }
}


