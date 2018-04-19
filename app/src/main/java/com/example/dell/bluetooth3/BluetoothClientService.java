package com.example.dell.bluetooth3;




import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BluetoothClientService extends Service {

    /** 搜索到的远程设备集合 **/
    private List<BluetoothDevice> discoveredDevices = new ArrayList<>();

    /** 蓝牙适配器 **/
    public final static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    /** 蓝牙通讯线程 **/
    private BluetoothCommonThread commonThread;

    public static boolean lyshsj=true;


    private List<BluetoothDevice> deviceList = new ArrayList<>();




    Handler mhandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if ((!bluetoothAdapter.isDiscovering())&&lyshsj){
            Intent startSearchIntent = new Intent(BluetoothTools.ACTION_START_DISCOVERY);
            sendBroadcast(startSearchIntent);
            mhandler.postDelayed(this,8000);}

        }
    };



    /** 控制信息广播的接收器 **/
    private BroadcastReceiver controlReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (BluetoothTools.ACTION_START_DISCOVERY.equals(action)) {
                /** 开始搜索 **/
                bluetoothAdapter.startDiscovery();	/** 开始搜索 **/
                Toast.makeText(getApplicationContext(),"开始搜索",Toast.LENGTH_SHORT).show();

            } else if (BluetoothTools.ACTION_SELECTED_DEVICE.equals(action)) {
                /** 选择了连接的服务器设备 **/
                BluetoothDevice device = (BluetoothDevice)intent.getExtras().get(BluetoothTools.DEVICE);

                /** 开启设备连接线程 **/
                new BluetoothClientConnThread(handler, device).start();
            } else if (BluetoothTools.ACTION_STOP_SERVICE.equals(action)) {
                /** 停止后台服务 **/
                if (commonThread != null) {
                    commonThread.isRun = false;
                }
                stopSelf();

            } else if (BluetoothTools.ACTION_DATA_TO_SERVICE.equals(action)) {
                /** 获取数据 **/
                TransmitBean data = (TransmitBean)intent.getExtras().getSerializable(BluetoothTools.DATA);
                if (commonThread != null) {
                    commonThread.write(data.getByte());
                }

            }
        }
    };



    /** 蓝牙搜索广播的接受器 **/

    private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            /** 获取广播的Action **/
            String action = intent.getAction();


            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                /** 开始搜索 **/
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                /** 发现远程蓝牙设备，获取设备 **/
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                discoveredDevices.add(bluetoothDevice);

                for (int i = 0 ; i<100;i++){
                    for (int j = 0 ; j<100;j++){
                        ;
                    }
                }

                /** 发送发现设备广播 **/
                Intent deviceListIntent = new Intent(BluetoothTools.ACTION_FOUND_DEVICE);
                deviceListIntent.putExtra(BluetoothTools.DEVICE, bluetoothDevice);
                sendBroadcast(deviceListIntent);


            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                /** 搜索结束 **/
                if (discoveredDevices.isEmpty()) {
                    /** 若未找到设备，则发动未发现设备广播 **/
                    Intent foundIntent = new Intent(BluetoothTools.ACTION_NOT_FOUND_SERVER);
                    sendBroadcast(foundIntent);

                    mhandler.postDelayed(runnable,8000);

                }

            }
            else if (BluetoothTools.ACTION_FOUND_DEVICE.equals(action))
            {
                BluetoothDevice device = (BluetoothDevice)intent.getExtras().get(BluetoothTools.DEVICE);
                try {
                    if (device.getName().equals("SuoAo")) {
                        deviceList.add(0, device);
                        Toast.makeText(getApplicationContext(),"找到SuoAo",Toast.LENGTH_SHORT).show();
                    }
                    else{handler.postDelayed(runnable,8000);}

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"无法找到SuoAo",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    /** 接受其他线程的Handler **/
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            /** 处理消息 **/
            switch (msg.what) {
                case BluetoothTools.MESSAGE_CONNECT_ERROR:
                    /** 连接错误，发送连接错误广播 **/
                    Intent errorIntent = new Intent(BluetoothTools.ACTION_CONNECT_ERROR);
                    sendBroadcast(errorIntent);
                    break;
                case BluetoothTools.MESSAGE_CONNECT_SUCCESS:
                    /** 连接成功，开启通讯线程 **/
                    commonThread = new BluetoothCommonThread(handler, (BluetoothSocket)msg.obj);
                    commonThread.start();

                    /** 发送连接成功广播 **/
                    Intent succIntent = new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS);
                    sendBroadcast(succIntent);
                    lyshsj=false;
                    break;
                case BluetoothTools.MESSAGE_READ_OBJECT:
                    /** 读取到对象，发送数据广播（包含数据对象）**/
                    TransmitBean data1 = new TransmitBean();
                    Intent sendDataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_AVR);
                    data1.setByte((byte[]) msg.obj);
                    sendDataIntent.putExtra(BluetoothTools.DATA1, data1);
                    sendBroadcast(sendDataIntent);

                    break;
            }
            super.handleMessage(msg);
        }
    };

    public BluetoothCommonThread getBluetoothCommonThread() {
        return commonThread;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {

        super.onStart(intent, startId);
    }
    @Override
    public void onCreate() {

        Toast.makeText(getApplicationContext(),"Service已打开", Toast.LENGTH_SHORT).show();

        /** discoveryReceiver的IntentFilter **/
        IntentFilter discoveryFilter = new IntentFilter();
        discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        discoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);

        /** controlReceiver的IntentFilter **/
        IntentFilter controlFilter = new IntentFilter();
        controlFilter.addAction(BluetoothTools.ACTION_START_DISCOVERY);
        controlFilter.addAction(BluetoothTools.ACTION_SELECTED_DEVICE);
        controlFilter.addAction(BluetoothTools.ACTION_STOP_SERVICE);
        controlFilter.addAction(BluetoothTools.ACTION_DATA_TO_SERVICE);

        /** 注册BroadcastReceiver **/
        registerReceiver(discoveryReceiver, discoveryFilter);
        registerReceiver(controlReceiver, controlFilter);

        super.onCreate();

    }
    @Override
    public void onDestroy() {
        if (commonThread != null) {
            commonThread.isRun = false;
        }
        /** 解除绑定 **/
        unregisterReceiver(discoveryReceiver);
        super.onDestroy();
    }


}