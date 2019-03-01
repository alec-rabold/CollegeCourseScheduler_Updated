package io.collegeplanner.my.collegecoursescheduler.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ScheduleDto implements Comparable<ScheduleDto> {
    private int scheduleRank;
    private final List<CourseSectionDto> courseSectionsInSchedule;
    @JsonIgnore private final int wantedProfessorsCount;
    @JsonIgnore private final int unwantedProfessorsCount;
    @JsonIgnore private final int courseTimeDensityMetric;
    @JsonIgnore private final int totalClassesPerWeekCount;
    @JsonIgnore private final long[] layoutIdentifierForDeduplication;
    @JsonIgnore private int rankingScore;

    // TODO: more logical ranking algorithm..
    public void setRankingScore(final UserOptionsDto userOptions) {
        this.rankingScore = userOptions.getScheduleSpreadPreference()
                * (2  * this.courseTimeDensityMetric)
                - (45 * this.wantedProfessorsCount)
                + (55 * this.unwantedProfessorsCount)
                + (this.totalClassesPerWeekCount * 30)
                * (userOptions.getDaysPerWeekPreference());
    }

    @Override
    public int compareTo(final ScheduleDto otherSchedule) {
        return ((Integer)otherSchedule.rankingScore).compareTo(this.rankingScore);
    }
}
