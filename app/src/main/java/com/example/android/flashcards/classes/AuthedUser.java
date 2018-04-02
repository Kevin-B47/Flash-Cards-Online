package com.example.android.flashcards.classes;

import java.util.HashMap;

/**
 * Created by Kevin on 2/28/2018.
 */

public class AuthedUser {
    private String email = "invalid@invalid.com";
    private String displayName = "Offline User";
    private String token = "";
    private int clientid = -1;
    private int serverid = -1;

    public AuthedUser(String email,String displayName, String token, int clientid, int serverid){
        this.email = email;
        this.displayName = displayName;
        this.token = token;
        this.clientid = clientid;
        this.serverid = serverid;
    }

    public AuthedUser(String email, String displayName, int clientid){
        this.email = email;
        this.displayName = displayName;
        this.clientid = clientid;
    }

    public AuthedUser(){}

    public void UpdateToken(String token){
        this.token = token;
    }

    public String GetToken(){
        return this.token;
    }

    public HashMap<String, Object> GetInfo(){
        HashMap<String,Object> arr = new HashMap<>();
        arr.put("email",this.email);
        arr.put("token",this.token);
        arr.put("serverid",this.serverid);
        return arr;
    }

    public int GetServerID(){
        return this.serverid;
    }

    public String GetName(){
        return this.displayName;
    }

    public String GetEmail(){ return this.email; }

}
