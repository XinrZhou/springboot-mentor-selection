package com.example.mentorselection.exception;

import lombok.Data;

@Data
public class XException extends RuntimeException{

    private int code;

    public XException(int code, String message) {
        super(message);
        this.code = code;
    }
}
