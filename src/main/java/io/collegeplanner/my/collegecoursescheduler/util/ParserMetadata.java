package io.collegeplanner.my.collegecoursescheduler.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ParserMetadata {
    private String data;
    private int startIndex;
    private int endIndex;
}