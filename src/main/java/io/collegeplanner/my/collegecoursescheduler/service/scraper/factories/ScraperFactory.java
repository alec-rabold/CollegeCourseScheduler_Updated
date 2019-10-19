package io.collegeplanner.my.collegecoursescheduler.service.scraper.factories;

import io.collegeplanner.my.collegecoursescheduler.model.dto.ApiRequestDto;
import io.collegeplanner.my.collegecoursescheduler.model.dto.UserOptionsDto;
import io.collegeplanner.my.collegecoursescheduler.service.scraper.GenericScraper;
import io.collegeplanner.my.collegecoursescheduler.service.scraper.impl.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.*;

@Log4j2
public final class ScraperFactory {

    public static GenericScraper getScraperForCollege(final String collegeName,
                                                      final ApiRequestDto formParameters) {
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
                scraper = new EllucianScraper(collegeName);
                break;
        }

        return buildScraperFromParameters(formParameters, scraper);
    }

   // TODO: legacy; change parameters so they don't need additional logic/processing
    private static GenericScraper buildScraperFromParameters(final ApiRequestDto params, final GenericScraper scraper) {
        scraper.setUserOptions(
                UserOptionsDto.builder()
                        .chosenCourseNames(Arrays.asList(params.getChosenCourses()))
                        .wantedProfessors(params.getFavoredProfessors())
                        .unwantedProfessors(params.getDisfavoredProfessors())
                        .excludeProfessors(params.getExcludedProfessors())
                        .relaxedVsCompactPreference(params.getRelaxedVsCompactPreference().equals("relaxed") ? -1 : 1)
                        .showWaitlistedClasses(Boolean.valueOf(params.getIncludeWaitlistedCourses()))
                        .unavailableTimesBitBlocks(constructUnavailableTimesBitsBlocks(params.getUnavailableTimeblockStart(),
                                params.getUnavailableTimeblockEnd(), params.getUnavailableTimeblockDays()))
                        .daysPerWeekPreference(params.getShorterVsFewerClassesPreference().equals("more") ? -1 : 1)
                        .showOnlineClasses(Boolean.valueOf(params.getIncludeOnlineCourses()))
                        .instantRandomSchedules(Boolean.valueOf(params.getInstantRandomSchedules()))
                        .build());
        scraper.setTermParameter(params.getSeason(), params.getYear());
        return scraper;
    }

    private static long[] constructUnavailableTimesBitsBlocks(final String[] unavailableTimeblocksStart,
                                                              final String[] unavailableTimeblocksEnd,
                                                              final String[] unavailableTimeblocksDays) {
        final long[] unavailableTimesBitBlocks = new long[5];
        final int numTimeblocks = unavailableTimeblocksStart.length;

        for(int currentTimeblock = 0; currentTimeblock < numTimeblocks; currentTimeblock++) {
            for(int dayOfWeek = 0; dayOfWeek < NUM_OF_WEEKDAYS; dayOfWeek++) {
                final String currentDay = WEEKDAY_ABBREVS[dayOfWeek];
                if(ArrayUtils.contains(unavailableTimeblocksDays, currentDay)
                        && (unavailableTimeblocksStart[currentTimeblock].length() > 4)
                        && (unavailableTimeblocksEnd[currentTimeblock].length() > 4)
                ){
                    final String timeblock = unavailableTimeblocksStart[currentTimeblock].substring(0, 5)
                            + "-" + unavailableTimeblocksEnd[currentTimeblock].substring(0, 5);
                    unavailableTimesBitBlocks[dayOfWeek] = unavailableTimesBitBlocks[dayOfWeek]
                            | (GenericScraper.convertTimeblockToStringOfBits(timeblock));
                }
            }
        }
        return unavailableTimesBitBlocks;
    }
}
