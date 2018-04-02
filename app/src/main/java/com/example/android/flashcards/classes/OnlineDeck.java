package com.example.android.flashcards.classes;

/**
 * Created by Kevin on 3/11/2018.
 */

public class OnlineDeck {
    private int deckAmount = 0;
    private int collectionid = -1;
    private int userid = -1;
    private String deckName = "";
    private String author = "";
    private String desc = "";

    public OnlineDeck(int collectionid, int userid, int deckAmount, String deckName, String desc, String author){
        this.deckAmount = deckAmount;
        this.deckName = deckName;
        this.author = author;
        this.collectionid = collectionid;
        this.userid = userid;
        this.desc = desc;
    }

    public int GetCollectionID(){
        return this.collectionid;
    }
    public String GetDeckName(){
        return this.deckName;
    }
    public int GetDeckAmount(){
        return this.deckAmount;
    }
    public String GetDeckAuthor(){
        return this.author;
    }
    public String GetDeckDescription(){ return this.desc; }
    public int GetUserID(){
        return this.userid;
    }
}
