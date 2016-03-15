/**
 * 最简单的基于FFmpeg的推流器（推送RTMP）
 * Simplest FFmpeg Streamer (Send RTMP)
 * 
 * 雷霄骅 Lei Xiaohua
 * leixiaohua1020@126.com
 * 中国传媒大学/数字电视技术
 * Communication University of China / Digital TV Technology
 * http://blog.csdn.net/leixiaohua1020
 * 
 * 本例子实现了推送本地视频至流媒体服务器（以RTMP为例）。
 * 是使用FFmpeg进行流媒体推送最简单的教程。
 *
 * This example stream local media files to streaming media 
 * server (Use RTMP as example). 
 * It's the simplest FFmpeg streamer.
 * 
 */

#include <stdio.h>
#include <unistd.h>
#include <pthread.h>

#define __STDC_CONSTANT_MACROS

#ifdef _WIN32
//Windows
extern "C"
{
#include "libavformat/avformat.h"
#include "libavutil/mathematics.h"
#include "libavutil/time.h"
};
#else
//Linux...
#ifdef __cplusplus
extern "C"
{
#endif
#include <libavformat/avformat.h>
#include <libavutil/mathematics.h>
#include <libavutil/time.h>
#ifdef __cplusplus
};
#endif
#endif

static AVOutputFormat *ofmt = NULL;
//输入对应一个AVFormatContext，输出对应一个AVFormatContext
//（Input AVFormatContext and Output AVFormatContext）
static AVFormatContext *ofmt_ctx = NULL;
static int ret, i;
static int currentindex=0;
static int audioindex=-1;
static int videoindex=-1;
static AVRational in_time_base={1,1000000};
static AVRational out_time_base={1,1000};

static int frame_index=0;
static int64_t g_start_time=-1;
static int64_t first_vpts = -1, last_vts = 0; 
static int64_t first_apts = -1, last_ats = 0; 

static pthread_mutex_t put_mutex=PTHREAD_MUTEX_INITIALIZER; 

static int g_rtmp_connected = 0;
static pthread_t g_send_thread;
static pthread_t g_rtmp_init_thread;

static int rtmp_stop = 1;

static char * g_out_filename = NULL;

static int64_t g_put_apts = 0;
static int64_t g_put_vpts = 0;
static int64_t g_put_vframe_count = 0;
static int g_put_vsize = 0;
static int g_put_asize = 0;
static int64_t g_send_apts = 0;
static int64_t g_send_vpts = 0;
static int64_t g_send_asize = 0;
static int64_t g_send_vsize = 0;
static int g_send_drop_vpack= 0;
static int g_send_apacket = 0;
static int g_send_vpacket = 0;
static uint8_t *g_spspps = NULL;
static int g_spspps_size= NULL;

 
#define LOCAL_FILE 0

//#if 1
//    char *out_filename = "rtmp://180.153.102.19/swaps/test";
//    //char *out_filename = "/sdcard/movies/rtmp.flv";
//    char *out_format = "flv";
//#else
//    char *out_filename = "/sdcard/movies/rtmp.mp4";
//    char *out_format = "mp4";
//#endif


#include <android/log.h>
#define printf(x...)    __android_log_print (ANDROID_LOG_INFO, "rtmp_sender", x);
#define ALOGI(x...)    __android_log_print (ANDROID_LOG_INFO, "rtmp_sender", x);
#define ALOGW(x...)    __android_log_print (ANDROID_LOG_WARN, "rtmp_sender", x);
#define ALOGE(x...)    __android_log_print (ANDROID_LOG_ERROR, "rtmp_sender", x);
#define ALOGV(x...)    __android_log_print (ANDROID_LOG_VERBOSE, "ffmpegnative", x);

#define SDL_mutex char 
#define SDL_cond  char 

#define SDL_CreateMutex() 0
#define SDL_CreateCond() 1

#define SDL_CondSignal(x) x=1

#define SDL_LockMutex(x) while(1){ if(!x){x=1;break;}else{usleep(1000);}}
#define SDL_UnlockMutex(x) x=0

#define SDL_CondWait(x,y) while(1){ if(x){x=0;break;}else{usleep(1000);}}

#define SDL_DestroyMutex(x)
#define SDL_DestroyCond(x)

static AVPacket flush_pkt;

#define MAX_CACHE_PKT 1024

typedef struct MyAVPacketList {
    AVPacket pkt;
    struct MyAVPacketList *next;
    int serial;
} MyAVPacketList;

typedef struct PacketQueue {
    MyAVPacketList *first_pkt, *last_pkt;
    int nb_packets;
    int size;
    int abort_request;
    int serial;
    //SDL_mutex *mutex;
    //SDL_cond *cond;
    SDL_mutex mutex;
    SDL_cond cond;
} PacketQueue;

PacketQueue audioq;
PacketQueue videoq;


