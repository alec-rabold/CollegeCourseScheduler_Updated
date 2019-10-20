package io.collegeplanner.my.collegecoursescheduler.controller.filters;

import io.collegeplanner.my.collegecoursescheduler.service.FirehoseStreamService;
import io.collegeplanner.my.collegecoursescheduler.util.GenericUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static io.collegeplanner.my.collegecoursescheduler.util.Constants.EXCLUDED_LOGGING_FILTER_PATHS;
import static io.collegeplanner.my.collegecoursescheduler.util.Constants.REFERER_HEADER;

@Log4j2
@Component
public class RequestLogFilter implements Filter {

    @Autowired
    private FirehoseStreamService firehoseStreamService;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
                         final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final String remoteAddress = req.getRemoteAddr();
        if(!(GenericUtils.stringContainsItemFromSet(req.getRequestURI(), EXCLUDED_LOGGING_FILTER_PATHS))
            || req.getRequestURI().equals("/")) {
            log.info("Request from {} {} : {} \t Referer: {}",
                    remoteAddress, req.getMethod(), req.getRequestURI(), req.getHeader(REFERER_HEADER));
        }
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterconfig) {}

    @Override
    public void destroy() {}

}
