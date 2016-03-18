package com.qike.feiyunlu.tv.presentation.view.adapter.adapterdto;

/**
 * Created by cherish on 2016/3/18.
 */
public class MessDto {


    public static final int NORMAL = 1;
    public static final int ALLROOM = 2;
    public static final int GIFT = 3;
    public static final int LIKE = 4;
    public static final int ATTENTION = 5;
    public static final int BAN = 6;

    // 消息种类： 1.普通文字弹幕 2.大喇叭弹幕（土豪送礼物，全站广播，如果有target_uid，即为土豪送礼物） 3.送出礼物 4.点赞 5.关注消息 6.禁言 （禁止用户以后发言，主播权利）
    private int type;
    private String user_id;// 发送消息人uid
    private String user_nick; // 发送消息人的昵称
    private String user_avatar;// 发送消息人的头像地址，普通消息无头像
    private String prop_id;// 送出礼物的id
    private String content;//消息内容
    private String target_uid;// 指定接收消息目标用户id


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_nick() {
        return user_nick;
    }

    public void setUser_nick(String user_nick) {
        this.user_nick = user_nick;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getProp_id() {
        return prop_id;
    }

    public void setProp_id(String prop_id) {
        this.prop_id = prop_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTarget_uid() {
        return target_uid;
    }

    public void setTarget_uid(String target_uid) {
        this.target_uid = target_uid;
    }
}
