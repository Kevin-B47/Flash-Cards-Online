package com.example.android.flashcards.classes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.flashcards.R;
import com.example.android.flashcards.SoloPlayActivity;
import com.example.android.flashcards.interfaces.RecycleCallback;

import java.util.List;

/**
 * Created by Kevin on 2/28/2018.
 */


public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private List<Card> collectionData;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView question;
        TextView cardnumber;
        View layout;
        private RecycleCallback callbackListener;

        ViewHolder(View v, RecycleCallback listener) {
            super(v);
            layout = v;
            cardnumber = v.findViewById(R.id.cardnum);
            question = v.findViewById(R.id.questionTxt);
            v.setOnClickListener(this);
            callbackListener = listener;
        }

        @Override
        public void onClick(View view) {
            callbackListener.onClick(view,getAdapterPosition());
        }
    }

    public CardAdapter(List<Card> map){
        collectionData = map;
    }

    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        RecycleCallback callback = new RecycleCallback() {
            @Override
            public void onClick(final View cardview, final int pos) {
                ((SoloPlayActivity)parent.getContext()).JumpToCard(pos);
            }
        };

        View v = inflater.inflate(R.layout.cardlayout,parent,false);
        return new ViewHolder(v,callback);
    }

    @Override
    public void onBindViewHolder(CardAdapter.ViewHolder holder, int i) {
        holder.cardnumber.setText(String.valueOf(i+1));
        holder.question.setText(collectionData.get(i).getFront());
    }

    public void DeleteData(){
        if (!collectionData.isEmpty()){
            collectionData.clear();
        }
    }

    public void AddCard(Card card){
        collectionData.add(card);
        notifyItemInserted(collectionData.size());
    }

    public void RemoveCard(int i){
        collectionData.remove(i);
        notifyItemRemoved(i);

        for(int k = i; k < collectionData.size(); k++){
            notifyItemChanged(k);
        }
    }

    public void CardChanged(int i){
        notifyItemChanged(i);
    }

    @Override
    public int getItemCount() {
        return collectionData.size();
    }
}
