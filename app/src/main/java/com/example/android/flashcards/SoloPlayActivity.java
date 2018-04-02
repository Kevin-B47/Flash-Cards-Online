package com.example.android.flashcards;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BlurMaskFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.flashcards.classes.Card;
import com.example.android.flashcards.classes.CardAdapter;
import com.example.android.flashcards.classes.Deck;
import com.example.android.flashcards.classes.FCApplication;
import com.example.android.flashcards.classes.OnSwipeTouchListener;

/*
*   Activity for Solo play allows user to go through deck, shuffle and flip cards
*
 */
//TODO Maybe a small indicator in a corner to show weather the user is viewing
//the front or back of a card
public class SoloPlayActivity extends AppCompatActivity {
    private Deck loadedDeck;
    private int currentCardNum = 1;

    private TextView frontText;
    private TextView backText;
    private TextView numOfCards;
    private Button answerButt;
    private TextView correctTxt;
    private boolean isBlurred = true;
    private long cooldown = 0;
    private int correct = 0;
    private boolean wasFinished = false;

    private AlertDialog currentBuilder;

    private SparseBooleanArray correctList = new SparseBooleanArray();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_play);
        Intent intentExtras = getIntent();

        frontText = findViewById(R.id.cardViewFront);
        backText = findViewById(R.id.cardViewBack);
        numOfCards = findViewById(R.id.numofCards);
        answerButt = findViewById(R.id.answer_butt);
        correctTxt = findViewById(R.id.numCorrect);

        if (!intentExtras.hasExtra("filename")){
            return;
        }


        String filename = intentExtras.getStringExtra("filename");

        if (!Deck.DoesDeckExistByFileName(this,filename)){
            Toast.makeText(this,"Deck "+filename+" no longer exists",Toast.LENGTH_SHORT).show();
            return;
        }

        loadedDeck = Deck.LoadDeckByFileName(this,filename);

        if (loadedDeck.getSize() == 0){
            frontText.setText("R u dum??");
            backText.setText("YES");
            Toast.makeText(this,"This deck has 0 cards what r u doing??",Toast.LENGTH_SHORT).show();
            return;
        }

        Card c = loadedDeck.getCard(currentCardNum-1);

        TextView deckname = findViewById(R.id.deckNameView);
        deckname.setText(loadedDeck.getName());
        numOfCards.setText("1/"+String.valueOf(loadedDeck.getSize()));
        correctTxt.setText(String.valueOf(correct)+"/"+loadedDeck.getSize()+" Correct");
        frontText.setText(c.getFront());
        backText.setText(c.getBack());

        float radius = backText.getTextSize()/3;
        backText.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        BlurMaskFilter mask = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
        backText.getPaint().setMaskFilter(mask);

        SetBackTextTouch();
        SetFrontTextTouch();
        SetCardNumTouch();
        FCApplication.incrementGamesPlayed();
    }

    private void UpdateBlur(){
        float radius = backText.getTextSize()/2;
        final ValueAnimator blurAnimate = ValueAnimator.ofFloat(0f,radius);
        blurAnimate.setDuration(100);
        blurAnimate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if(valueAnimator.getAnimatedFraction() != 0f){
                backText.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
                BlurMaskFilter mask = new BlurMaskFilter((float)valueAnimator.getAnimatedValue(), BlurMaskFilter.Blur.NORMAL);
                backText.getPaint().setMaskFilter(mask);
            }
            }
        });

        /*ObjectAnimator flip = ObjectAnimator.ofFloat(backText,"rotationX",270f,360f);
        flip.setDuration(100);
        flip.start();*/ // Kinda annoying animation

        blurAnimate.start();

    }

    public void UpdateCorrect(){
        correct++;
        correctList.put(currentCardNum-1,true);
        correctTxt.setText(String.valueOf(correct)+"/"+loadedDeck.getSize()+" Correct");

        if (correct == loadedDeck.getSize() && !wasFinished){
            FCApplication.incrementGamesWon();
        }
    }

    public void UpdateCardStatus(){
        numOfCards.setText(currentCardNum+"/"+loadedDeck.getSize());

        if (correctList.get(currentCardNum-1)){
            Unblur();
            answerButt.setVisibility(View.INVISIBLE);
            backText.setTextColor(this.getResources().getColor(R.color.correct));
        }else{
            answerButt.setVisibility(View.VISIBLE);
            backText.setTextColor(this.getResources().getColor(R.color.textcolor));
            isBlurred = true;
            float radius = backText.getTextSize()/2;
            backText.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
            BlurMaskFilter mask = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
            backText.getPaint().setMaskFilter(mask);
        }
    }

    public void onMenuClicked(View v){
        Intent menu = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(menu);
        finish();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void SetFrontTextTouch(){
        final Context context = this;
        frontText.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeRight(){
                if(currentCardNum > 1){
                    currentCardNum--;
                }else{
                    currentCardNum = loadedDeck.getSize();
                }

                backText.setText("");
                ((SoloPlayActivity)context).UpdateCardStatus();
                doCardAnimations(0,200);
            }

            @Override
            public void onSwipeLeft(){
                if(currentCardNum < loadedDeck.getSize()){
                    currentCardNum++;
                }else{
                    currentCardNum = 1;
                }

                backText.setText("");
                doCardAnimations(0,-200);
                ((SoloPlayActivity)context).UpdateCardStatus();
            }
        });
    }

    public void AttemptedAnswer(View v){
        final Context c = this;
        final Card card;

        try{
            card = loadedDeck.getCard(currentCardNum-1);
        }catch(NullPointerException e){
            Toast.makeText(c,"Card doesn't exist anymore",Toast.LENGTH_SHORT).show();
            return;
        }

        final LayoutInflater inflater = LayoutInflater.from(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ViewGroup view = findViewById(R.id.soloplaylayout);

        final View inflate = inflater.inflate(R.layout.answercard,view,false);
        builder.setView(inflate);
        builder.setTitle("");

        TextView questionTxt = inflate.findViewById(R.id.deck_question);
        questionTxt.setText(card.getFront());


        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                TextView answerTxt = inflate.findViewById(R.id.deck_answer);
                String answer = answerTxt.getText().toString();

                    if (answer.equalsIgnoreCase(card.getBack())){
                        backText.setTextColor(c.getResources().getColor(R.color.correct));
                        Toast.makeText(c,"Nice one!",Toast.LENGTH_SHORT).show();
                        Unblur();
                        UpdateCorrect();
                        answerButt.setVisibility(View.INVISIBLE);
                    }else{
                        Toast.makeText(c,"Your answer is incorrect!",Toast.LENGTH_SHORT).show();
                    }

            }
        });

        AlertDialog dialog = builder.show();

        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) positive.getLayoutParams();
        params.gravity = Gravity.CENTER;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    public void Unblur(){
        backText.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        backText.getPaint().setMaskFilter(null);
    }

    public void ForceBlur(){
        float radius = backText.getTextSize()/2;
        backText.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        BlurMaskFilter mask = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
        backText.getPaint().setMaskFilter(mask);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void SetBackTextTouch(){
        final Context context = this;

        backText.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Log.d("Action",eventActionToString(motionEvent.getAction()));
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (cooldown < System.currentTimeMillis()) { // needed or else you can just unblur like a machine gun
                        cooldown = System.currentTimeMillis() + 500;
                        if (isBlurred) {
                            isBlurred = false;
                            Unblur();

                        /*ObjectAnimator flip = ObjectAnimator.ofFloat(backText,"rotationX",60f,0f);
                        flip.setDuration(100);
                        flip.start();*/
                        } else {
                            isBlurred = true;
                            UpdateBlur();
                        }
                    }
                }
                return super.onTouch(view, motionEvent);
            }

            @Override
            public void onSwipeRight(){
                if(currentCardNum > 1){
                    currentCardNum--;
                }else{
                    currentCardNum =loadedDeck.getSize();
                }

                ForceBlur();
                backText.setText("");
                doCardAnimations(0,200);
                ((SoloPlayActivity)context).UpdateCardStatus();
            }
            @Override
            public void onSwipeLeft(){
                if(currentCardNum < loadedDeck.getSize()){
                    currentCardNum++;
                }else{
                    currentCardNum = 1;
                }

                ForceBlur();
                backText.setText("");
                doCardAnimations(0,-200);
                ((SoloPlayActivity)context).UpdateCardStatus();
            }
        });
    }

    public void JumpToCard(int pos) {
        backText.setText("");
        ForceBlur();

        try{
            currentCardNum = pos+1;
        }catch(IndexOutOfBoundsException e){
            currentCardNum = pos;
        }

        if (pos+1 < currentCardNum){
            doCardAnimations(0,200);
        }else if(pos+1 > currentCardNum){
            doCardAnimations(0,-200);
        }

        if (currentBuilder != null){
            currentBuilder.dismiss();
        }

        UpdateCardStatus();
        frontText.setText(loadedDeck.getCard(currentCardNum-1).getFront());
        backText.setText(loadedDeck.getCard(currentCardNum-1).getBack());
    }

    public void SetCardNumTouch(){
        final RecyclerView.Adapter recycleAdapter = new CardAdapter(loadedDeck.GetCards());
        final Context c = this;

        numOfCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LayoutInflater inflater = LayoutInflater.from(c);
                AlertDialog.Builder builder = new AlertDialog.Builder(c);

                final ViewGroup layout = findViewById(R.id.soloplaylayout);
                final View inflate = inflater.inflate(R.layout.cardjump,layout,false);
                builder.setView(inflate);

                RecyclerView rView = inflate.findViewById(R.id.jumptocards);
                rView.setLayoutManager(new LinearLayoutManager(c));
                rView.setAdapter(recycleAdapter);

                currentBuilder =  builder.create();
                currentBuilder.show();
            }
        });
    }

    public void doCardAnimations(final float start, final float end){
        ObjectAnimator moveFront = ObjectAnimator.ofFloat(frontText,"translationX",start,end);
        moveFront.setDuration(100);
        moveFront.start();

        final Context c = this;

        moveFront.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator move2 = ObjectAnimator.ofFloat(frontText,"translationX",-end,start);
                move2.setDuration(100);
                move2.start();

                try{
                    Card c = loadedDeck.getCard(currentCardNum-1);
                    frontText.setText(c.getFront());
                    backText.setText(c.getBack());
                }catch(NullPointerException e){
                    Toast.makeText(c,"Card doesn't exist anymore",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }
        });

        ObjectAnimator moveBack = ObjectAnimator.ofFloat(backText,"translationX",start,end);
        moveBack.setDuration(100);
        moveBack.start();

        moveBack.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator move2 = ObjectAnimator.ofFloat(backText,"translationX",-end,start);
                move2.setDuration(100);
                move2.start();

                try{
                    Card c = loadedDeck.getCard(currentCardNum-1);
                    frontText.setText(c.getFront());
                    backText.setText(c.getBack());
                }catch(NullPointerException e){
                    Toast.makeText(c,"Card doesn't exist anymore",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this,MenuActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}
