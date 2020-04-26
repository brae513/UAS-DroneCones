package com.example.uastrainingcones;

import android.graphics.Color;

public class Cone {

    private int status;
    private String id;

    public Cone(String id){
        this.id = id;
        status = 0;
    }
    public Cone(String id,int status){
        this.id = id;
        this.status = status;
    }

    // Attempts to connect to cone, returns -1 if failed, returns 1 if successful
    public int connect(){
        return 1;
    }

    public int getCol(){
        int lightCol = Color.GRAY;
        if(status == 1){
            lightCol = Color.BLUE;
        }
        else if(status == 2 ){
            lightCol = Color.GREEN;
        }
        else if(status >2){
            lightCol = Color.RED;
        }
        return lightCol;
    }

    public String getStatusString(){
        String s = "Disconnected";
        if(status == 1){
            s = "Standby";
        }
        else if(status == 2 ){
            s = "Triggered";
        }
        else if(status >2){
            s= "error";
        }
        return s;
    }
    public String getID(){
        return id;
    }

    public void setStatus(int i){
        status = i;
    }

    public void reset(){
        status = 1;

        //
        //   TODO: Implement reset signal here
        //
    }


}
