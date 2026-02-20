package uk.gov.companieshouse.api.testdata.model.rest.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRoles {
    CHS_ADMIN_SUPERVISOR("chs admin supervisor"),
    CHS_ADMIN_RESTRICTED_WORD("chs admin restricted word"),
    CHS_ADMIN_COMPANY_REFRESH("chs admin company refresh"),
    CHS_ADMIN_SUPPORT_MEMBER("chs admin support member"),
    CHS_ADMIN_EXTENSIONS("chs admin extensions"),
    CHS_ADMIN_FOI("chs admin foi"),
    CHS_ADMIN_STRIKE_OFF_OBJECTIONS("chs admin strike off objections"),
    CHS_ADMIN_BADOS_USER("chs admin bados user"),
    CHS_ADMIN_APPEALS_TEAM("chs admin appeals team"),
    CHS_ADMIN_CSI_SUPPORT("chs admin csi support"),
    CHS_ADMIN_FES_USER("chs admin fes user"),
    CHS_ADMIN_BULK_REFUNDS("chs admin bulk refunds"),
    CHS_ADMIN_ORDERS_INVESTIGATOR("chs admin orders investigator"),
    CHS_ADMIN_INTERNAL_MIDS_PURCHASER("chs admin internal mids purchaser"),
    CHS_ADMIN_INTERNAL_CERT_DOCS_PURCHASER("chs admin internal cert docs purchaser"),
    CHS_ADMIN_INTERNAL_CERT_PURCHASER("chs admin internal cert purchaser"),
    CHS_ADMIN_SEARCH_IDENTITY_VERIFICATION("chs admin search identity verification");

    private final String groupName;

    /**
     * Constructs a UserRoles enum constant with the group name.
     * @param groupName The display name of the group/role.
     */
    UserRoles(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Specifies that this method should be used for serialization (marshalling).
     * @return The group name string.
     */
    @JsonValue
    public String getGroupName() {
        return groupName;
    }

    /**
     * Specifies that this method should be used for deserialization (unmarshalling).
     * @param groupName The group name string from JSON.
     * @return The matching UserRoles enum constant.
     * @throws IllegalArgumentException If no matching group name is found.
     */
    @JsonCreator
    public static UserRoles fromGroupName(String groupName) {
        for (UserRoles perm : values()) {
            if (perm.groupName.equalsIgnoreCase(groupName)) {
                return perm;
            }
        }
        throw new IllegalArgumentException("Unknown group name: " + groupName);
    }
}