/**
 * Created by ygd on 2016/12/9.
 */

package com.pyzed.ringphone;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
/**
 * 获取来电号码服务
 */
public class IncomingCallService extends Service {

    /**
     * 电话服务管理器
     */
    private TelephonyManager telephonyManager;

    /**
     * 电话状态监听器
     */
    private MyPhoneStateListener myPhoneStateListener;

    SoundPool soundPool;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 获取来电号码
        getIncomingCall();

        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        soundPool.load(IncomingCallService.this,R.raw.temp2,1);


        int volume = SettingsUtil.getVolumeSetting(getBaseContext());
        Log.v("RingPhone", "IncomingCallService onCreate seekbar volume is " + volume );

        Intent intent = new Intent("incomingcallservice_oncreate");
        intent.putExtra("status", "on");
        sendBroadcast(intent);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 不获取来电号码
        getIncomingCallCancel();

        Intent intent = new Intent("incomingcallservice_oncreate");
        intent.putExtra("status", "off");
        sendBroadcast(intent);

        if(soundPool != null) {
            soundPool.release();
            soundPool = null;
        }

        Log.v("RingPhone", "IncomingCallService onDestroy!");
    }

    /**
     * 获取来电号码
     */
    private void getIncomingCall() {
        // 获取电话系统服务
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener();
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * 不获取来电号码
     */
    private void getIncomingCallCancel() {
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    /**
     * 电话状态监听器
     */
    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                // 如果电话铃响
                case TelephonyManager.CALL_STATE_RINGING:
                    Toast.makeText(IncomingCallService.this, "来电号码是：" + incomingNumber, Toast
                            .LENGTH_LONG).show();

                    boolean for_every_one = SettingsUtil.getEveryOneSetting(getBaseContext());
                    boolean find_phone = SettingsUtil.findPhoneNumber(getBaseContext(),incomingNumber);
                    Log.v("RingPhone", "onCallStateChanged for_every_one = " + for_every_one + ", find_phone = " + find_phone);
                    if(!for_every_one && !find_phone) return;
                    setSound();


            }
        }
    }

    private void setSound() {
        if(soundPool != null) {
            AudioManager mAudioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
            int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);

            int volume = SettingsUtil.getVolumeSetting(getBaseContext());
            Log.v("RingPhone", "setSound seekbar volume is " + volume );
            mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, (int)(maxVolume*volume/100), 0);

            soundPool.play(1,1, 1, 0, 4, 1);
        }
    }
}
