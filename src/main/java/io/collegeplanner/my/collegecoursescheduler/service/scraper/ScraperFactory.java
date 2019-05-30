package io.collegeplanner.my.collegecoursescheduler.service.scraper;

import com.google.common.collect.ImmutableList;
import io.collegeplanner.my.collegecoursescheduler.model.dto.FormParametersDto;
import io.collegeplanner.my.collegecoursescheduler.model.dto.UserOptionsDto;
import io.collegeplanner.my.collegecoursescheduler.service.scraper.impl.BerkeleyScraper;
import io.collegeplanner.my.collegecoursescheduler.service.scraper.impl.SdsuScraper;
import io.collegeplanner.my.collegecoursescheduler.service.scraper.impl.UcsbScraper;
import io.collegeplanner.my.collegecoursescheduler.service.scraper.impl.UwScraper;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.*;

@Log4j2
public final class ScraperFactory {

    public static GenericScraper getScraperForCollege(final String collegeName,
                                                      final FormParametersDto formParameters) {
        final GenericScraper scraper;

        switch(collegeName) {
            case SAN_DIEGO_STATE_UNIVERSITY:
                scraper = new SdsuScraper();
                break;
            case UNIVERSITY_OF_CALIFORNIA_SANTA_BARBARA:
                scraper = new UcsbScraper();
                break;
            case UNIVERSITY_OF_CALIFORNIA_BERKELEY:
                scraper = new BerkeleyScraper();
                break;
            case UNIVERSITY_OF_WASHINGTON:
                scraper = new UwScraper();
                break;
            default:
                log.error("Error creating scraper for college: {}", collegeName);
                return null;
        }
        // TODO: make sure this works; change implementation
        //scraper.setOutWriter(servletResponse.getWriter());
        // scraper.setServerPath(servletRequest.getServletPath());
        // scraper.setServerPath(getServletContext().getRealPath(File.separator));
        formParameters.setDaysForUnavailableTimeBlocks(
                ImmutableList.of(
                        formParameters.getMondaysChosen(),
                        formParameters.getTuesdaysChosen(),
                        formParameters.getWednesdaysChosen(),
                        formParameters.getThursdaysChosen(),
                        formParameters.getFridaysChosen()
                )
        );
        formParameters.setUnavTimesBitBlocks(constructUnavailableTimesBitsBlockFromParameters(formParameters));
        return buildScraperFromParameters(formParameters, scraper);
    }



   /** private static FormParametersDto buildParametersFromRequest(final HttpServletRequest request) {
        final FormParametersDto params = FormParametersDto.builder()
                .suggestion(request.getParameter("suggestions"))
                .problems(request.getParameter("problemsTextbox"))
                .chosenCourses(request.getParameterValues("needed-classes"))
                .wantedProfessors(request.getParameterValues("wanted-professors"))
                .unwantedProfessors(request.getParameterValues("unwanted-professors"))
                .season(request.getParameter("season"))
                .year(request.getParameter("year"))
                .spreadPreference(request.getParameter("schedule-breaks"))
                .isMobile(request.getParameter("isMobileBrowser").equals("true"))
                .excludeProfessors(request.getParameterValues("excluded-professors"))
                .waitlistOption(request.getParameter("waitlist-option"))
                .onlineOption(request.getParameter("online-option"))
                .numDaysOption(request.getParameter("numDays-option"))
                .setOfDays(ImmutableList.of(
                        request.getParameterValues(MONDAYS_ARRAY),
                        request.getParameterValues(TUESDAY_ARRAY),
                        request.getParameterValues(WEDNESDAY_ARRAY),
                        request.getParameterValues(THURSDAY_ARRAY),
                        request.getParameterValues(FRIDAY_ARRAY)
                ))
                .unavStartTimes(request.getParameterValues(UNAVAILABLE_START_TIMES_ARRAY))
                .unavEndTimes(request.getParameterValues(UNAVAILABLE_END_TIMES_ARRAY))
                .build();
        params.setUnavTimesBitBlocks(constructUnavailableTimesBitsBlockFromParameters(params));
        return params;
    }
    */

   // TODO: legacy; change parameters so they don't need additional logic/processing
    private static GenericScraper buildScraperFromParameters(final FormParametersDto params, final GenericScraper scraper) {
        scraper.setUserOptions(
                UserOptionsDto.builder()
                        .chosenCourseNames(Arrays.asList(params.getChosenCourses()))
                        .wantedProfessors(params.getWantedProfessors())
                        .unwantedProfessors(params.getUnwantedProfessors())
                        .excludeProfessors(params.getExcludedProfessors())
                        .scheduleSpreadPreference(params.getSpreadPreference().equals("relaxed") ? -1 : 1)
                        .showWaitlistedClasses(Boolean.valueOf(params.getDoShowWaitlisted()))
                        .unavailableTimesBitBlocks(params.getUnavTimesBitBlocks())
                        .daysPerWeekPreference(params.getNumClassesPerWeekPreference().equals("more") ? -1 : 1)
                        .showOnlineClasses(Boolean.valueOf(params.getDoShowOnline()))
                        .build());
        scraper.setMobileBrowser(params.isMobileBrowser());
        scraper.setTermParameter(params.getSeason(), params.getYear());
        return scraper;
    }

    private static long[] constructUnavailableTimesBitsBlockFromParameters(final FormParametersDto params) {
        final long[] unavailableTimesBitBlocks = new long[5];
        final int numUnavailableTimes = params.getDaysForUnavailableTimeBlocks().get(1).length;

        for(int currentTimeblock = 0; currentTimeblock < numUnavailableTimes; currentTimeblock++) {
            for(int dayOfWeek = 0; dayOfWeek < NUM_OF_WEEKDAYS; dayOfWeek++) {
                final String dayOfWeekForCurrentTimeblock = params.getDaysForUnavailableTimeBlocks().get(dayOfWeek)[currentTimeblock];
                if(dayOfWeekForCurrentTimeblock.equals(SELECTED_BY_USER)
                        && (params.getUnavailableBlockTimesStart()[currentTimeblock].length() > 4)
                        && (params.getUnavailableBlockTimesEnd()[currentTimeblock].length() > 4)
                ){
                    final String timeblock = params.getUnavailableBlockTimesStart()[currentTimeblock].substring(0, 5)
                            + "-" + params.getUnavailableBlockTimesEnd()[currentTimeblock].substring(0, 5);
                    unavailableTimesBitBlocks[dayOfWeek] =
                            unavailableTimesBitBlocks[dayOfWeek] | (GenericScraper.convertTimeblockToStringOfBits(timeblock));
                }
            }
        }
        return unavailableTimesBitBlocks;
    }
}
