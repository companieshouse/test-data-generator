package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.Basket;
import uk.gov.companieshouse.api.testdata.model.rest.request.BasketRequest;
import uk.gov.companieshouse.api.testdata.repository.BasketRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;

@Service
public class BasketServiceImpl {

    private final BasketRepository basketRepository;

    private final AddressService addressService;

    public BasketServiceImpl(BasketRepository basketRepository, AddressService addressService) {
        this.basketRepository = basketRepository;
        this.addressService = addressService;
    }

    public Basket createOrUpdateBasket(String userId, BasketRequest basketSpec, List<Basket.Item> items) {
        Instant currentDateTime = Instant.now().atZone(ZoneOffset.UTC).toInstant();
        var address = addressService.getAddress(JurisdictionType.UNITED_KINGDOM);

        Optional<Basket> existingBasketOpt = basketRepository.findById(userId);

        if (existingBasketOpt.isPresent()) {
            var existingBasket = existingBasketOpt.get();
            List<Basket.Item> existingItems = existingBasket.getItems();
            if (existingItems == null) {
                existingItems = new ArrayList<>();
                existingBasket.setItems(existingItems);
            }
            existingItems.addAll(items);
            existingBasket.setUpdatedAt(currentDateTime);
            return existingBasket;
        }

        var basket = new Basket();
        basket.setId(userId);
        basket.setCreatedAt(currentDateTime);
        basket.setUpdatedAt(currentDateTime);
        basket.setDeliveryDetails(address);
        if (basketSpec != null) {
            basket.setForename(basketSpec.getForename());
            basket.setSurname(basketSpec.getSurname());
            basket.setEnrolled(basketSpec.getEnrolled());
        }
        basket.setItems(items);

        return basket;
    }

    public void saveBasket(Basket basket) {
        basketRepository.save(basket);
    }

    public void deleteBasket(String basketId) {
        basketRepository.findById(basketId)
            .ifPresent(basket -> {
                if (basket.getId() != null) {
                    basketRepository.delete(basket);
                }
            });
    }
}
