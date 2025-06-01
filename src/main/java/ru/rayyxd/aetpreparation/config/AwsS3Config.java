package ru.rayyxd.aetpreparation.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3Config {

    // Берём лишь регион и имя бакета из application.properties
    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                // Провайдер, который автоматически читает ~/.aws/credentials
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.fromName(awsRegion))
                .build();
    }
}
