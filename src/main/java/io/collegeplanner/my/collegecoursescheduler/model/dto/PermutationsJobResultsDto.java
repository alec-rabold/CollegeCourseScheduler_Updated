package io.collegeplanner.my.collegecoursescheduler.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class PermutationsJobResultsDto implements Serializable {
    private int resultCode;
    private String completionTime;
    private int returnedSchedulesCount;
    private long validSchedulesCount;
    private long invalidSchedulesCount;
    private long performedPermutationsCount; // validSchedulesCount + invalidSchedulesCount
    private long theoreticalPermutationsCount; // for analyzing effectiveness of DP algorithm
    private Set<String> userCourseSelection;
    private Set<ScheduleDto> topRankedValidSchedules;
    private Set<String> coursesWithAllSectionsFull;
    private Set<String> coursesWithNoSectionsOffered;
}
