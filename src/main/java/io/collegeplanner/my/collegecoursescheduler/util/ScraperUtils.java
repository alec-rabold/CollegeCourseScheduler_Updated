package io.collegeplanner.my.collegecoursescheduler.util;

import io.collegeplanner.my.collegecoursescheduler.model.dto.CourseSectionDto;
import org.apache.commons.math3.util.CombinatoricsUtils;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ScraperUtils {

    public static Set<String> getCoursesWithNoSectionsOffered(final List<String> chosenCourseNames,
                                                              final List<List<CourseSectionDto>> parsedCourses) {
        final Set<String> unavailableClasses = new TreeSet<>();

        nextCourse:
        for(final String courseName : chosenCourseNames) {
            for(final List<CourseSectionDto> courseList : parsedCourses) {
                // Each courseIdentifier in courseList is the same, so just get the first
                final String courseIdentifier = courseList.get(0).getCourseID();
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
            final String courseIdentifier = courseList.get(0).getCourseID();
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

    public static int[] incrementIterationVariables(final int[] iterationVariables,
                                                    final Map<Integer, List<CourseSectionDto>> possibleCourses, final int numCourses) {

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
}
