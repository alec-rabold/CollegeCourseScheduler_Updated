package io.collegeplanner.my.collegecoursescheduler.repository.scrapers.ellucian.impl;

import com.google.common.collect.ImmutableSet;
import io.collegeplanner.my.collegecoursescheduler.repository.RegistrationDataDao;
import io.collegeplanner.my.collegecoursescheduler.repository.scrapers.ellucian.EllucianDataScraper;
import io.collegeplanner.my.collegecoursescheduler.util.ScraperUtils;
import lombok.extern.log4j.Log4j2;
import org.jdbi.v3.core.Jdbi;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.*;

@Log4j2
public class SubjectScraper extends EllucianDataScraper {

    // TODO: does this really need to be persisted?
    @Override
    public void scrapeAndPersistDataForCollege(final String college, final Set<String> termIds) throws IOException {
        final String baseDataPage = ELLUCIAN_UNIVERSITIES_SS_DATA_PAGES.get(college);
        final Map<String, String> subjectsMap = getSubjects(baseDataPage, termIds);

        final Jdbi jdbi = EllucianDataScraper.getDatabaseConnection();
        final String tableName = SUBJECTS_TABLE_PREFIX + college;
        jdbi.onDemand(RegistrationDataDao.class).createSubjectsTableIfNotExists(tableName);
        jdbi.onDemand(RegistrationDataDao.class).updateSubjectsTableBulk(tableName,
                subjectsMap.keySet(), subjectsMap.values());

    }

//    private Map<String, String> getSubjects(final String baseDataPage, final Set<String> termIds) {
//        // The abbreviations are used as form parameters by Ellucian's "Banner" system
//        final Map<String, String> subjectsMap = new TreeMap<>(); // ex. "CS" not "Computer Science"
//        nextTermId:
//        for(final String term : termIds) {
//            final String dataPage = baseDataPage + ELLUCIAN_REGISTRATION_TERM_DATA_RELATIVE_PATH;
//            final String unencodedFormData = ELLUCIAN_SS_TERM_DATA_FORM_DATA + term;
//            final BufferedReader in = getReaderForPageWithParams(dataPage, unencodedFormData,
//                    baseDataPage + ELLUCIAN_DYNAMIC_SCHEDULE_RELATIVE_PATH);
//            // TODO: clean up
//            try {
//                String inputLine = in.readLine();
//                if (inputLine == null) {
//                    log.error("Unreachable datapage");
//                    return null;
//                }
//                while (!inputLine.contains(ELLUCIAN_SS_TERM_DATA_SUBJECT_MARKER_START)
//                        || inputLine.contains(ELLUCIAN_SS_TERM_DATA_FALSE_MARKER)) {
//                    inputLine = in.readLine();
//                    if(inputLine == null) {
//                        log.debug("No subjects found specified term. End of page reached for termId: {}, basePage: {}", term, baseDataPage);
//                        continue nextTermId;
//                    }
//                }
//                inputLine = in.readLine();
//
//                nextSubject:
//                while (!inputLine.contains(ELLUCIAN_SS_TERM_DATA_SUBJECT_MARKER_END)) {
//                    final String subjAbbr = ScraperUtils.parseData(ELLUCIAN_SS_TERM_DATA_SUBJECT_ABBR_DATA_MARKER_START,
//                            ELLUCIAN_SS_TERM_DATA_SUBJECT_ABBR_DATA_MARKER_START.length(),
//                            ELLUCIAN_SS_TERM_DATA_SUBJECT_ABBR_DATA_MARKER_END, inputLine)
//                            .getData();
//                    final String subjFull = ScraperUtils.parseData(ELLUCIAN_SS_TERM_DATA_SUBJECT_FULL_DATA_MARKER_START,
//                            ELLUCIAN_SS_TERM_DATA_SUBJECT_FULL_DATA_MARKER_START.length(),
//                            ELLUCIAN_SS_TERM_DATA_SUBJECT_FULL_DATA_MARKER_END, inputLine)
//                            .getData();
//                    subjectsMap.put(subjAbbr, subjFull);
//                    inputLine = in.readLine();
//                }
//            } catch (final Exception e) {
//                log.error(e);
//            }
//        }
//        return subjectsMap;
//    }

    public static Map<String, String> getSubjects(final String baseDataPage, final String singleTerm) throws IOException {
        return getSubjects(baseDataPage, ImmutableSet.of(singleTerm));
    }

    public static Map<String, String> getSubjects(final String baseDataPage, final Set<String> termIds) throws IOException {
        // The abbreviations are used as form parameters by Ellucian's "Banner" system
        final Map<String, String> subjectsMap = new TreeMap<>(); // ex. "CS" not "Computer Science"

        nextTermId:
        for(final String term : termIds) {
            final String dataPage = baseDataPage + ELLUCIAN_REGISTRATION_TERM_DATA_RELATIVE_PATH;
//            final String unencodedFormData = ELLUCIAN_SS_TERM_DATA_FORM_DATA + EllucianDataScraper.formatTermParameters(termIds, false);
            final String unencodedFormData = ELLUCIAN_SS_TERM_DATA_FORM_DATA + term;
            final BufferedReader in = getReaderForPageWithParams(dataPage, unencodedFormData,
                    baseDataPage + ELLUCIAN_DYNAMIC_SCHEDULE_RELATIVE_PATH);
            // TODO: clean up
            try {
                String inputLine = in.readLine();
                if (inputLine == null) {
                    log.error("Unreachable datapage");
                    return null;
                }
                while (!inputLine.contains(ELLUCIAN_SS_TERM_DATA_SUBJECT_MARKER_START)
                        || inputLine.contains(ELLUCIAN_SS_TERM_DATA_FALSE_MARKER)) {
                    inputLine = in.readLine();
                    if (inputLine == null) {
                        log.error("No subjects found specified term. End of page reached for baseDataPage: {}", baseDataPage);
                        continue nextTermId;
                    }
                }
                inputLine = in.readLine();

                nextSubject:
                while (!inputLine.contains(ELLUCIAN_SS_TERM_DATA_SUBJECT_MARKER_END)) {
                    final String subjAbbr = ScraperUtils.parseData(ELLUCIAN_SS_TERM_DATA_SUBJECT_ABBR_DATA_MARKER_START,
                            ELLUCIAN_SS_TERM_DATA_SUBJECT_ABBR_DATA_MARKER_START.length(),
                            ELLUCIAN_SS_TERM_DATA_SUBJECT_ABBR_DATA_MARKER_END, inputLine)
                            .getData();
                    final String subjFull = ScraperUtils.parseData(ELLUCIAN_SS_TERM_DATA_SUBJECT_FULL_DATA_MARKER_START,
                            ELLUCIAN_SS_TERM_DATA_SUBJECT_FULL_DATA_MARKER_START.length(),
                            ELLUCIAN_SS_TERM_DATA_SUBJECT_FULL_DATA_MARKER_END, inputLine)
                            .getData();
                    subjectsMap.put(subjAbbr, subjFull);
                    inputLine = in.readLine();
                }
            } catch (final Exception e) {
                log.error(e);
            } finally {
                in.close();
            }
        }
        return subjectsMap;
    }
}
