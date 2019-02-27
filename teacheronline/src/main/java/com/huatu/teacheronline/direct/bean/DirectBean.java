package com.huatu.teacheronline.direct.bean;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.umeng.socialize.net.utils.UResponse;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 直播模块全部，我的，bean
 * Created by ply on 2016/1/6.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class DirectBean implements Serializable {
    private String rid;//直播id
    private String TeacherDesc;//老师
    private String Title;//直播标题
    private String title;
    private String lessionCount;//课时
    private String price;//价格  老版接口
    private double ActualPrice;//价格  新版接口
    private String scaleimg;//图片地址
    private String buy_lives;//购买人数
    private String is_fufei;//1 付费 0 免费
    private String content;//详情 webview加载
    private String is_zhibo;//0 往期 1 直播
    private String passwd;//直播口令
    private String yuming;//直播域名
    private String number;//房间号
    private String liveid;//点播id
    private String video_status;//直播状态 0 正在直播 ，1点播地址未生成 ， 2可以点播，3，直播未开始 4正在播放
    private String riqi;//播放日期
    private String is_buy;//1购买过 0未购买
    private String timeLength;//视频时长
    private String zhibotime;//直播时间
    private String zhiboendtime;//直播结束时间
    private String kouling;//直播
    private String zhibourl;//直播ID
    private String lubourl;//录播ID
    private String EffectDateDesc;//有效时间
    private String photo_url;
    private String Brief;
    private String phaseName;
    private String ClassNo;//课程编号
    private String is_living;//是否正在直播
    private String TypeName;//班次类型
    private int seq;//当前播放
    private String isHasJy;//是否有讲义
    private int videoType;//直播类型 1 是直播 0 网课
    private String ccUid;//ccuid
    private String ccApi_key;//ccApi_key
    private String ccCourses_id;//cc视频id
    private int isTrial;//是否试听
    private String NetClassId;//课程的Rid
    private String netclass_pdf;//pdf地址
    private String is_ax_Type;//是否可激活

    public String getIs_ax_Type() {
        return is_ax_Type;
    }

    public void setIs_ax_Type(String is_ax_Type) {
        this.is_ax_Type = is_ax_Type;
    }

    public String getNetClassId() {
        return NetClassId;
    }

    public void setNetClassId(String netClassId) {
        NetClassId = netClassId;
    }

    public int getVideoType() {
        return videoType;
    }

    public void setVideoType(int videoType) {
        this.videoType = videoType;
    }

    public String getCcUid() {
        return ccUid;
    }

    public void setCcUid(String ccUid) {
        this.ccUid = ccUid;
    }

    public String getCcApi_key() {
        return ccApi_key;
    }

    public void setCcApi_key(String ccApi_key) {
        this.ccApi_key = ccApi_key;
    }

    public String getCcCourses_id() {
        return ccCourses_id;
    }

    public void setCcCourses_id(String ccCourses_id) {
        this.ccCourses_id = ccCourses_id;
    }

    public int getIsTrial() {
        return isTrial;
    }

    public void setIsTrial(int isTrial) {
        this.isTrial = isTrial;
    }

    public String getIsHasJy() {
        return isHasJy;
    }

    public void setIsHasJy(String isHasJy) {
        this.isHasJy = isHasJy;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getTeacherDesc() {
        return TeacherDesc;
    }

    public void setTeacherDesc(String teacherDesc) {
        TeacherDesc = teacherDesc;
    }


    public String getLessionCount() {
        return lessionCount;
    }

    public void setLessionCount(String lessionCount) {
        this.lessionCount = lessionCount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public double getActualPrice() {
        return ActualPrice;
    }

    public void setActualPrice(double actualPrice) {
        ActualPrice = actualPrice;
    }

    public String getScaleimg() {
        return scaleimg;
    }

    public void setScaleimg(String scaleimg) {
        this.scaleimg = scaleimg;
    }

    public String getBuy_lives() {
        return buy_lives;
    }

    public void setBuy_lives(String buy_lives) {
        this.buy_lives = buy_lives;
    }

    public String getIs_fufei() {
        return is_fufei;
    }

    public void setIs_fufei(String is_fufei) {
        this.is_fufei = is_fufei;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIs_zhibo() {
        return is_zhibo;
    }

    public void setIs_zhibo(String is_zhibo) {
        this.is_zhibo = is_zhibo;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getYuming() {
        return yuming;
    }

    public void setYuming(String yuming) {
        this.yuming = yuming;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLiveid() {
        return liveid;
    }

    public void setLiveid(String liveid) {
        this.liveid = liveid;
    }

    public String getVideo_status() {
        return video_status;
    }

    public void setVideo_status(String video_status) {
        this.video_status = video_status;
    }

    public String getRiqi() {
        return riqi;
    }

    public void setRiqi(String riqi) {
        this.riqi = riqi;
    }

    public String getIs_buy() {
        return is_buy;
    }

    public void setIs_buy(String is_buy) {
        this.is_buy = is_buy;
    }

    public String getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(String timeLength) {
        this.timeLength = timeLength;
    }

    public String getZhibotime() {
        return zhibotime;
    }

    public void setZhibotime(String zhibotime) {
        this.zhibotime = zhibotime;
    }

    public String getZhiboendtime() {
        return zhiboendtime;
    }

    public void setZhiboendtime(String zhiboendtime) {
        this.zhiboendtime = zhiboendtime;
    }

    public String getKouling() {
        return kouling;
    }

    public void setKouling(String kouling) {
        this.kouling = kouling;
    }

    public String getZhibourl() {
        return zhibourl;
    }

    public void setZhibourl(String zhibourl) {
        this.zhibourl = zhibourl;
    }

    public String getLubourl() {
        return lubourl;
    }

    public void setLubourl(String lubourl) {
        this.lubourl = lubourl;
    }

    public String getEffectDateDesc() {
        return EffectDateDesc;
    }

    public void setEffectDateDesc(String effectDateDesc) {
        EffectDateDesc = effectDateDesc;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getBrief() {
        return Brief;
    }

    public void setBrief(String brief) {
        Brief = brief;
    }

    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public String getClassNo() {
        return ClassNo;
    }

    public void setClassNo(String classNo) {
        ClassNo = classNo;
    }

    public String getIs_living() {
        return is_living;
    }

    public void setIs_living(String is_living) {
        this.is_living = is_living;
    }

    public String getNetclass_pdf() {
        return netclass_pdf;
    }

    public void setNetclass_pdf(String netclass_pdf) {
        this.netclass_pdf = netclass_pdf;
    }

    @Override
    public String toString() {
        return "DirectBean{" +
                "rid='" + rid + '\'' +
                ", TeacherDesc='" + TeacherDesc + '\'' +
                ", Title='" + Title + '\'' +
                ", title='" + title + '\'' +
                ", lessionCount='" + lessionCount + '\'' +
                ", price='" + price + '\'' +
                ", ActualPrice=" + ActualPrice +
                ", scaleimg='" + scaleimg + '\'' +
                ", buy_lives='" + buy_lives + '\'' +
                ", is_fufei='" + is_fufei + '\'' +
                ", content='" + content + '\'' +
                ", is_zhibo='" + is_zhibo + '\'' +
                ", passwd='" + passwd + '\'' +
                ", yuming='" + yuming + '\'' +
                ", number='" + number + '\'' +
                ", liveid='" + liveid + '\'' +
                ", video_status='" + video_status + '\'' +
                ", riqi='" + riqi + '\'' +
                ", is_buy='" + is_buy + '\'' +
                ", timeLength='" + timeLength + '\'' +
                ", zhibotime='" + zhibotime + '\'' +
                ", zhiboendtime='" + zhiboendtime + '\'' +
                ", kouling='" + kouling + '\'' +
                ", zhibourl='" + zhibourl + '\'' +
                ", lubourl='" + lubourl + '\'' +
                ", EffectDateDesc='" + EffectDateDesc + '\'' +
                ", photo_url='" + photo_url + '\'' +
                ", Brief='" + Brief + '\'' +
                ", phaseName='" + phaseName + '\'' +
                ", ClassNo='" + ClassNo + '\'' +
                ", is_living='" + is_living + '\'' +
                ", TypeName='" + TypeName + '\'' +
                ", seq=" + seq +
                ", isHasJy='" + isHasJy + '\'' +
                ", videoType=" + videoType +
                ", ccUid='" + ccUid + '\'' +
                ", ccApi_key='" + ccApi_key + '\'' +
                ", ccCourses_id='" + ccCourses_id + '\'' +
                ", isTrial=" + isTrial +
                ", NetClassId='" + NetClassId + '\'' +
                ", netclass_pdf=" + netclass_pdf +
                '}';
    }
}