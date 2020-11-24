/*
 * HttpStatusException.java
 * Copyright (c) 2020 Ourcycle Sistemas.
 *
 * This software is confidential and Ourycle Sistemas property.
 * Its distribution or dissemination of its content is not permitted without
 * express authorization from Ourcycle Sistemas.
 * This archive contains proprietary information
 */

package dev.ourcycle.cm.util.jsonResponse.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class HttpStatusException extends RuntimeException {

    private static final long serialVersionUID = -3901000762616680152L;

    private String message;
    private final HttpStatus httpStatus;
    private List<String> messages;

    public HttpStatusException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public HttpStatusException(List<String> messages) {
        this(messages, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public HttpStatusException(List<String> messages, HttpStatus httpStatus) {
        this.messages = messages;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public List<String> getMessages() {
        return messages;
    }
}
