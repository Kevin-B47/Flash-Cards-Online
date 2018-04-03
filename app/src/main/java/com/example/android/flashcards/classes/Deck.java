package com.example.android.flashcards.classes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.flashcards.R;
import com.example.android.flashcards.interfaces.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by SPeggs on 2/17/2018.
 * Class for a deck of Cards
 */

public class Deck {
    private String deckname = "Default";
    private String deckdesc = "No Deck Description";
    private ArrayList<Card> cards = new ArrayList<>();
    private int serverid = -1;
    private int userid = -1;
    private int clientid = -1;
    private String realfileName = "";

    private static final String Extension = ".deck";

    public Deck(){
        deckname = "";
        cards = new ArrayList<>();
    }

    public Deck(String name, ArrayList<Card> cards){
        this.deckname = name;
        this.cards = cards;
    }

    public Deck(String name, String desc, int serverid, ArrayList<Card> cards){
        this.deckname = name;
        this.cards = cards;
        this.deckdesc = desc;
        this.serverid = serverid;
    }

    private static String GetGoodDeckName(String name){
        return name.replaceAll("[\\s;\\/:*?\"<>|&']","_");
    }

    private static boolean IsValidJSON(String json){
        try {
            JSONObject obj = new JSONObject(json);
            return true;
        } catch (JSONException e) {
            try{
                new JSONArray(json);
                return true;
            }catch(JSONException e2){
                return false;
            }
        }
    }

    public static Deck LoadDeckByName(Context c, String deckName) {
        String json = Deck.GetFileData(c,Deck.GetGoodDeckName(deckName+Deck.Extension));

        if (json.length() < 2){
            return new Deck();
        }

        return Deck.JSONToDeck(c,json);
    }

    public static Deck LoadDeckByFileName(Context c, String deckName) {
        String json = Deck.GetFileData(c,deckName);

        if (json.length() < 2){
            return new Deck();
        }

        return Deck.JSONToDeck(c,json);
    }

    public static boolean DoesDeckExist(Context c,String name){
        String finalfilename = Deck.GetGoodDeckName(name);
        finalfilename = finalfilename+Deck.Extension;
        String[] allFiles = c.fileList();

        for(String file : allFiles){
            if (file.equalsIgnoreCase(finalfilename)){
                return true;
            }
        }
        return false;
    }

