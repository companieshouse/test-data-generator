package uk.gov.companieshouse.api.testdata.service.impl;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        String randomDecodedWithSalt = new String(Base64.getUrlDecoder().decode(randomEncodedWithSalt));
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
}