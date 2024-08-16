package com.one_ccs.easy_spring_boot.entity.vo;

import java.util.Optional;

import org.slf4j.MDC;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

public record Result<T>(long id, Integer code, String message, T data) {

    /**
     * 成功（数据、提示信息）
     * @param data<T>
     * @param message<String>
     * @return
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(requestId(), 200, message, data);
    }

    /**
     * 成功
     * @return
     */
    public static <T> Result<T> success() {
        return success("请求成功", null);
    }

    /**
     * 成功（提示信息）
     * @param message<String>
     * @return
     */
    public static <T> Result<T> success(String message) {
        return success(message, null);
    }

    /**
     * 成功（数据）
     * @param data<T>
     * @return
     */
    public static <T> Result<T> success(T data) {
        return success("请求成功", data);
    }

    /**
     * 失败（状态码、提示信息）
     * @param code<Integer>
     * @param message<String>
     * @return
     */
    public static <T> Result<T> failure(Integer code, String message) {
        if (message == null) message = "未知错误，操作失败";
        return new Result<>(requestId(), code, message, null);
    }

    /**
     * 失败
     * @return
     */
    public static <T> Result<T> failure() {
        return failure(400, "操作失败");
    }

    /**
     * 失败（提示信息）
     * @param message<String>
     * @return
     */
    public static <T> Result<T> failure(String message) {
        return failure(400, message);
    }

    /**
     * 未登录 （401）
     * @return
     */
    public static <T> Result<T> unauthorized() {
        return failure(401, "请登录后操作");
    }

    /**
     * 无权访问（403）
     * @return
     */
    public static <T> Result<T> forbidden() {
        return failure(403, "您无权操作");
    }

    /**
     * 将 Result 转为 String JSON
     * @return
     */
    public String toJsonString() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }

    /**
     * 获取当前请求 ID 方便快速定位错误
     * @return ID
     */
    private static long requestId(){
        String requestId = Optional.ofNullable(MDC.get("reqId")).orElse("0");

        return Long.parseLong(requestId);
    }
}
