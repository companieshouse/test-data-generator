package uk.gov.companieshouse.api.testdata.service.impl;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class RandomServiceImpl implements RandomService {

    private static final String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final SecureRandom rnd = new SecureRandom();

    @Override
    public Long getNumber(int digits) {
        long min = (long) Math.pow(10, digits - (double) 1);

        return ThreadLocalRandom.current().nextLong(min, min * 10);
    }
    
    @Override
    public String getString(int digits) {
        StringBuilder salt = new StringBuilder();
        while (salt.length() < digits) {
            int index = rnd.nextInt(SALT_CHARS.length());
            salt.append(SALT_CHARS.charAt(index));
        }
        return salt.toString();
    }

    @Override
    public String getEncodedIdWithSalt(int idLength, int saltLength) {
        String id = String.valueOf(getNumber(idLength));
        String salt = getString(saltLength);
        String idSalt = id + salt;
        return Base64.getUrlEncoder().encodeToString(idSalt.getBytes(UTF_8));
    }
    
    @Override
    public String getEtag() {
        return GenerateEtagUtil.generateEtag();
    }

    @Override
    public String addSaltAndEncode(String baseString, int saltLength) {
        String salt = getString(saltLength);
        String baseSalt = baseString + salt;
        return Base64.getUrlEncoder().encodeToString(baseSalt.getBytes(UTF_8));
    }
}
