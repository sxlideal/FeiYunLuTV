#include <string.h>
#include <jni.h>
#include <android/log.h>

#include <math.h>
#include <libavutil/opt.h>
#include <libavcodec/avcodec.h>
#include <libavutil/channel_layout.h>
#include <libavutil/common.h>
#include <libavutil/imgutils.h>
#include <libavutil/mathematics.h>
#include <libavutil/samplefmt.h>


#define ALOGI(x...)    __android_log_print (ANDROID_LOG_INFO, "ffmpegnative", x);
#define ALOGE(x...)    __android_log_print (ANDROID_LOG_ERROR, "ffmpegnative", x);

typedef struct fields_t {
    jfieldID context;
    jmethodID arrayID;
} fields_t;

static fields_t gFields;

#define CHECK(x) do{if (!(x)) {ALOGI("faile check %s", #x); return -1;}}while(0)


/*
 * Java Bindings
 */
#if 0
static int native_init (JNIEnv* env, jobject thiz) {
    ALOGI("%s entry now empty",__FUNCTION__);
    return 0;
}

static int native_init_audio (JNIEnv* env, jobject thiz, jint channels, jint sample_rate, jint sample_fmt  ) {
    ALOGI("%s entry %d %d %d",__FUNCTION__, channels, sample_rate, sample_fmt);
    return 0;
}



static int native_init_video (JNIEnv* env, jobject thiz, jint width, jint height,  jobject byteBuf) {
    ALOGI("%s entry %d*%d %d",__FUNCTION__, width, height);
    return 0;
}

static int native_send_buffer (JNIEnv* env, jobject thiz, jlong ts, jint flags, jobject byteBuf) {
    ALOGI("%s entry %lld %x",__FUNCTION__, ts, flags);
    return 0;
}

static int native_start(JNIEnv* env, jobject thiz) {
    return 0;
}

static void native_stop (JNIEnv* env, jobject thiz) {
    ALOGI("%s entry",__FUNCTION__);
    return ;
}
#else
static int native_init (JNIEnv* env, jobject thiz) {
    ALOGI("%s entry now",__FUNCTION__);

    jclass byteBufClass = (*env)->FindClass (env, "java/nio/ByteBuffer");
    CHECK(byteBufClass != NULL);
    gFields.arrayID = (*env)->GetMethodID(env, byteBufClass, "array", "()[B");
    CHECK(gFields.arrayID != NULL);

    //static int init = 0;
    //if (init) {
    //    ALOGI("%s entry again WRONG!!",__FUNCTION__);
    //    return -1;
    //}
    //init =1;
    rtmp_sender_init();
    return 0;
}


static int native_connect_rtmp (JNIEnv* env, jobject thiz, jstring url) {
    if (url == NULL) {
        //jniThrowException(env, "java/lang/IllegalArgumentException", NULL);
        return -1;
    }
    
    const char *tmp = (*env)->GetStringUTFChars(env, url, NULL);
    if (tmp == NULL) {  // Out of memory
        return -1;
    }
    ALOGI("setDataSource: path %s", tmp);
    
    connect_rtmp(tmp);

    (*env)->ReleaseStringUTFChars(env, url, tmp);
    tmp = NULL;
    
    return 0;
}

static int get_bytearray(JNIEnv* env, jbyteArray byteArray,int max_size, void **buf, int *size) {
    jlong dstSize;
    jboolean isCopy;
    void *dst = (*env)->GetByteArrayElements(env, byteArray, &isCopy);
    dstSize = (*env)->GetArrayLength(env, byteArray);

    //ALOGI("get_bytearray size %lld", dstSize);
    if (max_size > 0 && dstSize > max_size) {
        dstSize = max_size;
    }

    void *e = malloc(dstSize);
    memcpy(e, dst, dstSize);
    //ALOGI("get_bytearray memcpy %lld", dstSize);

    *buf = e;
    *size = dstSize;

    if (byteArray != NULL) {
        (*env)->ReleaseByteArrayElements(env, byteArray, (jbyte *)dst, 0);
    }


    return 0;
}

static int native_init_audio (JNIEnv* env, jobject thiz, jint channels, jint sample_rate, jbyteArray byteBuf ) {
    ALOGI("%s entry %d %d ",__FUNCTION__, channels, sample_rate);
    void *e = NULL;
    int esize = 0;
    get_bytearray(env, byteBuf, -1, &e, &esize);
    set_acodec(sample_rate, channels, e, esize);

    return 0;
}

