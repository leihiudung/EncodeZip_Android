package com.bvt.encodezip;

public class StatusVo {

    private Integer code;
    private String msg;
    private Object data;

    private StatusVo(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }

    private StatusVo(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static StatusVo ok(String msg, Object data) {
        return new StatusVo(200, msg, data);
    }

    public static StatusVo ok(String msg) {
        return new StatusVo(200, msg);
    }

    public static StatusVo error(String msg) {
        return new StatusVo(500, msg);
    }

    public static StatusVo error() {
        return new StatusVo(500, "异常错误");
    }

    public static StatusVo create(Integer code, String msg, Object data) {
        return new StatusVo(code, msg, data);
    }

    public static StatusVo create(Integer code, String msg) {
        return new StatusVo(code, msg);
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}