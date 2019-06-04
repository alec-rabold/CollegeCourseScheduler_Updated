<%@ page contentType="text/html;charset=UTF-8" trimDirectiveWhitespaces="true" language="java" %>
<%@ page import="io.collegeplanner.my.collegecoursescheduler.util.Constants" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>

<jsp:include page="header.jsp"/>

<!----------- COLLEGE --------------->
<div class="block-area">
    <div class="row">
        <!--div class="col-xs-11 col-md-offset-3 col-md-6"-->
        <div class="col-xs-11 col-md-offset-2 col-md-8">
            <h2 class="form bold f-shadow" style="text-align: center">Step 1: Select your school</h2>
            <hr class="preview-rule">
            <div class="col-xs-12 col-md-offset-2 col-md-8">
                <!--button class="banner btn btn-block btn-alt university" onclick="location.href='${Constants.VSB_COMPLETE_PATH_UW}'" style="margin-top: 20px;">University of Washington</button>
                <button class="banner btn btn-block btn-alt university" onclick="location.href='${Constants.VSB_COMPLETE_PATH_BERKELEY}'">UC Berkeley</button>
                <button class="banner btn btn-block btn-alt university" onclick="location.href='${Constants.VSB_COMPLETE_PATH_BROWN}'">Brown University</button>
                <button class="banner btn btn-block btn-alt university" onclick="location.href='${Constants.VSB_COMPLETE_PATH_UCSB}'">UC Santa Barbara</button-->
                <button class="banner btn btn-block btn-alt university" onclick="location.href='${Constants.VSB_COMPLETE_PATH_SDSU}'">San Diego State University</button>
                <button class="banner btn btn-block btn-alt disabled">Big Update Coming Soon!</button>
                <button class="banner btn btn-block btn-alt disabled">700+ new universities to be supported</button>
            </div>
        </div>
    </div>
</div>

