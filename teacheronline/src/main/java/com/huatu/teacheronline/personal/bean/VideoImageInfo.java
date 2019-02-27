package com.huatu.teacheronline.personal.bean;

import java.util.List;

/**
 * Created by kinndann on 2018/11/23.
 * description:
 */
public class VideoImageInfo {


    /**
     * code : 1
     * data : {"height":640,"img_count":8,"imgs":["http://img.baijiayun.com/00-x-upload/image/14248939_cd798c405f8c5d45de7e64acdbdc3aac_lkAxKDui.jpg","http://img.baijiayun.com/00-x-upload/image/14248939_61f9750d509c9b7269d3d29cee6d4ebd_pGACuH0n.jpg","http://img.baijiayun.com/00-x-upload/image/14248939_b251c42c937281f64947d20424b8ff30_9ok1LON0.jpg","http://img.baijiayun.com/00-x-upload/image/14248939_9246e66315661b8d4c1f9841506496cb_DUSbBYjW.jpg","http://img.baijiayun.com/00-x-upload/image/14248939_338ee86e2efd849ef2552804fc75bbb8_Okhw1piD.jpg","http://img.baijiayun.com/00-x-upload/image/14248939_eb74be68b6ac4ccc2afeb032112663d6_9TSf8jfA.jpg","http://img.baijiayun.com/00-x-upload/image/14248939_3f96c2cca1999f05787c7036988bd22d_JkQngQDb.jpg","http://img.baijiayun.com/00-x-upload/image/14248939_8efadee76eb4810af9265b29c67da317_qMYzFyBA.jpg"],"width":432,"video_id":"14248939"}
     * message : success
     */

    private String code;
    private Img data;
    private String message;


    public boolean success() {
        return "1".equals(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Img getData() {
        return data;
    }

    public void setData(Img data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Img {
        /**
         * height : 640
         * img_count : 8
         * imgs : ["http://img.baijiayun.com/00-x-upload/image/14248939_cd798c405f8c5d45de7e64acdbdc3aac_lkAxKDui.jpg","http://img.baijiayun.com/00-x-upload/image/14248939_61f9750d509c9b7269d3d29cee6d4ebd_pGACuH0n.jpg","http://img.baijiayun.com/00-x-upload/image/14248939_b251c42c937281f64947d20424b8ff30_9ok1LON0.jpg","http://img.baijiayun.com/00-x-upload/image/14248939_9246e66315661b8d4c1f9841506496cb_DUSbBYjW.jpg","http://img.baijiayun.com/00-x-upload/image/14248939_338ee86e2efd849ef2552804fc75bbb8_Okhw1piD.jpg","http://img.baijiayun.com/00-x-upload/image/14248939_eb74be68b6ac4ccc2afeb032112663d6_9TSf8jfA.jpg","http://img.baijiayun.com/00-x-upload/image/14248939_3f96c2cca1999f05787c7036988bd22d_JkQngQDb.jpg","http://img.baijiayun.com/00-x-upload/image/14248939_8efadee76eb4810af9265b29c67da317_qMYzFyBA.jpg"]
         * width : 432
         * video_id : 14248939
         */

        private int height;
        private int img_count;
        private int width;
        private String video_id;
        private List<String> imgs;

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getImg_count() {
            return img_count;
        }

        public void setImg_count(int img_count) {
            this.img_count = img_count;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public String getVideo_id() {
            return video_id;
        }

        public void setVideo_id(String video_id) {
            this.video_id = video_id;
        }

        public List<String> getImgs() {
            return imgs;
        }

        public void setImgs(List<String> imgs) {
            this.imgs = imgs;
        }
    }
}
