package com.example.votersinformation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.votersinformation.Code.DataObjects.User;
import com.example.votersinformation.Code.Database.DatabaseHelper;
import com.google.gson.Gson;

public class AuthenticationActivity extends AppCompatActivity {

    //region async http request

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "saif@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                User us = helper.getUserToAuthenticate(mUsername);

                if (us == null) {
                    mEmailView.setError("Username not exist.");
                    return false;
                }

                if (us.getIs_alive() == 0) {
                    Toast.makeText(getApplicationContext(), "User is not alive.", Toast.LENGTH_SHORT);
                    return false;
                }
                if (us.getIs_active() == 0) {
                    Toast.makeText(getApplicationContext(), "User not in active state, contact with administrator.", Toast.LENGTH_SHORT);
                    return false;
                }

                if (us.getUser_name().equals(mUsername) && us.getPassword().equals(mPassword)) {
                    Gson gson = new Gson();
                    String userJson = gson.toJson(us);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("user", userJson);
                    editor.commit();
                    return true;
                } else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    return false;
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    //endregion

    //region UI references.

    private AutoCompleteTextView mEmailView;
    private View mProgressView;
    private View mLoginFormView;
    private EditText mPasswordView;

    //endregion

    //region private field(s)

    private DatabaseHelper helper;
    private SharedPreferences pref;

    //endregion

    //region method(s)

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        /*
        On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        for very easy animations. If available, use these APIs to fade-in
        the progress spinner.
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            /*
            The ViewPropertyAnimator APIs are not available, so simply show
            and hide the relevant UI components.
            */
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        /* Reset errors. */
        mEmailView.setError(null);
        mPasswordView.setError(null);

        /* Store values at the time of the login attempt. */
        String username = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        /* Check for a valid password, if the user entered one. */
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        /* Check for a valid email address. */
        if (TextUtils.isEmpty(username)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        /*
        There was an error; don't attempt login and focus the first
        form field with an error.
        */
        if (cancel) focusView.requestFocus();
        else {
            /*
            Show a progress spinner, and kick off a background task to
            perform the user login attempt.
            */
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String email) {
        //TODO: Replace this with your own logic
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    //endregion

    //region override

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.title_authentication_activity);

        setContentView(R.layout.activity_authentication);

        /* Set up the login form. */
        mEmailView = findViewById(R.id.actv_username_aa);

        mPasswordView = (EditText) findViewById(R.id.et_password_aa);
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

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        helper = new DatabaseHelper(this);

        pref = getSharedPreferences("user", MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user", "");
        editor.commit();

    }

    //endregion
}
