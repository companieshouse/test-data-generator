package uk.gov.companieshouse.api.testdata.model.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public abstract class AbstractCostedCompanyBasketRequest extends AbstractCompanyBasketRequest {

    @JsonProperty("customer_reference")
    private String customerReference;

    @JsonProperty("item_costs")
    private List<ItemCostsRequest> itemCosts;

    @JsonProperty("postage_cost")
    private String postageCost;

    @JsonProperty("total_item_cost")
    private String totalItemCost;

    public String getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public List<ItemCostsRequest> getItemCosts() {
        return itemCosts;
    }

    public void setItemCosts(List<ItemCostsRequest> itemCosts) {
        this.itemCosts = itemCosts;
    }

    public String getPostageCost() {
        return postageCost;
    }

    public void setPostageCost(String postageCost) {
        this.postageCost = postageCost;
    }

    public String getTotalItemCost() {
        return totalItemCost;
    }

    public void setTotalItemCost(String totalItemCost) {
        this.totalItemCost = totalItemCost;
    }
}
