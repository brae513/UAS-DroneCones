package com.example.uastrainingcones;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Cone> cones;
    ArrayList<TextView> coneText;
    ArrayList<TextView> statusText;
    ArrayList<ImageView> coneImages;
    BluetoothHandler btHandler;
    BluetoothChatService btChatService;
    BluetoothAdapter btAdapter;
    TextView status;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btHandler = new BluetoothHandler();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btChatService = new BluetoothChatService(this,test);
        setContentView(R.layout.start_main);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        setUpStartScreen();
    }

    public void setUpMainScreen(){
        Button reset = findViewById(R.id.reset_btn);
        reset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                for(int i=0;i<cones.size();i++){
                    //cones.get(i).reset();
                }
                send("reset");
                updateCones();
            }
        });

        Button disconnect = findViewById(R.id.disconnect_btn);
        disconnect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setContentView(R.layout.start_main);
                setUpStartScreen();
            }
        });

        //Setups the logic for the check status button
        Button check_status = findViewById(R.id.status_check);
        check_status.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Error checking function
                if (btChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    //Checks if the phone bluetooth is turned on
                    if(!mBluetoothAdapter.isEnabled()){
                        status.setText("Phone bluetooth is disabled. Please enable to use the app.");
                        coneText.get(0).setText("Cone 1");
                        statusText.get(0).setText("Status: Error");
                        coneImages.get(0).setColorFilter(Color.RED);
                        coneImages.get(0).setVisibility(View.VISIBLE);
                    }
                    //If the phone bluetooth is turned on and we get here, it means we have no connection to the Raspberry Pi
                    else{
                        status.setText("Connection to raspberry pi is lost. \nPlease ensure you're in range and the cone is powered.");
                        coneText.get(0).setText("Cone 1");
                        statusText.get(0).setText("Status: Error");
                        coneImages.get(0).setColorFilter(Color.RED);
                        coneImages.get(0).setVisibility(View.VISIBLE);
                    }
                }
                else {
                    updateCones();
                    //status.setText(btChatService.getState());
                }
            }
        });


        //Update the cones in the UI
        status = findViewById(R.id.status);

        coneText = new ArrayList<>();
        statusText = new ArrayList<>();
        coneImages = new ArrayList<>();

        coneText.add((TextView) findViewById(R.id.cone1_txt));
        coneText.add((TextView) findViewById(R.id.cone2_txt));
        coneText.add((TextView) findViewById(R.id.cone3_txt));
        coneText.add((TextView) findViewById(R.id.cone4_txt));
        coneText.add((TextView) findViewById(R.id.cone5_txt));

        statusText.add((TextView) findViewById(R.id.cone1_status));
        statusText.add((TextView) findViewById(R.id.cone2_status));
        statusText.add((TextView) findViewById(R.id.cone3_status));
        statusText.add((TextView) findViewById(R.id.cone4_status));
        statusText.add((TextView) findViewById(R.id.cone5_status));

        coneImages.add((ImageView) findViewById(R.id.image1));
        coneImages.add((ImageView) findViewById(R.id.image2));
        coneImages.add((ImageView) findViewById(R.id.image3));
        coneImages.add((ImageView) findViewById(R.id.image4));
        coneImages.add((ImageView) findViewById(R.id.image5));

        //Debug function to let you click on the cone status to tell the raspberry Pi it's triggered. Not used in code for Pi but good for helping debug
        for(int i=0;i<coneImages.size();i++){
            coneImages.get(i).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    send("trigger");
                }
            });
        }
        cones = new ArrayList<>();

        updateCones();
    }

    public void setUpStartScreen(){
        Button connect = findViewById(R.id.connect_btn);
        connect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                connect();
                setContentView(R.layout.content_main);
                setUpMainScreen();
            }
        });
    }

    //Goes through the status array (needs to be implemented to have multiple cones) and updates based on status
    public void updateCones(){
        ArrayList<Integer> statuses = btHandler.getStatuses();
        //No cone connected
        for(int i=0;i<coneText.size();i++){
            if(i>=cones.size()){
                coneText.get(i).setText("");
                statusText.get(i).setText("");
                coneImages.get(i).setVisibility(View.INVISIBLE);
            }
            else{
                coneText.get(i).setText("Cone "+(i+1));
                for(int j=0;j<statuses.size();j++){

                }
                statusText.get(i).setText("Status:"+cones.get(i).getStatusString());
                coneImages.get(i).setColorFilter(cones.get(i).getCol());
                coneImages.get(i).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        updateCones();
        return super.onOptionsItemSelected(item);
    }

    //THIS NEEDS TO BE CHANGED
    //TO-DO
    //Connect will connect to the raspberry pi bluetooth address. This should be made to automatically detect the Bluetooth address instead of hardcoding
    public int connect(){
        String address = "B8:27:EB:52:A1:03";
        //Set<BluetoothDevice> devices = btHandler.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int port_number = 0;
        // if auto port discovery is not used, get the port
        if (!sharedPreferences.getBoolean("auto_port", true)) {
            String port_value = sharedPreferences.getString("port", "auto");
            port_number = Integer.parseInt(port_value);
        }

        // Get the BluetoothDevice object
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        btChatService.connect(device, port_number,true);

        return -1;
    }
    
    //Send messages
    public void send(String message) {
        // Check that we're actually connected before trying anything
        if (btChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(!mBluetoothAdapter.isEnabled()){
                status.setText("Phone bluetooth is disabled. Please enable to use the app.");
                coneText.get(0).setText("Cone 1");
                statusText.get(0).setText("Status: Error");
                coneImages.get(0).setColorFilter(Color.RED);
                coneImages.get(0).setVisibility(View.VISIBLE);
            }
            else{
                status.setText("Connection to raspberry pi is lost. \nPlease ensure you're in range and the cone is powered.");
                coneText.get(0).setText("Cone 1");
                statusText.get(0).setText("Status: Error");
                coneImages.get(0).setColorFilter(Color.RED);
                coneImages.get(0).setVisibility(View.VISIBLE);
            }
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            btChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            //mOutStringBuffer.setLength(0);
        }
    }

    //Parses a message sent in to get which cone # and what status
    public void parseMsg(String msg){
        ArrayList<Cone> newCones = new ArrayList<>();
        while(msg.contains(":")){
            int index = Integer.parseInt(msg.substring(0,msg.indexOf(':')));
            msg=msg.substring(msg.indexOf(":")+1);
            String curStatus;
            if(msg.contains(";")){
                curStatus = (msg.substring(0,msg.indexOf(';')));
            }
            else{
                curStatus = (msg);
            }
            int curStat = Integer.parseInt(curStatus);
            newCones.add(new Cone(""+index,curStat));
            //cones.get(index).setStatus(curStat);
            status.setText(index+":"+curStatus);
        }
        cones=newCones;
        updateCones();

        //cones.get(i);
    }

    //Handler for the bluetooth server 
    @SuppressLint("HandlerLeak")
    private final Handler test = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case(1):
                    switch(msg.arg1) {
                        case (2):
                            status.setText("Connecting");
                            break;
                        case (3):
                            status.setText("Connected");
                            send("Connected");
                            break;
                        default:
                            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            if(!mBluetoothAdapter.isEnabled()){
                                status.setText("Phone bluetooth is disabled. Please enable to use the app.");
                                coneText.get(0).setText("Cone 1");
                                statusText.get(0).setText("Status: Error");
                                coneImages.get(0).setColorFilter(Color.RED);
                                coneImages.get(0).setVisibility(View.VISIBLE);
                            }
                            else{
                                status.setText("Connection to raspberry pi is lost. \nPlease ensure you're in range and the cone is powered.");
                                coneText.get(0).setText("Cone 1");
                                statusText.get(0).setText("Status: Error");
                                coneImages.get(0).setColorFilter(Color.RED);
                                coneImages.get(0).setVisibility(View.VISIBLE);
                            }

                    }
                    break;
                case(2):
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readData = new String(readBuf, 0, msg.arg1);
                    //status.setText(readData);
                    parseMsg(readData);
                    break;
                default:
                    //throw new IllegalStateException("Unexpected value: " + msg.what);
            }
            //status.setText(""+msg.what+":"+msg.arg1);
            //send("test");
        }
    };
}
