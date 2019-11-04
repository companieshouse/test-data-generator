package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class RandomServiceImpl implements RandomService {

    @Override
    public String getRandomInteger(int digits) {
        int min = (int) Math.pow(10, digits - (double) 1);

        int randomInteger = ThreadLocalRandom.current().nextInt(min, min * 10);
        return String.valueOf(randomInteger);
    }
}
