package com.example.android.flashcards.classes;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.flashcards.interfaces.VolleyCallback;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kevin on 2/13/2018.
 *
 * SimpleREST implements Android's Volley api which is handles http calls very fast
 * It does all of its work on another thread so it doesn't block the UI
 * This implementation of SimpleREST fetches JSON data from a web service with either a post and or get request
 *
 * Despite the functions being called something with JSON in it, the class doesn't automatically parse the json into an object
 * I don't know why I didn't do that but it returns a string (which if in json form you can make into a json object)
 *
 */

public class SimpleREST {

    private Context context;
    private final boolean isDev = true;

    public SimpleREST(Context context){
        this.context = context;
    }

    public void GetJSON (String url, final VolleyCallback callback) {
        // StringRequest has the parameter for the REST method (get, post, delete,ect)
        // Also the url of the request and the listener of that request
        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            // The listener has 2 methods to implement which is onResponse and onErrorResponse
            // By default, when having a successful response, there isn't any way to get the repose data back
            // This is where the interface VolleyCallback comes in which has methods with different parameters which you write
            // For example if I want to fetch weather and it has a successful response, it will then call the method I implemented with the data it got
            @Override
            public void onResponse(String response) { // This is when it gets the response
                //Log.d("Get REQUEST", response);
                try {
                    callback.onSuccessResponse(response); // After we got the response, send the data back into the method we wrote(callback)
                } catch (JSONException e) {
                    callback.onFailure(response); // Else we will send it to the on failure response
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() { // This is the default error handler for the String Request which you need to implement too
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("Get error", error.getMessage());
                try {
                    callback.onSuccessResponse("-1");
                    // personal preference what to send back on an error (this will only get called if there was an error reaching the url
                    // as the onSuccessResponse will still be called if the URL is alive and gets a response
                } catch (JSONException e) {
                    callback.onFailure(e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        // Volley requires you to make a request queue which handles all requests
        // I think this works like this: If I send 500 API requests in .1 seconds it will handle all of them from the time I sent them
        // Although I am not too sure
        // This can also be a Simpleton object as you don't have to make a new requestqueue per request (as it voids the whole point of a requestQUEUE)
        if (this.context != null) {
            Volley.newRequestQueue(this.context.getApplicationContext()).add(req);
        }
    }

    public void PostJSON (String url, final HashMap<String, String> params, final VolleyCallback callback) {
        // Hash map is the parameters for the POST request
        // A post request is like a get request but instead of sending the data in the url, it sends it in the body

        // This is 'safer' for sensitive things like passwords

        // This time we have the method set to a post request with the same listener functions as above
        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("Post Data", response);
                try {
                    callback.onSuccessResponse(response);
                    // On a success, pass the data back into the callback we wrote
                } catch (JSONException e) {
                    callback.onFailure(response);
                    // Dont think this will ever happen
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("Get ERROR", error.getMessage());
                try {
                    callback.onSuccessResponse("-1");
                } catch (JSONException e) {
                    callback.onFailure(e.getMessage());
                    e.printStackTrace();
                }
            }
        }){
            @Override
            protected Map<String,String> getParams() {
                return params;
            }
        };

        if (this.context != null) {
            Volley.newRequestQueue(this.context.getApplicationContext()).add(req);
        }
    }

}
