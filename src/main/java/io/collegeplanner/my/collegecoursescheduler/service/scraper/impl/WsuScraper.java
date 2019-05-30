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
public class WsuScraper extends GenericScraper {
    private final Map<String, List<CourseSectionDto>> courseMap = new HashMap<>();

    @Override
    public String getUniversityName() {
        return WASHINGTON_STATE_UNIVERSITY;
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
                    while (!inputLine.contains(WSU_COURSE_TITLE_MARKER)) {
                        inputLine = in.readLine();
                        continue searchForTitle;
                    }
                    inputLine = in.readLine(); // title is on next line
                    break searchForTitle;
                }
                if(inputLine == null) {
                    return;
                }
                courseBundleTitle = parseData(WSU_COURSE_TITLE_MARKER_START,
                WSU_COURSE_TITLE_MARKER_START.length(), WSU_COURSE_TITLE_MARKER_END, inputLine)
                        .getData();

                searchForCourseId:
                while (inputLine != null) {
                    while (!inputLine.contains(WSU_COURSE_ID_MARKER)) {
                        inputLine = in.readLine();
                        continue searchForCourseId;
                    }
                    break searchForCourseId;
                }
                if(inputLine == null) {
                    return;
                }
                // courseID_unf = "Biol/101/01" (lec) or "Biol/101/01L" (lab)
                courseID_unf = parseData(department, 0, WSU_COURSE_ID_MARKER_END, inputLine)
                        .getData();
                courseID_unf = courseID_unf.replaceFirst("/", "-").toUpperCase(); // format to "BIOL-101/01"
                // Save the bundle of courses under one name
                courseBundleID = courseID_unf.substring(0, courseID_unf.indexOf("/")); // "BIOL-101L"
                // What is to be checked against user class choices (e.g. users can't choose "BIOL-101L" but it comes
                // with selecting "BIOL-101"
                courseID = courseBundleID.replace("L", ""); // "BIOL-101"

                // TODO: if courseBundleID.contains("L").. (indicates required lab section and should be added as new class to hashmap)

                // If this class is one the user chose, then keep processing. Else continue searching
                if (!super.getUserOptions().getChosenCourseNames().contains(courseID)) {
                    continue nextInput;
                }
            }
            catch(final Exception e) {
                log.fatal("Error retrieving course's title.. \n inputLine: " + inputLine, e);
            }

