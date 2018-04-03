package com.example.android.flashcards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.flashcards.classes.FCApplication;
import com.example.android.flashcards.classes.User;

import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView profilePicIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePicIV = findViewById(R.id.profile_picture_image_view);
        loadProfilePicture();

        FCApplication.LoadDecks(this);

        TextView userNameTV, gamesPlayedTV, gamesWonTV, numDecksTV, numCardsTV;

        TextView gamesplayedView, gamesWonView, numCards,numDecks;

        userNameTV = findViewById(R.id.username_text_view);
        gamesPlayedTV = findViewById(R.id.games_played_text_view);
        gamesWonTV = findViewById(R.id.games_won_text_view);
        numDecksTV = findViewById(R.id.num_decks_text_view);
        numCardsTV = findViewById(R.id.num_cards_text_view);

        gamesplayedView = findViewById(R.id.games_played);
        gamesWonView = findViewById(R.id.games_won);
        numCards = findViewById(R.id.card_amount);
        numDecks = findViewById(R.id.deck_amount);

        userNameTV.setText(User.LoggedIn.GetName());
        gamesPlayedTV.setText(this.getString(R.string.profile_games_played));
        gamesplayedView.setText(String.valueOf(FCApplication.getGamesPlayed()));

        gamesWonTV.setText(this.getString(R.string.profile_games_won));
        gamesWonView.setText(String.valueOf(FCApplication.getGamesWon()));

        numDecksTV.setText(this.getString(R.string.decks));
        numDecks.setText(String.valueOf(FCApplication.getNumDecks()));

        numCardsTV.setText(this.getString(R.string.cards));
        numCards.setText(String.valueOf(FCApplication.getNumCards()));
    }

    public void onMenuClicked(View v){
        Intent menu = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(menu);
        finish();
    }

    //Takes a photo with an outside app, should check if user has camera and give option to load
    //saved file
    public void onProfilePictureClicked(View v){
        dispatchTakePictureIntent();
    }

    //Starts a camera application, results returned through onActivityResult
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //Handle bitmap small scale thumbnail returned by the camera app, put it as the icon and save it
    //to an application private file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profilePicIV.setImageBitmap(imageBitmap);
            saveProfilePicture(imageBitmap);
        }
    }

    //Saves the given Bitmap as <username>Icon.png in the application private folder
    private void saveProfilePicture(Bitmap bmp){
        FileOutputStream out = null;
        deleteFile(User.LoggedIn.GetServerID() + "Icon.png");

        try {
            // out = new FileOutputStream("filename");
            out = openFileOutput(User.LoggedIn.GetServerID() + "Icon.png", Context.MODE_PRIVATE);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //loads the bitmap named <username>Icon.png into the correct image view if the file exists
    private void loadProfilePicture(){
        String filepath = User.LoggedIn.GetServerID() + "Icon.png";
        if (this.getFileStreamPath(filepath).exists()){
            Bitmap bmp = BitmapFactory.decodeFile(filepath);
            if(bmp != null) {
                profilePicIV.setImageBitmap(bmp);
            }
        }
    }
}
