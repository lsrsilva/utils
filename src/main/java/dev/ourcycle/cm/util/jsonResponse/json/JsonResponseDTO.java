/*
 * JsonResponseDTO.java
 * Copyright (c) 2020 Ourcycle Sistemas.
 *
 * This software is confidential and Ourycle Sistemas property.
 * Its distribution or dissemination of its content is not permitted without
 * express authorization from Ourcycle Sistemas.
 * This archive contains proprietary information
 */

package dev.ourcycle.cm.util.jsonResponse.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

public class JsonResponseDTO {
    private Object result;
    protected String message;
    private List<String> messages;

    public JsonResponseDTO() {
    }

    public JsonResponseDTO(Object result, List<String> messages) {
        this.result = result;
        this.messages = messages;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public static JsonResponseDTO ok() {
        return ok(null, null);
    }

    public static JsonResponseDTO ok(String message) {
        return ok(null, message);
    }

    public static JsonResponseDTO ok(Object result) {
        return ok(result, null);
    }

    public static JsonResponseDTO ok(Object result, String message) {
        JsonResponseDTO JsonResponseDTO = new JsonResponseDTO();
        JsonResponseDTO.setResult(result);
        JsonResponseDTO.setMessage(message);
        return JsonResponseDTO;
    }

    public static JsonResponseDTO otherReponse(String message) {
        return otherReponse(null, message, null);
    }

    public static JsonResponseDTO otherReponse(String message, List<String> messages) {
        return otherReponse(null, message, messages);
    }

    public static JsonResponseDTO otherReponse(Object result, String message, List<String> messages) {
        JsonResponseDTO JsonResponseDTO = new JsonResponseDTO();
        JsonResponseDTO.setResult(result);
        JsonResponseDTO.setMessage(message);
        JsonResponseDTO.setMessages(messages);
        return JsonResponseDTO;
    }
}
