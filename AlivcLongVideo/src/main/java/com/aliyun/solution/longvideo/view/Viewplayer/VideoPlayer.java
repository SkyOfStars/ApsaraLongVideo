
package com.aliyun.solution.longvideo.view.Viewplayer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.VidPlayerConfigGen;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.source.VidSts;
import com.aliyun.solution.longvideo.R;
import com.aliyun.solution.longvideo.utils.SettingSpUtils;
import com.aliyun.solution.longvideo.view.Viewplayer.controller.BaseVideoPlayerController;
import com.aliyun.solution.longvideo.view.Viewplayer.listener.InterVideoPlayer;
import com.aliyun.solution.longvideo.view.Viewplayer.listener.OnSurfaceListener;
import com.aliyun.solution.longvideo.view.Viewplayer.manager.VideoPlayerManager;
import com.aliyun.svideo.common.utils.ToastUtils;

import static com.aliyun.player.alivcplayerexpand.widget.AliyunVodPlayerView.PLAY_DOMAIN;
import static com.aliyun.player.alivcplayerexpand.widget.AliyunVodPlayerView.TRAILER;


/**
 * 播放器
 * revise: 注意：在对应的播放Activity页面，清单文件中一定要添加
 * android:configChanges="orientation|keyboardHidden|screenSize"
 * android:screenOrientation="portrait"
 */
public class VideoPlayer extends FrameLayout implements InterVideoPlayer {

    private static final String TAG = VideoPlayer.class.getSimpleName();
    /**
     * 播放状态，错误，开始播放，暂停播放，缓存中等等状态
     **/
    private int mCurrentState = IPlayer.idle;

    private Context mContext;
    private AliPlayer mMediaPlayer;
    private FrameLayout mContainer;
    private VideoTextureView mTextureView;
    private BaseVideoPlayerController mController;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private VidSts mVidSts;

