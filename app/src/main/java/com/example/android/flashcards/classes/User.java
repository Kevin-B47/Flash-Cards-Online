package com.example.android.flashcards.classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.flashcards.LoginActivity;
import com.example.android.flashcards.MenuActivity;
import com.example.android.flashcards.db.UserDBHelper;
import com.example.android.flashcards.interfaces.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by Kevin on 2/13/2018.
 */


public class User {

    private static final int minPassLength = 4;
    private static final int maxPassLength = 32;
    private static final Pattern passMatcher = Pattern.compile("[a-zA-Z0-9!@#_]+");
    public static AuthedUser LoggedIn;

    private static String EncodePass(String pass){ // Regex is broken with server auth right now and there is legit no point in fixing it for a small amount of security in an app that doesn't matter
        return pass;
       // return  Base64.encodeToString(pass.getBytes(),Base64.DEFAULT);
    }

    private static boolean isEmailValid(CharSequence email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static Boolean isPassOkay(String pass){
        return pass.length() >= minPassLength && pass.length() <= maxPassLength;
    }

    private static Boolean isPassAllowed(String pass){
        return passMatcher.matcher(pass).matches();
    }

    private static void RemoveLoginActivity(Context context){
        Activity finishThis = (Activity) context;
        finishThis.finish();
    }

    public static void LoginNoToken(final Context context, final String email, final String password, final ProgressBar bar){
        if(!User.isEmailValid(email)){
            Toast.makeText(context,"Please enter a valid email!",Toast.LENGTH_SHORT).show();
            return;
        }else if (!User.isPassOkay(password) || !User.isPassAllowed(password)) {
            Toast.makeText(context, "Your password is incorrect!", Toast.LENGTH_SHORT).show();
            return;
        }

        String encode = User.EncodePass(password);

        SimpleREST api = new SimpleREST(context);

        HashMap<String, String> postData = new HashMap<>();

        postData.put("username",email);
        postData.put("password",encode);

        bar.setVisibility(View.VISIBLE);
        api.PostJSON(Fetcher.AuthURL(), postData, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) throws JSONException {
                JSONObject json = new JSONObject(result);

                //Log.d("POST DATA",json.toString());

                if (User.isDataKindaOkay(json)) {
                    context.startActivity(new Intent(context, MenuActivity.class));
                    User.SaveLogin(context,email,json.getString("token"));
                    RemoveLoginActivity(context);
                    User.SetNewAuthedUser(json);
                }else{
                    if (json.has("msg")){
                        Toast.makeText(context,json.getString("msg"),Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(context,"An error has occurred",Toast.LENGTH_LONG).show();
                    }
                }
                bar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(String result) {
                Toast.makeText(context,result,Toast.LENGTH_LONG).show();
                bar.setVisibility(View.INVISIBLE);
            }
        });

    }

    public static String GetLastEmail(Context context){
        SharedPreferences data = context.getSharedPreferences("flashdata",Context.MODE_PRIVATE);
        return data.getString("lastlogin","-1");
    }

    public static String LoginToken(final Context context){
        SharedPreferences data = context.getSharedPreferences("flashdata",Context.MODE_PRIVATE);
        String lastLogin = data.getString("lastlogin","-1");

        if (lastLogin.equals("-1")){
            return "-1";
        }else {
            return data.getString(lastLogin, "-1");
        }
    }

    public static void CreateNew(final Context context, final String email, final String password, final String displayName, final ProgressBar bar) {
        if (!isEmailValid(email)){
            Toast.makeText(context,"Please enter a valid email!",Toast.LENGTH_SHORT).show();
            return;
        }else if(!isPassOkay(password)){
            Toast.makeText(context,"Your password needs to be at least 4 characters!",Toast.LENGTH_SHORT).show();
            return;
        }else if(!isPassAllowed(password)){
            Toast.makeText(context,"Passwords can only contain letters, numbers and !@#_",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!User.HasConnection(context)){ // Since this isn't a real app it will just stay like this. Offline vs Online as of right now are 2 different entities
            Toast.makeText(context,"Logging in locally",Toast.LENGTH_LONG).show();
            SaveLoginNoConnection(context,email);
            context.startActivity(new Intent(context, MenuActivity.class));
            RemoveLoginActivity(context);
            LoggedIn = new AuthedUser();
            return;
        }

        //String encode = User.EncodePass(password);

        SimpleREST api = new SimpleREST(context);

        HashMap<String, String> postData = new HashMap<>();

        postData.put("username",email);
        postData.put("password",password);
        postData.put("displayname",displayName);
        postData.put("newuser","true");

        //Log.d("user", email);
        //Log.d("password", password);
        //Log.d("displayname", displayName);

        bar.setVisibility(View.VISIBLE);
        api.PostJSON(Fetcher.AuthURL(), postData, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) throws JSONException {
                JSONObject json = new JSONObject(result);

                //Log.d("POST DATA",json.toString());

                if (User.isDataKindaOkay(json)) {
                    User.SetNewAuthedUser(json);
                    UserDBHelper userDB = new UserDBHelper(context);
                    userDB.InsertUser(email,displayName,json.getString("token"));
                    context.startActivity(new Intent(context, MenuActivity.class));
                    User.SaveLogin(context,email,json.getString("token"));
                    RemoveLoginActivity(context);
                }else{
                    if (json.has("msg")){
                        Toast.makeText(context,json.getString("msg"),Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(context,"An error has occurred",Toast.LENGTH_LONG).show();
                    }
                }
                bar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(String result) {
                Toast.makeText(context,result,Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void ValidateToken(final Context context, String email, String token) throws IOException {
        SimpleREST api = new SimpleREST(context);

        HashMap<String,String> arr = new HashMap<>();

        arr.put("username",email);
        arr.put("token",token);

        api.PostJSON(Fetcher.AuthURL(), arr, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) throws JSONException {

                if (!User.HasConnection(context)){
                    User.LoggedIn = new AuthedUser();
                    context.startActivity(new Intent(context, MenuActivity.class));
                    RemoveLoginActivity(context);
                    return;
                }

                JSONObject userData = new JSONObject(result);

                if (User.isDataKindaOkay(userData)) {
                    User.SetNewAuthedUser(userData);
                    context.startActivity(new Intent(context, MenuActivity.class));
                    RemoveLoginActivity(context);
                }
            }

            @Override
            public void onFailure(String result) {
                Toast.makeText(context,"Your token has expired",Toast.LENGTH_LONG);
            }
        });
    }

    public static void SaveLoginNoConnection(Context context, String email){
        SharedPreferences flashData = context.getSharedPreferences("flashdata",Context.MODE_PRIVATE);
        SharedPreferences.Editor editData = flashData.edit();
        editData.putString("lastlogin",email);
        editData.apply();
    }

    private static void SetNewAuthedUser(JSONObject obj) throws JSONException {
        String email = obj.getString("email");
        String displayName = obj.getString("displayname");
        String token = obj.getString("token");
        int serverid = obj.getInt("serverid");

        //Log.d("Email",email);
        //Log.d("Displayname",displayName);
        //Log.d("Token",token);
        //Log.d("Serverid",String.valueOf(serverid));

        LoggedIn = new AuthedUser(email,displayName,token,-1,serverid);
    }

    public static boolean isDataKindaOkay(JSONObject obj){
        return obj.has("displayname") && obj.has("serverid") && obj.has("token") && obj.has("email");
    }

    public static boolean HasConnection(Context context){
        ConnectivityManager connection = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo connectionInfo;

        try{
            connectionInfo = connection.getActiveNetworkInfo();
        }catch(NullPointerException e){
            return false;
        }

        return connectionInfo != null && connectionInfo.isConnected();
    }

    //TODO:: Mover over to SQL as storing like this seems very limited and robust
    public static void SaveLogin(Context context,String email, String token){
        SharedPreferences flashData = context.getSharedPreferences("flashdata",Context.MODE_PRIVATE);
        SharedPreferences.Editor editData = flashData.edit();
        editData.putString(email,token);
        editData.putString("lastlogin",email);
        editData.apply();
    }

    public static void Logout(Context context,String email){
        SharedPreferences flashData = context.getSharedPreferences("flashdata",Context.MODE_PRIVATE);
        SharedPreferences.Editor editData = flashData.edit();
        editData.putString(email,"");
        editData.putString("lastlogin","");
        editData.apply();
        User.LoggedIn = new AuthedUser();
        Intent goToLogin = new Intent();
        goToLogin.setClass(context,LoginActivity.class);
        Toast.makeText(context,"You have logged out",Toast.LENGTH_LONG).show();
        context.startActivity(goToLogin);
    }

}
