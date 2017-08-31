package org.jay.androidpickimage.module;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by jay on 2017/8/11.
 */

public class ImageModule extends BmobObject {
    private String url;
    private List<String> urls;

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
