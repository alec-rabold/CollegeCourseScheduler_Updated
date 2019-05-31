package io.collegeplanner.my.collegecoursescheduler.repository.scrapers.ellucian.impl;

import io.collegeplanner.my.collegecoursescheduler.repository.RegistrationDataDao;
import io.collegeplanner.my.collegecoursescheduler.repository.scrapers.ellucian.EllucianDataScraper;
import io.collegeplanner.my.collegecoursescheduler.util.ChainedParserMetadata;
import io.collegeplanner.my.collegecoursescheduler.util.DatabaseUtils;
import io.collegeplanner.my.collegecoursescheduler.util.ScraperUtils;
import lombok.extern.log4j.Log4j2;
import org.jdbi.v3.core.Jdbi;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.*;

@Log4j2
public class CourseScraper extends EllucianDataScraper {
    @Override
    public void scrapeAndPersistDataForCollege(final String college, final Set<String> termIds) throws IOException {
        final String baseDataPage = ELLUCIAN_UNIVERSITIES_SS_DATA_PAGES.get(college);
//        final Set<String> subjectCodes = SubjectScraper.getSubjects(baseDataPage, termIds).keySet();
        final Map<String, String> coursesMap = getCourses(baseDataPage, termIds);

        coursesMap.forEach((key, value) -> System.out.println(key + ", " + value));

        final Jdbi jdbi = DatabaseUtils.getDatabaseConnection();
        final String tableName = COURSES_TABLE_PREFIX + college;
        jdbi.onDemand(RegistrationDataDao.class).createCoursesTableIfNotExists(tableName);
        jdbi.onDemand(RegistrationDataDao.class).updateCoursesTableBulk(tableName,
                coursesMap.keySet(), coursesMap.values());


        // ELLUCIAN_REGISTRATION_COURSES_RELATIVE_PATH
    }

    private static Map<String, String> getCourses(final String baseDataPage, final Set<String> termIds) throws IOException {
        final Map<String, String> coursesMap = new TreeMap<>(); // {CourseID --> Course Title}

        nextTermId:
        for(final String term : termIds) {
            final Set<String> subjectCodes = SubjectScraper.getSubjects(baseDataPage, term).keySet();
            final String dataPage = baseDataPage + ELLUCIAN_REGISTRATION_COURSES_RELATIVE_PATH;
//            final String unencodedFormData = ELLUCIAN_SS_COURSE_DATA_FORM_DATA + EllucianDataScraper.formatTermParameters(termIds, true) + EllucianDataScraper.formatSubjectParameters(subjects);
            final String unencodedFormData = ELLUCIAN_SS_COURSE_DATA_FORM_DATA + term + EllucianDataScraper.formatSubjectParameters(subjectCodes);
            final BufferedReader in = getReaderForPageWithParams(dataPage, unencodedFormData,
                    baseDataPage + ELLUCIAN_REGISTRATION_TERM_DATA_RELATIVE_PATH);

            // TODO: clean up
            try {
                String inputLine = in.readLine();
                if (inputLine == null) {
                    log.error("Unreachable datapage");
                    // continue nextTermId;
                }
                parsingJob:
                while (inputLine != null) {
//                while ((!inputLine.contains(ELLUCIAN_SS_COURSE_DATA_COURSE_MARKER_I)
//                        && (!inputLine.contains(ELLUCIAN_SS_COURSE_DATA_COURSE_MARKER_II)))
//                        || inputLine.contains(ELLUCIAN_SS_TERM_DATA_FALSE_MARKER)) {
                    while (!inputLine.contains(ELLUCIAN_SS_COURSE_DATA_COURSE_MARKER_I)
                            || inputLine.contains(ELLUCIAN_SS_TERM_DATA_FALSE_MARKER)) {
                        inputLine = in.readLine();
                        if (inputLine == null) {
                            log.error("End of page reached for baseDataPage {} TermID: {}", baseDataPage, term);
                            break parsingJob;
                        }
                    }

                    // This helps when parsing multiple pieces of data that occur in succession on one line
                    // by keeping track of the end index of the previously parsed piece of information
                    //   ex. "Introduction/Computer Science - 46665 - COSC 102 - 007"
                    ChainedParserMetadata chainedParser;

                    // starting chainedParser here
                    /** Course Title */
                    chainedParser = ScraperUtils.parseData(ELLUCIAN_SS_COURSE_DATA_COURSE_TITLE_DATA_MARKER_START,
                            ELLUCIAN_SS_COURSE_DATA_COURSE_TITLE_DATA_MARKER_START.length()
                                    + SCHEDULE_NUM_LENGTH + TWO_UNITS,
                            ELLUCIAN_SS_COURSE_DATA_COURSE_TITLE_DATA_MARKER_END, inputLine);
                    final String courseTitle = chainedParser.getData();

                    /** Schedule Number (unused here) */
                    chainedParser = ScraperUtils.parseData(chainedParser.getEndIndex() + TWO_UNITS,
                            ELLUCIAN_SCHEDULE_NUMBER_MARKER_END, inputLine);

                    /** CourseID */
                    chainedParser = ScraperUtils.parseData(chainedParser.getEndIndex() + TWO_UNITS,
                            ELLUCIAN_SS_COURSE_DATA_COURSE_ID_DATA_MARKER_END, inputLine);
                    final String courseId = chainedParser.getData().replace(" ", "-");

                    coursesMap.put(courseId, courseTitle);
//                    log.debug("CourseID: {} \t Title: {}", courseId, courseTitle);

                    inputLine = in.readLine();
                }
            } catch (final Exception e) {
                log.error(e);
                System.out.println("Continuing");
                continue nextTermId;
            } finally {
                in.close();
            }
        }

        return coursesMap;
    }
}
