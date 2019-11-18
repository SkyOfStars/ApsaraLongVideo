package com.aliyun.solution.longvideo.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.solution.longvideo.R;
import com.aliyun.solution.longvideo.bean.VipListStsBean;
import com.aliyun.solution.longvideo.view.Viewplayer.VideoPlayer;
import com.aliyun.solution.longvideo.view.Viewplayer.controller.VideoPlayerController;
import com.aliyun.solution.longvideo.view.Viewplayer.manager.VideoPlayerManager;
import com.aliyun.svideo.common.baseAdapter.BaseQuickAdapter;
import com.aliyun.svideo.common.baseAdapter.BaseViewHolder;
import com.aliyun.svideo.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.common.utils.image.ImageLoaderOptions;

import java.util.List;

/**
 * vip视频播放列表
 */
public class AlivcVipListQuickAdapter extends BaseQuickAdapter<VipListStsBean, AlivcVipListQuickAdapter.VideoViewHolder> {


    public AlivcVipListQuickAdapter(@Nullable List<VipListStsBean> data) {
        super(0, data);
    }

    /**
     * 切换界面暂停视频
     */
    public void videoPause() {
        VideoPlayerManager.instance().suspendVideoPlayer();
    }

    @Override
    protected void convert(VideoViewHolder helper, VipListStsBean item) {

        helper.bindData(item);
        new ImageLoaderImpl().loadImage(mContext, item.getVideoListBean().getCoverUrl(), new ImageLoaderOptions.Builder()
                                        .crossFade()
                                        .circle()
                                        .error(R.mipmap.ic_launcher)
                                        .build()).into(helper.mIvHeader);
//        helper.mTvName.setText(item.getVideoListBean().getTvName());
        helper.mTvName.setText(item.getVideoListBean().getTitle());
        helper.addOnClickListener(R.id.alivc_tv_enter);
    }

    @Override
    protected VideoViewHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.alivc_rv_vip_content_item, parent, false);
        VideoViewHolder holder = new VideoViewHolder(itemView);
        //创建视频播放控制器，只要创建一次就可以
        VideoPlayerController controller = new VideoPlayerController(mContext);
        holder.setController(controller);
        return holder;
    }

    public class VideoViewHolder extends BaseViewHolder {

        public VideoPlayerController mController;
        public VideoPlayer mVideoPlayer;
        public CardView mCardView;
        public TextView mTvName;
        public ImageView mIvHeader;
        public TextView mEnterDetail;

        VideoViewHolder(View itemView) {
            super(itemView);
            mVideoPlayer = (VideoPlayer) itemView.findViewById(R.id.alivc_videoplayer);
            mCardView = itemView.findViewById(R.id.alivc_cardView);
            mTvName = itemView.findViewById(R.id.alivc_tv_name);
            mIvHeader = itemView.findViewById(R.id.alivc_iv_header);
            mEnterDetail = itemView.findViewById(R.id.alivc_tv_enter);
            // 将列表中的每个视频设置为默认16:9的比例
            if (mVideoPlayer != null) {
                ViewGroup.LayoutParams params = mVideoPlayer.getLayoutParams();
                // 宽度为屏幕宽度
                params.width = itemView.getResources().getDisplayMetrics().widthPixels;
                // 高度为宽度的9/16
                params.height = (int) (params.width * 9f / 16f);
                mVideoPlayer.setLayoutParams(params);
                mCardView.setLayoutParams(params);
            }
        }

        /**
         * 设置视频控制器参数
         *
         * @param controller 控制器对象
         */
        void setController(VideoPlayerController controller) {
            mController = controller;
            mVideoPlayer.setController(mController);
        }

        void bindData(VipListStsBean video) {
            mController.setTitle(video.getVideoListBean().getTitle());
            mController.setLength((long) (Double.valueOf(video.getVideoListBean().getDuration()) * 1000L));
            mController.isVip(video.getVideoListBean().getIsVip());
            new ImageLoaderImpl().loadImage(mContext, video.getVideoListBean().getCoverUrl(), new ImageLoaderOptions.Builder()
                                            .crossFade()
                                            .error(R.mipmap.ic_launcher)
                                            .build()).into(mController.imageView());
            mVideoPlayer.setUp(video.getStsDataBean());
            //如果tvId是null的话，那么显示的是剧集，否则显示系列
            if (TextUtils.isEmpty(video.getVideoListBean().getTvId())) {
                mController.setVideoType(true);
            } else {
                mController.setVideoType(false);

            }
        }
    }
}