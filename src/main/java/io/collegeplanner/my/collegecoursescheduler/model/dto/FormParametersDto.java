package io.collegeplanner.my.collegecoursescheduler.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

@Data
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
    private String[] chosenCourse;
    private String[] wantedProfessors;
    private String[] unwantedProfessors;
    private String[] excludedProfessors;
    private String[] unavailableTimeblockStart;
    private String[] unavailableTimeblockEnd;
    private String[] unavailableTimeblockDays;
    private boolean isMobileBrowser;
}
