package com.example.dell.bluetooth3;

import java.io.Serializable;

public class TransmitBean implements Serializable {


    private byte[] byteCS=new byte[1024];

    public void setByte(byte[] bytes) {
        this.byteCS = bytes;
    }

    public byte[] getByte(){

        return this.byteCS;
    }
}