static int packet_queue_put_private(PacketQueue *q, AVPacket *pkt)
{
    MyAVPacketList *pkt1;

    if (q->abort_request)
       return -1;

    pkt1 = (MyAVPacketList *)av_malloc(sizeof(MyAVPacketList));
    if (!pkt1)
        return -1;
    pkt1->pkt = *pkt;
    pkt1->next = NULL;
    if (pkt == &flush_pkt)
        q->serial++;
    pkt1->serial = q->serial;

    if (!q->last_pkt)
        q->first_pkt = pkt1;
    else
        q->last_pkt->next = pkt1;
    q->last_pkt = pkt1;
    q->nb_packets++;
    q->size += pkt1->pkt.size + sizeof(*pkt1);
    /* XXX: should duplicate packet data in DV case */
    SDL_CondSignal(q->cond);
    return 0;
}



static int packet_queue_put(PacketQueue *q, AVPacket *pkt)
{
    int ret;

    /* duplicate the packet */
    if (pkt != &flush_pkt && av_dup_packet(pkt) < 0)
        return -1;

    SDL_LockMutex(q->mutex);
    ret = packet_queue_put_private(q, pkt);
    SDL_UnlockMutex(q->mutex);

    if (pkt != &flush_pkt && ret < 0)
        av_free_packet(pkt);

    return ret;
}

static int packet_queue_put_nullpacket(PacketQueue *q, int stream_index)
{
    AVPacket pkt1, *pkt = &pkt1;
    av_init_packet(pkt);
    pkt->data = NULL;
    pkt->size = 0;
    pkt->stream_index = stream_index;
    return packet_queue_put(q, pkt);
}

/* packet queue handling */
static void packet_queue_init(PacketQueue *q)
{
    memset(q, 0, sizeof(PacketQueue));
    q->mutex = SDL_CreateMutex();
    q->cond = SDL_CreateCond();
    q->abort_request = 1;
}

static void packet_queue_flush(PacketQueue *q)
{
    MyAVPacketList *pkt, *pkt1;

    SDL_LockMutex(q->mutex);
    for (pkt = q->first_pkt; pkt; pkt = pkt1) {
        pkt1 = pkt->next;
        av_free_packet(&pkt->pkt);
        av_freep(&pkt);
    }
    q->last_pkt = NULL;
    q->first_pkt = NULL;
    q->nb_packets = 0;
    q->size = 0;
    SDL_UnlockMutex(q->mutex);
}

static void packet_queue_destroy(PacketQueue *q)
{
    packet_queue_flush(q);
    SDL_DestroyMutex(q->mutex);
    SDL_DestroyCond(q->cond);
}

static void packet_queue_abort(PacketQueue *q)
{
    SDL_LockMutex(q->mutex);

    q->abort_request = 1;

    SDL_CondSignal(q->cond);

    SDL_UnlockMutex(q->mutex);
}

static void packet_queue_start(PacketQueue *q)
{
    SDL_LockMutex(q->mutex);
    q->abort_request = 0;
    packet_queue_put_private(q, &flush_pkt);
    SDL_UnlockMutex(q->mutex);
}

/* return < 0 if aborted, 0 if no packet and > 0 if packet.  */
static AVPacket *packet_queue_peek(PacketQueue *q)
{
    return q->first_pkt;
}

/* return < 0 if aborted, 0 if no packet and > 0 if packet.  */
static int packet_queue_get(PacketQueue *q, AVPacket *pkt, int block, int *serial)
{
    MyAVPacketList *pkt1;
    int ret;

    SDL_LockMutex(q->mutex);

    for (;;) {
        if (q->abort_request) {
            ret = -1;
            break;
        }

        pkt1 = q->first_pkt;
        if (pkt1) {
            q->first_pkt = pkt1->next;
            if (!q->first_pkt)
                q->last_pkt = NULL;
            q->nb_packets--;
            q->size -= pkt1->pkt.size + sizeof(*pkt1);
            *pkt = pkt1->pkt;
            if (serial)
                *serial = pkt1->serial;
            av_free(pkt1);
            ret = 1;
            break;
        } else if (!block) {
            ret = 0;
            break;
        } else {
            SDL_CondWait(q->cond, q->mutex);
        }
    }
    SDL_UnlockMutex(q->mutex);
    return ret;
}

static void sanitize(uint8_t *line){
    while(*line){
        if(*line < 0x08 || (*line > 0x0D && *line < 0x20))
            *line='?';
        line++;
    }
}


void nam_av_log_callback(void* ptr, int level, const char* fmt, va_list vl)
{
    static int print_prefix = 1;
    static int count;
    static char prev[1024];
    char line[1024];
    static int is_atty;
    if (!ptr || !fmt) {
        ALOGI("nam_av_log_callback %p %p", ptr, fmt);
    }

    //ALOGI("nam_av_log_callback");
    if (level > av_log_get_level())
        return;

    av_log_format_line(ptr, level, fmt, vl, line, sizeof(line), &print_prefix);

    if (print_prefix && !strcmp(line, prev)){
        count++;
        return;
    }
    if (count > 0) {
        ALOGI("Last message repeated %d times\n", count);
        count = 0;
    }
    strcpy(prev, line);
    sanitize((uint8_t *)line);

#if 0
    ALOGI("%s", line);
#else
#define LOG_BUF_SIZE 2048 
    static char g_msg[LOG_BUF_SIZE];
    static int g_msg_len = 0;

    int saw_lf, check_len;

    do {
        check_len = g_msg_len + strlen(line) + 1;
        if (check_len <= LOG_BUF_SIZE) {
            /* lf: Line feed ('\n') */
            saw_lf = (strchr(line, '\n') != NULL) ? 1 : 0;
            strncpy(g_msg + g_msg_len, line, strlen(line));
            g_msg_len += strlen(line);
            if (!saw_lf) {
               /* skip */
               return;
            } else {
               /* attach the line feed */
               g_msg_len += 1;
               g_msg[g_msg_len] = '\n';
            }
        } else {
            /* trace is fragmented */
            g_msg_len += 1;
            g_msg[g_msg_len] = '\n';
        }
        ALOGI("nam_av_log_callback %s", g_msg);
        /* reset g_msg and g_msg_len */
        memset(g_msg, 0, LOG_BUF_SIZE);
        g_msg_len = 0;
     } while (check_len > LOG_BUF_SIZE);
#endif
}

