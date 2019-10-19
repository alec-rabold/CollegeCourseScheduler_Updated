package io.collegeplanner.my.collegecoursescheduler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.web.servlet.view.UrlBasedViewResolver.REDIRECT_URL_PREFIX;

@Controller
public class RedirectController {

    @GetMapping("/redirect")
    public ModelAndView redirectUsingPrefix(final ModelMap model, final String redirectUrl) {
        return new ModelAndView(REDIRECT_URL_PREFIX + redirectUrl, model);
    }
}
