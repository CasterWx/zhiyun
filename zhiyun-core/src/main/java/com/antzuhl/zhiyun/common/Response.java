package com.antzuhl.zhiyun.common;

import lombok.Data;

/**
 * @author AntzUhl
 * Date 2020/09/27 16:17
 */
@Data
public class Response {

    private boolean isOk;
    private int code;
    private String message;
    private Object data;

    private Response() {}

    public static Response success() {
        Response response = new Response();
        response.setOk(true);
        response.setCode(CustomExceptionEnum.SUCCESS.getCode());
        response.setMessage("success");
        return response;
    }

    public static Response success(String message) {
        Response response = new Response();
        response.setOk(true);
        response.setCode(CustomExceptionEnum.SUCCESS.getCode());
        response.setMessage(message);
        return response;
    }

    public static Response success(String message, Object data) {
        Response response = new Response();
        response.setOk(true);
        response.setCode(CustomExceptionEnum.SUCCESS.getCode());
        response.setData(data);
        response.setMessage(message);
        return response;
    }

    public static Response error(String message) {
        Response response = new Response();
        response.setOk(false);
        response.setCode(CustomExceptionEnum.USER_INPUT_ERROR.getCode());
        response.setMessage(message);
        return response;
    }
}
