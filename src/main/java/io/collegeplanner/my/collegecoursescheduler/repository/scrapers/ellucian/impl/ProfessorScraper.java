package io.collegeplanner.my.collegecoursescheduler.repository.scrapers.ellucian.impl;

import io.collegeplanner.my.collegecoursescheduler.repository.RegistrationDataDao;
import io.collegeplanner.my.collegecoursescheduler.repository.scrapers.ellucian.EllucianDataScraper;
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
public class ProfessorScraper extends EllucianDataScraper {

//    @Autowired
//    private Jdbi jdbi;

    public void scrapeAndPersistDataForCollege(final String college, final Set<String> termIds) throws IOException {
        final String baseDataPage = ELLUCIAN_UNIVERSITIES_SS_DATA_PAGES.get(college);
        final Map<String, String> instuctorsMap = getInstructors(baseDataPage, termIds);

        final Jdbi jdbi = DatabaseUtils.getDatabaseConnection();
        final String tableName = PROFESSORS_TABLE_PREFIX + college;
        jdbi.onDemand(RegistrationDataDao.class).createProfessorsTableIfNotExists(tableName);
        jdbi.onDemand(RegistrationDataDao.class).updateProfessorsTableBulk(tableName,
                instuctorsMap.keySet(), instuctorsMap.values());

    }

//    private static Map<String, String> getInstructors(final String baseDataPage, final Set<String> termIds) {
//        final Map<String, String> instructorsMap = new TreeMap<>(); // {Instructor --> Instructor Code}
//        nextTermId:
//        for(final String term : termIds) {
////            log.debug("TermID: " + term);
//            final String dataPage = baseDataPage + ELLUCIAN_REGISTRATION_TERM_DATA_RELATIVE_PATH;
//            final String unencodedFormData = ELLUCIAN_SS_TERM_DATA_FORM_DATA + term;
//            final BufferedReader in = getReaderForPageWithParams(dataPage, unencodedFormData,
//                    baseDataPage + ELLUCIAN_DYNAMIC_SCHEDULE_RELATIVE_PATH);
//
//            // TODO: clean up
//            try {
//                String inputLine = in.readLine();
//                if (inputLine == null) {
//                    log.warn("Unreachable datapage");
//                    continue nextTermId;
//                }
//                while (!inputLine.contains(ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_MARKER_START)
//                        || inputLine.contains(ELLUCIAN_SS_TERM_DATA_FALSE_MARKER)) {
//                    inputLine = in.readLine();
//                    if(inputLine == null) {
//                        log.debug("No instructors found specified term. End of page reached for page {} termId: {} (likely ignorable)", baseDataPage, term);
//                        continue nextTermId;
//                    }
//                }
//                in.readLine(); // skip first line (kind of a hacky fix..) // TODO: make more elegant
//                inputLine = in.readLine();
//                while (!inputLine.contains(ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_MARKER_END)) {
//                    if(!inputLine.contains(ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_MARKER_START)) {
//                        continue nextTermId; // for those weird terms where there's no actual content.. (ex. Brown University 201915)
//                    }
//                    final String instructor = ScraperUtils.parseData(ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_MARKER_START,
//                            ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_MARKER_START.length(),
//                            ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_MARKER_END, inputLine).getData();
//                    final String value = ScraperUtils.parseData(ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_VALUE_MARKER_START,
//                            ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_VALUE_MARKER_START.length(),
//                            ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_VALUE_MARKER_END, inputLine).getData();
//                    instructorsMap.put(instructor, value);
//                    inputLine = in.readLine();
//                }
//            }
//            catch(final Exception e) {
//                log.error(e);
//            }
//        }
//        return instructorsMap;
//    }

    private static Map<String, String> getInstructors(final String baseDataPage, final Set<String> termIds) throws IOException {
        final Map<String, String> instructorsMap = new TreeMap<>(); // {Instructor --> Instructor Code}

        for(final String term : termIds) {
            final String dataPage = baseDataPage + ELLUCIAN_REGISTRATION_TERM_DATA_RELATIVE_PATH;
    //        final String unencodedFormData = ELLUCIAN_SS_TERM_DATA_FORM_DATA + EllucianDataScraper.formatTermParameters(termIds, false);
            final String unencodedFormData = ELLUCIAN_SS_TERM_DATA_FORM_DATA + term;
            final BufferedReader in = getReaderForPageWithParams(dataPage, unencodedFormData,
                    baseDataPage + ELLUCIAN_DYNAMIC_SCHEDULE_RELATIVE_PATH);

            // TODO: clean up
            try {
                String inputLine = in.readLine();
                if (inputLine == null) {
                    log.error("Unreachable datapage");
                    // continue nextTermId;
                }
                while (!inputLine.contains(ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_MARKER_START)
                        || inputLine.contains(ELLUCIAN_SS_TERM_DATA_FALSE_MARKER)) {
                    inputLine = in.readLine();
                    if (inputLine == null) {
                        log.error("No instructors found specified term. End of page reached for baseDataPage {}", baseDataPage);
                        return null;
                    }
                }
                in.readLine(); // skip first line (kind of a hacky fix..) // TODO: make more elegant
                inputLine = in.readLine();
                while (!inputLine.contains(ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_MARKER_END)) {
//                if(!inputLine.contains(ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_MARKER_START)) {
//                    continue nextTermId; // for those weird terms where there's no actual content.. (ex. Brown University 201915)
//                }
                    final String instructor = ScraperUtils.parseData(ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_MARKER_START,
                            ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_MARKER_START.length(),
                            ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_MARKER_END, inputLine).getData();
                    final String value = ScraperUtils.parseData(ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_VALUE_MARKER_START,
                            ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_VALUE_MARKER_START.length(),
                            ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_VALUE_MARKER_END, inputLine).getData();
                    instructorsMap.put(instructor, value);
                    inputLine = in.readLine();
                }
            } catch (final Exception e) {
                log.error(e);
            } finally {
                in.close();
            }
        }

        return instructorsMap;
    }
}
