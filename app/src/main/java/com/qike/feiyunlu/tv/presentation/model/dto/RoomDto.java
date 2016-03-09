package com.qike.feiyunlu.tv.presentation.model.dto;

/**
 * Created by cherish on 2016/3/9.
 */
public class RoomDto {


    private String publish_url;

    private String rtmp_live_url;

    private String m3u8;

    private String flv;

    public String getPublish_url() {
        return publish_url;
    }

    public void setPublish_url(String publish_url) {
        this.publish_url = publish_url;
    }

    public String getRtmp_live_url() {
        return rtmp_live_url;
    }

    public void setRtmp_live_url(String rtmp_live_url) {
        this.rtmp_live_url = rtmp_live_url;
    }

    public String getM3u8() {
        return m3u8;
    }

    public void setM3u8(String m3u8) {
        this.m3u8 = m3u8;
    }

    public String getFlv() {
        return flv;
    }

    public void setFlv(String flv) {
        this.flv = flv;
    }
}
