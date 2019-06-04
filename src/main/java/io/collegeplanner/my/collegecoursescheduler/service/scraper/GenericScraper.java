package io.collegeplanner.my.collegecoursescheduler.service.scraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.collegeplanner.my.collegecoursescheduler.model.dto.CourseSectionDto;
import io.collegeplanner.my.collegecoursescheduler.model.dto.PermutationsJobResultsDto;
import io.collegeplanner.my.collegecoursescheduler.model.dto.ScheduleDto;
import io.collegeplanner.my.collegecoursescheduler.model.dto.UserOptionsDto;
import io.collegeplanner.my.collegecoursescheduler.util.GenericUtils;
import io.collegeplanner.my.collegecoursescheduler.util.ScheduleBuilderUtils;
import io.collegeplanner.my.collegecoursescheduler.util.ScraperUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.*;

@Log4j2
@Getter
@Setter
// TODO: keep reducing to core components of a "Generic Scraper"
// TODO: this isn't even a "scraper" anymore.. refactor/rename/modularize
public abstract class GenericScraper {

    @Autowired
    private ObjectMapper objectMapper;

    private PrintWriter outWriter;

    private UserOptionsDto userOptions;

    private URL Registration_URL;

    private String parameters;
    private String serverPath;
    private String registrationSearchPage;
    private String termChosen; // (new) added for berkeley hotfix

    private int numChosenCourses = 0;
    private int sectionMeetingCounter = 0;

    private boolean isMobileBrowser;
    private boolean timedOut = false;

    private long timeEnd = 0L;
    private long numPermutations = 0;
    private long numValidSchedules = 0;

    private Set<String> foundAvailableCourses = new HashSet<>();
    private List<List<CourseSectionDto>> sizeSortedCourses = new ArrayList<>();
    private Map<Integer, List<CourseSectionDto>> countIndexedCourses = new TreeMap<>();

    private String timeToRetrieveCourseData;

    public abstract String getUniversityName();

    public abstract void iterateInput(final List<String> iterator) throws Exception;

    public abstract void parseRegistrationData(final String department) throws Exception;

    public abstract int[] convDaysToArray(final String days);

    // TODO: create a more "concrete" abstract method.. the method name itself is ambiguous and confusing
    public abstract void createSizeSortedCourses();

    public abstract void setTermParameter(final String season, String year);

    public abstract URL supplySearchUrl(final String s) throws MalformedURLException;

    public abstract String formatURL(String url);

    public abstract int rowspanFormula(final int startHour, final int startMin, final int endHour, final int endMin);

