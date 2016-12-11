package com.pyzed.ringphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RingPhone";
    /**
     * 获取来电号码
     */
    //private Button btnIncomingCall;

    /**
     * 不获取来电号码
     */
    //private Button btnIncomingCallCancel;


    /**
     * 获取去电号码
     */
    //private Button btnOutgoingcall;

    /**
     * 不获取去电号码
     */
    //private Button btnOutgoingCallCancel;


    private ToggleButton toggleButton;
    private CheckBox autoCheckBox;
    private SeekBar volumeSeekBar;

    private ListView listViewPhoneNumber;
    private ArrayList<String> listPhoneNumber; //=new ArrayList<String>();
    ArrayAdapter<String> adapterListPhoneNumber;

    private Button addButton;

    private RadioGroup whoRidioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "onCreate!");
        // 实例化控件
        //btnIncomingCall = (Button) findViewById(R.id.btn_incoming_call);
        //btnIncomingCallCancel = (Button) findViewById(R.id.btn_incoming_call_cancel);

        // 设置按键监听
        //btnIncomingCall.setOnClickListener(this);
        //btnIncomingCallCancel.setOnClickListener(this);


        //btnOutgoingcall = (Button) findViewById(R.id.btn_outgoing_call);
        //btnOutgoingCallCancel = (Button) findViewById(R.id.btn_outgoing_call_cancel);
        //btnOutgoingcall.setVisibility(View.INVISIBLE);
        //btnOutgoingCallCancel.setVisibility(View.INVISIBLE);

        //btnOutgoingcall.setOnClickListener(this);
        //btnOutgoingCallCancel.setOnClickListener(this);
        //btnIncomingCall.setVisibility(View.INVISIBLE);
        //btnIncomingCallCancel.setVisibility(View.INVISIBLE);


        toggleButton=(ToggleButton)findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                toggleButton.setChecked(isChecked);
                 if(isChecked){
                     startRing(getApplicationContext());
                 } else {
                     stopRing(getApplicationContext());
                 }

                try {
                    //SharedPreferences sp =MainActivity.this.getSharedPreferences("ring_phone_service", MODE_PRIVATE);
                    //boolean b  = sp.getBoolean("auto_start", false);
                    boolean b = SettingsUtil.getAutoStartSetting(MainActivity.this);
                    Log.v(TAG, "ToggleButton test, auto start service = " + b);
                } catch (Exception ex){
                    Log.e(TAG, ex.toString());
                }
            }
        });

        autoCheckBox = (CheckBox)findViewById(R.id.autoCheckBox);
        autoCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                    //Context ctx =MainActivity.this;
                    //SharedPreferences sp =ctx.getSharedPreferences("ring_phone_service", MODE_PRIVATE);
                    //SharedPreferences.Editor editor=sp.edit();
                    //editor.putBoolean("auto_start", isChecked);
                    //editor.commit();
                    SettingsUtil.storeAutoStartSettings(MainActivity.this, isChecked);
                    Log.v(TAG, "onCheckedChanged auto restat is " + isChecked);

            }
        });
        autoCheckBox.setChecked(SettingsUtil.getAutoStartSetting(MainActivity.this));


        //volume seekbar
        volumeSeekBar = (SeekBar)findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            int percent = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                Log.v(TAG, "onProgressChanged: " + progress);
                SettingsUtil.storeVolumeSettings(MainActivity.this, progress);
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        volumeSeekBar.setProgress(SettingsUtil.getVolumeSetting(MainActivity.this));


        listViewPhoneNumber = (ListView)findViewById(R.id.list_phone_number);
        adapterListPhoneNumber=new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,getListData());
        listViewPhoneNumber.setAdapter(adapterListPhoneNumber);
        listViewPhoneNumber.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                listPhoneNumber.remove(position);
                adapterListPhoneNumber.notifyDataSetChanged();
                listViewPhoneNumber.invalidate();

                SettingsUtil.storeListNumber(MainActivity.this, listPhoneNumber);

                return false;
            }
        });


        addButton = (Button)findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText numberEdit = (EditText) findViewById(R.id.edit_add_number);
                String str_number = numberEdit.getText().toString();
                listPhoneNumber.add(str_number);
                adapterListPhoneNumber.notifyDataSetChanged();
                listViewPhoneNumber.invalidate();

                numberEdit.setText("");

                SettingsUtil.storeListNumber(MainActivity.this, listPhoneNumber);

            }
        });

        whoRidioGroup = (RadioGroup)findViewById(R.id.whoRidioGroup);
        whoRidioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.radioEveryone) {

                    SettingsUtil.storeEveryOneSetting(MainActivity.this, true);

                } else {
                    SettingsUtil.storeEveryOneSetting(MainActivity.this, false);
                }
            }
        });
        whoRidioGroup.check(SettingsUtil.getEveryOneSetting(MainActivity.this)? R.id.radioEveryone: R.id.radioFollow);


        registerServiceOnReceiver();

    }
    public class ServiceCreatedBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            String on = intent.getExtras().getString("status", "off");
            Log.v(TAG, "ServiceCreatedBroadcastReceiver: " + on);
            Boolean checked = (on.equals("on"));
            if(toggleButton != null)
                toggleButton.setChecked(checked);
        }
    }
    public List<String> getListData(){

        listPhoneNumber = SettingsUtil.getListNumber(MainActivity.this);
        return listPhoneNumber;
    }


    private void registerServiceOnReceiver(){

        ServiceCreatedBroadcastReceiver receiver = new ServiceCreatedBroadcastReceiver();
        IntentFilter filter = new IntentFilter("incomingcallservice_oncreate");
        registerReceiver(receiver, filter);
    }

    @Override
    public void onClick(View v) {

        Log.v(TAG, "onClick!");
        switch (v.getId()) {
            /*case R.id.btn_incoming_call:
                startRing(MainActivity.this);
                break;
            case R.id.btn_incoming_call_cancel:
                stopRing();
                break;

            case R.id.btn_outgoing_call:
                Intent startOutgoingService = new Intent(this, OutgoingCallService.class);
                startService(startOutgoingService);
                Toast.makeText(this, "获取去电号码", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_outgoing_call_cancel:
                Intent stopOutgoingService = new Intent(this, OutgoingCallService.class);
                stopService(stopOutgoingService);
                Toast.makeText(this, "不获取去电号码", Toast.LENGTH_SHORT).show();
                break;*/
        }
    }

    public static class BootBroadcastReceiver extends BroadcastReceiver {
        //重写onReceive方法
        @Override
        public void onReceive(Context context, Intent intent) {

                Log.e(TAG, "BootBroadcastReceiver!");
                boolean isChecked = SettingsUtil.getAutoStartSetting(context);
                if(isChecked){
                    Log.v(TAG, "onReceive, can start service");
                    startRing(context);
                } else {
                    Log.v(TAG, "onReceive, not start service");
                }
        }

    }

    private static void startRing(Context context){
        Intent startIncomingService = new Intent(context, IncomingCallService.class);
        context.startService(startIncomingService);
        Toast.makeText(context, "获取来电号码", Toast.LENGTH_SHORT).show();
    }
    private  void stopRing(Context context) {
        Intent stopIncomingService = new Intent(context, IncomingCallService.class);
        context.stopService(stopIncomingService);
        Toast.makeText(this, "不获取来电号码", Toast.LENGTH_SHORT).show();
    }
}
