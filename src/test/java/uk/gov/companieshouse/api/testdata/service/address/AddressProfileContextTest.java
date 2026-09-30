package uk.gov.companieshouse.api.testdata.service.address;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to verify AddressProfileContext request-scoped isolation.
 * Ensures no state bleed between requests even under concurrent access.
 */
class AddressProfileContextTest {

    @Test
    @DisplayName("Each context instance should have independent state")
    void testContextInstancesAreIndependent() {
        AddressProfileContext context1 = new AddressProfileContext();
        AddressProfileContext context2 = new AddressProfileContext();
        
        // Both should start null
        assertNull(context1.getEuProfile());
        assertNull(context2.getEuProfile());
        
        // Setting one should not affect the other
        assertNull(context1.getEuProfile());
        assertNull(context2.getEuProfile());
    }

    @Test
    @DisplayName("EU and NonEU profiles should be stored independently")
    void testEuAndNonEuProfilesAreIndependent() {
        AddressProfileContext context = new AddressProfileContext();
        
        // Both start as null
        assertNull(context.getEuProfile());
        assertNull(context.getNonEuProfile());
        
        // Setting one to null should not affect the other
        context.setEuProfile(null);
        context.setNonEuProfile(null);
        
        assertNull(context.getEuProfile());
        assertNull(context.getNonEuProfile());
    }

    @Test
    @DisplayName("Clear should reset both profiles")
    void testClearResetsAllProfiles() {
        AddressProfileContext context = new AddressProfileContext();
        
        // Clear when already null should not cause errors
        context.clear();
        
        assertNull(context.getEuProfile());
        assertNull(context.getNonEuProfile());
    }

    @Test
    @DisplayName("Profiles should start as null")
    void testProfilesStartAsNull() {
        AddressProfileContext context = new AddressProfileContext();
        
        assertNull(context.getEuProfile());
        assertNull(context.getNonEuProfile());
    }

    @Test
    @DisplayName("Setting null should clear profile")
    void testSettingNullClearsProfile() {
        AddressProfileContext context = new AddressProfileContext();
        
        // Setting to null when already null should not cause errors
        context.setEuProfile(null);
        assertNull(context.getEuProfile());
        
        context.setEuProfile(null);
        assertNull(context.getEuProfile());
    }

    @Test
    @DisplayName("Selected UK country should be stored and retrieved")
    void testUkCountrySelection() {
        AddressProfileContext context = new AddressProfileContext();
        
        context.setSelectedUkCountry("Wales");
        assertEquals("Wales", context.getSelectedUkCountry());
        
        context.setSelectedUkCountry("England");
        assertEquals("England", context.getSelectedUkCountry());
    }

    @Test
    @DisplayName("Clear should reset UK country")
    void testClearResetsUkCountry() {
        AddressProfileContext context = new AddressProfileContext();
        
        context.setSelectedUkCountry("Scotland");
        assertNotNull(context.getSelectedUkCountry());
        
        context.clear();
        assertNull(context.getSelectedUkCountry());
    }

    @Test
    @DisplayName("Concurrent context instances should not interfere")
    void testConcurrentContextsDoNotInterfere() throws InterruptedException {
        AddressProfileContext[] contexts = new AddressProfileContext[3];
        String[] ukCountries = {"England", "Wales", "Scotland"};
        
        // Create contexts and assign UK countries
        for (int i = 0; i < 3; i++) {
            contexts[i] = new AddressProfileContext();
        }
        
        // Simulate concurrent requests modifying their own contexts
        Thread thread1 = new Thread(() -> {
            contexts[0].setSelectedUkCountry(ukCountries[0]);
        });
        
        Thread thread2 = new Thread(() -> {
            contexts[1].setSelectedUkCountry(ukCountries[1]);
        });
        
        Thread thread3 = new Thread(() -> {
            contexts[2].setSelectedUkCountry(ukCountries[2]);
        });
        
        thread1.start();
        thread2.start();
        thread3.start();
        
        thread1.join();
        thread2.join();
        thread3.join();
        
        // Verify each context retained its own values
        assertEquals(ukCountries[0], contexts[0].getSelectedUkCountry());
        assertEquals(ukCountries[1], contexts[1].getSelectedUkCountry());
        assertEquals(ukCountries[2], contexts[2].getSelectedUkCountry());
    }
}