    /** Analyze and rank all valid permutations */
    public final PermutationsJobResultsDto doPermutationsAndGetApiResults(final HttpServletRequest request,
                                                                          final HttpServletResponse response) {

        final long timeStartForPermutations = System.currentTimeMillis();
        final PriorityQueue<ScheduleDto> validSchedules = new PriorityQueue<>();
        final PermutationsJobResultsDto results = new PermutationsJobResultsDto();

        // TODO: crude mobile detection.. replace with feature lookbehind
        if(request.getHeader("User-Agent").contains("Mobile")) {
            this.setMobileBrowser(true);
        } else {
            this.setMobileBrowser(false);
        }

        try {
            // Sort the course lists by size to allow for efficient dynamic programming techniques
            createSizeSortedCourses();
            this.numChosenCourses = this.sizeSortedCourses.size();
            this.createCountIndexedCourses();

            // Speeds up permutations with dynamic programming
            final int mostSignificantBit = countIndexedCourses.get(0).size();
            final Map<String, long[]> storedTimeblocks = new HashMap<>();
            int[] iterationVariables = new int[this.numChosenCourses];


            permutations:
            while(iterationVariables[0] < mostSignificantBit) {

                if(this.getElapsedTime(timeStartForPermutations) > MAX_TIMEOUT_IN_MS) {
                    log.warn("Thread was interrupted during permutations due to MAX_TIMEOUT");
                    // this.timedOut = true;
                    results.setResultCode(RESULT_CODE_PARTIAL_CONTENT);
                    return results;
                }

                this.numPermutations++;

                final List<CourseSectionDto> currentSchedule = new ArrayList<>();
                long[] combinedTimeBlocks = new long[5]; // 5 days
                boolean schedulesCollide = false; // assume schedules don't collide

                for(int i = 0; i < this.numChosenCourses; i++) {
                    currentSchedule.add(countIndexedCourses.get(i).get(iterationVariables[i]));
                }

                final StringBuilder permutationString = new StringBuilder();
                for(int i = Math.max((this.numChosenCourses - 2), 0); i >= 0; i--) {
                    permutationString.append(iterationVariables[i]); // 000000, 000001, 000002
                }
                final String permString = permutationString.toString();

                /** This section is for UW and UCSB (so far) */
                for(final CourseSectionDto crs : currentSchedule) {
                    final CourseSectionDto parentCourse = crs.getParentCourse();
                    // See whether there is a related course or not
                    if(parentCourse != null) {
                        // If there is, make sure that the string of classes contains the parentCourse
                        if(!currentSchedule.contains(parentCourse)) {
                            // Minimum number of classes = 2 (e.g. permString = "01", "011001", etc.
                            if(permString.length() > 2) {
                                storedTimeblocks.put(permString, COLLISION_TIMEBLOCK);
                            }
                            iterationVariables = ScraperUtils.incrementIterationVariables(iterationVariables, this.countIndexedCourses, this.numChosenCourses);
                            continue permutations;
                            // TODO: do I need the "iterationVariables = " part, or will Java retain the correct values
                            // TODO:   (i.e. check whether pass-by-value vs. pass-by-reference)
                        }
                    }
                }
                /** End section */

                // Re-use and recycle; check if the current permutation has already been calculated
                // If so, it will have been stored in the HashMap "storedTimeblocks"
                if(storedTimeblocks.containsKey(permString)) {
                    final long[] retrievedBlock = storedTimeblocks.get(permString);
                    // See if the HashMap has this block marked as a schedule with collisions
                    if(Arrays.equals(retrievedBlock, COLLISION_TIMEBLOCK)){
                        iterationVariables = ScraperUtils.incrementIterationVariables(iterationVariables, this.countIndexedCourses, this.numChosenCourses);
                        continue permutations;
                    }
                    else {
                        // Using clone() because Java passed the memory reference without it, NOT value to combinedTimeBlocks
                        //   so it got unintentionally/unexpectedly got edited along the way and marked VALID schedules
                        //   as INVALID schedules
                        combinedTimeBlocks = (storedTimeblocks.get(permString)).clone();
                    }
                }
                // If HashMap "storedTimeblocks" doesn't already have the permutation, create it
                //    in order to be reused later
                // TODO: too much nesting to read; needs cleaning up
                else {
                    iterateBlocks:
                    // (this.numChosenCourses - 2) because there's no point in storing the full permutation string
                    //     (e.g. if there's 5 classes then stored the iterations: 0000, 0001, 0002, 0010, etc.
                    //      DON'T store 00001 since it will only be used once and hence, doesn't save any time)
                    // (Math.max(..., 0) since if there's only one class, this will become an infinite loop
                    //     (plus it wouldn't help us with only one class anyway..)
                    for(int i = Math.max((this.numChosenCourses - 2), 0); i >= 0; i--) {
                        for(int multipleDaySlots = 0; multipleDaySlots < countIndexedCourses.get(i).get(iterationVariables[i]).getDays().size(); multipleDaySlots++) {
                            final int[] daysArray = convDaysToArray(countIndexedCourses.get(i).get(iterationVariables[i]).getDays().get(multipleDaySlots));
                            // daysArray.length = 5 (Mon, Tues, Wed, Thurs, Fri)
                            //    represented as a string (e.g. daysArray = "10101")
                            for(int j = 0; j < daysArray.length; j++) {
                                // If there is class on the current day
                                if (daysArray[j] == 1) {
                                    // bitwise AND
                                    final long bits = convertTimeblockToStringOfBits(countIndexedCourses.get(i).get(iterationVariables[i]).getTimes().get(multipleDaySlots));
                                    // if the bitwise AND doesn't equal 0, then the schedules collide
                                    if ((bits & combinedTimeBlocks[j]) != 0) {
                                        // store the collided permutation so we don't have to do this calculation tons of unnecessary times
                                        storedTimeblocks.put(permString, COLLISION_TIMEBLOCK);
                                        iterationVariables = ScraperUtils.incrementIterationVariables(iterationVariables, this.countIndexedCourses, this.numChosenCourses);
                                        continue permutations;
                                    } else {
                                        // bitwise OR
                                        combinedTimeBlocks[j] = (bits | combinedTimeBlocks[j]);
                                    }
                                }
                            }
                        }
                    }
                    if(!schedulesCollide) {
                        storedTimeblocks.put(permString, Arrays.copyOf(combinedTimeBlocks, combinedTimeBlocks.length));
                    }
                }

                // Check the class[0 - (n-2] against the class[(n-1)]
                // Saves times since class[(n-1)] has the largest size() of all courses
                //     and class[(n-1)] timeblocks easily accessed from a map
                if(this.numChosenCourses > 1) {
                    final int lastCourse = this.numChosenCourses - 1;
                    // Checking against last course in permutation
                    for(int multipleDaySlots = 0; multipleDaySlots < countIndexedCourses.get(lastCourse).get(iterationVariables[lastCourse]).getDays().size(); multipleDaySlots++) {
                        final int[] daysArray = convDaysToArray(countIndexedCourses.get(lastCourse).get(iterationVariables[lastCourse]).getDays().get(multipleDaySlots));
                        for(int i = 0; i < daysArray.length; i++) {
                            if(daysArray[i] == 1) {
                                final long bits = convertTimeblockToStringOfBits(countIndexedCourses.get(lastCourse).get(iterationVariables[lastCourse]).getTimes().get(multipleDaySlots));
                                // bitwise AND
                                if((bits & combinedTimeBlocks[i]) != 0) {
                                    iterationVariables = ScraperUtils.incrementIterationVariables(iterationVariables, this.countIndexedCourses, this.numChosenCourses);
                                    continue permutations;
                                } else {
                                    // bitwise OR
                                    combinedTimeBlocks[i] = (bits | combinedTimeBlocks[i]);
                                }
                            }
                        }
                    }
                }

                iterationVariables = ScraperUtils.incrementIterationVariables(iterationVariables, this.countIndexedCourses, this.numChosenCourses);

                // If schedule makes it this far, it has no collisions

                /** USER PREFERENCES */

                /** Remove waitlisted classes option */
                if(currentSchedule.get(0).getSeats() != null) {
                    for (final CourseSectionDto crs : currentSchedule) {
                        final int numSeatsAvailable = Integer.parseInt(crs.getSeats().substring(0, crs.getSeats().indexOf("/")));
                        if (!(numSeatsAvailable > 0)) {
                            // this "option-check" is down here instead of a few lines up because
                            //
                            if (!userOptions.isShowWaitlistedClasses()) {
                                continue permutations;
                            }
                        } else {
                            // This is used in letting the user know which classes have no available non-waitlisted sessions
                            //    during the selected term (as a reminder / heads-up)
                            foundAvailableCourses.add(crs.getCourse());
                        }
                    }
                }

                /** User's unavailable time(s) option */
                // i = Mon, Tues, Wed, Thurs, Fri
                for (int i = 0; i < 5; i++) {
                    if ((userOptions.getUnavailableTimesBitBlocks()[i] & combinedTimeBlocks[i]) != 0) {
                        continue permutations;
                    }
                }

                /** ScheduleDto distance (tightness) factor */
                int scheduleDistance = 0;
                for(int whichDay = 0; whichDay < 5; whichDay++) {
                    final StringBuilder timeDistance = new StringBuilder(Long.toBinaryString(combinedTimeBlocks[whichDay]));
                    // Remove leading and trailing zeros
                    if(timeDistance.length() > 1) {
                        for(int i = 0; timeDistance.charAt(i) == '0'; i++){
                            timeDistance.deleteCharAt(i);
                        }
                        for(int i = timeDistance.length() - 1; timeDistance.charAt(i) == '0'; i--){
                            timeDistance.deleteCharAt(i);
                        }
                        // Count number of zeros (indicates distance in class times)
                        //    [less zeros == tighter schedule]
                        for(int i = 0; i < timeDistance.length(); i++)
                            if(timeDistance.charAt(i) == '0') {
                                scheduleDistance++;
                            }
                    }
                }

                // ScheduleDto variables
                int numWanted, numUnwanted, numDays;
                numWanted = numUnwanted = numDays = 0;

                breakLoop:
                for(final CourseSectionDto crs : currentSchedule) {
                    // Tally number of days in schedule
                    for(final String days : crs.getDays()) {
                        numDays = numDays + days.length();
                    }

                    /** Analyze user's optional preferences */
                    if(optionsToCheck()) {
                        for(final String professorUnformatted : crs.getInstructors()) {

                            // A. Narang --> Narang, A.
                            final String professor = (professorUnformatted.substring(3) + ", " + professorUnformatted.substring(0, 3)).toUpperCase().trim();

                            /** Exclude professors option */
                            if(userOptions.getExcludeProfessors() != null) {
                                for(final String excludedProfessor : userOptions.getExcludeProfessors()) {
                                    if(professor.equals(excludedProfessor.toUpperCase().trim())) {
                                        continue permutations;
                                    }
                                }
                            }
                            /** Prioritize preferred professors option */
                            if(userOptions.getWantedProfessors() != null) {
                                for(final String wantedProfessor : userOptions.getWantedProfessors())
                                    if(professor.equals(wantedProfessor.toUpperCase().trim())) {
                                        numWanted++;
                                    }
                            }
                            /** De-prioritize unwanted professors option */
                            if(userOptions.getUnwantedProfessors() != null) {
                                for(final String unwantedProfessor : userOptions.getUnwantedProfessors())
                                    if(professor.equals(unwantedProfessor.toUpperCase().trim())) {
                                        numUnwanted++;
                                    }
                            }
                        }
                        /** Remove hybrid/online classes */
                        if (!userOptions.isShowOnlineClasses()) {
                            for (String location : crs.getLocations()) {
                                if (location.contains("ON-LINE")) {
                                    continue permutations;
                                }
                            }
                        }
                    }
                }
                // If it reaches here, then it will be added as a valid schedule.

                final long[] schedLayout = calculateScheduleLayout(currentSchedule);

                final ScheduleDto sched = new ScheduleDto(currentSchedule, numWanted, numUnwanted, scheduleDistance, numDays, schedLayout, this.userOptions); //allows algorithm to update before adding to PQ
                validSchedules.add(sched);
                this.numValidSchedules++;

                // NOTE: remember, PriorityQueue is in REVERSE order (LAST is BEST schedule)
                // Remove lower ranked schedules that have basically the same schedule layout but with different instructors
                // TODO: make it a user option to remove schedules with the same days/times (but different instructors)
                if(userOptions.isRemoveSimilarLayouts()) {
                    boolean alreadySeen = false;
                    ScheduleDto scheduleToDelete = null;
                    final ScheduleDto[] schedulesArray = new ScheduleDto[validSchedules.size()];
                    for(int i = 0; i < schedulesArray.length; i++) {
                        schedulesArray[i] = validSchedules.poll();
                        if(Arrays.equals(schedulesArray[i].getLayoutIdentifierForDeduplication(), schedLayout)) {
                            // Check if already seen this layout before
                            if(alreadySeen) {
                                scheduleToDelete = schedulesArray[i];
                            } else {
                                alreadySeen = true;
                            }
                        }
                    }
                    // add back into PriorityQueue
                    for(int i = 0; i < schedulesArray.length; i++) {
                        // delete the lower-ranked scheduled if there is a duplicate layout
                        if(schedulesArray[i] != scheduleToDelete) {
                            validSchedules.add(schedulesArray[i]);
                        }
                    }
                }
                // Keep the number of schedules equal to 25 (or less)
                if (validSchedules.size() > 25) {
                    validSchedules.poll();
                }
            }
            if(response != null) {
                printTables(validSchedules, request, response, false);
            }


            results.setResultCode(RESULT_CODE_OK);

        }
        catch(final Exception e) {
            results.setResultCode(RESULT_CODE_INTERNAL_SERVER_ERROR);
            log.fatal("Error in analyzing the permutations", e);

        }
        finally {
            // How could I do this with Stream API
            final ScheduleDto[] schedulesArray = new ScheduleDto[validSchedules.size()];
            for(int i = 0; i < schedulesArray.length; i++) {
                schedulesArray[i] = validSchedules.poll();
                schedulesArray[i].setScheduleRank(i + 1);
            }
            final Set<ScheduleDto> orderedSchedules = new TreeSet<>(Arrays.asList(schedulesArray));

            final int numValidSchedulesToReturn = this.numValidSchedules < 25 ? (int) this.numValidSchedules : 25;
            final Set<String> coursesWithAllSectionsWaitlisted = ScraperUtils.getCoursesWithAllSectionsWaitlisted(this.foundAvailableCourses, this.sizeSortedCourses)
                    .stream().collect(Collectors.collectingAndThen(Collectors.toSet(),
                            Collections::unmodifiableSet));
            final Set<String> coursesWithNoSectionsOffered = ScraperUtils.getCoursesWithNoSectionsOffered(this.userOptions.getChosenCourseNames(), this.sizeSortedCourses)
                    .stream().collect(Collectors.collectingAndThen(Collectors.toSet(),
                            Collections::unmodifiableSet));
            final String timeToCompletePermutations = GenericUtils.getFormattedElapsedTime(timeStartForPermutations);


//            results.setCourseNamesSelected(userCourseSelection);
            results.setNumSchedulesReturned(numValidSchedulesToReturn);
            results.setTimeToRetrieveCourseData(this.timeToRetrieveCourseData + SECONDS_ABBR);
            results.setTimeToCompletePermutations(timeToCompletePermutations + SECONDS_ABBR);
            results.setRankedValidSchedules(orderedSchedules);
            results.setNumTotalValidSchedules(this.numValidSchedules);
            results.setNumPermutationsPerformed(this.numPermutations);
            results.setNumTotalInvalidSchedules(this.numPermutations - this.numValidSchedules);
            results.setNumTheoreticalPermutations(ScraperUtils.getNumTheoreticalPermutations(this.sizeSortedCourses));
            results.setCoursesWithAllSectionsWaitlisted(coursesWithAllSectionsWaitlisted);
            results.setCoursesWithNoSectionsOfferedForTerm(coursesWithNoSectionsOffered);

            final String chosenCoursesByUser = "[" + String.join(", ", userOptions.getChosenCourseNames()) + "]";
            log.info(String.format(
                "[%s] Analyze job took %s seconds to finish %,d permutations.",
                    this.getUniversityName(), timeToCompletePermutations, getNumPermutations())
                + "\t Course Selection: " + chosenCoursesByUser
            );
        }

        return results;
    }

