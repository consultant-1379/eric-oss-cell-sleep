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

package com.ericsson.oss.apps.client.beans;

import com.ericsson.oss.apps.client.interceptors.AuthenticationInterceptor;
import com.ericsson.oss.apps.client.util.InsecureRestTemplateCustomizer;
import com.ericsson.oss.apps.client.util.RestTemplateCustomizerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Configuration
public class RestTemplateConfiguration {

    @Bean
    @ConditionalOnProperty(value = "gateway.insecure", havingValue = "false")
    public RestTemplateCustomizerImpl restTemplateCustomizer() {
        return new RestTemplateCustomizerImpl();
    }

    @Bean
    @ConditionalOnProperty(value = "gateway.insecure", havingValue = "true")
    public InsecureRestTemplateCustomizer requestFactoryCustomizer()
        throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return new InsecureRestTemplateCustomizer();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, ObjectProvider<AuthenticationInterceptor> authenticationInterceptors) {
        RestTemplate restTemplate = builder.build();
        List<ClientHttpRequestInterceptor> existingInterceptors = restTemplate.getInterceptors();
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(existingInterceptors);
        Optional<AuthenticationInterceptor> authenticationInterceptor = authenticationInterceptors.orderedStream().findAny();
        if (authenticationInterceptor.isPresent()) {
            AuthenticationInterceptor interceptor = authenticationInterceptor.get();
            if (!existingInterceptors.contains(interceptor)) {
                interceptors.add(interceptor);
            }
        }

        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

}
