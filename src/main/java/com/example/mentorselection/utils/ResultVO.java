package com.example.mentorselection.utils;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 返回数据格式
 */
@Data
@Builder
public class ResultVO {
    public static final int BAD_REQUEST = 400;

    public static final int UNAUTHORIZED = 401;

    public static final int CONFLICT = 409;

    private Integer code;

    private String message;

    private Map<String, Object> data;

    public static ResultVO success() {
        return ResultVO.builder().code(200).build();
    }
    public static ResultVO success(String msg) {
        return ResultVO.builder().code(200).message(msg).build();
    }

    public static ResultVO success(Map<String, Object> data) {
        return ResultVO.builder().code(200).data(data).build();
    }

    public static ResultVO error(Integer code, String msg) {
        return ResultVO.builder().code(code).message(msg).build();
    }

}
