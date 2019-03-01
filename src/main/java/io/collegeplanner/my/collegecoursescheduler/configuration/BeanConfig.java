package io.collegeplanner.my.collegecoursescheduler.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.jdbi.v3.spring4.JdbiFactoryBean;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public JdbiFactoryBean jdbi() {
        return new JdbiFactoryBean(dataSource).setAutoInstallPlugins(true);
    }
}
