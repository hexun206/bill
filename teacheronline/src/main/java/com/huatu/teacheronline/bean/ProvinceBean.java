package com.huatu.teacheronline.bean;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;

/**
 * Created by ljzyuhenda on 16/1/15.
 */
@JsonObject
public class ProvinceBean implements Comparable<ProvinceBean>,Serializable{
    @JsonField
    public String Name;
    @JsonField
    public String Xzqh;
    @JsonField
    public String Header;

    @Override
    public int compareTo(ProvinceBean another) {
        return this.Header.compareTo(another.Header);
    }
}
