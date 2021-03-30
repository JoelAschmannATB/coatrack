package eu.coatrack.proxy.security;

/*-
 * #%L
 * coatrack-proxy
 * %%
 * Copyright (C) 2013 - 2021 Corizon | Institut für angewandte Systemtechnik Bremen GmbH (ATB)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import eu.coatrack.api.ApiKey;
import eu.coatrack.api.ServiceApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Offers communication services to the Coatrack admin server to receive data
 * required by the gateway for key verification.
 *
 * @author Christoph Baier
 */

@Service("adminCommunicator")
public class AdminCommunicator {

    private static final Logger log = LoggerFactory.getLogger(eu.coatrack.proxy.security.AdminCommunicator.class);

    private final RestTemplate restTemplate;
    private final AdminCommunicatorConfiguration config;

    public AdminCommunicator(RestTemplate restTemplate, AdminCommunicatorConfiguration config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    public ApiKey[] requestLatestApiKeyListFromAdmin() throws RestClientException {
        log.debug("Requesting latest API key list from CoatRack admin.");
        ResponseEntity<ApiKey[]> responseEntity = restTemplate.getForEntity(config.getApiKeyListRequestUrl(),
                ApiKey[].class, config.getGatewayId());

        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            log.info("Successfully requested latest API key list from CoatRack admin.");
            return responseEntity.getBody();
        } else {
            log.warn("Request of latest API key list from CoatRack admin failed. Received http status {} from " +
                    "CoatRack admin.", responseEntity.getStatusCode());
            return null;
        }
    }

    public ApiKey requestApiKeyFromAdmin(String apiKeyValue) throws RestClientException {
        log.debug("Requesting API key with the value {} from CoatRack admin.", apiKeyValue);
        ResponseEntity<ApiKey> responseEntity = restTemplate.getForEntity(
                config.getApiKeyRequestUrlWithoutApiKeyValue() + apiKeyValue, ApiKey.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            log.info("The API key with the value {} was found by CoatRack admin.", apiKeyValue);
            return responseEntity.getBody();
        } else {
            log.info("The API key with the value {} was not found by CoatRack admin.", apiKeyValue);
            return null;
        }
    }

    public ServiceApi requestServiceApiFromAdmin(String apiKeyValue) throws RestClientException {
        log.debug("Requesting service from CoatRack admin using the API with the value {}.", apiKeyValue);
        ResponseEntity<ServiceApi> responseEntity = restTemplate.getForEntity(
                config.getServiceApiRequestUrlWithoutApiKeyValue() + apiKeyValue, ServiceApi.class);
        return responseEntity.getBody();
    }
}
