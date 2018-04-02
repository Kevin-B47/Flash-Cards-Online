package com.example.android.flashcards;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.flashcards.classes.AuthedUser;
import com.example.android.flashcards.classes.User;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private ProgressBar progessBar;

    private LinearLayout regLayout;
    private LinearLayout loginLayout;

    public void HideKeyboard(){
        try {
            InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }catch(NullPointerException ignored){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = findViewById(R.id.username);
        progessBar = findViewById(R.id.progressBar);
        progessBar.setVisibility(View.INVISIBLE);
        regLayout = findViewById(R.id.registerLayout);
        loginLayout = findViewById(R.id.loginLayout);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.login);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        View mLoginFormView = findViewById(R.id.login_form);

        String token = User.LoginToken(this);
        String email = User.GetLastEmail(this);

        if (!User.HasConnection(this)){
            LoginNoConnection();
        }

        if (!token.equals("-1") && !email.equals("-1")){
            try {
                User.ValidateToken(this, email, token);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Button loginOfflineButt = findViewById(R.id.loginoffline);

        loginOfflineButt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginNoConnection();
            }
        });
    }

    public void RegisterUser(View v){
        EditText emailView = findViewById(R.id.email);
        EditText passwordView = findViewById(R.id.password_reg);
        EditText displaynameView = findViewById(R.id.displayname);

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String displayname = displaynameView.getText().toString();

        HideKeyboard();
        User.CreateNew(this,email,password,displayname,progessBar);

    }

    public void LoginNoConnection(){
        Toast.makeText(this,"Logging in locally",Toast.LENGTH_SHORT).show();
        User.LoggedIn = new AuthedUser();
        startActivity(new Intent(this, MenuActivity.class));
        finish();
    }

    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            // = new UserLoginTask(email, password);
            //mAuthTask.execute((Void) null);

            HideKeyboard();

            if (User.HasConnection(this)) {
                User.LoginNoToken(this, email, password, progessBar);
            }else{
                LoginNoConnection();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    public void RegisterUserView (View v){
        if (regLayout != null && loginLayout != null){
            this.regLayout.setVisibility(View.VISIBLE);
            this.loginLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void LoginUserView(){
        if (regLayout != null && loginLayout != null){
            this.regLayout.setVisibility(View.INVISIBLE);
            this.loginLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed(){
        if (regLayout != null && loginLayout != null && regLayout.getVisibility() == View.VISIBLE) {
            LoginUserView();
        }
    }
}

