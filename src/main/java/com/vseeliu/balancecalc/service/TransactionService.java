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

package com.vseeliu.balancecalc.service;

import com.vseeliu.balancecalc.Repository.AccountRepository;
import com.vseeliu.balancecalc.Repository.TransactionRepository;
import com.vseeliu.balancecalc.constant.ProcessResult;
import com.vseeliu.balancecalc.entity.Account;
import com.vseeliu.balancecalc.entity.Transaction;
import com.vseeliu.balancecalc.entity.TransactionResult;
import com.vseeliu.balancecalc.exception.TransactionProcessingException;
import com.vseeliu.balancecalc.util.AwsSqsUtil;
import com.vseeliu.balancecalc.util.JsonUtil;
import com.vseeliu.balancecalc.util.RedisLockUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Account Service, which mainly provides functions such as asynchronous real-time calculation,
 * synchronous real-time calculation, and asynchronous result query.
 *
 * @author vseeliu
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Service
public class TransactionService {
    private static final Long LOCK_EXPIRE = 3 * 1000L;// lock expire time
    private static final int RETRY_COUNT = 3;// retry times
    private static final String ACCOUNT_CACHE_PREFIX = "account_";
    private static final String TRANSACTION_CACHE_PREFIX = "transaction_";
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisLockUtil redisLockUtil;
    private final AwsSqsUtil awsSqsUtil;