    public VideoPlayer(Context context) {
        this(context, null);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mContainer = new FrameLayout(mContext);
        //设置背景颜色，目前设置为纯黑色
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                               ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, params);
    }

    /**
     * 如果锁屏，则屏蔽返回键，这个地方设置无效，需要在activity中设置处理返回键逻辑
     * 后期找替代方案
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "如果锁屏1，则屏蔽返回键onKeyDown" + event.getAction());
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow");
        //init();
        //在构造函数初始化时addView
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow");
        //onDetachedFromWindow方法是在Activity destroy的时候被调用的，也就是act对应的window被删除的时候，
        //且每个view只会被调用一次，父view的调用在后，也不论view的visibility状态都会被调用，适合做最后的清理操作
        //防止开发者没有在onDestroy中没有做销毁视频的优化
        release();
    }

    /*--------------setUp为必须设置的方法--------------------------------------*/

    /**
     * 设置，必须设置
     * vidsts
     */
    @Override
    public final void setUp(VidSts vidSts) {
        if(vidSts == null || TextUtils.isEmpty(vidSts.getVid())){
            Log.d(TAG, "设置的视频链接不能为空");
        }
        mVidSts = vidSts;
    }


    /**
     * 设置视频控制器，必须设置
     *
     * @param controller AbsVideoPlayerController子类对象，可用VideoPlayerController，也可自定义
     */
    public void setController(@NonNull BaseVideoPlayerController controller) {
        //这里必须先移除
        if (mContainer.getParent() != null) {
            mContainer.removeView(mController);
        }
        mController = controller;
        mController.reset();
        mController.setVideoPlayer(this);
        LayoutParams params = new LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);

        mContainer.addView(mController, params);
    }

    public BaseVideoPlayerController getController() {
        return mController;
    }

    /**
     * 开始播放
     */
    @Override
    public void start() {
        if (mCurrentState == IPlayer.idle) {
            VideoPlayerManager.instance().setCurrentVideoPlayer(this);
            initMediaPlayer();
            initTextureView();
        } else {
            Log.d(TAG, "VideoPlayer只有在mCurrentState == STATE_IDLE时才能调用start方法.");
        }
    }


    /**
     * 重新播放
     */
    @Override
    public void restart() {
        if (mCurrentState == IPlayer.paused) {
            //如果是暂停状态，那么则继续播放
            mMediaPlayer.start();
            mCurrentState = IPlayer.started;
            mController.onPlayStateChanged(mCurrentState);
            Log.d(TAG, "STATE_PLAYING");
        } else if (mCurrentState == IPlayer.completion
                   || mCurrentState == IPlayer.error) {
            //如果是完成播放或者播放错误，则重新播放
            mMediaPlayer.reset();
            openMediaPlayer();
        } else {
            Log.d(TAG, "VideoPlayer在mCurrentState == " + mCurrentState + "时不能调用restart()方法.");
        }
    }


    /**
     * 暂停播放
     */
    @Override
    public void pause() {
        if (mCurrentState == IPlayer.started) {
            //如果是播放状态，那么则暂停播放
            mMediaPlayer.pause();
            mCurrentState = IPlayer.paused;
            mController.onPlayStateChanged(mCurrentState);
            Log.d(TAG, "STATE_PAUSED");
        }
    }


    /**
     * 判断是否开始播放
     *
     * @return true表示播放未开始
     */
    @Override
    public boolean isIdle() {
        return mCurrentState == IPlayer.idle;
    }


    /**
     * 判断视频是否播放准备中
     *
     * @return true表示播放准备中
     */
    @Override
    public boolean isPreparing() {
        return mCurrentState == IPlayer.initalized;
    }


    /**
     * 判断视频是否准备就绪
     *
     * @return true表示播放准备就绪
     */
    @Override
    public boolean isPrepared() {
        return mCurrentState == IPlayer.prepared;
    }


    /**
     * 判断视频是否正在播放
     *
     * @return true表示正在播放
     */
    @Override
    public boolean isPlaying() {
        return mCurrentState == IPlayer.started;
    }


    /**
     * 判断视频是否暂停播放
     *
     * @return true表示暂停播放
     */
    @Override
    public boolean isPaused() {
        return mCurrentState == IPlayer.paused;
    }


    /**
     * 判断视频是否播放错误
     *
     * @return true表示播放错误
     */
    @Override
    public boolean isError() {
        return mCurrentState == IPlayer.error;
    }


    /**
     * 判断视频是否播放完成
     *
     * @return true表示播放完成
     */
    @Override
    public boolean isCompleted() {
        return mCurrentState == IPlayer.completion;
    }


    /**
     * 获取当前播放模式
     *
     * @return 返回当前播放模式
     */
    public int getCurrentState() {
        return mCurrentState;
    }


    /**
     * 初始化视频管理器
     */
    private void initMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = AliPlayerFactory.createAliPlayer(mContext);
        }
        mMediaPlayer.setLoop(true);
        mMediaPlayer.setAutoPlay(false);
    }

    /**
     * 初始化TextureView
     * 这个主要是用作视频的
     */
    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initTextureView() {
        if (mTextureView == null) {
            mTextureView = new VideoTextureView(mContext);
            mTextureView.setOnSurfaceListener(new OnSurfaceListener() {
                @Override
                public void onSurfaceAvailable(SurfaceTexture surface) {
                    if (mSurfaceTexture == null) {
                        mSurfaceTexture = surface;
                        openMediaPlayer();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mTextureView.setSurfaceTexture(mSurfaceTexture);
                        }
                    }
                }

                @Override
                public void onSurfaceSizeChanged(SurfaceTexture surface, int width, int height) {

                }

                @Override
                public boolean onSurfaceDestroyed(SurfaceTexture surface) {
                    return mSurfaceTexture == null;
                }

                @Override
                public void onSurfaceUpdated(SurfaceTexture surface) {

                }
            });
        }
        mTextureView.addTextureView(mContainer, mTextureView);
    }


    /**
     * 打开AivcPlayer播放器
     */
    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void openMediaPlayer() {
        // 屏幕常亮
        mContainer.setKeepScreenOn(true);
        // 设置监听，可以查看ijk中的IMediaPlayer源码监听事件
        // 设置准备视频播放监听事件
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        // 设置视频播放完成监听事件
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);

        // 设置视频大小更改监听器
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        // 设置视频错误监听器
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        //
        mMediaPlayer.setOnStateChangedListener(mOnStateChangedListener);
        // 设置dataSource
        if (mVidSts == null || mVidSts.getVid().length() == 0) {
            ToastUtils.show(mContext, mContext.getResources().getString(R.string.alivc_longvideo_video_info));
            return;
        }

        boolean mSpSettingIsVip = new SettingSpUtils.Builder(mContext).create().getVip();
        VidPlayerConfigGen vidPlayerConfigGen = new VidPlayerConfigGen();
        if (!mSpSettingIsVip) {
            vidPlayerConfigGen.addPlayerConfig("PlayDomain", PLAY_DOMAIN);
            vidPlayerConfigGen.setPreviewTime(TRAILER);
        }
        mVidSts.setPlayConfig(vidPlayerConfigGen);
        mMediaPlayer.setDataSource(mVidSts);
        if (mSurface == null) {
            mSurface = new Surface(mSurfaceTexture);
        }
        // 设置surface
        mMediaPlayer.setSurface(mSurface);
        // 开始加载
        mMediaPlayer.prepare();
        // 播放准备中
        mCurrentState = IPlayer.initalized;
        // 控制器，更新不同的播放状态的UI
        mController.onPlayStateChanged(mCurrentState);
        Log.d(TAG, "STATE_PREPARING");
    }

    /**
     * 设置准备视频播放监听事件
     */
    private IPlayer.OnPreparedListener mOnPreparedListener = new IPlayer.OnPreparedListener() {
        @Override
        public void onPrepared() {
            mCurrentState = IPlayer.prepared;
            mController.onPlayStateChanged(mCurrentState);
            Log.d(TAG, "onPrepared ——> STATE_PREPARED");
            mMediaPlayer.start();
        }

    };
    /**
     * 设置视频播放完成监听事件
     */
    private IPlayer.OnCompletionListener mOnCompletionListener =
    new IPlayer.OnCompletionListener() {
        @Override
        public void onCompletion() {
            mCurrentState = IPlayer.completion;
            mController.onPlayStateChanged(mCurrentState);
            Log.d(TAG, "onCompletion ——> STATE_COMPLETED");
            // 清除屏幕常亮
            mContainer.setKeepScreenOn(false);
        }
    };

    /**
     * 设置视频大小更改监听器
     */
    private IPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new IPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(int width, int height) {
            mTextureView.adaptVideoSize(width, height);
            Log.d(TAG, "onVideoSizeChanged ——> width：" + width + "， height：" + height);
        }
    };

    /**
     * 设置视频错误监听器
     */
    private IPlayer.OnErrorListener mOnErrorListener = new IPlayer.OnErrorListener() {


        @Override
        public void onError(ErrorInfo errorInfo) {
            ToastUtils.show(mContext, errorInfo.getMsg());
            if (errorInfo.getCode().getValue() == IPlayer.error) {
                mCurrentState = IPlayer.error;

            }
            Log.d(TAG, "onError ——> STATE_ERROR ———— what：" + errorInfo.getExtra() + ", extra: " + errorInfo.getMsg());
        }
    };
    /**
     * 视频播放状态
     */
    private IPlayer.OnStateChangedListener mOnStateChangedListener = new IPlayer.OnStateChangedListener() {
        @Override
        public void onStateChanged(int newState) {
            mCurrentState = newState;
        }
    };

    /**
     * 释放，内部的播放器被释放掉
     * 1.释放播放器
     * 2.恢复控制器
     * 3.gc回收
     */
    @Override
    public void release() {
        // 释放播放器
        releasePlayer();

        // 恢复控制器
        if (mController != null) {
            mController.reset();
        }
        // gc回收
        Runtime.getRuntime().gc();
    }

    /**
     * 释放播放器，注意一定要判断对象是否为空，增强严谨性
     */
    @Override
    public void releasePlayer() {
        if (mMediaPlayer != null) {
            //释放视频焦点
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mContainer != null) {
            //从视图中移除TextureView
            mContainer.removeView(mTextureView);
        }
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        //如果SurfaceTexture不为null，则释放
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        mCurrentState = IPlayer.idle;
    }


}
