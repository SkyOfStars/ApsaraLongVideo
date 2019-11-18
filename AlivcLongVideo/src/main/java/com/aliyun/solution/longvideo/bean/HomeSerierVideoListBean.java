package com.aliyun.solution.longvideo.bean;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeSerierVideoListBean implements Serializable {


    /**
     * result : true
     * requestId : 2c9bb803-8867-41b6-b698-6caf2ac16daa
     * message :
     * code : 200
     * data : {"tagTvPlayList":[{"tvPlayList":[{"id":"5","tvId":"7645","tvName":"eee","title":"说","description":"突然还没好","coverUrl":"http://alivc-demo-vod.aliyuncs.com/image/cover/D97385F420B7479C9E5E3DA06F1A52C9-6-2.png","creationTime":"2019-07-04 16:52:52.0","firstFrameUrl":"","size":0,"cateId":0,"cateName":"","tags":"精彩节目","isRecommend":"","isHomePage":"true","isRelease":"true","total":"5"}],"total":2,"tag":"精彩节目"},{"tvPlayList":[{"id":"4","tvId":"87654","tvName":"ttt","title":"你如果","description":"3他忽然","coverUrl":"http://alivc-demo-vod.aliyuncs.com/image/cover/EFF93CE32EC640E58CB740BC20E03071-6-2.png","creationTime":"2019-07-04 17:00:52.0","firstFrameUrl":"","size":0,"cateId":0,"cateName":"","tags":"专题节目","isRecommend":"","isHomePage":"true","isRelease":"true","total":"4"}],"total":2,"tag":"专题节目"}]}
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
        private List<TagTvPlayListBean> tagTvPlayList;

        public List<TagTvPlayListBean> getTagTvPlayList() {
            if (tagTvPlayList == null) {
                return tagTvPlayList = new ArrayList<>();
            }
            return tagTvPlayList;
        }

        public void setTagTvPlayList(List<TagTvPlayListBean> tagTvPlayList) {
            this.tagTvPlayList = tagTvPlayList;
        }

        public static class TagTvPlayListBean implements Serializable {
            /**
             * tvPlayList : [{"id":"5","tvId":"7645","tvName":"eee","title":"说","description":"突然还没好","coverUrl":"http://alivc-demo-vod.aliyuncs.com/image/cover/D97385F420B7479C9E5E3DA06F1A52C9-6-2.png","creationTime":"2019-07-04 16:52:52.0","firstFrameUrl":"","size":0,"cateId":0,"cateName":"","tags":"精彩节目","isRecommend":"","isHomePage":"true","isRelease":"true","total":"5"}]
             * total : 2
             * tag : 精彩节目
             */

            private int total;
            private String tag;
            private List<LongVideoBean> tvPlayList;

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

            public List<LongVideoBean> getTvPlayList() {
                if (tvPlayList == null) {
                    tvPlayList = new ArrayList<>();
                }
                return tvPlayList;
            }

            public void setTvPlayList(List<LongVideoBean> tvPlayList) {
                this.tvPlayList = tvPlayList;
            }
        }
    }
}
