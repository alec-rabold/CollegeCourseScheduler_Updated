package io.collegeplanner.my.collegecoursescheduler.repository;

import io.collegeplanner.my.collegecoursescheduler.model.schema.CoursesDto;
import io.collegeplanner.my.collegecoursescheduler.model.schema.ProfessorsDto;
import io.collegeplanner.my.collegecoursescheduler.model.view.CollegeRepositoryData;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.List;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.COURSE_REGISTRATION_DATA_TABLES;
import static io.collegeplanner.my.collegecoursescheduler.util.Constants.PREPARED_QUERY_SELECT_ALL_FROM_TABLE;
import static io.collegeplanner.my.collegecoursescheduler.util.Constants.PROFESSORS_TABLES;

@UseStringTemplate3StatementLocator
public interface CourseMetadataDao {

    @SqlQuery(PREPARED_QUERY_SELECT_ALL_FROM_TABLE)
    @RegisterBeanMapper(value = ProfessorsDto.class, prefix = "p")
    List<ProfessorsDto> getProfessorsFromTable(@Define("table") final String tableName);

    @SqlQuery(PREPARED_QUERY_SELECT_ALL_FROM_TABLE)
    @RegisterBeanMapper(value = CoursesDto.class, prefix = "c")
    List<CoursesDto> getCoursesFromTable(@Define("table") final String tableName);

    default CollegeRepositoryData getRegistrationDataIndexForCollege(final String collegeName) {
        return new CollegeRepositoryData(
                this.getCoursesFromTable(COURSE_REGISTRATION_DATA_TABLES.get(collegeName)),
                this.getProfessorsFromTable(PROFESSORS_TABLES.get(collegeName))
        );
    }


}