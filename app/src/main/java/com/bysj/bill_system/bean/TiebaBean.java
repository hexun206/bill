package com.bysj.bill_system.bean;

import java.util.List;

public class TiebaBean {
    public int id;
    public String title;
    public String content;
    public String style;
    public String nickname;
    public String phone;
    public String headUrl;
    public long time;
    public List<ReplyBean> replys;
    public TiebaBean(){}
    public TiebaBean(int id, String title, String content, String style, String nickname, String phone, String headUrl, long time) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.style = style;
        this.nickname = nickname;
        this.phone = phone;
        this.headUrl = headUrl;
        this.time = time;
    }
}
