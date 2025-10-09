package uk.gov.companieshouse.api.testdata.model.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Field("email")
    private String email;

    @Field("password")
    private String password;

    @Field("created")
    private Instant created;

    @Field("locale")
    private String locale;

    @Field("forename")
    private String forename;

    @Field("surname")
    private String surname;

    @Field("roles")
    private List<String> roles;

    @Field("direct_login_privilege")
    private Boolean directLoginPrivilege;

    @Field("one_login_data.user_id")
    private String oneLoginUserId;

    @Field("entra_group_ids")
    private List<String> entraGroupIds;

    @Field("test_data")
    private Boolean testData;

    @Field("admin_user")
    private Boolean adminUser;

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Instant getCreated() {
        return created;
    }

    public String getLocale() {
        return locale;
    }

    public String getForename() {
        return forename;
    }

    public String getSurname() {
        return surname;
    }

    public List<String> getRoles() {
        return roles;
    }

    public Boolean getDirectLoginPrivilege() {
        return directLoginPrivilege;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setDirectLoginPrivilege(Boolean directLoginPrivilege) {
        this.directLoginPrivilege = directLoginPrivilege;
    }

    public String getOneLoginUserId() {
        return oneLoginUserId;
    }

    public void setOneLoginUserId(String oneLoginUserId) {
        this.oneLoginUserId = oneLoginUserId;
    }

    public List<String> getEntraGroupIds() {
        return entraGroupIds;
    }

    public void setEntraGroupIds(List<String> entraGroupIds) {
        this.entraGroupIds = entraGroupIds;
    }

    public Boolean getTestData() {
        return testData;
    }

    public void setTestData(Boolean testData) {
        this.testData = testData;
    }

    public Boolean getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(Boolean adminUser) {
        this.adminUser = adminUser;
    }
}
