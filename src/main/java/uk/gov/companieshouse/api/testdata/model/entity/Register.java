package uk.gov.companieshouse.api.testdata.model.entity;

import java.util.List;
import java.util.Map;
import org.springframework.data.mongodb.core.mapping.Field;

public class Register {

    @Field("register_type")
    private String registerType;

    @Field("items")
    private List<RegisterItem> items;

    @Field("links")
    private Map<String, String> links;

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    public List<RegisterItem> getItems() {
        return items;
    }

    public void setItems(List<RegisterItem> items) {
        this.items = items;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}