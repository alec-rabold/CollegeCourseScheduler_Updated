package io.collegeplanner.my.collegecoursescheduler.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// Temporary until I add collegeName to ApiRequestDto model
public class KinesisRecordDto {
    private String collegeName;
    @JsonUnwrapped
    @JsonProperty("apiRequest")
    private ApiRequestDto apiRequestDto;
}
