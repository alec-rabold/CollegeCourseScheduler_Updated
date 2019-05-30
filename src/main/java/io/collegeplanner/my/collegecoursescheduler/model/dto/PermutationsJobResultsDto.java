package io.collegeplanner.my.collegecoursescheduler.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class PermutationsJobResultsDto implements Serializable {
    private int resultCode;
    private int numSchedulesReturned;
    private long numTotalValidSchedules;
    private long numTotalInvalidSchedules;
    private long numPermutationsPerformed; // numTotalValidSchedules + numTotalInvalidSchedules
    private long numTheoreticalPermutations; // for analyzing effectiveness of DP algorithm
    private String timeToRetrieveCourseData;
    private String timeToCompletePermutations;
    // private Set<String> courseNamesSelected;
    private Set<String> coursesWithAllSectionsWaitlisted;
    private Set<String> coursesWithNoSectionsOfferedForTerm;
    private Set<ScheduleDto> rankedValidSchedules;
}
