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

package com.ericsson.oss.apps.client.config;

import com.ericsson.oss.apps.client.ApiClient;
import com.ericsson.oss.apps.client.auth.ApiKeyAuth;
import com.ericsson.oss.apps.client.cts.LteServiceApi;
import com.ericsson.oss.apps.client.ncmp.NetworkCmProxyApi;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class ClientConfiguration {

    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String TENANT = "tenant";

    private final GatewayProperties gatewayProperties;

    @Bean
    @Primary
    public ApiClient apiClient(RestTemplateBuilder builder) {
        ApiClient apiClient = new ApiClient(builder.build());
        apiClient.setBasePath(gatewayProperties.getUrl());
        Arrays.asList(LOGIN, PASSWORD, TENANT).forEach(key -> {
            ApiKeyAuth apiKey = (ApiKeyAuth) apiClient.getAuthentication(key);
            apiKey.setApiKey(gatewayProperties.getAuth(key));
        });
        return apiClient;
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ApiClient apiClientPrototype(RestTemplate restTemplate, String serviceName) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(gatewayProperties.getBasePath(serviceName));
        gatewayProperties.getService(serviceName).getHeadersAsMap().forEach(apiClient::addDefaultHeader);
        return apiClient;
    }

    @Bean(name = "cts")
    public LteServiceApi ctsService(RestTemplate restTemplate) {
        ApiClient apiClient = apiClientPrototype(restTemplate, "cts");
        return new LteServiceApi(apiClient);
    }

    @Bean(name = "ncmp")
    public NetworkCmProxyApi ncmpService(RestTemplate restTemplate) {
        ApiClient apiClient = apiClientPrototype(restTemplate, "ncmp");
        return new NetworkCmProxyApi(apiClient);
    }
}
