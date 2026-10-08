package uk.gov.companieshouse.api.testdata.service.impl;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.OptionalLong;
import java.util.concurrent.ThreadLocalRandom;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class RandomServiceImpl implements RandomService {

    private static final String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int MINIMUM_AGE = 16;
    private static final int MAXIMUM_AGE = 100;
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
        var result = LocalDate.now();
        if (StringUtils.hasText(accountsDueStatus)) {
            if ("overdue".equalsIgnoreCase(accountsDueStatus)) {
                result = result.minusYears(1).minusMonths(11);
            } else if ("due-soon".equalsIgnoreCase(accountsDueStatus)) {
                result = result.minusYears(1).minusMonths(9);
            }
        }
        return result;
    }

    @Override
    public LocalDate generateDateOfBirth() {
        int age = (int) getNumberInRange(MINIMUM_AGE, MAXIMUM_AGE + 1)
                .orElseThrow(() -> new IllegalStateException("Unable to generate a random age"));
        LocalDate today = LocalDate.now();
        LocalDate latestDateOfBirth = today.minusYears(age);
        LocalDate earliestDateOfBirth = today.minusYears(age + 1).plusDays(1);
        int daysInRange = (int) ChronoUnit.DAYS.between(earliestDateOfBirth, latestDateOfBirth) + 1;
        int randomDay = (int) getNumberInRange(0, daysInRange)
                .orElseThrow(() -> new IllegalStateException("Unable to generate a random date of birth"));
        return earliestDateOfBirth.plusDays(randomDay);
    }

    @Override
    public ObjectId generateId() {
        return new ObjectId();
    }

     @Override
    public String getTransactionId() {
        int randomNumber = 100000 + RND.nextInt(900000);
        var value = String.valueOf(randomNumber);
        return String.join("-", value, value, value);
    }

    @Override
    public Instant getCurrentDateTime() {
        return Instant.now().atZone(ZoneOffset.UTC).toInstant();
    }
}