    public static boolean DoesDeckExistByFileName(Context c,String name){
        String[] allFiles = c.fileList();

        for(String file : allFiles){
            if (file.equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    public static Deck DoesDeckExistByServerID(Context c, int id){

        ArrayList<Deck> list = Deck.LoadAllDecks(c);

        for (Deck d: list){
            if (d.GetServerID() == id){
                return d;
            }
        }
        return new Deck();
    }

    public static int GetValidDeckCount(Context c){
        int hits = 0;
        String[] allFiles = c.fileList();

        for(String file : allFiles){
           String data = Deck.GetFileData(c,file);
           if (data.length() > 8){
               hits++;
           }
        }
        return hits;
    }

    public static String FindNewDeckName(Context c, String name){
        int inc = 1;
        int hits = 0;
        String[] allFiles = c.fileList();

        while(true){
            String finalfilename = Deck.GetGoodDeckName(name+"("+String.valueOf(inc)+")");
            finalfilename = finalfilename+Deck.Extension;
            for(String file : allFiles){
                if (file.equalsIgnoreCase(finalfilename)){
                    hits++;
                }
            }
            if (hits == 0){
                return finalfilename;
            }
            inc++;
            hits = 0;
        }
    }

    private void addCard(Card c){
        cards.add(c);
    }

    public void removeCard(int i){
        cards.remove(i);
    }

    public Card getCard(int i){
        try{
            return cards.get(i);
        }catch(IndexOutOfBoundsException e){
            return new Card();
        }
    }

    public int getSize(){
        return cards.size();
    }

    public boolean SetNewData(String name, String desc, Context c){
        this.deckname = name;
        this.deckdesc = desc;
        return this.save(c);
    }

    public void setName(String s) {
        this.deckname = s;
    }

    public String getName(){
        return deckname;
    }

    //Permanent shuffle, will effect save state etc
    public void shuffle(){
        Collections.shuffle(cards);
    }


    public boolean SetOverrideData(String name, String desc, String filename, Context c){
        this.deckname = name;
        this.deckdesc = desc;

        if (this.deckname.length() < 1) {
            Toast.makeText(c, "You cannot save a deck with no name!", Toast.LENGTH_LONG).show();
            return false;
        }

        this.SetFileName(filename);

        FileOutputStream stream;

        String jsonData = Deck.DeckToJSON(this);

        try {
            stream = c.openFileOutput(filename, Context.MODE_PRIVATE);
            stream.write(jsonData.getBytes());
            stream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    public boolean save(Context context){
        if (this.deckname.length() < 1) {
            Toast.makeText(context, "You cannot save a deck with no name!", Toast.LENGTH_LONG).show();
            return false;
        }

        String finalfilename;

        if (this.GetRealFileName().length() < 2){ // Has no file name
            finalfilename = Deck.GetGoodDeckName(this.deckname)+Deck.Extension;
            this.SetFileName(finalfilename);
        }else{ // Has a file name, just use that
            finalfilename = this.GetRealFileName();
        }

        FileOutputStream stream;

        String jsonData = Deck.DeckToJSON(this);

        try {
            stream = context.openFileOutput(finalfilename, Context.MODE_PRIVATE);
            stream.write(jsonData.getBytes());
            stream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void delete(Context context){
        context.deleteFile(this.GetRealFileName());
    }

    public static ArrayList<Deck> LoadAllDecks(Context c){
        String[] allFiles = c.fileList();
        ArrayList<Deck> deckList = new ArrayList<>();
        int hits = 0;
        for(String file : allFiles){
           String json = Deck.GetFileData(c,file);
           //Log.d("JSON FOR "+file,json);
           if (json.length() < 2){ continue; }

            Deck deck = Deck.JSONToDeck(c,json);
            deck.SetClientID(hits);
            deckList.add(deck);
            hits++;
        }

        return deckList;
    }

    private static String GetFileData(Context c,String deckname){
        try {
            FileInputStream fileInputStream = c.openFileInput(deckname);
            InputStreamReader read = new InputStreamReader(fileInputStream,"UTF-8");
            BufferedReader buff = new BufferedReader(read);
            StringBuilder builder = new StringBuilder();

            String line = buff.readLine();

            while(line != null){
                builder.append(line);
                line = buff.readLine();
            }

           // Log.d("BUILD",builder.toString());

            String json = builder.toString();

            if (Deck.IsValidJSON(json)){
                return json;
            }else{
                return "";
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void OverrideFileName(String n){
        this.realfileName = n;
    }

    private static String DeckToJSON(Deck deck){
       HashMap<String,String> deckmap = new HashMap<>();
       HashMap<String,String> fullCardMap = new HashMap<>();

       int hits = 0;

       for(Card card: deck.cards){
            HashMap<String,String> singleCard = new HashMap<>();
            singleCard.put("fronttext",card.getFront());
            singleCard.put("backtext", card.getBack());

            fullCardMap.put(String.valueOf(hits),new JSONObject(singleCard).toString());
            hits++;
       }

       String cardJSON = new JSONObject(fullCardMap).toString();

        deckmap.put("deckname",deck.getName());
        deckmap.put("cards",cardJSON);
        deckmap.put("serverid",String.valueOf(deck.GetServerID()));
        deckmap.put("realfilename",deck.GetRealFileName());
        deckmap.put("deckdesc",deck.GetDeckDesc());
        if (User.LoggedIn.GetServerID() > 0){
            deckmap.put("userid",String.valueOf(deck.GetUserID()));
        }

        return new JSONObject(deckmap).toString();
    }

    private static Deck JSONToDeck(Context c,String json){
        Deck deck = new Deck();
        try {
            JSONObject jsonData = new JSONObject(json);

            if (jsonData.has("deckname")){
                deck.setName(jsonData.getString("deckname"));
            }

            if (jsonData.has("deckdesc")){
                deck.SetDeckDesc(jsonData.getString("deckdesc"));
            }

            if (jsonData.has("realfilename")){
                deck.SetFileName(jsonData.getString("realfilename"));
            }

            if (jsonData.has("serverid")){
                deck.SetServerID(jsonData.getInt("serverid"));
            }

            if (jsonData.has("userid")){
               // Log.d("Loading Deck","USerid "+jsonData.getString("userid"));
                deck.SetUserID(jsonData.getInt("userid"));
            }

            if (jsonData.has("cards")){
                JSONObject cardObj = new JSONObject(jsonData.getString("cards"));
                Iterator cardItt = cardObj.keys();

                while(cardItt.hasNext()){
                    String key = (String) cardItt.next();
                    JSONObject cardData = new JSONObject(cardObj.getString(key));
                    if (cardData.has("fronttext") && cardData.has("backtext")) {
                        Card addCard = new Card(cardData.getString("fronttext"),cardData.getString("backtext"));
                        deck.addCard(addCard);
                    }
                }
            }
            return deck;
        } catch (JSONException e) {
            e.printStackTrace();
            return deck;
        }
    }

    public static void ConfirmOverwrite(final Context c,Deck oldDeck,final String deckname,final String deckdesc){ // Not used, use lazy / better way
        final LayoutInflater inflater = LayoutInflater.from(c);
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("");
        final View inflate = inflater.inflate(R.layout.answercard,null);

        builder.setView(inflate);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                //((DecksActivity)c).OverrideCallback(oldDeck,deckname,deckdesc);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i){

            }
        });

        AlertDialog dialog = builder.show();

        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) positive.getLayoutParams();
        params.gravity = Gravity.CENTER;
        params.weight = 1;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;

        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) negative.getLayoutParams();
        params2.gravity = Gravity.CENTER;
        params2.weight = 1;
        params2.width = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    public static void UploadDeck(final Context c, final Deck d) {
        if (d.getSize() <= 0){
            Toast.makeText(c,"You cannot upload a deck with 0 cards",Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleREST api = new SimpleREST(c);
        HashMap<String, String> postData = new HashMap<>();
        AuthedUser user = User.LoggedIn;

        if (!User.HasConnection(c)){
            Toast.makeText(c,"You cannot upload a deck while offline!",Toast.LENGTH_SHORT).show();
            return;
        }

        postData.put("token",user.GetToken());
        postData.put("userid",String.valueOf(user.GetServerID()));
        postData.put("collectionname",d.getName());
        postData.put("collectiondesc",d.GetDeckDesc());

        if(d.GetServerID() > 0){
            postData.put("collectionid",String.valueOf(d.GetServerID()));
        }

        ArrayList<JSONObject> jsonStuff = new ArrayList<>();

        for(int k = 0; k < d.getSize(); k++){
            Card card = d.getCard(k);
            HashMap<String,String> cardData = new HashMap<>();
            cardData.put("fronttext",card.getFront());
            cardData.put("backtext",card.getBack());

            JSONObject obj = new JSONObject(cardData);

            jsonStuff.add(obj);
        }

        JSONArray finalJSON = new JSONArray(jsonStuff);

        //Log.d("JSON",finalJSON.toString());

        postData.put("collectiondata",finalJSON.toString());
        postData.put("Content-Type","application/json");

        api.PostJSON(Fetcher.CollectionURL(), postData, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) throws JSONException {
                JSONObject json = new JSONObject(result);

                if (json.has("msg")){
                    Toast.makeText(c,json.getString("msg"),Toast.LENGTH_LONG).show();

                    if (json.has("deckid")){
                        d.SetServerID(json.getInt("deckid"));
                        if (User.LoggedIn.GetServerID() > 0){
                            d.SetUserID(User.LoggedIn.GetServerID());
                        }
                        d.save(c);
                    }
                }else{
                    Toast.makeText(c,"Error while uploading deck",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(String result) {
                Toast.makeText(c,result,Toast.LENGTH_LONG).show();
            }
        });
    }

    public void SetServerID(int serverid) {
        this.serverid = serverid;
    }

    private void SetFileName(String name){
        this.realfileName = name;
    }

    public String GetRealFileName(){
        return this.realfileName;
    }

    public int GetServerID(){
        return this.serverid;
    }

    private void SetClientID(int id){
        this.clientid = id;
    }

    public int GetClientID(){
        return this.clientid;
    }

    public void SetDeckDesc(String desc){
        this.deckdesc = desc;
    }

    public String GetDeckDesc(){
        return this.deckdesc;
    }

    public ArrayList<Card> GetCards(){
        return this.cards;
    }

    public void SetUserID(int i) {
        this.userid = i;
    }

    public int GetUserID(){
        return this.userid;
    }
}
