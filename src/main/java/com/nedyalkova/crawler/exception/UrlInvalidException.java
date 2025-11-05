package com.nedyalkova.crawler.exception;

public class UrlInvalidException extends Throwable {
    private final String message;
    public UrlInvalidException(String message) {
        this.message = message;
    }
}