    // TODO: modularize.. this is a behemoth mess
    /** Print 25 (or less) carousel tables of schedules */
    private final void printTables(final PriorityQueue<ScheduleDto> validSchedules,
                                   final HttpServletRequest request,
                                   final HttpServletResponse response,
                                   final boolean wasInterrupted) throws IOException, ServletException {
        final PrintWriter out = response.getWriter();
        try {
            // TODO: should just include header and footer in every request..
            request.getRequestDispatcher(JSP_VIEW_RESOLVER_PREFIX + HEADER_FILE_NAME)
                .include(request, response);

            final int size = validSchedules.size();

            final List<List<CourseSectionDto>> correctOrder = new ArrayList<>();
            for (int i = size - 1; i >= 0; i--) {
                final ScheduleDto schedule = validSchedules.poll();
                schedule.setScheduleRank(i + 1); // not zero-indexed
                correctOrder.add(0, schedule.getCourseSectionsInSchedule());
            }

            /** Tables header */
            out.println("<div class='block-area'>");
            out.println("<div id='scheduleHeader' class='row'>");
            // Classes with 0 non-waitlisted sections
            final Set<String> waitlistedClasses = ScraperUtils.getCoursesWithAllSectionsWaitlisted(foundAvailableCourses, sizeSortedCourses);
            out.println("<div class='col-xs-offset-1 col-xs-2'>");
            out.println("<h4 class='fewClasses'>[Note] All sections waitlisted:</h4>");
            if (!waitlistedClasses.isEmpty()) {
                out.println("<ul class='lists-fewClasses'>");
                for (final String className : waitlistedClasses) {
                    out.println("<li>" + className + "</li>");
                }
                out.println("</ul>");
            }
            out.println("</div>");
            // Table header
            out.println("<div class='col-xs-6' style='text-align:center'>");
            out.println("<h2 class='numSched'>Schedule <span id='numSched' class='numSched' data-count='1'>" + Math.min(1, size) + "</span> of " + Math.min(25, size) + "</h2>");
            if (!wasInterrupted)
                out.println("<i>There are " + NumberFormat.getNumberInstance(Locale.US).format(size) + " valid permutations of your schedule - we filtered out " + NumberFormat.getNumberInstance(Locale.US).format((numPermutations - size)) + " that didn't work</i>");
            else
                out.println("<i>There were too many permutations for your schedule so we ranked the first " + NumberFormat.getNumberInstance(Locale.US).format(numPermutations) + "." + "<br>We found " + NumberFormat.getNumberInstance(Locale.US).format(size) + " valid permutations of your schedule and filtered out " + NumberFormat.getNumberInstance(Locale.US).format((numPermutations - size)) + " that didn't work</i>");
            out.println("<br>");
            out.println("<i id='numValid' data-count='" + Math.min(25, size) + "'>Check out your top " + Math.min(25, size) + "!</i>");
            out.println("</div>");
            // Classes with 0 total sections (waitlisted or not)
            final Set<String> unavailableClasses = ScraperUtils.getCoursesWithNoSectionsOffered(this.userOptions.getChosenCourseNames(), this.sizeSortedCourses);
            out.println("<div class='col-xs-2'>");
            out.println("<h4 class='fewClasses'>[Note] Not offered this term:</h4>");
            if (!unavailableClasses.isEmpty()) {
                out.println("<ul class='lists-fewClasses'>");
                for (final String className : unavailableClasses) {
                    out.println("<li>" + className + "</li>");
                }
                out.println("</ul>");
            }
            out.println("</div>");
            out.println("</div>");
            out.println("<div id='myCarousel' class='carousel slide' data-ride='carousel' data-interval='false'>");
            out.println("<div class='carousel-inner'>");

            /** Create 25 (or less) tables */
            final Map<String, Integer> courseColors = new HashMap<>();
            for (int numTables = 0; numTables < (Math.min(25, size)); numTables++) {
                final List<CourseSectionDto> curSchedule = correctOrder.get(numTables);
                if (numTables == 0) {
                    out.println("<div class='item active'>");
                } else {
                    out.println("<div class='item'>");
                }
                out.println("<table class='optimized-table'>");
                out.println("<tr>");
                out.println("<th class='opt-table-label opt-table-time'></th>");
                if (!isMobileBrowser) {
                    out.println("<th class='opt-table-label'>Monday</th>");
                    out.println("<th class='opt-table-label'>Tuesday</th>");
                    out.println("<th class='opt-table-label'>Wednesday</th>");
                    out.println("<th class='opt-table-label'>Thursday</th>");
                    out.println("<th class='opt-table-label'>Friday</th>");
                } else if (isMobileBrowser) {
                    out.println("<th class='opt-table-label'>M</th>");
                    out.println("<th class='opt-table-label'>T</th>");
                    out.println("<th class='opt-table-label'>W</th>");
                    out.println("<th class='opt-table-label'>Th</th>");
                    out.println("<th class='opt-table-label'>F</th>");
                }
                out.println("</tr>");

                // to keep track of column offset due to rowspan attribute
                final int[] filledDays = new int[6];
                for (int i = 800; i < 2100; i += 15) {

                    final String time = ((Integer) i).toString();
                    boolean bottomBorder = true;

                    out.println("<tr>");
                    if (time.substring(time.length() - 2).equals("00") || time.substring(time.length() - 2).equals("30")) {
                        out.println("<td rowspan='2' class='opt-table-label opt-table-time'>" + time24to12(i) + "</td>");
                        bottomBorder = false;
                    }
                    for (int day = 0; day < 5; day++) {
                        boolean filled = false;
                        for (final CourseSectionDto course : curSchedule) {
                            int sections = Math.max(course.getLocations().size(), Math.max(course.getInstructors().size(), Math.max(course.getDays().size(), course.getTimes().size())));
                            for (int j = 0; j < sections; j++) {
                                String course_instructor, course_days, course_time, course_location;
                                course_instructor = course_days = course_time = course_location = null;
                                int course_num = j;

                                // TODO: clean this up; make it more elegant
                                // Handles irregularities in WebPortal registration
                                while (course_instructor == null || course_days == null || course_time == null || course_location == null) {
                                    if (course_instructor == null) {
                                        try {
                                            course_instructor = course.getInstructors().get(course_num);
                                        } catch (final Exception e) {
                                            course_instructor = "[No professor found]";
                                        }
                                    }
                                    if (course_days == null) {
                                        try {
                                            course_days = course.getDays().get(course_num);
                                        } catch (final Exception e) {
                                            course_days = "[No days found]";
                                        }
                                    }
                                    if (course_time == null) {
                                        try {
                                            course_time = course.getTimes().get(course_num);
                                        } catch (final Exception e) {
                                            course_time = "[No times found]";
                                        }
                                    }
                                    if (course_location == null) {
                                        try {
                                            course_location = course.getLocations().get(course_num);
                                        } catch (final Exception e) {
                                            course_location = "[No location found]";
                                        }
                                    }
                                    course_num--;
                                }

                                /** Create table elements for course meeting times */
                                // *If the course starts at time 'i'
                                final int[] classDays = convDaysToArray(course_days);
                                String string_of_i = ((Integer) i).toString();
                                // Fixes comparing 800 against 0800
                                if (i < 1000) {
                                    string_of_i = "0" + string_of_i;
                                }
                                if ((classDays[day] == 1) && (string_of_i.equals(course_time.substring(0, course_time.indexOf("-"))))) {
                                    final int rowSpan = calculateRowspan(course, j);
                                    final String uniqueIdentifier = (course.getSchedNum().contains("***")) ?
                                            removeIllegalChars(course.getCourse() + "noSchedNum") :
                                            removeIllegalChars(course.getCourse() + "_" + course.getSchedNum());
                                    out.print("<td rowspan='" + rowSpan + "' class='tableCourse opt-class-" + ScheduleBuilderUtils.getCourseColor(course.getTitle(), courseColors));
                                    out.print(" " + uniqueIdentifier + "_" + j + "h'");
                                    if (bottomBorder) {
                                        out.print(" bottomBorder");
                                    }
                                    out.print(" data-toggle='modal' data-courseid='" + uniqueIdentifier + "_" + j + "' data-target='#tableModal'");
                                    out.println(">");
                                    out.println("<p class='course-data course-id'><b>" + course.getCourse().toUpperCase() + "</b></p>");
                                    out.print("<div ");
                                    // indented for ease of visualization
                                    if (isMobileBrowser) {
                                        out.print("style='display:none' ");
                                    }
                                    out.println(">");
                                    out.print("<p class='course-data course-title'><i>");
                                    if (course.getTitle().length() > 40) {
                                        int cutoff = course.getTitle().indexOf(" ");
                                        while (cutoff >= 0 && cutoff < 35) {
                                            cutoff = course.getTitle().indexOf(" ", cutoff + 1);
                                        }
                                        if (cutoff < 0) {
                                            cutoff = 35;
                                        }
                                        out.print(course.getTitle().substring(0, cutoff).toUpperCase() + "...");
                                    } else {
                                        out.print(course.getTitle().toUpperCase());
                                    }
                                    out.print("</i></p>");
                                    out.print("</i></p>");
                                    out.println("<p class='course-data course-instructors'>" + course_instructor + "</p>");
                                    out.println("<p class='course-data course-times'>" + time24to12(course_time) + "</p>");
                                    out.println("<p class='course-data course-cid'>Schedule #: " + course.getSchedNum() + "</p>");
                                    out.println("</div>");

                                    /** Modal data (hidden) */
                                    out.print("<div ");
                                    out.print("style='display:none' ");
                                    out.println("class='" + uniqueIdentifier + "_" + j + " temp'>");
                                    out.println("<p class='course-title'><i>" + course.getTitle().toUpperCase() + "</i></p>");
                                    out.println("<p class='course-instructors'>" + course_instructor + "</p>");
                                    out.println("<p class='course-times'>" + time24to12(course_time) + "</p>");
                                    out.println("<p class='course-location'>" + course_location + "</p>");
                                    out.println("<p class='course-cid'>Schedule #: " + course.getSchedNum() + "</p>");
                                    if(course.getSeats() != null) {
                                        out.print("<p class='seats");
                                        // Check if waitlisted; if so red text
                                        if (course.getSeats().contains("-") || course.getSeats().charAt(0) == '0') {
                                            out.print(" text-red");
                                        }
                                        out.println("'>Available seats: " + course.getSeats() + "</p>");
                                    }
                                    out.println("</div>");
                                    out.println("</td>");
                                    filledDays[day] = filledDays[day] + (rowSpan - 1);
                                    filled = true;
                                }
                            }
                        }
                        if (!filled) {
                            if (filledDays[day] == 0) {
                                out.print("<td");
                                if (bottomBorder) {
                                    out.print(" class='bottomBorder'");
                                }
                                out.println("></td>");

                            } else {
                                filledDays[day] = filledDays[day] - 1;
                            }
                        }
                    }
                    out.println("</tr>");
                    // Change 8:60, 8:75, 8:90, etc.
                    if (time.substring(time.length() - 2).equals("45")) {
                        i += 40;
                    }
                }
                out.print("</table>");
                out.println("</div>");
            }

            out.println("</div>");
            /** Table navigation arrows */
            if (size > 0) {
                out.println("<a id='left-table' class='left carousel-control' href='#myCarousel' data-slide='prev'>");
                out.println("<span class='glyphicon glyphicon-chevron-left'></span>");
                out.println("<span class='sr-only'>Previous</span>");
                out.println("</a>");
                out.println("<a id='right-table' class='right carousel-control' href='#myCarousel' data-slide='next'>");
                out.println("<span class='glyphicon glyphicon-chevron-right'></span>");
                out.println("<span class='sr-only'>Next</span>");
                out.println("</a>");
            }
            out.println("</div>");
            out.println("</div>");

            this.timeEnd = System.currentTimeMillis();
        }
        catch(final Exception e) {
            log.error("Error building visual schedule tables", e);
        }
        finally {
            out.flush();
            /** Footer */
            request.getRequestDispatcher(JSP_VIEW_RESOLVER_PREFIX + FOOTER_FILE_NAME)
                .include(request, response);

            // Record data
//            DatabaseConnection rds = new DatabaseConnection(request);
//            rds.writeToDatalog(custom.getElapsedTime(), suggestion, problems, classes);
        }
    }

