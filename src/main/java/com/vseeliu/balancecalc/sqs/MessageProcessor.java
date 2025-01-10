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

package com.vseeliu.balancecalc.sqs;

import com.vseeliu.balancecalc.service.TransactionService;
import com.vseeliu.balancecalc.util.AwsSqsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;
import java.util.concurrent.*;

/**
 * Message Processing Service, which provides multi-threading capabilities to offer throughput.
 *
 * @author vseeliu
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Component
public class MessageProcessor {
    private final TransactionService transactionService;
    private final AwsSqsUtil awsSqsUtil;
    private final ExecutorService unprocessExecutorService =
        new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    @Autowired
    public MessageProcessor(TransactionService transactionService, AwsSqsUtil awsSqsUtil) {
        this.transactionService = transactionService;
        this.awsSqsUtil = awsSqsUtil;
    }

    @Async
    public void processMessage(List<Message> messages) {
        for (Message msg : messages) {
            unprocessExecutorService.submit(new MessageProcessTask(awsSqsUtil, msg, transactionService));
        }
    }
}