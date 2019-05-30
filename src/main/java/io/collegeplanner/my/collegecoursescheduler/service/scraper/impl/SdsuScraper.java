package io.collegeplanner.my.collegecoursescheduler.service.scraper.impl;

import io.collegeplanner.my.collegecoursescheduler.model.dto.CourseSectionDto;
import io.collegeplanner.my.collegecoursescheduler.service.scraper.GenericScraper;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.REGISTRATION_SEARCH_PAGE_SDSU;
import static io.collegeplanner.my.collegecoursescheduler.util.Constants.SAN_DIEGO_STATE_UNIVERSITY;

@Log4j2
public class SdsuScraper extends GenericScraper {
    private Map<String, List<CourseSectionDto>> departments = new TreeMap<>();

    @Override
    public String getUniversityName() {
        return SAN_DIEGO_STATE_UNIVERSITY;
    }

    // @Override
    public void iterateInput(final List<String> chosenCourseNames) {
        try {
            super.setNumChosenCourses(chosenCourseNames.size());

            final Set<String> userDepartments = new HashSet<>();
            // Set to remove duplicate departments ("CS-107", "CS-108", etc.)
            for (final String courseName : chosenCourseNames) {
                final String dept = courseName.substring(0, courseName.indexOf("-"));
                userDepartments.add(dept);
            }
            // Go to department page and extract the chosen courses
            for (final String dept : userDepartments) {
                parseRegistrationData(dept);
            }
        } catch(final Exception e) {
            log.fatal("Issue iterating chosen course input", e);
        }
    }

    @Override
    public void parseRegistrationData(String department) throws Exception {

        // Format the URL
        supplySearchUrl(department);

        // Store chosen courses in list
        List<CourseSectionDto> tempList = new ArrayList<>();
        CourseSectionDto temp = new CourseSectionDto();

        // Parse HTML
        try(final BufferedReader in = new BufferedReader(new InputStreamReader(getRegistration_URL().openStream()))) {
            String inputLine, value;

            while((inputLine = in.readLine()) != null) {
                updateCount(inputLine);

                /** Course ID */
                if(inputLine.contains("<a href=\"sectiondetails") && !inputLine.contains("footnote")) {
                    // Check that class has all attributes, then add the course & reset it
                    if(temp.isComplete()) {
                        tempList.add(temp);
                        temp = new CourseSectionDto();
                    }
                    // If a parsed course doesn't have all the required attributes (ex. time/day), then ignore it
                    else temp = new CourseSectionDto();

                    // Location of data inside HTML tags
                    int indexStart = inputLine.indexOf("\">") + 2;
                    int indexEnd = inputLine.indexOf("</a>");
                    value = inputLine.substring(indexStart, indexEnd);

                    // Check if this course is a chosen course
                    if(super.getUserOptions().getChosenCourseNames().contains(value))
                        temp.setCourse(value);
                    else {
                        continue;
                    }
                }

                /** ScheduleDto number */
                else if(inputLine.contains("sectionFieldSched")) {
                    value = parseSection(inputLine);
                    if(!value.equals("Sched #") && (value.matches(".*\\d+.*") || value.contains("***")))
                        temp.setSchedNum(value);
                }

                /** Course title  */
                else if(inputLine.contains("sectionFieldTitle")) {
                    value = parseSection(inputLine);
                    if(!value.equals("Course Title") && value.matches(".*[a-zA-Z]+.*"))
                        temp.setTitle(value);
                }

                /** Number of units */
                else if(inputLine.contains("sectionFieldUnits")) {
                    value = parseSection(inputLine);
                    if(!value.equals("Units") && value.matches(".*\\d+.*"))
                        temp.setUnits(value);
                }

                /** List of course times */
                else if(inputLine.contains("sectionFieldTime")) {
                    value = parseSection(inputLine);
                    if((!value.equals("Time")) && value.matches(".*\\d+.*"))
                        temp.getTimes().add(value);
                }

                /** List of course days */
                else if(inputLine.contains("sectionFieldDay")) {
                    value = parseSection(inputLine);
                    if(!value.equals("Day") && value.matches(".*[a-zA-Z]+.*") && !value.equals("Arranged"))
                        temp.getDays().add(value);
                }

                /** List of locations */
                else if(inputLine.contains("sectionFieldLocation") && !inputLine.contains(">Location<")) {
                    if((inputLine = in.readLine()).contains("<a")){
                        updateCount(inputLine);
                        inputLine = in.readLine(); // Workaround; HTML data is on the 3rd line due to inconsistent formatting on WebPortal
                        updateCount(inputLine);
                        value = inputLine.trim();
                    }
                    // Not all locations have surrounding <a>DATA</a> tags
                    else {
                        inputLine = in.readLine();
                        updateCount(inputLine);
                        value = inputLine.trim();
                    }
                    // If it's a hybrid class, set to location to "ONLINE/{location}"
                    if(value.contains("ON-LINE")) temp.getDays().clear(); // inconsistency in WebPortal (again)
                    if(temp.getLocations().contains("ON-LINE") && !value.contains("ON-LINE")) {
                        temp.getLocations().clear();
                        temp.getLocations().add("ON-LINE/" + value);
                    }
                    // If it's a regular class, add the location
                    else if(value.length() > 2) temp.getLocations().add(value);
                }

                /** Number of available seats */
                else if(inputLine.contains("sectionFieldSeats") && !inputLine.contains(">Seats Open<")) {
                    boolean seatsFound = false;
                    while(!seatsFound) {
                        inputLine = in.readLine();
                        if(inputLine.contains("Waitlisted")) {
                            // inputLine: "0/80<br><span id="statusValues">Waitlisted:9</span>"
                            int indexStart = inputLine.indexOf(":") + 1;
                            int indexEnd = inputLine.indexOf("</span>");
                            String numWaitlisted = inputLine.substring(indexStart, indexEnd);

                            inputLine = inputLine.trim();
                            indexStart = inputLine.indexOf("/");
                            indexEnd = inputLine.indexOf("<br>");
                            String totalSpots = inputLine.substring(indexStart, indexEnd);
                            value = "-" + numWaitlisted + totalSpots;
                            temp.setSeats(value);
                            seatsFound = true;
                        }
                        else if(inputLine.contains("/") && !(inputLine.contains("<"))) {
                            value = inputLine.trim();
                            temp.setSeats(value);
                            seatsFound = true;
                        }
                    }
                }

                /** List of instructors */
                else if(inputLine.contains("sectionFieldInstructor") && !inputLine.contains(">Instructor<")) {
                    for(int i = 0; i < 3; i++) {
                        inputLine = in.readLine();
                        updateCount(inputLine);
                        if(inputLine.contains("<a href=\"search?mode=search&instructor")) {
                            int indexStart = inputLine.indexOf("\">") + 2;
                            int indexEnd = inputLine.indexOf("</a>");
                            value = inputLine.substring(indexStart, indexEnd);
                            if(!value.equals("Instructor") && value.matches(".*[a-zA-Z]+.*"))
                                temp.getInstructors().add(value);
                        }
                    }
                }
            }
            // Add to departments instance variable
            departments.put(department, tempList);
        }
        catch(NullPointerException e) {
            log.error("Error in parsing data for SDSU", e);
            System.out.println("NullPointerException");
        }

    }