    /**
    * This method will convert a String representation of a time-block to a binary
    * representation of 15min increments starting at 8:00am (800)
    * Ex: "800-850" --> 1111 (8:45, 8:30, 8:15, 8:00)
    *  "1030-1120" --> 0011 1100 0000 0000 (11:15, 11:00, 10:45, 10:30)
     */
    public final static long convertTimeblockToStringOfBits(final String timeBlock) {
        // TODO: verify "timeBlock" adheres to correct form / create parser with RegEx(?)
        final String startTime = timeBlock.substring(0, timeBlock.indexOf("-"));
        final String endTime = timeBlock.substring(timeBlock.indexOf("-") + 1);
        // 8:00 offset (800 becomes 000) ==> multiplier: 8 --> 0
        // divide by 2 for three vs four digit numbers
        final int startTimeHundreds = Integer.parseInt(startTime.substring(0,(startTime.length()/2)));
        final int endTimeHundreds = Integer.parseInt(endTime.substring(0,(endTime.length()/2)));
        // spot in bundle of 4 bits (00, 15, 30, 45) --> (0, 1, 2, or 3)
        final int startTimeTens = Integer.parseInt(startTime.substring(startTime.length()-2));
        final int endTimeTens = Integer.parseInt(endTime.substring(endTime.length()-2));
        // Construct the bit String; represents both time and length of time
        final StringBuilder bitBuilder = new StringBuilder();
        final int numOnes = (((endTimeTens - startTimeTens) < 0 ? endTimeTens-startTimeTens+60 : endTimeTens-startTimeTens)  / 15) + 1;
        int onesFourMultiplier = endTimeHundreds - startTimeHundreds;
        // check for hour cutoff (ex. 845-900) shouldn't have 6 ones, only 2
        if((endTimeTens - startTimeTens) < 0) {
            onesFourMultiplier--;
        }
        final int numZeros = startTimeTens / 15;
        final int zerosFourMultiplier = startTimeHundreds - 8;

        // Build the bits
        for(int i = 0; i < onesFourMultiplier; i++) bitBuilder.append("1111");
        for(int i = 0; i < numOnes; i++) bitBuilder.append("1");
        for(int i = 0; i < zerosFourMultiplier; i++) bitBuilder.append("0000");
        for(int i = 0; i < numZeros; i++) bitBuilder.append("0");

        final String bitValue = bitBuilder.toString();
        return Long.parseLong(bitValue, 2);
    }

