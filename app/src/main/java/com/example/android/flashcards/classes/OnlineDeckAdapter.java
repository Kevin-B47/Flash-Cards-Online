package com.example.android.flashcards.classes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.flashcards.DeckDownload;
import com.example.android.flashcards.R;
import com.example.android.flashcards.interfaces.RecycleCallback;

import java.util.List;

/**
 * Created by Kevin on 2/28/2018.
 */


public class OnlineDeckAdapter extends RecyclerView.Adapter<OnlineDeckAdapter.ViewHolder> {

    private List<OnlineDeck> collectionData;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView numOfCards;
        public TextView deckName;
        public TextView author;
        public int serverid;
        public View layout;
        private RecycleCallback callbackListener;

        public ViewHolder(View v, RecycleCallback listener) {
            super(v);
            layout = v;
            numOfCards = v.findViewById(R.id.deck_amount);
            deckName = v.findViewById(R.id.deck_name);
            author = v.findViewById(R.id.deck_author);
            v.setOnClickListener(this);
            callbackListener = listener;
        }

        @Override
        public void onClick(View view) {
            callbackListener.onClick(view,getAdapterPosition());
        }
    }

    public OnlineDeckAdapter(List<OnlineDeck> map){
        collectionData = map;
    }

    @Override
    public OnlineDeckAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.onlinedecklayout,parent,false);

        RecycleCallback callback = new RecycleCallback() {
            @Override
            public void onClick(final View cardview, final int pos) {
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                builder.setMessage("Download this deck?");
                builder.setTitle("Download");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (parent.getContext() instanceof DeckDownload) {
                            ((DeckDownload) parent.getContext()).DownloadDeck(collectionData.get(pos));
                        }
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
            }
        };

        ViewHolder vh = new ViewHolder(v,callback);
        return vh;
    }

    public void onDeckAdded(){
        notifyItemInserted(collectionData.size());
    }

    public void onDeckDeleted(int pos){
        notifyItemRemoved(pos);
    }

    public void onFailedDelete(int pos) {
        notifyItemChanged(pos);
    }


    @Override
    public void onBindViewHolder(OnlineDeckAdapter.ViewHolder holder, int i) {
        holder.numOfCards.setText(String.format("%d",collectionData.get(i).GetDeckAmount()));
        holder.author.setText("By "+collectionData.get(i).GetDeckAuthor());
        holder.deckName.setText(collectionData.get(i).GetDeckName());
        holder.serverid = collectionData.get(i).GetCollectionID();

        try{
            if (collectionData.get(i).GetUserID() == User.LoggedIn.GetServerID() && User.LoggedIn.GetServerID() > 0){
                holder.numOfCards.setBackgroundResource(R.drawable.uploadeddeckborder);
            }
        }catch (NullPointerException ignored){

        }
    }

    public void DeleteData() {
        if (!collectionData.isEmpty()) {
            collectionData.clear();
        }
    }

    @Override
    public int getItemCount() {
        return collectionData.size();
    }
}
