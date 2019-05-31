package io.collegeplanner.my.collegecoursescheduler.repository;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.PREPARED_QUERY_UPDATE_FEEDBACK_TABLE;

@UseStringTemplate3StatementLocator
public interface FeedbackDao {

    @SqlUpdate(PREPARED_QUERY_UPDATE_FEEDBACK_TABLE)
    void updateFeedbackTable(@Define("table") final String tableName, @Bind("userInput") final String userInput);
}
