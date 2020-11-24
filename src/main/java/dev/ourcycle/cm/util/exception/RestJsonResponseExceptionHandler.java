/*
 * RestJsonResponseExceptionHandler.java
 * Copyright (c) 2020 Ourcycle Sistemas.
 *
 * This software is confidential and Ourycle Sistemas property.
 * Its distribution or dissemination of its content is not permitted without
 * express authorization from Ourcycle Sistemas.
 * This archive contains proprietary information
 */

package dev.ourcycle.cm.util.exception;

import dev.ourcycle.cm.util.jsonResponse.exception.HttpStatusException;
import dev.ourcycle.cm.util.jsonResponse.json.JsonResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestJsonResponseExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {HttpStatusException.class})
    protected ResponseEntity<JsonResponseDTO> handleConflict(HttpStatusException ex) {
        return new ResponseEntity<>(JsonResponseDTO.otherReponse(
                ex.getMessage(),
                ex.getMessages()
        ), ex.getHttpStatus());
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<JsonResponseDTO> handleConflict(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(
                JsonResponseDTO.otherReponse(
                        "Internal Server Error!"
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
