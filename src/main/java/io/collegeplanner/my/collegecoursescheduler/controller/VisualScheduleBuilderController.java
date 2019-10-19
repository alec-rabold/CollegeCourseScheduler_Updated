package io.collegeplanner.my.collegecoursescheduler.controller;

import io.collegeplanner.my.collegecoursescheduler.model.dto.ApiRequestDto;
import io.collegeplanner.my.collegecoursescheduler.model.view.CollegeRepositoryData;
import io.collegeplanner.my.collegecoursescheduler.repository.RegistrationDataDao;
import io.collegeplanner.my.collegecoursescheduler.service.ScheduleAnalyzerJob;
import lombok.extern.log4j.Log4j2;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.*;

@Log4j2
@Controller
@RequestMapping(value = "/university")
public class VisualScheduleBuilderController {

    // TODO: https://docs.spring.io/spring/docs/3.2.x/spring-framework-reference/html/mvc.html#mvc-ann-async

    @Autowired
    private Jdbi jdbi;

    @RequestMapping(value = "/{collegeName}")
    public String userPreferencesForSchedule(@PathVariable final String collegeName,
                                             final ModelMap modelMap) {
        final CollegeRepositoryData registrationData = jdbi.onDemand(RegistrationDataDao.class)
                .getRegistrationDataIndexForCollege(collegeName);

        modelMap.addAttribute(SELECTED_COLLEGE_ATTRIBUTE_NAME, collegeName);
        modelMap.addAttribute(REGISTRATION_DATA_ATTRIBUTE_NAME, registrationData);

        return USER_PREFERENCES_FOR_SCHEDULE_VIEW;
    }

    @PostMapping(value = "/{collegeName}/results")
    public void runSchedulePermutations(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        @PathVariable final String collegeName,
                                        final ApiRequestDto formParameters) {
        ScheduleAnalyzerJob.runScheduleAnalyzerJob(collegeName, formParameters, request, response);
    }
}

