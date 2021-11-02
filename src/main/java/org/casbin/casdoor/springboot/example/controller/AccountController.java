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

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.casbin.casdoor.config.CasdoorConfig;
import org.casbin.casdoor.entity.CasdoorUser;
import org.casbin.casdoor.service.CasdoorAuthService;
import org.casbin.casdoor.springboot.example.config.CasdoorSdkConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

/**
 * @author Yixiang Zhao (@seriouszyx)
 */
@Controller
public class AccountController {

    @Resource
    private CasdoorSdkConfig casdoorSdkConfig;

    @RequestMapping("toLogin")
    public String toLogin() {
        CasdoorConfig casdoorConfig = casdoorSdkConfig.getCasdoorConfig();
        String targetUrl = String.format("%s/login/oauth/authorize?client_id=%s&response_type=code&redirect_uri=%s&scope=read&state=%s",
                "http://localhost:7001", casdoorConfig.getClientId(),
                "http://localhost:8080/login", casdoorConfig.getApplicationName());
        return "redirect:" + targetUrl;
    }

    @RequestMapping("login")
    public String login(String code, String state, HttpServletRequest request) {
        CasdoorAuthService casdoorAuthService = new CasdoorAuthService(casdoorSdkConfig.getCasdoorConfig());
        String token = "";
        CasdoorUser user = null;
        try {
            token = casdoorAuthService.getOAuthToken(code, state);
            user = casdoorAuthService.parseJwtToken(token);
        } catch (OAuthSystemException | OAuthProblemException | ParseException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        HttpSession session = request.getSession();
        session.setAttribute("casdoorUser", user);
        return "redirect:/";
    }
}
