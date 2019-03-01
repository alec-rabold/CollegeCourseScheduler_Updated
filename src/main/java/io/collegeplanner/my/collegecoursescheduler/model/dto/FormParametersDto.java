package io.collegeplanner.my.collegecoursescheduler.model.dto;

import lombok.*;
import lombok.experimental.Wither;

import java.util.List;

@Getter
@Setter
@Wither
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormParametersDto {
    private String year;
    private String season;
    private String spreadPreference;
    private String doShowWaitlisted;
    private String doShowOnline;
    private String numClassesPerWeekPreference;
    private String problemsTextbox;
    private String suggestionsTextbox;
    private String[] chosenCourses;
    private String[] wantedProfessors;
    private String[] unwantedProfessors;
    private String[] excludedProfessors;
    private String[] unavailableBlockTimesStart;
    private String[] unavailableBlockTimesEnd;
    private List<String[]> daysForUnavailableTimeBlocks;
    private long[] unavTimesBitBlocks;
    private boolean isMobileBrowser;
    // Temporary until better solution
    private String[] mondaysChosen;
    private String[] tuesdaysChosen;
    private String[] wednesdaysChosen;
    private String[] thursdaysChosen;
    private String[] fridaysChosen;
}
