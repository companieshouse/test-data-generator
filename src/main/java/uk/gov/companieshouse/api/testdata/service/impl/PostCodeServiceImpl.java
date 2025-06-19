package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.PostCodes;
import uk.gov.companieshouse.api.testdata.repository.PostCodesRepository;
import uk.gov.companieshouse.api.testdata.service.PostCodeService;

@Service
public class PostCodeServiceImpl implements PostCodeService {
    @Autowired
    private PostCodesRepository postCodesRepository;

    @Override
    public List<PostCodes> get(String country) {
        List<PostCodes> postCodes = postCodesRepository.findByCountryContaining(country);
        return postCodes.isEmpty() ? Collections.emptyList() : postCodes;
    }
}
