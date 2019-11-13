package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "appointments")
public class Appointment {
    @Id
    @Field("id")
    private String id;

    @Field("created")
    private DateObject created;

    @Field("internal_id")
    private String internalId;

    @Field("appointment_id")
    private String appointmentId;

    @Field("data")
    private AppointmentData data;

    @Field("company_name")
    private String companyName;

    @Field("company_status")
    private String companyStatus;

    @Field("officer_id")
    private String officerId;

    @Field("company_number")
    private String companyNumber;

    @Field("updated")
    private DateObject updated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DateObject getCreated() {
        return created;
    }

    public void setCreated(DateObject created) {
        this.created = created;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public AppointmentData getData() {
        return data;
    }

    public void setData(AppointmentData data) {
        this.data = data;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyStatus() {
        return companyStatus;
    }

    public void setCompanyStatus(String companyStatus) {
        this.companyStatus = companyStatus;
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

    public DateObject getUpdated() {
        return updated;
    }

    public void setUpdated(DateObject updated) {
        this.updated = updated;
    }
}
