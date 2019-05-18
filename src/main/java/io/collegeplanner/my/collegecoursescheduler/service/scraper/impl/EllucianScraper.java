package io.collegeplanner.my.collegecoursescheduler.service.scraper.impl;

import io.collegeplanner.my.collegecoursescheduler.model.dto.CourseSectionDto;
import io.collegeplanner.my.collegecoursescheduler.repository.scrapers.ellucian.EllucianDataScraper;
import io.collegeplanner.my.collegecoursescheduler.service.scraper.GenericScraper;
import io.collegeplanner.my.collegecoursescheduler.service.scraper.factories.TermCodeFactory;
import io.collegeplanner.my.collegecoursescheduler.util.ChainedParserMetadata;
import io.collegeplanner.my.collegecoursescheduler.util.ScraperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.*;

/** Strictly a Proof-of-Concept for 100s of colleges to be added */
@Log4j2
@RequiredArgsConstructor
public class EllucianScraper extends GenericScraper {
    private final String collegeName;
    // private final String BASE_BANNER_PAGE;
    // private final String REGISTRATION_TERM;
    private final Map<String, List<CourseSectionDto>> courseMap = new HashMap<>();

    @Override
    public String getUniversityName() {
        return collegeName;
    }

    public void iterateInput(final List<String> chosenCourseNames) throws Exception {
        final Set<String> userDepartments = ScraperUtils.getDepartmentsFromChosenClasses(chosenCourseNames);
        super.setNumChosenCourses(chosenCourseNames.size());
        parseRegistrationData(userDepartments);
    }

    @Override // unused.. deprecate
    public void parseRegistrationData(final String department){}

