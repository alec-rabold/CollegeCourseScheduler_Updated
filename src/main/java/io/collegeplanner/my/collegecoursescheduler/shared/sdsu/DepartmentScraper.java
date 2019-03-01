package io.collegeplanner.my.collegecoursescheduler.shared.sdsu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

/**
 * Scans through the HTML of SDSU's departments page to find "subjectCode" and "subjectTitle"
 * and stores them in a TreeMap
 *
 * SubjectCode is the department's abbreviation (ex. CS). This abbreviation is used
 * in the HTML 'GET' for while searching through classes
 *
 * subjectTitle is the department's full name (ex. Computer Science)
 *
 * Storing these values in a TreeMap so that they are sorted and iterated
 * alphabetically in io.collegeplanner.my.shared.GenericScraper.java, and so that it prints easily
 * to the console to show the user what department abbreviations are
 *
 * @return sorted TreeMap of department abbreviations and full names
 */

public class DepartmentScraper {

    private final String DEPARTMENTS_PAGE = "https://sunspot.io.collegeplanner.my.shared.sdsu.edu/schedule/search?mode=browse_by_department&category=browse_by_department";
    private URL Dept_URL;
    private TreeMap<String, String> departmentMap;

    protected DepartmentScraper() {
        try {
            Dept_URL = new URL(DEPARTMENTS_PAGE);
            departmentMap = this.retrieveDepartments();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public TreeMap<String,String> retrieveDepartments() throws IOException {
        TreeMap<String, String> deptMap = new TreeMap<String, String>();
        String deptAbbrev, deptFull;

        String inputLine;
        BufferedReader in = new BufferedReader(new InputStreamReader(Dept_URL.openStream()));

        while((inputLine = in.readLine()) != null) {
            // "subjectCode" == Department Abbreviation
            int indexStart = inputLine.indexOf("subjectCode") + 13; // where the text starts in the HTML
            int indexEnd = inputLine.indexOf(":</div>"); // where the HTML text ends
            if(indexStart - 13 != -1) { // -1 indicates not found, subtracting the 13 above
                deptAbbrev = inputLine.substring(indexStart, indexEnd);

                // "subjectTitle" == Department Abbreviation   
                indexStart = inputLine.indexOf("subjectTitle") + 14;
                indexEnd = inputLine.indexOf("</div></a>");
                deptFull = inputLine.substring(indexStart, indexEnd);
                        
                // add both values to the TreeMap
                deptMap.put(deptAbbrev, deptFull);
            }
        }
        return deptMap;
    }

    /**
     * Prints a formatted list of SDSU's departments and their abbreviations
     */
    protected void printDepartments() {
        System.out.println("\nAbbrev.\t|  Department Name");
        System.out.println("--------+-------------------------------------");
        for(Map.Entry<String, String> entry : departmentMap.entrySet()) {
            System.out.println(entry.getKey() + "\t|  " + entry.getValue());
        }
        System.out.println("--------+--------------------------------------\n");
    }

    /**
     * Retrieves the department TreeMap
     *
     * @return dept TreeMap
     */
    protected TreeMap<String, String> getDepartmentMap() {
        return this.departmentMap;
    }

}