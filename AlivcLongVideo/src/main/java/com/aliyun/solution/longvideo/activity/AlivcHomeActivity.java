package com.aliyun.solution.longvideo.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aliyun.player.alivcplayerexpand.util.database.DatabaseHelper;
import com.aliyun.player.alivcplayerexpand.util.database.DatabaseManager;
import com.aliyun.player.alivcplayerexpand.util.database.LongVideoDatabaseManager;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadManager;
import com.aliyun.solution.longvideo.BuildConfig;
import com.aliyun.solution.longvideo.R;
import com.aliyun.solution.longvideo.adapter.AlivcFragmentPagerAdapter;
import com.aliyun.solution.longvideo.base.GlobalNetConstants;
import com.aliyun.solution.longvideo.bean.UpDateVersionBean;
import com.aliyun.solution.longvideo.fragment.AlivcHomeFragment;
import com.aliyun.solution.longvideo.fragment.AlivcMineFragment;
import com.aliyun.solution.longvideo.fragment.AlivcVipFragment;
import com.aliyun.solution.longvideo.utils.SettingSpUtils;
import com.aliyun.svideo.common.bottomnavigationbar.BottomNavigationBar;
import com.aliyun.svideo.common.bottomnavigationbar.BottomNavigationEntity;
import com.aliyun.svideo.common.okhttp.AlivcOkHttpClient;
import com.aliyun.svideo.common.utils.PermissionUtils;
import com.aliyun.svideo.common.utils.upgrade.AutoUpgradeClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class AlivcHomeActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_CODE = 1000;

    private BottomNavigationBar mBottomNavigationBar;
    private AliyunDownloadManager mAliyunDownloadManager;
    private ViewPager mViewPager;
    private List<BottomNavigationEntity> entities = new ArrayList<>();
    /**
     * 底部的fragment
     */
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    /**
     * 底部fragment的名称
     */
    private ArrayList<String> mTitles = new ArrayList<>();
    /**
     * 权限申请
     */
    String[] permission = {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private SettingSpUtils spUtils;
    private long onBackPressedTime;
    private AlertDialog mPermissionAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alivc_long_video_activity_home);
        spUtils = new SettingSpUtils.Builder(this).create();
        upDateVersion();
        initView();
        initData();
        initPagerAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean checkResult = PermissionUtils.checkPermissionsGroup(this, permission);
        if (!checkResult) {
            PermissionUtils.requestPermissions(this, permission, PERMISSION_REQUEST_CODE);
        }else{
            init();
        }
    }

    private void init(){
        initDataBase();
    }

    private void initDataBase(){
        DatabaseManager.getInstance().createDataBase(this, DatabaseHelper.DB_PATH);
        LongVideoDatabaseManager.getInstance().createDataBase(this);
        mAliyunDownloadManager = AliyunDownloadManager.getInstance(getApplicationContext());

        startDownLoad();
    }

    private void initPagerAdapter(){
        AlivcFragmentPagerAdapter pagerAdapter = new AlivcFragmentPagerAdapter(getSupportFragmentManager(), mFragments, mTitles);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mBottomNavigationBar.setEntities(entities);
        mBottomNavigationBar.setBnbItemSelectListener(new BottomNavigationBar.IBnbItemSelectListener() {
            @Override
            public void onBnbItemSelect(int position) {
                Log.i("scar", "onBnbItemSelect: " + position);
                mViewPager.setCurrentItem(position);
            }
        });
        mViewPager.setCurrentItem(0);
        mBottomNavigationBar.setCurrentPosition(0);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBottomNavigationBar.setCurrentPosition(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    /**
     *设置下载个数
     */
    private void startDownLoad() {
        //同时下载数量
        String number = spUtils.getVideoNumber();
        int num = 1;
        if (!TextUtils.isEmpty(number)) {
            num = Integer.valueOf(spUtils.getVideoNumber());
        }
        mAliyunDownloadManager.setMaxNum(num);

    }

    private void initData() {
        mTitles.add(getResources().getString(R.string.alivc_longvideo_home_title));
        mTitles.add(getResources().getString(R.string.alivc_longvideo_VIP_title));
        mTitles.add(getResources().getString(R.string.alivc_longvideo_mine_title));

        mFragments.add(AlivcHomeFragment.getInstance());
        mFragments.add(AlivcVipFragment.getInstance());
        mFragments.add(AlivcMineFragment.getInstance());
    }

    private void initView() {
        mBottomNavigationBar = findViewById(R.id.alivc_bottomNavigationBar);
        mViewPager = findViewById(R.id.alivc_viewpager);

        BottomNavigationEntity homeEntity = new BottomNavigationEntity(
                R.drawable.aivc_longvideo_icon_homepage_un_checked,
                R.drawable.aivc_longvideo_icon_homepage_checked);
        BottomNavigationEntity vipEntity = new BottomNavigationEntity(
                R.drawable.aivc_longvideo_icon_vip_un_checked,
                R.drawable.aivc_longvideo_icon_vip_checked);
        BottomNavigationEntity mineEntity = new BottomNavigationEntity(
                R.drawable.aivc_longvideo_icon_mine_un_checked,
                R.drawable.aivc_longvideo_icon_mine_checked);

        homeEntity.setText(getResources().getString(R.string.alivc_longvideo_home_title));
        vipEntity.setText(getResources().getString(R.string.alivc_longvideo_VIP_title));
        mineEntity.setText(getResources().getString(R.string.alivc_longvideo_mine_title));

        entities.add(homeEntity);
        entities.add(vipEntity);
        entities.add(mineEntity);
    }

    /**
     * 检查是否更新版本
     */
    private void upDateVersion() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("toolKitName", "longVideo");
        hashMap.put("type", "1");
        AlivcOkHttpClient.getInstance().get(GlobalNetConstants.GET_UPDATE_VERSION, hashMap, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                Log.e("scar", "onError: " + e.getMessage());
            }

            @Override
            public void onSuccess(Request request, String result) {
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                UpDateVersionBean upDateVersionBean = gson.fromJson(result, UpDateVersionBean.class);
                AutoUpgradeClient.setUpgradeJsonBaseUrl(upDateVersionBean.getData().getUrl());
                AutoUpgradeClient.checkUpgrade(AlivcHomeActivity.this, "", BuildConfig.VERSION_CODE);

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (!isAllGranted) {
                showPermissionDialog();
            }else{
                // 如果所有的权限都授予了
                init();
            }
        }
    }

    //系统授权设置的弹框
    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mPermissionAlertDialog = builder.setMessage(getString(R.string.app_name) + getString(R.string.alivc_longvideo_request_permission_content_text))
                .setPositiveButton(R.string.alivc_longvideo_request_permission_positive_btn_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                        mPermissionAlertDialog.dismiss();

                    }
                })
                .setNegativeButton(R.string.alivc_longvideo_request_permission_negative_btn_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .create();
        mPermissionAlertDialog.show();
    }

    @Override
    public void onBackPressed() {
        long timeSpan = System.currentTimeMillis() - onBackPressedTime;
        onBackPressedTime = System.currentTimeMillis();
        if (timeSpan > 2000) {
            Toast.makeText(this, getResources().getString(R.string.alivc_longvideo_exit_application), Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAliyunDownloadManager != null) {
            mAliyunDownloadManager.release();
        }
    }
}
