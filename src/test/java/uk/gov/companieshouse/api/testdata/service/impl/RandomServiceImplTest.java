package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Base64;
import java.util.OptionalLong;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

class RandomServiceImplTest {

    private RandomServiceImpl randomService = new RandomServiceImpl();

    @Test
    void getRandomInteger() {
        final int digits = 6;
        
        Long random = randomService.getNumber(digits);
        assertNotNull(random);
        assertEquals(digits, String.valueOf(random).length());
    }
    
    @Test
    void getRandomString() {
        final int digits = 6;
        
        String random = randomService.getString(digits);
        assertNotNull(random);
        assertEquals(digits, random.length());
    }

    @Test
    void getEncodedIdWithSalt() {
        String randomEncodedWithSalt = this.randomService.getEncodedIdWithSalt(5, 5);
        assertNotNull(randomEncodedWithSalt);

        String randomDecodedWithSalt = new String(Base64.getUrlDecoder()
                .decode(randomEncodedWithSalt));
        assertEquals(10, randomDecodedWithSalt.length());
    }
    
    @Test
    void getEtag() {
        String etag = this.randomService.getEtag();
        assertNotNull(etag);
    }

    @Test
    void addSaltAndEncode() {
        final String baseString = "abcde";

        String salted = this.randomService.addSaltAndEncode(baseString, 5);
        assertNotNull(salted);

        String saltedDecodedWithSalt = new String(Base64.getUrlDecoder().decode(salted));
        assertEquals(10, saltedDecodedWithSalt.length());
    }

    @Test
    void getNumberInRange() {
        int startInclusive = 10;
        int endExclusive = 20;

        OptionalLong random = randomService.getNumberInRange(startInclusive, endExclusive);
        assertTrue(random.isPresent());
        assertTrue(random.getAsLong() >= startInclusive && random.getAsLong() < endExclusive);
    }

    @Test
    void generateAccountsDueDateByStatus_overdue() {
        LocalDate now = LocalDate.now();
        LocalDate overdueDate = randomService.generateAccountsDueDateByStatus("overdue");
        assertEquals(now.minusYears(1).minusMonths(11), overdueDate);
    }

    @Test
    void generateAccountsDueDateByStatus_dueSoon() {
        LocalDate now = LocalDate.now();
        LocalDate dueSoonDate = randomService.generateAccountsDueDateByStatus("due-soon");
        assertEquals(now.minusYears(1).minusMonths(9), dueSoonDate);
    }

    @Test
    void generateAccountsDueDateByStatus_default() {
        LocalDate now = LocalDate.now();
        LocalDate defaultDate = randomService.generateAccountsDueDateByStatus(null);
        assertEquals(now, defaultDate);
    }

    @Test
    void generateAccountsDueDateByStatus_empty() {
        LocalDate now = LocalDate.now();
        LocalDate emptyDate = randomService.generateAccountsDueDateByStatus("");
        assertEquals(now, emptyDate);
    }

    @Test
    void testGenerateAccountsDueDate_UnknownStatus() {
        String status = "random-status";
        LocalDate expectedDate = LocalDate.now();
        LocalDate result = randomService.generateAccountsDueDateByStatus(status);
        assertEquals(expectedDate, result, "Unknown status should return default date.");
    }

    @Test
    void generateObjectId() {
        ObjectId firstId = randomService.generateId();
        ObjectId secondId = randomService.generateId();
        assertNotNull(firstId);
        assertNotNull(secondId);
        assertNotEquals(firstId, secondId);
    }

    @Test
    void getCurrentDateTime() {
        Instant firstTime = randomService.getCurrentDateTime();
        Instant secondTime =
                randomService.getCurrentDateTime();
        assertNotNull(firstTime);
        assertNotNull(secondTime);
        assertNotEquals(firstTime, secondTime);
    }
}