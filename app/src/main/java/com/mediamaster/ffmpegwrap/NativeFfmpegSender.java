package com.mediamaster.ffmpegwrap;

import java.nio.ByteBuffer;

/**
 * Created by paladin on 15-6-13.
 */
public class NativeFfmpegSender {

    static {

        System.loadLibrary("avutil-52");
        System.loadLibrary("avcodec-55");
        System.loadLibrary("avformat-55");

        System.loadLibrary("swresample-0");
        System.loadLibrary("swscale-2");
        System.loadLibrary("avfilter-4");
        System.loadLibrary("avdevice-55");

        System.loadLibrary("ffmpeg_jni");
    }


    public native int nativeInit();
    private native int nativeConnRtmp(String url);
    private native int nativeStart();
    private native void nativeStop();
    private native int nativeInitAudio(int channels, int sameple_rate,  byte []extract_data);
    private native int nativeInitVideo(int width, int height,  byte []extract_data);
    private native int nativeSendBuffer(int isVideo, long ts, int flags, int size, int offset, ByteBuffer extract_data);

    //AVOptions mAvOptions;

    public void init(){
        nativeInit();
    }
    public int connectRtmp(String url) {return nativeConnRtmp(url);}
    public void initAudio(int channels, int samplerate, byte []extract_data) {
        nativeInitAudio(channels, samplerate, extract_data);
    }


    public void initVideo(int width, int height, byte []extract_data) {
        nativeInitVideo(width, height, extract_data);
    }



    public  void sendBuffer(int isVideo, long ts, int flags, ByteBuffer data, int size, int offset) {
        nativeSendBuffer(isVideo, ts, flags, size, offset, data);
    }

    public void start() {
        nativeStart();
    }

    public void stop() {
        nativeStop();
    }


    public NativeFfmpegSender() {
        init();
    }

    /*
    public void setAVOptions(AVOptions jOpts) {
        mAvOptions = jOpts;
        //initAudio(jOpts.numAudioChannels, jOpts.audioSampleRate, 0);
        //initVideo( jOpts.extract_data);
    }
    */
    /*
    public void prepareAVFormatContext(String jOutputPath) {
        //start();
    }
    */

    public void writeAVPacketFromEncodedData(ByteBuffer jData, int jIsVideo, int jOffset, int jSize, int jFlags, long jPts) {
       // if(jIsVideo != 0)
            sendBuffer(jIsVideo, jPts, jFlags, jData, jSize, jOffset);
    }

    /*
    public void finalizeAVFormatContext() {
        stop();
    }
    */

    /**
     * Used to configure the muxer's options.
     * Note the name of this class's fields
     * have to be hardcoded in the native method
     * for retrieval.
     * @author davidbrodsky
     *
     */
    /*
    static public class AVOptions{
        //byte []extract_data;
        public int videoWidth = 720;
        public int videoHeight = 480;

        public int audioSampleRate = 44100;
        public int numAudioChannels = 1;

        // Format specific options
        public int hlsSegmentDurationSec = 10;

        public String outputFormatName = "hls";
        // TODO: Provide a Map for format-specific options
    }
    */
}
