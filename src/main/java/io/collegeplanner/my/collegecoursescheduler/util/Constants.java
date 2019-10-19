package io.collegeplanner.my.collegecoursescheduler.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Constants {
    /** Registration search pages */
    public static final String REGISTRATION_SEARCH_PAGE_UW = "http://www.washington.edu/students/timeschd/";
    public static final String REGISTRATION_SEARCH_PAGE_WSU = "http://schedules.wsu.edu";
    public static final String REGISTRATION_SEARCH_PAGE_SDSU = "https://sunspot.sdsu.edu/schedule/search?mode=search";
    public static final String REGISTRATION_SEARCH_PAGE_UCSB = "http://my.sa.ucsb.edu/Public/curriculum/coursesearch.aspx";
    public static final String REGISTRATION_SEARCH_PAGE_BERKELEY = "https://classes.berkeley.edu/json-all-sections/";
    // Ellucian
    public static final String ELLUCIAN_GENERIC_REGISTRATION_SEARCH_PAGE_PATH = "bwckschd.p_get_crse_unsec";
    public static final String ELLUCIAN_GENERIC_REGISTRATION_TERM_DATA_PAGE_PATH = "bwckschd.p_get_crse_unsec";

    /** College names */
    public static final String UNIVERSITY_OF_WASHINGTON = "UW";
    public static final String WASHINGTON_STATE_UNIVERSITY = "WSU";
    public static final String SAN_DIEGO_STATE_UNIVERSITY = "SDSU";
    public static final String UNIVERSITY_OF_CALIFORNIA_BERKELEY = "Berkeley";
    public static final String UNIVERSITY_OF_CALIFORNIA_SANTA_BARBARA = "UCSB";
    // (Ellucian)
    public static final String GEORGIA_STATE_UNIVERSITY = "GSU";
//    public static final String OTTERBEIN_UNIVERSITY = "Otterbein";
    public static final String WEBER_STATE_UNIVERSITY = "Weber";
    public static final String DREXEL_UNIVERSITY = "Drexel";
    public static final String PURDUE_UNIVERSITY = "Purdue";
    public static final String PURDUE_UNIVERSITY_NORTHWEST = "PurdueNW";
    public static final String GEORGE_MASON_UNIVERSITY = "GMU";
    public static final String UNIVERSITY_OF_TENNESSEE_KNOXVILLE = "UTK";
    public static final String HARPER_COLLEGE = "Harper";
    public static final String BROWN_UNIVERSITY = "Brown";
    public static final String GEORGIA_TECH = "GeorgiaTech";

    public static final String REDIRECT_URL_PREFIX = "redirect:/";

    /** Servlet mapping paths */
    public static final String VISUAL_SCHEDULE_BUILDER_RELATIVE_PATH = "/university";
    public static final String API_RELATIVE_PATH = "/v1/university";

    // Relative paths
    public static final String UW_RELATIVE_PATH = "/" + UNIVERSITY_OF_WASHINGTON;
    public static final String WSU_RELATIVE_PATH = "/" + WASHINGTON_STATE_UNIVERSITY;
    public static final String SDSU_RELATIVE_PATH = "/" + SAN_DIEGO_STATE_UNIVERSITY;
    public static final String UCSB_RELATIVE_PATH = "/" + UNIVERSITY_OF_CALIFORNIA_SANTA_BARBARA;
    public static final String BERKELEY_RELATIVE_PATH = "/" + UNIVERSITY_OF_CALIFORNIA_BERKELEY;
    public static final String PURDUE_RELATIVE_PATH = "/" + PURDUE_UNIVERSITY;
    public static final String BROWN_RELATIVE_PATH = "/" + BROWN_UNIVERSITY;
    public static final String RESULTS_PERMUTATIONS_RELATIVE_PATH = "/results";

    // Root paths (VSB)
    public static final String VSB_COMPLETE_PATH_UW = VISUAL_SCHEDULE_BUILDER_RELATIVE_PATH + UW_RELATIVE_PATH;
    public static final String VSB_COMPLETE_PATH_WSU = VISUAL_SCHEDULE_BUILDER_RELATIVE_PATH + WSU_RELATIVE_PATH;
    public static final String VSB_COMPLETE_PATH_SDSU = VISUAL_SCHEDULE_BUILDER_RELATIVE_PATH + SDSU_RELATIVE_PATH;
    public static final String VSB_COMPLETE_PATH_UCSB = VISUAL_SCHEDULE_BUILDER_RELATIVE_PATH + UCSB_RELATIVE_PATH;
    public static final String VSB_COMPLETE_PATH_BERKELEY = VISUAL_SCHEDULE_BUILDER_RELATIVE_PATH + BERKELEY_RELATIVE_PATH;
    public static final String VSB_COMPLETE_PATH_PURDUE = VISUAL_SCHEDULE_BUILDER_RELATIVE_PATH + PURDUE_RELATIVE_PATH;
    public static final String VSB_COMPLETE_PATH_BROWN = VISUAL_SCHEDULE_BUILDER_RELATIVE_PATH + BROWN_RELATIVE_PATH;

    // Root paths (API)
    public static final String API_COMPLETE_PATH_UW = API_RELATIVE_PATH + UW_RELATIVE_PATH;
    public static final String API_COMPLETE_PATH_WSU = API_RELATIVE_PATH + WSU_RELATIVE_PATH;
    public static final String API_COMPLETE_PATH_SDSU = API_RELATIVE_PATH + SDSU_RELATIVE_PATH;
    public static final String API_COMPLETE_PATH_UCSB = API_RELATIVE_PATH + UCSB_RELATIVE_PATH;
    public static final String API_COMPLETE_PATH_BERKELEY = API_RELATIVE_PATH + BERKELEY_RELATIVE_PATH;
    public static final String API_COMPLETE_PATH_PURDUE = API_RELATIVE_PATH + PURDUE_RELATIVE_PATH;
    public static final String API_COMPLETE_PATH_BROWN = API_RELATIVE_PATH + BROWN_RELATIVE_PATH;

    /** Kinesis */
    public static final String STREAM_NAME = "CollegePlannerUsageStream";

    /** Servlet config */
    public static final String JSP_VIEW_RESOLVER_PREFIX = "/WEB-INF/classes/templates/";
    public static final String JSP_VIEW_RESOLVER_SUFFIX = ".jsp";
    public static final String PATH_TO_CSS_FOLDER = "/css";
    public static final String PATH_TO_FONTS_FOLDER = "/fonts";
    public static final String PATH_TO_IMG_FOLDER = "/img";
    public static final String PATH_TO_JS_FOLDER = "/js";
    public static final String PATH_TO_FAVICON_ICO = "/favicon.ico";
    public static final String PATH_TO_ANALYTICS_SERVLET = "/AnalyticsServlet";
    public static final Set<String> RESOURCES_TOP_LEVEL_DIRECTORIES = ImmutableSet.<String>builder()
            .add(PATH_TO_CSS_FOLDER)
            .add(PATH_TO_FONTS_FOLDER)
            .add(PATH_TO_IMG_FOLDER)
            .add(PATH_TO_JS_FOLDER)
            .build();
    public static final Set<String> EXCLUDED_LOGGING_FILTER_PATHS = ImmutableSet.<String>builder()
            .addAll(RESOURCES_TOP_LEVEL_DIRECTORIES)
            .add(PATH_TO_ANALYTICS_SERVLET)
            .add(PATH_TO_FAVICON_ICO)
            .build();



    /** Parsing & Analyzing */
    // Generic Utils
    public static final int ONE_UNIT    = 1;
    public static final int TWO_UNITS   = 2;
    public static final int THREE_UNITS = 3;
    public static final int FOUR_UNITS  = 4;
    public static final int FIVE_UNITS  = 5;
    public static final int SIX_UNITS   = 6;
    public static final int SEVEN_UNITS = 7;
    public static final int EIGHT_UNITS = 8;
    public static final int NINE_UNITS  = 9;
    public static final int TEN_UNITS   = 10;
    public static final String SECONDS_ABBR = "s";
    public static final int SCHEDULE_NUM_LENGTH = 5;
    public static final int DEFAULT_NUM_SCHEDULES = 10;
    public static final String SPACE_CHARACTER = " ";
    public static final String EMPTY_STRING = "";
    public static final String POUND_SIGN = "#";
    public static final String PARENTHESES_OPEN = "(";
    public static final String PARENTHESES_CLOSE = ")";
    public static final String TO_BE_ARRANGED = "TBA";

    // Seasons
    public static final String SEASONS_FALL = "Fall";
    public static final String SEASONS_WINTER = "Winter";
    public static final String SEASONS_SPRING = "Spring";
    public static final String SEASONS_SUMMER = "Summer";

    // Permutation utils
    public static final long MAX_TIMEOUT_IN_MS = TimeUnit.SECONDS.toMillis(8);
    public static final long[] COLLISION_TIMEBLOCK = {-1L, -1L, -1L, -1L, -1L};

    // University of Washington
    public static final String UW_PAGELINK_MARKER_START = "<a href=\"";
    public static final String UW_PAGELINK_MARKER_END = "\">";
    public static final String UW_NON_PAGELINK_MARKER = "(CCS)";
    public static final String UW_BUNDLE_ID_MARKER = "<table bgcolor";
    public static final String UW_COURSE_ID_MARKER = "<A NAME=";
    public static final String UW_SECTION_ID_MARKER = "<A HREF";
    public static final String UW_SCHEDULE_NUM_PARSER_START = "&SLN=";
    public static final String UW_SCHEDULE_NUM_PARSER_END = ">";
    public static final String UW_UNITS_PARSER_START = "</A>";
    public static final String UW_BUILDING_ABBR_PARSER_START_1 = ">";
    public static final String UW_BUILDING_ABBR_PARSER_START_2 = "map.cgi";
    public static final String UW_BUILDING_ABBR_PARSER_END = "</A>";
    public static final String UW_COURSE_LETTERS_PARSER_START = "</A>";
    public static final String UW_OPEN_CLASS_MARKER = "Open";
    public static final String UW_CLOSED_CLASS_MARKER = "Closed";
    public static final Character UW_UNAVAILABLE_CLASS_MARKER = '>';

    // Washington State University
    public static final String WSU_URL_PREFIX = "/List/Pullman/";
    // TODO: Add more prefixes to support other areas like WSU tri-cities
    public static final String WSU_DEPARTMENT_PATHS_MARKER_START = "<a href=\"";
    public static final String WSU_DEPARTMENT_PATHS_MARKER_END = "\">";
    public static final String WSU_COURSE_TITLE_MARKER = "course_title";
    public static final String WSU_COURSE_TITLE_MARKER_START = ">";
    public static final String WSU_COURSE_TITLE_MARKER_END = "</td>";
    public static final String WSU_COURSE_ID_MARKER = "sched_sec";
    public static final String WSU_COURSE_ID_MARKER_END = "\">";
    public static final String WSU_SCHEDULE_NUMBER_MARKER = "sched_sln";

    // UC Santa Barbara
    public static final String UCSB_ID_MARKER = "CourseTitle";
    public static final String UCSB_SUBJECT_AREA = "ctl00_pageContent_courseList";
    public static final String UCSB_QUARTER_LIST = "ctl00$pageContent$quarterList";
    public static final String UCSB_COURSE_LEVELS = "ctl00$pageContent$dropDownCourseLevels";
    public static final String UCSB_SUBMIT_BUTTON = "ctl00_pageContent_searchButton";
    public static final String UCSB_LABEL_TITLE_MARKER = "labelTitle";

    // Ellucian
    public static final String ELLUCIAN_DYNAMIC_SCHEDULE_RELATIVE_PATH = "/bwckschd.p_disp_dyn_sched";
    public static final String ELLUCIAN_REGISTRATION_COURSES_RELATIVE_PATH = "/bwckschd.p_get_crse_unsec";
    public static final String ELLUCIAN_REGISTRATION_TERM_DATA_RELATIVE_PATH = "/bwckgens.p_proc_term_date";

    public static final String ELLUCIAN_SS_COURSE_DATA_COURSE_MARKER_I = "ddtitle"; // most use this
    public static final String ELLUCIAN_SS_COURSE_DATA_COURSE_MARKER_II = "ddlabel"; // at least one uses this (Purdue)
    public static final String ELLUCIAN_SS_COURSE_DATA_COURSE_TITLE_DATA_MARKER_START = "crn_in="; // at least one uses this (Purdue)

    public static final int ELLUCIAN_COURSE_TITLE_MARKER_LENGTH = SCHEDULE_NUM_LENGTH + ELLUCIAN_SS_COURSE_DATA_COURSE_TITLE_DATA_MARKER_START.length();
    public static final String ELLUCIAN_SS_COURSE_DATA_COURSE_ID_DATA_MARKER_END = " - ";
    public static final String ELLUCIAN_SS_COURSE_DATA_COURSE_TITLE_DATA_MARKER_END = " - ";
    public static final String ELLUCIAN_SCHEDULE_NUMBER_MARKER_END = " - ";
    public static final String ELLUCIAN_COURSE_ID_MARKER_END = " - ";
    public static final String ELLUCIAN_SCHEDULED_MEETING_TIMES_MARKER = "Scheduled Meeting Times";
    public static final String ELLUCIAN_COURSE_SECTION_MARKER = "<tr>";
//    public static final String ELLUCIAN_COURSE_SECTION_MARKER_END = "</tbody>";
    public static final String ELLUCIAN_COURSE_SECTION_MARKER_END = "</table>";
    public static final String ELLUCIAN_NUM_CREDITS_MARKER = "Credits";


    public static final int ELLUCIAN_SECTIONS_TABLE_ROW_HEADER = 1; // the first row is the table header (to be skipped while parsing)
    public static final int ELLUCIAN_SECTIONS_TABLE_SECOND_COURSE_IN_BUNDLE = 3; // first row is header (1), second row is first course (2), third row is second course (3)
    public static final String ELLUCIAN_SECTIONS_TABLE_COL_MARKER_START = "<td CLASS=\"dddefault\">";
    public static final String ELLUCIAN_SECTIONS_TABLE_COL_MARKER_END = "</td>";

    public static final String ELLUCIAN_SS_DATA_TERM_MARKER = "p_term";
    public static final String ELLUCIAN_SS_TERM_DATA_FALSE_MARKER = "dummy";
    public static final String ELLUCIAN_SS_TERM_DATA_SUBJECT_MARKER_START = "sel_subj";
    public static final String ELLUCIAN_SS_TERM_DATA_SUBJECT_ABBR_DATA_MARKER_START = "<OPTION VALUE=\"";
    public static final String ELLUCIAN_SS_TERM_DATA_SUBJECT_ABBR_DATA_MARKER_END = "\">";
    public static final String ELLUCIAN_SS_TERM_DATA_SUBJECT_FULL_DATA_MARKER_START = "\">";
    public static final String ELLUCIAN_SS_TERM_DATA_SUBJECT_FULL_DATA_MARKER_END = "</OPTION>";
    public static final String ELLUCIAN_SS_TERM_DATA_SUBJECT_MARKER_END = "</select>";
    public static final String ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_MARKER_START = "sel_instr";
    public static final String ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_MARKER_END = "</select>";
    public static final String ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_MARKER_START = "\">";
    public static final String ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_MARKER_END = "</OPTION>";
    public static final String ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_VALUE_MARKER_START = "VALUE=\"";
    public static final String ELLUCIAN_SS_TERM_DATA_INSTRUCTORS_DATA_VALUE_MARKER_END = "\">";

    public static final String ELLUCIAN_SS_DATA_TERM_MARKER_START = "VALUE=\"";
    public static final String ELLUCIAN_SS_DATA_TERM_MARKER_END = "\">";
    public static final int ELLUCIAN_SS_DATA_DEFAULT_NUM_TERMS = 4;
    public static final String ELLUCIAN_SS_TERM_DATA_FORM_DATA = "p_calling_proc=bwckschd.p_disp_dyn_sched&p_term=";
//    public static final String ELLUCIAN_SS_TERM_DATA_FORM_DATA = "p_calling_proc=bwckschd.p_disp_dyn_sched";
    public static final String ELLUCIAN_SS_COURSE_DATA_FORM_DATA = "sel_subj=dummy&sel_day=dummy&sel_schd=dummy&sel_insm=dummy&sel_camp=dummy&sel_levl=dummy&sel_sess=dummy&sel_instr=dummy&sel_ptrm=dummy&sel_attr=dummy&sel_crse=&sel_title=&sel_schd=%25&sel_from_cred=&sel_to_cred=&sel_levl=%25&sel_instr=%25&sel_attr=%25&begin_hh=0&begin_mi=0&begin_ap=a&end_hh=0&end_mi=0&end_ap=a&term_in=";
    public static final String ELLUCIAN_SS_TERM_DATA_FORM_TERM = "&p_term=";
    public static final String ELLUCIAN_SS_COURSE_DATA_FORM_TERM = "&term_in=";
    public static final String ELLUCIAN_SS_DATA_FORM_SUBJECT = "&sel_subj=";

    // Ellucian Universities Self-Service Data Pages
    public static final String ELLUCIAN_SS_DATA_GEORGIA_STATE_UNIVERSITY = "https://www.gosolar.gsu.edu/bprod";
//    public static final String ELLUCIAN_SS_DATA_OTTERBEIN_UNIVERSITY = "https://ssb.otterbein.edu/prod";
    public static final String ELLUCIAN_SS_DATA_WEBER_STATE_UNIVERSITY = "https://selfservice.weber.edu/pls/proddad";
    public static final String ELLUCIAN_SS_DATA_DREXEL_UNIVERSITY = "https://banner.drexel.edu/pls/duprod";
    public static final String ELLUCIAN_SS_DATA_PURDUE_UNIVERSITY = "https://selfservice.mypurdue.purdue.edu/prod";
    public static final String ELLUCIAN_SS_DATA_PURDUE_UNIVERSITY_NORTHWEST = "https://ssb-prod.pnw.edu/dbServer_prod";
    public static final String ELLUCIAN_SS_DATA_GEORGE_MASON_UNIVERSITY = "https://patriotweb.gmu.edu/pls/prod";
    public static final String ELLUCIAN_SS_DATA_UNIVERSITY_OF_TENNESSEE_KNOWXVILLE = "https://bannerssb.utk.edu/kbanpr";
    public static final String ELLUCIAN_SS_DATA_HARPER_COLLEGE = "https://student-self-service.harpercollege.edu/prod";
    public static final String ELLUCIAN_SS_DATA_BROWN_UNIVERSITY = "https://selfservice.brown.edu/ss";
    public static final String ELLUCIAN_SS_DATA_GEORGIA_TECH = "https://oscar.gatech.edu/pls/bprod";

    public static final Map<String, String> ELLUCIAN_UNIVERSITIES_SS_DATA_PAGES = ImmutableMap.<String, String>builder()
            .put(GEORGIA_STATE_UNIVERSITY, ELLUCIAN_SS_DATA_GEORGIA_STATE_UNIVERSITY)
//            .put(OTTERBEIN_UNIVERSITY, ELLUCIAN_SS_DATA_OTTERBEIN_UNIVERSITY)
            .put(WEBER_STATE_UNIVERSITY, ELLUCIAN_SS_DATA_WEBER_STATE_UNIVERSITY)
            .put(DREXEL_UNIVERSITY, ELLUCIAN_SS_DATA_DREXEL_UNIVERSITY)
            .put(PURDUE_UNIVERSITY, ELLUCIAN_SS_DATA_PURDUE_UNIVERSITY)
            .put(PURDUE_UNIVERSITY_NORTHWEST, ELLUCIAN_SS_DATA_PURDUE_UNIVERSITY_NORTHWEST)
            .put(GEORGE_MASON_UNIVERSITY, ELLUCIAN_SS_DATA_GEORGE_MASON_UNIVERSITY)
            .put(UNIVERSITY_OF_TENNESSEE_KNOXVILLE, ELLUCIAN_SS_DATA_UNIVERSITY_OF_TENNESSEE_KNOWXVILLE)
            .put(HARPER_COLLEGE, ELLUCIAN_SS_DATA_HARPER_COLLEGE)
            .put(BROWN_UNIVERSITY, ELLUCIAN_SS_DATA_BROWN_UNIVERSITY)
            .put(GEORGIA_TECH, ELLUCIAN_SS_DATA_GEORGIA_TECH)
            .build();

    // TermID titles to ignore while parsing
    public static final Set<String> ignoredTerms = ImmutableSet.<String>builder()
            .add("SPS")
            .add("Institute")
            .add("Inst")
            .add("Special")
            .add("Spec")
            .build();

    // Result codes
    // TODO: (remove) - these are already defined as constants in HTTPUrlConnection
    public static final int RESULT_CODE_OK = 200;
    public static final int RESULT_CODE_NO_CONTENT = 204;
    public static final int RESULT_CODE_PARTIAL_CONTENT = 206;
    public static final int RESULT_CODE_BAD_REQUEST = 400;
    public static final int RESULT_CODE_UNAUTHORIZED = 401;
    public static final int RESULT_CODE_TOO_MANY_REQUESTS = 429;
    public static final int RESULT_CODE_INTERNAL_SERVER_ERROR = 500;
    public static final int RESULT_CODE_SERVICE_UNAVAILABLE = 503;

    public static final String HEADER_FILE_NAME = "header.jsp";
    public static final String FOOTER_FILE_NAME = "footer.jsp";

    public static final int NUM_OF_WEEKDAYS = 5;
    public static final String[] WEEKDAY_ABBREVS = {"M","T","W","R","F"};

    public static final String REFERER_HEADER = "referer";

    /** JSP views */
    public static final String INDEX_VIEW = "index";
    public static final String ERROR_VIEW = "error";
    public static final String USER_PREFERENCES_FOR_SCHEDULE_VIEW = "customize";

    /** JSTL variable names */
    public static final String SELECTED_COLLEGE_ATTRIBUTE_NAME = "collegeName";
    public static final String REGISTRATION_DATA_ATTRIBUTE_NAME = "registrationData";
    public static final String ERROR_CODE_ATTRIBUTE = "errorCode";
    public static final String ERROR_CODE_MESSAGE = "errorMessage";

    public static final String ERROR_MESSAGE_BASE = "Sorry, something went wrong.";
    public static final String ERROR_MESSAGE_GENERIC = "We will be looking into this issue shortly.";
    public static final String ERROR_MESSAGE_404 = "We weren't able to find the page you requested.";
    public static final String ERROR_MESSAGE_500 = "We weren't able to complete your request. Please make sure you selected at least one course.";

    /** (Prepared) SQL Queries */
    public static final String PREPARED_QUERY_SELECT_ALL_FROM_TABLE = "SELECT * FROM <table>";

    public static final String PREPARED_QUERY_CREATE_PROFESSORS_TABLE = "CREATE TABLE IF NOT EXISTS <table> (p_name VARCHAR(255) PRIMARY KEY, p_value VARCHAR(255))";
    public static final String PREPARED_QUERY_CREATE_SUBJECTS_TABLE = "CREATE TABLE IF NOT EXISTS <table> (s_abbr VARCHAR(255) PRIMARY KEY, s_full VARCHAR(255))";
    public static final String PREPARED_QUERY_CREATE_COURSES_TABLE = "CREATE TABLE IF NOT EXISTS <table> (c_name VARCHAR(255) PRIMARY KEY, c_title VARCHAR(255), c_id VARCHAR(255))";

    public static final String PREPARED_QUERY_UPDATE_PROFESSORS_TABLE = "INSERT INTO <table>(p_name, p_value) VALUES (:name, :value) ON DUPLICATE KEY UPDATE p_value = VALUES(p_value)";
    public static final String PREPARED_QUERY_UPDATE_SUBJECTS_TABLE = "INSERT INTO <table>(s_abbr, s_full) VALUES (:subjAbbr, :subjFull) ON DUPLICATE KEY UPDATE s_full = VALUES(s_full)";
    public static final String PREPARED_QUERY_UPDATE_COURSES_TABLE = "INSERT INTO <table>(c_name, c_title, c_id) VALUES (:courseName, :title, :courseId) ON DUPLICATE KEY UPDATE c_title = VALUES(c_title)";
    public static final String PREPARED_QUERY_UPDATE_FEEDBACK_TABLE = "INSERT INTO <table>(user_input) VALUES (:userInput)";

    public static final String PREPARED_QUERY_REMOVE_INVALID_ROWS = "DELETE FROM <table> WHERE c_id REGEXP '^[0-9]+$'";
    public static final String PREPARED_QUERY_REPLACE_HYPHENS_WITH_SPACES = "UPDATE <table> SET c_id = REPLACE(c_id, '-', ' ')";


    /** Table Names */
    // Prefixes
    public static final String PROFESSORS_TABLE_PREFIX = "professors_";
    public static final String SUBJECTS_TABLE_PREFIX = "subjects_";
    public static final String COURSE_REGISTRATION_DATA_TABLE_PREFIX = "course_registration_data_";
    public static final String FEEDBACK_TABLE_PREFIX = "feedback_";

    // Registration data
    public static final String UW_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + UNIVERSITY_OF_WASHINGTON;
    public static final String WSU_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + WASHINGTON_STATE_UNIVERSITY;
    public static final String BERKELEY_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + UNIVERSITY_OF_CALIFORNIA_BERKELEY;
    public static final String SDSU_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + SAN_DIEGO_STATE_UNIVERSITY;
    public static final String UCSB_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + UNIVERSITY_OF_CALIFORNIA_SANTA_BARBARA;
    public static final String PURDUE_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + PURDUE_UNIVERSITY;
    public static final String BROWN_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + BROWN_UNIVERSITY;
    public static final String GSU_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + GEORGIA_STATE_UNIVERSITY;
    public static final String GMU_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + GEORGE_MASON_UNIVERSITY;
    public static final String GEORGIA_TECH_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + GEORGIA_TECH;
    public static final String UTK_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + UNIVERSITY_OF_TENNESSEE_KNOXVILLE;
    public static final String DREXEL_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + DREXEL_UNIVERSITY;
    public static final String WEBER_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + WEBER_STATE_UNIVERSITY;
    public static final String HARPER_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + HARPER_COLLEGE;

    // Professors
    public static final String UW_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + UNIVERSITY_OF_WASHINGTON;
    public static final String WSU_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + WASHINGTON_STATE_UNIVERSITY;
    public static final String SDSU_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + SAN_DIEGO_STATE_UNIVERSITY;
    public static final String UCSB_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + UNIVERSITY_OF_CALIFORNIA_SANTA_BARBARA;
    public static final String BERKELEY_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + UNIVERSITY_OF_CALIFORNIA_BERKELEY;
    public static final String PURDUE_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + PURDUE_UNIVERSITY;
    public static final String BROWN_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + BROWN_UNIVERSITY;
    public static final String GSU_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + GEORGIA_STATE_UNIVERSITY;
    public static final String GMU_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + GEORGE_MASON_UNIVERSITY;
    public static final String GEORGIA_TECH_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + GEORGIA_TECH;
    public static final String UTK_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + UNIVERSITY_OF_TENNESSEE_KNOXVILLE;
    public static final String DREXEL_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + DREXEL_UNIVERSITY;
    public static final String WEBER_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + WEBER_STATE_UNIVERSITY;
    public static final String HARPER_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + HARPER_COLLEGE;

    // Feedback
    public static final String FEEDBACK_BUGS_TABLE = FEEDBACK_TABLE_PREFIX + "bugs";
    public static final String FEEDBACK_SUGGESTIONS_TABLE = FEEDBACK_TABLE_PREFIX + "suggestions";

    /** Immutable Collections */
    public static final Set<String> SUPPORTED_ELLUCIAN_COLLEGES = ImmutableSet.of(
//            GEORGIA_STATE_UNIVERSITY,
//            OTTERBEIN_UNIVERSITY,
            WEBER_STATE_UNIVERSITY,
            DREXEL_UNIVERSITY,
//            PURDUE_UNIVERSITY,
//            PURDUE_UNIVERSITY_NORTHWEST,
            GEORGE_MASON_UNIVERSITY,
            UNIVERSITY_OF_TENNESSEE_KNOXVILLE,
            HARPER_COLLEGE,
            BROWN_UNIVERSITY,
            GEORGIA_TECH
    );

    public static final Map<String, String> COURSE_REGISTRATION_DATA_TABLES = ImmutableMap.<String, String>builder()
            .put(UNIVERSITY_OF_WASHINGTON, UW_COURSE_REGISTRATION_DATA_TABLE)
            .put(WASHINGTON_STATE_UNIVERSITY, WSU_COURSE_REGISTRATION_DATA_TABLE)
            .put(SAN_DIEGO_STATE_UNIVERSITY, SDSU_COURSE_REGISTRATION_DATA_TABLE)
            .put(UNIVERSITY_OF_CALIFORNIA_SANTA_BARBARA, UCSB_COURSE_REGISTRATION_DATA_TABLE)
            .put(UNIVERSITY_OF_CALIFORNIA_BERKELEY, BERKELEY_COURSE_REGISTRATION_DATA_TABLE)
//            .put(PURDUE_UNIVERSITY, PURDUE_COURSE_REGISTRATION_DATA_TABLE)
            .put(BROWN_UNIVERSITY, BROWN_COURSE_REGISTRATION_DATA_TABLE)
//            .put(GEORGIA_STATE_UNIVERSITY, GSU_COURSE_REGISTRATION_DATA_TABLE)
            .put(GEORGE_MASON_UNIVERSITY, GMU_COURSE_REGISTRATION_DATA_TABLE)
            .put(GEORGIA_TECH, GEORGIA_TECH_COURSE_REGISTRATION_DATA_TABLE)
            .put(UNIVERSITY_OF_TENNESSEE_KNOXVILLE, UTK_COURSE_REGISTRATION_DATA_TABLE)
            .put(DREXEL_UNIVERSITY, DREXEL_COURSE_REGISTRATION_DATA_TABLE)
            .put(WEBER_STATE_UNIVERSITY, WEBER_COURSE_REGISTRATION_DATA_TABLE)
            .put(HARPER_COLLEGE, HARPER_COURSE_REGISTRATION_DATA_TABLE)
            .build();

    public static final Map<String, String> PROFESSORS_TABLES = ImmutableMap.<String, String>builder()
            .put(UNIVERSITY_OF_WASHINGTON, UW_PROFESSORS_TABLE)
            .put(WASHINGTON_STATE_UNIVERSITY, WSU_PROFESSORS_TABLE)
            .put(SAN_DIEGO_STATE_UNIVERSITY, SDSU_PROFESSORS_TABLE)
            .put(UNIVERSITY_OF_CALIFORNIA_SANTA_BARBARA, UCSB_PROFESSORS_TABLE)
            .put(UNIVERSITY_OF_CALIFORNIA_BERKELEY, BERKELEY_PROFESSORS_TABLE)
//            .put(PURDUE_UNIVERSITY, PURDUE_PROFESSORS_TABLE)
            .put(BROWN_UNIVERSITY, BROWN_PROFESSORS_TABLE)
            .put(GEORGIA_STATE_UNIVERSITY, GSU_PROFESSORS_TABLE)
            .put(GEORGE_MASON_UNIVERSITY, GMU_PROFESSORS_TABLE)
            .put(GEORGIA_TECH, GEORGIA_TECH_PROFESSORS_TABLE)
            .put(UNIVERSITY_OF_TENNESSEE_KNOXVILLE, UTK_PROFESSORS_TABLE)
            .put(DREXEL_UNIVERSITY, DREXEL_PROFESSORS_TABLE)
            .put(WEBER_STATE_UNIVERSITY, WEBER_PROFESSORS_TABLE)
            .put(HARPER_COLLEGE, HARPER_PROFESSORS_TABLE)
            .build();

    /** Error logging */
   // TODO
}
