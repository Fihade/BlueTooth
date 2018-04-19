package com.example.dell.bluetooth3;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class BluetoothCommonThread extends Thread {

    private Handler serviceHandler;		/** 与Service通信的Handler **/
    private BluetoothSocket socket;
    private InputStream inStream=null;		/** 输入流 **/
    private OutputStream outStream=null;	/** 输出流 **/
    public volatile boolean isRun = true;	/** 运行标志位 **/


    /** 构造函数 **/
    public BluetoothCommonThread(Handler handler, BluetoothSocket socket) {
        this.serviceHandler = handler;
        this.socket = socket;
        try {
            this.outStream = socket.getOutputStream();
            this.inStream = socket.getInputStream();
        } catch (Exception e) {
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            /** 发送连接失败消息 **/
            serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
            e.printStackTrace();
        }
    }

    public void run() {
        int num = 0;
        byte[] buffer = new byte[1024];
        byte[] buffer_new;
        while (true) {
            if (!isRun) {
                break;
            }
            try {

                while (true) {
                    num = inStream.read(buffer);         /** 读入数据 **/
                    if (inStream.available()==0) {
                        break;
                    }  /** 短时间没有数据才跳出进行显示 **/
                }

                /** 发送成功读取到对象的消息，消息的obj参数为读取到的对象 **/
                if(num>0)
                {
                    buffer_new= Arrays.copyOfRange(buffer,0,num);
                }
                else
                {
                    buffer_new= Arrays.copyOfRange(buffer,0,1);
                }
                Message msg = serviceHandler.obtainMessage();
                msg.what = BluetoothTools.MESSAGE_READ_OBJECT;
                msg.obj = buffer_new;
                msg.sendToTarget();
            } catch (Exception ex) {
                /** 发送连接失败消息 **/
                serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
                ex.printStackTrace();
                return;
            }
        }

        /** 关闭流 **/
        if (inStream != null) {
            try {
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outStream != null) {
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** 写入一个可序列化的对象 **/
    public void write(byte[] obj) {
        try {
            outStream.flush();
            outStream.write(obj);
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

