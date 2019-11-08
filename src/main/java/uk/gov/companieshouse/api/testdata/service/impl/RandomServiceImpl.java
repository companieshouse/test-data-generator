package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class RandomServiceImpl implements RandomService {

    private static final String SALT_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final Random rnd = new Random();

    @Override
    public String getRandomInteger(int digits) {
        long min = (long) Math.pow(10, digits - (double) 1);

        long randomInteger = ThreadLocalRandom.current().nextLong(min, min * 10);
        return String.valueOf(randomInteger);
    }

    @Override
    public String getEncodedIdWithSalt(int idLength, int saltLength) {
        String id = getRandomInteger(idLength);
        String salt = generateSalt(saltLength);
        String idSalt = id + salt;
        return Base64.getUrlEncoder().encodeToString(idSalt.getBytes(UTF_8));
    }

    private String generateSalt(int saltLength) {
        StringBuilder salt = new StringBuilder();
        while (salt.length() < saltLength) {
            int index = rnd.nextInt(SALT_CHARS.length());
            salt.append(SALT_CHARS.charAt(index));
        }
        return salt.toString();
    }
}
