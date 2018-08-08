package com.example.demoApp.exception;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiError {
    private String type;
    private String description;

    @JsonUnwrapped
//    private Map<String, String> additionalData = new LinkedHashMap<>();
    private AdditionalData additionalData = new AdditionalData();


    public ApiError(String type) {
        this(type, null);
    }

    public ApiError(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public ApiError addData(String key, String value){
        additionalData.put(key, value);
        return this;
    }

    private static class AdditionalData {
        private Map<String, String> data = new LinkedHashMap<>();
        
        @JsonAnyGetter
        public Map<String, String> getAdditionalData() {
            return data;
        }

        private void put(String key, String value) {
            data.put(key, value);
        }
    }
}
