package com.example.votersinformation.Code.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.votersinformation.Code.DataObjects.User;
import com.example.votersinformation.R;

import java.util.ArrayList;

public class ManageUserListAdapter extends ArrayAdapter<User> {

    //region field(s)

    private LayoutInflater mInflater;
    private ArrayList<User> userInfo;
    private int mViewResourceId;

    //endregion

    //region constructor(s)

    public ManageUserListAdapter(Context context, int textViewResourceId, ArrayList<User> userInfo) {
        super(context, textViewResourceId, userInfo);
        this.userInfo = userInfo;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = textViewResourceId;
    }

    //endregion

    //region method(s)

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.i_lv_u, null);

        User user = userInfo.get(position);

        if (user != null) {
            TextView user_name = (TextView) convertView.findViewById(R.id.tv_man_user_name);
            TextView email_address = (TextView) convertView.findViewById(R.id.tv_man_email_address);


            ImageView status = convertView.findViewById(R.id.s_user_status);
            if (user_name != null) {
                user_name.setText(user.getUser_name());
            }
            if (email_address != null) {
                email_address.setText((user.getEmail_address()));
            }

            if (status != null) {
                status.setId(user.getUser_id());
            }
        }

        return convertView;
    }

    //endregion
}