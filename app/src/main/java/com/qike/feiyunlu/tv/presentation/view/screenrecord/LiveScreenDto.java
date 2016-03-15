package com.qike.feiyunlu.tv.presentation.view.screenrecord;

import java.io.Serializable;

/**
 * Created by cherish on 2016/3/11.
 */
public class LiveScreenDto implements Serializable{

    private String rtmp_url;
    /** 视频高 720 480 320*/
    private int height;
    /** 视频宽 */
    private int width;
    /** 视频比特率 */
    private int bitrate;
    /** 视频帧率 */
    private int frame;
    /** 视频横屏为0，竖屏为1 */
    private int orientation;

    private String filepath;

    private int sharpness;//清晰度

    public String getRtmp_url() {
        return rtmp_url;
    }

    public void setRtmp_url(String rtmp_url) {
        this.rtmp_url = rtmp_url;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getSharpness() {
        return sharpness;
    }

    public void setSharpness(int sharpness) {
        this.sharpness = sharpness;
    }


    @Override
    public String toString() {
        return "LiveScreenDto{" +
                "rtmp_url='" + rtmp_url + '\'' +
                ", height=" + height +
                ", width=" + width +
                ", bitrate=" + bitrate +
                ", frame=" + frame +
                ", orientation=" + orientation +
                ", filepath='" + filepath + '\'' +
                ", sharpness=" + sharpness +
                '}';
    }
}
