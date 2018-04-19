package com.example.dell.bluetooth3;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button button1;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        Intent startService = new Intent(MainActivity.this, BluetoothClientService.class);
        startService(startService);

        Toast.makeText(MainActivity.this,"打开Service",Toast.LENGTH_SHORT).show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button)findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                /** 打开蓝牙 **/
                if (!mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.enable();
                }

                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Two.class);
                startActivity(intent);

            }
        });
    }
}