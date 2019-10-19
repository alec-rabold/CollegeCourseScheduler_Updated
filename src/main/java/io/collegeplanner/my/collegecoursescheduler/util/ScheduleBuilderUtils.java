package io.collegeplanner.my.collegecoursescheduler.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ScheduleBuilderUtils {

    public static int getCourseColor(final String identifier, final Map<String, Integer> courseColors) {
        if(courseColors.containsKey(identifier)) {
            return courseColors.get(identifier);
        }
        else {
            final List<Integer> unusedColors = new ArrayList<>();
            for(int i = 1; i < 8; i++) {
                if (!courseColors.containsValue(i)) {
                    unusedColors.add(i);
                }
            }
            if(!unusedColors.isEmpty()) {
                final int num = unusedColors.get(0);
                courseColors.put(identifier, num);
                return num;
            }
            else {
                final int duplicateColor = (int)(Math.random()*8 + 1 );
                courseColors.put(identifier, duplicateColor);
                return duplicateColor;
            }

        }
    }
}
