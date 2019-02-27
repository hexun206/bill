package com.huatu.teacheronline.direct.bean;

import java.io.Serializable;
import java.util.Objects;

/**
 * pdf信息类
 * Created by ljyu on 2017/9/4.
 */
public class PdfBean implements Serializable {
    String Title;
    String fileUrl;
    int progress;
    String errorMessage;


    /**
     * 0:未下载 1:已下载 2:等待 3:异常 4:正在下载
     */
    int state;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PdfBean pdfBean = (PdfBean) o;
        return Objects.equals(Title, pdfBean.Title) &&
                Objects.equals(fileUrl, pdfBean.fileUrl);
    }

    @Override
    public int hashCode() {

        return Objects.hash(Title, fileUrl);
    }

    @Override
    public String toString() {
        return "PdfBean{" +
                "Title='" + Title + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                '}';
    }
}
