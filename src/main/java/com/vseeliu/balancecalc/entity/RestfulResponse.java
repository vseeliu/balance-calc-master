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

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Setter
@Getter
public class RestfulResponse<T> implements Serializable {
    private int status;
    private String message;
    private T data;

    public RestfulResponse(int status, T data) {
        this.status = status;
        this.message = "Success";
        this.data = data;
    }

    public RestfulResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public RestfulResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }

    public static <T> RestfulResponse<T> success(T data) {
        return new RestfulResponse<>(HttpStatus.OK.value(), data);
    }

    public static <T> RestfulResponse<T> success(String message, T data) {
        return new RestfulResponse<>(HttpStatus.OK.value(), message, data);
    }

    public static <T> RestfulResponse<T> fail(int status, String message) {
        return new RestfulResponse<>(status, message);
    }

    public static <T> RestfulResponse<T> fail(int status, String message, T data) {
        return new RestfulResponse<>(status, message, data);
    }
}