package io.collegeplanner.my.collegecoursescheduler.model.view;

import io.collegeplanner.my.collegecoursescheduler.model.schema.CoursesDto;
import io.collegeplanner.my.collegecoursescheduler.model.schema.ProfessorsDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CollegeRepositoryData {
    private List<CoursesDto> coursesMetadata;
    private List<ProfessorsDto> professorsMetadata;
}
