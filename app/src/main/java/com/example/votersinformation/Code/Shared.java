package com.example.votersinformation.Code;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.votersinformation.AuthenticationActivity;
import com.example.votersinformation.Code.Database.DatabaseHelper;
import com.example.votersinformation.Code.Services.Dropbox.Action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Shared {

    //region public method(s)

    public static String CheckDatabaseExists(Context context, DatabaseHelper helper) {

        try {
            File database = context.getDatabasePath(helper.DBNAME);
            if (false == database.exists()) {
                helper.getReadableDatabase();
                //Copy db
                if (CopyDatabase(context, helper.DBLOCATION, helper.DBNAME)) {
                    return "Database copied to root folder.";
                } else {
                    return "Error while copying database.";
                }
            }
        } catch (Exception e) {
            return null;
        }
        return "Database already exists.";
    }

    /**
     * Called after asynchronous processing is over.
     * Display a message.
     */
    public static void callback(Context context, Action action, boolean result) {
        switch (action) {
            case NONE:
                break;
            case BACKUP_DROPBOX:
                if (result) {
                    /* success */
                } else {
                    /* failed */
                }
                break;
            case RESTORE_DROPBOX:
                /* failed */
                if (result) {
                    /* success */
                    Toast.makeText(context, "Download successful.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(context, AuthenticationActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                } else Toast.makeText(context, "Download failed.", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    //endregion

    //region private method(s)

    private static boolean CopyDatabase(Context context, String dbLoc, String dbName) {
        try {
            InputStream inputStream = context.getAssets().open(dbName);
            String outFileName = dbLoc + dbName;
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buff = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            Log.w("AppStartActivity", "Database copied");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //endregion

}
