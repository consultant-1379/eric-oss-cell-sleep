/*******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 *
 *
 * The copyright to the computer program(s) herein is the property of
 *
 * Ericsson Inc. The programs may be used and/or copied only with written
 *
 * permission from Ericsson Inc. or in accordance with the terms and
 *
 * conditions stipulated in the agreement/contract under which the
 *
 * program(s) have been supplied.
 ******************************************************************************/

package com.ericsson.oss.apps.controller.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.ericsson.oss.apps.api.SampleApi;

/**
 * Implementation of SampleApi generated by open api generator.
 */
@RestController
public class SampleApiControllerImpl implements SampleApi {
    /**
     * Logger for the class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SampleApiControllerImpl.class);

    @Override
    public ResponseEntity<String> sample() {
        LOG.info("Sample service called sample");
        return new ResponseEntity<>("Sample response", HttpStatus.OK);
    }
}
