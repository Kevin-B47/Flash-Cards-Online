package com.example.android.flashcards;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.flashcards.classes.Deck;
import com.example.android.flashcards.classes.User;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        if (getIntent().hasExtra("loggedin") && User.LoggedIn != null && User.LoggedIn.GetName() != null) {
            Toast.makeText(this,"Welcome back " + User.LoggedIn.GetName(),Toast.LENGTH_LONG).show();
        }

        LinearLayout singleplayer = findViewById(R.id.singleplayerlayout);
        LinearLayout decks = findViewById(R.id.decklayout);
        LinearLayout profile = findViewById(R.id.profilelayout);
        LinearLayout community = findViewById(R.id.communitylayout);
        LinearLayout logout = findViewById(R.id.logoutlayout);

        singleplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlaySoloClicked(view);
            }
        });

        decks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDecksClicked(view);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onProfileClicked(view);
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDownloadClicked(view);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLogout(view);
            }
        });


    }

    private void onPlaySoloClicked(View v){

        int deckCount = Deck.GetValidDeckCount(this);

        if (deckCount > 0) {
            Intent decks = new Intent(getApplicationContext(), DecksActivity.class);
            decks.putExtra("selecting", true);
            startActivity(decks);
            finish();
        }else {
            Toast.makeText(this, "You have no decks! Make one!", Toast.LENGTH_SHORT).show();
            Intent decks = new Intent(getApplicationContext(), DecksActivity.class);
            startActivity(decks);
        }
    }

    private void onProfileClicked(View v){
        Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(profile);
    }

    private void onDecksClicked(View v){
        Intent decks = new Intent(getApplicationContext(), DecksActivity.class);
        startActivity(decks);
    }

    private void onDownloadClicked(View v){
        Intent decks = new Intent(getApplicationContext(), DeckDownload.class);
        startActivity(decks);
    }

    private void onLogout(View v){

        String loggedInEmail = User.LoggedIn.GetEmail();

        User.Logout(this,loggedInEmail);
    }
}
