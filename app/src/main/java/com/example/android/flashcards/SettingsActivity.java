package com.example.android.flashcards;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void onMenuClicked(View v){
        Intent menu = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(menu);
    }
}
