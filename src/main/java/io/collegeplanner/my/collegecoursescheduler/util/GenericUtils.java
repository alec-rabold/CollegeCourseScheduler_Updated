package io.collegeplanner.my.collegecoursescheduler.util;

import java.util.Arrays;
import java.util.Set;

public final class GenericUtils {

    // Calculates difference between provided time and current time
    //   returns String value to 3 decimal places
    public static String getFormattedElapsedTime(final Long startTime) {
        return String.format("%.3f", (System.currentTimeMillis() - startTime) / 1000.0f);
    }

    public static boolean stringContainsItemFromList(final String inputStr, final String[] items) {
        return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
    }
    public static boolean stringContainsItemFromSet(final String inputStr, final Set<String> items) {
        final String[] itemsArr = new String[items.size()];
        return stringContainsItemFromList(inputStr, items.toArray(itemsArr));
    }
}
