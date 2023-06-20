package eu.coatrack.admin.model.repository;

/*-
 * #%L
 * coatrack-admin
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

import java.util.List;
import eu.coatrack.api.ServiceApi;
import eu.coatrack.api.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 * @author perezdf
 */
@DataJpaTest
public class ServiceApiTest {

    @Autowired
    ServiceApiRepository serviceApiRepository;

    @Autowired
    UserRepository userRepository;

    @MockBean
    OAuth2AuthorizedClientService clientService;

    @Test
    public void save() {

        ServiceApi service = new ServiceApi();
        service.setName("something");
        service.setLocalUrl("https://www.google.com");
        service.setUriIdentifier("service");
        serviceApiRepository.save(service);

        User user = new User();
        user.setUsername("something");
        user.setFirstname("first");
        user.setLastname("Last");
        user.setCompany("company");
        user.setEmail("test@gmail.com");
        user.setInitialized(Boolean.FALSE);
        userRepository.save(user);

        service.setOwner(user);
        serviceApiRepository.save(service);
        
        List<ServiceApi> services = serviceApiRepository.findByOwnerUsername("something");
        
        assertNotNull(services);

    }

}
