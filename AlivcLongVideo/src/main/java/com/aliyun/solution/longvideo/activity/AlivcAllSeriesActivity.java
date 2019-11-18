package com.aliyun.solution.longvideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;
import com.aliyun.solution.longvideo.R;
import com.aliyun.solution.longvideo.adapter.AlivcAllSeriesQuickAdapter;
import com.aliyun.svideo.common.baseAdapter.BaseQuickAdapter;

import java.util.ArrayList;

/**
 * 全部剧集
 */
public class AlivcAllSeriesActivity extends AppCompatActivity {

    private RecyclerView mSeriesAllRecyclerView;
    private AlivcAllSeriesQuickAdapter alivcAllSeriesQuickAdapter;

    /**
     * 标题 Middle,标题 right,全部剧集
     */
    private TextView mBaseMiddleTitleTextView, mBaseRightTitleTextView, mSeriesAllTextView;
    /**
     * 当前正在播放的集数
     */
    private String mCurrentPlayintPosition;

    private ArrayList<LongVideoBean> mLongVideoBeanList;
    /**
     * 返回按钮
     */
    private FrameLayout mTitleLeftBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alivc_long_video_activity_series_all);

        mLongVideoBeanList = (ArrayList<LongVideoBean>) getIntent().getSerializableExtra(AlivcPlayerActivity.INTENT_SERIES_VIDEOS);
        mCurrentPlayintPosition = getIntent().getStringExtra(AlivcPlayerActivity.INTENT_CURRENT_PLAYING_POSITION);

        initView();
        initRecyclerView();
        initListener();
    }

    private void initView() {
        mTitleLeftBack = findViewById(R.id.alivc_base_fl_left_back);
        mSeriesAllTextView = findViewById(R.id.tv_series_all);
        mSeriesAllRecyclerView = findViewById(R.id.recyclerview_series_all);
        mBaseRightTitleTextView = findViewById(R.id.alivc_base_tv_right_edit);
        mBaseMiddleTitleTextView = findViewById(R.id.alivc_base_tv_middle_title);

        mBaseRightTitleTextView.setVisibility(View.GONE);
        mBaseMiddleTitleTextView.setText(R.string.alivc_longvideo_all_series_title);
        mSeriesAllTextView.setText(String.format(getResources().getString(R.string.alivc_longvideo_series_all_number_list),
                                   mLongVideoBeanList == null ? 0 : mLongVideoBeanList.size()));
    }

    private void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 6);
        mSeriesAllRecyclerView.setLayoutManager(gridLayoutManager);

        alivcAllSeriesQuickAdapter = new AlivcAllSeriesQuickAdapter(R.layout.alivc_long_video_series_player_episode_item,
                mLongVideoBeanList, mCurrentPlayintPosition);
        mSeriesAllRecyclerView.setAdapter(alivcAllSeriesQuickAdapter);
    }

    private void initListener() {
        alivcAllSeriesQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Intent intent = new Intent();
                intent.putExtra("result", mLongVideoBeanList.get(position));
                setResult(1, intent);
                finish();
            }
        });

        mTitleLeftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
