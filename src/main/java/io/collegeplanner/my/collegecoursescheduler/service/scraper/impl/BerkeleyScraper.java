package io.collegeplanner.my.collegecoursescheduler.service.scraper.impl;

import io.collegeplanner.my.collegecoursescheduler.model.dto.CourseSectionDto;
import io.collegeplanner.my.collegecoursescheduler.service.scraper.GenericScraper;
import io.collegeplanner.my.collegecoursescheduler.util.GenericUtils;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.REGISTRATION_SEARCH_PAGE_BERKELEY;
import static io.collegeplanner.my.collegecoursescheduler.util.Constants.UNIVERSITY_OF_CALIFORNIA_BERKELEY;

@Log4j2
public class BerkeleyScraper extends GenericScraper {

    // temp
    private long timerStart;

    private Map<String, List<CourseSectionDto>> courseSectionsMap = new HashMap<>();

    @Override
    public String getUniversityName() {
        return UNIVERSITY_OF_CALIFORNIA_BERKELEY;
    }

    @Override
    // todo: iterable instead of list (?)
    public void iterateInput(final List<String> chosenClasses) throws ExecutionException, InterruptedException {

        final List<String> cleanedChoseClasses = new ArrayList<>();
        // TODO: use Stream API
        for(final String classIDs : chosenClasses) {
            int start = 0;
            int position = classIDs.indexOf(",");
            while(position != -1) {
                String ID = classIDs.substring(start, position);
                start = position + 1;
                position = classIDs.indexOf(",", start);
                cleanedChoseClasses.add(ID);
            }
        }
        /**
        String[] toArray = new String[allIDs.size()];
        toArray = allIDs.toArray(toArray);
        final String[] schedNum = toArray;
         */

        final CompletableFuture<List<JSONObject>> future = new CompletableFuture<>();
        this.timerStart = System.currentTimeMillis();
        for(final String chosenClass : cleanedChoseClasses){
            future.supplyAsync(() -> supplyJsonResponseFromCourseID(chosenClass));
        }
        future.get().forEach(this::parseRegistrationData);


        /**
        final ExecutorService executor = Executors.newFixedThreadPool(4);
        for (int i = 0; i < schedNum.length; i++) {
            int finalI = i; // effectively final for Future API
            executor.execute(() -> {
                // Begin analyzing process
                try {
                    parseRegistrationData(schedNum[finalI]);
                } catch (final Exception e) {
                    log.error("Error parsing Berkeley registration data", e);
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {e.printStackTrace();}
         */
    }

    @Override
    public void parseRegistrationData(final String empty){}
    public void parseRegistrationData(final JSONObject obj) {
        /**
        // Format the URL
        // TODO: this is confusingly weird.. shouldn't be setting the search URL as a constant. Should just build off a base URL
        supplySearchUrl(schedNum);
         */

        // Parse json
        try {
            // Get course and all associated discussions, labs, etc.
            long timer2 = System.currentTimeMillis();
            // String jsonString = readUrl(super.getRegistration_URL());
            long t2Time = System.currentTimeMillis() - timer2;
            float bench2 = t2Time / 1000.0f;
            System.out.print("readUrl() time: ");
            System.out.format("%.3f", bench2);
            System.out.println();

            // JSONObject obj = new JSONObject(jsonString);


            JSONArray nodes = obj.optJSONArray("nodes");
            for(int i = 0 ; i < nodes.length() ; i++) {
                CourseSectionDto curCourse = new CourseSectionDto();

                JSONObject curNode = nodes.optJSONObject(i).optJSONObject("node");
                // Remove the backslashes and create JSON object
                String data = curNode.optString("json").replace("\\", "");
                JSONObject classData = new JSONObject(data);

                if(!classData.getJSONObject("class").getJSONObject("session").getJSONObject("term").getString("id").equals(super.getTermChosen())){
                    continue;
                }


                String classType = classData.optJSONObject("component").optString("code");
                String courseName = classData.getJSONObject("class").getJSONObject("course").getString("displayName");
                curCourse.setCourse(classType.equals("LEC") ? courseName : courseName + " " + classType);

                curCourse.setTitle(classData.optJSONObject("class").optJSONObject("course").optString("title"));
                curCourse.setSchedNum(classData.optString("id"));
                JSONArray meetings = classData.optJSONArray("meetings");

                curCourse.setUnits(classData.optJSONObject("class").optJSONObject("allowedUnits").optString("forAcademicProgress"));

                // if(!classType.equals("LEC")) curCourse.instructors.add("[Unknown]");
                if(meetings.optJSONObject(0).has("location")) {
                    try {
                        if(meetings.getJSONObject(0).getJSONObject("location").has("description"))
                            curCourse.getLocations().add(meetings.getJSONObject(0).getJSONObject("location").optString("description"));
                    }
                    catch(JSONException e) {}
                }
                if(meetings.getJSONObject(0).has("assignedInstructors")) {
                    try {
                        JSONArray assignedInstructors = meetings.getJSONObject(0).getJSONArray("assignedInstructors");
                        for(int j = 0; j < assignedInstructors.length(); j++) {
                            String name = assignedInstructors.getJSONObject(j).getJSONObject("instructor").getJSONArray("names").getJSONObject(0).getString("formattedName");
                            curCourse.getInstructors().add(name);
                        }
                    }
                    catch(JSONException e) {}
                }
                if(meetings.getJSONObject(0).has("startTime")) curCourse.getTimes().add((meetings.getJSONObject(0).getString("startTime").substring(0, 5) + "-" + meetings.getJSONObject(0).getString("endTime").substring(0, 5)).replace(":",""));
                if(meetings.getJSONObject(0).has("meetsDays")) curCourse.getDays().add(meetings.getJSONObject(0).getString("meetsDays"));

                if(classData.has("enrollmentStatus")) {
                    JSONObject enrollmentStatus = classData.optJSONObject("enrollmentStatus");
                    int numWaitlisted = enrollmentStatus.optInt("waitlistedCount");
                    int maxEnroll = enrollmentStatus.optInt("maxEnroll");
                    if(numWaitlisted > 0)
                        curCourse.setSeats("-" + numWaitlisted + "/" + maxEnroll);
                    else curCourse.setSeats((maxEnroll - enrollmentStatus.optInt("enrolledCount")) + "/" + maxEnroll);
                }
                if (curCourse.isComplete()) {
                    if(!courseSectionsMap.containsKey(curCourse.getCourse())) {
                        courseSectionsMap.put(curCourse.getCourse(), new ArrayList<>());
                    }
                    // TODO: beware of duplicates getting into lists; check that lists DON'T contain the course already
                    courseSectionsMap.get(curCourse.getCourse()).add(curCourse);
                }
                else System.out.println("Course thrown away: " + curCourse.getSchedNum());
            }
            long totalTime = System.currentTimeMillis() - timerStart;
            float benchmark = totalTime / 1000.0f;
            System.out.print("Time: ");
            System.out.format("%.3f", benchmark);
            System.out.println();
        }
        catch(final NullPointerException e) {
            log.error(e);
        }
    }

