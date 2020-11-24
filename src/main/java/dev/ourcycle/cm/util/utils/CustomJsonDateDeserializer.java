/*
 * CustomJsonDateDeserializer.java
 * Copyright (c) 2020 Ourcycle Sistemas.
 *
 * This software is confidential and Ourycle Sistemas property.
 * Its distribution or dissemination of its content is not permitted without
 * express authorization from Ourcycle Sistemas.
 * This archive contains proprietary information
 */

package dev.ourcycle.cm.util.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomJsonDateDeserializer extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser jsonparser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String date = jsonparser.getText();
        try {
            if (!date.isEmpty()) {
                return format.parse(date);
            }
            return null;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
