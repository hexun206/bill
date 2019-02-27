package com.huatu.teacheronline.personal.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kinndann on 2018/10/31.
 * description:
 */
public class UploadParamBeanCopy implements Serializable {


    /**
     * code : success
     * message :
     * data : {"phases":[{"id":"13","phasename":"幼儿"},{"id":"14","phasename":"小学"},{"id":"15","phasename":"初中"},{"id":"16","phasename":"高中"}],"subjects":{"14":[{"id":"10","subjectname":"语文","subjectid":"1","phaseid":"14"},{"id":"17","subjectname":"数学","subjectid":"2","phaseid":"14"},{"id":"18","subjectname":"英语","subjectid":"3","phaseid":"14"},{"id":"19","subjectname":"音乐","subjectid":"4","phaseid":"14"},{"id":"20","subjectname":"体育","subjectid":"5","phaseid":"14"},{"id":"21","subjectname":"美术","subjectid":"6","phaseid":"14"},{"id":"28","subjectname":"信息技术","subjectid":"13","phaseid":"14"}],"15":[{"id":"10","subjectname":"语文","subjectid":"1","phaseid":"15"},{"id":"17","subjectname":"数学","subjectid":"2","phaseid":"15"},{"id":"18","subjectname":"英语","subjectid":"3","phaseid":"15"},{"id":"19","subjectname":"音乐","subjectid":"4","phaseid":"15"},{"id":"20","subjectname":"体育","subjectid":"5","phaseid":"15"},{"id":"21","subjectname":"美术","subjectid":"6","phaseid":"15"},{"id":"22","subjectname":"物理","subjectid":"7","phaseid":"15"},{"id":"23","subjectname":"化学","subjectid":"8","phaseid":"15"},{"id":"24","subjectname":"生物","subjectid":"9","phaseid":"15"},{"id":"25","subjectname":"政治","subjectid":"10","phaseid":"15"},{"id":"26","subjectname":"历史","subjectid":"11","phaseid":"15"},{"id":"27","subjectname":"地理","subjectid":"12","phaseid":"15"},{"id":"28","subjectname":"信息技术","subjectid":"13","phaseid":"15"}],"16":[{"id":"10","subjectname":"语文","subjectid":"1","phaseid":"16"},{"id":"17","subjectname":"数学","subjectid":"2","phaseid":"16"},{"id":"18","subjectname":"英语","subjectid":"3","phaseid":"16"},{"id":"19","subjectname":"音乐","subjectid":"4","phaseid":"16"},{"id":"20","subjectname":"体育","subjectid":"5","phaseid":"16"},{"id":"21","subjectname":"美术","subjectid":"6","phaseid":"16"},{"id":"22","subjectname":"物理","subjectid":"7","phaseid":"16"},{"id":"23","subjectname":"化学","subjectid":"8","phaseid":"16"},{"id":"24","subjectname":"生物","subjectid":"9","phaseid":"16"},{"id":"25","subjectname":"政治","subjectid":"10","phaseid":"16"},{"id":"26","subjectname":"历史","subjectid":"11","phaseid":"16"},{"id":"27","subjectname":"地理","subjectid":"12","phaseid":"16"},{"id":"28","subjectname":"信息技术","subjectid":"13","phaseid":"16"}],"13":[{"id":"29","subjectname":"幼教","subjectid":"14","phaseid":"13"}]},"versions":{"10":[{"id":"16","versionname":"部编版（人教版）","versionid":"1","subjectid":"10"},{"id":"17","versionname":"北师大版","versionid":"2","subjectid":"10"},{"id":"18","versionname":"苏教版","versionid":"3","subjectid":"10"},{"id":"19","versionname":"语文A版","versionid":"4","subjectid":"10"},{"id":"20","versionname":"语文S版","versionid":"5","subjectid":"10"}],"17":[{"id":"17","versionname":"北师大版","versionid":"2","subjectid":"17"},{"id":"18","versionname":"苏教版","versionid":"3","subjectid":"17"},{"id":"21","versionname":"人教版","versionid":"6","subjectid":"17"},{"id":"23","versionname":"华东师大","versionid":"7","subjectid":"17"},{"id":"24","versionname":"青岛版","versionid":"8","subjectid":"17"},{"id":"25","versionname":"长春版","versionid":"9","subjectid":"17"}],"18":[{"id":"17","versionname":"北师大版","versionid":"2","subjectid":"18"},{"id":"26","versionname":"人教版（pep）","versionid":"10","subjectid":"18"},{"id":"27","versionname":"外研社版","versionid":"11","subjectid":"18"},{"id":"28","versionname":"牛津版","versionid":"12","subjectid":"18"},{"id":"29","versionname":"苏教译林版","versionid":"13","subjectid":"18"},{"id":"30","versionname":"冀教版","versionid":"14","subjectid":"18"}],"19":[{"id":"18","versionname":"苏教版","versionid":"3","subjectid":"19"},{"id":"21","versionname":"人教版","versionid":"6","subjectid":"19"},{"id":"31","versionname":"人音版","versionid":"15","subjectid":"19"},{"id":"32","versionname":"花城版","versionid":"16","subjectid":"19"},{"id":"33","versionname":"湘教版","versionid":"17","subjectid":"19"}],"20":[{"id":"21","versionname":"人教版","versionid":"6","subjectid":"20"},{"id":"40","versionname":"其他版","versionid":"24","subjectid":"20"}],"21":[{"id":"21","versionname":"人教版","versionid":"6","subjectid":"21"},{"id":"34","versionname":"人美版","versionid":"18","subjectid":"21"},{"id":"35","versionname":"湘美版","versionid":"19","subjectid":"21"},{"id":"36","versionname":"浙美版","versionid":"20","subjectid":"21"},{"id":"37","versionname":"苏少版","versionid":"21","subjectid":"21"},{"id":"38","versionname":"赣美版","versionid":"22","subjectid":"21"},{"id":"39","versionname":"岭南版","versionid":"23","subjectid":"21"}],"22":[{"id":"40","versionname":"其他版","versionid":"24","subjectid":"22"}],"23":[{"id":"40","versionname":"其他版","versionid":"24","subjectid":"23"}],"24":[{"id":"40","versionname":"其他版","versionid":"24","subjectid":"24"}],"25":[{"id":"40","versionname":"其他版","versionid":"24","subjectid":"25"}],"26":[{"id":"40","versionname":"其他版","versionid":"24","subjectid":"26"}],"27":[{"id":"40","versionname":"其他版","versionid":"24","subjectid":"27"}],"28":[{"id":"40","versionname":"其他版","versionid":"24","subjectid":"28"}],"29":[{"id":"40","versionname":"其他版","versionid":"24","subjectid":"29"}]},"questions":[{"id":"55","type":"组织管理","questionname":"学校要组织一次教师培训，校长把这项工作交给你来做，你怎么开展?"},{"id":"90","type":"人际沟通","questionname":"小学生李某比较调皮，经常惹是生非，对他的教育，家长也不大配合，你准备怎么办?"}]}
     */

