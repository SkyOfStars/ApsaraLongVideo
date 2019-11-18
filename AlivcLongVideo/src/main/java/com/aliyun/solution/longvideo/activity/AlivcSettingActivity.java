package com.aliyun.solution.longvideo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadManager;
import com.aliyun.solution.longvideo.R;
import com.aliyun.solution.longvideo.utils.ConvertionQuaitityUtil;
import com.aliyun.solution.longvideo.utils.SettingSpUtils;
import com.aliyun.svideo.common.base.AlivcListSelectorDialogFragment;
import com.aliyun.svideo.common.base.AlivcWheelDialogFragment;
import com.aliyun.svideo.common.widget.SwitchButton;

import java.util.ArrayList;

/**
 * 设置页面
 */
public class AlivcSettingActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 清晰度
     */
    private TextView mTvSharpness;
    /**
     * 下载个数
     */
    private TextView mDownloadNumber;
    /**
     * 下载清晰度选择器
     */
    private AlivcListSelectorDialogFragment mAlivcListSelectorDialogFragment;
    /**
     * 下载个数选择器
     */
    private AlivcWheelDialogFragment mWheelDialogFragment;

    /**
     * 运营商switch
     */
    private SwitchButton mSbOperator;
    /**
     * 自动播放switch
     */
    private SwitchButton mSbAutoPlay;
    /**
     * 硬解码switch
     */
    private SwitchButton mSbDecording;
    /**
     * 开启vip
     */
    private SwitchButton mSbVip;

    private SettingSpUtils spUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alivc_long_video_activity_setting);
        initSpHelper();
        initView();
        setTitle();
    }

    /**
     * 初始化SP辅助类
     */
    private void initSpHelper() {
        spUtils = new SettingSpUtils.Builder(AlivcSettingActivity.this).create();
    }

    private void initView() {
        findViewById(R.id.alivc_ll_sharpness).setOnClickListener(this);
        findViewById(R.id.alivc_ll_video_number).setOnClickListener(this);
        mTvSharpness = findViewById(R.id.alivc_tv_sharpness);
        mDownloadNumber = findViewById(R.id.alivc_tv_number);
        mSbOperator = findViewById(R.id.alivc_switch_operator);
        mSbAutoPlay = findViewById(R.id.alivc_switch_autoplay);
        mSbDecording = findViewById(R.id.alivc_switch_decoding);
        mSbVip = findViewById(R.id.alivc_switch_vip);

        //设置默认初始化的值
        if (spUtils.getVideoNumber() == null || spUtils.getVideoNumber().isEmpty()) {
            mDownloadNumber.setText("5");
        } else {
            mDownloadNumber.setText(spUtils.getVideoNumber());
        }
        if (spUtils.getVideoQuantity() == null || spUtils.getVideoNumber().isEmpty()) {
            mTvSharpness.setText(getResources().getString(R.string.alivc_longvideo_quality_chinese_sd));
        } else {
            mTvSharpness.setText(ConvertionQuaitityUtil.letterOrChinese(spUtils.getVideoQuantity(), AlivcSettingActivity.this));

        }
        mSbOperator.setOpened(spUtils.getOperatorDownload());
        mSbAutoPlay.setOpened(spUtils.getOperatorPlay());
        mSbDecording.setOpened(spUtils.getHardDecoding());
        mSbVip.setOpened(spUtils.getVip());

        //数据源
        ArrayList<String> selectors = new ArrayList<>();
        selectors.add(getResources().getString(R.string.alivc_longvideo_quality_chinese_sd));
        selectors.add(getResources().getString(R.string.alivc_longvideo_quality_chinese_hd));
        selectors.add(getResources().getString(R.string.alivc_longvideo_quality_chinese_ssd));

        mAlivcListSelectorDialogFragment = new AlivcListSelectorDialogFragment.Builder(getSupportFragmentManager())
        .setGravity(Gravity.BOTTOM)
        .setCancelableOutside(true)
        .setItemColor(ContextCompat.getColor(this, R.color.alivc_common_font_red_wine))
        .setUnItemColor(ContextCompat.getColor(this, R.color.alivc_common_font_black))
        .setNewData(selectors)
        .setDialogAnimationRes(R.style.Dialog_Animation)
        .setOnListItemSelectedListener(new AlivcListSelectorDialogFragment.OnListItemSelectedListener() {
            @Override
            public void onClick(String position) {
                mTvSharpness.setText(position);

            }
        })
        .create();

        mWheelDialogFragment = new AlivcWheelDialogFragment.Builder(getSupportFragmentManager())
        .dialogAnimationRes(R.style.Dialog_Animation)
        .setWheelData(getResources().getStringArray(R.array.alivc_longvideo_download_video_limit_number))
        .cancelString(getResources().getString(R.string.alivc_common_cancel))
        .sureString(getResources().getString(R.string.alivc_common_confirm))
        .onWheelDialogListener(new AlivcWheelDialogFragment.OnWheelDialogListener() {
            @Override
            public void onClickLeft(DialogFragment dialog, String value) {
                dialog.dismiss();
            }

            @Override
            public void onClickRight(DialogFragment dialog, String value) {
                mDownloadNumber.setText(value);
                dialog.dismiss();
            }

            @Override
            public void onValueChanged(DialogFragment dialog, String value) {

            }
        })
        .create();
    }


    /**
     * user setting entrance
     */
    public static void startJump(Context context) {
        Intent intent = new Intent(context, AlivcSettingActivity.class);
        context.startActivity(intent);
    }

    /**
     * setting common title
     */
    private void setTitle() {
        TextView tvTitle = findViewById(R.id.alivc_base_tv_middle_title);
        TextView tvRight = findViewById(R.id.alivc_base_tv_right_edit);
        FrameLayout flLeft = findViewById(R.id.alivc_base_fl_left_back);
        tvTitle.setText(getResources().getString(R.string.alivc_longvideo_mine_setting));
        tvRight.setText(getResources().getString(R.string.alivc_longvideo_save));
        tvRight.setOnClickListener(this);
        flLeft.setOnClickListener(this);

    }

    /**
     * 保存设置的属性
     */
    private void saveSetting() {
        new SettingSpUtils.Builder(AlivcSettingActivity.this)
        .saveVideoNumber(mDownloadNumber.getText().toString().trim())
        .saveVideoQuantity(ConvertionQuaitityUtil.letterOrChinese(mTvSharpness.getText().toString().trim(), AlivcSettingActivity.this))
        .saveIsOperatorDownload(mSbOperator.isOpened())
        .saveIsOperatorPlay(mSbAutoPlay.isOpened())
        .saveIsHardDecoding(mSbDecording.isOpened())
        .saveIsVip(mSbVip.isOpened());

        startDownLoad();
    }

    /**
     * 设置下载个数
     */
    private void startDownLoad() {
        //同时下载数量
        String number = spUtils.getVideoNumber();
        int num = 1;
        if (!TextUtils.isEmpty(number)) {
            num = Integer.valueOf(spUtils.getVideoNumber());
        }
        AliyunDownloadManager instance = AliyunDownloadManager.getInstance(getApplicationContext());

        instance.setMaxNum(num);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.alivc_ll_sharpness) {
            mAlivcListSelectorDialogFragment.show();
            mAlivcListSelectorDialogFragment.setPosition(mTvSharpness.getText().toString().trim());
        } else if (v.getId() == R.id.alivc_ll_video_number) {
            mWheelDialogFragment.show();
            //保存
        } else if (v.getId() == R.id.alivc_base_tv_right_edit) {
            saveSetting();
            finish();
        } else if (v.getId() == R.id.alivc_base_fl_left_back) {
            finish();
        }
    }
}
