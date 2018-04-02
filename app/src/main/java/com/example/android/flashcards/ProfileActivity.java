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

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView profilePicIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePicIV = findViewById(R.id.profile_picture_image_view);
        loadProfilePicture();

        FCApplication.LoadDecks(this);

        TextView userNameTV, gamesPlayedTV, gamesWonTV, numDecksTV, numCardsTV;

        userNameTV = findViewById(R.id.username_text_view);
        gamesPlayedTV = findViewById(R.id.games_played_text_view);
        gamesWonTV = findViewById(R.id.games_won_text_view);
        numDecksTV = findViewById(R.id.num_decks_text_view);
        numCardsTV = findViewById(R.id.num_cards_text_view);

        userNameTV.setText(User.LoggedIn.GetName());
        gamesPlayedTV.setText("Games Played - " + FCApplication.getGamesPlayed());

        gamesWonTV.setText("Games Won - " + FCApplication.getGamesWon());

        numDecksTV.setText("Decks - " + FCApplication.getNumDecks());
        numCardsTV.setText("Cards - " + FCApplication.getNumCards());
    }

    public void onMenuClicked(View v){
        Intent menu = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(menu);
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
        String username = "Offline User";
        username = User.LoggedIn.GetName();
        deleteFile(username + "Icon.png");

        try {
            // out = new FileOutputStream("filename");
            out = openFileOutput(username + "Icon.png", Context.MODE_PRIVATE);
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
        String username = "Offlin User";
        username = User.LoggedIn.GetName();
        String filename = getFilesDir().toString() + "/" + username + "Icon.png";

        Bitmap bmp = BitmapFactory.decodeFile(filename);
        if(bmp != null) {
            profilePicIV.setImageBitmap(bmp);
        }
    }
}
