package io.collegeplanner.my.collegecoursescheduler.repository;

import io.collegeplanner.my.collegecoursescheduler.repository.scrapers.ellucian.EllucianDataScraper;
import io.collegeplanner.my.collegecoursescheduler.repository.scrapers.ellucian.impl.CourseScraper;
import io.collegeplanner.my.collegecoursescheduler.repository.scrapers.ellucian.impl.ProfessorScraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Set;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.*;

@Log4j2
@RequiredArgsConstructor
public class EllucianDataScraperJob {

    public static void main(String[] args) throws IOException {
        scrapeAllDataForCollege(GEORGIA_TECH);
        scrapeAllDataForCollege(PURDUE_UNIVERSITY);
        scrapeAllDataForCollege(GEORGE_MASON_UNIVERSITY);
    }

    public static void scrapeAllDataForAllColleges() {
        for (final String college : SUPPORTED_ELLUCIAN_COLLEGES) {
            new Thread (() -> {
                try {
                    scrapeAllDataForCollege(college);
                } catch (final IOException e) {
                    log.error(e);
                }
            }).start();
        }
    }

    private static void scrapeAllDataForCollege(final String college) throws IOException {
        final ProfessorScraper instructorScraper = new ProfessorScraper();
        // final SubjectScraper subjectScraper = new SubjectScraper();
        final CourseScraper courseScraper = new CourseScraper();

        final String baseDataPage = ELLUCIAN_UNIVERSITIES_SS_DATA_PAGES.get(college);
        final Set<String> termIds = EllucianDataScraper.getMostRecentTermIds(
                ELLUCIAN_SS_DATA_DEFAULT_NUM_TERMS, baseDataPage);

        new Thread (() -> {
            try {
                instructorScraper.scrapeAndPersistDataForCollege(college, termIds);
            } catch (final Exception e) {
                log.error(e);
            }
        }).start();

        new Thread (() -> {
            try {
                courseScraper.scrapeAndPersistDataForCollege(college, termIds);
            } catch (final Exception e) {
                log.error(e);
            }
        }).start();

        log.info("Finished scraping data for college: {}", college);
    }
}