    @Autowired
    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository,
        StringRedisTemplate stringRedisTemplate, RedisLockUtil redisLockUtil1, AwsSqsUtil awsSqsUtil) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisLockUtil = redisLockUtil1;
        this.awsSqsUtil = awsSqsUtil;
    }

    @Transactional
    public TransactionResult processTransaction(Transaction transaction) {
        String message = "";
        String errCode = "";
        for (int attempt = 1; attempt <= RETRY_COUNT; attempt++) {
            try {
                return processTransactionInternal(transaction);
            } catch (TransactionProcessingException transactionProcessingException) {
                message = transactionProcessingException.getMessage();
                errCode = transactionProcessingException.getErrorCode();
                log.error("Attempt {} failed: {}.", attempt, transactionProcessingException.getMessage());
            }
        }
        return new TransactionResult(false, errCode, message);
    }

    public TransactionResult transferTransaction(Transaction transaction) {
        awsSqsUtil.sendMessage(JsonUtil.toJsonString(transaction));
        return new TransactionResult(true, ProcessResult.TRANSFER.getErrorCode(), "Transaction is transferred");
    }

    public TransactionResult processTransactionAsync(Transaction transaction) {
        return processTransactionInternal(transaction);
    }

    public TransactionResult getTransactionResult(String transactionId) {
        if (transactionRepository.existsByTransactionId(transactionId)) {
            return new TransactionResult(true, ProcessResult.SUCCESS.getErrorCode(),
                "Transaction " + transactionId + " successfully processed");
        }
        return new TransactionResult(false, ProcessResult.NO_PROCESSED.getErrorCode(),
            "Transaction " + transactionId + " not processed");
    }

    private TransactionResult processTransactionInternal(Transaction transaction) {
        String transactionId = transaction.getTransactionId();
        String sourceAccountNumber = transaction.getSourceAccountNumber();
        String targetAccountNumber = transaction.getTargetAccountNumber();

        String transactionLockKey = "transaction_lock_" + transactionId;
        String sourceAccountLockKey = "account_lock_" + sourceAccountNumber;
        String targetAccountLockKey = "account_lock_" + targetAccountNumber;
        try {
            // Attempt to acquire distributed locks for the transaction ID,source account,and target account.
            boolean transactionLockAcquired = redisLockUtil.tryLock(transactionLockKey, transactionId, LOCK_EXPIRE);
            boolean sourceAccountLockAcquired = redisLockUtil.tryLock(sourceAccountLockKey, transactionId, LOCK_EXPIRE);
            boolean targetAccountLockAcquired = redisLockUtil.tryLock(targetAccountLockKey, transactionId, LOCK_EXPIRE);

            if (!transactionLockAcquired || !sourceAccountLockAcquired || !targetAccountLockAcquired) {
                log.error("Transaction ID already exists, no need to process again. transaction is = {}",
                    transactionId);
                throw new TransactionProcessingException("Can not acquire distributed lock", "LOCK_ACQUISITION_FAILED");
            }

            // Check if the transaction ID already exists(to prevent it from being processed by another node while waiting for the lock)
            if (getTransactionFromCacheOrDB(transactionId).isPresent()) {
                log.info("Transaction ID already exists, no need to process again. transaction is = {}", transactionId);
                return new TransactionResult(true, ProcessResult.HAS_PROCESSED.getErrorCode(),
                    "Transaction ID already exists, no need to process again.");
            }
            return updateBalance(transaction);
        } catch (Exception exception) {
            throw new TransactionProcessingException(exception.getMessage());
        } finally {
            // release lock
            redisLockUtil.releaseLock(transactionLockKey, transactionId);
            redisLockUtil.releaseLock(sourceAccountLockKey, transactionId);
            redisLockUtil.releaseLock(targetAccountLockKey, transactionId);
        }
    }

    private TransactionResult updateBalance(Transaction transaction) {
        // Check if the account exists.
        Optional<Account> sourceAccountOpt = getAccountFromCacheOrDB(transaction.getSourceAccountNumber());
        Optional<Account> targetAccountOpt = getAccountFromCacheOrDB(transaction.getTargetAccountNumber());
        BigDecimal amount = transaction.getAmount();
        if (sourceAccountOpt.isPresent() && targetAccountOpt.isPresent()) {
            Account sourceAccount = sourceAccountOpt.get();
            Account targetAccount = targetAccountOpt.get();
            BigDecimal sourceBalance = sourceAccount.getBalance();
            BigDecimal destinationBalance = targetAccount.getBalance();
            if (sourceBalance.compareTo(amount) >= 0) {
                sourceAccount.setBalance(sourceBalance.subtract(amount));
                targetAccount.setBalance(destinationBalance.add(amount));
                accountRepository.save(sourceAccount);
                accountRepository.save(targetAccount);
                transactionRepository.save(transaction);
                // update redis cache
                updateAccountBalanceInCache(sourceAccount);
                updateAccountBalanceInCache(targetAccount);
                updateTransactionInCache(transaction);
            } else {
                // insufficient balance
                throw new TransactionProcessingException(
                    "The transaction account balance is insufficient, please check.",
                    ProcessResult.INSUFFICIENT_FUNDS.getErrorCode());
            }
        } else {
            // account not exist
            throw new TransactionProcessingException("The transaction account does not exist, please check.",
                ProcessResult.ACCOUNT_NOT_FOUND.getErrorCode());
        }
        return new TransactionResult(true, ProcessResult.SUCCESS);
    }

    private Optional<Account> getAccountFromCacheOrDB(String accountNumber) {
        String cacheKey = ACCOUNT_CACHE_PREFIX + accountNumber;
        String cacheValue = stringRedisTemplate.opsForValue().get(cacheKey);
        if ("NON".equals(cacheValue)) {
            return Optional.empty();
        }
        if (cacheValue == null) {
            Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
            if (accountOpt.isPresent()) {
                updateAccountBalanceInCache(accountOpt.get());
                return accountOpt;
            } else {
                // Store a special value to indicate that the account does not exist and prevent cache penetration
                stringRedisTemplate.opsForValue().set(cacheKey, "NON", 30, TimeUnit.MILLISECONDS);
                return Optional.empty();
            }
        } else {
            return Optional.of(JsonUtil.fromJsonString(cacheValue, Account.class));
        }
    }

    private Optional<Transaction> getTransactionFromCacheOrDB(String transactionId) {
        String cacheKey = TRANSACTION_CACHE_PREFIX + transactionId;
        String cacheValue = stringRedisTemplate.opsForValue().get(cacheKey);
        if ("NON".equals(cacheValue)) {
            return Optional.empty();
        }
        if (cacheValue == null) {
            Optional<Transaction> accountOpt = transactionRepository.findTransactionByTransactionId(transactionId);
            if (accountOpt.isPresent()) {
                updateTransactionInCache(accountOpt.get());
                return accountOpt;
            } else {
                // Store a special value to indicate that the account does not exist and prevent cache penetration
                stringRedisTemplate.opsForValue().set(cacheKey, "NON", 30, TimeUnit.MILLISECONDS);
                return Optional.empty();
            }
        } else {
            return Optional.of(JsonUtil.fromJsonString(cacheValue, Transaction.class));
        }
    }

    private void updateAccountBalanceInCache(Account account) {
        String cacheKey = ACCOUNT_CACHE_PREFIX + account.getAccountNumber();
        String cacheValue = JsonUtil.toJsonString(account);
        stringRedisTemplate.opsForValue().set(cacheKey, cacheValue);
    }

    private void updateTransactionInCache(Transaction transaction) {
        String cacheKey = TRANSACTION_CACHE_PREFIX + transaction.getTransactionId();
        String cacheValue = JsonUtil.toJsonString(transaction);
        stringRedisTemplate.opsForValue().set(cacheKey, cacheValue);
    }
}