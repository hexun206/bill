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
public class CategoryBean implements Serializable{
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
    public String explain;//说明（公务员叶子节点）        ****预留
    @JsonField
    public String count;//试题个数
    @JsonField
    public Integer ordernum;//排序字段
    @JsonField
    public String isdownload;//是否有下载包     0 存在  1 不存在
    @JsonField
    public String versions;//版本[有下载包的前提下使用]
    @JsonField
    public List<String> qids;//试题ID
    @JsonField
    public List<CategoryBean> children; //子节点
    @JsonField
    public List<String> areas;//地域id
    @JsonField
    public List<String> stages;
    @JsonField
    public String clevels;//层级

    public boolean isCheck;//多科目选择是否选中
    @JsonField
    public List<String> updateqids;//更新的试题IDs
    @JsonField
    public List<String> deleteqids;//删除的试题IDs


    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<CategoryBean> getChildren() {
        return children;
    }

    public void setChildren(List<CategoryBean> children) {
        this.children = children;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    @Override
    public String toString() {
        return "CategoryBean{" +
                "cid='" + cid + '\'' +
                ", name='" + name + '\'' +
                ", pid='" + pid + '\'' +
                ", explain='" + explain + '\'' +
                ", count='" + count + '\'' +
                ", ordernum=" + ordernum +
                ", isdownload='" + isdownload + '\'' +
                ", versions='" + versions + '\'' +
                ", qids=" + qids +
                ", children=" + children +
                ", areas=" + areas +
                ", stages=" + stages +
                ", clevels='" + clevels + '\'' +
                ", isCheck=" + isCheck +
                ", updateqids=" + updateqids +
                ", deleteqids=" + deleteqids +
                '}';
    }
}
