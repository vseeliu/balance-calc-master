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

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Transaction ID cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Transaction ID can only contain letters, numbers, and underscores")
    private String transactionId;

    @NotBlank(message = "The source account number cannot be blank.")
    @Pattern(regexp = "^\\d{10,20}$",
        message = "The source account number must be a number consisting of 10 to 20 digits.")
    private String sourceAccountNumber;

    @NotBlank(message = "The target account number cannot be blank.")
    @Pattern(regexp = "^\\d{10,20}$",
        message = "The target account number must be a number consisting of 10 to 20 digits.")
    private String targetAccountNumber;

    @NotNull(message = "Transaction amount cannot be null")
    @DecimalMin(value = "0.01", message = "Transaction amount must be greater than 0")
    private BigDecimal amount;

    @Min(value = 1000000000L)
    @Max(value = 9999999999L)
    private Long timestamp;
}