    private String code;
    private String message;
    private MesdataBean data;

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

    public MesdataBean getData() {
        return data;
    }

    public void setData(MesdataBean data) {
        this.data = data;
    }

    public boolean success() {
        return "1".equals(code);

    }

    public static class MesdataBean implements Serializable {
        /**
         * phases : [{"id":"13","phasename":"幼儿"},{"id":"14","phasename":"小学"},{"id":"15","phasename":"初中"},{"id":"16","phasename":"高中"}]
         * subjects : {"14":[{"id":"10","subjectname":"语文","subjectid":"1","phaseid":"14"},{"id":"17","subjectname":"数学","subjectid":"2","phaseid":"14"},{"id":"18","subjectname":"英语","subjectid":"3","phaseid":"14"},{"id":"19","subjectname":"音乐","subjectid":"4","phaseid":"14"},{"id":"20","subjectname":"体育","subjectid":"5","phaseid":"14"},{"id":"21","subjectname":"美术","subjectid":"6","phaseid":"14"},{"id":"28","subjectname":"信息技术","subjectid":"13","phaseid":"14"}],"15":[{"id":"10","subjectname":"语文","subjectid":"1","phaseid":"15"},{"id":"17","subjectname":"数学","subjectid":"2","phaseid":"15"},{"id":"18","subjectname":"英语","subjectid":"3","phaseid":"15"},{"id":"19","subjectname":"音乐","subjectid":"4","phaseid":"15"},{"id":"20","subjectname":"体育","subjectid":"5","phaseid":"15"},{"id":"21","subjectname":"美术","subjectid":"6","phaseid":"15"},{"id":"22","subjectname":"物理","subjectid":"7","phaseid":"15"},{"id":"23","subjectname":"化学","subjectid":"8","phaseid":"15"},{"id":"24","subjectname":"生物","subjectid":"9","phaseid":"15"},{"id":"25","subjectname":"政治","subjectid":"10","phaseid":"15"},{"id":"26","subjectname":"历史","subjectid":"11","phaseid":"15"},{"id":"27","subjectname":"地理","subjectid":"12","phaseid":"15"},{"id":"28","subjectname":"信息技术","subjectid":"13","phaseid":"15"}],"16":[{"id":"10","subjectname":"语文","subjectid":"1","phaseid":"16"},{"id":"17","subjectname":"数学","subjectid":"2","phaseid":"16"},{"id":"18","subjectname":"英语","subjectid":"3","phaseid":"16"},{"id":"19","subjectname":"音乐","subjectid":"4","phaseid":"16"},{"id":"20","subjectname":"体育","subjectid":"5","phaseid":"16"},{"id":"21","subjectname":"美术","subjectid":"6","phaseid":"16"},{"id":"22","subjectname":"物理","subjectid":"7","phaseid":"16"},{"id":"23","subjectname":"化学","subjectid":"8","phaseid":"16"},{"id":"24","subjectname":"生物","subjectid":"9","phaseid":"16"},{"id":"25","subjectname":"政治","subjectid":"10","phaseid":"16"},{"id":"26","subjectname":"历史","subjectid":"11","phaseid":"16"},{"id":"27","subjectname":"地理","subjectid":"12","phaseid":"16"},{"id":"28","subjectname":"信息技术","subjectid":"13","phaseid":"16"}],"13":[{"id":"29","subjectname":"幼教","subjectid":"14","phaseid":"13"}]}
         * versions : {"10":[{"id":"16","versionname":"部编版（人教版）","versionid":"1","subjectid":"10"},{"id":"17","versionname":"北师大版","versionid":"2","subjectid":"10"},{"id":"18","versionname":"苏教版","versionid":"3","subjectid":"10"},{"id":"19","versionname":"语文A版","versionid":"4","subjectid":"10"},{"id":"20","versionname":"语文S版","versionid":"5","subjectid":"10"}],"17":[{"id":"17","versionname":"北师大版","versionid":"2","subjectid":"17"},{"id":"18","versionname":"苏教版","versionid":"3","subjectid":"17"},{"id":"21","versionname":"人教版","versionid":"6","subjectid":"17"},{"id":"23","versionname":"华东师大","versionid":"7","subjectid":"17"},{"id":"24","versionname":"青岛版","versionid":"8","subjectid":"17"},{"id":"25","versionname":"长春版","versionid":"9","subjectid":"17"}],"18":[{"id":"17","versionname":"北师大版","versionid":"2","subjectid":"18"},{"id":"26","versionname":"人教版（pep）","versionid":"10","subjectid":"18"},{"id":"27","versionname":"外研社版","versionid":"11","subjectid":"18"},{"id":"28","versionname":"牛津版","versionid":"12","subjectid":"18"},{"id":"29","versionname":"苏教译林版","versionid":"13","subjectid":"18"},{"id":"30","versionname":"冀教版","versionid":"14","subjectid":"18"}],"19":[{"id":"18","versionname":"苏教版","versionid":"3","subjectid":"19"},{"id":"21","versionname":"人教版","versionid":"6","subjectid":"19"},{"id":"31","versionname":"人音版","versionid":"15","subjectid":"19"},{"id":"32","versionname":"花城版","versionid":"16","subjectid":"19"},{"id":"33","versionname":"湘教版","versionid":"17","subjectid":"19"}],"20":[{"id":"21","versionname":"人教版","versionid":"6","subjectid":"20"},{"id":"40","versionname":"其他版","versionid":"24","subjectid":"20"}],"21":[{"id":"21","versionname":"人教版","versionid":"6","subjectid":"21"},{"id":"34","versionname":"人美版","versionid":"18","subjectid":"21"},{"id":"35","versionname":"湘美版","versionid":"19","subjectid":"21"},{"id":"36","versionname":"浙美版","versionid":"20","subjectid":"21"},{"id":"37","versionname":"苏少版","versionid":"21","subjectid":"21"},{"id":"38","versionname":"赣美版","versionid":"22","subjectid":"21"},{"id":"39","versionname":"岭南版","versionid":"23","subjectid":"21"}],"22":[{"id":"40","versionname":"其他版","versionid":"24","subjectid":"22"}],"23":[{"id":"40","versionname":"其他版","versionid":"24","subjectid":"23"}],"24":[{"id":"40","versionname":"其他版","versionid":"24","subjectid":"24"}],"25":[{"id":"40","versionname":"其他版","versionid":"24","subjectid":"25"}],"26":[{"id":"40","versionname":"其他版","versionid":"24","subjectid":"26"}],"27":[{"id":"40","versionname":"其他版","versionid":"24","subjectid":"27"}],"28":[{"id":"40","versionname":"其他版","versionid":"24","subjectid":"28"}],"29":[{"id":"40","versionname":"其他版","versionid":"24","subjectid":"29"}]}
         * questions : [{"id":"55","type":"组织管理","questionname":"学校要组织一次教师培训，校长把这项工作交给你来做，你怎么开展?"},{"id":"90","type":"人际沟通","questionname":"小学生李某比较调皮，经常惹是生非，对他的教育，家长也不大配合，你准备怎么办?"}]
         */

