package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Document(collection = "acsp_profile")
public class AcspProfile {

    @Id
    @Field("_id")
    private String id;

    @Field("version")
    private Long version;

    @Field("data")
    private Data data = new Data();

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    public Long getVersion() { return version; }

    public void setVersion(Long version) { this.version = version; }

    public Data getData() { return data; }

    public void setData(Data data) { this.data = data; }


    /**
     * Setter methods delegating to Data fields
     */
    public void setAcspNumber(String acspNumber) {
        this.data.setAcspNumber(acspNumber);
    }

    public void setCompanyName(String companyName) {
        this.data.setName(companyName);
    }

    public void setType(String type) {
        this.data.setType(type);
    }

    public void setStatus(String status) {
        this.data.setStatus(status);
    }

    public void setLinks(Links links) {
        this.data.setLinks(links);
    }


    public static class Data {

        @Field("acsp_number")
        private String acspNumber;

        @Field("name")
        private String name;

        @Field("status")
        private String status;

        @Field("type")
        private String type;

        @Field("links")
        private Links links;

        public String getAcspNumber() {
            return acspNumber;
        }

        public void setAcspNumber(String acspNumber) {
            this.acspNumber = String.valueOf(acspNumber);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Links getLinks() {
            return links;
        }

        public void setLinks(Links links) {
            this.links = links;
        }

    }

    public static class Links {
        @Field("self")
        private String self;

        public String getSelf() {
            return self;
        }

        public void setSelf(String self) {
            this.self = self;
        }
    }


    public static class Audit {
        @Field("type")
        private String type;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}
