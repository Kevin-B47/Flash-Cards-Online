package com.example.android.flashcards;
/*This activity is for viewing and manipulating a list of Decks, the "view" deck button
* will allow to select, add, delete, view decks
* -SPeggs
* */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.flashcards.classes.Card;
import com.example.android.flashcards.classes.Deck;
import com.example.android.flashcards.classes.DeckAdapter;
import com.example.android.flashcards.classes.DeckSwiper;
import com.example.android.flashcards.classes.Fetcher;
import com.example.android.flashcards.classes.SimpleREST;
import com.example.android.flashcards.classes.User;
import com.example.android.flashcards.interfaces.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DecksActivity extends AppCompatActivity {
    //index of the currently highlighted list item
    private RecyclerView.Adapter recycleAdapter;
    private int focused = 0;
    private ArrayList<Deck> decks;
    private boolean isSelecting = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decks);

        if (getIntent().hasExtra("selecting")){
            isSelecting = true;
        }else{
            try{
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }catch(NullPointerException e){
                Toast.makeText(this,"UH OH",Toast.LENGTH_SHORT).show();
            }
        }

        decks = new ArrayList<>();
        final RecyclerView recycleView = findViewById(R.id.decksListView);
        recycleView.setLayoutManager(new LinearLayoutManager(this));

        final Context c = this;

        int allSwipe = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

        if (User.LoggedIn.GetServerID() == -1){
            allSwipe = ItemTouchHelper.LEFT; // no connection, only delete
        }

        if (!isSelecting){
            DeckSwiper itemTouch = new DeckSwiper(0,allSwipe,c) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    this.onSwipeCallback(c,decks,recycleView,viewHolder,direction);
                }
            };

            ItemTouchHelper touchObj = new ItemTouchHelper(itemTouch);
            touchObj.attachToRecyclerView(recycleView);
            onDeckView();
        }else{
            onSoloPlayView();
        }


        this.loadDecks();
    }

    private void loadDecks(){
        decks = Deck.LoadAllDecks(this);
        this.RefreshAdapter();
    }

    /*Set up list view*/
    private void RefreshAdapter(){
        if (recycleAdapter != null) {
            ((DeckAdapter) recycleAdapter).DeleteData();
        }
        recycleAdapter = new DeckAdapter(decks);
        RecyclerView recycleView = findViewById(R.id.decksListView);
        recycleView.setAdapter(recycleAdapter);
    }

    private void onSoloPlayView(){
        Button plusbutt = findViewById(R.id.plusbutt);
        RelativeLayout create = findViewById(R.id.create_deck_layout);
        plusbutt.setVisibility(View.INVISIBLE);
        create.setVisibility(View.INVISIBLE);

        TextView soloTxt = findViewById(R.id.selectDeckTxt);
        soloTxt.setVisibility(View.VISIBLE);
    }

    private void onDeckView(){
        Button plusbutt = findViewById(R.id.plusbutt);
        RelativeLayout create = findViewById(R.id.create_deck_layout);
        plusbutt.setVisibility(View.VISIBLE);
        create.setVisibility(View.VISIBLE);

        TextView soloTxt = findViewById(R.id.selectDeckTxt);
        soloTxt.setVisibility(View.INVISIBLE);
    }

    public void onNewDeck(final View v){
        final Context c = this;
        final LayoutInflater inflater = LayoutInflater.from(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ViewGroup view = findViewById(R.id.deck_create_layout);

        final View inflate = inflater.inflate(R.layout.createdeck,view,false);
        builder.setView(inflate);
        builder.setTitle("");

        builder.setPositiveButton("Create Deck", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                EditText deckNameTxt = inflate.findViewById(R.id.edit_deck_name);
                EditText deckDescTxt = inflate.findViewById(R.id.edit_deck_desc);
                String deckName = deckNameTxt.getText().toString();
                String deckDesc = deckDescTxt.getText().toString();
                if(Deck.DoesDeckExist(c,deckName)){
                    String uniqueFileName = Deck.FindNewDeckName(c,deckName);
                    Deck newDeck = new Deck();

                    if (deckDesc.equalsIgnoreCase("")){
                        deckDesc = newDeck.GetDeckDesc();
                    }

                    boolean didSave = newDeck.SetOverrideData(deckName,deckDesc,uniqueFileName,c);
                    if (didSave){
                        if (recycleAdapter != null) {
                            ((DeckAdapter) recycleAdapter).addDeck(newDeck);
                        }
                        Toast.makeText(c,"Deck "+deckName+" created",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Deck newDeck = new Deck();

                    if (deckDesc.equalsIgnoreCase("")){
                        deckDesc = newDeck.GetDeckDesc();
                    }

                    boolean didSave = newDeck.SetNewData(deckName,deckDesc,c);

                    if (didSave){
                        if (recycleAdapter != null) {
                            ((DeckAdapter) recycleAdapter).addDeck(newDeck);
                        }
                        Toast.makeText(c,"Deck "+deckName+" created",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        AlertDialog dialog = builder.show();

        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) positive.getLayoutParams();
        params.gravity = Gravity.CENTER;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    /*"Serialize" selected deck into a string and pass it as an extra to the deck viewer activity
    * TODO This code is unsafe, all sorts of problems like file name conflicts
    * there must be a better way to pass objects between activities
    */
    public void onEditDeck(String realfilename, int pos){

        boolean doesExist = Deck.DoesDeckExistByFileName(this,realfilename);

        Deck d;

        try{
          d = decks.get(pos);
        }catch(IndexOutOfBoundsException e){
            d = new Deck();
        }

        if (isSelecting && d.getSize() > 0){
            Intent playSolo = new Intent(this, SoloPlayActivity.class);
            playSolo.putExtra("filename", realfilename);
            startActivity(playSolo);
            finish();
            return;
        }else if(isSelecting && d.getSize() <= 0){
            Toast.makeText(this,"You cannot play a solo game with no cards!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!doesExist){
            Toast.makeText(this,"File "+realfilename+" doesn't exist",Toast.LENGTH_LONG).show();
        }else{
            Intent editDeck = new Intent(this, EditDeckActivity.class);
            editDeck.putExtra("filename", realfilename);
            startActivity(editDeck);
            finish();
        }
    }

    public void onMenuClicked(View v){
        Intent menu = new Intent(this, MenuActivity.class);
        startActivity(menu);
    }

    public void onUploadClicked(final int pos){
        HashMap<String,Object> userData = User.LoggedIn.GetInfo();

        if ((int)userData.get("serverid") == -1 || !User.HasConnection(this)){
            Toast.makeText(this,"You cannot upload a deck while offline!",Toast.LENGTH_LONG).show();
            return;
        }

        final Deck d;

        try{
            d = decks.get(pos);
        }catch(IndexOutOfBoundsException e){
            Toast.makeText(this,"Error when uploading deck", Toast.LENGTH_SHORT).show();
            return;
        }catch(NullPointerException e){
            Toast.makeText(this,"Error when uploading deck", Toast.LENGTH_SHORT).show();
            return;
        }

        if (d.getSize() <= 0){
            Toast.makeText(this,"You cannot upload a deck with 0 cards",Toast.LENGTH_SHORT).show();
            ((DeckAdapter) recycleAdapter).DeckUpdated(pos);
            return;
        }

        SimpleREST api = new SimpleREST(this);

        HashMap<String, String> postData = new HashMap<>();

        postData.put("token",(String)userData.get("token"));
        postData.put("userid",String.valueOf(userData.get("serverid")));
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

        final Context c = this;

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

                    if (recycleAdapter != null) {
                        ((DeckAdapter) recycleAdapter).DeckUpdated(pos);
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

    public void OverrideCallback(String deckname, String deckdesc) {
        Deck d = new Deck();
        boolean didSave = d.SetNewData(deckname,deckdesc,this);

        if (didSave){
            if (recycleAdapter != null) {
                ((DeckAdapter) recycleAdapter).addDeck(d);
            }
            Toast.makeText(this,"Deck "+d.getName()+" created",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this,MenuActivity.class);
        startActivity(intent);
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if (isSelecting){
            Intent intent = new Intent(this,MenuActivity.class);
            startActivity(intent);
            finish();
        }
        super.onBackPressed();
    }
}
