package io.collegeplanner.my.collegecoursescheduler.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.INDEX_VIEW;

@Log4j2
@Controller
public class ApplicationController {

    @RequestMapping(value = "/")
    public String index() {
        return INDEX_VIEW;
    }

}
