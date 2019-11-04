package uk.gov.companieshouse.api.testdata.service;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.testdata.service.impl.RandomServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RandomServiceImplTest {

    private RandomServiceImpl randomService = new RandomServiceImpl();

    @Test
    void getRandomInteger() {
        String randomInt = this.randomService.getRandomInteger(6);
        assertEquals(6, randomInt.length());
    }
}