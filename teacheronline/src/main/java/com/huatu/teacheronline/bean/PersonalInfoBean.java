package com.huatu.teacheronline.bean;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.huatu.teacheronline.personal.bean.HourEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 账户信息
 * Created by ply on 2016/1/28.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class  PersonalInfoBean implements Serializable {
    private String id;//用户id
    private String account;// 用户名，登录时填的用户名
    private String password;//MD5加密的密码
    private String nickname;//昵称
    private String mobile;//电话
    private String face;//头像
    private String sex;//性别
    private String gold;//金币
    private String city;//城市，保存行政区划编码
    private String province;//省份，保存行政区划编码
    private String sec_id;//学段，存学段名字
    private String sub_id;//科目，保存科目id
    private String sub_ids;//多科目，保存科目id，科目名字
    private String type_id;//考试类型，保存类型的key值
    private String num;//连续签到几天
    private String msg_status;//是否有未读消息
    private String lession_status;//是否有未读课程消息

    private ArrayList<AdBean> advertising;//广告
    private String birthday;//生日
    private String first_pass;//是否修改过密码 1修改 0没修改
    private String accessToken;//TOKEN
    private String userPoint;//用户积分
    private List<ScrollBean> scroll_bar;//跑马灯广告
    @JsonField
    private PopupAdsBean popup_ads;//首页弹窗广告
    private String is_feedback;//判断意见反馈中是否有反馈消息
    private List<String> baike;//首页H5链接
    private List<PraiseBean> praise;//学员好评
    private List<RecomBean> recommend;//推荐课程
    private String face_courses;//1面授课学员  2非面授
    private String title;//标题
    private HourEntity hour;
    private String im_pic;//首页图片
    private String provinceName;//选择科目的省份
    private String cityName;//选择科目的市
    private int facesum;//首页消息未读数


    public int getfacesum() {
        return facesum;
    }

    public void setfacesum(int facesum) {
        this.facesum = facesum;
    }

    public HourEntity gethour() {
        return hour;
    }

    public void sethour(HourEntity hour) {
        this.hour = hour;
    }

    public String getim_pic() {
        return im_pic;
    }

    public void setim_pic(String im_pic) {
        this.im_pic = im_pic;
    }

    public String gettitle() {
        return title;
    }

    public void settitle(String title) {
        this.title = title;
    }


    public String getface_courses() {
        return face_courses;
    }

    public void setface_courses(String face_courses) {
        this.face_courses = face_courses;
    }


    public List<RecomBean> getrecommend() {
        return recommend;
    }
    public void setrecommend(List<RecomBean> recommend) {
        this.recommend = recommend;
    }

    public List<String> getbaike() {
        return baike;
    }
    public void setbaike(List<String> baike) {
        this.baike = baike;
    }

    public List<PraiseBean> getpraise() {
        return praise;
    }
    public void setpraise(List<PraiseBean> praise) {
        this.praise = praise;
    }


    public String getis_feedback() {
        return is_feedback;
    }
    public void setis_feedback(String is_feedback) {
        this.is_feedback = is_feedback;
    }

    public String getlession_status() {
        return lession_status;
    }
    public void setlession_status(String lession_status) {
        this.lession_status = lession_status;
    }

    public void setPopup_ads(PopupAdsBean popup_ads) {
        this.popup_ads = popup_ads;
    }

    public PopupAdsBean getPopup_ads() {
        return popup_ads;

    }

    public List<ScrollBean> getscroll_bar() {
        return scroll_bar;
    }

    public void setscroll_bar(List<ScrollBean> scroll_bar) {
        this.scroll_bar = scroll_bar;
    }
    public String getaccessToken() {
        return accessToken;
    }

    public void setaccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getGold() {
        return gold;
    }

    public void setGold(String gold) {
        this.gold = gold;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getSec_id() {
        return sec_id;
    }

    public void setSec_id(String sec_id) {
        this.sec_id = sec_id;
    }

    public String getSub_id() {
        return sub_id;
    }

    public void setSub_id(String sub_id) {
        this.sub_id = sub_id;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public ArrayList<AdBean> getAdvertising() {
        return advertising;
    }

    public void setAdvertising(ArrayList<AdBean> advertising) {
        this.advertising = advertising;
    }

    public String getMsg_status() {
        return msg_status;
    }
    public void setMsg_status(String msg_status) {
        this.msg_status = msg_status;
    }

    public String getSub_ids() {
        return sub_ids;
    }

    public void setSub_ids(String sub_ids) {
        this.sub_ids = sub_ids;
    }

    public String getFirst_pass() {
        return first_pass;
    }

    public void setFirst_pass(String first_pass) {
        this.first_pass = first_pass;
    }

    public String getUserPoint() {
        return userPoint;
    }

    public void setUserPoint(String userPoint) {
        this.userPoint = userPoint;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    @Override
    public String toString() {
        return "PersonalInfoBean{" +
                "id='" + id + '\'' +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", face='" + face + '\'' +
                ", sex='" + sex + '\'' +
                ", gold='" + gold + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", sec_id='" + sec_id + '\'' +
                ", sub_id='" + sub_id + '\'' +
                ", sub_ids='" + sub_ids + '\'' +
                ", type_id='" + type_id + '\'' +
                ", num='" + num + '\'' +
                ", msg_status='" + msg_status + '\'' +
                ", lession_status='" + lession_status + '\'' +
                ", advertising=" + advertising +
                ", birthday='" + birthday + '\'' +
                ", first_pass='" + first_pass + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", userPoint='" + userPoint + '\'' +
                ", scroll_bar='" + scroll_bar + '\'' +
                ", popup_ads='" + popup_ads + '\''+
                 ", baike='" + baike + '\''+
                 ", praise='" + praise + '\''+
                 ", recommend='" + recommend + '\''+
                ", face_courses='" + face_courses + '\''+
                ", title='" + title + '\''+
                ", hour='" + hour + '\''+
                ", im_pic='" + im_pic + '\''+
                '}';
    }
}
