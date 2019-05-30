package io.collegeplanner.my.collegecoursescheduler.service.scraper.impl;

import io.collegeplanner.my.collegecoursescheduler.model.dto.CourseSectionDto;
import io.collegeplanner.my.collegecoursescheduler.service.scraper.GenericScraper;
import io.collegeplanner.my.collegecoursescheduler.util.ChainedParserMetadata;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.*;

@Log4j2
public class UwScraper extends GenericScraper {
    private final Map<String, List<CourseSectionDto>> courseMap = new HashMap<>();

    @Override
    public String getUniversityName() {
        return UNIVERSITY_OF_WASHINGTON;
    }

    // @Override
    public void iterateInput(final List<String> chosenCourseNames) throws Exception {

        super.setNumChosenCourses(chosenCourseNames.size());

        // Set to remove duplicate departments ("CS-107", "CS-108", etc.)
        final Set<String> userDepartments = new HashSet<>();
        for(final String courseName : chosenCourseNames) {
            final String dept = courseName.substring(0, courseName.indexOf("-"));
            userDepartments.add(dept);
        }

        // Step to retrieve department folders/pages in real-time
        final Set<String> departmentFolders = getDepartmentFolders(userDepartments);

        // Go to department page and extract the chosen courses
        for(final String dept : departmentFolders) {
            parseRegistrationData(dept);
        }
    }

