package com.example.android.flashcards.classes;

/**
 * Created by SPeggs on 2/17/2018.
 * Class for a single flash card
 */

public class Card {

    public String front;
    public String back;

    public Card(String sFront, String sBack){
        front = sFront;
        back = sBack;
    }

    public Card(){
        front = "CARD FRONT";
        back = "CARD BACK";
    }

    public String getFront(){
        return front;
    }

    public void setFront(String s){
        front = s;
    }

    public String getBack(){
        return back;
    }

    public void setBack(String s){
        back = s;
    }

    //Return card as a string for saving
    public String saveString() {
        String saveString = front + ":" + back;
        return saveString;
    }
}
