package com.beta.tp3;

import android.content.Context;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class MySingleton {

    private static MySingleton mInstance;
    private static Context ctx;

    private static Socket socket;
    private static CountDownLatch latch;


    private MySingleton(Context context) {
        ctx = context;
    }

    public static synchronized MySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }


}