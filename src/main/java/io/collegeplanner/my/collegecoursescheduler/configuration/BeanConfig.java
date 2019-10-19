package io.collegeplanner.my.collegecoursescheduler.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehose;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.jdbi.v3.spring4.JdbiFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Value("${aws.access.key}")
    private String accessKey;

    @Value("${aws.secret.key}")
    private String secretKey;

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

    //TODO: IAM role access
    @Bean
    public AWSStaticCredentialsProvider awsStaticCredentialsProvider() {
        final BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return new AWSStaticCredentialsProvider(awsCredentials);
    }

    @Bean
    public AmazonKinesisFirehose firehoseClient() {
        return AmazonKinesisFirehoseClient.builder()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(awsStaticCredentialsProvider())
                .build();
    }


}
