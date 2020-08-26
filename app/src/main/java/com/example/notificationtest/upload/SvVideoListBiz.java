package com.example.notificationtest.upload;

/**
 * Created by lining on 2017/5/26.
 * 视频列表也是逻辑里
 */

public class SvVideoListBiz {

    private static SvVideoListBiz videoListBiz;
    public static SvVideoListBiz getInstance() {
        if (videoListBiz == null) {
            synchronized (SvVideoListBiz.class) {
                if (videoListBiz == null)
                    videoListBiz = new SvVideoListBiz();
            }
        }
        return videoListBiz;
    }


}
