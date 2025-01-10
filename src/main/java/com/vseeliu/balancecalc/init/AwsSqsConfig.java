/*
 * Copyright (c) 2025- vseeliu. All rights reserved.
 *
 *  This file is part of the balance calculation. Without authorization, copying, modifying, distributing,
 *  or using part or all of the content of this file is prohibited,
 *  unless the following conditions are met:
 *  This copyright notice and other copyright information in the file must be retained.
 *  This copyright notice must be included in any modified or derivative works.
 *  This software is provided "as is" and does not provide any express or implied warranties,
 *  including but not limited to merchantability,
 *  fitness for a particular purpose, and non-infringement warranties. Under any circumstances,
 *  [company name or individual name] will not be responsible for any direct, indirect, incidental, special, exemplary,
 *  or consequential damages resulting from the use of this software,
 *  even if it has been informed that such damages may occur.
 *  For more information, please contact:：381054972@qq.com vseeliu
 */

package com.vseeliu.balancecalc.init;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * initialize aws sql client
 *
 * @author vseeliu
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class AwsSqsConfig {
    @Value("${aws.credentials.accessKey}")
    private String accessKey;

    @Value("${aws.credentials.secretKey}")
    private String secretKey;

    @Value("${aws.credentials.region}")
    private String region;

    /**
     * aws sqs client init
     *
     * @return sqsClient
     */
    @Bean
    public SqsClient awsSQSClient() {
        return SqsClient.builder()
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
            .region(Region.of(region)).build();
    }
}