<div class="block-area preview-sched">
    <hr class="preview-rule">
    <h2 class="form f-shadow bold" style="margin: 5px 0px 15px 0px; text-align: center;">Preview</h2>
    <div id="myCarousel" class="carousel slide" data-ride="carousel" data-interval="false">
        <div class="carousel-inner">
            <div class="item active">
                <table class="optimized-table">
                    <tbody><tr>
                        <th class="opt-table-label opt-table-time"></th>
                        <th class="opt-table-label">Monday</th>
                        <th class="opt-table-label">Tuesday</th>
                        <th class="opt-table-label">Wednesday</th>
                        <th class="opt-table-label">Thursday</th>
                        <th class="opt-table-label">Friday</th>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">8:00 am</td>
                        <td></td>
                        <td rowspan="5" class="tableCourse opt-class-2 RWS-100_23001_0h" data-toggle="modal" data-courseid="RWS-100_23001_0" data-target="#tableModal">
                            <p id="courseID"><b>RWS-100</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>RHETORIC WRITTEN ARGUMENT</i></i></p><p></p><p id="instructors">C. SIGMON</p>
                                <p id="times">8:00am - 9:15am</p>
                                <p id="schedNum">Schedule #: 23001</p>
                            </div>
                            <div style="display:none" class="RWS-100_23001_0 temp">
                                <p id="title"><i>RHETORIC WRITTEN ARGUMENT</i></p>
                                <p id="instructors">C. SIGMON</p>
                                <p id="times">8:00am - 9:15am</p>
                                <p id="location">SH-320</p>
                                <p id="schedNum">Schedule #: 23001</p>
                                <p id="seats" class="text-red">Available seats: -5/30</p>
                            </div>
                        </td>
                        <td></td>
                        <td rowspan="5" class="tableCourse opt-class-2 RWS-100_23001_0h" data-toggle="modal" data-courseid="RWS-100_23001_0" data-target="#tableModal">
                            <p id="courseID"><b>RWS-100</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>RHETORIC WRITTEN ARGUMENT</i></i></p><p></p><p id="instructors">C. SIGMON</p>
                                <p id="times">8:00am - 9:15am</p>
                                <p id="schedNum">Schedule #: 23001</p>
                            </div>
                            <div style="display:none" class="RWS-100_23001_0 temp">
                                <p id="title"><i>RHETORIC WRITTEN ARGUMENT</i></p>
                                <p id="instructors">C. SIGMON</p>
                                <p id="times">8:00am - 9:15am</p>
                                <p id="location">SH-320</p>
                                <p id="schedNum">Schedule #: 23001</p>
                                <p id="seats" class="text-red">Available seats: -5/30</p>
                            </div>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">8:30 am</td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">9:00 am</td>
                        <td rowspan="3" class="tableCourse opt-class-3 MATH-150_22120_0h" data-toggle="modal" data-courseid="MATH-150_22120_0" data-target="#tableModal">
                            <p id="courseID"><b>MATH-150</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>CALCULUS I</i></i></p><p></p><p id="instructors">S. KIRSCHVINK</p>
                                <p id="times">9:00am - 9:50am</p>
                                <p id="schedNum">Schedule #: 22120</p>
                            </div>
                            <div style="display:none" class="MATH-150_22120_0 temp">
                                <p id="title"><i>CALCULUS I</i></p>
                                <p id="instructors">S. KIRSCHVINK</p>
                                <p id="times">9:00am - 9:50am</p>
                                <p id="location">HH-130</p>
                                <p id="schedNum">Schedule #: 22120</p>
                                <p id="seats" class="text-red">Available seats: 0/31</p>
                            </div>
                        </td>
                        <td rowspan="3" class="tableCourse opt-class-3 MATH-150_22120_0h" data-toggle="modal" data-courseid="MATH-150_22120_0" data-target="#tableModal">
                            <p id="courseID"><b>MATH-150</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>CALCULUS I</i></i></p><p></p><p id="instructors">S. KIRSCHVINK</p>
                                <p id="times">9:00am - 9:50am</p>
                                <p id="schedNum">Schedule #: 22120</p>
                            </div>
                            <div style="display:none" class="MATH-150_22120_0 temp">
                                <p id="title"><i>CALCULUS I</i></p>
                                <p id="instructors">S. KIRSCHVINK</p>
                                <p id="times">9:00am - 9:50am</p>
                                <p id="location">HH-130</p>
                                <p id="schedNum">Schedule #: 22120</p>
                                <p id="seats" class="text-red">Available seats: 0/31</p>
                            </div>
                        </td>
                        <td rowspan="3" class="tableCourse opt-class-3 MATH-150_22120_0h" data-toggle="modal" data-courseid="MATH-150_22120_0" data-target="#tableModal">
                            <p id="courseID"><b>MATH-150</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>CALCULUS I</i></i></p><p></p><p id="instructors">S. KIRSCHVINK</p>
                                <p id="times">9:00am - 9:50am</p>
                                <p id="schedNum">Schedule #: 22120</p>
                            </div>
                            <div style="display:none" class="MATH-150_22120_0 temp">
                                <p id="title"><i>CALCULUS I</i></p>
                                <p id="instructors">S. KIRSCHVINK</p>
                                <p id="times">9:00am - 9:50am</p>
                                <p id="location">HH-130</p>
                                <p id="schedNum">Schedule #: 22120</p>
                                <p id="seats" class="text-red">Available seats: 0/31</p>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">9:30 am</td>
                        <td rowspan="5" class="tableCourse opt-class-4 MATH-254_22174_0h" data-toggle="modal" data-courseid="MATH-254_22174_0" data-target="#tableModal">
                            <p id="courseID"><b>MATH-254</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>INTRO TO LINEAR ALGEBRA</i></i></p><p></p><p id="instructors">P. Blomgren</p>
                                <p id="times">9:30am - 10:45am</p>
                                <p id="schedNum">Schedule #: 22174</p>
                            </div>
                            <div style="display:none" class="MATH-254_22174_0 temp">
                                <p id="title"><i>INTRO TO LINEAR ALGEBRA</i></p>
                                <p id="instructors">P. Blomgren</p>
                                <p id="times">9:30am - 10:45am</p>
                                <p id="location">SHW-011</p>
                                <p id="schedNum">Schedule #: 22174</p>
                                <p id="seats">Available seats: 38/200</p>
                            </div>
                        </td>
                        <td rowspan="5" class="tableCourse opt-class-4 MATH-254_22174_0h" data-toggle="modal" data-courseid="MATH-254_22174_0" data-target="#tableModal">
                            <p id="courseID"><b>MATH-254</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>INTRO TO LINEAR ALGEBRA</i></i></p><p></p><p id="instructors">P. Blomgren</p>
                                <p id="times">9:30am - 10:45am</p>
                                <p id="schedNum">Schedule #: 22174</p>
                            </div>
                            <div style="display:none" class="MATH-254_22174_0 temp">
                                <p id="title"><i>INTRO TO LINEAR ALGEBRA</i></p>
                                <p id="instructors">P. Blomgren</p>
                                <p id="times">9:30am - 10:45am</p>
                                <p id="location">SHW-011</p>
                                <p id="schedNum">Schedule #: 22174</p>
                                <p id="seats">Available seats: 38/200</p>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">10:00 am</td>
                        <td rowspan="3" class="tableCourse opt-class-5 ART-258_20223_0h" data-toggle="modal" data-courseid="ART-258_20223_0" data-target="#tableModal">
                            <p id="courseID"><b>ART-258</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>INTRO TO ART HISTORY</i></i></p><p></p><p id="instructors">A. WOODS</p>
                                <p id="times">10:00am - 10:50am</p>
                                <p id="schedNum">Schedule #: 20223</p>
                            </div>
                            <div style="display:none" class="ART-258_20223_0 temp">
                                <p id="title"><i>INTRO TO ART HISTORY</i></p>
                                <p id="instructors">A. WOODS</p>
                                <p id="times">10:00am - 10:50am</p>
                                <p id="location">HH-221</p>
                                <p id="schedNum">Schedule #: 20223</p>
                                <p id="seats">Available seats: 29/108</p>
                            </div>
                        </td>
                        <td rowspan="3" class="tableCourse opt-class-5 ART-258_20223_0h" data-toggle="modal" data-courseid="ART-258_20223_0" data-target="#tableModal">
                            <p id="courseID"><b>ART-258</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>INTRO TO ART HISTORY</i></i></p><p></p><p id="instructors">A. WOODS</p>
                                <p id="times">10:00am - 10:50am</p>
                                <p id="schedNum">Schedule #: 20223</p>
                            </div>
                            <div style="display:none" class="ART-258_20223_0 temp">
                                <p id="title"><i>INTRO TO ART HISTORY</i></p>
                                <p id="instructors">A. WOODS</p>
                                <p id="times">10:00am - 10:50am</p>
                                <p id="location">HH-221</p>
                                <p id="schedNum">Schedule #: 20223</p>
                                <p id="seats">Available seats: 29/108</p>
                            </div>
                        </td>
                        <td rowspan="3" class="tableCourse opt-class-5 ART-258_20223_0h" data-toggle="modal" data-courseid="ART-258_20223_0" data-target="#tableModal">
                            <p id="courseID"><b>ART-258</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>INTRO TO ART HISTORY</i></i></p><p></p><p id="instructors">A. WOODS</p>
                                <p id="times">10:00am - 10:50am</p>
                                <p id="schedNum">Schedule #: 20223</p>
                            </div>
                            <div style="display:none" class="ART-258_20223_0 temp">
                                <p id="title"><i>INTRO TO ART HISTORY</i></p>
                                <p id="instructors">A. WOODS</p>
                                <p id="times">10:00am - 10:50am</p>
                                <p id="location">HH-221</p>
                                <p id="schedNum">Schedule #: 20223</p>
                                <p id="seats">Available seats: 29/108</p>
                            </div>
                        </td>
                    </tr>
                    <tr>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">10:30 am</td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">11:00 am</td>
                        <td rowspan="3" class="tableCourse opt-class-2 RWS-101_23005_0h" data-toggle="modal" data-courseid="RWS-101_23005_0" data-target="#tableModal">
                            <p id="courseID"><b>RWS-101</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>RHETORIC WRITTEN ARGUMENT</i></i></p><p></p><p id="instructors">J. TOWNER</p>
                                <p id="times">11:00am - 11:50am</p>
                                <p id="schedNum">Schedule #: 23005</p>
                            </div>
                            <div style="display:none" class="RWS-101_23005_0 temp">
                                <p id="title"><i>RHETORIC WRITTEN ARGUMENT</i></p>
                                <p id="instructors">J. TOWNER</p>
                                <p id="times">11:00am - 11:50am</p>
                                <p id="location">LSS-246</p>
                                <p id="schedNum">Schedule #: 23005</p>
                                <p id="seats">Available seats: 8/30</p>
                            </div>
                        </td>
                        <td rowspan="5" class="tableCourse opt-class-6 CS-237_21053_0h" data-toggle="modal" data-courseid="CS-237_21053_0" data-target="#tableModal">
                            <p id="courseID"><b>CS-237</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>MACHINE ORG&amp;ASSEMBLY LANG</i></i></p><p></p><p id="instructors">L. RIGGINS</p>
                                <p id="times">11:00am - 12:15pm</p>
                                <p id="schedNum">Schedule #: 21053</p>
                            </div>
                            <div style="display:none" class="CS-237_21053_0 temp">
                                <p id="title"><i>MACHINE ORG&amp;ASSEMBLY LANG</i></p>
                                <p id="instructors">L. RIGGINS</p>
                                <p id="times">11:00am - 12:15pm</p>
                                <p id="location">HT-183</p>
                                <p id="schedNum">Schedule #: 21053</p>
                                <p id="seats">Available seats: 5/90</p>
                            </div>
                        </td>
                        <td rowspan="3" class="tableCourse opt-class-2 RWS-101_23005_0h" data-toggle="modal" data-courseid="RWS-101_23005_0" data-target="#tableModal">
                            <p id="courseID"><b>RWS-101</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>RHETORIC WRITTEN ARGUMENT</i></i></p><p></p><p id="instructors">J. TOWNER</p>
                                <p id="times">11:00am - 11:50am</p>
                                <p id="schedNum">Schedule #: 23005</p>
                            </div>
                            <div style="display:none" class="RWS-101_23005_0 temp">
                                <p id="title"><i>RHETORIC WRITTEN ARGUMENT</i></p>
                                <p id="instructors">J. TOWNER</p>
                                <p id="times">11:00am - 11:50am</p>
                                <p id="location">LSS-246</p>
                                <p id="schedNum">Schedule #: 23005</p>
                                <p id="seats">Available seats: 8/30</p>
                            </div>
                        </td>
                        <td rowspan="5" class="tableCourse opt-class-6 CS-237_21053_0h" data-toggle="modal" data-courseid="CS-237_21053_0" data-target="#tableModal">
                            <p id="courseID"><b>CS-237</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>MACHINE ORG&amp;ASSEMBLY LANG</i></i></p><p></p><p id="instructors">L. RIGGINS</p>
                                <p id="times">11:00am - 12:15pm</p>
                                <p id="schedNum">Schedule #: 21053</p>
                            </div>
                            <div style="display:none" class="CS-237_21053_0 temp">
                                <p id="title"><i>MACHINE ORG&amp;ASSEMBLY LANG</i></p>
                                <p id="instructors">L. RIGGINS</p>
                                <p id="times">11:00am - 12:15pm</p>
                                <p id="location">HT-183</p>
                                <p id="schedNum">Schedule #: 21053</p>
                                <p id="seats">Available seats: 5/90</p>
                            </div>
                        </td>
                        <td rowspan="3" class="tableCourse opt-class-2 RWS-101_23005_0h" data-toggle="modal" data-courseid="RWS-101_23005_0" data-target="#tableModal">
                            <p id="courseID"><b>RWS-101</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>RHETORIC WRITTEN ARGUMENT</i></i></p><p></p><p id="instructors">J. TOWNER</p>
                                <p id="times">11:00am - 11:50am</p>
                                <p id="schedNum">Schedule #: 23005</p>
                            </div>
                            <div style="display:none" class="RWS-101_23005_0 temp">
                                <p id="title"><i>RHETORIC WRITTEN ARGUMENT</i></p>
                                <p id="instructors">J. TOWNER</p>
                                <p id="times">11:00am - 11:50am</p>
                                <p id="location">LSS-246</p>
                                <p id="schedNum">Schedule #: 23005</p>
                                <p id="seats">Available seats: 8/30</p>
                            </div>
                        </td>
                    </tr>
                    <tr>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">11:30 am</td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">12:00 pm</td>
                        <td rowspan="3" class="tableCourse opt-class-3 MATH-150_22120_1h" data-toggle="modal" data-courseid="MATH-150_22120_1" data-target="#tableModal">
                            <p id="courseID"><b>MATH-150</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>CALCULUS I</i></i></p><p></p><p id="instructors">C. MCGRANE</p>
                                <p id="times">12:00pm - 12:50pm</p>
                                <p id="schedNum">Schedule #: 22120</p>
                            </div>
                            <div style="display:none" class="MATH-150_22120_1 temp">
                                <p id="title"><i>CALCULUS I</i></p>
                                <p id="instructors">C. MCGRANE</p>
                                <p id="times">12:00pm - 12:50pm</p>
                                <p id="location">GMCS-327</p>
                                <p id="schedNum">Schedule #: 22120</p>
                                <p id="seats" class="text-red">Available seats: 0/31</p>
                            </div>
                        </td>
                        <td rowspan="3" class="tableCourse opt-class-3 MATH-150_22120_1h" data-toggle="modal" data-courseid="MATH-150_22120_1" data-target="#tableModal">
                            <p id="courseID"><b>MATH-150</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>CALCULUS I</i></i></p><p></p><p id="instructors">C. MCGRANE</p>
                                <p id="times">12:00pm - 12:50pm</p>
                                <p id="schedNum">Schedule #: 22120</p>
                            </div>
                            <div style="display:none" class="MATH-150_22120_1 temp">
                                <p id="title"><i>CALCULUS I</i></p>
                                <p id="instructors">C. MCGRANE</p>
                                <p id="times">12:00pm - 12:50pm</p>
                                <p id="location">GMCS-327</p>
                                <p id="schedNum">Schedule #: 22120</p>
                                <p id="seats" class="text-red">Available seats: 0/31</p>
                            </div>
                        </td>
                        <td rowspan="3" class="tableCourse opt-class-7 ARP-207_20187_0h" data-toggle="modal" data-courseid="ARP-207_20187_0" data-target="#tableModal">
                            <p id="courseID"><b>ARP-207</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>COMM SERVICE FIELD EXP</i></i></p><p></p><p id="instructors">R. TIMM</p>
                                <p id="times">12:00pm - 12:50pm</p>
                                <p id="schedNum">Schedule #: 20187</p>
                            </div>
                            <div style="display:none" class="ARP-207_20187_0 temp">
                                <p id="title"><i>COMM SERVICE FIELD EXP</i></p>
                                <p id="instructors">R. TIMM</p>
                                <p id="times">12:00pm - 12:50pm</p>
                                <p id="location">SH-123</p>
                                <p id="schedNum">Schedule #: 20187</p>
                                <p id="seats" class="text-red">Available seats: -1/45</p>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">12:30 pm</td>
                        <td rowspan="5" class="tableCourse opt-class-2 CS-570_21077_0h" data-toggle="modal" data-courseid="CS-570_21077_0" data-target="#tableModal">
                            <p id="courseID"><b>CS-570</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>OPERATING SYSTEMS</i></i></p><p></p><p id="instructors">J. CARROLL</p>
                                <p id="times">12:30pm - 1:45pm</p>
                                <p id="schedNum">Schedule #: 21077</p>
                            </div>
                            <div style="display:none" class="CS-570_21077_0 temp">
                                <p id="title"><i>OPERATING SYSTEMS</i></p>
                                <p id="instructors">J. CARROLL</p>
                                <p id="times">12:30pm - 1:45pm</p>
                                <p id="location">EBA-343</p>
                                <p id="schedNum">Schedule #: 21077</p>
                                <p id="seats" class="text-red">Available seats: -22/70</p>
                            </div>
                        </td>
                        <td rowspan="5" class="tableCourse opt-class-2 CS-570_21077_0h" data-toggle="modal" data-courseid="CS-570_21077_0" data-target="#tableModal">
                            <p id="courseID"><b>CS-570</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>OPERATING SYSTEMS</i></i></p><p></p><p id="instructors">J. CARROLL</p>
                                <p id="times">12:30pm - 1:45pm</p>
                                <p id="schedNum">Schedule #: 21077</p>
                            </div>
                            <div style="display:none" class="CS-570_21077_0 temp">
                                <p id="title"><i>OPERATING SYSTEMS</i></p>
                                <p id="instructors">J. CARROLL</p>
                                <p id="times">12:30pm - 1:45pm</p>
                                <p id="location">EBA-343</p>
                                <p id="schedNum">Schedule #: 21077</p>
                                <p id="seats" class="text-red">Available seats: -22/70</p>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">1:00 pm</td>
                        <td rowspan="3" class="tableCourse opt-class-4 COMM-103_20920_0h" data-toggle="modal" data-courseid="COMM-103_20920_0" data-target="#tableModal">
                            <p id="courseID"><b>COMM-103</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>ORAL COMMUNICATION</i></i></p><p></p><p id="instructors">K. PINCHART</p>
                                <p id="times">1:00pm - 1:50pm</p>
                                <p id="schedNum">Schedule #: 20920</p>
                            </div>
                            <div style="display:none" class="COMM-103_20920_0 temp">
                                <p id="title"><i>ORAL COMMUNICATION</i></p>
                                <p id="instructors">K. PINCHART</p>
                                <p id="times">1:00pm - 1:50pm</p>
                                <p id="location">EBA-251</p>
                                <p id="schedNum">Schedule #: 20920</p>
                                <p id="seats" class="text-red">Available seats: -1/23</p>
                            </div>
                        </td>
                        <td rowspan="3" class="tableCourse opt-class-4 COMM-103_20920_0h" data-toggle="modal" data-courseid="COMM-103_20920_0" data-target="#tableModal">
                            <p id="courseID"><b>COMM-103</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>ORAL COMMUNICATION</i></i></p><p></p><p id="instructors">K. PINCHART</p>
                                <p id="times">1:00pm - 1:50pm</p>
                                <p id="schedNum">Schedule #: 20920</p>
                            </div>
                            <div style="display:none" class="COMM-103_20920_0 temp">
                                <p id="title"><i>ORAL COMMUNICATION</i></p>
                                <p id="instructors">K. PINCHART</p>
                                <p id="times">1:00pm - 1:50pm</p>
                                <p id="location">EBA-251</p>
                                <p id="schedNum">Schedule #: 20920</p>
                                <p id="seats" class="text-red">Available seats: -1/23</p>
                            </div>
                        </td>
                        <td rowspan="3" class="tableCourse opt-class-7 CHEM-790_33483_0h" data-toggle="modal" data-courseid="CHEM-790_33483_0" data-target="#tableModal">
                            <p id="courseID"><b>CHEM-790</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>SEMINAR: CHEMISTRY</i></i></p><p></p><p id="instructors">J. LOVE</p>
                                <p id="times">1:00pm - 1:50pm</p>
                                <p id="schedNum">Schedule #: 33483</p>
                            </div>
                            <div style="display:none" class="CHEM-790_33483_0 temp">
                                <p id="title"><i>SEMINAR: CHEMISTRY</i></p>
                                <p id="instructors">J. LOVE</p>
                                <p id="times">1:00pm - 1:50pm</p>
                                <p id="location">GMCS-305</p>
                                <p id="schedNum">Schedule #: 33483</p>
                                <p id="seats">Available seats: 15/20</p>
                            </div>
                        </td>
                    </tr>
                    <tr>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">1:30 pm</td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">2:00 pm</td>
                        <td></td>
                        <td rowspan="5" class="tableCourse opt-class-1 MATH-151_22147_1h" data-toggle="modal" data-courseid="MATH-151_22147_1" data-target="#tableModal">
                            <p id="courseID"><b>MATH-151</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>CALCULUS II</i></i></p><p></p><p id="instructors">C. MANCHESTER</p>
                                <p id="times">2:00pm - 3:15pm</p>
                                <p id="schedNum">Schedule #: 22147</p>
                            </div>
                            <div style="display:none" class="MATH-151_22147_1 temp">
                                <p id="title"><i>CALCULUS II</i></p>
                                <p id="instructors">C. MANCHESTER</p>
                                <p id="times">2:00pm - 3:15pm</p>
                                <p id="location">GMCS-333</p>
                                <p id="schedNum">Schedule #: 22147</p>
                                <p id="seats" class="text-red">Available seats: -5/32</p>
                            </div>
                        </td>
                        <td></td>
                        <td rowspan="5" class="tableCourse opt-class-1 MATH-151_22147_1h" data-toggle="modal" data-courseid="MATH-151_22147_1" data-target="#tableModal">
                            <p id="courseID"><b>MATH-151</b></p>
                            <div>
                                <p id="title"><i></i></p><p id="title"><i><i>CALCULUS II</i></i></p><p></p><p id="instructors">C. MANCHESTER</p>
                                <p id="times">2:00pm - 3:15pm</p>
                                <p id="schedNum">Schedule #: 22147</p>
                            </div>
                            <div style="display:none" class="MATH-151_22147_1 temp">
                                <p id="title"><i>CALCULUS II</i></p>
                                <p id="instructors">C. MANCHESTER</p>
                                <p id="times">2:00pm - 3:15pm</p>
                                <p id="location">GMCS-333</p>
                                <p id="schedNum">Schedule #: 22147</p>
                                <p id="seats" class="text-red">Available seats: -5/32</p>
                            </div>
                        </td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">2:30 pm</td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">3:00 pm</td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">3:30 pm</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">4:00 pm</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">4:30 pm</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">5:00 pm</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">5:30 pm</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">6:00 pm</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">6:30 pm</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">7:00 pm</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">7:30 pm</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">8:00 pm</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    <tr>
                        <td rowspan="2" class="opt-table-label opt-table-time">8:30 pm</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                        <td class="bottomBorder"></td>
                    </tr>
                    </tbody></table></div>
        </div>
    </div>
</div>


<jsp:include page="footer.jsp"/>