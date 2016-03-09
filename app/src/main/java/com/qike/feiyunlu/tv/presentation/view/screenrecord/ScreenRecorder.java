/*
 * Copyright (c) 2014 Yrom Wang <http://www.yrom.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qike.feiyunlu.tv.presentation.view.screenrecord;

import android.hardware.display.VirtualDisplay;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.util.Log;
import android.view.Surface;

import com.mediamaster.ffmpegwrap.NativeFfmpegSender;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author Yrom
 */
public class ScreenRecorder extends Thread {
		
	private static final String aMIME_TYPE = "audio/mp4a-latm";
    private static final int SAMPLE_RATE = 44100;	// 44.1[KHz] is only setting guaranteed to be available on all devices.
    private static final int BIT_RATE = 64000;    
    
    private AudioThread mAudioThread = null;    
	
    protected final Object mSync = new Object();
	
	private static final String TAG = "ScreenRecorder";
    private int mWidth;
    private int mHeight;
    private int mBitRate;
    private int mDpi;
    private String mDstPath;
    private MediaProjection mMediaProjection;
    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc"; // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 24; // 30 fps
    private static final int IFRAME_INTERVAL = 2; // 10 seconds between I-frames
    private static final long TIMEOUT_US = 10000;
    
    private MediaCodec mVideoEncoder;
    private MediaCodec mAudioEncoder;
    public static Surface mSurface = null;
    //public static MediaMuxer mMuxer;
    public static NativeFfmpegSender mSender;
    public static boolean mMuxerStarted = false;
    public static boolean mIsCapturing = true;
    public static boolean swapbuffer ;
    private int mVideoTrackIndex = -1;
    private int mAudioTrackIndex = -1;
    public static AtomicBoolean mQuit = new AtomicBoolean(false);
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    private MediaCodec.BufferInfo aBufferInfo = new MediaCodec.BufferInfo();
    private VirtualDisplay mVirtualDisplay;


    // Related to extracting H264 SPS + PPS from MediaCodec
    private ByteBuffer mH264Keyframe;
    private int mH264MetaSize = 0;                   // Size of SPS + PPS data
    private final Object mVideoEncoderReleasedSync = new Object();
    private boolean mVideoEncoderReleased;                   // TODO: Account for both encoders
    long mFirstPts = -1;
    String mRtmpUri;

    public ScreenRecorder(String uri, int width, int height, int bitrate, int dpi, MediaProjection mp, String dstPath) {
        super(TAG);
        mRtmpUri = uri;
        mWidth = width;
        mHeight = height;
        mBitRate = bitrate;
        mDpi = dpi;
        mMediaProjection = mp;
        mDstPath = dstPath;
        mQuit = new AtomicBoolean(false);
        mMuxerStarted = false;
    }


//    public ScreenRecorder(MediaProjection mp) {
//        // 480p 2Mbps
//        this(640, 480, 2000000, 1, mp, "/sdcard/download/test.mp4");
//    }

    /**
     * stop task
     */
    public final void quit() {
        mQuit.set(true);
        Log.i(TAG, "chh set quit");
    }

