/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.alivcsolution;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.aliyun.solution.longvideo.bean.RandomUserBean;
import com.aliyun.solution.longvideo.utils.UserSpUtils;
import com.aliyun.svideo.common.okhttp.AlivcOkHttpClient;
import com.aliyun.svideo.common.utils.ThreadUtils;
import com.aliyun.svideo.common.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Request;

import static com.aliyun.solution.longvideo.base.GlobalNetConstants.GET_USER_INFO;


/**
 * @author Mulberry
 */
public class SplashActivity extends Activity {
    private static final String LOG_TAG = "AlivcQuVideo";
    /**
     * 动画时间 2000ms
     */
    private static final int ANIMATOR_DURATION = 2000;

    /**
     * 动画样式-- 透明度动画
     */
    private static final String ANIMATOR_STYLE = "alpha";

    /**
     * 动画起始值
     */
    private static final float ANIMATOR_VALUE_START = 0f;

    /**
     * 动画结束值
     */
    private static final float ANIMATOR_VALUE_END = 1f;
    private ObjectAnimator alphaAnimIn;
    /**
     * 开始动画是否结束
     */
    private boolean isAniminEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadUser();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_spalash);
        LinearLayout splashView = findViewById(R.id.splash_view);

        alphaAnimIn = ObjectAnimator.ofFloat(splashView, ANIMATOR_STYLE, ANIMATOR_VALUE_START, ANIMATOR_VALUE_END);

        alphaAnimIn.setDuration(ANIMATOR_DURATION);

        alphaAnimIn.start();
        alphaAnimIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAniminEnd = true;
                tryJumpToMain();

            }
        });

    }

    /**
     * 尝试跳转到主界面
     */
    private void tryJumpToMain() {
        if (isAniminEnd) {
            Intent intent = new Intent();
            intent.setClassName(SplashActivity.this, "com.aliyun.solution.longvideo.activity.AlivcHomeActivity");
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (alphaAnimIn != null) {

            alphaAnimIn.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alphaAnimIn != null) {
            alphaAnimIn.cancel();
            alphaAnimIn.removeAllListeners();
            alphaAnimIn = null;
        }

    }

    /**
     * 加载用户信息
     */
    private void loadUser() {
        AlivcOkHttpClient.getInstance().get(GET_USER_INFO, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                Log.e("TAG", "onError: " + e.getMessage());
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.show(SplashActivity.this, R.string.aliyun_net_error);
                    }
                });
            }

            @Override
            public void onSuccess(Request request, String result) {
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                RandomUserBean userBean = gson.fromJson(result, RandomUserBean.class);
                new UserSpUtils.Builder(SplashActivity.this)
                .userAvatar(userBean.getData().getAvatarUrl())
                .userID(userBean.getData().getUserId())
                .userNickName(userBean.getData().getNickName())
                .userToken(userBean.getData().getToken());
            }
        });

    }
}
