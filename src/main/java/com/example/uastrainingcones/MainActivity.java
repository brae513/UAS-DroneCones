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

    public void updateCones(){
        ArrayList<Integer> statuses = btHandler.getStatuses();
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

    public int connect(){
        String address = "B8:27:EB:62:C3:CE";

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

    public void send(String message) {
        // Check that we're actually connected before trying anything
        if (btChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            return;
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
                            status.setText("Unexpected Message");
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
