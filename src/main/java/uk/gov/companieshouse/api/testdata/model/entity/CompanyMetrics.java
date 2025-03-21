package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Document(collection = "company_metrics")
public class CompanyMetrics {

    private class PscMetrics {
        @Field("total_count")
        int totalCount;

        @Field("psc_count")
        int pscCount;

        @Field("active_pscs_count")
        int activePscCount;

        @Field("ceased_pscs_count")
        int ceasedPscCount;

        @Field("statements_count")
        int statementsCount;

        @Field("active_statements_count")
        int activeStatementsCount;

        @Field("withdrawn_statements_count")
        int withdrawnStatementsCount;

        void updateCounts() {
            pscCount = activePscCount + ceasedPscCount;
            statementsCount = activeStatementsCount + withdrawnStatementsCount;
            totalCount = pscCount + statementsCount;
        }
    }

    private class OfficerMetrics {
        @Field("total_count")
        int totalCount;

        @Field("active_count")
        int activeCount;

        @Field("resigned_count")
        int resignedCount;

        @Field("active_llp_members_count")
        int activeLlpMembersCount;

        @Field("active_directors_count")
        int activeDirectorsCount;

        @Field("active_secretaries_count")
        int activeSecretariesCount;

        void updateCounts() {
            activeCount = activeDirectorsCount + activeSecretariesCount + activeLlpMembersCount;
            totalCount = activeCount + resignedCount;
        }
    }

    @Id
    @Field("id")
    private String id;

    @Field("data.counts.persons-with-significant-control")
    private PscMetrics psc = new PscMetrics();

    @Field("data.counts.appointments")
    private OfficerMetrics officer = new OfficerMetrics();

    @Field("data.etag")
    private String etag;

    @Field("data.registers")
    private Map<String, RegisterItem> registers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public int getActivePscCount() {
        return this.psc.activePscCount;
    }

    public void setActivePscCount(int count) {
        this.psc.activePscCount = count;
        this.psc.updateCounts();
    }

    public int getCeasedPscCount() {
        return this.psc.ceasedPscCount;
    }

    public void setCeasedPscCount(int count) {
        this.psc.ceasedPscCount = count;
        this.psc.updateCounts();
    }
    
    public int getActivePscStatementsCount() {
        return this.psc.activeStatementsCount;
    }

    public void setActivePscStatementsCount(int count) {
        this.psc.activeStatementsCount = count;
        this.psc.updateCounts();
    }

    public int getWithdrawnStatementsCount() {
        return this.psc.withdrawnStatementsCount;
    }

    public void setWithdrawnPscStatementsCount(int count) {
        this.psc.withdrawnStatementsCount = count;
        this.psc.updateCounts();
    }

    public int getPscCount() {
        return this.psc.pscCount;
    }

    public int getPscTotalCount() {
        return this.psc.totalCount;
    }

    public int getPscStatementsCount() {
        return this.psc.statementsCount;
    }

    public int getActiveDirectorsCount() {
        return this.officer.activeDirectorsCount;
    }

    public void setActiveDirectorsCount(int count) {
        this.officer.activeDirectorsCount = count;
        this.officer.updateCounts();
    }

    public int getActiveSecretariesCount() {
        return this.officer.activeSecretariesCount;
    }

    public void setActiveSecretariesCount(int count) {
        this.officer.activeSecretariesCount = count;
        this.officer.updateCounts();
    }

    public int getActiveLlpMembersCount() {
        return this.officer.activeLlpMembersCount;
    }

    public void setActiveLlpMembersCount(int count) {
        this.officer.activeLlpMembersCount = count;
        this.officer.updateCounts();
    }

    public int getResignedOfficerCount() {
        return this.officer.resignedCount;
    }

    public void setResignedCount(int count) {
        this.officer.resignedCount = count;
        this.officer.updateCounts();
    }

    public int getOfficersTotalCount() {
        return this.officer.totalCount;
    }

    public int getActiveOfficersCount() {
        return this.officer.activeCount;
    }

    public Map<String, RegisterItem> getRegisters() {
        return registers;
    }

    public void setRegisters(Map<String, RegisterItem> registers) {
        this.registers = registers;
    }
}
