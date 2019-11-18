package com.aliyun.solution.longvideo.bean;

public class CacheBean {
    public int number;
    public String title;
    public String image;
    public boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public CacheBean(int number, String title, String image) {
        this.number = number;
        this.title = title;
        this.image = image;
    }
}
