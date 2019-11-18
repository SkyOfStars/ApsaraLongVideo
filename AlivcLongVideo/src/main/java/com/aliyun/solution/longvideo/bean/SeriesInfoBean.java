package com.aliyun.solution.longvideo.bean;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;

import java.util.List;

/**
 * 剧集信息实体类
 */
public class SeriesInfoBean {

    private String result;
    private String message;
    private String code;
    private SeriesInfoBeanData data;

    public static class SeriesInfoBeanData{
        private List<LongVideoBean> tvPlayList;

        public List<LongVideoBean> getTvPlayList() {
            return tvPlayList;
        }

        public void setTvPlayList(List<LongVideoBean> tvPlayList) {
            this.tvPlayList = tvPlayList;
        }
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public SeriesInfoBeanData getData() {
        return data;
    }

    public void setData(SeriesInfoBeanData data) {
        this.data = data;
    }
}
