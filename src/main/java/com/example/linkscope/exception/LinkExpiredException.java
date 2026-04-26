package com.example.linkscope.exception;

public class LinkExpiredException extends RuntimeException {

    public LinkExpiredException(String shortCode) {
        super("Link is inactive or expired: " + shortCode);
    }
}