        private SubjectsBean subjects;
        private VersionsBean versions;
        private List<PhasesBean> phases;
//        private List<QuestionsBean> questions;

        public SubjectsBean getSubjects() {
            return subjects;
        }

        public void setSubjects(SubjectsBean subjects) {
            this.subjects = subjects;
        }

        public VersionsBean getVersions() {
            return versions;
        }

        public void setVersions(VersionsBean versions) {
            this.versions = versions;
        }

        public List<PhasesBean> getPhases() {
            return phases;
        }

        public void setPhases(List<PhasesBean> phases) {
            this.phases = phases;
        }

//        public List<QuestionsBean> getQuestions() {
//            return questions;
//        }
//
//        public void setQuestions(List<QuestionsBean> questions) {
//            this.questions = questions;
//        }

        public static class SubjectsBean implements Serializable {
            @SerializedName("14")
            private List<SubjectBean> _$14;
            @SerializedName("15")
            private List<SubjectBean> _$15;
            @SerializedName("16")
            private List<SubjectBean> _$16;
            @SerializedName("13")
            private List<SubjectBean> _$13;

            public List<SubjectBean> get_$14() {
                return _$14;
            }

            public void set_$14(List<SubjectBean> _$14) {
                this._$14 = _$14;
            }

            public List<SubjectBean> get_$15() {
                return _$15;
            }