    public void parseRegistrationData(final Set<String> departments) throws IOException {
        final String baseDataPage = ELLUCIAN_UNIVERSITIES_SS_DATA_PAGES.get(this.collegeName);
        final String dataPage = baseDataPage + ELLUCIAN_REGISTRATION_COURSES_RELATIVE_PATH;
        final String unencodedFormData = ELLUCIAN_SS_COURSE_DATA_FORM_DATA
                + this.getTermChosen() + EllucianDataScraper.formatSubjectParameters(departments);
        final BufferedReader in = EllucianDataScraper.getReaderForPageWithParams(dataPage, unencodedFormData,
                baseDataPage + ELLUCIAN_REGISTRATION_TERM_DATA_RELATIVE_PATH);

        // Initialize variables (up here instead of in while() loop so we can save previous values between iterations
        String inputLine, courseBundleTitle, courseID, courseID_unf, courseBundleID, courseBundleSchedNum, courseBundleUnits;
        inputLine = courseBundleTitle = courseID = courseID_unf = courseBundleID = courseBundleSchedNum = courseBundleUnits = "";

//        boolean sameCourseDiffBlock = false;

        nextInput:
        while(inputLine != null) {

            // This will store the temporary list of classes which we will check for time collisions
            final List<CourseSectionDto> tempList = new ArrayList<>();

            // Found a new course
            CourseSectionDto newCourse = new CourseSectionDto();

            // Storing the related course if there is one (e.g. a discussion section for lecture class)
            CourseSectionDto parentCourse = null;

            // Starts here as the first class in a "bundle" of courses
            boolean firstClassInBundle = true;

            // Only do this once for an entire "block" of classes (which may have multiple lecture courses)
//            if(!sameCourseDiffBlock) {
            try {
                searchForTitle:
                while (inputLine != null) {
                    // TODO: double check this is supposed to be AND (&&) vs. OR (||)
                    while (!inputLine.contains(ELLUCIAN_SS_COURSE_DATA_COURSE_MARKER_I)
                            && (!inputLine.contains(ELLUCIAN_SS_COURSE_DATA_COURSE_MARKER_II))) {
                        inputLine = in.readLine();
                        continue searchForTitle;
                    }
                    break searchForTitle;
                }
                // debugging artifact - might not be necessary
                if(inputLine == null) {
                    return;
                }

                // This helps when parsing multiple pieces of data that occur in succession on one line
                // by keeping track of the end index of the previously parsed piece of information
                //   ex. "Introduction/Computer Science - 46665 - COSC 102 - 007"
                ChainedParserMetadata chainedParser;

                // starting chainedParser here
                /** Course Title */
                chainedParser = parseData(ELLUCIAN_SS_COURSE_DATA_COURSE_TITLE_DATA_MARKER_START,
                        ELLUCIAN_COURSE_TITLE_MARKER_LENGTH + TWO_UNITS, ELLUCIAN_SS_COURSE_DATA_COURSE_ID_DATA_MARKER_END, inputLine);
                courseBundleTitle = chainedParser.getData();

                /** Schedule Number */
                chainedParser = parseData(chainedParser.getEndIndex() + TWO_UNITS,
                        ELLUCIAN_SCHEDULE_NUMBER_MARKER_END, inputLine);
                courseBundleSchedNum = chainedParser.getData();
                // newCourse.setScheduleNum(chainedParser.getData());

                /** CourseID */
                chainedParser = parseData(chainedParser.getEndIndex() + TWO_UNITS,
                        ELLUCIAN_COURSE_ID_MARKER_END, inputLine);
                // final String formattedCourseId = chainedParser.getData().replace(" ", "-");
                // courseBundleID = formattedCourseId;
                courseBundleID = chainedParser.getData();
                // newCourse.setCourseID(formattedCourseId);
                // courseID = formattedCourseId;

                // If this class is one the user chose, then keep processing. Else continue searching
                if (!super.getUserOptions().getChosenCourseNames().contains(courseBundleID)) {
                    inputLine = in.readLine();
                    continue nextInput;
                }
            }
            catch(final Exception e) {
                log.fatal("Error retrieving course's title.. \n inputLine: " + inputLine, e);
            }

//            }

            // newCourse.setCourseID(courseBundleID);

            /** === Course Title === */
            // newCourse.setTitle(courseBundleTitle);

            // This specific course will either be a lecture with sub-sections, or just a single course

            inputLine = in.readLine();
            // uhh just in case
            // TODO: test if this is actually necessary; it's a good precaution, just not in the BEST location..
            if(inputLine == null) {
                return;
            }

            // TODO: too much nesting
            int sectionsTableRowNumber = 0;
            searchForNextSection:
            while(!(inputLine = in.readLine()).contains(ELLUCIAN_SS_COURSE_DATA_COURSE_MARKER_I)
                    && (!inputLine.contains(ELLUCIAN_SS_COURSE_DATA_COURSE_MARKER_II))) {
                if(inputLine.contains(ELLUCIAN_SCHEDULED_MEETING_TIMES_MARKER)
                        || (sectionsTableRowNumber >= 2)) {
                    // while-loop here
                    findData:
                    while(true) {
                        if (inputLine.contains(ELLUCIAN_COURSE_SECTION_MARKER_END)) {
                            continue nextInput;
                        }
                        if (inputLine.contains(ELLUCIAN_COURSE_SECTION_MARKER)) {
                            sectionsTableRowNumber++;
                            if (sectionsTableRowNumber == ELLUCIAN_SECTIONS_TABLE_ROW_HEADER) {
                                inputLine = in.readLine();
                                continue findData;
                            }
                            if (sectionsTableRowNumber >= ELLUCIAN_SECTIONS_TABLE_SECOND_COURSE_IN_BUNDLE) {
                                parentCourse = newCourse;
                                firstClassInBundle = false;
                            }
                            newCourse = new CourseSectionDto();
                            newCourse.setTitle(courseBundleTitle);
                            newCourse.setCourseID(courseBundleID);
                            newCourse.setScheduleNum(courseBundleSchedNum);
                            newCourse.setUnits(courseBundleUnits);
                            break findData;
                        }
                        inputLine = in.readLine();
                    }
                }
                else if(inputLine.contains(ELLUCIAN_NUM_CREDITS_MARKER)) {
                    courseBundleUnits = inputLine.trim().replaceAll(" +", " ");
                    continue searchForNextSection;
                }
                // If it's NOT a section inputLine, then search for next section
                else {
                    continue searchForNextSection;
                }
                /**
                else if(!inputLine.contains(ELLUCIAN_COURSE_TITLE_MARKER)) {
                    continue searchForNextSection;
                }
                 */

                // If it IS a lecture (or section), continue down here

                // data looks like this:
                // \/ <-- sectionsTableColNumber
                //     <tr>                                                                    <-- [current inputLine]
                // [0]  <td CLASS="dddefault">Class</td>                                        <-- Type (ignore)
                // [1]  <td CLASS="dddefault">12:01 am - 12:02 am</td>                          <-- Times (parse)
                // [2]  <td CLASS="dddefault">TR</td>                                           <-- Days (parse)
                // [3]  <td CLASS="dddefault"><ABBR title = "To Be Announced">TBA</ABBR></td>   <-- Location (parse)
                // [4]  <td CLASS="dddefault">21-Aug-2019 - 04-Dec-2019</td>                    <-- Dates (ignore)
                // [5]  <td CLASS="dddefault">Primary Meeting</td>                              <-- Schedule Type (parse)
                // [6]  <td CLASS="dddefault">Jon E.  Cohen (<ABBR title= "Primary">P</ABBR>)</td> <-- Instructor (parse)

                // Columnar data starts one line down so skip down to the 2nd line
                // currently on 1st line
                inputLine = in.readLine();  // 2nd line (the [0] line above)

                // TODO: simplify this so less if-else statements.. (define an ImmutableMap in Constants with corresponding data/index)
                // Parse data line-by-line
                /** Times */
                inputLine = in.readLine();
                final String parsedTimeblock = parseData(ELLUCIAN_SECTIONS_TABLE_COL_MARKER_START,
                        ELLUCIAN_SECTIONS_TABLE_COL_MARKER_START.length(), ELLUCIAN_SECTIONS_TABLE_COL_MARKER_END,
                        inputLine).getData();
                if(parsedTimeblock.equals(TO_BE_ARRANGED)) {
                    // If there's no time, this tool is useless. Scrap this class and look for the next..
                    continue nextInput;
                }
                final String formattedTimeblock = formatCourseTime(parsedTimeblock);
                newCourse.getTimes().add(formattedTimeblock);
                /** Days */
                inputLine = in.readLine();
                final String parsedDays = parseData(ELLUCIAN_SECTIONS_TABLE_COL_MARKER_START,
                        ELLUCIAN_SECTIONS_TABLE_COL_MARKER_START.length(), ELLUCIAN_SECTIONS_TABLE_COL_MARKER_END,
                        inputLine).getData();
                newCourse.getDays().add(parsedDays);
                /** Location */
                inputLine = in.readLine();
                final String parsedLocation = parseData(ELLUCIAN_SECTIONS_TABLE_COL_MARKER_START,
                        ELLUCIAN_SECTIONS_TABLE_COL_MARKER_START.length(), ELLUCIAN_SECTIONS_TABLE_COL_MARKER_END,
                        inputLine).getData();
                newCourse.getLocations().add(parsedLocation);
                /** Schedule Type */
                in.readLine(); // Skip line
                inputLine = in.readLine();
                final String scheduleType = parseData(ELLUCIAN_SECTIONS_TABLE_COL_MARKER_START,
                        ELLUCIAN_SECTIONS_TABLE_COL_MARKER_START.length(), ELLUCIAN_SECTIONS_TABLE_COL_MARKER_END,
                        inputLine).getData();
                final String formattedSchedType = formatSchedType(scheduleType);
                newCourse.setCourseID(courseBundleID + formattedSchedType);
                /** Instructor */
                inputLine = in.readLine();
                final String parsedInstructor = parseData(ELLUCIAN_SECTIONS_TABLE_COL_MARKER_START,
                        ELLUCIAN_SECTIONS_TABLE_COL_MARKER_START.length(), ELLUCIAN_SECTIONS_TABLE_COL_MARKER_END,
                        inputLine).getData();
                newCourse.getInstructors().add(parsedInstructor);

                if(!firstClassInBundle) {
                    newCourse.setParentCourse(parentCourse);
                }

                // If it has every data point we need, then add it to the Course Map
                //   (for readability.. this will be "true" after AT LEAST one iteration of this while loop)
                // If it's NOT complete, then there's not enough info to intuitively use this webapp
                //  so forget about the class..
                // TODO: maybe I should put this at the bottom for better readability.. would that work?
                if (newCourse.isComplete()) {
                    // If the map doesn't already contain an array list for this class, make a new list
                    if (!courseMap.containsKey(newCourse.getCourseID())) {
                        courseMap.put(newCourse.getCourseID(), new ArrayList<>());
                    }
                    // Add course to the hash map
                    courseMap.get(newCourse.getCourseID()).add(newCourse);
                }

                // Read next line to decide what action to take
                inputLine = in.readLine();
                if(inputLine.contains(ELLUCIAN_SS_COURSE_DATA_COURSE_MARKER_I)
                    || inputLine.contains(ELLUCIAN_SS_COURSE_DATA_COURSE_MARKER_II)){
                    // Not likely, but just in case (?)
                    continue nextInput;
                }
                else if(inputLine != null) {
                    continue searchForNextSection;
                }
                else {
                    return;
                }
            }

        }

    }

