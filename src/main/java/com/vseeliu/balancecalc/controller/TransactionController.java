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

package com.vseeliu.balancecalc.controller;

import com.vseeliu.balancecalc.entity.Transaction;
import com.vseeliu.balancecalc.entity.TransactionResult;
import com.vseeliu.balancecalc.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{transactionId}/result")
    public ResponseEntity<TransactionResult> getTransactionResult(@PathVariable("transactionId") String transactionId) {
        try {
            TransactionResult transactionResult = transactionService.getTransactionResult(transactionId);
            if (transactionResult.isSuccess()) {
                return ResponseEntity.ok(transactionResult);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(transactionResult);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new TransactionResult(false, "", "Transaction failed"));
        }
    }

    @PostMapping
    public ResponseEntity<String> processTransaction(@Valid @RequestBody Transaction transaction) {
        try {
            TransactionResult transactionResult = transactionService.processTransaction(transaction);
            if (transactionResult.isSuccess()) {
                return ResponseEntity.ok(transactionResult.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(transactionResult.getMessage());
            }
        } catch (Exception exception) {
            log.error("Transaction failed, {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transaction failed.");
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> asyncProcessTransaction(@Validated @RequestBody Transaction transaction) {
        try {
            TransactionResult transactionResult = transactionService.transferTransaction(transaction);
            return ResponseEntity.ok(transactionResult.getMessage());
        } catch (Exception exception) {
            log.error("Transaction failed, {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transaction failed.");
        }
    }
}