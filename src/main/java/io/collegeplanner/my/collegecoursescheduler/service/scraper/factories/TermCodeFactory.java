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
            case GEORGIA_TECH:
                return termCodeForGeorgiaTech(season, year);
            case GEORGE_MASON_UNIVERSITY:
                return termCodeForGeorgeMason(season, year);
            default:
                return errorSettingTerm(college, season, year);
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
                return errorSettingTerm(BROWN_UNIVERSITY, season, year);
        }
    }

    private static String termCodeForGeorgiaTech(final String season, final String year) {
        switch (season) {
            case SEASONS_FALL:
                return year + "08";
            case SEASONS_SUMMER:
                return year + "05";
            case SEASONS_WINTER:
            case SEASONS_SPRING:
                return year + "02";
            default:
                return errorSettingTerm(GEORGIA_TECH, season, year);
        }
    }

    private static String termCodeForGeorgeMason(final String season, final String year) {
        switch (season) {
            case SEASONS_FALL:
                return year + "70";
            case SEASONS_SUMMER:
                return year + "40";
            case SEASONS_WINTER:
            case SEASONS_SPRING:
                return year + "10";
            default:
                return errorSettingTerm(GEORGE_MASON_UNIVERSITY, season, year);
        }
    }

    private static String errorSettingTerm(final String college, final String season, final String year) {
        log.error("Error setting term for college: {}, season: {}, year: {}", college, season, year);
        return StringUtils.EMPTY;
    }

    private static String incrementString(final String year) {
        return Integer.toString(Integer.parseInt(year) + 1);
    }

    private static String decrementString(final String year) {
        return Integer.toString(Integer.parseInt(year) - 1);
    }
}
