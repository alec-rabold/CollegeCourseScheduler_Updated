package io.collegeplanner.my.collegecoursescheduler.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.List;
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

    /** College names */
    public static final String UNIVERSITY_OF_WASHINGTON = "UW";
    public static final String WASHINGTON_STATE_UNIVERSITY = "WSU";
    public static final String SAN_DIEGO_STATE_UNIVERSITY = "SDSU";
    public static final String UNIVERSITY_OF_CALIFORNIA_BERKELEY = "Berkeley";
    public static final String UNIVERSITY_OF_CALIFORNIA_SANTA_BARBARA = "UCSB";


    /** Short college names */
    public static final String UW = UNIVERSITY_OF_WASHINGTON;
    public static final String WSU = WASHINGTON_STATE_UNIVERSITY;
    public static final String SDSU = SAN_DIEGO_STATE_UNIVERSITY;
    public static final String UCSB = UNIVERSITY_OF_CALIFORNIA_SANTA_BARBARA;
    public static final String BERKELEY = UNIVERSITY_OF_CALIFORNIA_BERKELEY;


    public static final String REDIRECT_URL_PREFIX = "redirect:/";

    /** Servlet mapping paths */
    public static final String API_RELATIVE_PATH = "/v1/university";

    // Relative paths
    public static final String UW_RELATIVE_PATH = "/" + UNIVERSITY_OF_WASHINGTON;
    public static final String WSU_RELATIVE_PATH = "/" + WASHINGTON_STATE_UNIVERSITY;
    public static final String SDSU_RELATIVE_PATH = "/" + SAN_DIEGO_STATE_UNIVERSITY;
    public static final String UCSB_RELATIVE_PATH = "/" + UNIVERSITY_OF_CALIFORNIA_SANTA_BARBARA;
    public static final String BERKELEY_RELATIVE_PATH = "/" + UNIVERSITY_OF_CALIFORNIA_BERKELEY;
    public static final String RESULTS_PERMUTATIONS_RELATIVE_PATH = "/results";

    // Root paths
    public static final String UW_COMPLETE_PATH = API_RELATIVE_PATH + UW_RELATIVE_PATH;
    public static final String WSU_COMPLETE_PATH = API_RELATIVE_PATH + WSU_RELATIVE_PATH;
    public static final String SDSU_COMPLETE_PATH = API_RELATIVE_PATH + SDSU_RELATIVE_PATH;
    public static final String UCSB_COMPLETE_PATH = API_RELATIVE_PATH + UCSB_RELATIVE_PATH;
    public static final String BERKELEY_COMPLETE_PATH = API_RELATIVE_PATH + BERKELEY_RELATIVE_PATH;

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
    public static final String SPACE_CHARACTER = " ";
    public static final String EMPTY_STRING = "";
    public static final String POUND_SIGN = "#";
    public static final String PARENTHESES_OPEN = "(";
    public static final String PARENTHESES_CLOSE = ")";

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

    // Result codes
    public static final int RESULT_CODE_OK = 200;
    public static final int RESULT_CODE_NO_CONTENT = 204;
    public static final int RESULT_CODE_PARTIAL_CONTENT = 206;
    public static final int RESULT_CODE_BAD_REQUEST = 400;
    public static final int RESULT_CODE_UNAUTHORIZED = 401;
    public static final int RESULT_CODE_TOO_MANY_REQUESTS = 429;
    public static final int RESULT_CODE_INTERNAL_SERVER_ERROR = 500;
    public static final int RESULT_CODE_SERVICE_UNAVAILABLE = 503;






    // TODO: refactor; legacy
    /** Names of arrays in request params */
    public static final String MONDAYS_ARRAY = "mondayChosen";
    public static final String TUESDAY_ARRAY = "tuesdayChosen";
    public static final String WEDNESDAY_ARRAY = "wednesdayChosen";
    public static final String THURSDAY_ARRAY = "thursdayChosen";
    public static final String FRIDAY_ARRAY = "fridayChosen";

    public static final String HEADER_FILE_NAME = "header.jsp";
    public static final String FOOTER_FILE_NAME = "footer.jsp";

    public static final int NUM_OF_WEEKDAYS = 5;
    public static final String SELECTED_BY_USER = "1";

    public static final String REFERER_HEADER = "referer";

    /** JSP views */
    public static final String INDEX_VIEW = "index";
    public static final String USER_PREFERENCES_FOR_SCHEDULE_VIEW = "customize";

    /** JSTL variable names */
    public static final String SELECTED_COLLEGE_ATTRIBUTE_NAME = "collegeName";
    public static final String REGISTRATION_DATA_ATTRIBUTE_NAME = "registrationData";

    /** (Prepared) SQL Queries */
    public static final String PREPARED_QUERY_SELECT_ALL_FROM_TABLE = "SELECT * FROM <table>";

    /** Table Names */
    // Prefixes
    public static final String PROFESSORS_TABLE_PREFIX = "professors_";
    public static final String COURSE_REGISTRATION_DATA_TABLE_PREFIX = "course_registration_data_";

    // Registration data
    public static final String UW_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + "uw";
    public static final String WSU_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + "wsu";
    public static final String BERKELEY_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + "berkeley";
    public static final String SDSU_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + "sdsu";
    public static final String UCSB_COURSE_REGISTRATION_DATA_TABLE =
            COURSE_REGISTRATION_DATA_TABLE_PREFIX + "ucsb";

    // Professors
    public static final String UW_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + "uw";
    public static final String WSU_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + "wsu";
    public static final String SDSU_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + "sdsu";
    public static final String UCSB_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + "ucsb";
    public static final String BERKELEY_PROFESSORS_TABLE =
            PROFESSORS_TABLE_PREFIX + "berkeley";

    /** Immutable Collections */
    public static final List<String> SUPPORTED_COLLEGES_RELATIVE_PATHS = ImmutableList.of(
            UW_RELATIVE_PATH,
            WSU_RELATIVE_PATH,
            SDSU_RELATIVE_PATH,
            UCSB_RELATIVE_PATH,
            BERKELEY_RELATIVE_PATH
    );

    public static final Map<String, String> COURSE_REGISTRATION_DATA_TABLES = ImmutableMap.<String, String>builder()
            .put(UW, UW_COURSE_REGISTRATION_DATA_TABLE)
            .put(WSU, WSU_COURSE_REGISTRATION_DATA_TABLE)
            .put(SDSU, SDSU_COURSE_REGISTRATION_DATA_TABLE)
            .put(UCSB, UCSB_COURSE_REGISTRATION_DATA_TABLE)
            .put(BERKELEY, BERKELEY_COURSE_REGISTRATION_DATA_TABLE)
            .build();

    public static final Map<String, String> PROFESSORS_TABLES = ImmutableMap.<String, String>builder()
            .put(UW, UW_PROFESSORS_TABLE)
            .put(WSU, WSU_PROFESSORS_TABLE)
            .put(SDSU, SDSU_PROFESSORS_TABLE)
            .put(UCSB, UCSB_PROFESSORS_TABLE)
            .put(BERKELEY, BERKELEY_PROFESSORS_TABLE)
            .build();

    /** Error logging */
   // TODO
}
