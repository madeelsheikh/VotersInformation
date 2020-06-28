package com.example.votersinformation;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.votersinformation.Code.Adapter.FilterColumnRecyclerAdapter;
import com.example.votersinformation.Code.DataObjects.FilterColumn;
import com.example.votersinformation.Code.DataObjects.User;
import com.example.votersinformation.Code.Database.DatabaseHelper;
import com.example.votersinformation.Code.MyDividerItemDecoration;
import com.google.gson.Gson;

import java.util.List;

public class FilterationActivity extends
        AppCompatActivity implements
        FilterColumnRecyclerAdapter.FilterColumnAdapterListener {

    //region private field(s)

    private static final String TAG = FilterationActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    FilterColumnRecyclerAdapter adapter;
    private List<FilterColumn> filterColumnList;
    private SearchView searchView;
    private DatabaseHelper helper;
    private SharedPreferences pref;
    private String fileId;
    private String filterOption;

    //endregion

    //region method(s)

    public void generic_column_detail_view(View v) {
        int id = v.getId();
        String column = null;
        Spinner spinner = findViewById(R.id.sp_column_names);
        column = spinner.getSelectedItem().toString();

        Intent intent = new Intent(this, VotersActivity.class);
        Bundle extras = new Bundle();
        extras.putString("referrer_page", "Filters");
        extras.putInt("id", id);
        extras.putString("column_name", column);
        intent.putExtras(extras);
        startActivity(intent);
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    private void dataBinding() {
        whiteNotificationBar(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        adapter = new FilterColumnRecyclerAdapter(this, filterColumnList, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

        getSupportActionBar().setTitle(R.string.title_filteration_activity);

        setContentView(R.layout.activity_filteration);

        recyclerView = findViewById(R.id.rv_selected_column_data);

        pref = getSharedPreferences("user", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("user", "");

        if (json == "") {
            return;
        }

        User us = gson.fromJson(json, User.class);

        if (us.getIs_admin() == 1) {
            fileId = "";
        }
        else{
            filterOption = us.getFilter_option();
            if(filterOption.equals("Village")) fileId = us.getVillage();
            else fileId = String.valueOf(us.getStat_block_code());
        }

        helper = new DatabaseHelper(this);

        Spinner spinner = findViewById(R.id.sp_column_names);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String columnName = String.valueOf(parent.getSelectedItem());

                filterColumnList = helper.getListFilterColumn(columnName, fileId, filterOption);
                dataBinding();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // white background notification bar

        filterColumnList = helper.getListFilterColumn("", fileId, filterOption);
        dataBinding();

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
    public void onFilterColumnSelected(FilterColumn filterColumn) {
    }

    //endregion
}
