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

package com.vseeliu.balancecalc.entity;

import com.vseeliu.balancecalc.constant.ProcessResult;
import lombok.Data;

@Data
public class TransactionResult {
    private boolean success;
    private String errorCode;
    private String message;

    public TransactionResult(boolean success, String errCode, String message) {
        this.success = success;
        this.errorCode = errCode;
        this.message = message;
    }

    public TransactionResult(boolean success, ProcessResult processResult) {
        this.success = success;
        this.errorCode = processResult.getErrorCode();
        this.message = processResult.getMessage();
    }
}