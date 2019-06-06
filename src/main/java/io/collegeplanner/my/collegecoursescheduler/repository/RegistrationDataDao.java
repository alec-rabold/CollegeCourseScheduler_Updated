package io.collegeplanner.my.collegecoursescheduler.repository;

import io.collegeplanner.my.collegecoursescheduler.model.schema.CoursesDto;
import io.collegeplanner.my.collegecoursescheduler.model.schema.ProfessorsDto;
import io.collegeplanner.my.collegecoursescheduler.model.view.CollegeRepositoryData;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.*;

@UseStringTemplate3StatementLocator
public interface RegistrationDataDao {

    @SqlUpdate(PREPARED_QUERY_CREATE_PROFESSORS_TABLE)
    void createProfessorsTableIfNotExists(@Define("table") final String tableName);

    @SqlUpdate(PREPARED_QUERY_CREATE_SUBJECTS_TABLE)
    void createSubjectsTableIfNotExists(@Define("table") final String tableName);

    @SqlUpdate(PREPARED_QUERY_CREATE_COURSES_TABLE)
    void createCoursesTableIfNotExists(@Define("table") final String tableName);

    @SqlBatch(PREPARED_QUERY_UPDATE_PROFESSORS_TABLE)
    void updateProfessorsTableBulk(@Define("table") final String tableName,
                                   @Bind("name") final Set<String> names, @Bind("value") final Collection<String> values);

    @SqlBatch(PREPARED_QUERY_UPDATE_SUBJECTS_TABLE)
    void updateSubjectsTableBulk(@Define("table") final String tableName,
                                   @Bind("subjAbbr") final Set<String> subjectsAbbr, @Bind("subjFull") final Collection<String> subjectsFull);

    @SqlBatch(PREPARED_QUERY_UPDATE_COURSES_TABLE)
    void updateCoursesTableBulk(@Define("table") final String tableName,
                                @Bind("courseName") final Set<String> courseName, @Bind("title")
                                final Collection<String> titles, @Bind("courseId") final Set<String> courseIds);

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