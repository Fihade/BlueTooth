package com.example.dell.bluetooth3;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;

public class BluetoothClientConnThread extends Thread {

    private Handler serviceHandler;		//用于向客户端Service回传消息的handler
    private BluetoothDevice serverDevice;	//服务器设备
    private BluetoothSocket socket;		//通信Socket

    /** 构造函数handler、serverDevice **/
    public BluetoothClientConnThread(Handler handler, BluetoothDevice serverDevice) {
        this.serviceHandler = handler;
        this.serverDevice = serverDevice;
    }

    public void run() {
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        try {
            socket = serverDevice.createRfcommSocketToServiceRecord(BluetoothTools.PRIVATE_UUID);
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            socket.connect();

        } catch (Exception ex) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            /** 发送连接失败消息 **/
            serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
            return;
        }

        /** 发送连接成功消息，消息的obj参数为连接的socket **/
        Message msg = serviceHandler.obtainMessage();
        msg.obj = socket;
        msg.what = BluetoothTools.MESSAGE_CONNECT_SUCCESS;
        msg.sendToTarget();
    }

}
