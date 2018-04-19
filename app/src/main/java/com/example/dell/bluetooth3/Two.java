package com.example.dell.bluetooth3;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.dell.bluetooth3.BluetoothClientService.bluetoothAdapter;
import static com.example.dell.bluetooth3.BluetoothClientService.lyshsj;

public class Two extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

    }

    @Override
    protected void onResume(){

        Intent startSearchIntent = new Intent(BluetoothTools.ACTION_START_DISCOVERY);
        sendBroadcast(startSearchIntent);

        lyshsj=true;
        super.onResume();
    }

    Handler mhandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(lyshsj&&(!bluetoothAdapter.isDiscovering())) {
                Intent startSearchIntent = new Intent(BluetoothTools.ACTION_START_DISCOVERY);
                sendBroadcast(startSearchIntent);
                mhandler.postDelayed(this, 8000);
            }
        }
    };


    private List<BluetoothDevice> deviceList = new ArrayList<>();

    /** 广播接受 **/
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (BluetoothTools.ACTION_NOT_FOUND_SERVER.equals(action))
            {
                Toast.makeText(Two.this,"未发现设备",Toast.LENGTH_SHORT).show();
                mhandler.postDelayed(runnable,8000);

            }
            else if (BluetoothTools.ACTION_FOUND_DEVICE.equals(action))
            {
                Toast.makeText(Two.this,"发现设备",Toast.LENGTH_SHORT).show();
                BluetoothDevice device = (BluetoothDevice)intent.getExtras().get(BluetoothTools.DEVICE);
                if(device.getName().equals("SuoAo"))
                {
                    deviceList.add(0,device);
                    Toast.makeText(Two.this,"发现SuoAo",Toast.LENGTH_SHORT).show();

                    Intent selectDeviceIntent = new Intent(BluetoothTools.ACTION_SELECTED_DEVICE);
                    selectDeviceIntent.putExtra(BluetoothTools.DEVICE, deviceList.get(0));
                    sendBroadcast(selectDeviceIntent);
                }
                else{
                    mhandler.postDelayed(runnable,8000);
                    }

            }
            else if (BluetoothTools.ACTION_CONNECT_SUCCESS.equals(action))
            {
                Toast.makeText(Two.this,"连接成功",Toast.LENGTH_SHORT).show();

                TransmitBean abc = new TransmitBean();
                byte[] a={0x12,0x12,0x12};
                abc.setByte(a);
                Intent sendDataIntent1 = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
                sendDataIntent1.putExtra(BluetoothTools.DATA,abc);
                sendBroadcast(sendDataIntent1);

            }
            else if (BluetoothTools.ACTION_CONNECT_ERROR.equals(action))
            {
                Toast.makeText(Two.this,"连接失败",Toast.LENGTH_SHORT).show();
                mhandler.postDelayed(runnable,8000);
            }
            else if (BluetoothTools.ACTION_DATA_TO_AVR.equals(action)) {
                /** 接受数据 **/
                TransmitBean data1 = (TransmitBean)intent.getExtras().getSerializable(BluetoothTools.DATA1);
                byte[] msg = data1.getByte();
                byte i[]= {0x12};
                if(Arrays.equals(msg,i)){
                Toast.makeText(Two.this,"药箱销毁成功:"+msg,Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(Two.this,"药箱销毁失败:"+msg,Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    protected void onStart(){
        deviceList.clear();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothTools.ACTION_NOT_FOUND_SERVER);
        intentFilter.addAction(BluetoothTools.ACTION_FOUND_DEVICE);
        intentFilter.addAction(BluetoothTools.ACTION_DATA_TO_AVR);
        intentFilter.addAction(BluetoothTools.ACTION_CONNECT_SUCCESS);

        registerReceiver(broadcastReceiver, intentFilter);

        super.onStart();
    }

    protected void onDestroy() {
        Intent startService = new Intent(BluetoothTools.ACTION_STOP_SERVICE);
        sendBroadcast(startService);
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }




}