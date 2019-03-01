package io.collegeplanner.my.collegecoursescheduler.model.view;

import io.collegeplanner.my.collegecoursescheduler.model.schema.CoursesDto;
import io.collegeplanner.my.collegecoursescheduler.model.schema.ProfessorsDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CollegeRepositoryData {
    private List<CoursesDto> coursesMetadata;
    private List<ProfessorsDto> professorsMetadata;
}
