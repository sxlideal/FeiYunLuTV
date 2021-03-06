package com.qike.feiyunlu.tv.presentation.presenter.socket;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.qike.feiyunlu.tv.module.network.Paths;
import com.qike.feiyunlu.tv.presentation.view.adapter.adapterdto.MessDto;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by cherish on 2016/3/18.
 */
public class MessSocket {


    private static boolean isShieldGift = false;
    private static boolean isShieldSystem = false;
    private static boolean isShieldContent = false;

    private static MessSocket INSTANCE;
    private Socket mSocket;
    private List<OnNewMessageListener> listeners;

    private MessSocket(  String user, String roomid ){
        listeners = new ArrayList<OnNewMessageListener>();

        String url = Paths.SOCKET_URL+"?"+"room="+roomid+"&user="+user+"&token=123";
        Log.e("test",url);
        try{
            mSocket = IO.socket(url);
        }catch (Exception e){
            Log.e("socket error", e.toString());
        }
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT, onConnect);

        mSocket.on("connect", onConnect);
        mSocket.on("message", onNewMessage);
        mSocket.on("disconnect", onDisconnect);
        mSocket.on("error", onError);

        mSocket.connect();
    }

    public void reconnect(){
        if( mSocket != null){
            mSocket.connect();
        }
    }

    public void setShieldMessage( ShieldMessageType shieldMessageType){

        switch (shieldMessageType){
            case ShieldGift:
                isShieldGift = true;
                break;
            case ShieldSystem:
                isShieldSystem = true;
                break;
            case ShieldContent:
                isShieldContent = true;
                break;
            default:
                break;
        }
    }

    public void unShieldMessage( ShieldMessageType shieldMessageType){
        switch (shieldMessageType){
            case ShieldGift:
                isShieldGift = false;
                break;
            case ShieldSystem:
                isShieldSystem = false;
                break;
            case ShieldContent:
                isShieldContent = false;
                break;
            default:
                break;
        }
    }

    public boolean getIsShield(){
        return isShieldContent || isShieldSystem || isShieldGift;
    }

    public boolean getIsChatShield(){
        return isShieldContent;
    }
    public boolean getIsSystemShield(){
        return isShieldSystem;
    }
    public boolean getIsGiftShield(){
        return isShieldGift;
    }

   public enum ShieldMessageType{
        ShieldGift,ShieldSystem,ShieldContent
    }

    public static MessSocket getSocket(String user, String roomid ){

        if( INSTANCE == null ){
            if ( INSTANCE == null){
                synchronized (MessSocket.class){
                    INSTANCE = new MessSocket(user,roomid);

                }
            }
        }
        return INSTANCE;
    }


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {


            Object obj = args[0];
            String objStr = String.valueOf(obj);
            MessDto message = JSON.parseObject(objStr, MessDto.class);

            if (isShieldContent && message.getType() == MessDto.NORMAL){
                return;
            }else if(isShieldGift && message.getType() == MessDto.GIFT ){
                return;
            }else if (isShieldSystem && message.getType() == MessDto.BAN){
                return;
            }else {
                for (OnNewMessageListener listener:listeners) {
                    listener.OnNewMessage(message);
                }
            }
        }
    };


    public void emitMessage( MessDto dto){

        String message =  JSON.toJSONString(dto);
        mSocket.emit("message",message);

    }


    public void registListener( OnNewMessageListener listener){

        if( listeners != null && !listeners.contains(listener)){
            listeners.add(listener);
        }
    }
    public void unRegistListener(OnNewMessageListener listener){
        if( listeners != null && listeners.contains(listener)){
            listeners.remove(listener);
        }
    }


    public interface OnNewMessageListener{
        public void OnNewMessage( MessDto message);
    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.e("test", "connected");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Log.e("test","connect error");
            reconnect();
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.e("test","disconnect");
            reconnect();
        }
    };

    private Emitter.Listener onError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.e("test","onerror");
            reconnect();
        }
    };

    public  void destroySocket(){

        mSocket.off();
        mSocket.disconnect();
        mSocket = null;
        listeners = null;
    }
}