    /** Sets the department URL to parse */
    @Override
    public URL supplySearchUrl(final String courseID) throws MalformedURLException {
        if(super.getParameters() != null) {
            return new URL(REGISTRATION_SEARCH_PAGE_BERKELEY + courseID + getParameters());
        }
        else {
            return new URL(REGISTRATION_SEARCH_PAGE_BERKELEY + courseID);
        }
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
        if(days.contains("Mo")) res[0] = 1;
        if(days.contains("Tu")) res[1] = 1;
        if(days.contains("We")) res[2] = 1;
        if(days.contains("Th")) res[3] = 1;
        if(days.contains("Fr")) res[4] = 1;
        return res;
    }

    /** Create the size-sorted-courses list */
    @Override
    public void createSizeSortedCourses() {
        final Iterator it = courseSectionsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            super.getSizeSortedCourses().add((List<CourseSectionDto>)pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        Collections.sort(super.getSizeSortedCourses(), (Comparator<List>) (a1, a2) -> {
            return a1.size() - a2.size();
        });
    }

    /** Rowspan formula */
    @Override
    public int rowspanFormula(int startHour, int startMin, int endHour, int endMin) {
        return ((((endHour * 60) + (endMin + 1)) - ((startHour * 60) + (startMin + 1))) / 15);
    }

    /** Append params to the search URL */
    public void appendParameter(final String addParam) {
        if(getParameters() != null)
            super.setParameters(getParameters() + addParam);
        else
            super.setParameters(addParam);
    }

    /** Set the period/term to search in the URL */
    @Override
    public void setTermParameter(final String season, final String year) {
        String seasonNumber = "";
        switch (season) {
            case "Winter":
                seasonNumber = "2";
                break;
            case "Spring":
                seasonNumber = "2";
                break;
            case "Summer":
                seasonNumber = "5";
                break;
            case "Fall":
                seasonNumber = "8";
                break;
        }
        final String term = "/2" + year.substring(2) + seasonNumber;

        super.setTermChosen(term);
        appendParameter(term);
    }

    /**** --------------------  *****
     *****    Private Methods    *****
     ***** --------------------  ****/

    /** Supply JSON response for CompletableFuture */
    private JSONObject supplyJsonResponseFromCourseID(final String courseID) {
        try {
            final URL searchUrl = supplySearchUrl(courseID);
            final String responseString = readUrl(searchUrl);
            return new JSONObject(responseString);
        } catch (final MalformedURLException e) {
            log.error(e);
        }
        return null;
    }

    /** Get the JSON response String from Berk */
    private static String readUrl(final URL connection) {
        BufferedReader reader = null;
        final long timerStart = System.currentTimeMillis();
        try {
            reader = new BufferedReader(new InputStreamReader(connection.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            final long elapsedTimeInMillis = System.currentTimeMillis() - timerStart;
            log.info("[Berkeley] URL took " + GenericUtils.getFormattedElapsedTime(elapsedTimeInMillis) +
                    "sec to load (" + connection.toString() + ")");

            return buffer.toString();
        } catch(final Exception e) {
            log.error("Error opening URL: " + connection.toString(), e);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    log.error("Error closing connection", e);
                }
            }
        }
        return null;
    }
}
