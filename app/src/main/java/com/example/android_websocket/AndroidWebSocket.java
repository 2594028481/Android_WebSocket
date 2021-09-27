package com.example.android_websocket;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;


public class AndroidWebSocket extends WebSocketClient {
    private static final String TAG = "AndroidWebSocketClient";

    /**
     * 构造方法中的new Draft_6455()代表使用的协议版本，这里可以不写或者写成这样即可
     * @param serverUri
     */
    public AndroidWebSocket(URI serverUri) {
//        super(serverUri, new Draft_6455());
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

        Log.d(TAG, "onOpen: 连接开启");
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "onMessage: 接受到消息");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "onClose: 连接关闭");
        Log.d(TAG, "onClose: code="+code+"\nreason="+reason+"\nremote="+remote);
    }

    @Override
    public void onError(Exception ex) {
        Log.d(TAG, "onError: 连接断开");
        ex.printStackTrace();
    }

}
