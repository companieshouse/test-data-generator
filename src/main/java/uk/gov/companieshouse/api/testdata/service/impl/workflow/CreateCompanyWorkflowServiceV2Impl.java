package uk.gov.companieshouse.api.testdata.service.impl.workflow;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequestV2;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.service.CreateCompanyWorkflowServiceV2;

/**
 * V2 implementation of CreateCompanyWorkflowService.
 * Handles PublicCompanyRequestV2 with nested structures (e.g., company_type).
 * Delegates to the shared orchestration in CreateCompanyWorkflowServiceImpl.
 */
@Service
public class CreateCompanyWorkflowServiceV2Impl implements CreateCompanyWorkflowServiceV2 {

    private final CreateCompanyWorkflowServiceImpl workflowService;

    public CreateCompanyWorkflowServiceV2Impl(CreateCompanyWorkflowServiceImpl workflowService) {
        this.workflowService = workflowService;
    }

    @Override
    public CompanyProfileResponse createPublicCompanyV2(PublicCompanyRequestV2 companySpec)
            throws DataException {
        // Convert v2 API DTO to internal request for shared orchestration.
        InternalCompanyRequest request = toInternalCompanyRequest(companySpec);
        return workflowService.createInternalCompany(request);
    }

    /**
     * Converts PublicCompanyRequestV2 (v2 API DTO with nested structures)
     * to InternalCompanyRequest (internal format used by orchestration services).
     */
    private InternalCompanyRequest toInternalCompanyRequest(PublicCompanyRequestV2 source) {
        InternalCompanyRequest target = new InternalCompanyRequest();
        target.setJurisdiction(source.getJurisdiction());
        target.setCompanyStatus(source.getCompanyStatus());
        target.setCompanyStatusDetail(source.getCompanyStatusDetail());
        target.setHasSuperSecurePscs(source.getHasSuperSecurePscs());
        target.setSecureOfficer(source.getSecureOfficer());
        target.setRegisters(source.getRegisters());
        target.setFilingHistoryList(source.getFilingHistoryList());
        target.setNumberOfAppointments(source.getNumberOfAppointments());
        target.setOfficerRoles(source.getOfficerRoles());
        target.setAccountsDueStatus(source.getAccountsDueStatus());
        target.setNumberOfPscs(source.getNumberOfPscs());
        target.setPscType(source.getPscType());
        target.setPscActive(source.getPscActive());
        target.setWithdrawnStatements(source.getWithdrawnStatements());
        target.setActiveStatements(source.getActiveStatements());
        target.setHasUkEstablishment(source.getHasUkEstablishment());
        target.setRegisteredOfficeIsInDispute(source.getRegisteredOfficeIsInDispute());
        target.setUndeliverableRegisteredOfficeAddress(source.getUndeliverableRegisteredOfficeAddress());
        if (source.getForeignCompanyLegalForm() != null && !source.getForeignCompanyLegalForm().isBlank()) {
            target.setForeignCompanyLegalForm(source.getForeignCompanyLegalForm());
        }

        // Extract company_type nested structure
        if (source.getCompanyTypeDetails() != null) {
            if (source.getCompanyTypeDetails().getType() != null) {
                target.setCompanyType(source.getCompanyTypeDetails().getType());
            }
            if (source.getCompanyTypeDetails().getSubType() != null) {
                target.setSubType(source.getCompanyTypeDetails().getSubType());
            }
        }

        return target;
    }
}

