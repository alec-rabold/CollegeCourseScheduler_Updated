package io.collegeplanner.my.collegecoursescheduler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.collegeplanner.my.collegecoursescheduler.model.dto.ApiRequestDto;
import io.collegeplanner.my.collegecoursescheduler.model.dto.KinesisRecordDto;
import io.collegeplanner.my.collegecoursescheduler.model.dto.PermutationsJobResultsDto;
import io.collegeplanner.my.collegecoursescheduler.service.FirehoseStreamService;
import io.collegeplanner.my.collegecoursescheduler.service.ScheduleAnalyzerJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.FIREHOSE_USAGE_STREAM;

@Log4j2
@Controller
@RequestMapping(value = "/v1/university")
public class ScheduleAnalyzerApiController {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private FirehoseStreamService firehoseStreamService;

    @PostMapping(value = "/{collegeName}/results")
    @ResponseBody
    public PermutationsJobResultsDto runSchedulePermutations(@PathVariable final String collegeName,
                                                             final ApiRequestDto formParameters) throws JsonProcessingException {
        firehoseStreamService.addToStream(FIREHOSE_USAGE_STREAM, mapper.writeValueAsString(new KinesisRecordDto(collegeName, formParameters)));
        return ScheduleAnalyzerJob.runScheduleAnalyzerJob(collegeName, formParameters, null, null);
    }
}
