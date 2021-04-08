package eu.coatrack.proxy.security;

/*-
 * #%L
 * coatrack-proxy
 * %%
 * Copyright (C) 2013 - 2020 Corizon | Institut für angewandte Systemtechnik Bremen GmbH (ATB)
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Checks if the API key token value sent by the service consumer is valid.
 *
 * @author gr-hovest, Christoph Baier
 */

@Service
public class ApiKeyAuthTokenVerifier implements AuthenticationManager {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthTokenVerifier.class);

    private final LocalApiKeyManager localApiKeyManager;
    private final ApiKeyFetcher apiKeyFetcher;
    private final ApiKeyVerifier apiKeyVerifier;

    public ApiKeyAuthTokenVerifier(LocalApiKeyManager localApiKeyManager,
                                   ApiKeyFetcher apiKeyFetcher, ApiKeyVerifier apiKeyVerifier) {
        this.localApiKeyManager = localApiKeyManager;
        this.apiKeyFetcher = apiKeyFetcher;
        this.apiKeyVerifier = apiKeyVerifier;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            log.debug("Verifying the authentication {}.", authentication.getName());
            return createApiKeyAuthToken(authentication);
        } catch (Exception e) {
            log.info("During the authentication process this exception occurred: ", e);
        }
        throw new SessionAuthenticationException("The authentication process failed.");
    }

    private Authentication createApiKeyAuthToken(Authentication authentication) {
        String apiKeyValue = getApiKeyValue(authentication);
        //TODO this is just a workaround for now: check for fixed API key to allow CoatRack admin access
        if (isAdminsKey(apiKeyValue)) {
            return createAdminsAuthToken(apiKeyValue);
        } else
            return verifyApiKeyAndIfAuthorizedCreateConsumerAuthToken(apiKeyValue);
    }

    private String getApiKeyValue(Authentication authentication) {
        log.debug("Getting API key value from authentication {}.", authentication.getName());
        Assert.notNull(authentication.getCredentials());
        Assert.isInstanceOf(String.class, authentication.getCredentials());
        String apiKeyValue = (String) authentication.getCredentials();
        Assert.hasText(apiKeyValue);
        return apiKeyValue;
    }

    private boolean isAdminsKey(String apiKeyValue) {
        log.debug("Checking if the API with the value {} is admins API key.", apiKeyValue);
        return apiKeyValue.equals(ApiKey.API_KEY_FOR_YGG_ADMIN_TO_ACCESS_PROXIES);
    }

    private Authentication createAdminsAuthToken(String apiKeyValue) {
        log.debug("Creating admins authentication token using API key with the value {}.", apiKeyValue);
        Set<SimpleGrantedAuthority> authoritiesGrantedToYggAdmin = new HashSet<>();
        authoritiesGrantedToYggAdmin.add(new SimpleGrantedAuthority(
                ServiceApiAccessRightsVoter.ACCESS_SERVICE_AUTHORITY_PREFIX + "refresh"));
        ApiKeyAuthToken apiKeyAuthToken = new ApiKeyAuthToken(apiKeyValue, authoritiesGrantedToYggAdmin);
        apiKeyAuthToken.setAuthenticated(true);
        log.info("Admin is successfully authenticated using the API key with the value {}.", apiKeyValue);
        return apiKeyAuthToken;
    }

    private Authentication verifyApiKeyAndIfAuthorizedCreateConsumerAuthToken(String apiKeyValue) {
        log.debug("Verifying the API with the value {} from consumer.", apiKeyValue);

        ApiKeyAndAuth apiKeyAndAuth = findApiKeyAndCheckAuthorization(apiKeyValue);

        if (apiKeyAndAuth.isAuthorized) {
            return createConsumersAuthToken(apiKeyAndAuth.apiKey);
        } else
            return null;
    }

    private ApiKeyAndAuth findApiKeyAndCheckAuthorization(String apiKeyValue) {
        ApiKeyAndAuth apiKeyAndAuth = new ApiKeyAndAuth();

        try {
            apiKeyAndAuth.apiKey = apiKeyFetcher.requestApiKeyFromAdmin(apiKeyValue);
            apiKeyAndAuth.isAuthorized = apiKeyVerifier.isApiKeyValid(apiKeyAndAuth.apiKey);
        } catch (ApiKeyFetchingException e) {
            log.info("Trying to verify consumers API key with the value {}, the connection to admin failed.",
                    apiKeyValue);
            apiKeyAndAuth.apiKey = localApiKeyManager.findApiKeyFromLocalApiKeyList(apiKeyValue);
            if(apiKeyAndAuth.apiKey != null)
                apiKeyAndAuth.isAuthorized = apiKeyVerifier.isApiKeyAuthorizedToAccessItsService(apiKeyValue);
        }

        return apiKeyAndAuth;
    }

    private ApiKeyAuthToken createConsumersAuthToken(ApiKey apiKey) {
        log.debug("Create consumers authentication token using API key with the value {}.", apiKey.getKeyValue());
        ServiceApi serviceApi = apiKey.getServiceApi();
        String uriIdentifier = serviceApi.getUriIdentifier();

        ApiKeyAuthToken apiKeyAuthToken = new ApiKeyAuthToken(apiKey.getKeyValue(), Collections.singleton(
                new SimpleGrantedAuthority(
                        ServiceApiAccessRightsVoter.ACCESS_SERVICE_AUTHORITY_PREFIX + uriIdentifier)));
        apiKeyAuthToken.setAuthenticated(true);
        return apiKeyAuthToken;
    }

    private class ApiKeyAndAuth {
        public ApiKey apiKey = null;
        public boolean isAuthorized = false;
    }
}
