package io.collegeplanner.my.collegecoursescheduler.service.scraper.factories;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.*;

@Log4j2
public class TermCodeFactory {
    public static String getCodeForCollege(final String college, final String season, final String year) {
        switch(college) {
            case BROWN_UNIVERSITY:
                return termCodeForBrown(season, year);
            default:
                return termCodeForDefault(season, year);

        }


    }

    private static String termCodeForBrown(final String season, final String year) {
        switch (season) {
            case SEASONS_FALL:
                return year + "10";
            case SEASONS_WINTER:
                return decrementString(year) + "15";
            case SEASONS_SPRING:
                return decrementString(year) + "20";
            case SEASONS_SUMMER:
                return year + "00";
            default:
                log.error("Error setting term");
                return "";
        }
    }

    private static String termCodeForDefault(final String season, final String year) {
        final String res = "";
        return res;
    }

    private static String incrementString(final String year) {
        return Integer.toString(Integer.parseInt(year) + 1);
    }

    private static String decrementString(final String year) {
        return Integer.toString(Integer.parseInt(year) - 1);
    }
}
