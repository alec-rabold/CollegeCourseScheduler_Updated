package io.collegeplanner.my.collegecoursescheduler.model.view;

import io.collegeplanner.my.collegecoursescheduler.model.dto.PermutationsJobResultsDto;
import io.collegeplanner.my.collegecoursescheduler.util.CourseColorMapper;
import lombok.Data;

@Data
public class ScheduleTableBuilderDto {
    private CourseColorMapper courseColorMapper;
    private PermutationsJobResultsDto schedulePermutationsJobResults;
}
