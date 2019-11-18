
package com.aliyun.solution.longvideo.view.Viewplayer.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.player.IPlayer;
import com.aliyun.player.alivcplayerexpand.util.TimeFormater;
import com.aliyun.solution.longvideo.R;
import com.aliyun.solution.longvideo.utils.SettingSpUtils;
import com.aliyun.solution.longvideo.view.Viewplayer.listener.InterVideoPlayer;
import com.aliyun.solution.longvideo.view.Viewplayer.listener.OnCompletedListener;
import com.aliyun.solution.longvideo.view.Viewplayer.listener.OnPlayOrPauseListener;
import com.aliyun.svideo.common.utils.NetWatchdogUtils;


/**
 * desc  : 播放器控制器，主要是处理UI操作逻辑
 * 注意：先判断状态，再进行设置参数
 */
public class VideoPlayerController extends BaseVideoPlayerController implements View.OnClickListener {

    private Context mContext;
    private ImageView mImage;
    private ImageView mALivcPause;
    private ImageView mCenterStart;
    private TextView mLength;
    private RelativeLayout mRoot;
    private TextView mTvVIp;
    private TextView mTvVIpTitle;
    private TextView mTvVIpType;

    /**
     * 是否锁屏
     */
    private boolean mIsLock = false;
    /**
     * 设置视频播放器中间的播放键是否显示
     * 默认为false，不显示
     */
    private boolean mIsCenterPlayerVisibility = false;

    /**
     * 获取用户设置页面信息
     */
    private SettingSpUtils mSettingSpUtils;


    public VideoPlayerController(Context context) {
        super(context);
        mContext = context;
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    /**
     * 初始化操作
     */
    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.alivc_custom_video_player, this, true);

