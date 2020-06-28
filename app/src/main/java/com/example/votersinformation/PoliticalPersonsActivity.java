package com.example.votersinformation;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.votersinformation.Code.Adapter.PoliticalPersonsRecyclerAdapter;
import com.example.votersinformation.Code.DataObjects.PoliticalPersonsWithAffiliation;
import com.example.votersinformation.Code.DataObjects.User;
import com.example.votersinformation.Code.Database.DatabaseHelper;
import com.example.votersinformation.Code.MyDividerItemDecoration;
import com.google.gson.Gson;

import java.util.List;

public class PoliticalPersonsActivity extends
        AppCompatActivity implements
        PoliticalPersonsRecyclerAdapter.PoliticalPersonsAffiliationAdapterListener {

    //region private field(s)

    private static final String TAG = PoliticalPersonsActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private DatabaseHelper helper;
    PoliticalPersonsRecyclerAdapter adapter;
    private List<PoliticalPersonsWithAffiliation> politicalPersonWithAffiliationList;
    private SearchView searchView;
    private SharedPreferences pref;

    //endregion

    //region method(s)

    public void political_person_detail_view(View view) {
        int political_person_id = view.getId();

        Intent intent = new Intent(this, VotersActivity.class);
        Bundle extras = new Bundle();
        extras.putString("referrer_page","Political Person");
        extras.putInt("political_person_id",political_person_id);
        intent.putExtras(extras);
        startActivity(intent);
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    //endregion

    //region override

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.title_political_person_activity);

        setContentView(R.layout.activity_political_persons);

        pref = getSharedPreferences("user", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("user", "");

        if (json == "") {
            return;
        }

        User us = gson.fromJson(json, User.class);

        String file_id = null;
        String filterOption = null;

        if (us.getIs_admin() == 1) {
            file_id = "";
            filterOption = "";
        }
        else{
            filterOption = us.getFilter_option();
            if(filterOption.equals("Village")) file_id = us.getVillage();
            else file_id = String.valueOf(us.getStat_block_code());
        }

        recyclerView = findViewById(R.id.rv_political_persons);
        helper = new DatabaseHelper(this);

        whiteNotificationBar(recyclerView);
        politicalPersonWithAffiliationList = helper.getListPoliticalPersonWithAffiliationCount(file_id, filterOption);
        RecyclerView recyclerView = findViewById(R.id.rv_political_persons);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        adapter = new PoliticalPersonsRecyclerAdapter(this, politicalPersonWithAffiliationList, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.political_persons_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //endregion

    //region interface implementation

    @Override
    public void onPoliticalPersonWithAffiliationSelected(PoliticalPersonsWithAffiliation politicalPersonWithAffiliation) {
        /*Toast.makeText(getApplicationContext(), "Selected: " + politicalPersonWithAffiliation.getName() + ", " + politicalPersonWithAffiliation.getAlive(), Toast.LENGTH_LONG).show();*/
    }

    //endregion
}
