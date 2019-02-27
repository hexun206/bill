package com.huatu.teacheronline.sensorsdata.event;

/**
 * Created by kinndann on 2018/12/21.
 * description:
 */
public class BrowseCourse {


    /**
     * 来源域名
     */
    private String source_host;
    /**
     * 来源地址
     */
    private String source_url;
    /**
     * 前向来源模块
     */
    private String forward_source_module;
    /**
     * 课程名称
     */
    private String course_title;
    /**
     * 课程ID
     */
    private String course_id;
    /**
     * 课程编号
     */
    private String course_number;
    /**
     * 授课方式
     */
    private String course_teaching_method;
    /**
     * 考试方式
     */
    private String course_examination_method;
    /**
     * 所属考试
     */
    private String course_examination;
    /**
     * 学段
     */
    private String course_study_section;
    /**
     * 课程科目
     */
    private String course_subject;
    /**
     * 关联协议
     */
    private String course_agreement;
    /**
     * 班次类型
     */
    private String course_class_type;
    /**
     * 适用地区
     */
    private String course_province;
    /**
     * 总课时
     */
    private Integer course_class_hour;
    /**
     * 返现小时数
     */
    private Integer course_cashback_hour;
    /**
     * 总课件数
     */
    private Integer course_courseware_quantity;
    /**
     * 课程授课老师
     */
    private String course_teacher;
    /**
     * 是否含视频点评
     */
    private Boolean course_video_review;
    /**
     * 视频点评次数
     */
    private Integer course_video_review_frequency;
    /**
     * 创建String
     */
    private String course_creation_date;
    /**
     * 直播开始时间
     */
    private String course_live_start_time;
    /**
     * 直播结束时间
     */
    private String course_live_end_time;
    /**
     * 有效期
     */
    private Integer course_validity_period;
    /**
     * 下线String
     */
    private String course_offline_date;
    /**
     * 课程原价
     */
    private Double course_price;
    /**
     * 试听性质
     */
    private String course_audition_nature;
    /**
     * 课件性质
     */
    private String course_courseware_nature;
    /**
     * 是否是一对一
     */
    private Boolean course_1to1;
    /**
     * 是否是协议班
     */
    private Boolean course_agreement_class;
    /**
     * 是否是系列课程
     */
    private Boolean course_series_class;
    /**
     * 优惠方式
     */
    private String course_preferential_method;
    /**
     * 优惠价格
     */
    private Double course_preferential_price;
    /**
     * 优惠数量
     */
    private Integer course_preferential_quantity;
    /**
     * 优惠开始时间
     */
    private String course_preferential_start_time;
    /**
     * 优惠结束时间
     */
    private String course_preferential_end_time;
    /**
     * 折扣率
     */
    private Double course_discount_rate;
    /**
     * 实际价格
     */
    private Double course_real_price;

    private BrowseCourse(Builder builder) {
        setSource_host(builder.source_host);
        setSource_url(builder.source_url);
        setForward_source_module(builder.forward_source_module);
        setCourse_title(builder.course_title);
        setCourse_id(builder.course_id);
        setCourse_number(builder.course_number);
        setCourse_teaching_method(builder.course_teaching_method);
        setCourse_examination_method(builder.course_examination_method);
        setCourse_examination(builder.course_examination);
        setCourse_study_section(builder.course_study_section);
        setCourse_subject(builder.course_subject);
        setCourse_agreement(builder.course_agreement);
        setCourse_class_type(builder.course_class_type);
        setCourse_province(builder.course_province);
        setCourse_class_hour(builder.course_class_hour);
        setCourse_cashback_hour(builder.course_cashback_hour);
        setCourse_courseware_quantity(builder.course_courseware_quantity);
        setCourse_teacher(builder.course_teacher);
        setCourse_video_review(builder.course_video_review);
        setCourse_video_review_frequency(builder.course_video_review_frequency);
        setCourse_creation_date(builder.course_creation_date);
        setCourse_live_start_time(builder.course_live_start_time);
        setCourse_live_end_time(builder.course_live_end_time);
        setCourse_validity_period(builder.course_validity_period);
        setCourse_offline_date(builder.course_offline_date);
        setCourse_price(builder.course_price);
        setCourse_audition_nature(builder.course_audition_nature);
        setCourse_courseware_nature(builder.course_courseware_nature);
        setCourse_1to1(builder.course_1to1);
        setCourse_agreement_class(builder.course_agreement_class);
        setCourse_series_class(builder.course_series_class);
        setCourse_preferential_method(builder.course_preferential_method);
        setCourse_preferential_price(builder.course_preferential_price);
        setCourse_preferential_quantity(builder.course_preferential_quantity);
        setCourse_preferential_start_time(builder.course_preferential_start_time);
        setCourse_preferential_end_time(builder.course_preferential_end_time);
        setCourse_discount_rate(builder.course_discount_rate);
        setCourse_real_price(builder.course_real_price);
    }


