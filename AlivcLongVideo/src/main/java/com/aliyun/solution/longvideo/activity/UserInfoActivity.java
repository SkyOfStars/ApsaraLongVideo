package com.aliyun.solution.longvideo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.solution.longvideo.R;
import com.aliyun.svideo.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.common.utils.image.ImageLoaderOptions;


/**
 * user information
 */
public class UserInfoActivity extends AppCompatActivity {

    public static final String KEY_TITLE = "key_title";
    public static final String KEY_HEADER_URL = "key_header_url";

    private ImageView mIvTitle;
    private TextView mNickName;

    private String mTitle;
    private String mHeaderUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alivc_long_video_activity_user_info);
        getData();
        initView();
        setTitle();
    }

    private void getData() {
        try {
            mTitle = getIntent().getStringExtra(KEY_TITLE);
            mHeaderUrl = getIntent().getStringExtra(KEY_HEADER_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mIvTitle = findViewById(R.id.alivc_iv_header);
        mNickName = findViewById(R.id.alivc_tv_nickname);
        mNickName.setText(mTitle);
        new ImageLoaderImpl().loadImage(UserInfoActivity.this, mHeaderUrl, new ImageLoaderOptions.Builder()
                                        .circle()
                                        .error(R.mipmap.ic_launcher)
                                        .crossFade().build()).into(mIvTitle);

    }

    private void setTitle() {
        TextView tvTitle = findViewById(R.id.alivc_base_tv_middle_title);
        TextView tvRight = findViewById(R.id.alivc_base_tv_right_edit);
        tvRight.setVisibility(View.GONE);
        tvTitle.setText(getResources().getString(R.string.alivc_longvideo_user_info));
        findViewById(R.id.alivc_base_fl_left_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * user information entrance
     */
    public static void startJump(Context context, String title, String headerUrl) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_HEADER_URL, headerUrl);
        context.startActivity(intent);
    }

}
