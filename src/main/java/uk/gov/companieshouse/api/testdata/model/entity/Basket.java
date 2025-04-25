package uk.gov.companieshouse.api.testdata.model.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "basket")
public class Basket {

    @Id
    @Field("_id")
    private String id;

    @Field("created_at")
    private Instant createdAt;

    @Field("updated_at")
    private Instant updatedAt;

    @Field("data.delivery_details")
    private Address deliveryDetails;

    @Field("data.delivery_details.forename")
    private String forename;

    @Field("data.delivery_details.surname")
    private String surname;

    @Field("data.items")
    private List<Item> items;

    @Field("data.enrolled")
    private boolean enrolled;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Address getDeliveryDetails() {
        return deliveryDetails;
    }

    public void setDeliveryDetails(Address deliveryDetails) {
        this.deliveryDetails = deliveryDetails;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public void setForeName(String forename) {
        this.forename = forename;
    }

    public String getForeName() {
        return forename;
    }

    public void setSurName(String surname) {
        this.surname = surname;
    }

    public String getSurName() {
        return surname;
    }

    // Separate class (not inner class)
    public static class Item {
        @Field("item_uri")
        private String itemUri;

        public String getItemUri() {
            return itemUri;
        }

        public void setItemUri(String itemUri) {
            this.itemUri = itemUri;
        }
    }
}