    public String getSource_host() {
        return source_host;
    }

    public void setSource_host(String source_host) {
        this.source_host = source_host;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public String getForward_source_module() {
        return forward_source_module;
    }

    public void setForward_source_module(String forward_source_module) {
        this.forward_source_module = forward_source_module;
    }

    public String getCourse_title() {
        return course_title;
    }

    public void setCourse_title(String course_title) {
        this.course_title = course_title;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getCourse_number() {
        return course_number;
    }

    public void setCourse_number(String course_number) {
        this.course_number = course_number;
    }

    public String getCourse_teaching_method() {
        return course_teaching_method;
    }

    public void setCourse_teaching_method(String course_teaching_method) {
        this.course_teaching_method = course_teaching_method;
    }

    public String getCourse_examination_method() {
        return course_examination_method;
    }

    public void setCourse_examination_method(String course_examination_method) {
        this.course_examination_method = course_examination_method;
    }

    public String getCourse_examination() {
        return course_examination;
    }

    public void setCourse_examination(String course_examination) {
        this.course_examination = course_examination;
    }

    public String getCourse_study_section() {
        return course_study_section;
    }

    public void setCourse_study_section(String course_study_section) {
        this.course_study_section = course_study_section;
    }

    public String getCourse_subject() {
        return course_subject;
    }

    public void setCourse_subject(String course_subject) {
        this.course_subject = course_subject;
    }

    public String getCourse_agreement() {
        return course_agreement;
    }

    public void setCourse_agreement(String course_agreement) {
        this.course_agreement = course_agreement;
    }

    public String getCourse_class_type() {
        return course_class_type;
    }

    public void setCourse_class_type(String course_class_type) {
        this.course_class_type = course_class_type;
    }

    public String getCourse_province() {
        return course_province;
    }

    public void setCourse_province(String course_province) {
        this.course_province = course_province;
    }

    public Integer getCourse_class_hour() {
        return course_class_hour;
    }

    public void setCourse_class_hour(Integer course_class_hour) {
        this.course_class_hour = course_class_hour;
    }

    public Integer getCourse_cashback_hour() {
        return course_cashback_hour;
    }

    public void setCourse_cashback_hour(Integer course_cashback_hour) {
        this.course_cashback_hour = course_cashback_hour;
    }

    public Integer getCourse_courseware_quantity() {
        return course_courseware_quantity;
    }

    public void setCourse_courseware_quantity(Integer course_courseware_quantity) {
        this.course_courseware_quantity = course_courseware_quantity;
    }

    public String getCourse_teacher() {
        return course_teacher;
    }

    public void setCourse_teacher(String course_teacher) {
        this.course_teacher = course_teacher;
    }

    public Boolean getCourse_video_review() {
        return course_video_review;
    }

    public void setCourse_video_review(Boolean course_video_review) {
        this.course_video_review = course_video_review;
    }

    public Integer getCourse_video_review_frequency() {
        return course_video_review_frequency;
    }

    public void setCourse_video_review_frequency(Integer course_video_review_frequency) {
        this.course_video_review_frequency = course_video_review_frequency;
    }

    public String getCourse_creation_date() {
        return course_creation_date;
    }

    public void setCourse_creation_date(String course_creation_date) {
        this.course_creation_date = course_creation_date;
    }

    public String getCourse_live_start_time() {
        return course_live_start_time;
    }

    public void setCourse_live_start_time(String course_live_start_time) {
        this.course_live_start_time = course_live_start_time;
    }

    public String getCourse_live_end_time() {
        return course_live_end_time;
    }

    public void setCourse_live_end_time(String course_live_end_time) {
        this.course_live_end_time = course_live_end_time;
    }

    public Integer getCourse_validity_period() {
        return course_validity_period;
    }

    public void setCourse_validity_period(Integer course_validity_period) {
        this.course_validity_period = course_validity_period;
    }

    public String getCourse_offline_date() {
        return course_offline_date;
    }

    public void setCourse_offline_date(String course_offline_date) {
        this.course_offline_date = course_offline_date;
    }

    public Double getCourse_price() {
        return course_price;
    }

    public void setCourse_price(Double course_price) {
        this.course_price = course_price;
    }

    public String getCourse_audition_nature() {
        return course_audition_nature;
    }

    public void setCourse_audition_nature(String course_audition_nature) {
        this.course_audition_nature = course_audition_nature;
    }

    public String getCourse_courseware_nature() {
        return course_courseware_nature;
    }

    public void setCourse_courseware_nature(String course_courseware_nature) {
        this.course_courseware_nature = course_courseware_nature;
    }

    public Boolean getCourse_1to1() {
        return course_1to1;
    }

    public void setCourse_1to1(Boolean course_1to1) {
        this.course_1to1 = course_1to1;
    }

    public Boolean getCourse_agreement_class() {
        return course_agreement_class;
    }

    public void setCourse_agreement_class(Boolean course_agreement_class) {
        this.course_agreement_class = course_agreement_class;
    }

    public Boolean getCourse_series_class() {
        return course_series_class;
    }

    public void setCourse_series_class(Boolean course_series_class) {
        this.course_series_class = course_series_class;
    }

    public String getCourse_preferential_method() {
        return course_preferential_method;
    }

    public void setCourse_preferential_method(String course_preferential_method) {
        this.course_preferential_method = course_preferential_method;
    }

    public Double getCourse_preferential_price() {
        return course_preferential_price;
    }

    public void setCourse_preferential_price(Double course_preferential_price) {
        this.course_preferential_price = course_preferential_price;
    }

    public Integer getCourse_preferential_quantity() {
        return course_preferential_quantity;
    }

    public void setCourse_preferential_quantity(Integer course_preferential_quantity) {
        this.course_preferential_quantity = course_preferential_quantity;
    }

    public String getCourse_preferential_start_time() {
        return course_preferential_start_time;
    }

    public void setCourse_preferential_start_time(String course_preferential_start_time) {
        this.course_preferential_start_time = course_preferential_start_time;
    }

    public String getCourse_preferential_end_time() {
        return course_preferential_end_time;
    }

    public void setCourse_preferential_end_time(String course_preferential_end_time) {
        this.course_preferential_end_time = course_preferential_end_time;
    }

    public Double getCourse_discount_rate() {
        return course_discount_rate;
    }

    public void setCourse_discount_rate(Double course_discount_rate) {
        this.course_discount_rate = course_discount_rate;
    }

    public Double getCourse_real_price() {
        return course_real_price;
    }

    public void setCourse_real_price(Double course_real_price) {
        this.course_real_price = course_real_price;
    }


    public static final class Builder {
        private String source_host;
        private String source_url;
        private String forward_source_module;
        private String course_title;
        private String course_id;
        private String course_number;
        private String course_teaching_method;
        private String course_examination_method;
        private String course_examination;
        private String course_study_section;
        private String course_subject;
        private String course_agreement;
        private String course_class_type;
        private String course_province;
        private Integer course_class_hour;
        private Integer course_cashback_hour;
        private Integer course_courseware_quantity;
        private String course_teacher;
        private Boolean course_video_review;
        private Integer course_video_review_frequency;
        private String course_creation_date;
        private String course_live_start_time;
        private String course_live_end_time;
        private Integer course_validity_period;
        private String course_offline_date;
        private Double course_price;
        private String course_audition_nature;
        private String course_courseware_nature;
        private Boolean course_1to1;
        private Boolean course_agreement_class;
        private Boolean course_series_class;
        private String course_preferential_method;
        private Double course_preferential_price;
        private Integer course_preferential_quantity;
        private String course_preferential_start_time;
        private String course_preferential_end_time;
        private Double course_discount_rate;
        private Double course_real_price;

        public Builder() {
        }

        public Builder source_host(String source_host) {
            this.source_host = source_host;
            return this;
        }

        public Builder source_url(String source_url) {
            this.source_url = source_url;
            return this;
        }

        public Builder forward_source_module(String forward_source_module) {
            this.forward_source_module = forward_source_module;
            return this;
        }

        public Builder course_title(String course_title) {
            this.course_title = course_title;
            return this;
        }

        public Builder course_id(String course_id) {
            this.course_id = course_id;
            return this;
        }

        public Builder course_number(String course_number) {
            this.course_number = course_number;
            return this;
        }

        public Builder course_teaching_method(String course_teaching_method) {
            this.course_teaching_method = course_teaching_method;
            return this;
        }

        public Builder course_examination_method(String course_examination_method) {
            this.course_examination_method = course_examination_method;
            return this;
        }

        public Builder course_examination(String course_examination) {
            this.course_examination = course_examination;
            return this;
        }

        public Builder course_study_section(String course_study_section) {
            this.course_study_section = course_study_section;
            return this;
        }

        public Builder course_subject(String course_subject) {
            this.course_subject = course_subject;
            return this;
        }

        public Builder course_agreement(String course_agreement) {
            this.course_agreement = course_agreement;
            return this;
        }

        public Builder course_class_type(String course_class_type) {
            this.course_class_type = course_class_type;
            return this;
        }

        public Builder course_province(String course_province) {
            this.course_province = course_province;
            return this;
        }

        public Builder course_class_hour(Integer course_class_hour) {
            this.course_class_hour = course_class_hour;
            return this;
        }

        public Builder course_cashback_hour(Integer course_cashback_hour) {
            this.course_cashback_hour = course_cashback_hour;
            return this;
        }

        public Builder course_courseware_quantity(Integer course_courseware_quantity) {
            this.course_courseware_quantity = course_courseware_quantity;
            return this;
        }

        public Builder course_teacher(String course_teacher) {
            this.course_teacher = course_teacher;
            return this;
        }

        public Builder course_video_review(Boolean course_video_review) {
            this.course_video_review = course_video_review;
            return this;
        }

        public Builder course_video_review_frequency(Integer course_video_review_frequency) {
            this.course_video_review_frequency = course_video_review_frequency;
            return this;
        }

        public Builder course_creation_date(String course_creation_date) {
            this.course_creation_date = course_creation_date;
            return this;
        }

        public Builder course_live_start_time(String course_live_start_time) {
            this.course_live_start_time = course_live_start_time;
            return this;
        }

        public Builder course_live_end_time(String course_live_end_time) {
            this.course_live_end_time = course_live_end_time;
            return this;
        }

        public Builder course_validity_period(Integer course_validity_period) {
            this.course_validity_period = course_validity_period;
            return this;
        }

        public Builder course_offline_date(String course_offline_date) {
            this.course_offline_date = course_offline_date;
            return this;
        }

        public Builder course_price(Double course_price) {
            this.course_price = course_price;
            return this;
        }

        public Builder course_audition_nature(String course_audition_nature) {
            this.course_audition_nature = course_audition_nature;
            return this;
        }

        public Builder course_courseware_nature(String course_courseware_nature) {
            this.course_courseware_nature = course_courseware_nature;
            return this;
        }

        public Builder course_1to1(Boolean course_1to1) {
            this.course_1to1 = course_1to1;
            return this;
        }

        public Builder course_agreement_class(Boolean course_agreement_class) {
            this.course_agreement_class = course_agreement_class;
            return this;
        }

        public Builder course_series_class(Boolean course_series_class) {
            this.course_series_class = course_series_class;
            return this;
        }

        public Builder course_preferential_method(String course_preferential_method) {
            this.course_preferential_method = course_preferential_method;
            return this;
        }

        public Builder course_preferential_price(Double course_preferential_price) {
            this.course_preferential_price = course_preferential_price;
            return this;
        }

        public Builder course_preferential_quantity(Integer course_preferential_quantity) {
            this.course_preferential_quantity = course_preferential_quantity;
            return this;
        }

        public Builder course_preferential_start_time(String course_preferential_start_time) {
            this.course_preferential_start_time = course_preferential_start_time;
            return this;
        }

        public Builder course_preferential_end_time(String course_preferential_end_time) {
            this.course_preferential_end_time = course_preferential_end_time;
            return this;
        }

        public Builder course_discount_rate(Double course_discount_rate) {
            this.course_discount_rate = course_discount_rate;
            return this;
        }

        public Builder course_real_price(Double course_real_price) {
            this.course_real_price = course_real_price;
            return this;
        }

        public BrowseCourse build() {
            return new BrowseCourse(this);
        }
    }
}
