package com.musinsa.productmanageserver.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.productmanageserver.exception.DataParseException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataParser {

    private final ObjectMapper objectMapper;

    public <R> R parse(String data, Class<R> clazz) {
        try {
            return objectMapper.readValue(data, clazz);
        } catch (IOException e) {
            throw new DataParseException(e.getMessage());
        }
    }

    public <T> String parseToString(T data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new DataParseException(e.getMessage());
        }
    }
}
