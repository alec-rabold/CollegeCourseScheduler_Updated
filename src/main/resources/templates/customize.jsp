<%@ page contentType="text/html;charset=UTF-8" trimDirectiveWhitespaces="true" language="java" %>
<%@ page import="io.collegeplanner.my.collegecoursescheduler.util.Constants" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>

<jsp:include page="header.jsp"/>

<!----------- FORM--------------->
<form id="preferences-form" method="POST" action="${requestScope['javax.servlet.forward.servlet_path']}${Constants.RESULTS_PERMUTATIONS_RELATIVE_PATH}">
    <div class="block-area">
        <h2 class="form bold" style="margin-bottom: 0px">Step 2: Enter your classes</h2>
    </div>
    <!-- Term -->
    <div class="block-area">
        <div id="termYear">
            <h4 class="form-h4"><span class="text-red">* </span>Select the term:</h4>
            <div class="row">
                <div class="col-xs-4 col-md-2">
                    <select name="season" class="form-control">
                    <c:choose>
                        <c:when test="${collegeName == Constants.UNIVERSITY_OF_WASHINGTON}">
                            <option>Fall</option>
                            <option>Winter</option>
                            <option selected="selected">Spring</option>
                            <option>Summer</option>
                        </c:when>
                        <c:otherwise>
                            <option>Fall</option>
                            <option>Winter</option>
                            <option selected="selected">Spring</option>
                            <option>Summer</option>
                        </c:otherwise>
                    </c:choose>
                    </select>
                </div>
                <div class="col-xs-4 col-md-2">
                    <select name="year" class="form-control">
                        <option selected="selected">2020</option>
                        <option>2019</option>
                        <option>2018</option>
                        <option>2017</option>
                        <option>2016</option>
                        <option>2015</option>
                        <option>2014</option>
                        <option>2013</option>
                        <option>2012</option>
                    </select>
                </div>
                <div class="col-xs-4 col-md-8"></div>
            </div>
        </div>
    </div>
    <!-- /Term -->
    <!-- Needed classes -->
    <div class="block-area">
        <div id="needed-classes-form">
            <h4 class="form-h4"><span class="text-red">* </span>Select your classes:</h4>
            <div class="row">
                <div class="col-xs-12 col-md-9">
                    <select name="chosenCourses" data-placeholder="Select needed classes..." class="tag-select" multiple required>
                        <c:forEach items="${registrationData.coursesMetadata}" var="course">
                            <c:out escapeXml="false" value="<option value='${course.id}'/>${course.name} | ${course.title}"/>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-xs-0 col-md-3"></div>
            </div>
        </div>
    </div>
    <!-- /Needed classes -->

    <div class="block-area">
        <h2 class="form bold">Step 3: Choose your preferences</h2>
        <!-- Unavailable -->
        <div id="appendage" data-count="1">
            <div id="timeConflicts">
                <h4 class="form-h4">Set your unavailable times:</h4>

                <div class="row">
                    <div class="col-xs-5 col-md-4 m-b-15">
                        <p>No classes from:</p>
                        <div class="input-icon datetime-pick time-only-12">
                            <input data-format="hh:mm:ss" name="unavailableTimeblockStart" value="08:00:00" type="text" class="form-control input-sm" />
                            <span class="add-on">
                                <i class="sa-plus"></i>
                            </span>
                        </div>
                    </div>
                    <div class="col-xs-1">
                        <p style="text-align:center;"><br><br>until</p> <!-- Make text-align-center class in CSS -->
                    </div>
                    <div class="col-xs-5 col-md-4 m-b-15">
                        <p><br></p>
                        <div class="input-icon datetime-pick time-only-12">
                            <input data-format="hh:mm:00" name="unavailableTimeblockEnd" placeholder="Select the time you'd be willing to start..." type="text" class="form-control input-sm" />
                            <span class="add-on">
                                <i class="sa-plus"></i>
                            </span>
                        </div>
                    </div>
                    <div class="col-xs-1 col-md-3"></div>
                </div>
                <div class="row">
                    <div class="col-md-1"></div>
                    <div class="col-xs-11 col-md-6" style="padding-bottom: 15px;">
                        <input class="timeblockDays" type="hidden" name="unavailableTimeblockDays"/>
                        <label class="checkbox-inline">
                            <input id="monday_sel" class="days" type="checkbox" value="M"> Mon
                        </label>
                        <label class="checkbox-inline">
                            <input id="tuesday_sel" class="days" type="checkbox" value="T"> Tues
                        </label>
                        <label class="checkbox-inline">
                            <input id="wendesday_sel" class="days" type="checkbox" value="W"> Wed
                        </label>
                        <label class="checkbox-inline">
                            <input id="thursday_sel" class="days" type="checkbox" value="R"> Thurs
                        </label>
                        <label class="checkbox-inline">
                            <input id="friday_sel" class="days" type="checkbox" value="F"> Fri
                        </label>
                    </div>
                    <div class="col-xs-7 col-xs-offset-2 col-md-offset-0 col-md-3 " style="display: none">
                        <button class="btn btn-block btn-alt removeTimeblock" data-num="1" onclick="return false">Remove timeblock</button>
                    </div>
                    <div class="col-md-2"></div>
                </div>
            </div>
        </div>
        <div class="col-xs-11 col-md-7 p-l-20 p-r-15 m-t-15">
            <button id="addTimeblock" class="btn btn-block btn-alt" onclick="return false">Add another timeblock</button>
        </div>
    </div>
    <!-- /Unavailable -->

    <div id="loadedProfessors" style="display: none;">
        <c:forEach items="${registrationData.professorsMetadata}" var="professors">
            <option><c:out value="${professors.name}"/></option>
        </c:forEach>
    </div>

    <!-- Preferred professors -->
    <div class="block-area">
        <div id="wantedProfessors" class="professorsDB">
            <h4 class="form-h4">Prioritize (preferred) professors:</h4>
            <div class="row">
                <div class="col-xs-12 col-md-9">
                    <select id="favoredProfs" name="favoredProfessors" data-native-menu="false"  data-placeholder="Search and select wanted professors..." class="tag-select" multiple>

                    </select>
                </div>
                <div class="col-xs-0 col-md-3"></div>
            </div>
        </div>
    </div>
    <!-- /Preferred professors -->

    <!-- Unwanted professors -->
    <div class="block-area">
        <div id="unwantedProfessors" class="professorsDB">
            <h4 class="form-h4">De-prioritize (unwanted) professors:</h4>
            <div class="row">
                <div class="col-xs-12 col-md-9">
                    <select id="disfavoredProfs" name="disfavoredProfessors" data-native-menu="false"  data-placeholder="Search and select unwanted professors..." class="tag-select" multiple>

                    </select>
                </div>
                <div class="col-xs-0 col-md-3"></div>
            </div>
        </div>
    </div>
    <!-- /Unwanted professors -->

    <!-- Exclude professors -->
    <div class="block-area">
        <div id="excludeProfessors" class="professorsDB">
            <h4 class="form-h4">Exclude professors:</h4>
            <div class="row">
                <div class="col-xs-12 col-md-9">
                    <select id="excludedProfs" name="excludedProfessors" data-native-menu="false" data-placeholder="Search and exclude unwanted professors..." class="tag-select" multiple>

                    </select>
                </div>
                <div class="col-xs-0 col-md-3"></div>
            </div>
        </div>
    </div>
    <!-- /Exclude professors -->

    <!-- Relaxed/Tight, Waitlisted, Online -->
    <div class="block-area">
        <div class="row">
            <div id="relaxed-tight-form" class="col-xs-12 col-md-5">
                <h4 class="form-h4"><span class="text-red">* </span>Which schedule do you prefer?</h4>
                <div class="row">
                    <div class="col-xs-12 col-md-9">
                        <div class="radio">
                            <label>
                                <input type="radio" name="relaxedVsCompactPreference" value="tight" checked="checked" required>
                                Tight schedule (less breaks between classes)
                            </label>
                        </div>
                        <div class="radio">
                            <label>
                                <input type="radio" name="relaxedVsCompactPreference" value="relaxed">
                                Relaxed schedule (more breaks between classes)
                            </label>
                        </div>
                    </div>
                    <div class="col-xs-0 col-md-3"></div>
                </div>
            </div>

            <div id="waitlist-form" class="col-xs-12 col-md-4">
                <h4 class="form-h4"><span class="text-red">* </span>Do you want to show full classes?</h4>
                <div class="row">
                    <div class="col-xs-12">
                        <div class="radio">
                            <label>
                                <input type="radio" name="includeWaitlistedCourses" value="true" checked="checked" required>
                                Yes, I want to see them in case I decide to waitlist
                            </label>
                        </div>
                        <div class="radio">
                            <label>
                                <input type="radio" name="includeWaitlistedCourses" value="false">
                                No, I am not planning on waitlisting any classes
                            </label>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-xs-0 col-md-3"></div>
        </div>
        <div class="row">
            <div id="online-form" class="col-xs-12 col-md-5">
                <h4 class="form-h4"><span class="text-red">* </span>Show hybrid (half-online) classes?</h4>
                <div class="row">
                    <div class="col-xs-12 col-md-9">
                        <div class="radio">
                            <label>
                                <input type="radio" name="includeOnlineCourses" value="true" checked="checked" required>
                                Yes, include hybrid classes
                            </label>
                        </div>
                        <div class="radio">
                            <label>
                                <input type="radio" name="includeOnlineCourses" value="false">
                                No, do not include hybrid classes
                            </label>
                        </div>
                    </div>
                    <div class="col-xs-0 col-md-3"></div>
                </div>
            </div>

            <div id="numDays-form" class="col-xs-12 col-md-5">
                <h4 class="form-h4"><span class="text-red">* </span>Fewer days or shorter classes?</h4>
                <div class="row">
                    <div class="col-xs-12 col-md-9">
                        <div class="radio">
                            <label>
                                <input type="radio" name="shorterVsFewerClassesPreference" value="fewer" checked="checked" required>
                                I prefer having fewer days, but with longer classes
                            </label>
                        </div>
                        <div class="radio">
                            <label>
                                <input type="radio" name="shorterVsFewerClassesPreference" value="more">
                                I prefer having shorter classes, but on more days
                            </label>
                        </div>
                    </div>
                    <div class="col-xs-0 col-md-3"></div>
                </div>
            </div>
            <div id="api-sel" class="col-xs-12 col-md-5">
                <h4 class="form-h4"><span class="text-red">* </span>Preview the experimental API?</h4>
                <div class="row">
                    <div class="col-xs-12 col-md-9">
                        <div class="radio">
                            <label>
                                <input type="radio" name="api_key" value="8ad0544d-62d2-4937-8d5a-2f5dae04209e" required>
                                Yes
                            </label>
                        </div>
                        <div class="radio">
                            <label>
                                <input type="radio" name="api_key" checked="checked" value="e652ec27-7b0d-4a24-9611-ce5ff46a6f08">
                                No
                            </label>
                        </div>
                    </div>
                    <div class="col-xs-0 col-md-3"></div>
                </div>
            </div>
            <div class="col-xs-12 col-md-5">
                <h4 class="form-h4"><span class="text-red">* </span>Random schedules or ranked schedules?</h4>
                <div class="row">
                    <div class="col-xs-12 col-md-9">
                        <div class="radio">
                            <label>
                                <input type="radio" name="instantRandomSchedules" value="true" checked="checked" required>
                                Random schedules (fastest)
                            </label>
                        </div>
                        <div class="radio">
                            <label>
                                <input type="radio" name="instantRandomSchedules" value="false">
                                Ranked Schedules (slower)
                            </label>
                        </div>
                    </div>
                    <div class="col-xs-0 col-md-3"></div>
                </div>
            </div>
        </div>
        <div class="row">
            <div id="problems-form" class="feedback-box col-xs-12 col-md-4">
                <h4 class="form-h4"><span class="text-red"></span>Found a bug?</h4>
                    <div class="feedback-box col-xs-12" style="padding-top: 10px">
                        <textarea id="bug-text" class="form-control textarea-autosize" placeholder="Let us know here..." style="overflow: hidden; word-wrap: break-word; height: 48px;"></textarea>
                        <button id="bug-button" type="button" class="btn-absolute btn-alt">Submit</button>
                    </div>
            </div>
            <div id="suggestions-form" class="feedback-box col-xs-12 col-md-4 col-md-offset-1">
                <h4 class="form-h4"><span class="text-red"></span>Any suggestions for us?</h4>
                <div class="feedback-box col-xs-12" style="padding-top: 10px">
                    <textarea id="suggestion-text" class="form-control textarea-autosize" placeholder="Tell us what we can improve..." style="overflow: hidden; word-wrap: break-word; height: 48px;"></textarea>
                    <button id="suggestion-button" type="button" class="btn-absolute btn-alt">Submit</button>
                </div>
            </div>
        </div>
            <div class="col-xs-0 col-md-3"></div>
        </div>
    </div>
    <!-- /Relaxed or Tight schedule && Waitlist -->
    <div class="col-xs-12 col-md-8 p-l-20 p-r-15 m-t-15">
        <button id="submit-preferences" class="btn btn-block btn-alt m-b-20" type="submit">Submit!</button>
    </div>
</form>


<jsp:include page="footer.jsp"/>



