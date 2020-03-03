package com.example.uastrainingcones;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Set;

public class BluetoothHandler {

    BluetoothAdapter btAdapter;
    BluetoothChatService btChatService;

    public BluetoothHandler(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public ArrayList<Integer> getStatuses(){
        return new ArrayList<>();
    }
    public void sendReset(){

    }

    public ArrayList<BluetoothDevice> getDevices(){
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        ArrayList<BluetoothDevice> out = new ArrayList<>();
        /*
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                out.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            out.add("no devices");
        }*/
        for (BluetoothDevice device : pairedDevices) {
            out.add(device);
        }
        return out;
    }

    private final Handler test = new Handler(){
        @Override
        public void handleMessage(Message msg){

        }
    };


}
