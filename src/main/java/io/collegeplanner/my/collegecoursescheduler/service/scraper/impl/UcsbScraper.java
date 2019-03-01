package io.collegeplanner.my.collegecoursescheduler.service.scraper.impl;

import io.collegeplanner.my.collegecoursescheduler.model.dto.CourseSectionDto;
import io.collegeplanner.my.collegecoursescheduler.service.scraper.GenericScraper;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.*;


@Getter
@Setter
public class UcsbScraper extends GenericScraper {

    private Map<String, List<CourseSectionDto>> courseMap = new HashMap<>();
    private String desiredTerm;

    @Override
    public String getUniversityName() {
        return UNIVERSITY_OF_CALIFORNIA_SANTA_BARBARA;
    }

    // @Override
    public void iterateInput(final List<String> chosenCourseNames) throws Exception {

        // Set to remove duplicate departments ("CS-107", "CS-108", etc.)
        final Map<String, List<String>> userDepartments = new HashMap<>();
        for(final String courseName : chosenCourseNames) {
            final String dept = courseName.substring(0, courseName.indexOf(" "));
            if(!userDepartments.containsKey(dept))
                userDepartments.put(dept, new ArrayList<>());
            userDepartments.get(dept).add(courseName.replace(" ","").toUpperCase());
        }

        // Go to department page and extract the chosen courses
        final Iterator it = userDepartments.entrySet().iterator();
        while(it.hasNext()) {
            final Map.Entry pair = (Map.Entry)it.next();
            final String department = (String)pair.getKey();
            final List<String> courseList = (List<String>)pair.getValue();
            parseRegistrationData(department, courseList);
            it.remove();
        }
    }

    // TODO: parseRegistrationData(String course) shouldn't even be in the abstract class..
    @Override
    public void parseRegistrationData(final String course) {}

