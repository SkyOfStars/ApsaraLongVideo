package com.aliyun.solution.longvideo.view.Viewplayer.listener;

import com.aliyun.player.source.VidSts;


/**
 * VideoPlayer抽象接口
 */
public interface InterVideoPlayer {

    /**
     * 设置视频播放信息
     *
     * @param vidSts
     */
    void setUp(VidSts vidSts);

    /**
     * 开始播放
     */
    void start();


    /**
     * 重新播放，播放器被暂停、播放错误、播放完成后，需要调用此方法重新播放
     */
    void restart();

    /**
     * 暂停播放
     */
    void pause();

    boolean isIdle();

    boolean isPreparing();

    boolean isPrepared();

    boolean isPlaying();

    boolean isPaused();

    boolean isError();

    boolean isCompleted();


    /**
     * 此处只释放播放器（如果要释放播放器并恢复控制器状态需要调用{@link #release()}方法）
     */
    void releasePlayer();

    /**
     * 释放INiceVideoPlayer，释放后，内部的播放器被释放掉，
     * 控制器的UI也应该恢复到最初始的状态.
     */
    void release();
}
