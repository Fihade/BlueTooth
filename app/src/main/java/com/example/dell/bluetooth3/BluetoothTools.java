package com.example.dell.bluetooth3;

import android.bluetooth.BluetoothAdapter;



import java.util.UUID;

/** 蓝牙工具类 **/
public class BluetoothTools{



    /** UUID **/
    public static final UUID PRIVATE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /** 存放在Intent中的设备对象 **/
    public static final String DEVICE = "DEVICE";

    /** Intent中的数据 **/
    public static final String DATA = "DATA";
    public static final String DATA1 = "DATA1";

    /** Action类型标识符 **/
    public static final String ACTION_NOT_FOUND_SERVER = "ACTION_NOT_FOUND_DEVICE";
    public static final String ACTION_START_DISCOVERY = "ACTION_START_DISCOVERY";
    public static final String ACTION_FOUND_DEVICE = "ACTION_FOUND_DEVICE";
    public static final String ACTION_SELECTED_DEVICE = "ACTION_SELECTED_DEVICE";
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
    public static final String ACTION_DATA_TO_SERVICE = "ACTION_DATA_TO_SERVICE";
    public static final String ACTION_DATA_TO_AVR = "ACTION_DATA_TO_AVR";
    public static final String ACTION_CONNECT_SUCCESS = "ACTION_CONNECT_SUCCESS";
    public static final String ACTION_CONNECT_ERROR = "ACTION_CONNECT_ERROR";

    /** Mseeage类型标识符 **/
    public static final int MESSAGE_CONNECT_SUCCESS = 0x00000002;
    public static final int MESSAGE_CONNECT_ERROR = 0x00000003;
    public static final int MESSAGE_READ_OBJECT = 0x00000004;


}
