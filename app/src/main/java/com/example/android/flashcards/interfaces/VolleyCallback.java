package com.example.android.flashcards.interfaces;

import org.json.JSONException;

/**
 * Created by Kevin on 2/14/2018.
 */

public interface VolleyCallback {
    void onSuccessResponse(String result) throws JSONException;
    void onFailure(String result);
}
