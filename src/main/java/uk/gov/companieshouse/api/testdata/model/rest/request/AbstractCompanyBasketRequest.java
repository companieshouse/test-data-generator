package uk.gov.companieshouse.api.testdata.model.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public abstract class AbstractCompanyBasketRequest {

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("item_options")
    private List<ItemOptionsRequest> itemOptions;

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("postal_delivery")
    private boolean postalDelivery;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("user_id")
    @NotNull(message = "User ID cannot be null")
    private String userId;

    @JsonProperty("basket")
    private BasketRequest basketRequest;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public List<ItemOptionsRequest> getItemOptions() {
        return itemOptions;
    }

    public void setItemOptions(List<ItemOptionsRequest> itemOptions) {
        this.itemOptions = itemOptions;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public boolean isPostalDelivery() {
        return postalDelivery;
    }

    public void setPostalDelivery(boolean postalDelivery) {
        this.postalDelivery = postalDelivery;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BasketRequest getBasketSpec() {
        return basketRequest;
    }

    public void setBasketSpec(BasketRequest basketRequest) {
        this.basketRequest = basketRequest;
    }
}
