package com.huatu.teacheronline.sensorsdata.event;

/**
 * Created by kinndann on 2018/12/19.
 * description:观看课程开始/结束
 */
public class WatchCourse {

    /**
     * 课程ID
     */
    private String course_id;
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
     * 班次类型
     */
    private String course_class_type;
    /**
     * 适用地区
     */
    private String course_province;
    /**
     * 课件ID
     */
    private Long course_courseware_id;
    /**
     * 课件标题
     */
    private String course_courseware_title;
    /**
     * 直播时间
     */
    private String course_courseware_live_start_time;
    /**
     * 结束时间
     */
    private String course_courseware_live_end_time;
    /**
     * 是否直播
     */
    private Boolean course_courseware_live;
    /**
     * 百家云ID
     */
    private String course_courseware_BJY_id;
    /**
     * 课时
     */
    private Integer course_courseware_hour;
    /**
     * 课件授课老师
     */
    private String course_courseware_teacher;
    /**
     * 排序
     */
    private Integer course_courseware_sort;

    /**
     * 时长
     */
    private Long course_courseware_duration;

    private WatchCourse(Builder builder) {
        setCourse_id(builder.course_id);
        setCourse_teaching_method(builder.course_teaching_method);
        setCourse_examination_method(builder.course_examination_method);
        setCourse_examination(builder.course_examination);
        setCourse_study_section(builder.course_study_section);
        setCourse_subject(builder.course_subject);
        setCourse_class_type(builder.course_class_type);
        setCourse_province(builder.course_province);
        setCourse_courseware_id(builder.course_courseware_id);
        setCourse_courseware_title(builder.course_courseware_title);
        setCourse_courseware_live_start_time(builder.course_courseware_live_start_time);
        setCourse_courseware_live_end_time(builder.course_courseware_live_end_time);
        setCourse_courseware_live(builder.course_courseware_live);
        setCourse_courseware_BJY_id(builder.course_courseware_BJY_id);
        setCourse_courseware_hour(builder.course_courseware_hour);
        setCourse_courseware_teacher(builder.course_courseware_teacher);
        setCourse_courseware_sort(builder.course_courseware_sort);
        setCourse_courseware_duration(builder.course_courseware_duration);
    }


    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
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

    public Long getCourse_courseware_id() {
        return course_courseware_id;
    }

    public void setCourse_courseware_id(Long course_courseware_id) {
        this.course_courseware_id = course_courseware_id;
    }

    public String getCourse_courseware_title() {
        return course_courseware_title;
    }

    public void setCourse_courseware_title(String course_courseware_title) {
        this.course_courseware_title = course_courseware_title;
    }

    public String getCourse_courseware_live_start_time() {
        return course_courseware_live_start_time;
    }

    public void setCourse_courseware_live_start_time(String course_courseware_live_start_time) {
        this.course_courseware_live_start_time = course_courseware_live_start_time;
    }

    public String getCourse_courseware_live_end_time() {
        return course_courseware_live_end_time;
    }

    public void setCourse_courseware_live_end_time(String course_courseware_live_end_time) {
        this.course_courseware_live_end_time = course_courseware_live_end_time;
    }

    public Boolean getCourse_courseware_live() {
        return course_courseware_live;
    }

    public void setCourse_courseware_live(Boolean course_courseware_live) {
        this.course_courseware_live = course_courseware_live;
    }

    public String getCourse_courseware_BJY_id() {
        return course_courseware_BJY_id;
    }

    public void setCourse_courseware_BJY_id(String course_courseware_BJY_id) {
        this.course_courseware_BJY_id = course_courseware_BJY_id;
    }

    public Integer getCourse_courseware_hour() {
        return course_courseware_hour;
    }

    public void setCourse_courseware_hour(Integer course_courseware_hour) {
        this.course_courseware_hour = course_courseware_hour;
    }

    public String getCourse_courseware_teacher() {
        return course_courseware_teacher;
    }

    public void setCourse_courseware_teacher(String course_courseware_teacher) {
        this.course_courseware_teacher = course_courseware_teacher;
    }

    public Integer getCourse_courseware_sort() {
        return course_courseware_sort;
    }

    public void setCourse_courseware_sort(Integer course_courseware_sort) {
        this.course_courseware_sort = course_courseware_sort;
    }

    public Long getCourse_courseware_duration() {
        return course_courseware_duration;
    }

    public void setCourse_courseware_duration(Long course_courseware_duration) {
        this.course_courseware_duration = course_courseware_duration;
    }


    public static final class Builder {
        private String course_id;
        private String course_teaching_method;
        private String course_examination_method;
        private String course_examination;
        private String course_study_section;
        private String course_subject;
        private String course_class_type;
        private String course_province;
        private Long course_courseware_id;
        private String course_courseware_title;
        private String course_courseware_live_start_time;
        private String course_courseware_live_end_time;
        private Boolean course_courseware_live;
        private String course_courseware_BJY_id;
        private Integer course_courseware_hour;
        private String course_courseware_teacher;
        private Integer course_courseware_sort;
        private Long course_courseware_duration;

        public Builder() {
        }

        public Builder course_id(String course_id) {
            this.course_id = course_id;
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

        public Builder course_class_type(String course_class_type) {
            this.course_class_type = course_class_type;
            return this;
        }

        public Builder course_province(String course_province) {
            this.course_province = course_province;
            return this;
        }

        public Builder course_courseware_id(Long course_courseware_id) {
            this.course_courseware_id = course_courseware_id;
            return this;
        }

        public Builder course_courseware_title(String course_courseware_title) {
            this.course_courseware_title = course_courseware_title;
            return this;
        }

        public Builder course_courseware_live_start_time(String course_courseware_live_start_time) {
            this.course_courseware_live_start_time = course_courseware_live_start_time;
            return this;
        }

        public Builder course_courseware_live_end_time(String course_courseware_live_end_time) {
            this.course_courseware_live_end_time = course_courseware_live_end_time;
            return this;
        }

        public Builder course_courseware_live(Boolean course_courseware_live) {
            this.course_courseware_live = course_courseware_live;
            return this;
        }

        public Builder course_courseware_BJY_id(String course_courseware_BJY_id) {
            this.course_courseware_BJY_id = course_courseware_BJY_id;
            return this;
        }

        public Builder course_courseware_hour(Integer course_courseware_hour) {
            this.course_courseware_hour = course_courseware_hour;
            return this;
        }

        public Builder course_courseware_teacher(String course_courseware_teacher) {
            this.course_courseware_teacher = course_courseware_teacher;
            return this;
        }

        public Builder course_courseware_sort(Integer course_courseware_sort) {
            this.course_courseware_sort = course_courseware_sort;
            return this;
        }

        public Builder course_courseware_duration(Long course_courseware_duration) {
            this.course_courseware_duration = course_courseware_duration;
            return this;
        }

        public WatchCourse build() {
            return new WatchCourse(this);
        }
    }
}
