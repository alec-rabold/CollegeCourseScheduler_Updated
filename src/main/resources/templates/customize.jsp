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
                            <option selected="selected">Fall</option>
                            <option>Winter</option>
                            <option>Spring</option>
                            <option>Summer</option>
                        </c:when>
                        <c:otherwise>
                            <option selected="selected">Fall</option>
                            <option>Winter</option>
                            <option>Spring</option>
                            <option>Summer</option>
                        </c:otherwise>
                    </c:choose>
                    </select>
                </div>
                <div class="col-xs-4 col-md-2">
                    <select name="year" class="form-control">
                        <option selected="selected">2019</option>
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
                            <input data-format="hh:mm:ss" name="unavailableBlockTimesStart" value="08:00:00" type="text" class="form-control input-sm" />
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
                            <input data-format="hh:mm:00" name="unavailableBlockTimesEnd" placeholder="Select the time you'd be willing to start..." type="text" class="form-control input-sm" />
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
                        <label class="checkbox-inline">
                            <input class="days" type="checkbox" name="mondaysChosen" value="0"> Mon
                        </label>
                        <label class="checkbox-inline">
                            <input class="days" type="checkbox" name="tuesdaysChosen" value="0"> Tues
                        </label>
                        <label class="checkbox-inline">
                            <input class="days" type="checkbox" name="wednesdaysChosen" value="0"> Wed
                        </label>
                        <label class="checkbox-inline">
                            <input class="days" type="checkbox" name="thursdaysChosen" value="0"> Thurs
                        </label>
                        <label class="checkbox-inline">
                            <input class="days" type="checkbox" name="fridaysChosen" value="0"> Fri
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
                    <select id="wantedProfs" name="wantedProfessors" data-native-menu="false"  data-placeholder="Search and select wanted professors..." class="tag-select" multiple>

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
                    <select id="unwantedProfs" name="unwantedProfessors" data-native-menu="false"  data-placeholder="Search and select unwanted professors..." class="tag-select" multiple>

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
                                <input type="radio" name="spreadPreference" value="tight" checked="checked" required>
                                Tight schedule (less breaks between classes)
                            </label>
                        </div>
                        <div class="radio">
                            <label>
                                <input type="radio" name="spreadPreference" value="relaxed">
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
                                <input type="radio" name="doShowWaitlisted" value="true" checked="checked" required>
                                Yes, I want to see them in case I decide to waitlist
                            </label>
                        </div>
                        <div class="radio">
                            <label>
                                <input type="radio" name="doShowWaitlisted" value="false">
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
                                <input type="radio" name="doShowOnline" value="true" checked="checked" required>
                                Yes, include hybrid classes
                            </label>
                        </div>
                        <div class="radio">
                            <label>
                                <input type="radio" name="doShowOnline" value="false">
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
                                <input type="radio" name="numClassesPerWeekPreference" value="fewer" checked="checked" required>
                                I prefer having fewer days, but with longer classes
                            </label>
                        </div>
                        <div class="radio">
                            <label>
                                <input type="radio" name="numClassesPerWeekPreference" value="more">
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
                                <input type="radio" name="doApi" value="true" required>
                                Yes
                            </label>
                        </div>
                        <div class="radio">
                            <label>
                                <input type="radio" name="doApi" checked="checked" value="false">
                                No
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
                        <!--textarea name="problemsTextbox" class="form-control textarea-autosize" placeholder="Let us know here..." style="overflow: hidden; word-wrap: break-word; height: 48px;"></textarea-->
                        <textarea id="bug-text" class="form-control textarea-autosize" placeholder="Let us know here..." style="overflow: hidden; word-wrap: break-word; height: 48px;"></textarea>
                        <button id="bug-button" class="btn-absolute btn-alt">Submit</button>
                    </div>
            </div>
            <div id="suggestions-form" class="feedback-box col-xs-12 col-md-4 col-md-offset-1">
                <h4 class="form-h4"><span class="text-red"></span>Any suggestions for us?</h4>
                <div class="feedback-box col-xs-12" style="padding-top: 10px">
                    <!--textarea name="suggestionsTextbox" class="form-control textarea-autosize" placeholder="Tell us what we can improve..." style="overflow: hidden; word-wrap: break-word; height: 48px;"></textarea-->
                    <textarea id="suggestion-text" class="form-control textarea-autosize" placeholder="Tell us what we can improve..." style="overflow: hidden; word-wrap: break-word; height: 48px;"></textarea>
                    <button id="suggestion-button" class="btn-absolute btn-alt">Submit</button>
                </div>
            </div>
        </div>
            <div class="col-xs-0 col-md-3"></div>
        </div>
    </div>
    <!-- /Relaxed or Tight schedule && Waitlist -->

    <!-- Determine if mobile -->
    <input type="hidden" name="isMobileBrowser" id="isMobileBrowser" value="false">
    <script>
        $(document).ready(function() {
        var isMobile = window.matchMedia("only screen and (max-width: 760px)");
        if (isMobile.matches) {
            $('#isMobileBrowser').val("true");
        }
        });
    </script>
    <!-- /Determine if mobile -->

    <div class="col-xs-12 col-md-8 p-l-20 p-r-15 m-t-15">
        <button id="submit-preferences" class="btn btn-block btn-alt m-b-20" type="submit">Submit!</button>
    </div>
</form>


<jsp:include page="footer.jsp"/>



