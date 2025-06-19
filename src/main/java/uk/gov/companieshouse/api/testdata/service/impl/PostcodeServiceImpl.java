package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;
import uk.gov.companieshouse.api.testdata.repository.PostCodesRepository;
import uk.gov.companieshouse.api.testdata.service.PostCodeService;

@Service
public class PostcodeServiceImpl implements PostCodeService {
    @Autowired
    private PostCodesRepository postCodesRepository;

    @Override
    public List<Postcodes> get(String country) {
        List<Postcodes> postCodes = postCodesRepository.findByCountryContaining(country);
        return postCodes.isEmpty() ? Collections.emptyList() : postCodes;
    }
}
