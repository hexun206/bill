package com.huatu.teacheronline.direct.bean;
import com.greendao.DirectBean;

/**
 * Created by kinndann on 2018/9/11.
 * description:
 */
public class PlayByIndexEvent {

    private int index;

    private DirectBean info;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public DirectBean getInfo() {
        return info;
    }

    public void setInfo(DirectBean info) {
        this.info = info;
    }
}
