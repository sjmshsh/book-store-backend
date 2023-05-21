package com.bookback.utils;

public class JsonResponse<T> {
    private String code;
    private String message;
    private T data;


    public JsonResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public JsonResponse(T data) {
        this.data = data;
        this.message = "success";
        this.code = "0";
    }

    public static JsonResponse<String> success() {
        return new JsonResponse<String>(null);
    }

    public static JsonResponse<String> success(String data) {
        return new JsonResponse<>(data);
    }

    public static JsonResponse<String> fail() {
        return new JsonResponse<>("1", "失败");
    }

    public static JsonResponse<String> fali(String code, String message) {
        return new JsonResponse<>(code, message);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
