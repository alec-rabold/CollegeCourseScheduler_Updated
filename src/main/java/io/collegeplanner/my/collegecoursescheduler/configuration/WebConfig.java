package io.collegeplanner.my.collegecoursescheduler.configuration;

import io.collegeplanner.my.collegecoursescheduler.controller.filters.RequestLogFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class WebConfig {

    @Bean
    public Filter requestLogFilter() {
        return new RequestLogFilter();
    }
}
