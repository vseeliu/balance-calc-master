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

import com.vseeliu.balancecalc.entity.Transaction;
import com.vseeliu.balancecalc.entity.TransactionResult;
import com.vseeliu.balancecalc.exception.TransactionProcessingException;
import com.vseeliu.balancecalc.service.TransactionService;
import com.vseeliu.balancecalc.util.AwsSqsUtil;
import com.vseeliu.balancecalc.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.model.Message;

/**
 * Message Processing Service, which provides multi-threading capabilities to offer throughput.
 *
 * @author vseeliu
 * @version 1.0
 * @since 1.0
 */
@Slf4j
public class MessageProcessTask implements Runnable {
    private final Message message;
    private final TransactionService transactionService;
    private final AwsSqsUtil awsSqsUtil;

    public MessageProcessTask(AwsSqsUtil awsSqsUtil, Message message, TransactionService transactionService) {
        this.message = message;
        this.transactionService = transactionService;
        this.awsSqsUtil = awsSqsUtil;
    }

    @Override
    public void run() {
        try {
            Transaction transaction = JsonUtil.fromJsonString(message.body(), Transaction.class);
            if (!checkMsg(transaction)) {
                log.error("transaction message is wrong, drop it.");
                return;
            }
            TransactionResult transactionResult = transactionService.processTransactionAsync(transaction);
            if (!transactionResult.isSuccess()) {
                retry();
            }
        } catch (Exception exception) {
            log.error("transaction process fail, will retry, error msg = {}", exception.getMessage());
            retry();
        } finally {
            deleteMessage();
        }
    }

    private boolean checkMsg(Transaction transaction) {
        // TODO need add data validation and timestamp validation.
        return true;
    }
    private void retry() {
        Transaction transaction = JsonUtil.fromJsonString(message.body(), Transaction.class);
        if (checkMsg(transaction)) {
            awsSqsUtil.sendMessage(message.body());
        }
    }

    private void deleteMessage() {
        awsSqsUtil.deleteMessage(message);
    }
}