            public void set_$15(List<SubjectBean> _$15) {
                this._$15 = _$15;
            }

            public List<SubjectBean> get_$16() {
                return _$16;
            }

            public void set_$16(List<SubjectBean> _$16) {
                this._$16 = _$16;
            }

            public List<SubjectBean> get_$13() {
                return _$13;
            }

            public void set_$13(List<SubjectBean> _$13) {
                this._$13 = _$13;
            }


            public static class SubjectBean implements Serializable {
                /**
                 * id : 29
                 * subjectname : 幼教
                 * subjectid : 14
                 * phaseid : 13
                 */

                private String id;
                private String subjectname;
                private String subjectid;
                private String phaseid;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getSubjectname() {
                    return subjectname;
                }

                public void setSubjectname(String subjectname) {
                    this.subjectname = subjectname;
                }

                public String getSubjectid() {
                    return subjectid;
                }

                public void setSubjectid(String subjectid) {
                    this.subjectid = subjectid;
                }

                public String getPhaseid() {
                    return phaseid;
                }

                public void setPhaseid(String phaseid) {
                    this.phaseid = phaseid;
                }
            }
        }

        public static class VersionsBean implements Serializable {
            @SerializedName("10")
            private List<VersionBean> _$10;
            @SerializedName("17")
            private List<VersionBean> _$17;
            @SerializedName("18")
            private List<VersionBean> _$18;
            @SerializedName("19")
            private List<VersionBean> _$19;
            @SerializedName("20")
            private List<VersionBean> _$20;
            @SerializedName("21")
            private List<VersionBean> _$21;
            @SerializedName("22")
            private List<VersionBean> _$22;
            @SerializedName("23")
            private List<VersionBean> _$23;
            @SerializedName("24")
            private List<VersionBean> _$24;
            @SerializedName("25")
            private List<VersionBean> _$25;
            @SerializedName("26")
            private List<VersionBean> _$26;
            @SerializedName("27")
            private List<VersionBean> _$27;
            @SerializedName("28")
            private List<VersionBean> _$28;
            @SerializedName("29")
            private List<VersionBean> _$29;

