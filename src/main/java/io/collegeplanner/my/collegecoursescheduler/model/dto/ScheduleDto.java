package io.collegeplanner.my.collegecoursescheduler.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class ScheduleDto implements Comparable<ScheduleDto> {
    private int scheduleRank;
    private final List<CourseSectionDto> courseSectionsInSchedule;
    @JsonIgnore private final int wantedProfessorsCount;
    @JsonIgnore private final int unwantedProfessorsCount;
    @JsonIgnore private final int courseTimeDensityMetric;
    @JsonIgnore private final int totalClassesPerWeekCount;
    @JsonIgnore private final long[] layoutIdentifierForDeduplication;
//    @JsonIgnore private int rankingScore;
    @JsonIgnore private final UserOptionsDto userOptions;

    // TODO: more logical ranking algorithm..
    public int scoreSchedule() {
        return this.userOptions.getRelaxedVsCompactPreference()
                * (2  * this.courseTimeDensityMetric)
                - (45 * this.wantedProfessorsCount)
                + (55 * this.unwantedProfessorsCount)
                + (this.totalClassesPerWeekCount * 30)
                * (userOptions.getDaysPerWeekPreference());
    }

    @Override
    public int compareTo(final ScheduleDto otherSchedule) {
        final int similarity = ((Integer)otherSchedule.scoreSchedule()).compareTo(this.scoreSchedule());
        return similarity == 0 ? 1 : similarity;
    }
}
