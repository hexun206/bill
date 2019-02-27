package com.huatu.teacheronline.exercise.bean;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Arrays;

/**
 * Created by ljzyuhenda on 16/2/19.
 */
@JsonObject
public class PaperItem {
    @JsonField
    public String pid;// 试题ID
    @JsonField
    public String name;// 试卷名称
    @JsonField
    public String attribute;// 属性：真题 模拟题
    @JsonField
    public String versions;// 试卷版本
    @JsonField
    public String area;// 地区
    @JsonField
    public String year;// 年份
    @JsonField
    public float ptimelimit;//试卷时限(单位分钟)
    @JsonField
    public int count;//试题总数

    @JsonField
    public int rolltype;//种类 1 面授 0 普通
    @JsonField
    public String pattentions;//注意事项
    @JsonField
    public String otherType;//0 收费 1不收费
    @JsonField
    public String score;//最后一次得分 -1 未做
    @JsonField
    public String rank;//排名 -1未做
    @JsonField
    public int isactivitie;//活动状态 0：未开始，1正在进行，2结束
    @JsonField
    public int onlyOne;// 是否限制做题次数：0:不限制，1限制
    @JsonField
    public String starttime;// 活动开始时间(如果：onlyOne=0,则不传)
    @JsonField
    public String endtime;//  活动结束时间(如果：onlyOne=0,则不传)
    @JsonField
    public int userone;//  用户在活动期内是否还可做0可做，1次数超过了，2活动未开始，3活动结束不可做
    @JsonField
    public int ptotal;//  试卷总分
    @JsonField
    public int donumber;//  试卷可做次数
    @JsonField
    public int pconstraint;//  强制收卷：0否，1是
    @JsonField
    public int isnew;//  最新卷：0否，1是
    @JsonField
    public String[] qids;//  收藏和错题qids
    @JsonField
    public String open;//   试卷活动中是否开放解析-1不开放,0立即开放
    @JsonField
    public int openHour;//  当活动结束后不开放试卷,0开放,-1不开放
    @JsonField
    public String down;//  试卷是否下线 1在线,0下线(下线时请求接口获取试卷题包)

    @Override
    public String toString() {
        return "PaperItem{" +
                "pid='" + pid + '\'' +
                ", name='" + name + '\'' +
                ", attribute='" + attribute + '\'' +
                ", versions='" + versions + '\'' +
                ", area='" + area + '\'' +
                ", year='" + year + '\'' +
                ", ptimelimit=" + ptimelimit +
                ", count=" + count +
                ", rolltype=" + rolltype +
                ", pattentions='" + pattentions + '\'' +
                ", otherType='" + otherType + '\'' +
                ", score='" + score + '\'' +
                ", rank='" + rank + '\'' +
                ", isactivitie=" + isactivitie +
                ", onlyOne=" + onlyOne +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", userone=" + userone +
                ", ptotal=" + ptotal +
                ", donumber=" + donumber +
                ", pconstraint=" + pconstraint +
                ", isnew=" + isnew +
                ", qids=" + Arrays.toString(qids) +
                ", open='" + open + '\'' +
                ", openHour='" + openHour + '\'' +
                ", down='" + down + '\'' +
                '}';
    }
}
