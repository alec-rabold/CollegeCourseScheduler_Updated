package io.collegeplanner.my.collegecoursescheduler.util;

import io.collegeplanner.my.collegecoursescheduler.model.dto.CourseSectionDto;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ScraperUtils {

    public static Set<String> getCoursesWithNoSectionsOffered(final List<String> chosenCourseNames,
                                                              final List<List<CourseSectionDto>> parsedCourses) {
        final Set<String> unavailableClasses = new TreeSet<>();

        nextCourse:
        for(final String courseName : chosenCourseNames) {
            for(final List<CourseSectionDto> courseList : parsedCourses) {
                // Each courseIdentifier in courseList is the same, so just get the first
                final String courseIdentifier = courseList.get(0).getCourse();
                if(courseIdentifier.contains(courseName)) {
                    continue nextCourse;
                }
            }
            unavailableClasses.add(courseName);
        }
        return unavailableClasses;
    }

    public static Set<String> getCoursesWithAllSectionsWaitlisted(final Set<String> foundAvailableCourses, final List<List<CourseSectionDto>> parsedCourses) {
        final Set<String> waitlistedClasses = new TreeSet<>();

        for(final List<CourseSectionDto> courseList : parsedCourses) {
            // Each courseIdentifier in courseList is the same, so just get the first
            final String courseIdentifier = courseList.get(0).getCourse();
            if(!foundAvailableCourses.contains(courseIdentifier)) {
                waitlistedClasses.add(courseIdentifier);
            }
        }
        return waitlistedClasses;
    }

    public static long getNumTheoreticalPermutations(final List<List<CourseSectionDto>> parsedCourses) {
        long totalPermutations = 1;
        for(int i = 0; i < parsedCourses.size(); i++) {
            totalPermutations = totalPermutations * (parsedCourses.get(i).size());
        }
        return totalPermutations;
    }

    // TODO: I know this is bad.. just POC testing
    public static int[] incrementIterationVariables(final int[] iterationVariables,
                                                    final Map<Integer, List<CourseSectionDto>> possibleCourses,
                                                    final int numCourses, final boolean isInstantSchedules,
                                                    final Set<String> usedIterationBlocks) {
//        return incrementInterationVariablesRandom(iterationVariables, possibleCourses, numCourses, usedIterationBlocks);
        return incrementIterationVariablesSequential(iterationVariables, possibleCourses, numCourses);
    }

    public static int[] incrementIterationVariablesSequential(final int[] iterationVariables,
                                                              final Map<Integer, List<CourseSectionDto>> possibleCourses,
                                                              final int numCourses) {
        // Increment the variables up
        iterationVariables[numCourses - 1] += 1;
        for(int i = numCourses - 1; i > 0; i--) {
            if(iterationVariables[i] == possibleCourses.get(i).size()) {
                iterationVariables[i] = 0;
                iterationVariables[i - 1] += 1;
            }
        }
        return iterationVariables;
    }

    // TODO: temporary for testing; improve method later
    public static int[] incrementInterationVariablesRandom(final int[] iterationVariables,
                                                           final Map<Integer, List<CourseSectionDto>> possibleCourses,
                                                           final int numCourses, final Set<String> usedIterationBlocks) {
        while(true) {
            final StringBuilder randomIteration = new StringBuilder();
            for (int curCourse = numCourses - 1; curCourse >= 0; curCourse--) { // > vs v=
                final int courseSize = possibleCourses.get(curCourse).size();
                final int randomVar = ThreadLocalRandom.current().nextInt(0, courseSize);
                iterationVariables[curCourse] = randomVar;
                randomIteration.append(randomVar);
            }
            if (!usedIterationBlocks.contains(randomIteration.toString())) {
                usedIterationBlocks.add(randomIteration.toString());
                return iterationVariables;
            }
        }
    }


    public static Set<String> getDepartmentsFromChosenClasses(final Collection<String> chosenClasses) {
        final Set<String> departments = new HashSet<>();
        for(final String courseName : chosenClasses) {

            final String dept = courseName.contains("-") ? courseName.substring(0, courseName.indexOf("-"))
                    : courseName.substring(0, courseName.indexOf(" "));
            departments.add(dept);
        }
        return departments;
    }

    public static ChainedParserMetadata parseData(final String indexStartChar, int startOffset, final int numCharsToParse, final String inputLine) {
        final int indexStart = inputLine.indexOf(indexStartChar) + startOffset;
        final int indexEnd = indexStart + numCharsToParse;
        return parseData(indexStart, indexEnd, inputLine);
    }
    public static ChainedParserMetadata parseData(final String indexStartChar, int startOffset, final String indexEndChar, final String inputLine) {
        final int indexStart = inputLine.indexOf(indexStartChar) + startOffset;
        final int indexEnd = inputLine.indexOf(indexEndChar, indexStart); // starts the search from indexStart
        return parseData(indexStart, indexEnd, inputLine);
    }
    public static ChainedParserMetadata parseData(final int indexStart, final String indexEndChar, final String inputLine) {
        final int indexEnd = inputLine.indexOf(indexEndChar, indexStart);
        return parseData(indexStart, indexEnd, inputLine);
    }
    public static ChainedParserMetadata parseData(final int indexStart, final int indexEnd, final String inputLine) {
        try {
            final String data = inputLine.substring(indexStart, indexEnd).trim();
            final String value = cleanData(data);
            return new ChainedParserMetadata(value, indexStart, indexEnd);
        }
        catch(final Exception e) {
            System.out.println("inputLine: " + inputLine);
        }
        return null;
    }
    public static String cleanData(final String data) {
        final StringBuilder modifiedData = new StringBuilder(data);
        int indexLeft = data.indexOf('<');
        int indexRight = data.indexOf('>');
        while(indexLeft >= 0 && indexRight >= 0) {
            modifiedData.delete(indexLeft, indexRight);
            indexLeft = data.indexOf('<', indexLeft + 1);
            indexRight = data.indexOf('>', indexRight + 1);
        }
        return modifiedData.toString();
    }

}
