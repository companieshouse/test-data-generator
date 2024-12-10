package uk.gov.companieshouse.api.testdata.model.authentication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Permissions {

    private static final Map<RoleTypes, List<String>> permissions = new HashMap<>();

    static {
        permissions.put(RoleTypes.BADOS_USER, Arrays.asList(
                "USER_PERMISSIONS_ADMIN_VIEW_JSON",
                "USER_PERMISSIONS_BANKRUPT_SCOTTISH_OFFICER_SEARCH"
        ));

        permissions.put(RoleTypes.SUPERVISOR, Arrays.asList(
                "USER_PERMISSIONS_TRANSACTION_REPROCESS",
                "USER_PERMISSIONS_MONITOR",
                "USER_PERMISSIONS_EXTENSIONS_DOWNLOAD",
                "USER_PERMISSIONS_APPEALS_VIEW",
                "USER_PERMISSIONS_FILING_RESUBMIT",
                "USER_PERMISSIONS_BANKRUPT_SCOTTISH_OFFICER_SEARCH",
                "USER_PERMISSIONS_APPEALS_DOWNLOAD",
                "USER_PERMISSIONS_ROLE_ADMIN",
                "USER_PERMISSIONS_CHS_ORDER_INVESTIGATION",
                "USER_PERMISSIONS_TRANSACTION_LOOKUP",
                "USER_PERMISSIONS_RESTRICTED_WORD",
                "USER_PERMISSIONS_PENALTY_LOOKUP",
                "USER_PERMISSIONS_PAYMENT_BULK_REFUNDS",
                "USER_PERMISSIONS_EXTENSIONS_VIEW",
                "USER_PERMISSIONS_USER_ROLES",
                "USER_PERMISSIONS_STRIKE_OFF_OBJECTIONS_DOWNLOAD",
                "USER_PERMISSIONS_ADMIN_COMPANY",
                "USER_PERMISSIONS_USER_FILINGS",
                "USER_PERMISSIONS_ADMIN_IMAGES",
                "USER_PERMISSIONS_PAYMENT_LOOKUP",
                "USER_PERMISSIONS_ADMIN_SEARCH",
                "USER_PERMISSIONS_FILING_RESEND",
                "USER_PERMISSIONS_ADMIN_QUEUES",
                "USER_PERMISSIONS_ADMIN_VIEW_JSON",
                "USER_PERMISSIONS_ADMIN_USER_SEARCH"
        ));

        permissions.put(RoleTypes.ADMIN_ROLE, Arrays.asList(
                "USER_PERMISSIONS_FILING_RESUBMIT",
                "USER_PERMISSIONS_ADMIN_USER_SEARCH",
                "USER_PERMISSIONS_MONITOR",
                "USER_PERMISSIONS_ROLE_ADMIN",
                "USER_PERMISSIONS_ADMIN_QUEUES",
                "USER_PERMISSIONS_ADMIN_IMAGES",
                "USER_PERMISSIONS_FILING_RESEND",
                "USER_PERMISSIONS_TRANSACTION_LOOKUP",
                "USER_PERMISSIONS_ADMIN_COMPANY",
                "USER_PERMISSIONS_ADMIN_SEARCH",
                "USER_PERMISSIONS_USER_FILINGS",
                "USER_PERMISSIONS_ADMIN_USER_SEARCH"
        ));

        permissions.put(RoleTypes.APPEALS_TEAM, Arrays.asList(
                "USER_PERMISSIONS_APPEALS_VIEW",
                "USER_PERMISSIONS_APPEALS_DOWNLOAD",
                "USER_PERMISSIONS_CHS_ORDER_INVESTIGATION"
        ));

        permissions.put(RoleTypes.BULK_REFUNDS, Arrays.asList(
                "USER_PERMISSIONS_PAYMENT_BULK_REFUNDS"
        ));

        permissions.put(RoleTypes.CHS_ORDER_INVESTIGATOR, Arrays.asList(
                "USER_PERMISSIONS_CHS_ORDER_INVESTIGATION"
        ));

        permissions.put(RoleTypes.COMPANY_REFRESH, Arrays.asList(
                "USER_PERMISSIONS_ADMIN_COMPANY"
        ));

        permissions.put(RoleTypes.CSI_SUPPORT, Arrays.asList(
                "USER_PERMISSIONS_ADMIN_IMAGES"
        ));

        permissions.put(RoleTypes.EXTENSIONS_DOWNLOAD, Arrays.asList(
                "USER_PERMISSIONS_EXTENSIONS_DOWNLOAD"
        ));

        permissions.put(RoleTypes.EXTENSIONS_VIEW, Arrays.asList(
                "USER_PERMISSIONS_EXTENSIONS_VIEW"
        ));

        permissions.put(RoleTypes.FES_ADMIN_USER, Arrays.asList(
                "USER_PERMISSIONS_TRANSACTION_SEARCH"
        ));

        permissions.put(RoleTypes.FRONT_END_SUPPORT, Arrays.asList(
                "USER_PERMISSIONS_USER_FILINGS",
                "USER_PERMISSIONS_ADMIN_USER_SEARCH",
                "USER_PERMISSIONS_ADMIN_SEARCH",
                "USER_PERMISSIONS_ADMIN_QUEUES",
                "USER_PERMISSIONS_ADMIN_COMPANY",
                "USER_PERMISSIONS_ADMIN_IMAGES",
                "USER_PERMISSIONS_FILING_RESEND",
                "USER_PERMISSIONS_ROLE_ADMIN",
                "USER_PERMISSIONS_TRANSACTION_REPROCESS",
                "USER_PERMISSIONS_FILING_RESUBMIT",
                "USER_PERMISSIONS_USER_ROLES",
                "USER_PERMISSIONS_TRANSACTION_LOOKUP"
        ));

        permissions.put(RoleTypes.INTERNAL_CERTS_PURCHASER, Arrays.asList(
                "USER_PERMISSIONS_FREE_CERTS"
        ));

        permissions.put(RoleTypes.INTERNAL_CERT_DOCS_PURCHASER, Arrays.asList(
                "USER_PERMISSIONS_FREE_CERT_DOCS"
        ));

        permissions.put(RoleTypes.INTERNAL_MIDS_PURCHASER, Arrays.asList(
                "USER_PERMISSIONS_FREE_MIDS"
        ));

        permissions.put(RoleTypes.MANAGE_ROLES, Arrays.asList(
                "USER_PERMISSIONS_ADMIN_SEARCH",
                "USER_PERMISSIONS_ADMIN_USER_SEARCH",
                "USER_PERMISSIONS_ROLE_ADMIN",
                "USER_PERMISSIONS_USER_ROLES"
        ));

        permissions.put(RoleTypes.RESTRICTED_WORD, Arrays.asList(
                "USER_PERMISSIONS_ADMIN_SEARCH",
                "USER_PERMISSIONS_ADMIN_USER_SEARCH",
                "USER_PERMISSIONS_ROLE_ADMIN",
                "USER_PERMISSIONS_USER_ROLES",
                "USER_PERMISSIONS_ADMIN_QUEUES",
                "USER_PERMISSIONS_RESTRICTED_WORD",
                "USER_PERMISSIONS_EXTENSIONS_DOWNLOAD",
                "USER_PERMISSIONS_STRIKE_OFF_OBJECTIONS_DOWNLOAD",
                "USER_PERMISSIONS_ADMIN_IMAGES",
                "USER_PERMISSIONS_USER_FILINGS"
        ));

        permissions.put(RoleTypes.SALUS, Arrays.asList(
                "USER_PERMISSIONS_ADMIN_COMPANY",
                "USER_PERMISSIONS_ADMIN_USER_SEARCH"
        ));

        permissions.put(RoleTypes.SERVICE_ADMINISTRATION, Arrays.asList(
                "USER_PERMISSIONS_ADMIN_USER_SEARCH",
                "USER_PERMISSIONS_TRANSACTION_LOOKUP",
                "USER_PERMISSIONS_FILING_RESEND",
                "USER_PERMISSIONS_USER_ROLES",
                "USER_PERMISSIONS_ROLE_ADMIN",
                "USER_PERMISSIONS_FILING_RESUBMIT",
                "USER_PERMISSIONS_USER_FILINGS",
                "USER_PERMISSIONS_ADMIN_SEARCH",
                "USER_PERMISSIONS_ADMIN_IMAGES",
                "USER_PERMISSIONS_ADMIN_COMPANY",
                "USER_PERMISSIONS_ADMIN_QUEUES"
        ));

        permissions.put(RoleTypes.STRIKE_OFF_OBJECTIONS_ADMIN, Arrays.asList(
                "USER_PERMISSIONS_STRIKE_OFF_OBJECTIONS_DOWNLOAD"
        ));

        permissions.put(RoleTypes.TEST_ROLE, Arrays.asList(
                "USER_PERMISSIONS_USER_ROLES",
                "USER_PERMISSIONS_ROLE_ADMIN"
        ));

        permissions.put(RoleTypes.TEST_TRANSACTION_ROLE, Arrays.asList(
                "USER_PERMISSIONS_FILING_RESUBMIT",
                "USER_PERMISSIONS_ADMIN_USER_SEARCH",
                "USER_PERMISSIONS_TRANSACTION_LOOKUP",
                "USER_PERMISSIONS_ADMIN_IMAGES",
                "USER_PERMISSIONS_USER_FILINGS",
                "USER_PERMISSIONS_ADMIN_VIEW_JSON"
        ));
    }

    public static List<String> getPermissions(RoleTypes role) {
        return permissions.get(role);
    }
}

