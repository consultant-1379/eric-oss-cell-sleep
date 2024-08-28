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

package com.ericsson.oss.apps.init;

import com.ericsson.oss.apps.client.cts.LteServiceApi;
import com.ericsson.oss.apps.client.cts.model.ENodeB;
import com.ericsson.oss.apps.client.ncmp.NetworkCmProxyApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class NcmpDemo {

    private static final String MSG_ID = "[NCMP Demo] ";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gateway.insecure}")
    private String insecure;

    @Value("${gateway.host}")
    private String host;

    @PostConstruct
    public void init() {
        // Step 1 - Login and get jSessionId (Done in DynamicAuthenticationInterceptor)
        final String mode = insecure.equalsIgnoreCase("true") ? "insecure": "secure";
        logMessage(String.format("Starting NCMP Demo in '%s' mode", mode));

        // Step 2 - Connect to CTS
        logMessage("Connecting to CTS");
        logMessage("Getting eNodeB information from CTS");
        final LteServiceApi lteServiceApi = (LteServiceApi) applicationContext.getBean("cts", restTemplate);
        final List<ENodeB> eNodeBs = lteServiceApi.listENodeBs();
        logMessage(String.format("Retrieved %d eNodeB's", eNodeBs.size()));
        final List<String> externalIds = eNodeBs.stream().map(ENodeB::getExternalId).collect(Collectors.toList());

        // Step 3 - Connect to NCMP
        logMessage("Connecting to NCMP");
        final NetworkCmProxyApi networkCmProxyApi = (NetworkCmProxyApi) applicationContext.getBean("ncmp", restTemplate);

        logMessage("Selecting first eNodeB");
        final String externalId = externalIds.get(0);
        logMessage(String.format("ExternalId: '%s'", externalId));

        final String cmHandle = getCmHandle(externalId);
        logMessage(String.format("cmHandle: '%s'", cmHandle));

        final String resourceIdentifier = getResourceIdentifier(externalId);
        logMessage(String.format("Resource Identifier: '%s'", resourceIdentifier));

        final Object result = networkCmProxyApi.getResourceDataOperationalForCmHandle(cmHandle,
                resourceIdentifier, null, "");
        logMessage(String.format("NCMP Result: %n%s", GSON.toJson(result)));
    }

    private static String getCmHandle(final String externalId) {
        return externalId.split("/")[0].trim();
    }

    private static String getResourceIdentifier(final String externalId) {
        final List<String> resources = Stream.of(externalId.split("/"))
                .filter(entry -> entry.contains("="))
                .map(entry -> entry.replace("=", "[id=").concat("]"))
                .collect(Collectors.toList());
        return String.join("/", resources);
    }

    private static void logMessage(final String msg) {
        log.info(MSG_ID.concat(msg));
    }
}
