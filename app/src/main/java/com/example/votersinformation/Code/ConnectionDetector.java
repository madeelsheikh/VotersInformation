package com.example.votersinformation.Code;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {

    //region field(s)

    private Context _context;

    //endregion

    //region constructor(s)

    public ConnectionDetector(Context context) {
        _context = context;
    }

    //endregion

    //region method(s)

    public boolean isConnected() {

        ConnectivityManager manager = (ConnectivityManager) _context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo network = manager.getActiveNetworkInfo();
            if (network != null) {
                return network.isConnected();
            }
        }
        return false;
    }

    //endregion
}
