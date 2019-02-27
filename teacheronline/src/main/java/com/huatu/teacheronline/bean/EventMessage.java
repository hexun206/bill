package com.huatu.teacheronline.bean;

import java.io.Serializable;

/**
 * Created by 18250 on 2017/3/7.
 */
public class EventMessage implements Serializable{
    public static final int NEED_RE_LOGIN = 1000;
    private int type;
    private Object object;

    public EventMessage(int type, Object object) {
        this.type = type;
        this.object = object;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
