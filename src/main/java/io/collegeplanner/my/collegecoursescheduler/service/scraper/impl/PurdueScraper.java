package io.collegeplanner.my.collegecoursescheduler.service.scraper.impl;

import lombok.extern.log4j.Log4j2;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.PURDUE_UNIVERSITY;

@Log4j2
public class PurdueScraper extends EllucianScraper {


    public PurdueScraper(final String collegeName) {
        super(collegeName);
    }

    @Override
    public String getUniversityName() {
        return PURDUE_UNIVERSITY;
    }

    @Override
    public void setTermParameter(final String season, final String year) {
        final String seasonName;
        final String yearName;
        switch (season) {
            case "Fall":
                seasonName = "10";
                yearName = Integer.toString(Integer.getInteger(year) + 1); // Purdue is weird like that..
                break;
            case "Winter":
                seasonName = "20";
                yearName = year;
                break;
            case "Spring":
                seasonName = "30";
                yearName = year;
                break;
            case "Summer":
                seasonName = "40";
                yearName = year;
                break;
            default:
                seasonName = yearName = null;
                log.error("Error setting term");

        }
        final String termParameter = seasonName + yearName;
        setTermChosen(termParameter);
        // TODO: something to edit POST request parameter: "p_term" (DONE)
    }
}
