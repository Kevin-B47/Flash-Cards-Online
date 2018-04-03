package com.example.android.flashcards.classes;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by SPeggs on 3/22/2018.
 * This class extends applicaton to allow getting and setting of global variables
 */

public class FCApplication {
    private static ArrayList<Deck> decks = new ArrayList<>();
    private static int gamesPlayed = 0;
    private static int gamesWon = 0;

    public static void LoadDecks(Context c){
        decks = Deck.LoadAllDecks(c);
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
}