static void init_gparmeters() {
    ofmt = NULL;
    ofmt_ctx = NULL;
    currentindex=0;
    audioindex=-1;
    videoindex=-1;
    
    frame_index=0;
    g_start_time=-1;
    first_vpts = -1;
    last_vts = 0; 
    first_apts = -1;
    last_ats = 0; 
    
    //put_mutex = PTHREAD_MUTEX_INITIALIZER; 
    pthread_mutex_init(&put_mutex,NULL);
    
    g_rtmp_connected = 0;
    
    g_send_thread = 0;
    g_rtmp_init_thread = 0 ;

    rtmp_stop = 1;
    g_out_filename = NULL;

 
}

int init() {
    av_log_set_level(AV_LOG_DEBUG);
    av_log_set_callback(nam_av_log_callback);

    av_register_all();
    //Network
    avformat_network_init();
    packet_queue_init(&videoq);
    packet_queue_init(&audioq);
    packet_queue_start(&videoq);
    packet_queue_start(&audioq);

}

static int end_all()  {
    /* close output */
    if (ofmt_ctx && !(ofmt->flags & AVFMT_NOFILE))
        avio_close(ofmt_ctx->pb);
    ALOGI("%s %d",__FUNCTION__, __LINE__);
    avformat_free_context(ofmt_ctx);
    if (ret < 0 && ret != AVERROR_EOF) {
        printf( "Error occurred.\n");
        return -1;
    }
    ALOGI("%s %d",__FUNCTION__, __LINE__);
}

void setNalHeader_annexb(uint8_t *buf) {
    // annex-b
    buf[0] = buf[1] = buf[2] = 0x0;
    buf[3] = 0x1;
}

void setNalHeader(uint8_t *buf, int size) {
    buf[0] = size>>24 & 0xff;
    buf[1] = size>>16 & 0xff;
    buf[2] = size>>8 & 0xff;
    buf[3] = size & 0xff;
}

#define alloc_and_copy_or_fail(obj, size, pad) \
    if (obj && size > 0) { \
        dest->obj = av_malloc(size + pad); \
        if (!dest->obj) \
            goto fail; \
        memcpy(dest->obj, obj, size); \
        if (pad) \
            memset(((uint8_t *) dest->obj) + size, 0, pad); \
    }

//static AVStream *out_stream;

int set_acodec(int sample_rate, int channels, uint8_t *extradata, int extradata_size) {
    ofmt = ofmt_ctx->oformat;

    AVStream *out_stream = avformat_new_stream(ofmt_ctx, NULL);
    if (!out_stream) {
        printf( "Failed allocating output stream\n");
        ret = AVERROR_UNKNOWN;
        return -1;
    }

    audioindex =  currentindex++;
    AVCodecContext *dest = out_stream->codec;
    // audio
    dest->codec_id = AV_CODEC_ID_AAC;
    dest->codec_type = AVMEDIA_TYPE_AUDIO;
    dest->sample_rate = sample_rate;
    dest->channels = channels;

    int i ;
    printf("%s channels %d sample_rate %d adts %d :",__FUNCTION__, channels, sample_rate, extradata_size);
    for(i = 0; i < extradata_size; i++) {
        printf("%x ", extradata[i]);
    }
    printf("\n");

    uint8_t *extra = (uint8_t *)av_mallocz(extradata_size);
    memcpy(extra, extradata, extradata_size);
    dest->extradata = extra;
    dest->extradata_size = extradata_size;
    
    //alloc_and_copy_or_fail(extradata,    extradata_size,
    //                       FF_INPUT_BUFFER_PADDING_SIZE);
    //dest->extradata_size  = extradata_size;

    out_stream->codec->codec_tag = 0;
    if (ofmt_ctx->oformat->flags & AVFMT_GLOBALHEADER)
        out_stream->codec->flags |= CODEC_FLAG_GLOBAL_HEADER;



}


