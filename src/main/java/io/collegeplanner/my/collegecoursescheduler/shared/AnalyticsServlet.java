package io.collegeplanner.my.collegecoursescheduler.shared;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.AnalyticsReportingScopes;
import com.google.api.services.analyticsreporting.v4.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@WebServlet(urlPatterns = {"/AnalyticsServlet"})
public class AnalyticsServlet extends HttpServlet {
    private static final String APPLICATION_NAME = "scheduleoptimizer-192803";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String KEY_FILE_LOCATION = "/WEB-INF/classes/etc/GA-key.json";
    private static final String VIEW_ID = "168164451";

    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            final PrintWriter out = response.getWriter();
            final AnalyticsReporting service = initializeAnalyticsReporting();
            final GetReportsResponse res = getReport(service);

            final int[] data = getResponse(res);
            out.write(data[0] + "," + data[1] + "," + data[2]);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes an Analytics Reporting API V4 serviceTemp object.
     *
     * @return An authorized Analytics Reporting API V4 serviceTemp object.
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private AnalyticsReporting initializeAnalyticsReporting() throws GeneralSecurityException, IOException {

        final HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        final GoogleCredential credential = GoogleCredential
                .fromStream(getServletContext().getResourceAsStream(KEY_FILE_LOCATION))
                .createScoped(AnalyticsReportingScopes.all());

        // Construct the Analytics Reporting serviceTemp object.
        return new AnalyticsReporting.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }

    /**
     * Queries the Analytics Reporting API V4.
     *
     * @param service An authorized Analytics Reporting API V4 serviceTemp object.
     * @return GetReportResponse The Analytics Reporting API V4 response.
     * @throws IOException
     */
    private static GetReportsResponse getReport(final AnalyticsReporting service) throws IOException {
        // Create the DateRange object.
        final DateRange dateRange = new DateRange()
                .setStartDate("2018-01-19")
                .setEndDate("today");

        // Create the Metrics object.
        final Metric users = new Metric()
                .setExpression("ga:users")
                .setAlias("users");
        final Metric sessions = new Metric()
                .setExpression("ga:sessions")
                .setAlias("sessions");
        final Metric pageviews = new Metric()
                .setExpression("ga:pageviews")
                .setAlias("pageviews");

        final Dimension pageTitle = new Dimension().setName("ga:pageTitle");

        // Create the ReportRequest object.
        final ReportRequest request = new ReportRequest()
                .setViewId(VIEW_ID)
                .setDateRanges(Arrays.asList(dateRange))
                .setMetrics(Arrays.asList(users,sessions,pageviews))
                .setDimensions(Arrays.asList(pageTitle));

        final ArrayList<ReportRequest> requests = new ArrayList<>();
        requests.add(request);

        // Create the GetReportsRequest object.
        final GetReportsRequest getReport = new GetReportsRequest()
                .setReportRequests(requests);

        // Call the batchGet method.
        final GetReportsResponse response =
                service.reports().batchGet(getReport).execute();

        // Return the response.
        return response;
    }

    /**
     * Parses and prints the Analytics Reporting API V4 response.
     *
     * @param response An Analytics Reporting API V4 response.
     */
    private static int[] getResponse(final GetReportsResponse response) {
        final int[] res = new int[3];
        final int[] oldData = {914, 1282, 4113};

        for (final Report report: response.getReports()) {
            final ColumnHeader header =
                    report.getColumnHeader();
            final List<String> dimensionHeaders =
                    header.getDimensions();
            final List<MetricHeaderEntry> metricHeaders =
                    header.getMetricHeader().getMetricHeaderEntries();
            final List<ReportRow> rows =
                    report.getData().getRows();

            for (final ReportRow row: rows) {
                final List<String> dimensions = row.getDimensions();
                final List<DateRangeValues> metrics = row.getMetrics();

                for (int j = 0; j < metrics.size(); j++) {
                    final DateRangeValues values = metrics.get(j);
                    for (int k = 0; k < values.getValues().size() && k < metricHeaders.size(); k++) {
                        res[k] = Integer.parseInt(values.getValues().get(k)) + oldData[k];
                    }
                }
            }
        }
        return res;
    }


}
