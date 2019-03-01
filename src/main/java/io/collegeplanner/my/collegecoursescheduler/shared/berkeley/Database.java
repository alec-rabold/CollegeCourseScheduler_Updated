package io.collegeplanner.my.collegecoursescheduler.shared.berkeley;

import com.google.common.collect.ImmutableMap;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;

public class Database {


    public static void main(String[] args) {
        Database db = new Database();
        db.refreshDatabase();
    }

    private class CourseData {
        String name;
        String title;
        List<String> IDs;
        public CourseData(String name, String title, List<String> IDs) {
            this.name = name;
            this.title = title;
            this.IDs = IDs;
        }
    }


    private void refreshDatabase() {
        String departmentsPage = "http://guide.berkeley.edu/courses/";
        String coursesPage = "https://classes.berkeley.edu/search/class/?f%5B0%5D=im_field_term_name%3A831&page=";
        Map<String, String> terms = ImmutableMap.<String, String>builder()
                .put("SPRING_2019", "https://classes.berkeley.edu/search/class/?f%5B0%5D=im_field_term_name%3A831&page=")
                .put("FALL_2018", "https://classes.berkeley.edu/search/class/?f%5B0%5D=im_field_term_name%3A789&page=")
                .build();
        String professorsPage = "https://classes.berkeley.edu/search/class";
        Map<String, CourseData> courseMap = new LinkedHashMap<>(); // courseName, scheduleNumbers
        List<String> departmentsLinks = new ArrayList<>();
        List<String> departmentsFormatted = new ArrayList<>();

        // Parse HTML
        try {
            /** Courses, CourseIDs, and Departments */
            String inputLine;
            BufferedReader in;
            PrintWriter courseMap_DB = new PrintWriter(new File("berkeley_courseMap_DB.txt"), "UTF-8");
            PrintWriter courses_DB = new PrintWriter(new File("berkeley_courses_DB.txt"), "UTF-8");
            PrintWriter departments_DB = new PrintWriter(new File("berkeley_departments_DB.txt"), "UTF-8");
            PrintWriter courseID_DB = new PrintWriter(new File("berkeley_courseID_DB.txt"), "UTF-8");

            int pageNum = 0;
            loop:
            while(true) {
                System.out.println(pageNum);
                in = new BufferedReader(new InputStreamReader(new URL(coursesPage + (pageNum++)).openStream()));
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.contains("Check if your spelling is correct")) break loop;
                    if (inputLine.contains("data-json")) {
                        String data = inputLine.substring(inputLine.indexOf("{"), inputLine.length() - 1);
                        JSONObject node = new JSONObject(data);

                        String courseName = node.getJSONObject("class").getJSONObject("course").getString("displayName");
                        String courseTitle = node.getJSONObject("class").getJSONObject("course").getString("title").replace("&amp;","&");
                        String schedNum = Integer.toString(node.getInt("id"));

                        if(!courseMap.containsKey(courseName))
                            courseMap.put(courseName, new CourseData(courseName, courseTitle, new ArrayList<String>()));
                        courseMap.get(courseName).IDs.add(schedNum);
                    }
                }
                // in.close();
            }
            in.close();

            // Print to txt file
            Iterator it = courseMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                CourseData data = (CourseData)pair.getValue();

                courseMap_DB.print(data.name + " | " + data.title + " | ");
                List<String> schedList = data.IDs;

                for(String schedNum : schedList) {
                    courseMap_DB.print(schedNum + ",");
                }
                courseMap_DB.println();

                it.remove(); // avoids a ConcurrentModificationException
            }

            courseMap_DB.close();

            /** Departments */
            in = new BufferedReader(new InputStreamReader(new URL(departmentsPage).openStream()));
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains("/courses/") && !inputLine.contains("Course Catalog")) {
                    int start = inputLine.indexOf("/courses/") + 9;
                    int end = inputLine.indexOf("/", start);
                    String dept = inputLine.substring(start, end);
                    departmentsLinks.add(dept);
                }
            }
            in.close();

            for (String dept : departmentsLinks) {
                String formattedDept = dept.replace("_", " ").toUpperCase();
                departmentsFormatted.add(formattedDept);
                departments_DB.println(formattedDept);

                in = new BufferedReader(new InputStreamReader(new URL(departmentsPage + dept + "/").openStream()));
                while ((inputLine = in.readLine()) != null) {
                    if(inputLine.contains("<span class=\"code\"")) {
                        /** CourseID */
                        int start = inputLine.indexOf("code") + 6;
                        int end = inputLine.indexOf("</span>");
                        String courseName = (inputLine.substring(start, end)).replace("&amp;","&").replace("&quot;","\"").replace("&lt;","<").replace("&gt;",">").replace("&#039;","'").replace("&#160;"," ");
                        courseID_DB.println(courseName);

                        inputLine = in.readLine();

                        /** Course Title */
                        start = inputLine.indexOf("<span>") + 21;
                        end = inputLine.indexOf("</span>");
                        String courseTitle = (inputLine.substring(start, end)).replace("&amp;","&").replace("&quot;","\"").replace("&lt;","<").replace("&gt;",">").replace("&#039;","'").replace("&#160;"," ");
                        courses_DB.println(courseName + " | " + courseTitle);
                    }
                }
            }

            in.close();
            courses_DB.close();
            departments_DB.close();
            courseID_DB.close();

            /** Professors */
            in = new BufferedReader(new InputStreamReader(new URL(professorsPage).openStream()));
            PrintWriter professors_DB = new PrintWriter(new File("berkeley_professors_DB.txt"), "UTF-8");
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains("f[0]=sm_instructors")) {
                    int fromIndex = inputLine.indexOf("facetapi-link--");
                    int start = inputLine.indexOf("\">", fromIndex) + 2;
                    int end = inputLine.indexOf("(", fromIndex);
                    String professor = (inputLine.substring(start, end)).replace("&#039;","'").replace("&amp;","&").replace("&quot;","\"").replace("&lt;","<").replace("&gt;",">");
                    professors_DB.println(professor);
                }
            }

            in.close();
            professors_DB.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
