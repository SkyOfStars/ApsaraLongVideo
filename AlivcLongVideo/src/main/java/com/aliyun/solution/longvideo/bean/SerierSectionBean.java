package com.aliyun.solution.longvideo.bean;


import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;
import com.aliyun.svideo.common.baseAdapter.entity.SectionEntity;

public class SerierSectionBean extends SectionEntity<LongVideoBean> {


    public SerierSectionBean(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public SerierSectionBean(LongVideoBean videoListBean) {
        super(videoListBean);
    }
}
