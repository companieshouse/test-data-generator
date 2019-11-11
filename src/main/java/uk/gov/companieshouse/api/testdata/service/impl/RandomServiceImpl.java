package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class RandomServiceImpl implements RandomService {

    private static final String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final Random rnd = new Random();

    @Override
    public Long getRandomNumber(int digits) {
        long min = (long) Math.pow(10, digits - (double) 1);

        return ThreadLocalRandom.current().nextLong(min, min * 10);
    }
    
    @Override
    public String getRandomString(int digits) {
        StringBuilder salt = new StringBuilder();
        while (salt.length() < digits) {
            int index = rnd.nextInt(SALT_CHARS.length());
            salt.append(SALT_CHARS.charAt(index));
        }
        return salt.toString();
    }

    @Override
    public String getEncodedIdWithSalt(int idLength, int saltLength) {
        String id = String.valueOf(getRandomNumber(idLength));
        String salt = getRandomString(saltLength);
        String idSalt = id + salt;
        return Base64.getUrlEncoder().encodeToString(idSalt.getBytes(UTF_8));
    }
}
