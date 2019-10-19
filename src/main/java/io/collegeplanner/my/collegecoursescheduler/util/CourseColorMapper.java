package io.collegeplanner.my.collegecoursescheduler.util;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CourseColorMapper {
    // TODO: fix this up
    private final Map<String, Integer> courseColorMap;

    public int getCourseColor(final String uniqueCourseIdentifier) {
        if(this.courseColorMap.containsKey(uniqueCourseIdentifier)) {
            return this.courseColorMap.get(uniqueCourseIdentifier);
        }
        else {
            final List<Integer> unusedColors = new ArrayList<>();
            for(int i = 1; i < 8; i++) {
                if (!this.courseColorMap.containsValue(i)) {
                    unusedColors.add(i);
                }
            }
            if(!unusedColors.isEmpty()) {
                final int num = unusedColors.get(0);
                this.courseColorMap.put(uniqueCourseIdentifier, num);
                return num;
            }
            else {
                final int duplicateColor = (int)(Math.random()*8 + 1 );
                this.courseColorMap.put(uniqueCourseIdentifier, duplicateColor);
                return duplicateColor;
            }

        }
    }
}
