package com.aliyun.solution.longvideo.view.Viewplayer.manager;


import com.aliyun.solution.longvideo.view.Viewplayer.VideoPlayer;

/**
 * 将类置成final,视频播放器管理器
 */
public final class VideoPlayerManager {

    private VideoPlayer mVideoPlayer;
    private static volatile VideoPlayerManager sInstance;

    private VideoPlayerManager() {
    }

    /**
     * 一定要使用单例模式，保证同一时刻只有一个视频在播放，其他的都是初始状态
     * 单例模式
     *
     * @return VideoPlayerManager对象
     */
    public static VideoPlayerManager instance() {
        if (sInstance == null) {
            synchronized (VideoPlayerManager.class) {
                if (sInstance == null) {
                    sInstance = new VideoPlayerManager();
                }
            }
        }
        return sInstance;
    }


    /**
     * 获取对象
     *
     * @return VideoPlayerManager对象
     */
    public VideoPlayer getCurrentVideoPlayer() {
        return mVideoPlayer;
    }


    /**
     * 设置VideoPlayer
     *
     * @param videoPlayer VideoPlayerManager对象
     */
    public void setCurrentVideoPlayer(VideoPlayer videoPlayer) {
        if (mVideoPlayer != videoPlayer) {
            releaseVideoPlayer();
            mVideoPlayer = videoPlayer;
        }
    }


    /**
     * 当视频正在播放或者正在缓冲时，调用该方法暂停视频
     */
    public void suspendVideoPlayer() {
        if (mVideoPlayer != null) {
            if (mVideoPlayer.isPlaying()) {
                mVideoPlayer.pause();
            }
        }
    }


    /**
     * 当视频暂停时或者缓冲暂停时，调用该方法重新开启视频播放
     */
    public void resumeVideoPlayer() {
        if (mVideoPlayer != null) {
            if (mVideoPlayer.isPaused()) {
                mVideoPlayer.restart();
            }
        }
    }


    /**
     * 释放，内部的播放器被释放掉
     */
    public void releaseVideoPlayer() {
        if (mVideoPlayer != null) {
            mVideoPlayer.release();
            mVideoPlayer = null;
        }
    }


}
