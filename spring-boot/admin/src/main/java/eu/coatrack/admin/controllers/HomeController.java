package eu.coatrack.admin.controllers;

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

import eu.coatrack.admin.components.WebUI;
import eu.coatrack.admin.model.repository.CoverRepository;
import eu.coatrack.admin.model.repository.ErrorRepository;
import eu.coatrack.admin.service.HomeService;
import eu.coatrack.api.ServiceCover;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Timon Veenstra <tveenstra@bebr.nl>
 */
@CrossOrigin(origins = "*")
@Controller
public class HomeController {

    @Autowired
    private HomeService homeService;

    @RequestMapping("/")
    public String home(Model model) {
        return homeService.home(model);
    }

    @PostMapping(value = "/errors", produces = "application/json")
    @ResponseBody
    public eu.coatrack.api.Error saveErrors(eu.coatrack.api.Error error) {
        return homeService.saveErrors(error);
    }

    @RequestMapping(value = "/covers", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Iterable<ServiceCover> serviceCoversListPageRest(){
        return homeService.serviceCoversListPageRest();
    }

}
