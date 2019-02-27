package com.huatu.teacheronline.bean;

import com.google.gson.annotations.Expose;

/**
 * Created by kinndann on 2018/10/1.
 * description:由淘宝接口获取的ip信息
 */
public class IPInfo {

    /**
     * code : 0
     * data : {"ip":"120.41.76.92","country":"中国","area":"","region":"福建","city":"厦门","county":"XX","isp":"电信","country_id":"CN","area_id":"","region_id":"350000","city_id":"350200","county_id":"xx","isp_id":"100017"}
     */
    @Expose
    private int code;
    @Expose
    private DataBean data;

    public boolean isSuccess() {
        return code == 0;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * ip : 120.41.76.92
         * country : 中国
         * area :
         * region : 福建
         * city : 厦门
         * county : XX
         * isp : 电信
         * country_id : CN
         * area_id :
         * region_id : 350000
         * city_id : 350200
         * county_id : xx
         * isp_id : 100017
         */
        @Expose
        private String ip;
        @Expose
        private String country;
        private String area;
        @Expose
        private String region;
        @Expose
        private String city;
        @Expose
        private String county;
        private String isp;
        private String country_id;
        private String area_id;
        private String region_id;
        /**
         * 市级邮政编码
         */
        @Expose
        private String city_id;
        private String county_id;
        private String isp_id;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCounty() {
            return county;
        }

        public void setCounty(String county) {
            this.county = county;
        }

        public String getIsp() {
            return isp;
        }

        public void setIsp(String isp) {
            this.isp = isp;
        }

        public String getCountry_id() {
            return country_id;
        }

        public void setCountry_id(String country_id) {
            this.country_id = country_id;
        }

        public String getArea_id() {
            return area_id;
        }

        public void setArea_id(String area_id) {
            this.area_id = area_id;
        }

        public String getRegion_id() {
            return region_id;
        }

        public void setRegion_id(String region_id) {
            this.region_id = region_id;
        }

        public String getCity_id() {
            return city_id;
        }

        public void setCity_id(String city_id) {
            this.city_id = city_id;
        }

        public String getCounty_id() {
            return county_id;
        }

        public void setCounty_id(String county_id) {
            this.county_id = county_id;
        }

        public String getIsp_id() {
            return isp_id;
        }

        public void setIsp_id(String isp_id) {
            this.isp_id = isp_id;
        }
    }
}
