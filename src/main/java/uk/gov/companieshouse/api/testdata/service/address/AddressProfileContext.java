package uk.gov.companieshouse.api.testdata.service.address;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import uk.gov.companieshouse.api.testdata.service.address.profile.EuProfile;
import uk.gov.companieshouse.api.testdata.service.address.profile.NonEuProfile;

/**
 * Request-scoped context for holding address profile selections.
 * Each HTTP request gets its own instance, ensuring thread safety.
 */
@Component
@RequestScope
public class AddressProfileContext {
    
    private EuProfile euProfile;
    private NonEuProfile nonEuProfile;
    private String selectedUkCountry;
    
    public EuProfile getEuProfile() {
        return euProfile;
    }
    
    public void setEuProfile(EuProfile euProfile) {
        this.euProfile = euProfile;
    }
    
    public NonEuProfile getNonEuProfile() {
        return nonEuProfile;
    }
    
    public void setNonEuProfile(NonEuProfile nonEuProfile) {
        this.nonEuProfile = nonEuProfile;
    }
    
    public String getSelectedUkCountry() {
        return selectedUkCountry;
    }
    
    public void setSelectedUkCountry(String selectedUkCountry) {
        this.selectedUkCountry = selectedUkCountry;
    }
    
    public void clear() {
        this.euProfile = null;
        this.nonEuProfile = null;
        this.selectedUkCountry = null;
    }
}
