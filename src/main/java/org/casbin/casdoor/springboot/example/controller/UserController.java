// Copyright 2021 The casbin Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.casbin.casdoor.springboot.example.controller;

import org.casbin.casdoor.config.CasdoorConfiguration;
import org.casbin.casdoor.entity.CasdoorUser;
import org.casbin.casdoor.service.CasdoorUserService;
import org.casbin.casdoor.util.http.CasdoorResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Yixiang Zhao (@seriouszyx)
 */
@RestController
public class UserController {

    @Resource
    private CasdoorConfiguration casdoorConfiguration;

    @Resource
    private CasdoorUserService casdoorUserService;

    @RequestMapping("getUser")
    public CasdoorUser getUser(String name) throws Exception {
        return casdoorUserService.getUser(name);
    }

    @RequestMapping("getUsers")
    public List<CasdoorUser> getUsers() throws Exception {
        return Arrays.asList(casdoorUserService.getUsers());
    }

    @RequestMapping("addUser")
    public CasdoorResponse addUser(String name) throws IOException {
        CasdoorUser user = new CasdoorUser();
        user.setOwner(casdoorConfiguration.getOrganizationName());
        user.setName(name);
        user.setDisplayName(name);
        return casdoorUserService.addUser(user);
    }

    @RequestMapping("updateUser")
    public CasdoorResponse updateUser(CasdoorUser user) throws IOException {
        return casdoorUserService.updateUser(user);
    }

    @RequestMapping("deleteUser")
    public CasdoorResponse deleteUser(String name) throws Exception {
        CasdoorUser user = casdoorUserService.getUser(name);
        return casdoorUserService.deleteUser(user);
    }
}
