package com.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table EXERCISE_DOWNLOAD_PACKAGE.
 */
public class ExerciseDownloadPackage {

    private String id;
    private String downloadstatus;
    private String versions;

    public ExerciseDownloadPackage() {
    }

    public ExerciseDownloadPackage(String id, String downloadstatus, String versions) {
        this.id = id;
        this.downloadstatus = downloadstatus;
        this.versions = versions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDownloadstatus() {
        return downloadstatus;
    }

    public void setDownloadstatus(String downloadstatus) {
        this.downloadstatus = downloadstatus;
    }

    public String getVersions() {
        return versions;
    }

    public void setVersions(String versions) {
        this.versions = versions;
    }

}
