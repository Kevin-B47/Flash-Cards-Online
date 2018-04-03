package com.example.android.flashcards;

/*
* Speggs
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.flashcards.classes.Card;
import com.example.android.flashcards.classes.CardSwiper;
import com.example.android.flashcards.classes.CollectionAdapter;
import com.example.android.flashcards.classes.Deck;

/*This activity is for viewing and manipulating a Deck as a list of cards,
 */
//TODO when editing the deck name the 'done' button should dismiss the on screen keyboard
//card numbers should probably start with 1 for our users
public class EditDeckActivity extends AppCompatActivity {

    private Deck loadedDeck;

    private RecyclerView.Adapter recycleAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deck);

        String filename = getIntent().getStringExtra("filename");

        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(NullPointerException e){
            Toast.makeText(this,"UH OH",Toast.LENGTH_SHORT).show();
        }


        if(filename.length() < 3){ return; }

        loadedDeck = Deck.LoadDeckByFileName(this,filename);

        recycleAdapter = new CollectionAdapter(loadedDeck.GetCards());
        final  RecyclerView recycleView = findViewById(R.id.editDeckListView);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleView.setAdapter(recycleAdapter);

        final Context c = this;

        final View deckEditView = findViewById(R.id.deckinfo);

        final TextView setNameTxt = findViewById(R.id.deckNameEditText);
        final TextView setDescTxt = findViewById(R.id.deckDescEditTxt);

        setNameTxt.setText("Name: "+loadedDeck.getName());
        setDescTxt.setText("Description: "+loadedDeck.GetDeckDesc());

        deckEditView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final LayoutInflater inflater = LayoutInflater.from(c);
                AlertDialog.Builder builder = new AlertDialog.Builder(c);

                final ViewGroup layout = findViewById(R.id.deck_edit_layout);
                final View inflate = inflater.inflate(R.layout.editdeck,layout,false);
                builder.setView(inflate);
                builder.setTitle("");

                final EditText decknameTxt = inflate.findViewById(R.id.edit_deck_name);
                final EditText deckdescTxt = inflate.findViewById(R.id.edit_deck_desc);

                decknameTxt.setText(loadedDeck.getName());
                deckdescTxt.setText(loadedDeck.GetDeckDesc());

                builder.setPositiveButton("Edit Deck", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){
                        String deckname = decknameTxt.getText().toString();
                        String deckdesc = deckdescTxt.getText().toString();

                        setNameTxt.setText("Name: "+deckname);
                        setDescTxt.setText("Description: "+deckdesc);

                        ((EditDeckActivity)c).onDeckInfoEdited(deckname,deckdesc);

                    }
                });

                AlertDialog dialog = builder.show();

                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) positive.getLayoutParams();
                params.gravity = Gravity.CENTER;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        });

        CardSwiper itemTouch = new CardSwiper(0,ItemTouchHelper.LEFT,c) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                this.onSwipeCallback(c,loadedDeck,recycleView,viewHolder,direction);
            }
        };

        ItemTouchHelper touchObj = new ItemTouchHelper(itemTouch);
        touchObj.attachToRecyclerView(recycleView);

    }

    private void onDeckInfoEdited(String name, String desc){
        if (loadedDeck != null){
            loadedDeck.setName(name);
            loadedDeck.SetDeckDesc(desc);
            loadedDeck.save(this);
            Toast.makeText(this,"Deck "+name+" saved!",Toast.LENGTH_SHORT).show();
        }
    }

    public void onCardEdited(int pos, String fronttext, String backtext){
        if (loadedDeck != null){
            try{
                Card c = loadedDeck.getCard(pos);
                c.setFront(fronttext);
                c.setBack(backtext);
                loadedDeck.save(this);
                ((CollectionAdapter) recycleAdapter).CardChanged(pos);
            }catch(IndexOutOfBoundsException ignored){

            }catch(NullPointerException ignored){

            }
        }
    }

    public void onNewCard(View v){
        if (loadedDeck == null){
            Toast.makeText(this,"The deck is invalid!",Toast.LENGTH_SHORT).show();
            return;
        }
        final LayoutInflater inflater = LayoutInflater.from(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final ViewGroup view = findViewById(R.id.deck_edit_layout);
        final View inflate = inflater.inflate(R.layout.createcard,view,false);
        builder.setView(inflate);
        builder.setTitle("");
        final Context c = this;

        builder.setPositiveButton("Create Card", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                EditText frontTxt = inflate.findViewById(R.id.edit_front);
                EditText backTxt = inflate.findViewById(R.id.edit_back);
                String front = frontTxt.getText().toString();
                String back = backTxt.getText().toString();

                if (recycleAdapter != null) {
                    Card card = new Card(front,back);
                    ((CollectionAdapter) recycleAdapter).AddCard(card);
                     boolean didSave = loadedDeck.save(c);
                     if (didSave){
                         Toast.makeText(c,"Card created!",Toast.LENGTH_SHORT).show();
                     }
                }
            }
        });

        AlertDialog dialog = builder.show();

        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) positive.getLayoutParams();
        params.gravity = Gravity.CENTER;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this,DecksActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.deckeditmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(this,DecksActivity.class);
            startActivity(intent);
            finish();
        }else if(item.getItemId() == R.id.solo){
            if (loadedDeck.getSize() <= 0){
                Toast.makeText(this,"You need at least one card!",Toast.LENGTH_SHORT).show();
            }else{
                Intent editDeck = new Intent(this, SoloPlayActivity.class);
                editDeck.putExtra("filename", loadedDeck.GetRealFileName());
                startActivity(editDeck);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
