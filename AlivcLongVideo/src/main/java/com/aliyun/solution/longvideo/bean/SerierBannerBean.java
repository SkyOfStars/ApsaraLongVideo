package com.aliyun.solution.longvideo.bean;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;

import java.util.ArrayList;
import java.util.List;

public class SerierBannerBean {

    /**
     * result : true
     * requestId : c0141dcb-dc17-42e8-a1e8-b46c80076548
     * message :
     * code : 200
     * data : {"tvPlayList":[{"id":"12","tvId":"15345","tvName":"ttt","title":"的","description":"tggg","coverUrl":"http://alivc-demo-vod.aliyuncs.com/image/cover/EFF93CE32EC640E58CB740BC20E03071-6-2.png","creationTime":"2019-07-04 17:00:52.0","firstFrameUrl":"","size":0,"cateId":0,"cateName":"","tags":"test并不比标签","isRecommend":"true","isHomePage":"","isRelease":"true","total":"12"},{"id":"11","tvId":"123","tvName":"eee","title":"第三方","description":"bbbb","coverUrl":"http://alivc-demo-vod.aliyuncs.com/image/cover/D97385F420B7479C9E5E3DA06F1A52C9-6-2.png","creationTime":"2019-07-04 16:52:52.0","firstFrameUrl":"","size":0,"cateId":0,"cateName":"","tags":"","isRecommend":"true","isHomePage":"","isRelease":"true","total":"12"},{"id":"8","tvId":"137249","tvName":"ttt","title":" 调方式","description":"撒旦","coverUrl":"http://alivc-demo-vod.aliyuncs.com/image/cover/EFF93CE32EC640E58CB740BC20E03071-6-2.png","creationTime":"2019-07-04 17:00:52.0","firstFrameUrl":"","size":0,"cateId":0,"cateName":"","tags":"test并不比标签","isRecommend":"true","isHomePage":"true","isRelease":"true","total":"7"},{"id":"3","tvId":"563","tvName":"eee","title":"阿斯顿","description":"士大夫","coverUrl":"http://alivc-demo-vod.aliyuncs.com/image/cover/D97385F420B7479C9E5E3DA06F1A52C9-6-2.png","creationTime":"2019-07-04 16:52:52.0","firstFrameUrl":"","size":0,"cateId":0,"cateName":"","tags":"","isRecommend":"true","isHomePage":"","isRelease":"true","total":"2"},{"id":"2","tvId":"15345","tvName":"ttt","title":"的","description":"tggg","coverUrl":"http://alivc-demo-vod.aliyuncs.com/image/cover/EFF93CE32EC640E58CB740BC20E03071-6-2.png","creationTime":"2019-07-04 17:00:52.0","firstFrameUrl":"","size":0,"cateId":0,"cateName":"","tags":"test并不比标签","isRecommend":"true","isHomePage":"","isRelease":"true","total":"12"},{"id":"1","tvId":"324","tvName":"eee","title":"第三方","description":"bbbb","coverUrl":"http://alivc-demo-vod.aliyuncs.com/image/cover/D97385F420B7479C9E5E3DA06F1A52C9-6-2.png","creationTime":"2019-07-04 16:52:52.0","firstFrameUrl":"","size":0,"cateId":0,"cateName":"","tags":"","isRecommend":"true","isHomePage":"","isRelease":"true","total":"12"}],"total":7}
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
         * tvPlayList : [{"id":"12","tvId":"15345","tvName":"ttt","title":"的","description":"tggg","coverUrl":"http://alivc-demo-vod.aliyuncs.com/image/cover/EFF93CE32EC640E58CB740BC20E03071-6-2.png","creationTime":"2019-07-04 17:00:52.0","firstFrameUrl":"","size":0,"cateId":0,"cateName":"","tags":"test并不比标签","isRecommend":"true","isHomePage":"","isRelease":"true","total":"12"},{"id":"11","tvId":"123","tvName":"eee","title":"第三方","description":"bbbb","coverUrl":"http://alivc-demo-vod.aliyuncs.com/image/cover/D97385F420B7479C9E5E3DA06F1A52C9-6-2.png","creationTime":"2019-07-04 16:52:52.0","firstFrameUrl":"","size":0,"cateId":0,"cateName":"","tags":"","isRecommend":"true","isHomePage":"","isRelease":"true","total":"12"},{"id":"8","tvId":"137249","tvName":"ttt","title":" 调方式","description":"撒旦","coverUrl":"http://alivc-demo-vod.aliyuncs.com/image/cover/EFF93CE32EC640E58CB740BC20E03071-6-2.png","creationTime":"2019-07-04 17:00:52.0","firstFrameUrl":"","size":0,"cateId":0,"cateName":"","tags":"test并不比标签","isRecommend":"true","isHomePage":"true","isRelease":"true","total":"7"},{"id":"3","tvId":"563","tvName":"eee","title":"阿斯顿","description":"士大夫","coverUrl":"http://alivc-demo-vod.aliyuncs.com/image/cover/D97385F420B7479C9E5E3DA06F1A52C9-6-2.png","creationTime":"2019-07-04 16:52:52.0","firstFrameUrl":"","size":0,"cateId":0,"cateName":"","tags":"","isRecommend":"true","isHomePage":"","isRelease":"true","total":"2"},{"id":"2","tvId":"15345","tvName":"ttt","title":"的","description":"tggg","coverUrl":"http://alivc-demo-vod.aliyuncs.com/image/cover/EFF93CE32EC640E58CB740BC20E03071-6-2.png","creationTime":"2019-07-04 17:00:52.0","firstFrameUrl":"","size":0,"cateId":0,"cateName":"","tags":"test并不比标签","isRecommend":"true","isHomePage":"","isRelease":"true","total":"12"},{"id":"1","tvId":"324","tvName":"eee","title":"第三方","description":"bbbb","coverUrl":"http://alivc-demo-vod.aliyuncs.com/image/cover/D97385F420B7479C9E5E3DA06F1A52C9-6-2.png","creationTime":"2019-07-04 16:52:52.0","firstFrameUrl":"","size":0,"cateId":0,"cateName":"","tags":"","isRecommend":"true","isHomePage":"","isRelease":"true","total":"12"}]
         * total : 7
         */

        private int total;
        private List<LongVideoBean> tvPlayList;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
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
