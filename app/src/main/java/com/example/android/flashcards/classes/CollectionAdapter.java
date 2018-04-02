package com.example.android.flashcards.classes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.flashcards.EditDeckActivity;
import com.example.android.flashcards.R;
import com.example.android.flashcards.interfaces.RecycleCallback;

import java.util.List;

/**
 * Created by Kevin on 2/28/2018.
 */


public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    private List<Card> collectionData;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView question;
        public TextView answer;
        public TextView cardnumber;
        public View layout;
        private RecycleCallback callbackListener;

        public ViewHolder(View v, RecycleCallback listener) {
            super(v);
            layout = v;
            cardnumber = v.findViewById(R.id.cardnum);
            question = v.findViewById(R.id.questionTxt);
            answer = v.findViewById(R.id.answerTxt);
            v.setOnClickListener(this);
            callbackListener = listener;
        }

        @Override
        public void onClick(View view) {
            callbackListener.onClick(view,getAdapterPosition());
        }
    }

    public CollectionAdapter(List<Card> map){
        collectionData = map;
    }

    @Override
    public CollectionAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());



        RecycleCallback callback = new RecycleCallback() {
            @Override
            public void onClick(final View cardview, final int pos) {
                ViewGroup viewGroup = parent.findViewById(R.id.deck_edit_layout);
                final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                builder.setTitle("");
                final View inflate = inflater.inflate(R.layout.editcard,viewGroup,false);

                builder.setView(inflate);

                final EditText frontTxt = inflate.findViewById(R.id.edit_front);
                final EditText backTxt = inflate.findViewById(R.id.edit_back);

                frontTxt.setText(collectionData.get(pos).getFront());
                backTxt.setText(collectionData.get(pos).getBack());

                builder.setPositiveButton("Edit Card", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){
                        String front = frontTxt.getText().toString();
                        String back = backTxt.getText().toString();

                        EditDeckActivity act = ((EditDeckActivity) parent.getContext());
                        act.onCardEdited(pos,front,back);
                    }
                });

                AlertDialog dialog = builder.show();

                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) positive.getLayoutParams();
                params.gravity = Gravity.CENTER;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                if (Build.VERSION.SDK_INT >= 23) {
                    positive.setBackgroundColor(parent.getContext().getColor(R.color.whitebackground));
                }
                else {
                    positive.setBackgroundColor(parent.getContext().getResources().getColor(R.color.whitebackground));
                }
            }
        };

        View v = inflater.inflate(R.layout.flashlayout,parent,false);
        ViewHolder vh = new ViewHolder(v,callback);
        return vh;
    }

    @Override
    public void onBindViewHolder(CollectionAdapter.ViewHolder holder, int i) {
        holder.cardnumber.setText(String.valueOf(i+1));
        holder.question.setText(collectionData.get(i).getFront());
        holder.answer.setText(collectionData.get(i).getBack());
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
