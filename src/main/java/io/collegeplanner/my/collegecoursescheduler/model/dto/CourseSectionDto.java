package io.collegeplanner.my.collegecoursescheduler.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class CourseSectionDto implements Serializable {
    private String courseID;
    private String title;
    private String scheduleNum;
    private String units;
    private String seats;
    private List<String> days = new ArrayList<>();
    private List<String> times = new ArrayList<>();
    private List<String> locations = new ArrayList<>();
    private List<String> instructors = new ArrayList<>();
    private CourseSectionDto parentCourse;

//    @JsonIgnore
//    public boolean isComplete() {
//        return StringUtils.isNoneEmpty(courseID, scheduleNum, title, units, seats) && !times.isEmpty();
//    }

    @JsonIgnore
    public boolean isComplete() {
        return StringUtils.isNoneEmpty(courseID, scheduleNum, title, units) && !times.isEmpty();
    }
}
