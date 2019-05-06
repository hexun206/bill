package com.bysj.bill_system.bean;

import java.util.List;

public class ReplyBean {
    //        id:主键、无意义(id 为0 代表一级评论)
//        owner：该评论的发出者
//        toname: 该评论是回复哪个人的
//        sendtime：评论的时间
//        content：评论的内容
//        ssid:是哪条评论的下面的评论
//        pid:一级评论的id
    public int id;
    public String content;
    public String toName;
    public String owner;
    public long sendTime;
    public int ssid;
    public int pid;
    public String toNamePhone;
    public String ownerPhone;
    public List<ReplyBean> replyBeanList;
    public TiebaBean tiebaBean;
}