    @Override
    public void run() {
        try {
            try {            	
                prepareEncoder();
                //mMuxer = new MediaMuxer(mDstPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Log.i(TAG, "chh new file " + mDstPath);
            //mVirtualDisplay_f5042y = mMediaProjection_f5033p.createVirtualDisplay("kascend_display", mWidth, mHeight, mDpi, 1, mSurface_f5041x, null, null);
            mVirtualDisplay = mMediaProjection.createVirtualDisplay(TAG + "-display",
                        mWidth, mHeight, mDpi, 1,     //DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mSurface, null, null);
            Log.d(TAG, "created virtual display: " + mVirtualDisplay);
            
            recordVirtualDisplay();

        } finally {
            release();
        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j*3+2] = ' ';
        }
        return new String(hexChars);
    }


    private void recordVirtualDisplay() {
        while (!mQuit.get()) {    
        	int index = mVideoEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_US);  
        	     	 
        	
        	if ( index == -1 )
			{        		
        		      		
			}else if(index == -2){
				  resetOutputFormat();
			}else if(index >= 0 ){
				
				 encodeToVideoTrack(mVideoEncoder,index, mVideoTrackIndex);
                 mVideoEncoder.releaseOutputBuffer(index, false);
			}
        	/*	
        	if( index2 == -1){        		
        		
        		 if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                   
                     
                 } else if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                     try {
                         // wait 10ms
                         Thread.sleep(10);
                     } catch (InterruptedException e) {
                     }                     
                 
                 	
                 	
                 } else if (index >= 0) {              
                     encodeToVideoTrack(mVideoEncoder,index, mVideoTrackIndex);
                     mVideoEncoder.releaseOutputBuffer(index, false);
                 }
        	}*/
        }
    }
    /**
     * Should only be called once, when the encoder produces
     * an output buffer with the BUFFER_FLAG_CODEC_CONFIG flag.
     * For H264 output, this indicates the Sequence Parameter Set
     * and Picture Parameter Set are contained in the buffer.
     * These NAL units are required before every keyframe to ensure
     * playback is possible in a segmented stream.
     *
     * @param bufferInfo
     */
    private void captureH264MetaData( MediaCodec.BufferInfo bufferInfo, ByteBuffer encodedData22222) {
        mH264MetaSize = bufferInfo.size;
        ByteBuffer encodedData = ByteBuffer.allocateDirect(encodedData22222.capacity());
        encodedData.put(encodedData22222);
        encodedData.position(0);

        mH264Keyframe = ByteBuffer.allocateDirect(encodedData.capacity());

        byte[] videoConfig = new byte[bufferInfo.size];
        encodedData.get(videoConfig, bufferInfo.offset, bufferInfo.size);
        encodedData.position(bufferInfo.offset);
        encodedData.put(videoConfig, 0, bufferInfo.size);
        encodedData.position(bufferInfo.offset);
        mH264Keyframe.put(videoConfig, 0, bufferInfo.size);
    }

    /**
     * Adds the SPS + PPS data to the ByteBuffer containing a h264 keyframe
     *
     * @param encodedData
     * @param bufferInfo
     */
    private void packageH264Keyframe(ByteBuffer encodedData, MediaCodec.BufferInfo bufferInfo) {
        mH264Keyframe.position(mH264MetaSize);
        mH264Keyframe.put(encodedData); // BufferOverflow
    }




