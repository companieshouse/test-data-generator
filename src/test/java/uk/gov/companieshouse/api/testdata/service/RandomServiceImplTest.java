package uk.gov.companieshouse.api.testdata.service;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.testdata.service.impl.RandomServiceImpl;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RandomServiceImplTest {

    private RandomServiceImpl randomService = new RandomServiceImpl();

    @Test
    void getRandomInteger() {
        String randomInt = this.randomService.getRandomInteger(6);
        assertEquals(6, randomInt.length());
    }

    @Test
    void getEncodedIdWithSalt() {
        String randomEncodedWithSalt = this.randomService.getEncodedIdWithSalt(5, 5);
        assertNotNull(randomEncodedWithSalt);

        String randomDecodedWithSalt = new String(Base64.getUrlDecoder().decode(randomEncodedWithSalt));
        assertEquals(10, randomDecodedWithSalt.length());
    }
}