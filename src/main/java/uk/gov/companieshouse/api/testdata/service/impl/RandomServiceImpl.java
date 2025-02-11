package uk.gov.companieshouse.api.testdata.service.impl;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.OptionalLong;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class RandomServiceImpl implements RandomService {

    private static final String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final SecureRandom RND = new SecureRandom();

    @Override
    public Long getNumber(int digits) {
        long min = (long) Math.pow(10, digits - (double) 1);

        return ThreadLocalRandom.current().nextLong(min, min * 10);
    }

    @Override
    public OptionalLong getNumberInRange(int startInclusive, int endExclusive) {

        if (endExclusive <= startInclusive) {
            int temp = startInclusive;
            startInclusive = endExclusive;
            endExclusive = temp;
        }

        return RND.longs(startInclusive, endExclusive)
                        .findFirst();
    }

    @Override
    public String getString(int digits) {
        StringBuilder salt = new StringBuilder();
        while (salt.length() < digits) {
            int index = RND.nextInt(SALT_CHARS.length());
            salt.append(SALT_CHARS.charAt(index));
        }
        return salt.toString();
    }

    @Override
    public String getEncodedIdWithSalt(int idLength, int saltLength) {
        String id = String.valueOf(getNumber(idLength));
        return addSaltAndEncode(id, saltLength);
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

    @Override
    public LocalDate generateAccountsDueDateByStatus(String accountsDueStatus) {
        var now = LocalDate.now();
        if (accountsDueStatus != null) {
            if (accountsDueStatus.equalsIgnoreCase("overdue")) {
                now = now.minusYears(1).minusMonths(11);
            } else if (accountsDueStatus.equalsIgnoreCase("due-soon")) {
                now = now.minusYears(1).minusMonths(9);
            }
        }
        return now;
    }
}