static int native_init_video (JNIEnv* env, jobject thiz, jint width, jint height, jbyteArray byteBuf) {
    ALOGI("%s entry %d*%d ",__FUNCTION__, width, height);
    void *e;
    int esize;
    get_bytearray(env, byteBuf, -1, &e, &esize);
    ALOGI("start set_vcodec %d %d %d",width, height, esize);
    set_vcodec(width, height, e, esize);

    return 0;
}



static int get_bytebuffer(JNIEnv* env, jobject byteBuf,int size, int offset,  void **buf, int *out_size)
{
    // Try to convert the incoming byteBuffer into ABuffer
    void *dst = (*env)->GetDirectBufferAddress(env, byteBuf);

    jlong dstSize;
    jbyteArray byteArray = NULL;

    if (dst == NULL) {

        byteArray =
            (jbyteArray)(*env)->CallObjectMethod(env, byteBuf, gFields.arrayID);

        if (byteArray == NULL) {
            ALOGE("java/lang/IllegalArgumentException byteArray is null");
            return -1;
        }

        jboolean isCopy;
        dst = (*env)->GetByteArrayElements(env, byteArray, &isCopy);

        dstSize = (*env)->GetArrayLength(env, byteArray);
    } else {
        dstSize = (*env)->GetDirectBufferCapacity(env, byteBuf);
    }

    if (dstSize < (offset + size)) {
        ALOGE("writeSampleData saw wrong dstSize %lld, size  %d, offset %d",
              dstSize, size, offset);
        if (byteArray != NULL) {
            (*env)->ReleaseByteArrayElements(env, byteArray, (jbyte *)dst, 0);
        }
        ALOGE("java/lang/IllegalArgumentException sample has a wrong size");
        return -1;
    }

    void *e = malloc(size);
    memcpy(e, dst+offset, size);
    //ALOGI("get_bytearray memcpy %lld", dstSize);

    *buf = e;
    *out_size = size;

    if (byteArray != NULL) {
        (*env)->ReleaseByteArrayElements(env, byteArray, (jbyte *)dst, 0);
    }

    return 0;


}

static int native_send_buffer (JNIEnv* env, jobject thiz, jint isVideo, jlong ts, jint flags, jint size, jint offset, jobject byteBuf) {
    //ALOGI("%s entry ts %lld size %d flag %x",__FUNCTION__, ts, size, flags);
    void *data;
    int data_size;

    get_bytebuffer(env, byteBuf, size, offset, &data, &data_size);
    putBuffer(isVideo, data, data_size, ts, flags);
    free(data);


    return 0;
}

static int native_start(JNIEnv* env, jobject thiz) {
    ALOGI("%s entry",__FUNCTION__);
   start_send_data();
   return 0;
}

static void native_stop (JNIEnv* env, jobject thiz) {
    ALOGI("%s entry",__FUNCTION__);
    rtmp_sender_stop();
    return ;
}
#endif

// IILjava/nio/ByteBuffer
static JNINativeMethod native_methods[] = {
  { "nativeInit", "()I", (void *) native_init},
  { "nativeConnRtmp", "(Ljava/lang/String;)I", (void *) native_connect_rtmp},
  { "nativeStart", "()I", (void *) native_start},
  { "nativeInitAudio", "(II[B)I", (void *) native_init_audio},
  { "nativeInitVideo", "(II[B)I", (void *) native_init_video},
  { "nativeSendBuffer", "(IJIIILjava/nio/ByteBuffer;)I", (void *) native_send_buffer},
  { "nativeStop", "()V", (void *) native_stop}
};

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
  JNIEnv *env = NULL;

  if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
    __android_log_print (ANDROID_LOG_ERROR, "tutorial-1", "Could not retrieve JNIEnv");
    return 0;
  }
  //com.android.testtool
  //jclass klass = (*env)->FindClass (env, "com/android/testtool/NativeFfmpegSender");
  //io.kickflip.sdk.av FFmpegRtmpWrapper
  // com.android.grafika
  //jclass klass = (*env)->FindClass (env, "com/android/grafika/NativeFfmpegSender");
  // io.kickflip.sdk.av;
  jclass klass = (*env)->FindClass (env, "com/mediamaster/ffmpegwrap/NativeFfmpegSender");
  (*env)->RegisterNatives (env, klass, native_methods,  sizeof(native_methods)/sizeof(native_methods[0]));

  return JNI_VERSION_1_4;
}