    /** Sets the department URL to parse */
    @Override
    public URL supplySearchUrl(final String department) throws MalformedURLException {
        this.setRegistration_URL(new URL(this.getRegistrationSearchPage() + "/" + department));
        return null; //temp
    }

    /** Formats the URL (unused for ELLUCIAN) */
    @Override
    public String formatURL(String url) {
        return url;
    }

    /** Format string of days into usable array */
    @Override
    public int[] convDaysToArray(final String days) {
        final int[] res = new int[5];
        if(days.contains("M")) res[0] = 1;
        if(days.contains("T")) res[1] = 1;
        if(days.contains("W")) res[2] = 1;
        if(days.contains("R")) res[3] = 1;
        if(days.contains("F")) res[4] = 1;
        return res;
    }

    /** Create the size-sorted-courses list */
    @Override
    public void createSizeSortedCourses() {
        // List<List<Course>> sizeSortedCourses
        final Iterator it = courseMap.entrySet().iterator();
        while(it.hasNext()) {
            final Map.Entry pair = (Map.Entry)it.next();
            final List<CourseSectionDto> courseList = (List<CourseSectionDto>)pair.getValue();
            super.getSizeSortedCourses()
                    .add(courseList);
        }
        Collections.sort(super.getSizeSortedCourses(), (Comparator<List>) (a1, a2) -> {
            return a1.size() - a2.size();
        });
    }