int set_vcodec(int width, int height, uint8_t *spspps, int spspps_size) {
    ofmt = ofmt_ctx->oformat;
    
    ALOGI("set_vcodec %dX%d %p %d\n", width, height, spspps, spspps_size);

    AVStream *out_stream = avformat_new_stream(ofmt_ctx, NULL);
    if (!out_stream) {
        printf( "Failed allocating output stream\n");
        ret = AVERROR_UNKNOWN;
        return -1;
    }
    videoindex =  currentindex++;
    AVCodecContext *dest = out_stream->codec;

    //my_copy_codec(dest, src);

    ///* set values specific to opened codecs back to their default state */
    //dest->slice_offset    = NULL;
    //dest->hwaccel         = NULL;
    //dest->internal        = NULL;
    //dest->coded_frame     = NULL;

    ///* reallocate values that should be allocated separately */
    //dest->extradata       = NULL;
    //dest->intra_matrix    = NULL;
    //dest->inter_matrix    = NULL;
    //dest->rc_override     = NULL;
    //dest->subtitle_header = NULL;


    dest->codec_id = AV_CODEC_ID_H264;
    dest->codec_type = AVMEDIA_TYPE_VIDEO;
    dest->width = width;
    dest->height = height;
    dest->time_base.den = 1000000;
    dest->time_base.num = 1;

    g_spspps = malloc(spspps_size);
    memcpy(g_spspps, spspps, spspps_size);
    g_spspps_size = spspps_size;

    
    int i ;
    printf("%s %d %d spspps %d: ",__FUNCTION__, width, height, spspps_size);
    for(i = 0; i < spspps_size; i++) {
        printf("%x ", spspps[i]);
    }
    printf("\n");

    // java 1280 X 720 configure sps: 00 00 00 01 67 64 00 29 AC 1B 1A 80 50 05 BA 01 E1 10 8A 70  pps: 00 00 00 01 68 EA 43 CB
    // c : 00 10 67 64 00 29 AC 1B 1A 80 50 05 BA 01 E1 10 8A 70  pps:  00 04 68 EA 43 CB

    // 00 00 00 01 67 42 80 1F DA 05 07 E4  pps: 00 00 00 01 68 CE 06 F2
    //=>  00 08 67 42 80 1F DA 05 07 E4 00 04 68 CE 06 F2

    /*
     1 64 0 d ff e1 0 
     19 
     67 64 0 d ac d9 41 71 fe 5e 10 0 0 3 0 10 0 0 3 3 20 f1 42 99 60 
     1 
     0 6 68 eb e3 cb 22 c0 
     */

    // video
    //out_stream->codec->codec_tag = 0;

#if 1 // libavformat/avc.c::ff_isom_write_avcc
    int extradata_size = spspps_size + 7;
    uint32_t spsSize = spspps[1];
    uint32_t ppsSize = spspps[spsSize + 3];
    uint8_t *sps =  &spspps[2];
    uint8_t *pps = &spspps[2 + spsSize + 2];

    //g_spspps = (uint8_t *)av_mallocz(extradata_size);


    uint8_t *extra = (uint8_t *)av_mallocz(extradata_size);
    extra[0] = 1; // version
    extra[1] = sps[1]; // profile
    extra[2] = spspps[2]; // compatibility
    extra[3] = spspps[3]; // level
    extra[4] = 0xFF ;  // reserved (6 bits), NALU length size - 1 (2 bits)
    extra[5] = 0xE1 ;  // reserved (3 bits), num of SPS (5 bits)
    uint8_t *pExtra = extra + 6;
    memcpy(pExtra, spspps, spsSize+2);
    pExtra += spsSize+2;
    *pExtra++ = 1; // num of PPS
    memcpy(pExtra, spspps+2+spsSize, ppsSize+2);


#else
#if 0
    // Calc extradata from nvidia 

    int extradata_size = spspps_size + 7;
    int spsSize = spspps[1];
    int ppsSize = spspps[spsSize + 3];

    //g_spspps = (uint8_t *)av_mallocz(extradata_size);


    uint8_t *extra = (uint8_t *)av_mallocz(extradata_size);
    extra[0] = 1; // version
    extra[1] = spspps[3]; // profile
    extra[2] = spspps[4]; // compatibility
    extra[3] = spspps[5]; // level
    extra[4] = 0xFC | 3;  // reserved (6 bits), NALU length size - 1 (2 bits)
    extra[5] = 0xE0 | 1;  // reserved (3 bits), num of SPS (5 bits)
    uint8_t *pExtra = extra + 6;
    memcpy(pExtra, spspps, spsSize+2);
    pExtra += spsSize+2;
    *pExtra++ = 1; // num of PPS
    memcpy(pExtra, spspps+2+spsSize, ppsSize+2);

#else
    int spsSize = spspps[1];
    int ppsSize = spspps[spsSize + 3];
    int extradata_size = 4 + spsSize + 4 + ppsSize;

    uint8_t *sps_start =  &spspps[2];
    uint8_t *pps_start = &spspps[2 + spsSize + 2];

    uint8_t *extra = (uint8_t *)av_mallocz(extradata_size);

    setNalHeader_annexb(extra);
    memcpy(&extra[4], sps_start, spsSize);

    setNalHeader_annexb(&extra[4+spsSize]);
    memcpy(&extra[4+spsSize + 4], pps_start, ppsSize);


#endif
#endif

    dest->extradata = extra;
    dest->extradata_size = extradata_size;

    {
        int ii ;
        char tmp[24];
        char tmp2[1024];
        tmp2[0] = 0;
        for(ii = 0; ii < extradata_size; ii++) {
            sprintf(tmp, "%02x ", extra[ii]);
            strcat(tmp2, tmp);
        }
        printf("extradata %s\n", tmp2);
    }


    //alloc_and_copy_or_fail(extradata,    extradata_size,
    //                       FF_INPUT_BUFFER_PADDING_SIZE);

    out_stream->codec->codec_tag = 0;
    if (ofmt_ctx->oformat->flags & AVFMT_GLOBALHEADER)
        out_stream->codec->flags |= CODEC_FLAG_GLOBAL_HEADER;
fail:
    return -1;
}



