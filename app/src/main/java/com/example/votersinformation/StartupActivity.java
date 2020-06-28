package com.example.votersinformation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.votersinformation.Code.Database.DatabaseHelper;
import com.example.votersinformation.Code.Services.Dropbox.Action;
import com.example.votersinformation.Code.Services.Dropbox.DropboxManager;
import com.example.votersinformation.Code.Shared;

public class StartupActivity extends AppCompatActivity {

    //region async http request

    /**
     * An asynchronous processing task that makes an HTTP request for upload / download.
     */
    public class AsyncHttpRequest extends AsyncTask<Void, Void, Boolean> {

        private StartupActivity activity;
        private Action action;
        private ProgressDialog progressDialog;

        public AsyncHttpRequest(StartupActivity activity, Action action) {
            this.activity = activity;
            this.action = action;
        }

        @Override
        protected void onPreExecute() {
            // Produce the progress dialog
            this.progressDialog = new ProgressDialog(activity);
            switch (action) {
                case BACKUP_DROPBOX:
                    this.progressDialog.setTitle("Synchronizing ...");
                    this.progressDialog.setMessage("Please wait for a while.");
                    break;
                case RESTORE_DROPBOX:
                    this.progressDialog.setTitle("Synchronizing ...");
                    this.progressDialog.setMessage("Please wait for a while.");
                    break;
                default:
                    break;
            }
            this.progressDialog.setCancelable(false);  // Cancel Disable
            this.progressDialog.show();
            return;
        }

        @Override
        protected Boolean doInBackground(Void... builder) {
            boolean isSuccess = false;
            String srcFilePath = "/data/data/" + getPackageName() + "/databases/voters-new.db";  // SQLite in Android terminal Data
            String dstFilePath = "voters-new.db";  // Filename to be saved in Dropbox

            switch (action) {
                case BACKUP_DROPBOX: {
                    isSuccess = dropboxManager.backup(srcFilePath, dstFilePath);
                    break;
                }
                case RESTORE_DROPBOX: {
                    isSuccess = dropboxManager.restore(dstFilePath, srcFilePath);
                    break;
                }
                default:
                    break;
            }
            return isSuccess;
        }

        @Override
        protected void onPostExecute(Boolean Result) {
            // close the progress dialog
            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }
            Shared.callback(activity.getApplicationContext(), action, Result);
        }
    }

    //endregion

    //region private field(s)

    private DropboxManager dropboxManager;
    private Action currentAction;
    private DatabaseHelper helper;

    //endregion

    //region activity related functionality

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /***
         * Full screen activity
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_startup);

        currentAction = Action.NONE;

        helper = new DatabaseHelper(this);
        String res = Shared.CheckDatabaseExists(this, helper);
        if (res == null) {
            Toast.makeText(this, "Something went wrong. Please re-install your application.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Synchronizing...");
        builder.setMessage("If this is first time after application install please press OK. If it is not first time then first SYNC your work and press OK. Otherwise by pressing OK your work may be lost.");
        // Add the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                /* Update from Dropbox API */

                try {
                    if (dropboxManager == null) {
                        dropboxManager = new DropboxManager(getApplicationContext());
                    }
                    /* perform the download */
                    new AsyncHttpRequest(StartupActivity.this, Action.RESTORE_DROPBOX).execute();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {



                Intent i = new Intent(getApplicationContext(), AuthenticationActivity.class);
                startActivity(i);

            }
        });

        AlertDialog dialog  = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (currentAction) {
            case NONE:
                break;
            case BACKUP_DROPBOX:
                currentAction = Action.NONE;

                try {
                    String token = dropboxManager.getAccessToken();
                    if (token == null) {
                        break;
                    }
                    dropboxManager = new DropboxManager(this);

                    /* run the upload */
                    new AsyncHttpRequest(this, Action.BACKUP_DROPBOX).execute();

                } catch (IllegalStateException e) {
                    /* IllegalStateException */
                }
                break;
            case RESTORE_DROPBOX:
                currentAction = Action.NONE;

                try {
                    String token = dropboxManager.getAccessToken();
                    if (token == null) {
                        break;
                    }

                    dropboxManager = new DropboxManager(this);

                    new AsyncHttpRequest(this, Action.RESTORE_DROPBOX).execute();
                } catch (IllegalStateException e) {
                    /* IllegalStateException */
                }
                break;
            default:
                break;
        }
    }

    //endregion
}
