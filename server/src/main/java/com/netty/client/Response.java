package com.netty.client;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = -7923482537225241373L;
    private Integer id;
    private String name;
    private String msg;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMsg() {
        return msg;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
