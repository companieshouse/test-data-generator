package uk.gov.companieshouse.api.testdata.model.rest.validation;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used to encapsulate validation errors
 */
public class ValidationErrors {

    @JsonProperty("errors")
    private Set<ValidationError> errors = new HashSet<>();

    /**
     * Add the given {@link ValidationError}
     *
     * @return True if added, false otherwise
     * @throws IllegalArgumentException on null errors
     */
    public boolean addError(final ValidationError error) {
        if (error == null) {
            throw new IllegalArgumentException("Error cannot be null");
        }

        return errors.add(error);
    }

    /**
     * Determine whether there are any {@link ValidationError}s
     *
     * @return True or false
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Determine whether the given {@link ValidationError} is contained
     *
     * @return True or false
     */
    public boolean containsError(ValidationError error) {
        if (error == null) {
            throw new IllegalArgumentException("Error cannot be null");
        }

        return errors.contains(error);
    }

    /**
     * Get the number of {@link ValidationError}s contained
     *
     * @return An int
     */
    @JsonIgnore
    public int getErrorCount() {
        return errors.size();
    }

}