        initFindViewById();
        initListener();


    }

    private void initFindViewById() {
        mSettingSpUtils = new SettingSpUtils.Builder(mContext).create();
        mCenterStart = findViewById(R.id.center_start);
        mImage = findViewById(R.id.alivc_iv_preview);
        mLength = findViewById(R.id.length);
        //暂停
        mALivcPause = findViewById(R.id.alivc_iv_pause);
        mRoot = findViewById(R.id.alivc_root_view);
        mTvVIp = findViewById(R.id.alivc_tv_vip);
        mTvVIpTitle = findViewById(R.id.alivc_tv_vip_title);
        //视频类型
        mTvVIpType = findViewById(R.id.alivc_tv_video_type);
    }


    private void initListener() {
        mCenterStart.setOnClickListener(this);
        mRoot.setOnClickListener(this);
        mALivcPause.setOnClickListener(this);
        this.setOnClickListener(this);
    }


    /**
     * 设置视频标题
     *
     * @param title 视频标题
     */
    @Override
    public void setTitle(@NonNull String title) {
        mTvVIpTitle.setText(title);
    }

    /**
     * 获取ImageView的对象
     *
     * @return 对象
     */
    @Override
    public ImageView imageView() {
        return mImage;
    }

    /**
     * 是否vip
     */
    @Override
    public void isVip(boolean vip) {
        if (vip) {
            mTvVIp.setVisibility(VISIBLE);
        } else {
            mTvVIp.setVisibility(GONE);
        }
    }

    /**
     * 是剧集还是系列
     */
    @Override
    public void setVideoType(boolean videoType) {
        if (mTvVIpType != null) {
            mTvVIpType.setVisibility(VISIBLE);
            if (videoType) {
                mTvVIpType.setText(mContext.getResources().getString(R.string.alivc_longvideo_singleset_title));
            } else {
                mTvVIpType.setText(mContext.getResources().getString(R.string.alivc_longvideo_series_title));

            }
        }
    }

    /**
     * 设置图片
     *
     * @param resId 视频底图资源
     */
    @Override
    public void setImage(@DrawableRes int resId) {
        mImage.setImageResource(resId);
    }


    /**
     * 设置视频时长
     *
     * @param length 时长，long类型
     */
    @Override
    public void setLength(long length) {
        mLength.setText(TimeFormater.formatMs(length));
    }

    /**
     * 设置视频时长
     *
     * @param length 时长，String类型
     */
    @Override
    public void setLength(String length) {
        mLength.setText(length);
    }


    /**
     * 设置播放器
     *
     * @param videoPlayer 播放器
     */
    @Override
    public void setVideoPlayer(InterVideoPlayer videoPlayer) {
        super.setVideoPlayer(videoPlayer);
        // 给播放器配置视频地址
    }

    /**
     * 设置中间播放按钮是否显示，并且支持设置自定义图标
     *
     * @param isVisibility 是否可见，默认不可见
     * @param image        image
     */
    @Override
    public void setCenterPlayer(boolean isVisibility, @DrawableRes int image) {
        this.mIsCenterPlayerVisibility = isVisibility;
        if (isVisibility) {
            if (image == 0) {
                mCenterStart.setImageResource(R.drawable.alivc_longvideo_icon_player);
            } else {
                mCenterStart.setImageResource(image);
            }
        }
    }


    /**
     * 获取是否是锁屏模式
     *
     * @return true表示锁屏
     */
    @Override
    public boolean getLock() {
        return mIsLock;
    }

    /**
     * 如果锁屏，则屏蔽滑动事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //如果锁屏了，那就就不需要处理滑动的逻辑
        return !getLock() && super.onTouchEvent(event);
    }


    /**
     * 当播放状态发生改变时
     *
     * @param playState 播放状态：
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
        case IPlayer.idle:
            break;
        //播放准备中
        case IPlayer.initalized:
            //  startPreparing();
            break;
        //播放准备就绪
        case IPlayer.prepared:
            startPreparing();
            break;
        //正在播放
        case IPlayer.started:
            mCenterStart.setVisibility(View.GONE);
            mALivcPause.setVisibility(GONE);
            break;
        //暂停播放
        case IPlayer.paused:
            mALivcPause.setVisibility(VISIBLE);
            mCenterStart.setVisibility(mIsCenterPlayerVisibility ? View.VISIBLE : View.GONE);
            break;
        //播放错误
        case IPlayer.error:
            stateError();
            break;
        //播放完成
        case IPlayer.completion:
            stateCompleted();
            break;
        default:
            break;
        }
    }


    /**
     * 播放准备中
     */
    private void startPreparing() {
        mImage.setVisibility(View.GONE);
        mCenterStart.setVisibility(View.GONE);
        mALivcPause.setVisibility(GONE);
        mLength.setVisibility(View.GONE);
        mTvVIp.setVisibility(GONE);
        mTvVIpTitle.setVisibility(GONE);
    }


    /**
     * 播放错误
     */
    private void stateError() {
    }

    /**
     * 播放完成
     */
    private void stateCompleted() {
        mImage.setVisibility(View.VISIBLE);
        //设置播放完成的监听事件
        if (mOnCompletedListener != null) {
            mOnCompletedListener.onCompleted();
        }
    }


    /**
     * 重新设置
     */
    @Override
    public void reset() {
        mCenterStart.setVisibility(VISIBLE);
        mLength.setVisibility(View.VISIBLE);
        mTvVIpTitle.setVisibility(VISIBLE);
        mTvVIp.setVisibility(VISIBLE);
        mImage.setVisibility(View.VISIBLE);
        mALivcPause.setVisibility(GONE);

    }

    /**
     * 注意：跟重置有区别
     * 控制器意外销毁，比如手动退出，意外崩溃等等
     */
    @Override
    public void destroy() {
    }

    /**
     * 尽量不要在onClick中直接处理控件的隐藏、显示及各种UI逻辑。
     * UI相关的逻辑都尽量到{@link #onPlayStateChanged}和{@link }中处理.
     */
    @Override
    public void onClick(View v) {
        if (v == mCenterStart) {
            //如果是手机网络，提示一下&&如果选中了运营商网络播放，则不提示
            if (NetWatchdogUtils.is4GConnected(mContext) && !mSettingSpUtils.getOperatorPlay()) {
                showDialog();
            } else {
                //开始播放
                if (mVideoPlayer.isIdle()) {
                    mVideoPlayer.start();
                } else if (mVideoPlayer.isPlaying()) {
                    mVideoPlayer.pause();
                } else if (mVideoPlayer.isPaused()) {
                    mVideoPlayer.restart();
                }
            }
        } else if (v == mRoot) {
            if (mVideoPlayer.isPlaying()) {
                mVideoPlayer.pause();
                if (mOnPlayOrPauseListener != null) {
                    mOnPlayOrPauseListener.onPlayOrPauseClick(true);
                }
            } else if (mVideoPlayer.isPaused()) {
                mVideoPlayer.restart();
                if (mOnPlayOrPauseListener != null) {
                    mOnPlayOrPauseListener.onPlayOrPauseClick(false);
                }
            }
        }
    }


    /**
     * 播放暂停监听事件
     */
    private OnPlayOrPauseListener mOnPlayOrPauseListener;

    public void setOnPlayOrPauseListener(OnPlayOrPauseListener listener) {
        this.mOnPlayOrPauseListener = listener;
    }

    /**
     * 监听视频播放完成事件
     */
    private OnCompletedListener mOnCompletedListener;

    public void setOnCompletedListener(OnCompletedListener listener) {
        this.mOnCompletedListener = listener;
    }

    /**
     * 4G网络的时候每次都提示
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder
        .setMessage(mContext.getResources().getString(R.string.alivc_longvideo_moble_operator_dialog_message))
        .setPositiveButton(mContext.getResources().getString(R.string.alivc_longvideo_dialog_continue), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //开始播放
                if (mVideoPlayer.isIdle()) {
                    mVideoPlayer.start();
                } else if (mVideoPlayer.isPlaying()) {
                    mVideoPlayer.pause();
                } else if (mVideoPlayer.isPaused()) {
                    mVideoPlayer.restart();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(mContext.getResources().getString(R.string.alivc_common_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        builder.show();
    }

}
