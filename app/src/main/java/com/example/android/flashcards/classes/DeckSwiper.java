package com.example.android.flashcards.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.example.android.flashcards.DecksActivity;
import com.example.android.flashcards.R;

import java.util.ArrayList;

public class DeckSwiper extends ItemTouchHelper.SimpleCallback {

    private RecyclerView.ViewHolder lastHolder = null;
    private float lastMove = 0;
    private Context context;
    private int color = 0;

    protected DeckSwiper(int dragDirs, int swipeDirs, Context c) {
        super(dragDirs, swipeDirs);
        this.context = c;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        Paint p = new Paint();
        Bitmap icon;

        if (context == null){ return;}

        View itemView = viewHolder.itemView;

        c.drawColor(context.getResources().getColor(R.color.whitebackground));

        // Basically a ctrl+v from so

        if (dX > 0){ // move right
            icon = BitmapFactory.decodeResource(context.getResources(),R.drawable.download);

            p.setARGB(255,46, 204, 113);
            c.drawRect(itemView.getLeft(),itemView.getTop(), dX*2,itemView.getBottom(), p);
            c.drawBitmap(icon,
                    itemView.getLeft() + convertDpToPx(24),
                    itemView.getTop() + (itemView.getBottom() - itemView.getTop() - icon.getHeight())/2,
                    p);
        }else if(dX < 0){ // move left
            icon = BitmapFactory.decodeResource(context.getResources(),R.drawable.delete);

            p.setARGB(255,240,52,52);
            c.drawRect(itemView.getRight() + dX*2,itemView.getTop(), itemView.getRight(),itemView.getBottom(), p);
            c.drawBitmap(icon,
                    itemView.getRight() - convertDpToPx(16) - icon.getWidth(),
                    itemView.getTop() + (itemView.getBottom() - itemView.getTop() - icon.getHeight())/2,
                    p);
        }
    }


    protected void onSwipeCallback(Context c, ArrayList<Deck> decks, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int direction){
        if (direction == ItemTouchHelper.LEFT){ // delete
            try{
                Deck d = decks.get(viewHolder.getAdapterPosition());
                Toast.makeText(c,"Deck "+d.getName()+" deleted",Toast.LENGTH_SHORT).show();
                ((DeckAdapter)recyclerView.getAdapter()).deleteDeck(viewHolder.getAdapterPosition());
                d.delete(c);
            }catch(IndexOutOfBoundsException e){
                Toast.makeText(c,"Error deleting "+viewHolder.getAdapterPosition(),Toast.LENGTH_SHORT).show();
            }catch(NullPointerException e){
                Toast.makeText(c,"Error deleting "+viewHolder.getAdapterPosition(),Toast.LENGTH_SHORT).show();
            }
        }else if(direction == ItemTouchHelper.RIGHT){ // upload
            try{
                ((DecksActivity)c).onUploadClicked(viewHolder.getAdapterPosition());
            }catch(IndexOutOfBoundsException e){
                Toast.makeText(c,"Error Uploading "+viewHolder.getAdapterPosition(),Toast.LENGTH_SHORT).show();
            }catch(NullPointerException e){
                Toast.makeText(c,"Error Uploading "+viewHolder.getAdapterPosition(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int convertDpToPx(int dp){
        return Math.round(dp * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }
}
