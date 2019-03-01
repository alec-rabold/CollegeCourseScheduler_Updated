package io.collegeplanner.my.collegecoursescheduler.shared.UW;

import io.collegeplanner.my.collegecoursescheduler.util.ParserMetadata;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Database {
    // TODO: Collect all data
    PrintWriter id_and_course_DB = new PrintWriter(new File("UW_id_and_course_DB.txt"), "UTF-8");
    PrintWriter professors_DB = new PrintWriter(new File("UW_professors_DB.txt"), "UTF-8");

    private String registrationAddress = "http://www.washington.edu/students/timeschd/";

    public Database() throws Exception {}

    public static void main(String[] args) throws Exception {
        Database db = new Database();
        db.refreshDB();
    }

    protected void refreshDB() throws Exception {
        setRegistrationTerm("WIN2019");
        final Set<String> departmentFolders = getDepartmentFolders();
        /** DEBUG */
        int progressCounter = 0;
        /***/
        for(final String folder : departmentFolders) {
            System.out.println(++progressCounter + "/" + departmentFolders.size());
            scrapeCourses(folder);
            scrapeProfessors(folder);
        }
        id_and_course_DB.close();
        professors_DB.close();
    }

    private void scrapeCourses(final String folder) throws Exception {
        /** DEBUG */
        System.out.println(folder);
        /**
        if(folder.contains("uconjoint")){
            System.out.println("DEBUG HERE");
        }
        else {
            return;
        }
         */

        final URL coursesPage = new URL(registrationAddress + "/" + folder);
        final List<String> deptURLs = new ArrayList<>();
        final BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(coursesPage.openStream()));
        }
        catch(final FileNotFoundException e) {
            // there's at least one bad link on UW's page ("egypt.html")
            // skip to next department
            return;
        }
        final String COURSE_ID_STRING = "<A NAME=";
        String inputLine;


        while((inputLine = in.readLine()) != null) {
            try {
                inputLine = in.readLine();

                searchForID:
                while ((inputLine = in.readLine()) != null) {
                    while (!inputLine.contains(COURSE_ID_STRING)) {
                        continue searchForID;
                    }
                    break searchForID;
                }
                // Unformatted Course ID
                final String courseID_unf = parseData(COURSE_ID_STRING, COURSE_ID_STRING.length(), ">", inputLine).getData().toUpperCase();
                // CSE143
                final StringBuilder hyphenatedValue = new StringBuilder(courseID_unf);
                int i = 0;
                // Insert hyphen between last alphabetical & first digit (e.g. "CSE143" --> "CSE-143")
                while (!Character.isDigit(courseID_unf.charAt(i))) {
                    i++;
                }
                hyphenatedValue.insert(i, "-");
                // CSE-143 (formatted Course ID)
                final String courseID = hyphenatedValue.toString();
                final String courseTitle = parseData(("#" + courseID_unf.toLowerCase()), courseID_unf.length() + 2, "</A>", inputLine).getData();

                // Print to file
                id_and_course_DB.println(courseID + " | " + courseTitle);
            }
            catch(final NullPointerException e) {
                // Do nothing
            }
            catch(final Exception e) {
                // Usually just null pointer exception from above nested while loop
                System.out.println(inputLine);
            }
        }
    }

    private void scrapeProfessors(String link) throws Exception {
      /**
     `    * TBD
         *
        URL coursesPage = new URL("https:// my.sa.ucsb.edu/catlog/Current/" + link + "?DeptTab=Faculty");

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
         */
    }




    private ParserMetadata parseData(final String indexStartChar, int startOffset, final int numCharsToParse, final String inputLine) {
        final int indexStart = inputLine.indexOf(indexStartChar) + startOffset;
        final int indexEnd = indexStart + numCharsToParse;
        return parseData(indexStart, indexEnd, inputLine);
    }
    private ParserMetadata parseData(final String indexStartChar, int startOffset, final String indexEndChar, final String inputLine) {
        final int indexStart = inputLine.indexOf(indexStartChar) + startOffset;
        final int indexEnd = inputLine.indexOf(indexEndChar, indexStart); // starts the search from indexStart
        return parseData(indexStart, indexEnd, inputLine);
    }
    private ParserMetadata parseData(final int indexStart, final int indexEnd, final String inputLine) {
        final String value = inputLine.substring(indexStart, indexEnd).trim();
        return new ParserMetadata(value, indexStart, indexEnd);
    }

    /** Gets unique HTML addresses for each chosen department */
    private Set<String> getDepartmentFolders() {
        final Set<String> departmentFolders = new TreeSet<>();

        // URL should already be formatted by this point..
        // Parse HTML
        try(BufferedReader in = new BufferedReader(new InputStreamReader(getRegistrationURL().openStream()))) {
            String inputLine;

            while((inputLine = in.readLine()) != null) {
                // Find all page-links
                // TODO: remove hard-coded check for CCS
                if(inputLine.contains("<a href=\"") && inputLine.contains("(") && inputLine.contains(")") && !inputLine.contains("(CCS)")) {
                    // Find course abbreviation inside parentheses
                    final String courseAbbr = parseData("(", 1, ")", inputLine).getData();
                    final String folder = parseData("<a href=\"", 9, "\">", inputLine).getData();
                    if(!folder.contains("#")) departmentFolders.add(folder);
                }
                else continue;
            }
        }
        catch(Exception e){
            System.out.println("Oh fuck");
        }


        return departmentFolders;
    }

    private void setRegistrationTerm(final String term) {
        this.registrationAddress += term;
    }

    private URL getRegistrationURL() throws MalformedURLException {
        return new URL(registrationAddress);
    }
    private URL getRegistrationURL(final String department) throws MalformedURLException {
         return new URL(registrationAddress + "/" + department);
    }
    private URL getRegistrationURL(final String department, final String subject) throws MalformedURLException {
        return new URL(registrationAddress + "/" + department + "/" + subject);
    }



}
