package io.collegeplanner.my.collegecoursescheduler.shared.UCSB;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Database {
    // TODO: Collect all data
    PrintWriter id_and_course_DB = new PrintWriter(new File("UCSB_id_and_course_DB.txt"), "UTF-8");
    PrintWriter professors_DB = new PrintWriter(new File("UCSB_professors_DB.txt"), "UTF-8");

    public Database() throws Exception {}

    public static void main(String[] args) throws Exception {
        Database db = new Database();
        db.refreshDB();
    }

    protected void refreshDB() throws Exception {
        Set<String> departmentURLs = getDeptURLs();
        for(String link : departmentURLs) {
            scrapeCourses(link);
            scrapeProfessors(link);
        }
        id_and_course_DB.close();
        professors_DB.close();
    }

    private Set<String> getDeptURLs() throws Exception {
        URL departmentPage = new URL("https://my.sa.ucsb.edu/catalog/Current/UndergraduateDegreeList.aspx");

        String inputLine;
        Set<String> deptURLs = new HashSet<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(departmentPage.openStream()));

        searchForURL:
        while((inputLine = in.readLine()) != null) {
            if(inputLine.contains("<td style=\"height: 13px;\">")) {
                // Does not contain a (correct) link
                if(!inputLine.contains("href") || inputLine.contains("<h4>")) continue searchForURL;
                // Does contain a link
                int start = inputLine.indexOf("href") + 6;
                int end = inputLine.indexOf("aspx") + 4;
                String link = inputLine.substring(start, end);
                deptURLs.add(link);
            }
        }
        return deptURLs;
    }

    private void scrapeCourses(String link) throws Exception {
        URL coursesPage = new URL("https://my.sa.ucsb.edu/catalog/Current/" + link + "?DeptTab=Courses");

        String inputLine;
        List<String> deptURLs = new ArrayList<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(coursesPage.openStream()));

        while((inputLine = in.readLine()) != null) {
            if(inputLine.contains("<div class=\"CourseDisplay\">")) {
                in.readLine();
                inputLine = in.readLine();

                String courseID = inputLine.substring(0, inputLine.indexOf(".")).trim().replaceAll(" +", " ");
                String courseTitle;

                // Undergrad courses
                if(inputLine.contains("class=\"CourseFullTitle\"")) {
                    inputLine = in.readLine();
                    courseTitle = inputLine.trim().replaceAll(" +", " ");
                }
                // Upper div courses
                else {
                    courseTitle = inputLine.substring(inputLine.indexOf(".") + 1).trim().replaceAll(" +", " ");
                }

                // Print to file
                id_and_course_DB.println(courseID + " | " + courseTitle);
            }
        }
    }

    private void scrapeProfessors(String link) throws Exception {
        URL coursesPage = new URL("https://my.sa.ucsb.edu/catalog/Current/" + link + "?DeptTab=Faculty");

        String inputLine;
        List<String> deptURLs = new ArrayList<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(coursesPage.openStream()));

        while((inputLine = in.readLine()) != null) {
            if(inputLine.contains("FacultyDisplay")) {
                inputLine = in.readLine();
                int start = inputLine.indexOf("<b>") + 3;
                int end = inputLine.indexOf("</b>");
                String professor = inputLine.substring(start, end);
                professors_DB.println(professor);
            }
        }
    }


}
