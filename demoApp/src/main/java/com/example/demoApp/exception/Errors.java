package com.example.demoApp.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Errors {
    public List<ErrorItem> errors = new ArrayList<>();

    public void addError(String title, String detail) {
        errors.add(new ErrorItem(null, null, title, detail, null));
    }

    public void addError(String title, String detail, Map<String, Object> meta) {
        errors.add(new ErrorItem(null, null, title, detail, meta));
    }

    public void addError(String status, String code, String title, String detail, Map<String, Object> meta) {
        errors.add(new ErrorItem(status, code, title, detail, meta));
    }

    /**
     * see <a href="http://jsonapi.org/format/">http://jsonapi.org/format/</a>
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ErrorItem {
        public String status;
        public String code;
        public String title;
        public String detail;

        public Map<String, Object> meta;

        public ErrorItem(String title, String detail) {
            this(null, null, title, detail, null);
        }

        public ErrorItem(String title, String detail, Map<String, Object> meta) {
            this(null, null, title, detail, meta);
        }

        public ErrorItem(String status, String code, String title, String detail, Map<String, Object> meta) {
            this.status = status;
            this.code = code;
            this.title = title;
            this.detail = detail;
            this.meta = meta;
        }
    }
}

