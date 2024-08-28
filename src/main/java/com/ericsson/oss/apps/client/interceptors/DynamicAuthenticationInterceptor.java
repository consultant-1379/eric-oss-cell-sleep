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

package com.ericsson.oss.apps.client.interceptors;

import com.ericsson.oss.apps.client.gw.GatewayServiceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@ConditionalOnExpression("!'${gateway.auth.login:}'.isEmpty() || !'${gateway.auth.password:}'.isEmpty()")
public class DynamicAuthenticationInterceptor extends AuthenticationInterceptor {

    public static final String SESSION_KEY = "JSESSIONID";
    public static final String EQUAL = "=";

    private final GatewayServiceApi gatewayServiceApi;

    @PostConstruct
    private void init() {
        refreshSession();
    }

    @Scheduled(initialDelayString = "${gateway.auth.refresh-period}000",
            fixedRateString = "${gateway.auth.refresh-period}000")
    void refreshSession() {
        final String sessionValue = SESSION_KEY + EQUAL + gatewayServiceApi.login();
        session.set(sessionValue);
    }
}
