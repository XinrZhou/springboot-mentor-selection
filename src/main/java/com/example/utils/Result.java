package com.example.utils;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 返回数据格式
 */
@Data
@Builder
public class Result {

    private Integer code;

    private String message;

    private Map<String, Object> data;

    public static Result success() {
        return Result.builder().code(200).build();
    }
    public static Result success(String msg) {
        return Result.builder().code(200).message(msg).build();
    }

    public static Result success(Map<String, Object> data) {
        return Result.builder().code(200).data(data).build();
    }

    public static Result success(Map<String, Object> data, String msg) {
        return Result.builder().code(200).data(data).message(msg).build();
    }

    public static Result error(String msg) {
        return Result.builder().code(400).message(msg).build();
    }

}
