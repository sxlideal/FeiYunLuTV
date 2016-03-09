package com.mediamaster.ffmpegwrap;

/**
 * Created by paladin on 16-2-3.
 */
public class NativeScreenRecord {

    static {

        System.loadLibrary("avutil-52");
        System.loadLibrary("avcodec-55");
        System.loadLibrary("avformat-55");

        System.loadLibrary("swresample-0");
        System.loadLibrary("swscale-2");
        System.loadLibrary("avfilter-4");
        System.loadLibrary("avdevice-55");
//        System.loadLibrary("screenrecord_sdk");
//        System.loadLibrary("c++");
        System.loadLibrary("screenrecord_jni");
    }


    public native int nativeInit();
    private native int nativeConnRtmp(String url);
    private native int nativeStart();
    private native void nativeStop();


    //AVOptions mAvOptions;

    public void init(){
        nativeInit();
    }
    public int connectRtmp(String url) {return nativeConnRtmp(url);}

    public void start() {
        nativeStart();
    }

    public void stop() {
        nativeStop();
    }


    public NativeScreenRecord() {
        init();
    }



}
