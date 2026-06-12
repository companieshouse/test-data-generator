package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.Basket;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.model.rest.request.BasketRequest;
import uk.gov.companieshouse.api.testdata.repository.BasketRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;

@ExtendWith(MockitoExtension.class)
class BasketServiceImplTest {

    private static final String USER_ID = "user-123";

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private BasketServiceImpl basketService;

    @Test
    void createOrUpdateBasketWhenBasketExistsAppendsItemsAndUpdatesTimestamp() {
        Basket existingBasket = new Basket();
        existingBasket.setId(USER_ID);
        existingBasket.setItems(new ArrayList<>(List.of(new Basket.Item())));

        BasketRequest basketRequest = new BasketRequest();
        List<Basket.Item> itemsToAdd = List.of(new Basket.Item(), new Basket.Item());
        Address address = new Address();

        when(addressService.getAddress(JurisdictionType.UNITED_KINGDOM)).thenReturn(address);
        when(basketRepository.findById(USER_ID)).thenReturn(Optional.of(existingBasket));

        Instant beforeUpdate = Instant.now();
        Basket updatedBasket = basketService.createOrUpdateBasket(USER_ID, basketRequest, itemsToAdd);

        assertSame(existingBasket, updatedBasket);
        assertEquals(3, updatedBasket.getItems().size());
        assertNotNull(updatedBasket.getUpdatedAt());
        assertFalse(updatedBasket.getUpdatedAt().isBefore(beforeUpdate));
        verify(addressService).getAddress(JurisdictionType.UNITED_KINGDOM);
        verify(basketRepository).findById(USER_ID);
    }

    @Test
    void createOrUpdateBasketWhenExistingItemsAreNullInitializesItemsList() {
        Basket existingBasket = new Basket();
        existingBasket.setId(USER_ID);
        existingBasket.setItems(null);

        List<Basket.Item> itemsToAdd = List.of(new Basket.Item());
        Address address = new Address();

        when(addressService.getAddress(JurisdictionType.UNITED_KINGDOM)).thenReturn(address);
        when(basketRepository.findById(USER_ID)).thenReturn(Optional.of(existingBasket));

        Basket updatedBasket = basketService.createOrUpdateBasket(USER_ID, new BasketRequest(), itemsToAdd);

        assertNotNull(updatedBasket.getItems());
        assertEquals(1, updatedBasket.getItems().size());
    }

    @Test
    void createOrUpdateBasketWhenBasketDoesNotExistCreatesNewBasketWithSpecAndAddress() {
        BasketRequest basketRequest = new BasketRequest();
        basketRequest.setForename("Jane");
        basketRequest.setSurname("Doe");
        basketRequest.setEnrolled(true);

        List<Basket.Item> items = List.of(new Basket.Item());
        Address address = new Address();

        when(addressService.getAddress(JurisdictionType.UNITED_KINGDOM)).thenReturn(address);
        when(basketRepository.findById(USER_ID)).thenReturn(Optional.empty());

        Basket createdBasket = basketService.createOrUpdateBasket(USER_ID, basketRequest, items);

        assertEquals(USER_ID, createdBasket.getId());
        assertNotNull(createdBasket.getCreatedAt());
        assertNotNull(createdBasket.getUpdatedAt());
        assertEquals("Jane", createdBasket.getForename());
        assertEquals("Doe", createdBasket.getSurname());
        assertTrue(createdBasket.isEnrolled());
        assertSame(items, createdBasket.getItems());
    }

    @Test
    void createOrUpdateBasketWhenSpecIsNullUsesDefaultPersonFields() {
        List<Basket.Item> items = List.of(new Basket.Item());
        Address address = new Address();

        when(addressService.getAddress(JurisdictionType.UNITED_KINGDOM)).thenReturn(address);
        when(basketRepository.findById(USER_ID)).thenReturn(Optional.empty());

        Basket createdBasket = basketService.createOrUpdateBasket(USER_ID, null, items);

        assertEquals(USER_ID, createdBasket.getId());
        assertNull(createdBasket.getForename());
        assertNull(createdBasket.getSurname());
        assertFalse(createdBasket.isEnrolled());
        assertSame(items, createdBasket.getItems());
    }

    @Test
    void saveBasketDelegatesToRepository() {
        Basket basket = new Basket();

        basketService.saveBasket(basket);

        verify(basketRepository).save(basket);
    }

    @Test
    void deleteBasketDeletesWhenBasketExistsWithNonNullId() {
        Basket basket = new Basket();
        basket.setId(USER_ID);
        when(basketRepository.findById(USER_ID)).thenReturn(Optional.of(basket));

        basketService.deleteBasket(USER_ID);

        verify(basketRepository).delete(basket);
    }

    @Test
    void deleteBasketDoesNothingWhenBasketNotFound() {
        when(basketRepository.findById(USER_ID)).thenReturn(Optional.empty());

        basketService.deleteBasket(USER_ID);

        verify(basketRepository, never()).delete(any());
    }

    @Test
    void deleteBasketDoesNothingWhenBasketIdIsNull() {
        Basket basket = new Basket();
        basket.setId(null);
        when(basketRepository.findById(USER_ID)).thenReturn(Optional.of(basket));

        basketService.deleteBasket(USER_ID);

        verify(basketRepository, never()).delete(any());
    }
}
