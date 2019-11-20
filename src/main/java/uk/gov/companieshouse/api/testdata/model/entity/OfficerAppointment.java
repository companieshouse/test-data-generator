package uk.gov.companieshouse.api.testdata.model.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Document(collection = "officer_appointments")
public class OfficerAppointment {

    @Id
    @Field("id")
    private String id;
    @Field("created.at")
    private Instant createdAt;
    @Field("updated.at")
    private Instant updatedAt;
    @Field("data.total_results")
    private Integer totalResults;
    @Field("data.active_count")
    private Integer activeCount;
    @Field("data.inactive_count")
    private Integer inactiveCount;
    @Field("data.name")
    private String name;
    @Field("data.is_corporate_officer")
    private Boolean isCorporateOfficer;
    @Field("data.resigned_count")
    private Integer resignedCount;
    @Field("data.links")
    private Links links;
    @Field("data.etag")
    private String etag;
    @Field("data.date_of_birth.year")
    private Integer dateOfBirthYear;
    @Field("data.date_of_birth.month")
    private Integer dateOfBirthMonth;
    @Field("data.items")
    private List<OfficerAppointmentItem> officerAppointmentItems;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(Integer activeCount) {
        this.activeCount = activeCount;
    }

    public Integer getInactiveCount() {
        return inactiveCount;
    }

    public void setInactiveCount(Integer inactiveCount) {
        this.inactiveCount = inactiveCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCorporateOfficer() {
        return isCorporateOfficer;
    }

    public void setCorporateOfficer(Boolean corporateOfficer) {
        isCorporateOfficer = corporateOfficer;
    }

    public Integer getResignedCount() {
        return resignedCount;
    }

    public void setResignedCount(Integer resignedCount) {
        this.resignedCount = resignedCount;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Integer getDateOfBirthYear() {
        return dateOfBirthYear;
    }

    public void setDateOfBirthYear(Integer dateOfBirthYear) {
        this.dateOfBirthYear = dateOfBirthYear;
    }

    public Integer getDateOfBirthMonth() {
        return dateOfBirthMonth;
    }

    public void setDateOfBirthMonth(Integer dateOfBirthMonth) {
        this.dateOfBirthMonth = dateOfBirthMonth;
    }

    public List<OfficerAppointmentItem> getOfficerAppointmentItems() {
        return officerAppointmentItems;
    }

    public void setOfficerAppointmentItems(List<OfficerAppointmentItem> officerAppointmentItems) {
        this.officerAppointmentItems = officerAppointmentItems;
    }
}
