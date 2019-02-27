package com.huatu.teacheronline.bean;

import java.util.Objects;

/**
 * Created by kinndann on 2018/9/3.
 * description:清晰度
 */
public class DefinitionBean {

    private String description;
    private String type;
    private int value;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefinitionBean that = (DefinitionBean) o;
        return value == that.value &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, value);
    }
}
