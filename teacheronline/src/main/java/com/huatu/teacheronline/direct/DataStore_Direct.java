package com.huatu.teacheronline.direct;


import com.greendao.DirectBean;
import com.huatu.teacheronline.direct.bean.PdfBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljyu on 2017/02/14.
 * 静态类，存储
 * 退出时需要置空
 */
public class DataStore_Direct {
    // 我的直播课单个课程信息
    public static ArrayList<DirectBean> directDatailList;
    //单个课程pdf列表信息
    public static ArrayList<PdfBean> directPdfDatailList;
    //课程常见问题
    public static String Common_problem;

    public static void resetDatas() {
        DataStore_Direct.directDatailList = null;
        DataStore_Direct.directPdfDatailList = null;
        DataStore_Direct.Common_problem = null;
    }
}
