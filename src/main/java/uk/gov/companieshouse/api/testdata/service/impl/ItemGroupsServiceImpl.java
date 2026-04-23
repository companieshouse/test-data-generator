package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.repository.ItemGroupsRepository;
import uk.gov.companieshouse.api.testdata.service.ItemGroupsService;

@Service
public class ItemGroupsServiceImpl implements ItemGroupsService {

    private final ItemGroupsRepository itemGroupsRepository;

    @Autowired
    public ItemGroupsServiceImpl(ItemGroupsRepository itemGroupsRepository) {
        this.itemGroupsRepository = itemGroupsRepository;
    }

    @Override
    public boolean deleteItemGroups(String orderNumber) {
        var itemGroups = itemGroupsRepository.findByDataOrderNumber(orderNumber);
        if (itemGroups.isPresent()) {
            itemGroupsRepository.deleteByDataOrderNumber(orderNumber);
            return true;
        }
        return false;
    }
}