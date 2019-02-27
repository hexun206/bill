package com.huatu.teacheronline.bean;

import java.io.Serializable;

/**
 * Created by 18250 on 2017/3/7.
 *
 */
public class MyssageBean implements Serializable{

    private String type_1;
    private String type_2;
    private String type_3;
    public MyssageBean(String type_1, String type_2, String type_3) {
        this.type_1 = type_1;
        this.type_2 = type_2;
        this.type_3 = type_3;
    }

    public String gettype_1() {
        return type_1;
    }

    public void settype_1(String type_1) {
        this.type_1 = type_1;
    }

    public String gettype_2() {
        return type_2;
    }

    public void settype_2(String type_2) {
        this.type_2 = type_2;
    }

    public String gettype_3() {
        return type_3;
    }

    public void settype_3(String type_3) {
        this.type_3 = type_3;
    }
}