            public List<VersionBean> get_$10() {
                return _$10;
            }

            public void set_$10(List<VersionBean> _$10) {
                this._$10 = _$10;
            }

            public List<VersionBean> get_$17() {
                return _$17;
            }

            public void set_$17(List<VersionBean> _$17) {
                this._$17 = _$17;
            }

            public List<VersionBean> get_$18() {
                return _$18;
            }

            public void set_$18(List<VersionBean> _$18) {
                this._$18 = _$18;
            }

            public List<VersionBean> get_$19() {
                return _$19;
            }

            public void set_$19(List<VersionBean> _$19) {
                this._$19 = _$19;
            }

            public List<VersionBean> get_$20() {
                return _$20;
            }

            public void set_$20(List<VersionBean> _$20) {
                this._$20 = _$20;
            }

            public List<VersionBean> get_$21() {
                return _$21;
            }

            public void set_$21(List<VersionBean> _$21) {
                this._$21 = _$21;
            }

            public List<VersionBean> get_$22() {
                return _$22;
            }

            public void set_$22(List<VersionBean> _$22) {
                this._$22 = _$22;
            }

            public List<VersionBean> get_$23() {
                return _$23;
            }

            public void set_$23(List<VersionBean> _$23) {
                this._$23 = _$23;
            }

            public List<VersionBean> get_$24() {
                return _$24;
            }

            public void set_$24(List<VersionBean> _$24) {
                this._$24 = _$24;
            }

            public List<VersionBean> get_$25() {
                return _$25;
            }

            public void set_$25(List<VersionBean> _$25) {
                this._$25 = _$25;
            }

            public List<VersionBean> get_$26() {
                return _$26;
            }

            public void set_$26(List<VersionBean> _$26) {
                this._$26 = _$26;
            }

            public List<VersionBean> get_$27() {
                return _$27;
            }

            public void set_$27(List<VersionBean> _$27) {
                this._$27 = _$27;
            }

            public List<VersionBean> get_$28() {
                return _$28;
            }

            public void set_$28(List<VersionBean> _$28) {
                this._$28 = _$28;
            }

            public List<VersionBean> get_$29() {
                return _$29;
            }

            public void set_$29(List<VersionBean> _$29) {
                this._$29 = _$29;
            }


            public static class VersionBean implements Serializable {
                /**
                 * id : 40
                 * versionname : 其他版
                 * versionid : 24
                 * subjectid : 29
                 */

                private String id;
                private String versionname;
                private String versionid;
                private String subjectid;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getVersionname() {
                    return versionname;
                }

                public void setVersionname(String versionname) {
                    this.versionname = versionname;
                }

                public String getVersionid() {
                    return versionid;
                }

                public void setVersionid(String versionid) {
                    this.versionid = versionid;
                }

                public String getSubjectid() {
                    return subjectid;
                }

                public void setSubjectid(String subjectid) {
                    this.subjectid = subjectid;
                }
            }
        }

        public static class PhasesBean implements Serializable {
            /**
             * id : 13
             * phasename : 幼儿
             */

            private String id;
            private String phasename;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getPhasename() {
                return phasename;
            }

            public void setPhasename(String phasename) {
                this.phasename = phasename;
            }
        }

//        public static class QuestionsBean implements Serializable {
//            /**
//             * id : 55
//             * type : 组织管理
//             * questionname : 学校要组织一次教师培训，校长把这项工作交给你来做，你怎么开展?
//             */
//
//            private String id;
//            private String type;
//            private String questionname;
//
//            public String getId() {
//                return id;
//            }
//
//            public void setId(String id) {
//                this.id = id;
//            }
//
//            public String getType() {
//                return type;
//            }
//
//            public void setType(String type) {
//                this.type = type;
//            }
//
//            public String getQuestionname() {
//                return questionname;
//            }
//
//            public void setQuestionname(String questionname) {
//                this.questionname = questionname;
//            }
//        }
    }
}
