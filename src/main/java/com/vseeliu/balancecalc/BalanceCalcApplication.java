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

package com.vseeliu.balancecalc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is a microservice for real-time balance calculation, which provides two methods: synchronous calculation and
 * asynchronous calculation. Both have specific retry functions. The synchronous calculation has simple three-time
 * retries, and the asynchronous calculation uses message queues for retries. Both use distributed locks and Amazon
 * ElastiCache high-speed cache, providing higher performance and throughput. Due to time constraints, the locks,
 * exceptions, internationalization, data validation, etc. will be optimized subsequently.
 *
 * @author vseeliu
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication
public class BalanceCalcApplication {
    public static void main(String[] args) {
        SpringApplication.run(BalanceCalcApplication.class, args);
    }
}
