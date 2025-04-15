package uk.gov.companieshouse.api.testdata.model.entity;

import java.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Field;

public class RegisterItem {

    @Field("register_moved_to")
    private String registerMovedTo;

    @Field("moved_on")
    private LocalDate movedOn;

    @Field("links.filing")
    private String filingLink;

    public String getRegisterMovedTo() {
        return registerMovedTo;
    }

    public void setRegisterMovedTo(String registerMovedTo) {
        this.registerMovedTo = registerMovedTo;
    }

    public LocalDate getMovedOn() {
        return movedOn;
    }

    public void setMovedOn(LocalDate movedOn) {
        this.movedOn = movedOn;
    }

    public String getFilingLink() {
        return filingLink;
    }

    public void setFilingLink(String filingLink) {
        this.filingLink = filingLink;
    }

}