package io.collegeplanner.my.collegecoursescheduler.service;

import io.collegeplanner.my.collegecoursescheduler.model.dto.PermutationsJobResultsDto;
import io.collegeplanner.my.collegecoursescheduler.model.dto.FormParametersDto;
import io.collegeplanner.my.collegecoursescheduler.service.scraper.factories.ScraperFactory;
import io.collegeplanner.my.collegecoursescheduler.service.scraper.GenericScraper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
@Getter
@Setter
public final class ScheduleAnalyzerJob {

    // TODO: Servlet Context shouldn't be available outside web module/layer

    public static PermutationsJobResultsDto runScheduleAnalyzerJob(final String collegeName,
                                                                   final FormParametersDto formParameters,
                                                                   final HttpServletRequest request,
                                                                   final HttpServletResponse response)  {
        final GenericScraper scraper = ScraperFactory.getScraperForCollege(collegeName, formParameters);
        return analyzeSchedulePermutations(scraper, request, response);
    }

    private static PermutationsJobResultsDto analyzeSchedulePermutations(final GenericScraper scraper,
                                                                         final HttpServletRequest request,
                                                                         final HttpServletResponse response) {
        try {
            scraper.iterateInput(scraper.getUserOptions().getChosenCourseNames());
        } catch (final Exception e) {
            log.error("Error iterating through user's course selections", e);
        }

        final PermutationsJobResultsDto results = scraper.doPermutationsAndGetApiResults(request, response);

        return results;
    }

    /**
    private void pushUsageDataToDatabase(final HttpServletRequest request, final FormParametersDto params) {
        DatabaseConnection.writeToDatalog(
                request, scraper.getElapsedTime(), params.getSuggestionsTextbox(),
                params.getProblemsTextbox(), params.getChosenCourses(), scraper.getNumPermutations(), scraper.isTimedOut()
        );
    }
     */
}
