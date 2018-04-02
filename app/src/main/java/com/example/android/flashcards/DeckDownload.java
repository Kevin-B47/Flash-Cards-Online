package com.example.android.flashcards;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.flashcards.classes.AuthedUser;
import com.example.android.flashcards.classes.Card;
import com.example.android.flashcards.classes.Deck;
import com.example.android.flashcards.classes.Fetcher;
import com.example.android.flashcards.classes.OnlineDeck;
import com.example.android.flashcards.classes.OnlineDeckAdapter;
import com.example.android.flashcards.classes.OnlineDeckSwiper;
import com.example.android.flashcards.classes.SimpleREST;
import com.example.android.flashcards.classes.User;
import com.example.android.flashcards.interfaces.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeckDownload extends AppCompatActivity {
    private RecyclerView.Adapter recycleAdapter;

    List<OnlineDeck> decksToShow = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_download);

        AuthedUser loggedIn = User.LoggedIn;

        if (!User.HasConnection(this) || loggedIn == null || loggedIn.GetServerID() < 0){
            Toast.makeText(this,"You need to be online for this!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(NullPointerException e){
            Toast.makeText(this,"UH OH",Toast.LENGTH_SHORT).show();
        }

        final Context cContext = this;

        RecyclerView recycleView = findViewById(R.id.onlineDeckView);
        recycleView.setLayoutManager(new LinearLayoutManager(this));

        recycleAdapter = new OnlineDeckAdapter(decksToShow);
        recycleView.setAdapter(recycleAdapter);

        SimpleREST fetchCollections = new SimpleREST(this);
        fetchCollections.GetJSON(Fetcher.FetchCollections(), new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) throws JSONException {
                JSONArray data = new JSONArray(result);

                for(int k = 0;k < data.length(); k++){
                    JSONObject deckInfo = (JSONObject) data.get(k);

                    int collectionid = (int) deckInfo.get("collectionid");
                    int deckAmount = (int) deckInfo.get("amountofcards");
                    int userid = (int) deckInfo.get("userid");

                    if (deckAmount <= 0){
                        continue;
                    }

                    String deckName = (String) deckInfo.get("collectionname");
                    String deckDesc = (String) deckInfo.get("collectiondesc");
                    String deckAuthor = (String) deckInfo.get("displayname");

                    OnlineDeck deckData = new OnlineDeck(collectionid,userid,deckAmount,deckName,deckDesc,deckAuthor);
                    decksToShow.add(deckData);
                    ((OnlineDeckAdapter)recycleAdapter).onDeckAdded();
                }
            }

            @Override
            public void onFailure(String result) {
                Toast.makeText(cContext,"Error when fetching collections",Toast.LENGTH_SHORT).show();
            }
        });


        OnlineDeckSwiper itemTouch = new OnlineDeckSwiper(0,ItemTouchHelper.LEFT,this) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                try {
                    this.onSwipeCallback(cContext,viewHolder,direction);
                } catch (IndexOutOfBoundsException e) {
                    Toast.makeText(cContext,"Error modifying deck",Toast.LENGTH_LONG).show();
                } catch (NullPointerException e){
                    Toast.makeText(cContext,"Error modifying deck",Toast.LENGTH_LONG).show();
                }
            }
        };

        ItemTouchHelper touchObj = new ItemTouchHelper(itemTouch);
        touchObj.attachToRecyclerView(recycleView);

    }

    public void DownloadDeck(final OnlineDeck deckinfo){
        final int collectionid = deckinfo.GetCollectionID();

        SimpleREST fetch = new SimpleREST(this);
        final Context cContext = this;
        fetch.GetJSON(Fetcher.DownloadURL(collectionid), new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) throws JSONException {
                JSONArray downloadedDeck = new JSONArray(result);
                ArrayList<Card> cards = new ArrayList<>();

                for(int k = 0; k < downloadedDeck.length(); k++){
                    JSONObject cardData = (JSONObject) downloadedDeck.get(k);

                    Card coolCard = new Card(cardData.getString("fronttext"),cardData.getString("backtext"));
                    cards.add(coolCard);
                }

                String deckName = deckinfo.GetDeckName();
                String deckDesc = deckinfo.GetDeckDescription();
                int deckCollectionID = deckinfo.GetCollectionID();

                Deck newDeck = new Deck(deckName,deckDesc,deckCollectionID,cards);
                newDeck.OverrideFileName(Deck.FindNewDeckName(cContext,deckName));
                newDeck.save(cContext);
                Toast.makeText(cContext,"Deck "+deckName+" saved!",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String result) {
                Toast.makeText(cContext,"Error when downloading deck",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void DeleteFromServer(final int pos){

        SimpleREST api = new SimpleREST(this);
        final OnlineDeck d;
        try{
             d = decksToShow.get(pos);
        }catch(IndexOutOfBoundsException e){
            Toast.makeText(this,"Error while deleting deck",Toast.LENGTH_LONG).show();
            ((OnlineDeckAdapter)recycleAdapter).onFailedDelete(pos);
            return;
        }catch(NullPointerException e) {
            Toast.makeText(this,"Error while deleting deck",Toast.LENGTH_LONG).show();
            ((OnlineDeckAdapter)recycleAdapter).onFailedDelete(pos);
            return;
        }

        if (d.GetUserID() != User.LoggedIn.GetServerID()){
            Toast.makeText(this,"You do not own this deck!", Toast.LENGTH_SHORT).show();
            ((OnlineDeckAdapter)recycleAdapter).onFailedDelete(pos);
            return;
        }

        HashMap<String, String> postData = new HashMap<>();

        postData.put("token", User.LoggedIn.GetToken());
        postData.put("userid",String.valueOf(User.LoggedIn.GetServerID()));
        postData.put("collectionid",String.valueOf(d.GetCollectionID()));
        postData.put("delete","true");

        final Context c = this;

        api.PostJSON(Fetcher.CollectionURL(), postData, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) throws JSONException {
                JSONObject json = new JSONObject(result);

                if (json.has("msg")){
                    Toast.makeText(c,json.getString("msg"),Toast.LENGTH_LONG).show();
                    decksToShow.remove(pos);
                    ((OnlineDeckAdapter)recycleAdapter).onDeckDeleted(pos);

                    Deck deck = Deck.DoesDeckExistByServerID(c,d.GetCollectionID());

                    if (deck.GetServerID() > 0){ // No longer is uploaded
                        deck.SetServerID(-1);
                        deck.SetUserID(-1);
                        deck.save(c);
                    }

                }else{
                    Toast.makeText(c,"Error while deleting deck",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(String result) {
                Toast.makeText(c,result,Toast.LENGTH_LONG).show();
            }
        });
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
