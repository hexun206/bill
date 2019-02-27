package com.huatu.teacheronline.exercise.bean;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ljzyuhenda on 16/1/26.
 * 章节树bean
 */
@JsonObject
public class CategoryBeanVt implements Serializable{
    @JsonField
    public String cid;//章节ID
    public void setName(String name) {
        this.name = name;
    }
    @JsonField
    public String name;//章节名称
    @JsonField
    public String pid;//父节点ID
    @JsonField
    public int count;//试题个数
    @JsonField
    public String versions;//版本[有下载包的前提下使用]
    @JsonField
    public List<String> qids;//试题ID
//    @JsonField
//    public int rolltype;//1面授；0普通


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "CategoryBean{" +
                ", name='" + name + '\'' +
                ", pid='" + pid + '\'' +
                ", count='" + count + '\'' +
                ", versions='" + versions + '\'' +
                ", qids=" + qids +
                "cid='" + cid + '\'' +
                '}';
    }
}
