package com.huatu.teacheronline.personal.bean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 18250 on 2017/10/24.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class HourEntity implements Serializable {
    /**
     * next : [{"netclass_pdf":"","video_status":"1","isTrial":0,"ccCourses_id":null,"ccApi_key":null,"camera":"1","lubourl":"","number":"","hd":"","pic":"","is_buy":1,"videoType":1,"Title":"教综早读-王晓燕\n","kouling":"","NetClassId":"1925","classTitle":"24小时大咖直播在线","ccUid":null,"zhibourl":"","zhibotime":"07:30-08:00","is_next_day":"1","iscamera":1},{"netclass_pdf":"","video_status":"1","isTrial":0,"ccCourses_id":null,"ccApi_key":null,"camera":"1","lubourl":"","number":"","hd":"","pic":"","is_buy":1,"videoType":1,"Title":"2017年下半年四川教师招聘笔试备考讲座-王晓燕\n","kouling":"","NetClassId":"1928","classTitle":"24小时大咖直播在线","ccUid":null,"zhibourl":"","zhibotime":"08:00-09:30","is_next_day":"1","iscamera":1},{"netclass_pdf":"","video_status":"1","isTrial":0,"ccCourses_id":null,"ccApi_key":null,"camera":"1","lubourl":"","number":"","hd":"","pic":"","is_buy":1,"videoType":1,"Title":"资格笔试刷题：一起来刷语文必做题-刘伟丹\n","kouling":"zgyw123","NetClassId":"1929","classTitle":"24小时大咖直播在线","ccUid":null,"zhibourl":"","zhibotime":"09:30-11:00","is_next_day":"1","iscamera":1}]
     * today : [{"netclass_pdf":"","video_status":0,"isTrial":0,"ccCourses_id":null,"ccApi_key":null,"camera":"1","lubourl":"","number":"","hd":"","pic":"","is_buy":1,"videoType":1,"Title":"中学科目二笔试备考","kouling":"","NetClassId":"1920","classTitle":"24小时大咖直播在线","ccUid":null,"zhibourl":"be44b3a821dc4245a6398c00577aeec4","zhibotime":"09:30-11:00","is_next_day":"0","iscamera":1},{"netclass_pdf":"","video_status":1,"isTrial":0,"ccCourses_id":null,"ccApi_key":null,"camera":"1","lubourl":"","number":"","hd":"","pic":"","is_buy":1,"videoType":1,"Title":"2018福建教师招聘考试数学学科备考-王威\n","kouling":"","NetClassId":"1921","classTitle":"24小时大咖直播在线","ccUid":null,"zhibourl":"6b90d1f89bed45cd8908c1a12a978451","zhibotime":"11:00-12:00","is_next_day":"0","iscamera":1},{"netclass_pdf":"","video_status":1,"isTrial":0,"ccCourses_id":null,"ccApi_key":null,"camera":"1","lubourl":"","number":"","hd":"","pic":"","is_buy":1,"videoType":1,"Title":"解开教招考试（资格证）的记忆密码-武晶晶\n","kouling":"","NetClassId":"1922","classTitle":"24小时大咖直播在线","ccUid":null,"zhibourl":"be44b3a821dc4245a6398c00577aeec4","zhibotime":"12:30-14:00","is_next_day":"0","iscamera":1},{"netclass_pdf":"","video_status":1,"isTrial":0,"ccCourses_id":null,"ccApi_key":null,"camera":"1","lubourl":"","number":"","hd":"","pic":"","is_buy":1,"videoType":1,"Title":"教师资格证小学科目二刷题（14）-唐堂老师\n","kouling":"","NetClassId":"1923","classTitle":"24小时大咖直播在线","ccUid":null,"zhibourl":"6b90d1f89bed45cd8908c1a12a978451","zhibotime":"14:00-15:30","is_next_day":"0","iscamera":1},{"netclass_pdf":"","video_status":1,"isTrial":0,"ccCourses_id":null,"ccApi_key":null,"camera":"1","lubourl":"","number":"","hd":"","pic":"","is_buy":1,"videoType":1,"Title":"带你读懂英语面试评分标准-李敏\n","kouling":"","NetClassId":"1924","classTitle":"24小时大咖直播在线","ccUid":null,"zhibourl":"be44b3a821dc4245a6398c00577aeec4","zhibotime":"15:30-17:00","is_next_day":"0","iscamera":1},{"netclass_pdf":"","video_status":1,"isTrial":0,"ccCourses_id":null,"ccApi_key":null,"camera":"1","lubourl":"","number":"","hd":"","pic":"","is_buy":1,"videoType":1,"Title":"2018江西招考，你必须知道的一些事-李嫘\n","kouling":"","NetClassId":"1926","classTitle":"24小时大咖直播在线","ccUid":null,"zhibourl":"6b90d1f89bed45cd8908c1a12a978451","zhibotime":"17:00-19:00","is_next_day":"0","iscamera":1},{"netclass_pdf":"","video_status":1,"isTrial":0,"ccCourses_id":null,"ccApi_key":null,"camera":"1","lubourl":"","number":"","hd":"","pic":"","is_buy":1,"videoType":1,"Title":"老司机带你正确打开资格证语文笔试-真题篇-刘伟丹\n","kouling":"","NetClassId":"1927","classTitle":"24小时大咖直播在线","ccUid":null,"zhibourl":"be44b3a821dc4245a6398c00577aeec4","zhibotime":"19:00-21:00","is_next_day":"0","iscamera":1},{"netclass_pdf":"","video_status":1,"isTrial":0,"ccCourses_id":null,"ccApi_key":null,"camera":"1","lubourl":"","number":"","hd":"","pic":"","is_buy":1,"videoType":1,"Title":"老司机带你正确打开资格证语文笔试-真题篇-刘伟丹\n","kouling":"","NetClassId":"1927","classTitle":"24小时大咖直播在线","ccUid":null,"zhibourl":"be44b3a821dc4245a6398c00577aeec4","zhibotime":"19:00-21:00","is_next_day":"0","iscamera":1}]
     * pic : http://mobileapp.hteacher.net/teacher/attachments/living/063237144dadfffbedc73735c9cf84dc.png
     */
    public List<TodayEntity> next;
    public List<TodayEntity> today;
    private String pic;

    public void setNext(List<TodayEntity> next) {
        this.next = next;
    }

    public void setToday(List<TodayEntity> today) {
        this.today = today;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public List<TodayEntity> getNext() {
        return next;
    }

    public List<TodayEntity> getToday() {
        return today;
    }

    public String getPic() {
        return pic;
    }

}