static void * open_output2(void *data) {
     const char *filename = (const char *) data;
#if LOCAL_FILE
     filename = "/sdcard/gamelive.flv";
#endif
    ALOGI("%s entry start connect %s", __FUNCTION__, filename);
    //输出（Output）
    avformat_alloc_output_context2(&ofmt_ctx, NULL, "flv", filename); //RTMP

    //avformat_alloc_output_context2(&ofmt_ctx, NULL, "mpegts", out_filename);//UDP
    if (!ofmt_ctx) {
        printf( "Could not create output context\n");
        ret = AVERROR_UNKNOWN;
        goto end;
    }

    //if (!(ofmt->flags & AVFMT_NOFILE)) {
        ret = avio_open(&ofmt_ctx->pb, filename, AVIO_FLAG_WRITE);
        if (ret < 0) {
            printf( "Could not open output URL '%s'", filename);
            goto end;
        }
    //}
    ALOGI("%s entry connect %s finish", __FUNCTION__, filename);
    g_rtmp_connected = 1;
    return NULL;


end:
    end_all();
    return NULL;
}


int find_start_code(uint8_t *data, int start_find_i, int size) {
    int i = start_find_i ;
    for( ; i + 3 < size ; i++ ) {
        if (data[i] == 0
            && data[i+1] == 0
            && data[i+2] == 0
            && data[i+3] == 1) 
            return i;
    }
    return size;
}


int remove_prevent_code(uint8_t *data, int nal_start_i, int nal_end_i, uint8_t *nal_buf) {
    int i = nal_start_i ;
    int j = 0;

    nal_buf[j++] = data[i++];
    nal_buf[j++] = data[i++];
    nal_buf[j++] = data[i++];
    nal_buf[j++] = data[i++];

    for ( ; i + 2 < nal_end_i; ){
        //if (data[i] == 0 
        //    && data[i+1] == 0 
        //    && data[i+2] == 3 ){
        //    nal_buf[j++] = 0;
        //    nal_buf[j++] = 0;
        //    i+=3;
        //} else 
        {
            nal_buf[j++] = data[i++];
        }
    }
    nal_buf[j++] = data[i++];
    nal_buf[j++] = data[i++];
    setNalHeader(nal_buf, j-4);

    return j;
}

