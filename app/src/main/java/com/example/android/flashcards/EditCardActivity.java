package com.example.android.flashcards;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditCardActivity extends AppCompatActivity {
    String front;
    String back;
    EditText frontET;
    EditText backET;
    String deckName;
    int cardNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(NullPointerException e){
            Toast.makeText(this,"UH OH",Toast.LENGTH_SHORT).show();
        }


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                deckName= null;
            } else {
                deckName= extras.getString("deckName");
                cardNum = extras.getInt("cardnum");
            }
        } else {
            deckName= (String) savedInstanceState.getSerializable("deckName");
        }

        String sCard;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                sCard= null;
            } else {
                sCard= extras.getString("sCard");
            }
        } else {
            sCard= (String) savedInstanceState.getSerializable("sCard");
        }

        //TODO: Sanitize card editing input with a pattern to prevent people from using : as it will break the card
        int split = sCard.indexOf(':');
        front = sCard.substring(0, split);
        back = sCard.substring(split+1, sCard.length());

        frontET = findViewById(R.id.frontTextView);
        backET = findViewById(R.id.backTextView);

        if (savedInstanceState != null && savedInstanceState.getString("back") != null && savedInstanceState.getString("font") != null){
            back = savedInstanceState.getString("back");
            front = savedInstanceState.getString("font");
        }
        frontET.setText(front);
        backET.setText(back);
    }

    public void onMenuClicked(View v){
        Intent menu = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(menu);
    }

    public void onReturnClicked(View v){
        Intent editDeck = new Intent(getApplicationContext(), EditDeckActivity.class);
        editDeck.putExtra("deckName", deckName);
        editDeck.putExtra("sCard", frontET.getText().toString() + ':' + backET.getText().toString());
        editDeck.putExtra("cardnum",cardNum);
        startActivity(editDeck);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
