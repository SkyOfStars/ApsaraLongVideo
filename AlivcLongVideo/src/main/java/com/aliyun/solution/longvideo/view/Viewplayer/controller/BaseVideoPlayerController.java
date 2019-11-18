package com.aliyun.solution.longvideo.view.Viewplayer.controller;


import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aliyun.solution.longvideo.view.Viewplayer.listener.InterVideoPlayer;

/**
 * 控制器抽象类
 */
public abstract class BaseVideoPlayerController extends FrameLayout {

    protected InterVideoPlayer mVideoPlayer;

    public BaseVideoPlayerController(Context context) {
        super(context);
    }

    public void setVideoPlayer(InterVideoPlayer videoPlayer) {
        mVideoPlayer = videoPlayer;
    }

    /**
     * 获取是否是锁屏模式
     *
     * @return true表示锁屏
     */
    public abstract boolean getLock();

    /**
     * 设置视频播放器中间的播放键是否显示，设置自定义图片
     *
     * @param isVisibility 是否可见
     * @param image        image
     */
    public abstract void setCenterPlayer(boolean isVisibility, @DrawableRes int image);

    /**
     * 设置播放的视频的标题
     *
     * @param title 视频标题
     */
    public abstract void setTitle(String title);

    /**
     * 当播放器的播放状态发生变化，在此方法中国你更新不同的播放状态的UI
     *
     * @param playState 播放状态：
     */
    public abstract void onPlayStateChanged(int playState);

    /**
     * 是否vip
     */
    public abstract void isVip(boolean vip);

    /**
     * 是剧集还是系列
     * true 剧集 false 系列
     */
    public abstract void setVideoType(boolean videoType);

    /**
     * 重置控制器，将控制器恢复到初始状态。
     */
    public abstract void reset();

    /**
     * 控制器意外销毁，比如手动退出，意外崩溃等等
     */
    public abstract void destroy();

    public abstract ImageView imageView();

    public abstract void setImage(@DrawableRes int resId);

    public abstract void setLength(long length);

    public abstract void setLength(String length);

}
