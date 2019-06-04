package io.collegeplanner.my.collegecoursescheduler.model.dto;

import lombok.Data;

@Data
public class UnavailableTimeblocks {
    // TODO: regex validator(?)
    private final String[] timeStart; // hh:mm:ss
    private final String[] timeEnd; // hh:mm:ss
    private final String[] days; // MWF, MTW, etc..
}
