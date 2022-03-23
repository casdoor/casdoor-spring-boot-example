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

import org.casbin.casdoor.entity.CasdoorUser;
import org.casbin.casdoor.exception.CasdoorAuthException;
import org.casbin.casdoor.service.CasdoorAuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Yixiang Zhao (@seriouszyx)
 */
@Controller
public class AccountController {

    @Resource
    private CasdoorAuthService casdoorAuthService;

    @RequestMapping("toLogin")
    public String toLogin() {
        return "toLogin";
    }

    @RequestMapping("login")
    public String login() {
        return "redirect:" + casdoorAuthService.getSigninUrl("http://localhost:8080/callback");
    }

    @RequestMapping("callback")
    public String callback(String code, String state, HttpServletRequest request) {
        String token = "";
        CasdoorUser user = null;
        try {
            token = casdoorAuthService.getOAuthToken(code, state);
            user = casdoorAuthService.parseJwtToken(token);
        } catch (CasdoorAuthException e) {
            e.printStackTrace();
        }
        HttpSession session = request.getSession();
        session.setAttribute("casdoorUser", user);
        return "redirect:/";
    }
}
