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

package com.vseeliu.balancecalc.constant;

import lombok.Getter;

@Getter
public enum ProcessResult {
    SUCCESS("T000", "Transaction processed successfully"),
    FAILED("T001", ""),
    ACCOUNT_NOT_FOUND("T002", ""),
    INSUFFICIENT_FUNDS("T003", ""),
    HAS_PROCESSED("T004", "Transaction ID already exists, no need to process again."),
    NO_PROCESSED("T005", "Transaction not processed."),
    TRANSFER("T006", "Transaction is transferred."),
    UNKNOWN_ERROR("T999", "");
    private final String errorCode;
    private final String message;
    ProcessResult(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
