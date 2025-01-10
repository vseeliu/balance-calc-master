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

package com.vseeliu.balancecalc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * Json util
 *
 * @author vseeliu
 * @version 1.0
 * @since 1.0
 */
@Slf4j
public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException jsonProcessingException) {
            throw new RuntimeException("Failed to convert object to JSON string", jsonProcessingException);
        }
    }

    public static <T> T fromJsonString(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (JsonProcessingException jsonProcessingException) {
            throw new RuntimeException("Failed to convert object to JSON string", jsonProcessingException);
        }
    }
}