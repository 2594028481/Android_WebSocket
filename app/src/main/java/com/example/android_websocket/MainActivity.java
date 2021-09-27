package com.example.android_websocket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private boolean is_establishConnection;//是否建立连接
    private AndroidWebSocket androidWebSocket;
    private EditText send_webSocket;
    private int TIME_HEARTBEAT = 3 * 1000;
    private final int HEARTBEAT = 1;
    private final int TextView_message = 2;
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HEARTBEAT:
                    //开始检测连接
                    if (null != androidWebSocket) {
                        Log.d(TAG, "handleMessage: 连接状态为：" + androidWebSocket.isOpen());

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                        Date date = new Date(System.currentTimeMillis());

                        tv.append("\n"+simpleDateFormat.format(date)+"当前连接状态为:"+androidWebSocket.isOpen());
                        if (!androidWebSocket.isOpen()) {
                            Log.d(TAG, "run: androidWebSocket.getReadyState()=" + androidWebSocket.getReadyState());
                            if (androidWebSocket.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
                                Log.d(TAG, "establishConnection: 进入1");
                                try {
                                    is_establishConnection = androidWebSocket.connectBlocking();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (androidWebSocket.getReadyState().equals(ReadyState.CLOSING) || androidWebSocket.getReadyState().equals(ReadyState.CLOSED)) {
                                Log.d(TAG, "establishConnection: 进入2");
                                try {
                                    is_establishConnection = androidWebSocket.reconnectBlocking();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                androidWebSocket.reconnect();
                            }
                        }
                        mhandler.sendEmptyMessageDelayed(HEARTBEAT, TIME_HEARTBEAT);
                    } else {
                        androidWebSocket = null;
                        initWebSocket();
                    }
                    break;

                case TextView_message:
                    String ms = (String) msg.obj;
                    Log.d(TAG, "handleMessage: ms" + ms);
                    tv.append("\n" + ms);
                    break;

            }

        }
    };
    private boolean is_disconnect;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send_webSocket = findViewById(R.id.send_WebSocket);
        tv = findViewById(R.id.tv);
        initWebSocket();
    }

    private void initWebSocket() {
        //初始化连接
        URI uri = URI.create("ws://192.168.5.104:8082/wc/send/10004");//设置服务端webSocket消息地址
        androidWebSocket = new AndroidWebSocket(uri) {
            @Override
            public void onMessage(String message) {
                super.onMessage(message);
                Log.d(TAG, "onMessage: 接收到消息：" + message);
                //此处根据接收到的消息类型进行解析。判断需要做什么事情。
                Message message1 = new Message();
                message1.what = TextView_message;
                message1.obj = message;
                mhandler.sendMessage(message1);
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                //连接成功开始进行心跳判断。
                mhandler.removeMessages(HEARTBEAT);
                mhandler.sendEmptyMessageDelayed(HEARTBEAT, TIME_HEARTBEAT);
                super.onOpen(handshakedata);
            }

            @Override
            public void onError(Exception ex) {
                super.onError(ex);
                ex.printStackTrace();
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                super.onClose(code, reason, remote);
                Log.d(TAG, "onClose: code=" + code + "\nreason=" + reason + "\nremote=" + remote);
                Message message1 = new Message();
                message1.what = TextView_message;
                message1.obj = "关闭了 code=" + code + ",  reason=" + reason + ","+ (remote ? "服务器断开连接" : "本地断开连接");
                mhandler.sendMessage(message1);
            }
        };
    }

    public void establishConnection(View view) {
       /* //建立连接
        if (isestablishConnection==true){
            isestablishConnection = false;
        }else {
            isestablishConnection = true;
        }*/
        tv.append("\n" + "点击了建立连接");

        if (null == androidWebSocket) {
            initWebSocket();
        }
  /*      try {
            is_establishConnection = androidWebSocket.connectBlocking();//connectBlocking会有一个等待操作。
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        if (androidWebSocket != null && !androidWebSocket.isOpen()) {

            Log.d(TAG, "run: androidWebSocket.getReadyState()=" + androidWebSocket.getReadyState());
            if (androidWebSocket.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
                Log.d(TAG, "establishConnection: 进入1");
                try {
                    is_establishConnection = androidWebSocket.connectBlocking();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (androidWebSocket.getReadyState().equals(ReadyState.CLOSING) || androidWebSocket.getReadyState().equals(ReadyState.CLOSED)) {
                Log.d(TAG, "establishConnection: 进入2");
                        /*try {
                            is_establishConnection = androidWebSocket.reconnectBlocking();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                androidWebSocket.reconnect();
            }


        }
//        connect();


    }


    private void connect() {
        new Thread() {
            @Override
            public void run() {
             /*   try {
                    //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                    androidWebSocket.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/


            }
        }.start();

    }

    public void disconnect(View view) {
        tv.append("\n" + "点击了断开连接");
        mhandler.removeMessages(1);
        if (is_establishConnection) {
            is_establishConnection = false;
            if (null != androidWebSocket && androidWebSocket.isOpen()) {
                try {
                    androidWebSocket.closeBlocking();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    androidWebSocket = null;
                }

            }
        }

    }

    public void reconnect(View view) {
        tv.append("\n" + "点击了重新连接");
        try {
            is_establishConnection = androidWebSocket.reconnectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int mipmap = getResources().getIdentifier("image.name", "mipmap", getPackageName());

    }

    public void sendMessages(View view) {

        if (null != androidWebSocket && androidWebSocket.isOpen()) {
            String s = send_webSocket.getText().toString();
            if (s != null && s.length() > 0) {
                send_webSocket.setText("");
                androidWebSocket.send("{\"cocoNum\":,\"message\":\"" + s + "\"}");
            } else {
                Toast.makeText(view.getContext(), "请输入信息。", Toast.LENGTH_SHORT).show();
            }
        }

    }
}