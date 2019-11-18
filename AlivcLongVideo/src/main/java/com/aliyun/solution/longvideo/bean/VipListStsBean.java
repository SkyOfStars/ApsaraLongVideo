package com.aliyun.solution.longvideo.bean;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;
import com.aliyun.player.source.VidSts;

public class VipListStsBean {
    private LongVideoBean videoListBean;
    private VidSts stsDataBean;

    public VipListStsBean(LongVideoBean videoListBean, VidSts stsDataBean) {
        this.videoListBean = videoListBean;
        this.stsDataBean = stsDataBean;
    }

    public LongVideoBean getVideoListBean() {
        return videoListBean;
    }

    public void setVideoListBean(LongVideoBean videoListBean) {
        this.videoListBean = videoListBean;
    }

    public VidSts getStsDataBean() {
        return stsDataBean;
    }

    public void setStsDataBean(VidSts stsDataBean) {
        this.stsDataBean = stsDataBean;
    }
}

