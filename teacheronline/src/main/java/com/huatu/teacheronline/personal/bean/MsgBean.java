package com.huatu.teacheronline.personal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 18250 on 2017/8/25.
 */
public class MsgBean implements Serializable {


    /**
     * content : 内容 1
     * title : 标题
     * checkedCount : 0
     * leaveMessageList : [{"content":"留言","createTime":"2017-08-25 10:44:42","replayContent":"","isReplyFlg":0,"replayTime":""}]
     * pushTime : 2017-04-21 10:17:05
     * type : 开课通知
     * messageId : 86
     * checked : 0
     *
     */
    private String content;
    private String title;
    private int checkedCount;
//    leaveGMessageList
    private List<LeaveMessageListEntity> leaveMessageList;
    private String pushTime;
    private String type;
    private int messageId;
    private int checked;
    private int noticeId;

    public void setnoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    public int getnoticeId() {
        return noticeId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCheckedCount(int checkedCount) {
        this.checkedCount = checkedCount;
    }

    public void setLeaveMessageList(List<LeaveMessageListEntity> leaveMessageList) {
        this.leaveMessageList = leaveMessageList;
    }

    public void setPushTime(String pushTime) {
        this.pushTime = pushTime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public int getCheckedCount() {
        return checkedCount;
    }

    public List<LeaveMessageListEntity> getLeaveMessageList() {
        return leaveMessageList;
    }

    public String getPushTime() {
        return pushTime;
    }

    public String getType() {
        return type;
    }

    public int getMessageId() {
        return messageId;
    }

    public int getChecked() {
        return checked;
    }

    public class LeaveMessageListEntity implements Serializable{
        /**
         * content : 留言
         * createTime : 2017-08-25 10:44:42
         * replayContent :
         * isReplyFlg : 0
         * replayTime :
         */
        private String content;
        private String createTime;
        private String replayContent;
        private int isReplyFlg;
        private String replayTime;

        public void setContent(String content) {
            this.content = content;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public void setReplayContent(String replayContent) {
            this.replayContent = replayContent;
        }

        public void setIsReplyFlg(int isReplyFlg) {
            this.isReplyFlg = isReplyFlg;
        }

        public void setReplayTime(String replayTime) {
            this.replayTime = replayTime;
        }

        public String getContent() {
            return content;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getReplayContent() {
            return replayContent;
        }

        public int getIsReplyFlg() {
            return isReplyFlg;
        }

        public String getReplayTime() {
            return replayTime;
        }
    }
}
