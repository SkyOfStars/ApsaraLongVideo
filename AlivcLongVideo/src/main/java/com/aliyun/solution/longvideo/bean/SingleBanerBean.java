package com.aliyun.solution.longvideo.bean;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;

import java.util.ArrayList;
import java.util.List;

public class SingleBanerBean {

    /**
     * result : true
     * requestId : 609c5f88-a509-4029-b944-af91d5469669
     * message :
     * code : 200
     * data : {"total":2,"videoList":[{"id":"7","tvId":"324","tvName":"","title":"爱到骨头嘉年华","description":"稍等","coverUrl":"3切图54","creationTime":"2019-07-03 19:07:25.0","firstFrameUrl":"32","size":234,"cateId":0,"cateName":"","tags":"关注","isRecommend":"true","isHomePage":"","videoId":"87356","duration":2342,"transcodeStatus":"234","snapshotStatus":"43","censorStatus":"success","snapshotList":null,"dot":"35过","sort":"8","isVip":"true"},{"id":"1","tvId":"123","tvName":"","title":"t23而非我","description":"324","coverUrl":"3切图54","creationTime":"2019-07-03 19:07:25.0","firstFrameUrl":"32","size":234,"cateId":0,"cateName":"","tags":"","isRecommend":"true","isHomePage":"true","videoId":"34","duration":2342,"transcodeStatus":"234","snapshotStatus":"43","censorStatus":"success","snapshotList":null,"dot":"35过","sort":"1","isVip":""}]}
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

    public static class DataBean {
        /**
         * total : 2
         * videoList : [{"id":"7","tvId":"324","tvName":"","title":"爱到骨头嘉年华","description":"稍等","coverUrl":"3切图54","creationTime":"2019-07-03 19:07:25.0","firstFrameUrl":"32","size":234,"cateId":0,"cateName":"","tags":"关注","isRecommend":"true","isHomePage":"","videoId":"87356","duration":2342,"transcodeStatus":"234","snapshotStatus":"43","censorStatus":"success","snapshotList":null,"dot":"35过","sort":"8","isVip":"true"},{"id":"1","tvId":"123","tvName":"","title":"t23而非我","description":"324","coverUrl":"3切图54","creationTime":"2019-07-03 19:07:25.0","firstFrameUrl":"32","size":234,"cateId":0,"cateName":"","tags":"","isRecommend":"true","isHomePage":"true","videoId":"34","duration":2342,"transcodeStatus":"234","snapshotStatus":"43","censorStatus":"success","snapshotList":null,"dot":"35过","sort":"1","isVip":""}]
         */

        private int total;
        private List<LongVideoBean> videoList;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
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
