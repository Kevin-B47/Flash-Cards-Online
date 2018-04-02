package com.example.android.flashcards.classes;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by SPeggs on 3/22/2018.
 * This class extends applicaton to allow getting and setting of global variables
 */

public class FCApplication extends Application {
    private static ArrayList<Deck> decks = new ArrayList<>();
    private static int focused = -1;
    private static int gamesPlayed = 0;
    private static int gamesWon = 0;

    /*
     *  Goes into the application's private directory, scans for and loads .deck files
     */
    public static int getDeckCount(Context context){
        //TODO move save directory to 'My Documents/Flashcards'i
        int hits = 0;
        String[] allFiles = context.fileList();

        for(int i = 0; i < allFiles.length; i++){
            if(allFiles[i].endsWith(".deck")){
               hits++;
            }
        }
        return hits;
    }

    public static void LoadDecks(Context c){
        decks = Deck.LoadAllDecks(c);
    }
    /*
     *   Return deck at index i
     */
    public Deck getDeck(int i){
        if(i <= 0 && i < decks.size()){
            return decks.get(i);
        }else{
            Toast.makeText(this, "Select Valid Deck", Toast.LENGTH_LONG).show();
            return null;
        }
    }


    public static int getNumDecks(){
        return decks.size();
    }

    public static int getNumCards(){
        int sum = 0;
        for(Deck d: decks){
            sum = sum + d.getSize();
        }
        return sum;
    }

    public static void incrementGamesWon(){
        gamesWon++;
    }

    public static int getGamesWon(){
        return gamesWon;
    }

    public static void incrementGamesPlayed(){
        gamesPlayed++;
    }

    public static int getGamesPlayed(){
        return gamesPlayed;
    }

    public static int getFocused(){
        return focused;
    }

    public static void setFocused(int i){
        focused = i;
    }
}
