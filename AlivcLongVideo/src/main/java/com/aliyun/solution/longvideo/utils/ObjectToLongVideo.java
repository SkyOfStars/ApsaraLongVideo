package com.aliyun.solution.longvideo.utils;

import android.text.TextUtils;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadMediaInfo;
import com.aliyun.player.source.VidSts;

public class ObjectToLongVideo {

    /**
     * 待完善，部分数据没有
     */
    public static LongVideoBean downloadMediaInfoToLongVideo(AliyunDownloadMediaInfo aliyunDownloadMediaInfo) {
        LongVideoBean longVideoBean = new LongVideoBean();
        longVideoBean.setTitle(aliyunDownloadMediaInfo.getTitle());
        longVideoBean.setVideoId(aliyunDownloadMediaInfo.getVid());
        longVideoBean.setDuration(aliyunDownloadMediaInfo.getDuration() + "");
        longVideoBean.setCoverUrl(aliyunDownloadMediaInfo.getCoverUrl());
        longVideoBean.setSize(aliyunDownloadMediaInfo.getSize() + "");
        longVideoBean.setTvId(aliyunDownloadMediaInfo.getTvId());
        longVideoBean.setSaveUrl(aliyunDownloadMediaInfo.getSavePath());
        return longVideoBean;
    }

    /**
     * longVideoBean转换成AliyunDownloadMediaInfo
     */
    public static AliyunDownloadMediaInfo longVideoBeanToAliyunDownloadMediaInfo(LongVideoBean longVideoBean, VidSts vidSts, String quality, String format) {
        AliyunDownloadMediaInfo aliyunDownloadMediaInfo = new AliyunDownloadMediaInfo();
        aliyunDownloadMediaInfo.setVid(longVideoBean.getVideoId());
        aliyunDownloadMediaInfo.setTvId(longVideoBean.getTvId());
        aliyunDownloadMediaInfo.setCoverUrl(longVideoBean.getCoverUrl());
        aliyunDownloadMediaInfo.setTitle(longVideoBean.getTitle());
        aliyunDownloadMediaInfo.setTvName(longVideoBean.getTvName());
        double duration = TextUtils.isEmpty(longVideoBean.getDuration()) ? 0 : (Double.valueOf(longVideoBean.getDuration()) * 1000);
        aliyunDownloadMediaInfo.setDuration((long) duration);
        aliyunDownloadMediaInfo.setStatus(AliyunDownloadMediaInfo.Status.Prepare);
        aliyunDownloadMediaInfo.setProgress(0);
        long size = TextUtils.isEmpty(longVideoBean.getSize()) ? 0 : Long.valueOf(longVideoBean.getSize());
        aliyunDownloadMediaInfo.setSize(size);
        aliyunDownloadMediaInfo.setVidSts(vidSts);
        aliyunDownloadMediaInfo.setQuality(quality);
        aliyunDownloadMediaInfo.setFormat(format);

        return aliyunDownloadMediaInfo;
    }
}
