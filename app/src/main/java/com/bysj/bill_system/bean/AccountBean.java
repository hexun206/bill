package com.bysj.bill_system.bean;

public class AccountBean {
    public String phone;
    public String passwrod;
    public String nickname;
    public String headUrl;
    public int sex;

    public AccountBean(String phone, String passwrod) {
        this.phone = phone;
        this.passwrod = passwrod;
    }
}
