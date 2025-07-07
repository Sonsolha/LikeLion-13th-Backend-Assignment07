package com.likelion.basecode.common.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    // Amazon S3 Bean 생성
    public AmazonS3 amazonS3() {
        // IAM 인증 정보 설정
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        // AmazonS3 클라이언트를 빌드하여 반환
        return AmazonS3ClientBuilder.standard()
                .withRegion(region) // 리전 설정
                .withCredentials(new AWSStaticCredentialsProvider(credentials)) // 인증 정보 주입
                .build();
    }
}

