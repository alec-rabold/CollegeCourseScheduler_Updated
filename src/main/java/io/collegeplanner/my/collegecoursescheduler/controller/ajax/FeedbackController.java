package io.collegeplanner.my.collegecoursescheduler.controller.ajax;

import io.collegeplanner.my.collegecoursescheduler.repository.FeedbackDao;
import io.collegeplanner.my.collegecoursescheduler.util.DatabaseUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.FEEDBACK_BUGS_TABLE;
import static io.collegeplanner.my.collegecoursescheduler.util.Constants.FEEDBACK_SUGGESTIONS_TABLE;

@Log4j2
@RestController
@RequestMapping(value = "/ajax")
public class FeedbackController {

    @RequestMapping(value = "/submit-bug", method = RequestMethod.POST, headers = "Accept=application/json")
    public void submitUserBug(@RequestParam final String userInput) {
        DatabaseUtils.getDatabaseConnection().onDemand(FeedbackDao.class)
                .updateFeedbackTable(FEEDBACK_BUGS_TABLE, userInput);
    }

    @RequestMapping(value = "/submit-suggestion", method = RequestMethod.POST, headers = "Accept=application/json")
    public void submitUserSuggestions(@RequestParam final String userInput) {
        DatabaseUtils.getDatabaseConnection().onDemand(FeedbackDao.class)
                .updateFeedbackTable(FEEDBACK_SUGGESTIONS_TABLE, userInput);
    }
}