    // TODO: better error handling..
    @Override
    public void parseRegistrationData(final String department) throws Exception {

        // Format the URL
        supplySearchUrl(department);

        final BufferedReader in = new BufferedReader(new InputStreamReader(getRegistration_URL().openStream()));
        // Initialize variables (up here instead of in while() loop so we can save previous values between iterations
        String inputLine, courseBundleTitle, courseID, courseID_unf, courseBundleID;
        inputLine = courseBundleTitle = courseID = courseID_unf = courseBundleID = "";

        boolean sameCourseDiffBlock = false;

        nextInput:
        while(inputLine != null) {

            // This is specific to UW; pertains to UW's choice of structured HTML data
            if(!sameCourseDiffBlock) {
                inputLine = in.readLine();
            }

            // This will store the temporary list of classes which we will check for time collisions
            final List<CourseSectionDto> tempList = new ArrayList<>();

            // Found a new course
            CourseSectionDto newCourse = new CourseSectionDto();

            // Storing the related course if there is one (e.g. a discussion section for lecture class)
            CourseSectionDto parentCourse = null;

            // Starts here as the first class in a "bundle" of courses
            boolean firstClassInBundle = true;

            // Only do this once for an entire "block" of classes (which may have multiple lecture courses)
            if(!sameCourseDiffBlock) {
                try {
                    searchForID:
                    while (inputLine != null) {
                        while (!inputLine.contains(UW_COURSE_ID_MARKER)) {
                            inputLine = in.readLine();
                            continue searchForID;
                        }
                        break searchForID;
                    }
                    if(inputLine == null) {
                        return;
                    }

                    // Unformatted Course ID
                    // raw: "cse143"
                    courseID_unf = parseData(UW_COURSE_ID_MARKER, UW_COURSE_ID_MARKER.length(), ">", inputLine)
                            .getData()
                            .toUpperCase();
                    // courseID_unf: "CSE143"
                    final StringBuilder hyphenatedValue = new StringBuilder(courseID_unf);
                    int indexForHyphen = 0;
                    while (!Character.isDigit(courseID_unf.charAt(indexForHyphen))) {
                        indexForHyphen++;
                    }
                    hyphenatedValue.insert(indexForHyphen, "-");
                    // course (hyphenated): "CSE-143"
                    courseID = hyphenatedValue.toString();

                    // If this class is one the user chose, then keep processing. Else continue searching
                    if (!super.getUserOptions().getChosenCourseNames().contains(courseID)) {
                        continue nextInput;
                    }

                    // Save the bundle of courses under one name
                    courseBundleID = (department.replace(".html", "")
                            + courseID.substring(department.length() - 5))
                            .toUpperCase(); // minus ".html"

                    // Extract course title
                    // TODO: THIS COULD BE PROBLEM IF ***.html IS NOT EQUAL TO DEPARTMENT ALL THE TIME
                    final String courseTitle = parseData(("#" + courseID_unf.toLowerCase()),
                            courseID_unf.length() + 2, "</A>", inputLine)
                            .getData();
                    courseBundleTitle = courseTitle;

                    //Info is on next line
                    inputLine = in.readLine();
                }
                catch(final Exception e) {
                    log.fatal("Error retrieving course's title.. \n inputLine: " + inputLine, e);
                }

            }

            newCourse.setCourse(courseBundleID + " (LEC)");

            /** === Course Title === */
            newCourse.setTitle(courseBundleTitle);

            // This specific course will either be a lecture with sub-sections, or just a single course

            // Just in case
            // TODO: test if this is actually necessary; it's a good precaution, just not in the BEST location..
            if(inputLine == null) {
                return;
            }

            // Add this one, then check for sections by seeing if the inputLine contains the UW_COURSE_ID_MARKER
            searchForNextSection:
            while(!inputLine.contains(UW_COURSE_ID_MARKER)) {
                if(inputLine.contains(UW_SECTION_ID_MARKER)) {

                    /**
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
                        if (!courseMap.containsKey(newCourse.getCourse())) {
                            courseMap.put(newCourse.getCourse(), new ArrayList<>());
                        }
                        // Add course to the hash map
                        courseMap.get(newCourse.getCourse()).add(newCourse);
                    }
                     */

                    newCourse = new CourseSectionDto();
                    // We're still in the same "bundle" of classes, so this next class will share the previous title
                    newCourse.setTitle(courseBundleTitle);
                    // Same as reasoning above, the course (e.g. "CSE-143") stays the same in each "bundle"
                    newCourse.setCourse(courseBundleID);
                }
                else if((inputLine = in.readLine()).contains(UW_BUNDLE_ID_MARKER)){
                    sameCourseDiffBlock = false;
                    continue nextInput;
                }
                // If it's NOT a section inputLine, then search for next section
                else if(!inputLine.contains(UW_COURSE_ID_MARKER)) {
                    continue searchForNextSection;
                }

                // If the class is marked by UW as unavailable for registration, we don't want to
                //   include it. Plus those classes often interfere with the data parser below
                if(((inputLine.trim().charAt(0) == UW_UNAVAILABLE_CLASS_MARKER))) {
                    inputLine = in.readLine();
                    continue searchForNextSection;
                }

                // If it IS a lecture (or section), continue down here
                // When code reaches this point, the class is a section/additional class for a lecture

                /** Find data using UW's HTML order:
                 *      Order: CourseLetterID | Units/SectionType | MeetingDays | MeetingTimeBlock |
                 *              Building Abbr. | Room # | Open/Closed | Seats (Remaining/Total)
                 *  Store index end location of each successive data and use to locate the next
                 */
                ChainedParserMetadata chainedParser;

                /** Find schedule number (same process as above; doesn't use chainedParser) */
                newCourse.setSchedNum(
                        parseData(UW_SCHEDULE_NUM_PARSER_START, UW_SCHEDULE_NUM_PARSER_START.length(),
                                UW_SCHEDULE_NUM_PARSER_END, inputLine)
                            .getData()
                );


                /** Find Units / Section Type (starting chainedParser here) */
                chainedParser = parseData(UW_UNITS_PARSER_START,
                        UW_UNITS_PARSER_START.length() + FOUR_UNITS,
                        FOUR_UNITS, inputLine);
                newCourse.setUnits(chainedParser.getData());

                /** Find Meeting Days */
                chainedParser = parseData(chainedParser.getEndIndex(),
                        chainedParser.getEndIndex() + TEN_UNITS, inputLine);
                newCourse.getDays()
                        .add(chainedParser.getData().toUpperCase());

                /** Find Meeting TimeBlock */
                chainedParser = parseData(chainedParser.getEndIndex(),
                        chainedParser.getEndIndex() + TEN_UNITS, inputLine);
                // Change "630-720P" to "630-720"
                final String timeblock = chainedParser.getData()
                        .replace("P","");
                try {
                    int startTime = Integer.parseInt(timeblock.substring(0, timeblock.indexOf("-")));
                    int endTime = Integer.parseInt(timeblock.substring(timeblock.indexOf("-") + 1));

                    // convert to 24-hour time
                    if(startTime < 800) {
                        startTime += 1200;
                        endTime += 1200;
                    }
                    else if(endTime < startTime) {
                        endTime += 1200;
                    }
                    // Change format from "900-950" to "0900-0950"
                    final String startTimeString = (startTime < 1000) ? "0" + Integer.toString(startTime) : Integer.toString(startTime);
                    final String endTimeString = (endTime < 1000) ? "0" + Integer.toString(endTime) : Integer.toString(endTime);

                    final String formattedTime = startTimeString + "-" + endTimeString;

                    newCourse.getTimes().add(formattedTime);
                }
                catch(final Exception e) {
                    log.fatal("Error retrieving/parsing time: \n\t" +
                            "Department:" + department + "\n\t" +
                            "CourseBundle ID: " + courseBundleID + "\n\t" +
                            "ScheduleDto Number: " + newCourse.getSchedNum() + "\n\t" +
                            "Input line: " + inputLine, e);
                }

                /** Find Building Abbrev. */
                int startIndex = inputLine.indexOf(UW_BUILDING_ABBR_PARSER_START_1,
                        inputLine.indexOf(UW_BUILDING_ABBR_PARSER_START_2)) + 1;
                int endIndex = inputLine.indexOf(UW_BUILDING_ABBR_PARSER_END, startIndex);
                final String buildingAbbrev =
                        inputLine.substring(startIndex, endIndex).trim();
                /** Find Room # */
                startIndex = endIndex + UW_BUILDING_ABBR_PARSER_END.length();
                endIndex = startIndex + TEN_UNITS;
                final String roomNum =
                        inputLine.substring(startIndex, endIndex).trim();
                final String location = buildingAbbrev + "-" + roomNum;
                newCourse.getLocations()
                        .add(location);

                /** Find Professor */
                int charIndex = endIndex;
                final StringBuilder professorBuilder = new StringBuilder();
                // Until we reach a digit..
                char curChar = inputLine.charAt(charIndex);
                while(!Character.isDigit(curChar)) {
                    professorBuilder.append(curChar);
                    curChar = inputLine.charAt(++charIndex);
                }
                // Take out the "Open" or "Closed" if it got added to their name (very likely)
                if(professorBuilder.toString().contains(UW_OPEN_CLASS_MARKER)) {
                    final int startDelete = professorBuilder.toString().indexOf(UW_OPEN_CLASS_MARKER);
                    final int endDelete = startDelete + UW_OPEN_CLASS_MARKER.length();
                    professorBuilder.delete(startDelete, endDelete);
                }
                if(professorBuilder.toString().contains(UW_CLOSED_CLASS_MARKER)) {
                    final int startDelete = professorBuilder.toString().indexOf(UW_CLOSED_CLASS_MARKER);
                    final int endDelete = startDelete + UW_CLOSED_CLASS_MARKER.length();
                    professorBuilder.delete(startDelete, endDelete);
                }
                final String professorName = professorBuilder.toString().trim();
                newCourse.getInstructors()
                        .add(professorName);

                /** Find Seats (Remaining/Total) */
                // Now charIndex is at a digit (seats)
                final String seats = inputLine.substring(charIndex, charIndex + TEN_UNITS)
                        .replace(SPACE_CHARACTER, EMPTY_STRING);
                newCourse.setSeats(seats);

                // Look for where the course says the course identification letter(s)
                //  * Single letters (e.g. "A") indicates new block & lecture
                //  * Double letters (e.g. "AB") indicates same block & section
                // Begins after the line's first </A>; take the next 4 characters as a string & trim (parseData() already does this)
                final String courseLettersID = parseData(UW_COURSE_LETTERS_PARSER_START,
                            UW_COURSE_LETTERS_PARSER_START.length(),
                        FOUR_UNITS, inputLine)
                        .getData();
                // If it is a single letter (see above), then start from the beginning as a new block of classes
                if(courseLettersID.length() == 1) {
                    sameCourseDiffBlock = true;
                    firstClassInBundle = true;
                    // Mark this course as a lecture
                    newCourse.setCourse(newCourse.getCourse() + " (LEC)");
                    // TODO: can I take out related course and just set it directly?
                    parentCourse = newCourse;
                    // Continue to next line and look for next section
//                    inputLine = in.readLine();
//                    continue searchForNextSection;
                } else {
                    // Now that we reach here, the next class is not the first class (therefore set boolean to false)
                    firstClassInBundle = false;
                    newCourse.setCourse(newCourse.getCourse() + " (SEC)");
                }

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
                    if (!courseMap.containsKey(newCourse.getCourse())) {
                        courseMap.put(newCourse.getCourse(), new ArrayList<>());
                    }
                    // Add course to the hash map
                    courseMap.get(newCourse.getCourse()).add(newCourse);
                }

                // Read next line to decide what action to take
                inputLine = in.readLine();
                if(inputLine.contains(UW_BUNDLE_ID_MARKER)){
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

    /** Formats the URL (deprecated for UW) */
    @Override
    public String formatURL(String url) {
        return url;
    }

    /** Format string of days into usable array */
    @Override
    public int[] convDaysToArray(final String days) {
        final int[] res = new int[5];
        if(days.contains("M")) res[0] = 1;
        if(days.contains("W")) res[2] = 1;
        if(days.contains("TH")) res[3] = 1;
        if(days.contains("F")) res[4] = 1;
        // Distinguish between T in 'MTW' and T in 'WTHF"
        int tuesCount = 0;
        for(int i = 0; i < days.length(); i++) {if(days.charAt(i) == 'T') tuesCount++;}
        switch(tuesCount) {
            case 1:
                if(days.indexOf("T") == (days.length() - 1)) res[1] = 1; // MT, T
                else if(days.charAt(days.indexOf("T") + 1) != 'H') res[1] = 1; // MTW, TF, etc.
                break;
            case 2:
                res[1] = 1; // TTH, etc.
                break;
        }
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

    /** Rowspan formula */
    @Override
    public int rowspanFormula(final int startHour, final int startMin, final int endHour, final int endMin) {
        return ((((endHour * 60) + endMin) - ((startHour * 60) + startMin)) / 15);
    }

    /** Append parameters to the search URL parameters */
    public void appendParameter(final String paramToAdd) {
        if(StringUtils.isEmpty(super.getRegistrationSearchPage())) {
            super.setRegistrationSearchPage(REGISTRATION_SEARCH_PAGE_UW);
        }
        super.setRegistrationSearchPage(this.getRegistrationSearchPage() + paramToAdd);
    }

    /** Set the period/term to search in the URL */
    @Override
    public void setTermParameter(final String season, final String year) {
        String seasonName = "";
        switch (season) {
            case "Winter":
                seasonName = "WIN";
                break;
            case "Spring":
                seasonName = "SPR";
                break;
            case "Summer":
                seasonName = "SUM";
                break;
            case "Fall":
                seasonName = "AUT";
                break;
        }
        final String seasonFolder = seasonName + year;
        appendParameter(seasonFolder);
    }

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
    private ChainedParserMetadata parseData(final int indexStart, final int indexEnd, final String inputLine) {
        final String value = inputLine.substring(indexStart, indexEnd).trim();
        return new ChainedParserMetadata(value, indexStart, indexEnd);
    }

    /** Gets unique HTML addresses for each chosen department */
    private Set<String> getDepartmentFolders(final Set<String> userDepartments) {
        final Set<String> departmentFolders = new HashSet<>();

        // Parse HTML
        try(BufferedReader in = new BufferedReader(new InputStreamReader(new URL(this.getRegistrationSearchPage()).openStream()))) {
            String inputLine;

            while((inputLine = in.readLine()) != null) {
                // Find all page-links
                if(inputLine.contains(UW_PAGELINK_MARKER_START)
                        && !inputLine.contains(UW_NON_PAGELINK_MARKER)
                        && inputLine.contains(PARENTHESES_OPEN)
                        && inputLine.contains(PARENTHESES_CLOSE))
                {
                    // Find course abbreviation inside parentheses
                    final String courseAbbr = parseData(PARENTHESES_OPEN, ONE_UNIT,
                                PARENTHESES_CLOSE, inputLine)
                            .getData();
                    if(userDepartments.contains(courseAbbr)) {
                        final String folder = parseData(UW_PAGELINK_MARKER_START, NINE_UNITS,
                                UW_PAGELINK_MARKER_END, inputLine)
                                .getData();
                        if(!folder.contains(POUND_SIGN)) {
                            departmentFolders.add(folder);
                        }
                    }
                }
                else continue;
            }

        }
        catch(final Exception e){
            log.fatal("Error with getting department folders" +
                    "URL: " + getRegistration_URL().toString(), e);
        }

        return departmentFolders;
    }

}
