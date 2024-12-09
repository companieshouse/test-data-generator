package uk.gov.companieshouse.api.testdata.spec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CompanySpecTest {

    // Test that a spec is not accepting field attributes that are not defined in the CompanySpec class
    @Test
    void testDeserializationFailsOnUnknownProperties() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String invalidJson = "{ \"jurisdiction\": \"england-wales\", \"companystatus\": \"active\" }";
        assertThrows(UnrecognizedPropertyException.class, () -> {
            objectMapper.readValue(invalidJson, CompanySpec.class);
        });
    }

    // Test that a spec is accepting field attributes that are defined in the CompanySpec class
    @Test
    void testDeserializationSucceedsWithValidProperties() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String validJson = "{ \"jurisdiction\": \"england-wales\", \"company_status\": \"dissolved\" }";
        assertDoesNotThrow(() -> {
            objectMapper.readValue(validJson, CompanySpec.class);
        });
    }
}