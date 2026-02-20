package uk.gov.companieshouse.api.testdata.model.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CertificatesResponse {

    @JsonProperty("certificates")
    private final List<CertificateEntry> certificates;

    public CertificatesResponse(List<CertificateEntry> certificates) {
        this.certificates = certificates;
    }

    public List<CertificateEntry> getCertificates() {
        return certificates;
    }

    public static class CertificateEntry {
        @JsonProperty("id")
        private final String id;

        @JsonProperty("created_at")
        private final String createdAt;

        @JsonProperty("updated_at")
        private final String updatedAt;

        public CertificateEntry(String id, String createdAt, String updatedAt) {
            this.id = id;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public String getId() {
            return id;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}