    /** Sets the department URL to parse */
    @Override
    public URL supplySearchUrl(final String department) throws MalformedURLException {
        String formURL;
        if(this.getParameters() != null)
            formURL = REGISTRATION_SEARCH_PAGE_SDSU + "&abbrev=" + department + this.getParameters();
        else
            formURL = REGISTRATION_SEARCH_PAGE_SDSU + "&abbrev=" + department;
        String searchURL = formatURL(formURL);
        this.setRegistration_URL(new URL(searchURL));

        return null; // temp
    }

    /** Formats the URL */
    @Override
    public String formatURL(String url) {
        StringBuilder newURL = new StringBuilder();

        for(int i = 0; i < url.length(); i++) {
            if(url.charAt(i) == ' ')
                newURL.append('+');
            else
                newURL.append(url.charAt(i));
        }
        return newURL.toString();
    }

    /** Format string of days into usable array */
    @Override
    public int[] convDaysToArray(String days) {
        int[] res = new int[5];
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
        // Add to list of all permutable courses
        for(int i = 0; i < super.getNumChosenCourses(); i++) {
            String tCourse = super.getUserOptions().getChosenCourseNames().get(i);
            List<CourseSectionDto> tList = new ArrayList<>();
            String deptSubString = tCourse.substring(0,tCourse.indexOf("-"));
            for(String dept : departments.keySet()) {
                if(dept.equals(deptSubString)) {
                    List<CourseSectionDto> departmentCourses = departments.get(dept);
                    for(int j = 0; j < departmentCourses.size(); j++) {
                        CourseSectionDto entry = departmentCourses.get(j);
                        if(tCourse.equals(entry.getCourse())) tList.add(entry);
                    }
                }
            }
            // countIndexedCourses.put(i, tList);
            if(!tList.isEmpty()) {
                getSizeSortedCourses().add(tList);
            }
        }
        Collections.sort(super.getSizeSortedCourses(), (Comparator<List>) (a1, a2) -> {
            return a1.size() - a2.size();
        });
    }

    /** Rowspan formula */
    @Override
    public int rowspanFormula(int startHour, int startMin, int endHour, int endMin) {
        return ((((endHour * 60) + endMin) - ((startHour * 60) + startMin)) / 15);
    }

    /** Set the period/term to search in the URL */
    @Override
    public void setTermParameter(final String season, final String year) {
        final String termParameter = "&period=";
        final String seasonNumber;

        checkIfParametersContainsString(termParameter);

        switch (season) {
            case "Winter":
                seasonNumber = "2";
                break;
            case "Spring":
                seasonNumber = "2";
                break;
            case "Summer":
                seasonNumber = "3";
                break;
            case "Fall":
                seasonNumber = "4";
                break;
            default:
                seasonNumber = null;
        }
        String addParam = termParameter + year + seasonNumber;
        appendParameter(addParam);
    }

    /**** --------------------  *****
     *****    Private Methods    *****
     ***** --------------------  ****/

    /** Extracts data from HTML tags */
    private String parseSection(String inputLine) {
        int indexStart = inputLine.indexOf("column\">") + 8;
        int indexEnd = inputLine.indexOf("</div>");
        String value = inputLine.substring(indexStart, indexEnd);
        return value;
    }

    /** Accounts for courses with lecture, activity, lab, etc. sessions */
    private void updateCount(String inputLine) {
        if(inputLine.contains("sectionRecordEven") || inputLine.contains("sectionRecordOdd"))
            this.setSectionMeetingCounter(0);
    }

    /** Append params to the search URL */
    private void appendParameter(String addParam) {
        if(this.getParameters() != null)
            this.setParameters(this.getParameters() + addParam);
        else {
            this.setParameters(addParam);
        }
    }
}
