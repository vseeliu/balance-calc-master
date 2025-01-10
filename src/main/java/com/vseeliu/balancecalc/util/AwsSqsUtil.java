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

package com.vseeliu.balancecalc.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.UUID;

/**
 * aws sqs util
 *
 * @author vseeliu
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Component
public class AwsSqsUtil {
    @Value("${aws.sqs.queue.url}")
    private String queueUrl;
    private final SqsClient sqsClient;

    @Autowired
    public AwsSqsUtil(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public void sendMessage(String messageBody) {
        try {
            SendMessageRequest sendMessageRequest =
                SendMessageRequest.builder().queueUrl(queueUrl)
                    .messageGroupId("unprocessed-message")
                    .messageDeduplicationId(UUID.randomUUID().toString())
                    .messageBody(messageBody).build();
            sqsClient.sendMessage(sendMessageRequest);
        } catch (SqsException sqsException) {
            log.error(sqsException.awsErrorDetails().errorMessage());
        }
    }

    public void deleteMessage(Message message) {
        try {
            DeleteMessageRequest deleteMessageRequest =
                DeleteMessageRequest.builder().queueUrl(queueUrl).receiptHandle(message.receiptHandle()).build();
            sqsClient.deleteMessage(deleteMessageRequest);
        } catch (SqsException sqsException) {
            log.error(sqsException.awsErrorDetails().errorMessage());
        }
    }
}
