package com.aliyun.solution.longvideo.view.Viewplayer.listener;


import android.graphics.SurfaceTexture;

public interface OnSurfaceListener {

    void onSurfaceAvailable(SurfaceTexture surface);

    void onSurfaceSizeChanged(SurfaceTexture surface, int width, int height);

    boolean onSurfaceDestroyed(SurfaceTexture surface);

    void onSurfaceUpdated(SurfaceTexture surface);

}