void putBuffer(int isVideo, void *data, int size, int64_t ts, int flags) {


    int64_t now_time  = 0;
    if ( g_start_time > 0 ) {
        now_time = av_gettime() - g_start_time;
    } else {
        now_time = 0;
    }
    ts = now_time;

    //printf("putBuffer %x size %d pts %lld", flags,size, ts);
    if (!g_rtmp_connected) {
        ALOGW("not connect");
        // not connect , just abandon data
        // TO think
        return;
    }
    if (isVideo) {
        g_put_vpts = ts;
        g_put_vframe_count++;
        g_put_vsize += size;
    } else {
        g_put_asize += size;
        g_put_apts = ts;
    }

    pthread_mutex_lock(&put_mutex); 
    //printf("putBuffer 222 %x %d %lld", flags,size, ts);

    AVPacket p2;
    av_init_packet(&p2);
    p2.flags= flags;

    //p2.pts = ts;
    //p2.pts = now_time;
    float fps = 0.0;
    int bitrate = 0;
    if ( isVideo )  {
        if ( first_vpts == -1) {
            first_vpts = ts;
        } 
        p2.duration = (int)(ts - last_vts);
        p2.pts = ts - first_vpts ;
        p2.stream_index = videoindex; //pkt.stream_index;
        last_vts = ts;
        if (p2.pts/1000 != 0) {
            fps = (float)(g_put_vframe_count * 1000)/(float)(p2.pts/1000);
            bitrate = g_put_vsize / (p2.pts/1000);
        }

    } else {
        if ( first_apts == -1) {
            first_apts = ts;
        } 

        p2.duration = (int)(ts - last_ats);
        p2.pts = ts - first_apts ;
        p2.stream_index = audioindex; //pkt.stream_index;
        last_ats = ts;
        if (p2.pts/1000 != 0) {
            fps = -1.0;
            bitrate = g_put_asize / (p2.pts/1000);
        }
    }

    //ALOGI("putBuffer %s  %x size %d pts %lld, dur %d, fps %f, bitrate %dK/s  diff : %lld - %lld = %lld ", 
    //    isVideo?"V":"A", flags, size, ts,
    //    p2.duration/1000, 
    //    fps, bitrate,
    //    g_put_apts/1000, g_put_vpts/1000, (g_put_apts - g_put_vpts)/1000);



#if 1 //donot need remove prevent
    p2.dts = p2.pts;
    p2.data = av_malloc(size );
    memcpy(p2.data , data, size );
    p2.size = size ;

    if (!isVideo) {
        packet_queue_put(&audioq, &p2);
    } else {
        int nal_start_i = 0;
        int nal_end_i;
        int nal_size;

        while (nal_start_i + 4 < size) {
            nal_end_i = find_start_code(p2.data, nal_start_i + 4, size);
            setNalHeader(&(p2.data[nal_start_i]), nal_end_i-nal_start_i-4);
            nal_start_i = nal_end_i;
        }

        packet_queue_put(&videoq, &p2);
    }
#else // remove prevent
    p2.dts = p2.pts;
    p2.data = av_malloc(size );

    if (!isVideo) {
        memcpy(p2.data , data, size );
        p2.size = size ;
        packet_queue_put(&audioq, &p2);
    } else {
        int nal_start_i = 0;
        int nal_end_i;
        int nal_size;
        uint8_t *my_nal_buf = av_malloc(size);

        while (nal_start_i + 4 < size) {
            nal_end_i = find_start_code(data, nal_start_i + 4, size);

            nal_size = remove_prevent_code(data, nal_start_i, nal_end_i, my_nal_buf);

            // packet nal buf

            AVPacket p;
            av_init_packet(&p);
            p.flags = p2.flags;
            p.pts = p2.pts;
            p.duration = p2.duration;
            p.stream_index =  videoindex;

            //printf("memcpy  %d", nal_size);
            p.data = av_malloc(size);
            p.size = 0;
            memcpy(&p.data[p.size] , my_nal_buf, nal_size);
            p.size += nal_size;
            packet_queue_put(&videoq, &p);

            nal_start_i = nal_end_i;

        }
        av_free(my_nal_buf);
    }
#endif

#if 0
        int nal_size = size  - 4;
        setNalHeader(p2.data, nal_size);
//#else
        //TODO: annex-b byte stream format
    static int first_send_video = 0;
    if ( first_send_video > 0) {
        AVPacket sps_p ;
        AVPacket pps_p ;

    int spsSize = g_spspps[1];
    int ppsSize = g_spspps[spsSize + 3];
    uint8_t *sps_start =  &g_spspps[2];
    uint8_t *pps_start = &g_spspps[2 + spsSize + 2];

        // SPS packet
        av_init_packet(&sps_p);
        sps_p.pts = 0;
        sps_p.data = av_malloc(spsSize + 4);
        setNalHeader(sps_p.data, spsSize);
        memcpy(&(sps_p.data[4]), sps_start, spsSize);
        sps_p.size = spsSize + 4;
        sps_p.stream_index = videoindex;
        packet_queue_put(&videoq, &sps_p);

        // PPS packet
        av_init_packet(&pps_p);
        pps_p.data = av_malloc(ppsSize + 4);
        pps_p.pts = 0;
        setNalHeader(pps_p.data, ppsSize);
        memcpy(&(pps_p.data[4]), pps_start, ppsSize);
        pps_p.size = ppsSize + 4;
        pps_p.stream_index = videoindex;
        packet_queue_put(&videoq, &pps_p);


        first_send_video --;

        // send first sps,pps
        //AVCodecContext *dest = out_stream->codec;
        //p2.data = av_malloc(size + g_spspps_size);
        //p2.data[0] = p2.data[1] = p2.data[2] = 0;
        //p2.data[3] = 0x01;
        //memcpy(&p2.data[4], dest->extradata, dest->extradata_size);

        //memcpy(&p2.data , g_spspps, g_spspps_size);
        //memcpy(&p2.data[g_spspps_size] , data, size );
        //p2.data[g_spspps_size + 0] = p2.data[g_spspps_size + 1] = p2.data[g_spspps_size + 2] = 0;
        //p2.data[g_spspps_size + 3] = 0x01;
        //p2.size = size + g_spspps_size ;

    } else {
        //p2.data[0] = p2.data[1] = p2.data[2] = 0;
        //p2.data[3] = 0x01;
    }
#endif

    //printf( "putBuffer flags %x %x now %lld pts %lld dts %lld duration %d  stream_index %d pos %lld size %d data %p\n",
    //    flags ,p2.flags, now_time, p2.pts, p2.dts, p2.duration, p2.stream_index, p2.pos, p2.size, p2.data);

    //printf( "after rescale flags %x %x now %lld pts %lld dts %lld duration %d  stream_index %d pos %lld size %d data %p\n",
    //    flags ,p2.flags, now_time, p2.pts, p2.dts, p2.duration, p2.stream_index, p2.pos, p2.size, p2.data);
    //printf("start put end\n");
    //printf("put end\n");
    
    pthread_mutex_unlock(&put_mutex); 
}


