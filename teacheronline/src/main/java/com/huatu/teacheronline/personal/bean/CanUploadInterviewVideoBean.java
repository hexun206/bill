package com.huatu.teacheronline.personal.bean;

import java.util.List;

/**
 * Created by kinndann on 2018/10/31.
 * description:
 */
public class CanUploadInterviewVideoBean {


    /**
     * code : success
     * message :
     * data : [{"netclassid":"48651","netclassname":"2017年北京教师招聘笔试 美术全程通关班","count_essay":1,"isnumbers":"0","iscomment":1},{"netclassid":"48368","netclassname":"2017年教师招聘笔试 美术全程通关班","count_essay":3,"isnumbers":"0","iscomment":1}]
     */

    private String code;
    private String message;
    private List<ClasslistBean> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ClasslistBean> getData() {
        return data;
    }

    public void setData(List<ClasslistBean> data) {
        this.data = data;
    }

    public boolean success() {
        return "1".equals(code);

    }


    public static class ClasslistBean {
        /**
         * netclassid : 48651
         * netclassname : 2017年北京教师招聘笔试 美术全程通关班
         * count_essay : 1
         * isnumbers : 0
         * iscomment : 1
         */

        private String netclassid;
        private String netclassname;
        private int count_essay;
        private String isnumbers;
        private int iscomment;
        private int firstread;


        public boolean isOutOfDate() {
            int commited = Integer.parseInt(isnumbers);

            if (commited < count_essay && iscomment == 0) {
                return true;
            }

            return false;
        }

        public String getState() {

            int commited = Integer.parseInt(isnumbers);
            return "点评次数：" + commited + "/" + count_essay + "次";

        }


        /**
         * 是否存在没有首次阅读的订单
         *
         * @return
         */
        public boolean hasUnReadInfo() {
            return firstread == 1;
        }


        public String getNetclassid() {
            return netclassid;
        }

        public void setNetclassid(String netclassid) {
            this.netclassid = netclassid;
        }

        public String getNetclassname() {
            return netclassname;
        }

        public void setNetclassname(String netclassname) {
            this.netclassname = netclassname;
        }

        public int getCount_essay() {
            return count_essay;
        }

        public void setCount_essay(int count_essay) {
            this.count_essay = count_essay;
        }

        public String getIsnumbers() {
            return isnumbers;
        }

        public void setIsnumbers(String isnumbers) {
            this.isnumbers = isnumbers;
        }

        public int getIscomment() {
            return iscomment;
        }

        public void setIscomment(int iscomment) {

            this.iscomment = iscomment;
        }

        public int getFirstread() {
            return firstread;
        }

        public void setFirstread(int firstread) {
            this.firstread = firstread;
        }
    }
}
