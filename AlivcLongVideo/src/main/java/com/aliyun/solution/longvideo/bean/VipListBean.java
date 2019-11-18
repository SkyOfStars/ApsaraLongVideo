package com.aliyun.solution.longvideo.bean;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;

import java.util.ArrayList;
import java.util.List;

public class VipListBean {

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


        private LongVideoListBean longVideoList;

        public LongVideoListBean getLongVideoList() {
            if (longVideoList == null) {
                longVideoList = new LongVideoListBean();
            }
            return longVideoList;
        }

        public void setLongVideoList(LongVideoListBean longVideoList) {
            this.longVideoList = longVideoList;
        }

        public static class LongVideoListBean {


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
}