static void *thread_send_func(void *data) {
    AVPacket pkt;
    int serial;
    static int64_t report_time = 0 ;


    ALOGI("%s %d now ", __FUNCTION__, __LINE__);
    while (!g_rtmp_connected) {
    ALOGI("%s %d now ", __FUNCTION__, __LINE__);
        if (rtmp_stop) goto end;
        printf(" wait rtmp connect ");
        av_usleep(50000);
    }

    g_start_time = av_gettime();
 
   //Dump Format------------------
    av_dump_format(ofmt_ctx, 0, g_out_filename, 1);

    ALOGI("%s %d now ", __FUNCTION__, __LINE__);
    //写文件头（Write file header）
    ret = avformat_write_header(ofmt_ctx, NULL);
    if (ret < 0) {
        printf( "Error occurred when opening output URL\n");
        goto end;
    }

    ALOGI("%s %d now ", __FUNCTION__, __LINE__);

     while (1) {
        if (rtmp_stop) goto end_stop;
        int idle_dur = 0;

         while(1) {
            AVPacket *apkt = packet_queue_peek(&audioq);
            AVPacket *vpkt = packet_queue_peek(&videoq);
            if ( apkt != NULL &&
                (vpkt ==NULL || (apkt->pts >= vpkt->pts)) ){
                if (1 == packet_queue_get(&audioq, &pkt, 0, &serial))
                    break;
                else 
                    ALOGW("SHOULD NOT HERE got audioq failed ");
            } else if ( vpkt != NULL &&
                (apkt ==NULL || (vpkt->pts > apkt->pts)) ){
                if (1 == packet_queue_get(&videoq, &pkt, 0, &serial))
                    break;
                else 
                    ALOGW("SHOULD NOT HERE got videoq failed ");
            }

            if (apkt != NULL && vpkt != NULL){
                ALOGW("SHOULD NOT HERE apkt %p  vpkt %p ", apkt, vpkt);
            }

            //if (1 == packet_queue_get(&audioq, &pkt, 0, &serial))
            //    break;
            //if (1 == packet_queue_get(&videoq, &pkt, 0, &serial))
            //    break;

            if (rtmp_stop) goto end_stop;
            usleep(10000);
            idle_dur += 10;
            if (idle_dur > 1000 && (idle_dur %1000 == 0)) {
                ALOGI("not data , idle %d", idle_dur);
            }
         }

    


    int64_t now_time = av_gettime() - g_start_time;

    if (now_time - report_time > 1000000) {
        ALOGI("send:a %lldms(%lldK, %d), v %lldms(%lldK, %d/%d), br %lld diff %lld  lelf : a %d(%d) , v %d(%d)", 
            g_send_apts, g_send_asize/1000, g_send_apacket,
            g_send_vpts, g_send_vsize/1000, g_send_drop_vpack, g_send_vpacket,
            (g_send_asize + g_send_vsize)/(now_time/1000),
            (g_send_apts - g_send_vpts),
            audioq.size, audioq.nb_packets, videoq.size, videoq.nb_packets);
        report_time = now_time;

    }


    if(pkt.stream_index==videoindex){
            AVRational time_base_q={1,AV_TIME_BASE};
            int64_t pts_time = av_rescale_q(pkt.dts, in_time_base, time_base_q);
            //int64_t now_time = av_gettime() - g_start_time;
            //printf(" pts_time %lld now_time %lld, sleep %lld", pts_time, now_time, pts_time-now_time);
            if (pts_time > now_time) {
                //if ((pts_time-now_time)/1000 > 200) {
                //    ALOGW("--- pts_time %lld now_time %lld, sleep %lld ms", pts_time, now_time, (pts_time-now_time)/1000);
                //} else if ((pts_time-now_time)/1000 > 5) {
                //    ALOGI("pts_time %lld now_time %lld, sleep %lld ms", pts_time, now_time, (pts_time-now_time)/1000);
                //}

                //av_usleep(pts_time - now_time);
                if ((pts_time - now_time) > 5000)
                    av_usleep((pts_time - now_time) - 5000);
            }
    }

    pkt.pts = av_rescale_q_rnd(pkt.pts, in_time_base, out_time_base, (int)(AV_ROUND_NEAR_INF|AV_ROUND_PASS_MINMAX));
    pkt.dts = av_rescale_q_rnd(pkt.dts, in_time_base, out_time_base, (int)(AV_ROUND_NEAR_INF|AV_ROUND_PASS_MINMAX));
    pkt.duration = av_rescale_q(pkt.duration, in_time_base, out_time_base);
    pkt.pos = -1;

    //static int v_frame_index = 0;
    //pkt.pts = v_frame_index * 25;
    //pkt.dts = pkt.pts;
    //pkt.duration = 25;
    //v_frame_index++;

    int64_t adiff = g_send_apts - now_time/1000;
    int64_t vdiff = g_send_vpts - now_time/1000;
    int64_t avdiff = g_send_apts - g_send_vpts;

    if (abs(adiff) > 500 || abs(vdiff) > 500 || abs(avdiff) > 500) 
    {
        ALOGI("diff s %lld a %lld, v %lld,  as %lld, vs %lld av %lld",
            now_time/1000, g_send_apts, g_send_vpts, adiff, vdiff, avdiff);
    }

    //if (avdiff < -500 && pkt.stream_index == videoindex) {
    //    g_send_drop_vpack ++;
    //    ALOGI("audio too late %lld, drop this video packet %lld", avdiff, g_send_vpts);
    //   av_free_packet(&pkt);
    //   continue;
    //}

    //ALOGI("stream %s flags %x size %d pts %lld dts %lld duration %lld",
    //    pkt.stream_index==videoindex?"V":"A", pkt.flags, pkt.size, pkt.pts, pkt.dts, pkt.duration);




        if(pkt.stream_index==videoindex){
            g_send_vpts = pkt.pts;
            g_send_vsize += pkt.size;
            g_send_vpacket++;
#if 1
            static int tmp_i = 0;
            if ((tmp_i %60) == 0) {
             int i ;
            char tmp[1024];
            char tmp2[12];
            tmp[0] = '\0';
            for(i = 0; i < 32 && i < pkt.size; i++) {
                sprintf(tmp2, "%02x ", pkt.data[i]);
                strcat(tmp, tmp2);
            }
            printf("write frame ts %lld len %d data: %s\n", pkt.pts, pkt.size, tmp);
            }
#endif

            //ALOGV("send Video %lld  size %d dur %d\n",pkt.pts, pkt.size, pkt.duration);
        } else {
            g_send_apts = pkt.pts;
            g_send_asize += pkt.size;
            g_send_apacket++;
            //ALOGV("send Audio %lld  size %d dur %d\n",pkt.pts, pkt.size, pkt.duration);
            //ALOGI("%s %d now ", __FUNCTION__, __LINE__);

        }

        //ALOGI("send avsync %lld - %lld = %lld ", g_send_apts, g_send_vpts, g_send_apts - g_send_vpts);

#if LOCAL_FILE
        if ( pkt.dts > 20000) {
            break;
        }
#endif

#if 0
        if (pkt.flags == 9) {
             int i ;
            char tmp[65*7200*4];
            char tmp2[12];
            tmp[0] = '\0';
            //ALOGI("i %d %d", i, __LINE__);
            for(i = 0; i < 65*7000 && i < pkt.size; i++) {
                //ALOGI("i %d %d", i, __LINE__);
                sprintf(tmp2, "%x ", pkt.data[i]);
                //ALOGI("i %d %d", i, __LINE__);
                //ALOGI("i %d %d %s, %s", i, __LINE__, tmp, tmp2);
                strcat(tmp, tmp2);
                //ALOGI("i %d %d", i, __LINE__);
                if (i %32 == 0)
                    strcat(tmp, "\n");
                //ALOGI("i %d %d", i, __LINE__);
            }
            //ALOGI("i %d %d", i, __LINE__);
            printf("keyframe write frame ts %lld len %d data: %s", pkt.pts, pkt.size, tmp);
            //av_free_packet(&pkt);
            //continue;
        }
#endif
       

       //ret = av_write_frame(ofmt_ctx, &pkt);
       ret = av_interleaved_write_frame(ofmt_ctx, &pkt);
        if (ret < 0) {
            ALOGW( "Error muxing packet ret = %d %s, avsync %lld - %lld = %lld  \n", ret,
                (pkt.stream_index==videoindex)?"V":"A",
                g_send_apts, g_send_vpts, g_send_apts - g_send_vpts
                );
            //break;
        }
        
       av_free_packet(&pkt);
    }
end_stop:
     ALOGI("Write file trailer");
    //写文件尾（Write file trailer）
    av_write_trailer(ofmt_ctx);
end:
    end_all();
}



