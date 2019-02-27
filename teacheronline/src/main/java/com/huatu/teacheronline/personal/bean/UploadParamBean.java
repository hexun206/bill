package com.huatu.teacheronline.personal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kinndann on 2018/10/31.
 * description:
 */
public class UploadParamBean implements Serializable {

    private List<PhasesBean> phases;


    public List<PhasesBean> getPhases() {
        return phases;
    }

    public void setPhases(List<PhasesBean> phases) {
        this.phases = phases;
    }


    public static class PhasesBean implements Serializable {
        /**
         * id : 13
         * phasename : 幼儿
         */

        private String id;
        private String phasename;
        private List<SubjectBean> subjects;

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

        public List<SubjectBean> getSubjects() {
            return subjects;
        }

        public void setSubjects(List<SubjectBean> subjects) {
            this.subjects = subjects;
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

            private List<VersionBean> versions;


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

            public List<VersionBean> getVersions() {
                return versions;
            }

            public void setVersions(List<VersionBean> versions) {
                this.versions = versions;
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


    }

}
