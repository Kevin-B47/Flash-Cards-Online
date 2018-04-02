package com.example.android.flashcards.classes;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.flashcards.DecksActivity;
import com.example.android.flashcards.R;
import com.example.android.flashcards.interfaces.RecycleCallback;

import java.util.List;

/**
 * Created by Kevin on 2/28/2018.
 */


public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder> {

    private List<Deck> deckData;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView numOfCards;
        public TextView deckname;
        public TextView deckdesc;
        public int serverid;
        public int clientid;
        public String realfilename;
        public View layout;
        private RecycleCallback callbackListener;

        public ViewHolder(View v, RecycleCallback listener) {
            super(v);
            layout = v;
            numOfCards = v.findViewById(R.id.deckcards);
            deckname = v.findViewById(R.id.deckname);
            deckdesc = v.findViewById(R.id.deckdesc);
            v.setOnClickListener(this);
            callbackListener = listener;
        }

        @Override
        public void onClick(View view) {
            callbackListener.onClick(view,getAdapterPosition());
        }
    }

    public DeckAdapter(List<Deck> map){
        deckData = map;
    }

    @Override
    public DeckAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.decklayout,parent,false);

        RecycleCallback callback = new RecycleCallback() {
            @Override
            public void onClick(final View cardview, final int pos) {

                DecksActivity act = ((DecksActivity) parent.getContext());

                try{
                    act.onEditDeck(deckData.get(pos).GetRealFileName(),pos);
                }catch(IndexOutOfBoundsException e){
                    Toast.makeText(parent.getContext(),"Error when editing deck", Toast.LENGTH_SHORT);
                }catch(NullPointerException e){
                    Toast.makeText(parent.getContext(),"Error when editing deck", Toast.LENGTH_SHORT);
                }
            }
        };

        ViewHolder vh = new ViewHolder(v,callback);
        return vh;
    }

    public void addDeck(Deck d){
        deckData.add(d);
        notifyItemInserted(deckData.size());
    }

    public void deleteDeck(int pos){
        try{
        deckData.remove(pos);
        notifyItemRemoved(pos);
        }catch(IndexOutOfBoundsException e){
            //Log.d("OUT OF BOUDNS", e.getMessage());
        }
    }

    public void DeckUpdated(int pos) {
        notifyItemChanged(pos);
    }

    @Override
    public void onBindViewHolder(DeckAdapter.ViewHolder holder, int i) {

        Deck d = deckData.get(i);

        holder.numOfCards.setText(String.format("%d",d.getSize()));
        holder.deckname.setText(d.getName());
        holder.deckdesc.setText(d.GetDeckDesc());
        holder.clientid = d.GetClientID();
        holder.serverid = d.GetServerID();
        holder.realfilename = d.GetRealFileName();

        //Log.d("DeckID",String.valueOf(d.GetUserID()));
        if (User.LoggedIn.GetServerID() != -1 && d.GetUserID() == User.LoggedIn.GetServerID()){
            holder.numOfCards.setBackgroundResource(R.drawable.uploadeddeckborder);
        }else if(d.GetServerID() > 0 && d.GetServerID() != User.LoggedIn.GetServerID()){
            holder.numOfCards.setBackgroundResource(R.drawable.downloadeddeckborder);
        }

    }

    public void DeleteData() {
        if (!deckData.isEmpty()) {
            deckData.clear();
        }
    }

    @Override
    public int getItemCount() {
        return deckData.size();
    }
}