    @Override
    public void setTermParameter(final String season, final String year) {
        this.setTermChosen(TermCodeFactory.getCodeForCollege(this.collegeName, season, year));
    }

    /** Rowspan formula */
    @Override
    public int rowspanFormula(final int startHour, final int startMin, final int endHour, final int endMin) {
        return ((((endHour * 60) + endMin) - ((startHour * 60) + startMin)) / 15);
    }

    /** Append parameters to the search URL parameters */
//    public void appendParameter(final String paramToAdd) {
//        if(StringUtils.isEmpty(super.getRegistrationSearchPage())) {
//            super.setRegistrationSearchPage(REGISTRATION_SEARCH_PAGE_ELLUCIAN);
//        }
//        super.setRegistrationSearchPage(this.getRegistrationSearchPage() + paramToAdd);
//    }



    /**** --------------------  *****
     *****    Private Methods    *****
     ***** --------------------  ****/

    /** Extracts data from HTML tags */
    private ChainedParserMetadata parseData(final String indexStartChar, int startOffset, final int numCharsToParse, final String inputLine) {
        final int indexStart = inputLine.indexOf(indexStartChar) + startOffset;
        final int indexEnd = indexStart + numCharsToParse;
        return parseData(indexStart, indexEnd, inputLine);
    }
    private ChainedParserMetadata parseData(final String indexStartChar, int startOffset, final String indexEndChar, final String inputLine) {
        final int indexStart = inputLine.indexOf(indexStartChar) + startOffset;
        final int indexEnd = inputLine.indexOf(indexEndChar, indexStart); // starts the search from indexStart
        return parseData(indexStart, indexEnd, inputLine);
    }
    private ChainedParserMetadata parseData(final int indexStart, final String indexEndChar, final String inputLine) {
        final int indexEnd = inputLine.indexOf(indexEndChar, indexStart);
        return parseData(indexStart, indexEnd, inputLine);
    }
    private ChainedParserMetadata parseData(final int indexStart, final int indexEnd, final String inputLine) {
        try {
            final String data = inputLine.substring(indexStart, indexEnd).trim();
            final String value = removeExcessTags(data);
            return new ChainedParserMetadata(value, indexStart, indexEnd);
        } catch(final Exception e) {
            log.error("Error parsing data: {}", inputLine, e);
        }
        return null;
    }
    private String removeExcessTags(final String data) {
        try {
            StringBuilder modifiedData = new StringBuilder(data);
            int indexLeft = modifiedData.indexOf("<");
            int indexRight = modifiedData.indexOf(">") + 1; // +1 to include '>' in the following deletion
            while (indexLeft >= 0 && indexRight >= 0) {
                modifiedData.delete(indexLeft, indexRight);
                modifiedData = new StringBuilder(modifiedData.toString()); // can't use delete(start, end) for some reason..
                indexLeft = modifiedData.indexOf("<");
                indexRight = modifiedData.indexOf(">") + 1;
            }
            return modifiedData.toString().trim(); // remove leading and trailing spaces from StringBuilder
        }
        catch(final Exception e) {
            log.error("Improperly parsed/formatted data: {}", data, e);
        }
        return "";
    }