//            }

            newCourse.setCourse(courseBundleID);

            /** === Course Title === */
            newCourse.setTitle(courseBundleTitle);

            // This specific course will either be a lecture with sub-sections, or just a single course

            // uhh just in case
            // TODO: test if this is actually necessary; it's a good precaution, just not in the BEST location..
            if(inputLine == null) {
                return;
            }

            // Add this one, then check for sections by seeing if the inputLine contains the UW_COURSE_ID_MARKER
            searchForNextSection:
            while(!inputLine.contains(WSU_COURSE_TITLE_MARKER)) {
                if(inputLine.contains(WSU_COURSE_ID_MARKER)) {

                    newCourse = new CourseSectionDto();
                    // We're still in the same "bundle" of classes, so this next class will share the previous title
                    newCourse.setTitle(courseBundleTitle);
                    // Same as reasoning above, the course (e.g. "CSE-143") stays the same in each "bundle"
                    newCourse.setCourse(courseBundleID);
                }
                // If it's NOT a section inputLine, then search for next section
                else if(!inputLine.contains(WSU_COURSE_TITLE_MARKER)) {
                    continue searchForNextSection;
                }

                // If the class is marked by UW as unavailable for registration, we don't want to
                //   include it. Plus those classes often interfere with the data parser below
                /** TODO..
                if(((inputLine.trim().charAt(0) == WSU_UNAVAILABLE_CLASS_MARKER))) {
                    inputLine = in.readLine();
                    continue searchForNextSection;
                }
                 */

                // If it IS a lecture (or section), continue down here
                // When code reaches this point, the class is a section/additional class for a lecture
//
//                /** Find schedule number */
//                while(!inputLine.contains(WSU_SCHEDULE_NUMBER_MARKER)) inputLine = in.readLine();
//                newCourse.setSchedNum(
//                    parseData(WSU_SCHEDULE_NUM_MARKER_START, WSU_SCHEDULE_NUM_MARKER_START.length(),
//                                WSU_SCHEDULE_NUM_MARKER_END, inputLine)
//                            .getData()
//                );
//
//                /** Find Units / Section Type */
//                while(!inputLine.contains(WSU_UNITS_MARKER)) inputLine = in.readLine();
//                newCourse.setUnits(
//                    parseData(WSU_UNITS_MARKER_START, WSU_UNITS_MARKER_START.length(),
//                            WSU_UNITS_MARKER_END, inputLine)
//                        .getData()
//                );
//
//                /** Find Meeting Days and Times */
//                while(!inputLine.contains(WSU_UNITS_MARKER)) inputLine = in.readLine();
//                final String combinedDaysAndTimes = parseData(WSU_DAYS_AND_TIMES_MARKER_START,
//                        WSU_DAYS_AND_TIMES_MARKER_START.length(), WSU_DAYS_AND_TIMES_MARKER_END, inputLine);
//                // Find first instance of a digit
//                String timeblock = "";
//                for(int i = 0; i < combinedDaysAndTimes.length(); i++) {
//                    if(Character.isDigit(combinedDaysAndTimes.charAt(i))) {
//                        newCourse.getDays().add(
//                            combinedDaysAndTimes.substring(0, i).replace(",", "")
//                        );
//                        timeblock = combinedDaysAndTimes.substring(i).replace(".", ":");
//                    }
//                }
//
//                /** Meeting TimeBlock Formatting */
//                try {
//                    String parsedStartTime = timeblock.substring(0, timeblock.indexOf("-"));
//                    String parsedEndTime = timeblock.substring(timeblock.indexOf("-") + 1);
//
//                    // convert to 4-digits
//                    if(parsedStartTime.length() == 2) {
//                        parsedStartTime += ":00";
//                    }
//                    if(parsedEndTime.length() == 2) {
//                        parsedEndTime += ":00";
//                    }
//
//                    final String formattedTime = parsedStartTime + "-" + parsedEndTime;
//                    newCourse.getTimes().add(formattedTime);
//                }
//                catch(final Exception e) {
//                    log.fatal("Error retrieving/parsing time: \n\t" +
//                            "Department:" + department + "\n\t" +
//                            "CourseBundle ID: " + courseBundleID + "\n\t" +
//                            "ScheduleDto Number: " + newCourse.getSchedNum() + "\n\t" +
//                            "Input line: " + inputLine, e);
//                }
//
//                /** Find Building Abbrev. and Room # */
//                while(!inputLine.contains(WSU_BUILDING_ABBR_MARKER)) inputLine = in.readLine();
//                newCourse.getLocations().add(parseData(WSU_BUILDING_ABBR_MARKER_START,
//                            WSU_BUILDING_ABBR_MARKER_START.length(), WSU_BUILDING_ABBR_MARKER_END, inputLine)
//                        .getData()
//                );
//
//                /** Find Professor */
//                while(!inputLine.contains(WSU_PROFESSOR_MARKER)) inputLine = in.readLine();
//                newCourse.getLocations().add(parseData(WSU_PROFESSOR_MARKER_START,
//                            WSU_PROFESSOR_MARKER_START.length(), WSU_PROFESSOR_MARKER_END, inputLine)
//                        .getData()
//                );
//                /** Find Seats (Remaining/Total) */
//                while(!inputLine.contains(WSU_SEATS_ALLOTTED_MARKER)) inputLine = in.readLine();
//                final String seatsAllotted = parseData(WSU_SEATS_ALLOTTED_MARKER_START,
//                        WSU_SEATS_ALLOTTED_MARKER_START.length(), WSU_SEATS_ALLOTTED_MARKER_END, inputLine);
//                while(!inputLine.contains(WSU_SEATS_TAKEN_MARKER)) inputLine = in.readLine();
//                final String seatsTaken = parseData(WSU_SEATS_TAKEN_MARKER_START,
//                        WSU_SEATS_TAKEN_MARKER_START_START.length(), WSU_SEATS_TAKEN_MARKER_END, inputLine);
//                final String seats = seatsTaken + "/" + seatsAllotted;
//                newCourse.setSeats(seats);
//

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

    /** Formats the URL (unused for WSU) */
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
            super.setRegistrationSearchPage(REGISTRATION_SEARCH_PAGE_WSU);
        }
        super.setRegistrationSearchPage(this.getRegistrationSearchPage() + paramToAdd);
    }

    /** Set the period/term to search in the URL */
    @Override
    public void setTermParameter(final String season, final String year) {
        String seasonName = "";
        switch (season) {
            case "Winter":
                seasonName = "3"; // same as Fall (no differentiation by WSU)
                break;
            case "Spring":
                seasonName = "1";
                break;
            case "Summer":
                seasonName = "2";
                break;
            case "Fall":
                seasonName = "3";
                break;
        }
        final String seasonFolder = seasonName + year;
        appendParameter(WSU_URL_PREFIX + seasonFolder);
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
                if(inputLine.contains(WSU_URL_PREFIX)) {
                    final String relativePath = parseData(WSU_DEPARTMENT_PATHS_MARKER_START,
                            WSU_DEPARTMENT_PATHS_MARKER_START.length(), WSU_DEPARTMENT_PATHS_MARKER_END, inputLine)
                            .getData();
                    // Extract department abbreviation from its relative path
                    final String departmentAbbr = parseData(WSU_DEPARTMENT_PATHS_MARKER_START,
                            WSU_DEPARTMENT_PATHS_MARKER_START.length(), WSU_DEPARTMENT_PATHS_MARKER_END , relativePath)
                            .getData();
                    if(userDepartments.contains(departmentAbbr)) {
                        departmentFolders.add(relativePath);
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