    private String time24to12(final int time) {
        final String s = ((Integer)time).toString();
        final int hr = Integer.parseInt((s.length() == 3) ? s.substring(0,1) : s.substring(0,2));
        final String min = s.substring(s.length()-2, s.length());
        return (((hr<=12) ? hr : hr-12) + ":" + min + " " + ((hr>=12) ? "pm" : "am"));
    }
    private String time24to12(final String timeframe) {
        final StringBuilder timeblock = new StringBuilder();

        String s = timeframe.substring(0,timeframe.indexOf("-"));
        int hr = Integer.parseInt((s.length() == 3) ? s.substring(0,1) : s.substring(0,2));
        String min = s.substring(s.length()-2, s.length());
        timeblock.append(((hr<=12) ? hr : hr-12) + ":" + min + ((hr>=12) ? "pm" : "am"));

        timeblock.append(" - ");

        s = timeframe.substring(timeframe.indexOf("-") + 1);
        hr = Integer.parseInt((s.length() == 3) ? s.substring(0,1) : s.substring(0,2));
        min = s.substring(s.length()-2, s.length());
        timeblock.append(((hr<=12) ? hr : hr-12) + ":" + min + ((hr>=12) ? "pm" : "am"));

        return timeblock.toString();
    }
    private int calculateRowspan(final CourseSectionDto course, final int j) {
        String t = course.getTimes().get(j).substring(0, course.getTimes().get(j).indexOf("-"));
        final int startHour = Integer.parseInt((t.length() == 3) ? t.substring(0, 1) : t.substring(0, 2));
        final int startMin = Integer.parseInt((t.length() == 3) ? t.substring(1, 3) : t.substring(2, 4));

        t = course.getTimes().get(j).substring(course.getTimes().get(j).indexOf("-") + 1);
        final int endHour = Integer.parseInt((t.length() == 3) ? t.substring(0, 1) : t.substring(0, 2));
        int endMin = Integer.parseInt((t.length() == 3) ? t.substring(1, 3) : t.substring(2, 4));
        if(endMin > 30 && endMin < 45) {
            endMin = 45;
        }

        final int rowSpan = rowspanFormula(startHour, startMin, endHour, endMin);

        return rowSpan;
    }


