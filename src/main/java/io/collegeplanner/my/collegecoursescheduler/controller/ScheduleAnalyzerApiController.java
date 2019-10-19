package io.collegeplanner.my.collegecoursescheduler.controller;

import io.collegeplanner.my.collegecoursescheduler.model.dto.ApiRequestDto;
import io.collegeplanner.my.collegecoursescheduler.model.dto.PermutationsJobResultsDto;
import io.collegeplanner.my.collegecoursescheduler.service.ScheduleAnalyzerJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Log4j2
@Controller
@RequestMapping(value = "/v1/university")
public class ScheduleAnalyzerApiController {

    @PostMapping(value = "/{collegeName}/results")
    @ResponseBody
    public PermutationsJobResultsDto runSchedulePermutations(@PathVariable final String collegeName,
                                                             final ApiRequestDto formParameters) {
        return ScheduleAnalyzerJob.runScheduleAnalyzerJob(collegeName, formParameters, null, null);
    }
}
