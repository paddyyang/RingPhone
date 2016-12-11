package com.pyzed.ringphone;

/**
 * Created by ygd on 2016/12/9.
 */

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * 获取去电号码服务
 */
public class OutgoingCallService extends Service {

    private static final String TAG = "OutgoingCallService";

    /**
     * 去电 Action
     */
    private static final String OUTGOING_ACTION = "android.intent.action.NEW_OUTGOING_CALL";

    /**
     * 去电广播接收器
     */
    private MyPhoneStateReceiver myPhoneStateReceiver;

    @Override
    public IBinder onBind(Intent intent) {

        Log.d(TAG, "onBind !");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate !");
        // 获取去电号码
        getOutgoingCall();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 不获取去电号码
        Log.d(TAG, "onDestroy !");
        getOutgoingCallCancel();
    }

    /**
     * 获取去电号码
     */
    private void getOutgoingCall() {
        IntentFilter intentFilter = new IntentFilter();
        // 监听去电广播
        intentFilter.addAction(OUTGOING_ACTION);
        myPhoneStateReceiver = new MyPhoneStateReceiver();
        // 动态注册去电广播接收器
        registerReceiver(myPhoneStateReceiver, intentFilter);
    }

    /**
     * 不获取去电号码
     */
    private void getOutgoingCallCancel() {
        // 取消注册去电广播接收器
        unregisterReceiver(myPhoneStateReceiver);
    }

    /**
     * 监听去电广播
     */
    class MyPhoneStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取去电号码
            String outgoingNumber = getResultData();
            Toast.makeText(context, "去电号码是：" + outgoingNumber, Toast.LENGTH_LONG).show();
        }
    }
}