int start_send_data() {
    ALOGI("%s entry", __FUNCTION__);
    printf("output_start thread start");
    //打开输出URL（Open output URL）
    //if (!(ofmt->flags & AVFMT_NOFILE)) {
    //    ret = avio_open(&ofmt_ctx->pb, out_filename, AVIO_FLAG_WRITE);
    //    if (ret < 0) {
    //        printf( "Could not open output URL '%s'", out_filename);
    //        goto end;
    //    }
    //}
    pthread_create (&g_send_thread, NULL, &thread_send_func, NULL);

 
    return 0;

end:
    return -1;
}

int rtmp_sender_init()
{

    ALOGI("%s entry", __FUNCTION__);

    init_gparmeters();

    init();
    rtmp_stop = 0;

    ALOGI("%s %d finish",__FUNCTION__, __LINE__);

    return 0;
}


int connect_rtmp(const char *filename) {
    ALOGI("%s entry %s", __FUNCTION__, filename);
    // TODO: maybe timeout while start_send_data
    g_out_filename = strdup(filename);
    pthread_create (&g_rtmp_init_thread, NULL, &open_output2, g_out_filename);
    //open_output2(NULL);
}

void rtmp_sender_stop() {
    void*status;
    ALOGI("%s entry", __FUNCTION__);
    rtmp_stop = 1;

    if (g_rtmp_init_thread)
        pthread_join(g_rtmp_init_thread ,&status);
    if (g_send_thread)
        pthread_join(g_send_thread,&status);
    if ( g_out_filename)
        free(g_out_filename);

    packet_queue_destroy(&audioq);
    packet_queue_destroy(&videoq);

    init_gparmeters();
    ALOGI("%s finish", __FUNCTION__);
}