    private String removeIllegalChars(final String s) {
        return s.replaceAll("\\s+","").replaceAll("[\\[\\](){}]","");
    }

    private boolean optionsToCheck() {
        return (userOptions.getWantedProfessors() != null ||
                userOptions.getUnwantedProfessors() != null ||
                userOptions.getExcludeProfessors() != null ||
                !userOptions.isShowWaitlistedClasses() ||
                !userOptions.isShowOnlineClasses());
    }

    public void checkIfParametersContainsString(final String query) {
        int startIndex = 0, endIndex = 0;
        boolean nextParamFound = false;
        if(this.parameters != null) {
            if(this.parameters.contains(query)) {
                StringBuilder removeParam = new StringBuilder(this.parameters);
                startIndex = this.parameters.indexOf(query);
                for(int i = startIndex; i < this.parameters.length(); i++) {
                    if(this.parameters.charAt(i) == '&') {
                        endIndex = i;
                        nextParamFound = true;
                    }
                }
                if(!nextParamFound) {
                    endIndex = this.parameters.length();
                }
                this.parameters = removeParam.delete(startIndex, endIndex).toString();
            }
        }
    }

    // Insert into map indexed by number of courses
    private void createCountIndexedCourses() {
        for(int i = 0; i < sizeSortedCourses.size(); i++) {
            countIndexedCourses.put(i, sizeSortedCourses.get(i));
        }
    }

    private long getElapsedTime(long startTime) {
        return System.currentTimeMillis() - startTime;
    }

    private long[] calculateScheduleLayout(final List<CourseSectionDto> courses) {
        final long[] scheduleLayout = new long[5];
        for(int i = 0; i < courses.size(); i++) {
            for(int multipleDaySlots = 0; multipleDaySlots < courses.get(i).getDays().size(); multipleDaySlots++) {
                final int[] daysArray = convDaysToArray(courses.get(i).getDays().get(multipleDaySlots));
                for(int j = 0; j < daysArray.length; j++) {
                    if(daysArray[j] == 1) {
                        final long bits = convertTimeblockToStringOfBits(courses.get(i).getTimes().get(multipleDaySlots));
                        scheduleLayout[j] = (bits | scheduleLayout[j]);
                    }
                }
            }
        }
        return scheduleLayout;
    }
}
