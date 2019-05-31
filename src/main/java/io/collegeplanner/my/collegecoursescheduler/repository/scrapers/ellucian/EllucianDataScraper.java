package io.collegeplanner.my.collegecoursescheduler.repository.scrapers.ellucian;

import io.collegeplanner.my.collegecoursescheduler.util.ScraperUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.*;

@Log4j2
public abstract class EllucianDataScraper {

    public abstract void scrapeAndPersistDataForCollege(final String college, final Set<String> termIds) throws IOException;

    public static Set<String> getMostRecentTermIds(final int numTerms, final String baseDataPage) throws IOException {
        final Set<String> res = new HashSet<>();
        final String dataPage = baseDataPage + ELLUCIAN_DYNAMIC_SCHEDULE_RELATIVE_PATH;
        final BufferedReader in = getReaderForPageWithParams(dataPage, null,
                baseDataPage + ELLUCIAN_DYNAMIC_SCHEDULE_RELATIVE_PATH);
        String inputLine = in.readLine();
        // TODO: clean up
        if(inputLine == null) {
            log.error("Unreachable datapage");
            return null;
        }
        while(!inputLine.contains(ELLUCIAN_SS_DATA_TERM_MARKER)) {
            inputLine = in.readLine();
        }
        in.readLine(); // skip 2 lines TODO: kinda hacky fix
        while(res.size() < numTerms) {
            final String parsedTerm = ScraperUtils.parseData(ELLUCIAN_SS_DATA_TERM_MARKER_START,
                    ELLUCIAN_SS_DATA_TERM_MARKER_START.length(), ELLUCIAN_SS_DATA_TERM_MARKER_END, in.readLine())
                    .getData();
            if(parsedTerm.isEmpty()) continue;
            res.add(parsedTerm);
        }
        return res;
    }

    public static BufferedReader getReaderForPageWithParams(final String dataPage, final String unencodedParams,
                                                               final String referer) {
        try {
            final URL url = new URL(dataPage);
            final HttpURLConnection http = (HttpURLConnection) url.openConnection();
//            final HttpsURLConnection http = (HttpsURLConnection) url.openConnection();

            http.setDoOutput(false); // might be necessary for certain security setups (403 error)
            http.setUseCaches(false);
            http.setRequestMethod("GET");
            http.setInstanceFollowRedirects(false);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if(referer != null) {
                http.setRequestProperty("Referer", referer);
            }

            if(StringUtils.isNotEmpty(unencodedParams)) {
                http.setDoOutput(true);
                http.setRequestMethod("POST");
                final byte[] encodedFormParams = unencodedParams.getBytes(StandardCharsets.UTF_8);
                http.setFixedLengthStreamingMode(encodedFormParams.length);
                http.getOutputStream().write(encodedFormParams);
            }

            // http.disconnect();

            return new BufferedReader(new InputStreamReader(http.getInputStream()));

        } catch(final Exception e) {
            log.error("Error getting html reader for page {} \n\t Params: {} \n\t Referer: {}", dataPage, unencodedParams, referer, e);
        }
        return null;
    }

//    public static String formatTermParameters(final Set<String> termIds, final boolean isCoursePage) {
//        final StringBuilder paramBuilder = new StringBuilder();
//        for(final String term : termIds) {
//            // TODO: simplify
//            if(isCoursePage) {
//                paramBuilder.append(ELLUCIAN_SS_COURSE_DATA_FORM_TERM + term);
//            }
//            else {
//                paramBuilder.append(ELLUCIAN_SS_TERM_DATA_FORM_TERM + term);
//            }
//        }
//        return paramBuilder.toString();
//    }

    public static String formatSubjectParameters(final Set<String> subjects) {
        final StringBuilder paramBuilder = new StringBuilder();
        for(final String subj : subjects) {
            paramBuilder.append(ELLUCIAN_SS_DATA_FORM_SUBJECT + subj);
        }
        return paramBuilder.toString();
    }
}
