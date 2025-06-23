package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;
import uk.gov.companieshouse.api.testdata.repository.PostcodesRepository;
import uk.gov.companieshouse.api.testdata.service.PostcodeService;

@Service
public class PostcodeServiceImpl implements PostcodeService {
    @Autowired
    private PostcodesRepository postcodesRepository;

    @Override
    public List<Postcodes> get(String country) {
        return postcodesRepository.findByCountryContaining(country);
    }
}
