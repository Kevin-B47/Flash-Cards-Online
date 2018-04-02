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

import com.example.android.flashcards.R;

public class CardSwiper extends ItemTouchHelper.SimpleCallback {

    private Context context;

    public CardSwiper(int dragDirs, int swipeDirs, Context c) {
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

        if(dX < 0){ // move left
            icon = BitmapFactory.decodeResource(context.getResources(),R.drawable.delete);

            p.setARGB(255,240,52,52);
            c.drawRect(itemView.getRight() + dX*2,itemView.getTop(), itemView.getRight(),itemView.getBottom(), p);
            c.drawBitmap(icon,
                    itemView.getRight() - convertDpToPx(16) - icon.getWidth(),
                    itemView.getTop() + (itemView.getBottom() - itemView.getTop() - icon.getHeight())/2,
                    p);
        }
    }

    protected void onSwipeCallback(Context c, Deck loadedDeck, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int direction){
        if (direction == ItemTouchHelper.LEFT) { // delete
            try {
                Card card = loadedDeck.getCard(viewHolder.getAdapterPosition());
                Toast.makeText(c, "Card " + card.getFront() + " deleted", Toast.LENGTH_SHORT).show();
                ((CollectionAdapter) recyclerView.getAdapter()).RemoveCard(viewHolder.getAdapterPosition());
                loadedDeck.save(c);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(c, "Error deleting " + viewHolder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Toast.makeText(c, "Error deleting " + viewHolder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
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
