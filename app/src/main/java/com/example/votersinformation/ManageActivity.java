package com.example.votersinformation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.example.votersinformation.Code.Adapter.ManageUserListAdapter;
import com.example.votersinformation.Code.DataObjects.User;
import com.example.votersinformation.Code.Database.DatabaseHelper;

import java.util.ArrayList;

public class ManageActivity extends AppCompatActivity {

    //region field(s)

    private ListView listViewUsers;
    private ManageUserListAdapter adapter;
    private ArrayList<User> users;
    private DatabaseHelper helper;

    private RadioGroup radioGroup;

    //endregion

    //region method(s)

    public void change_user_status_window(View v) {
        int user_id = v.getId();
        Intent i = new Intent(this, UserMaintenanceActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("user_id", user_id);
        i.putExtras(extras);
        startActivity(i);
    }

    private void bind_users_with_grid(int status) {
        users = helper.getUsers(status);
        listViewUsers = findViewById(R.id.lv_manage_users);
        adapter = new ManageUserListAdapter(this, R.layout.activity_manage, users);
        listViewUsers.setAdapter(adapter);
    }

    //endregion

    //region override

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.title_manage_activity);

        setContentView(R.layout.activity_manage);

        helper = new DatabaseHelper(this);

        radioGroup = findViewById(R.id.rg_user);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.active_user) {
                    bind_users_with_grid(1);
                } else if (checkedId == R.id.not_active_user) {
                    bind_users_with_grid(0);
                } else {

                }
            }
        });

        bind_users_with_grid(1);
    }
    //endregion
}
