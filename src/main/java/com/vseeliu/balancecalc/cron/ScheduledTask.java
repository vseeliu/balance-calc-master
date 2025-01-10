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

package com.vseeliu.balancecalc.cron;

import com.vseeliu.balancecalc.sqs.MessageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.List;

@Component
@EnableScheduling
public class ScheduledTask {
    private final SqsClient sqsClient;
    private final MessageProcessor messageProcessor;
    @Value("${aws.sqs.queue.url}")
    private String queueUrl;

    @Autowired
    public ScheduledTask(SqsClient sqsClient, MessageProcessor messageProcessor) {
        this.sqsClient = sqsClient;
        this.messageProcessor = messageProcessor;
    }

    @Scheduled(fixedRate = 5000)
    public void sqsMessageScheduledTask() {
        ReceiveMessageRequest receiveMessageRequest =
            ReceiveMessageRequest.builder().queueUrl(queueUrl).maxNumberOfMessages(10).build();
        ReceiveMessageResponse messageResponse = sqsClient.receiveMessage(receiveMessageRequest);
        List<Message> messageList = messageResponse.messages();
        messageProcessor.processMessage(messageList);
    }
}