    private String formatCourseTime(final String parsedTimeblock) {
        final String timesWithMark = parsedTimeblock.trim().replace(" ", "").replace(":", "");
        final String timeRange = timesWithMark.replaceAll("[a-zA-Z]+", "");

        int startTime = Integer.parseInt(timeRange.substring(0, timeRange.indexOf("-")));
        startTime = (timesWithMark.substring(0, timesWithMark.indexOf("-")).contains("pm") && startTime < 1200) ? startTime + 1200 : startTime;
        int endTime = Integer.parseInt(timeRange.substring(timeRange.indexOf("-") + 1));
        endTime = (timesWithMark.substring(timesWithMark.indexOf("-") + 1).contains("pm") && endTime < 1200) ? endTime + 1200 : endTime;

        // Change format from "900-950" to "0900-0950"
        final String startTimeString = (startTime < 1000) ? "0" + Integer.toString(startTime) : Integer.toString(startTime);
        final String endTimeString = (endTime < 1000) ? "0" + Integer.toString(endTime) : Integer.toString(endTime);

        return (startTimeString + "-" + endTimeString);
    }

    // TODO: don't hardcode strings (put into Constants)
    private static String formatSchedType(final String scheduleType) {
        switch(scheduleType) {
            case "Primary Meeting":
                return " [LEC]";
            case "Conference":
                return " [CONF]";
            case "Independent Study/Research":
                return " [IS/P]";
            default:
                return (" [" + scheduleType + "]");
        }
    }
}

