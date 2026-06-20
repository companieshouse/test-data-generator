package uk.gov.companieshouse.api.testdata.model.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.AppointmentsData;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointment;

import java.time.Instant;
import java.util.List;


public class AppointmentsResultResponse {

    @JsonProperty("appointment_id")
    private String appointmentId;
    @JsonProperty("officer_id")
    private String officerId;
    @JsonProperty ("company_number")
    private String companyNumber;
    @JsonProperty("officer_roles")
    private String officerRoles;
    @JsonProperty("is_pre_1992_appointment")
    private Boolean isPre1992Appointment;
    @JsonProperty("is_resigned")
    private Boolean isResigned;
    @JsonProperty("identification_type")
    private String identificationType;

    @JsonProperty("resigned_on")
    private Instant resignedOn;

    private List<AppointmentsData> appointmentsData;
    private List<Appointment> appointment;
    private List<OfficerAppointment> officerAppointment;

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getOfficerId() {
        return officerId;
    }

    public void setOfficerId(String officerId) {
        this.officerId = officerId;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getOfficerRoles() {
        return officerRoles;
    }

    public void setOfficerRoles(String officerRoles) {
        this.officerRoles = officerRoles;
    }

    public Boolean getPre1992Appointment() {
        return isPre1992Appointment;
    }

    public void setPre1992Appointment(Boolean pre1992Appointment) {
        isPre1992Appointment = pre1992Appointment;
    }

    public Boolean getResigned() {
        return isResigned;
    }

    public void setResigned(Boolean resigned) {
        isResigned = resigned;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

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

    public Instant getResignedOn() {
        return resignedOn;
    }

    public void setResignedOn(Instant resignedOn) {
        this.resignedOn = resignedOn;
    }

}

