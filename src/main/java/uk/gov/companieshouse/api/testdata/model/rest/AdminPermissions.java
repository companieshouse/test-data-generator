package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AdminPermissions {
    CHS_ADMIN_SUPERVISOR("chs admin supervisor", "b7c48d82-444b-414f-870b-4da96c2d075e"),
    CHS_ADMIN_RESTRICTED_WORD("chs admin restricted word", "71c2fd0b-62a3-4c5d-9417-b2cf12b441d1"),
    CHS_ADMIN_COMPANY_REFRESH("chs admin company refresh", "49fe6f2a-0301-45aa-84fe-918617d0d31b"),
    CHS_ADMIN_SUPPORT_MEMBER("chs admin support member", "1cf86422-f0e6-4463-9aac-37c210cdc7f6"),
    CHS_ADMIN_EXTENSIONS("chs admin extensions", "f00e5d3e-9563-4643-85e3-62605617e802"),
    CHS_ADMIN_FOI("chs admin foi", "dc04442b-a1a0-4bd2-9f5c-032d4cb80017"),
    CHS_ADMIN_STRIKE_OFF_OBJECTIONS("chs admin strike off objections", "008a25fe-5cf5-4df7-9091-e94682e4340e"),
    CHS_ADMIN_BADOS_USER("chs admin bados user", "6b19906c-b9a6-409c-8017-d35b9222856b"),
    CHS_ADMIN_APPEALS_TEAM("chs admin appeals team", "6374fb48-0d86-458c-9dc7-783fd13605e2"),
    CHS_ADMIN_CSI_SUPPORT("chs admin csi support", "8aa9fc1c-8d78-4ce3-8ba9-fee57adf3a84"),
    CHS_ADMIN_FES_USER("chs admin fes user", "c5d61e11-ec3d-4652-ab6c-061cf02868f3"),
    CHS_ADMIN_BULK_REFUNDS("chs admin bulk refunds", "e8448c80-92b1-4170-9cb8-8f8cc436fff1"),
    CHS_ADMIN_ORDERS_INVESTIGATOR("chs admin orders investigator", "e64259bc-282f-48cd-97ed-f9b04ae41827"),
    CHS_ADMIN_INTERNAL_MIDS_PURCHASER("chs admin internal mids purchaser", "407aa1df-f4c6-419f-a68a-a7f8635626c2"),
    CHS_ADMIN_INTERNAL_CERT_DOCS_PURCHASER("chs admin internal cert docs purchaser", "29102f51-ba64-4aa6-ab77-690100b8ba29"),
    CHS_ADMIN_INTERNAL_CERT_PURCHASER("chs admin internal cert purchaser", "25241dbf-3789-4fba-a3d3-32024d479f3b"),
    CHS_ADMIN_SEARCH_IDENTITY_VERIFICATION("chs admin search identity verification", "037b1e7a-9719-4344-8e80-c97d723b2a5b");

    private final String groupName;
    private final String entraGroupId;

    AdminPermissions(String groupName, String entraGroupId) {
        this.groupName = groupName;
        this.entraGroupId = entraGroupId;
    }

    @JsonValue
    public String getGroupName() {
        return groupName;
    }

    public String getEntraGroupId() {
        return entraGroupId;
    }

    @JsonCreator
    public static AdminPermissions fromGroupName(String groupName) {
        for (AdminPermissions perm : values()) {
            if (perm.groupName.equalsIgnoreCase(groupName)) {
                return perm;
            }
        }
        throw new IllegalArgumentException("Unknown group name: " + groupName);
    }
}