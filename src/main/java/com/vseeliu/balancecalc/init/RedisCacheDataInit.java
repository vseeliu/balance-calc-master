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

import com.vseeliu.balancecalc.Repository.AccountRepository;
import com.vseeliu.balancecalc.Repository.TransactionRepository;
import com.vseeliu.balancecalc.entity.Account;
import com.vseeliu.balancecalc.entity.Transaction;
import com.vseeliu.balancecalc.util.JsonUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * initialize Redis High-Speed Cache
 *
 * @author vseeliu
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Configuration
public class RedisCacheDataInit {
    private final String ACCOUNT_CACHE_PREFIX = "account_";
    private final String TRANSACTION_CACHE_PREFIX = "transaction_";
    @Autowired
    private final AccountRepository accountRepository;

    @Autowired
    private final TransactionRepository transactionRepository;

    @Autowired
    private final StringRedisTemplate stringRedisTemplate;

    public RedisCacheDataInit(AccountRepository accountRepository, StringRedisTemplate stringRedisTemplate,
        TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostConstruct
    public void init() {
        // TODO Such as obtaining data by paging and partitioning, etc.
        log.info("start init redis data...");
        List<Account> accountList = accountRepository.findAll();
        for (Account account : accountList) {
            stringRedisTemplate.opsForValue()
                .set(ACCOUNT_CACHE_PREFIX + account.getAccountNumber(), JsonUtil.toJsonString(account));
        }

        List<Transaction> transactionList = transactionRepository.findAll();
        for (Transaction transaction : transactionList) {
            stringRedisTemplate.opsForValue()
                .set(TRANSACTION_CACHE_PREFIX + transaction.getTransactionId(), JsonUtil.toJsonString(transaction));
        }
    }
}