    public void parseRegistrationData(final String dept, final List<String> courseList) throws Exception {

        /** For Linux OS: */
        System.setProperty("webdriver.chrome.driver", super.getServerPath() + "/WEB-INF/etc/chromedriver_server");
        /** For Windows OS: */
        //System.setProperty("webdriver.chrome.driver", super.getServerPath() + "/WEB-INF/etc/chromedriver.exe");
        final ChromeOptions options = new ChromeOptions()
                .setHeadless(true);
        final WebDriver driver = new ChromeDriver(options);

        driver.get(REGISTRATION_SEARCH_PAGE_UCSB);

        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        final WebElement subjectArea = driver.findElement(By.id(UCSB_SUBJECT_AREA));
        final Select selectSubject = new Select(subjectArea);
        selectSubject.selectByValue(dept);

        final WebElement quarterList = driver.findElement(By.name(UCSB_QUARTER_LIST));
        final Select selectQuarter = new Select(quarterList);
        selectQuarter.selectByValue(this.desiredTerm);

        final WebElement courseLevel = driver.findElement(By.name(UCSB_COURSE_LEVELS));
        final Select selectLevel = new Select(courseLevel);
        selectLevel.selectByValue("All");

        final WebElement submitBtn = driver.findElement((By.id(UCSB_SUBMIT_BUTTON)));
        submitBtn.click();

        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        final Reader inputString = new StringReader(driver.getPageSource());
        final BufferedReader in = new BufferedReader(inputString);
        String inputLine;

        driver.quit();

        nextInput:
        while((inputLine = in.readLine()) != null) {
            final List<CourseSectionDto> tempList = new ArrayList<>();
            final String courseBundleID;
            final String courseBundleTitle;

            // Found a new course
            CourseSectionDto newCourse = new CourseSectionDto();

            // Store here for repeated use later
            final CourseSectionDto parentCourse = newCourse;
            boolean firstClassInBundle = true;

            searchForID:
            while((inputLine = in.readLine()) != null) {
                while(!inputLine.contains(UCSB_ID_MARKER)) {
                    continue searchForID;
                }
                break searchForID;
            }
            if(inputLine == null) {
                return;
            }


            // Now it contains CourseTitle (ID); next line is course (ex: "Art 1A")
            final String courseID = in.readLine().trim().replace(SPACE_CHARACTER, EMPTY_STRING);

            if(courseList.contains(courseID)) {
                courseBundleID = dept + " " + courseID.substring(dept.length());
                // Now seach for labelTitle (courseTitle)
                while (!inputLine.contains(UCSB_LABEL_TITLE_MARKER)) {
                    inputLine = in.readLine();
                }
                // Extract title
                int start = inputLine.indexOf(">") + 1;
                int end = inputLine.indexOf("</span");
                final String courseTitle = inputLine.substring(start, end).trim();
                courseBundleTitle = courseTitle;

                newCourse.setTitle(courseBundleTitle);
                newCourse.setCourseID(courseBundleID + " (LEC)");
                newCourse.setScheduleNum("[LECTURE]");
            }
            else {
                continue nextInput; // not the right bundle of courses
            }

            // If gets here, IS the right bundle of courses
            // This specific course will either be a lecture with sub-sections, or just a single course
            //    Add this one, then check for sections by seeing if the inputLine is another CourseHeader
            //TODO: change to do-while to take care of lectures vs. sessions

            int shadowBottomCount = 0;
            while(!(inputLine = in.readLine()).contains("Header Clickable")) {
                if(inputLine.contains("CourseInfoRow")) {

                    if(!firstClassInBundle)
                        newCourse.setParentCourse(parentCourse);

                    if(newCourse.isComplete()) {
                        if (!courseMap.containsKey(newCourse.getCourseID()))
                            courseMap.put(newCourse.getCourseID(), new ArrayList<>());
                        courseMap.get(newCourse.getCourseID()).add(newCourse);
                    }

                    newCourse = new CourseSectionDto();
                    newCourse.setTitle(courseBundleTitle);
                    newCourse.setCourseID(courseBundleID);
                    shadowBottomCount = 0;
                    firstClassInBundle = false;

                    // Check if is a new block of classes
                    inputLine = in.readLine();
                    if(inputLine.contains("Header Clickable")) {
                        continue nextInput;
                    }
                }

                //indicates end of enrollment <td></td> (which is a nightmare)
                if(inputLine.contains("shadowbottom")) {

                    int tableDataCount = 0;
                    while(tableDataCount != 3) {
                        inputLine = in.readLine();
                        if(inputLine.contains("<td")) tableDataCount++;
                    }
                    // enrollmentCode is on next line
                    inputLine = in.readLine();
                    int start = inputLine.indexOf(">") + 1;
                    int end = inputLine.indexOf("</a>");
                    String enrollCode = inputLine.substring(start, end).trim();
                    if(enrollCode.length() > 2)
                        newCourse.setScheduleNum(enrollCode);

                    while(shadowBottomCount != 1) {
                        inputLine = in.readLine();
                        if(inputLine.contains("shadowbottom")) shadowBottomCount++;
                    }
                    // Find <td> for professor
                    while(!(inputLine = in.readLine()).contains("<td")) continue;

                    // Professor is on the next line
                    inputLine = in.readLine().trim();
                    if(inputLine.length() > 2) {
                        String professor = inputLine.substring(0, inputLine.indexOf("<br")).trim();
                        newCourse.getInstructors()
                                .add(professor);
                    }

                    // Find next "<td" (Days)
                    while(!(inputLine = in.readLine()).contains("<td")) continue;
                    inputLine = in.readLine();
                    String days = inputLine.trim().replace(" ","");
                    newCourse.getDays()
                            .add(days);

                    // Find next "<td" (Times)
                    while(!(inputLine = in.readLine()).contains("<td")) continue;
                    // 3 more lines down
                    inputLine = in.readLine();
                    try {
                        String timesWithMark = inputLine.trim().replace(" ", "").replace(":", "");
                        String times = timesWithMark.replaceAll("[a-zA-Z]+", "");

                        int startTime = Integer.parseInt(times.substring(0, times.indexOf("-")));
                        startTime = (timesWithMark.substring(0, timesWithMark.indexOf("-")).contains("pm") && startTime < 1200) ? startTime + 1200 : startTime;
                        int endTime = Integer.parseInt(times.substring(times.indexOf("-") + 1));
                        endTime = (timesWithMark.substring(timesWithMark.indexOf("-") + 1).contains("pm") && endTime < 1200) ? endTime + 1200 : endTime;

                        // Change format from "900-950" to "0900-0950"
                        String startTimeString = (startTime < 1000) ? "0" + Integer.toString(startTime) : Integer.toString(startTime);
                        String endTimeString = (endTime < 1000) ? "0" + Integer.toString(endTime) : Integer.toString(endTime);

                        times = startTimeString + "-" + endTimeString;

                        newCourse.getTimes().add(times);
                    }
                    catch(StringIndexOutOfBoundsException e) {
                        // No associated times for this course
                        e.printStackTrace();
                    }


                    // Find next "<td" (Location)
                    while(!(inputLine = in.readLine()).contains("<td")) continue;
                    // Next line down
                    inputLine = in.readLine();
                    String location = inputLine.trim();
                    newCourse.getLocations().add(location);

                    // Find next "<td" (numSeats)
                    while(!(inputLine = in.readLine()).contains("<td")) continue;
                    // Next line down
                    inputLine = in.readLine();
                    String numSeats = inputLine.trim().replace(" ", "");
                    if(numSeats.contains("/")) {
                        int numEnrolled = Integer.parseInt(numSeats.substring(0, numSeats.indexOf("/")));
                        int maxAllowed = Integer.parseInt(numSeats.substring(numSeats.indexOf("/") + 1));
                        numSeats = (maxAllowed - numEnrolled) + "/" + maxAllowed;
                    }

                    newCourse.setSeats(numSeats);

                    // Find end of row
                    while(!(inputLine = in.readLine()).contains("</tr>")) continue;

                    /** TEMP */
                    // TODO: wtf is this
                    newCourse.setUnits("3[temp]");

                }

            }

        }
        in.close();
    }

    /** Format string of days into usable array */
    @Override
    public int[] convDaysToArray(String days) {
        int[] res = new int[5];
        if(days.contains("M")) res[0] = 1;
        if(days.contains("T")) res[1] = 1;
        if(days.contains("W")) res[2] = 1;
        if(days.contains("R")) res[3] = 1;
        if(days.contains("F")) res[4] = 1;
        return res;
    }

    public void createSizeSortedCourses() {
        // List<List<Course>> sizeSortedCourses
        Iterator it = courseMap.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            List<CourseSectionDto> courseList = (List<CourseSectionDto>)pair.getValue();
            super.getSizeSortedCourses().add(courseList);
        }
        Collections.sort(super.getSizeSortedCourses(), (Comparator<List>) (a1, a2) -> {
            return a1.size() - a2.size();
        });
    }

    public void appendParameter(String addParam) {
        return;
    }

    @Override
    public void setTerm(String season, String year) {
        String seasonNumber = "";
        switch (season) {
            case "Winter":
                seasonNumber = "1";
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
        }
        setDesiredTerm(year + seasonNumber);
    }

    public URL supplySearchUrl(String s) throws MalformedURLException {
        return null;
    }

    public String formatURL(String url) {
        return null;
    }

    /** Rowspan formula */
    @Override
    public int rowspanFormula(int startHour, int startMin, int endHour, int endMin) {
        return ((((endHour * 60) + endMin) - ((startHour * 60) + startMin)) / 15);
    }
}
