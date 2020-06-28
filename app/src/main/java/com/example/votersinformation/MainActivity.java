package com.example.votersinformation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.votersinformation.Code.ConnectionDetector;
import com.example.votersinformation.Code.DataObjects.User;
import com.example.votersinformation.Code.Database.DatabaseHelper;
import com.example.votersinformation.Code.Services.Dropbox.Action;
import com.example.votersinformation.Code.Services.Dropbox.DropboxManager;
import com.example.votersinformation.Code.Shared;
import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //region async http request

    /**
     * An asynchronous processing task that makes an HTTP request for upload / download.
     */
    public class AsyncHttpRequest extends AsyncTask<Void, Void, Boolean> {

        private MainActivity activity;
        private Action action;
        private ProgressDialog progressDialog;

        public AsyncHttpRequest(MainActivity activity, Action action) {
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
                case UPLOAD_VOTERS_LIST:
                    this.progressDialog.setTitle("Uploading ...");
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

            switch (action) {
                case BACKUP_DROPBOX: {
                    String srcFilePath = "/data/data/" + getPackageName() + "/databases/voters-new.db";  // SQLite in Android terminal Data
                    String dstFilePath = "voters-new.db";  // Filename to be saved in Dropbox
                    isSuccess = dropboxManager.backup(srcFilePath, dstFilePath);
                    break;
                }
                case RESTORE_DROPBOX:
                    break;
                case UPLOAD_VOTERS_LIST:
                    String srcFilePath = db_directoryPath + "/" + db_fileName;
                    String dstFilePath = db_fileName;
                    isSuccess = dropboxManager.backup(srcFilePath, dstFilePath);
                    break;
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

    private SharedPreferences pref;
    private ConnectionDetector inet_detect;

    private String db_directoryPath = null;
    private String db_fileName = null;

    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;

    //endregion

    //region override

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Gson gson = new Gson();
        pref = getSharedPreferences("user", MODE_PRIVATE);
        String json = pref.getString("user", "");

        if (json == "") {
            Intent intent = new Intent(this, AuthenticationActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();

        MenuItem item1 = (MenuItem) menu.findItem(R.id.nav_add_user);
        MenuItem item2 = (MenuItem) menu.findItem(R.id.nav_manage_users);
        MenuItem item5 = (MenuItem) menu.findItem(R.id.nav_export);

        Button btnPP = (Button) findViewById(R.id.btn_political_persons);
        btnPP.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PoliticalPersonsActivity.class));
                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        Button btnV = (Button) findViewById(R.id.btn_voters);
        btnV.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VotersActivity.class));
                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        Button btnF = (Button) findViewById(R.id.btn_filters);
        btnF.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FilterationActivity.class));
                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        TextView voter = (TextView) findViewById(R.id.tv_add_voter);

        voter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ParameterActivity.class);
                Bundle extras = new Bundle();
                extras.putString("referrer", "Add Voter");
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        currentAction = Action.NONE;


        User us = gson.fromJson(json, User.class);

        View headerView = navigationView.getHeaderView(0);

        TextView navUsername = (TextView) headerView.findViewById(R.id.tv_current_user_name);
        navUsername.setText(us.getUser_name());

        TextView navEmailAddress = (TextView) headerView.findViewById(R.id.tv_current_user_email_address);
        navEmailAddress.setText(us.getEmail_address());

        if (us.getIs_admin() == 0) {
            item1.setVisible(false);
            item2.setVisible(false);
            item5.setVisible(false);
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (doubleBackToExitPressedOnce) {
                pref = getPreferences(Context.MODE_PRIVATE);

                pref = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("user", "");
                editor.commit();

                Intent intent = new Intent(this, AuthenticationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_logout) {

            pref = getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("user", "");
            editor.commit();

            Intent intent = new Intent(this, AuthenticationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_user) {

            Intent intent = new Intent(this, ParameterActivity.class);
            Bundle extras = new Bundle();
            extras.putString("referrer", "Add User");
            intent.putExtras(extras);
            startActivity(intent);
            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_manage_users) {

            Intent intent = new Intent(this, ManageActivity.class);
            Bundle extras = new Bundle();
            extras.putString("referrer", "Add User");
            intent.putExtras(extras);
            startActivity(intent);

        } else if (id == R.id.nav_add_region) {
            Intent intent = new Intent(this, ParameterActivity.class);
            Bundle extras = new Bundle();
            extras.putString("referrer", "Add Region");
            intent.putExtras(extras);
            startActivity(intent);
        } else if (id == R.id.nav_sync) {
            inet_detect = new ConnectionDetector(this);
            if (inet_detect.isConnected()) {
                currentAction = Action.NONE;
                try {
                    if (dropboxManager == null) {
                        dropboxManager = new DropboxManager(getApplicationContext());
                    }
                    /* perform the download */
                    new AsyncHttpRequest(MainActivity.this, Action.BACKUP_DROPBOX).execute();

                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                            .remove("isFirstRun").commit();
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Internet not available, Cross check your internet connectivity and try again", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_export) {

            String datetime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

            String fileName = "voters_list_v" + datetime + ".xls";

            File directory = null;

            directory = new File("/data/data/" + getPackageName() + "/Docs/");

            if (!directory.isDirectory()) {
                directory.mkdirs();
            }

            helper = new DatabaseHelper(this);
            helper.dataExport(directory, fileName);

            db_directoryPath = directory.toString();
            Toast.makeText(this, db_directoryPath, Toast.LENGTH_SHORT);
            db_fileName = fileName;

            inet_detect = new ConnectionDetector(this);
            if (inet_detect.isConnected()) {
                currentAction = Action.NONE;
                try {
                    if (dropboxManager == null) {
                        dropboxManager = new DropboxManager(getApplicationContext());
                    }
                    /* perform the download */
                    new AsyncHttpRequest(MainActivity.this, Action.UPLOAD_VOTERS_LIST).execute();

                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                            .remove("isFirstRun").commit();
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Internet not available, Cross check your internet connectivity and try again", Toast.LENGTH_SHORT).show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                    new MainActivity.AsyncHttpRequest(this, Action.BACKUP_DROPBOX).execute();

                } catch (IllegalStateException e) {
                    /* IllegalStateException */
                }
                break;
            case RESTORE_DROPBOX:
                break;
            default:
                break;
        }
    }

    //endregion
}
