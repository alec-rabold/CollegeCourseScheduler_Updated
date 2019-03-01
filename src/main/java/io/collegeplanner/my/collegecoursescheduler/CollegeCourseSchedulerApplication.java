 package io.collegeplanner.my.collegecoursescheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

 @SpringBootApplication
 public class CollegeCourseSchedulerApplication extends SpringBootServletInitializer {

     @Override
     protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
         return application.sources(CollegeCourseSchedulerApplication.class);
     }

     public static void main(String[] args) {
         SpringApplication.run(CollegeCourseSchedulerApplication.class, args);
     }
 }

