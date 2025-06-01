package uk.gov.companieshouse.api.testdata.model.entity;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
            totalCount = pscCount + statementsCount;
        }
    }

    private PscMetrics psc = new PscMetrics();

    @Id
    private String id;
    private String etag;
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

    public void setPscCount(int pscCount) {
        this.psc.pscCount = pscCount;
        this.psc.updateCounts();
    }

    public int getPscCount() {
        return this.psc.pscCount;
    }

    public void setActivePscCount(int activePscCount) {
        this.psc.activePscCount = activePscCount;
        this.psc.updateCounts();
    }

    public int getActivePscCount() {
        return this.psc.activePscCount;
    }

    public int getActiveStatementsCount() {
        return this.psc.activeStatementsCount;
    }

    public void setActiveStatementsCount(int activeStatementsCount) {
        this.psc.activeStatementsCount = activeStatementsCount;
        this.psc.updateCounts();
    }

    public int getCeasedPscCount() {
        return this.psc.ceasedPscCount;
    }

    public void setCeasedPscCount(int ceasedPscCount) {
        this.psc.ceasedPscCount = ceasedPscCount;
        this.psc.updateCounts();
    }

    public int getWithdrawnStatementsCount() {
        return this.psc.withdrawnStatementsCount;
    }

    public void setWithdrawnStatementsCount(int withdrawnStatementsCount) {
        this.psc.withdrawnStatementsCount = withdrawnStatementsCount;
        this.psc.updateCounts();
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
            activeCount = activeLlpMembersCount + activeDirectorsCount + activeSecretariesCount;
            totalCount = activeCount + resignedCount;
        }
    }

    private OfficerMetrics officer = new OfficerMetrics();

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