package com.aliyun.solution.longvideo.bean;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeSingleSetVideoListBean implements Serializable {

    /**
     * result : true
     * requestId : 5b80e24a-63f1-4b60-935d-152e0e5d01e6
     * message :
     * code : 200
     * data : {"tagVideoList":[{"total":1,"videoList":[{"id":"3","tvId":"294484","tvName":"","title":"发文v但是","description":"稍等","coverUrl":"3切图54","creationTime":"2019-07-03 19:07:25.0","firstFrameUrl":"32","size":234,"cateId":0,"cateName":"","tags":"专题节目","isRecommend":"true","isHomePage":"true","videoId":"653","duration":2342,"transcodeStatus":"234","snapshotStatus":"43","censorStatus":"success","snapshotList":null,"dot":"35过","sort":"3","isVip":"true"}],"tag":"专题节目"},{"total":1,"videoList":[{"id":"4","tvId":"324","tvName":"","title":"会台湾艺人美女","description":"稍等","coverUrl":"3切图54","creationTime":"2019-07-03 19:07:25.0","firstFrameUrl":"32","size":234,"cateId":0,"cateName":"","tags":"精彩节目","isRecommend":"true","isHomePage":"true","videoId":"363145","duration":2342,"transcodeStatus":"234","snapshotStatus":"43","censorStatus":"success","snapshotList":null,"dot":"35过","sort":"4","isVip":""}],"tag":"精彩节目"}]}
     */

    private String result;
    private String requestId;
    private String message;
    private String code;
    private DataBean data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        private List<TagVideoListBean> tagVideoList;

        public List<TagVideoListBean> getTagVideoList() {
            if (tagVideoList == null) {
                tagVideoList = new ArrayList<>();
            }
            return tagVideoList;
        }

        public void setTagVideoList(List<TagVideoListBean> tagVideoList) {
            this.tagVideoList = tagVideoList;
        }

        public static class TagVideoListBean implements Serializable {
            /**
             * total : 1
             * videoList : [{"id":"3","tvId":"294484","tvName":"","title":"发文v但是","description":"稍等","coverUrl":"3切图54","creationTime":"2019-07-03 19:07:25.0","firstFrameUrl":"32","size":234,"cateId":0,"cateName":"","tags":"专题节目","isRecommend":"true","isHomePage":"true","videoId":"653","duration":2342,"transcodeStatus":"234","snapshotStatus":"43","censorStatus":"success","snapshotList":null,"dot":"35过","sort":"3","isVip":"true"}]
             * tag : 专题节目
             */

            private int total;
            private String tag;
            private List<LongVideoBean> videoList;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public String getTag() {
                return tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }

            public List<LongVideoBean> getVideoList() {
                if (videoList == null) {
                    videoList = new ArrayList<>();
                }
                return videoList;
            }

            public void setVideoList(List<LongVideoBean> videoList) {
                this.videoList = videoList;
            }
        }
    }
}
