package io.collegeplanner.my.collegecoursescheduler.model.dto;

import lombok.Data;

import java.util.List;

// TODO: finish and implement for each permutation
@Data
public class SchedulePermutation {
    private List<CourseSectionDto> currentSchedule;
    private long[] combinedTimeBlocks;
    private boolean schedulesCollide;
}