    private void encodeToVideoTrack(MediaCodec localEncoder , int index, int TrackIndex  ) {
    	MediaCodec localMediaCodec = localEncoder;
        ByteBuffer encodedData = localMediaCodec.getOutputBuffer(index);
        //mBufferInfo.presentationTimeUs = getPTSUs();
        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");

            captureH264MetaData( mBufferInfo, encodedData);
            mBufferInfo.size = 0;
        }
        if (mBufferInfo.size == 0 || mH264MetaSize == 0) {
            Log.d(TAG, "info.size == " + mBufferInfo.size + " metasize " + mH264MetaSize + ", drop it.");
            encodedData = null;
        } else {
//            Log.d(TAG, "got buffer, info: size=" + mBufferInfo.size
//                    + ", presentationTimeUs=" + mBufferInfo.presentationTimeUs
//                    + ", " + (mBufferInfo.presentationTimeUs - mFirstPts)/1000
//                    + ", offset=" + mBufferInfo.offset
//                    + " metasize=" + mH264MetaSize
//            + " flags " + mBufferInfo.flags);
        }
        if (encodedData != null && mMuxerStarted) {
            if (mFirstPts < 0 ) {
                mFirstPts = mBufferInfo.presentationTimeUs;
            }
            encodedData.position(mBufferInfo.offset);
            encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
            //mMuxer.writeSampleData(TrackIndex, encodedData, mBufferInfo);
            //long ts, int flags, ByteBuffer data, int size, int offset

//            //mSender.sendBuffer(1, mBufferInfo.presentationTimeUs, mBufferInfo.flags, encodedData, mBufferInfo.size - mBufferInfo.offset, mBufferInfo.offset);
            ByteBuffer muxerInput;
            muxerInput =  ByteBuffer.allocateDirect(mBufferInfo.size) ;
            muxerInput.put(encodedData);
            muxerInput.position(0);

            if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_SYNC_FRAME) != 0) {
                packageH264Keyframe(muxerInput, mBufferInfo);
                mSender.writeAVPacketFromEncodedData(mH264Keyframe, 1, mBufferInfo.offset, mBufferInfo.size + mH264MetaSize, mBufferInfo.flags, mBufferInfo.presentationTimeUs - mFirstPts);
            } else {
                mSender.writeAVPacketFromEncodedData(muxerInput, 1, mBufferInfo.offset, mBufferInfo.size, mBufferInfo.flags, mBufferInfo.presentationTimeUs - mFirstPts);
            }

            prevOutputPTSUs = mBufferInfo.presentationTimeUs;
        	if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            	// when EOS come.
           		//mIsCapturing = false;
        		ScreenRecorder.mQuit.set(true);
               
            }
        }
    }

    private void encodeToAudioTrack(MediaCodec localEncoder , int index, int TrackIndex  ) {
    	MediaCodec localMediaCodec = localEncoder;
        ByteBuffer encodedData = localMediaCodec.getOutputBuffer(index);
       
        if ((aBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            //Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
            aBufferInfo.size = 0;
        }
        if (aBufferInfo.size == 0) {
           // Log.d(TAG, "info.size == 0, drop it.");
            encodedData = null;
        } else {
//            Log.d(TAG, "Audio got buffer, info: size=" + aBufferInfo.size
//                    + ", presentationTimeUs=" + aBufferInfo.presentationTimeUs
//                    + ", " + (mBufferInfo.presentationTimeUs - mFirstPts)/1000
//                    + ", offset=" + aBufferInfo.offset);
        }
        //aBufferInfo.presentationTimeUs = getPTSUs() -120000 ;
        if (encodedData != null && mMuxerStarted) {
            encodedData.position(aBufferInfo.offset);
            encodedData.limit(aBufferInfo.offset + aBufferInfo.size);
            //TODO:
            //mMuxer.writeSampleData(TrackIndex, encodedData, aBufferInfo);

            mSender.writeAVPacketFromEncodedData(encodedData, 0, aBufferInfo.offset, aBufferInfo.size, aBufferInfo.flags, aBufferInfo.presentationTimeUs - mFirstPts);

        	//prevOutputPTSUs = aBufferInfo.presentationTimeUs;
        	if ((aBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
            	// when EOS come.
           		//mIsCapturing = false;
        		ScreenRecorder.mQuit.set(true);
               
            }
        }
    }
    private void resetOutputFormat() {
        if (mMuxerStarted) {
            throw new IllegalStateException("output format already changed!");
        }
        MediaFormat newFormat = mVideoEncoder.getOutputFormat();
        MediaFormat newFormat2 = mAudioEncoder.getOutputFormat();
        //Log.i(TAG, "output format changed.\n new format: " + newFormat.toString());       

        //mVideoTrackIndex = mMuxer.addTrack(newFormat);
        //mAudioTrackIndex = mMuxer.addTrack(newFormat2);
		//mMuxer.start();
        prepareRtmp();
		mSender.start();

		 if (mAudioThread == null) {
		        mAudioThread = new AudioThread();
				mAudioThread.start();
			}       
        mMuxerStarted = true;
      
    }

    private void prepareRtmp() {
        mSender = new NativeFfmpegSender();
        mSender.init();
        mSender.connectRtmp(mRtmpUri);
        //mSender.connectRtmp("/sdcard/movies/record.flv");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /****************** init video ************************************/
        MediaFormat videoTrackFormat = mVideoEncoder.getOutputFormat();
        int width = videoTrackFormat.getInteger(MediaFormat.KEY_WIDTH);
        int height = videoTrackFormat.getInteger(MediaFormat.KEY_HEIGHT);
        ByteBuffer sps = videoTrackFormat.getByteBuffer("csd-0");
        ByteBuffer pps = videoTrackFormat.getByteBuffer("csd-1");
        byte[] sps_nal = new byte[sps.remaining()];
        sps.get(sps_nal);
        byte[] pps_nal = new byte[pps.remaining()];
        pps.get(pps_nal);

        Log.i(TAG, "handleAddTrack Init Video , " + width + " X " + height + " configure sps: " + bytesToHex(sps_nal) + " pps: " + bytesToHex(pps_nal));


        int j = 0;
        byte[] combined = new byte[sps_nal.length + pps_nal.length - 8 + 4];
        int slen = sps_nal.length - 4;
        int plen = pps_nal.length - 4;
        combined[j++] = (byte) ((slen >> 8) & 0xFF);
        combined[j++] = (byte) ((slen) & 0xFF);
        for (int i = 4; i < sps_nal.length; ++i) {
            combined[j++] = sps_nal[i];
        }
        combined[j++] = (byte) ((plen >> 8) & 0xFF);
        combined[j++] = (byte) ((plen) & 0xFF);
        for (int i = 4; i < pps_nal.length; ++i) {
            combined[j++] = pps_nal[i];
        }

        mSender.initVideo(width, height ,combined);

        /****************** init audio ************************************/
        MediaFormat audioTrackFormat = mAudioEncoder.getOutputFormat();
        int channels = audioTrackFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
        int samplerate = audioTrackFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        ByteBuffer audio_extract_data = audioTrackFormat.getByteBuffer("csd-0");
        byte[] audio_extract_data_nal = new byte[audio_extract_data.remaining()];
        audio_extract_data.get(audio_extract_data_nal);
        Log.i(TAG, "handleAddTrack Init aac , " + channels + " X " + samplerate + " configure audio_extract_data_nal: " + bytesToHex(audio_extract_data_nal));
        mSender.initAudio(channels, samplerate, audio_extract_data_nal);
    }

    private void prepareEncoder() throws IOException {
// Kascend
//        MediaCodecInfo codecInfoAt;
//        int codecCount = MediaCodecList.getCodecCount();
//        MediaCodecInfo mediaCodecInfo = null;
//        for (int i = 0; i < codecCount && mediaCodecInfo == null; i++) {
//            codecInfoAt = MediaCodecList.getCodecInfoAt(i);
//            if (codecInfoAt.isEncoder()) {
//                String[] supportedTypes = codecInfoAt.getSupportedTypes();
//                int i3 = 0;
//                for (int i2 = 0; i2 < supportedTypes.length && i3 == 0; i2++) {
//                    if (supportedTypes[i2].equals("video/avc")) {
//                        i3 = 1;
//                    }
//                }
//                if (i3 != 0) {
//                    mediaCodecInfo = codecInfoAt;
//                }
//            }
//        }
//        codecInfoAt = mediaCodecInfo;
//        for (int i4 = 0; i4 < MediaCodecList.getCodecCount(); i4++) {
//            MediaCodecInfo codecInfoAt2 = MediaCodecList.getCodecInfoAt(i4);
//            if (codecInfoAt2.isEncoder()) {
//                String[] supportedTypes2 = codecInfoAt2.getSupportedTypes();
//                MediaCodecInfo mediaCodecInfo2 = codecInfoAt;
//                for (int i5 = 0; i5 < supportedTypes2.length; i5++) {
//                    if (supportedTypes2[i5].equals("video/avc")) {
//                        MediaCodecInfo.CodecCapabilities capabilitiesForType = codecInfoAt2.getCapabilitiesForType(supportedTypes2[i5]);
//                        MediaCodecInfo mediaCodecInfo3 = mediaCodecInfo2;
//                        for (MediaCodecInfo.CodecProfileLevel codecProfileLevel : capabilitiesForType.profileLevels) {
//                            if (codecProfileLevel.profile == 8) {
//                                mediaCodecInfo3 = codecInfoAt2;
//                            }
//                        }
//                        mediaCodecInfo2 = mediaCodecInfo3;
//                    }
//                }
//                codecInfoAt = mediaCodecInfo2;
//            }
//        }
//        Log.d("ScreenRecorder", "Found " + codecInfoAt.getName() + " supporting " + "video/avc");
//        mVideoEncoder = MediaCodec.createByCodecName(codecInfoAt.getName());
//        MediaFormat createVideoFormat = MediaFormat.createVideoFormat("video/avc", mWidth, mHeight);
//        createVideoFormat.setInteger("color-format", MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
//        createVideoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, mBitRate);
//        createVideoFormat.setInteger("frame-rate", 24);
//        createVideoFormat.setInteger("max-input-size", 0);
//        createVideoFormat.setInteger("i-frame-interval", 2);
//        mVideoEncoder.configure(createVideoFormat, null, null, 1);
//        Log.d(TAG, " mEncoder format == " + mVideoEncoder.getOutputFormat());
//        mSurface = mVideoEncoder.createInputSurface();
//        mVideoEncoder.start();


        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        Log.d(TAG, "created video format: " + format);
        mVideoEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mVideoEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        mSurface = mVideoEncoder.createInputSurface();
        Log.d(TAG, "created input surface: " + mSurface);
        mVideoEncoder.start();
       

        final MediaFormat audioFormat = MediaFormat.createAudioFormat(aMIME_TYPE, SAMPLE_RATE, 1);
		audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
		audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
		audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
		audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
	
        mAudioEncoder = MediaCodec.createEncoderByType(aMIME_TYPE);
        mAudioEncoder.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mAudioEncoder.start();        
        
    }    
    
    class AudioThread extends Thread {
    	@Override
    	public void run() {
            final int buf_sz = AudioRecord.getMinBufferSize(
                    SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) ;
            final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
            	SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buf_sz);
            try {            					
	                final byte[] buf = new byte[buf_sz];
	                int readBytes;
	                audioRecord.startRecording();
	                try {
			    		while (!mQuit.get()) {
			    			// read audio data from internal mic
			    			readBytes = audioRecord.read(buf, 0, buf_sz);
			    			if (readBytes > 0 && !mQuit.get() && mIsCapturing) {
			    			    // set audio data to encoder
			    				encode(buf, readBytes, getPTSUs());			    			
			    			}
			    			
			    		}	    			
	                } finally {
	                	audioRecord.stop();
	                }            	
            } finally {
            	audioRecord.release();
            }		
    	}
    }


    protected void encode(byte[] buffer, int length, long presentationTimeUs) {
      	int ix = 0, sz;
        final ByteBuffer[] inputBuffers = mAudioEncoder.getInputBuffers();
        while (!mQuit.get() && ix < length) {
	        final int inputBufferIndex = mAudioEncoder.dequeueInputBuffer(TIMEOUT_US);
	        if (inputBufferIndex >= 0) {
	            final ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
	            inputBuffer.clear();
	            sz = inputBuffer.remaining();
	            sz = (ix + sz < length) ? sz : length - ix; 
	            if (sz > 0 && (buffer != null)) {
	            	inputBuffer.put(buffer, ix, sz);
	            }
	            ix += sz;
	            if (length <= 0) {	            	
	            	mAudioEncoder.queueInputBuffer(inputBufferIndex, 0, 0,
	            		presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
		            break;
	            } else {
	            	mAudioEncoder.queueInputBuffer(inputBufferIndex, 0, sz,
	            		presentationTimeUs, 0);
	            }
	        } else if (inputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
	        	
	        }       
	        
	        int index2 = mAudioEncoder.dequeueOutputBuffer(aBufferInfo, TIMEOUT_US);       	        		
	        if (index2 == MediaCodec.INFO_TRY_AGAIN_LATER) {
                                                      
                  
              }else if (index2 >= 0) {        
           	   
                  encodeToAudioTrack(mAudioEncoder,index2, mAudioTrackIndex);
                  mAudioEncoder.releaseOutputBuffer(index2, false);
              }  
	        
	        
        }
    }
    
    protected void signalEndOfInputStream() {	
        encode(null, 0, System.nanoTime() / 1000L);
	}
    
    private long prevOutputPTSUs = 0;
    
    protected long getPTSUs() {
  		long result = System.nanoTime() / 1000L;
  		if (result < prevOutputPTSUs)
  			result = (prevOutputPTSUs - result) + result;  		 		
         
         return result;
      }
    
    
    protected void release() {
        if (mVideoEncoder != null) {
            mVideoEncoder.stop();
            mVideoEncoder.release();
            mVideoEncoder = null;
        }    
        if (mAudioEncoder != null) {
            mAudioEncoder.stop();
            mAudioEncoder.release();
            mAudioEncoder = null;
        }
       
        if ( mAudioThread != null )
		{
        	 mAudioThread = null;
		}      
        
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
//        if (mMuxer != null) {
//            mMuxer.stop();
//            mMuxer.release();
//            mMuxer = null;
//        }

        if (mSender != null) {
            mSender.stop();
        }
    }
    
     
}
