package com.example.votersinformation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.votersinformation.Code.DataObjects.User;
import com.example.votersinformation.Code.Database.DatabaseHelper;
import com.google.gson.Gson;

import java.util.List;

public class ParameterActivity extends AppCompatActivity {

    //region field(s)

    private DatabaseHelper helper;
    private Spinner _isAdmin;
    private Spinner filterOption;
    private Spinner _statBlockCode;
    private Spinner _presentLocation;
    private Spinner _ps;
    private Spinner village;
    private Spinner politicalPerson;
    private SharedPreferences pref;
    private String fileId;
    private String option;

    //endregion

    //region method(s)

    public void addItemsOnPoliticalPersonSpinner(String option, String fileId) {
        politicalPerson = findViewById(R.id.sp_add_political_person);
        List<String> list = helper.getListPoliticalPerson(option, fileId);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        politicalPerson.setAdapter(dataAdapter);
    }

    public void addItemsOnStatBlockCodeSpinnerVoter(String fileId) {
        _statBlockCode = (Spinner) findViewById(R.id.sp_add_stat_block_code_v);
        List<String> list = helper.getListStatBlockCode(fileId);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _statBlockCode.setAdapter(dataAdapter);
    }

    public void addItemsOnStatBlockCodeSpinnerByVillage(String fileId) {
        _statBlockCode = (Spinner) findViewById(R.id.sp_add_stat_block_code_v);
        List<String> list = helper.getListStatBlockCodeByVillage(fileId);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _statBlockCode.setAdapter(dataAdapter);
    }

    public void addItemsOnStatBlockCodeSpinner(String fileId) {
        _statBlockCode = (Spinner) findViewById(R.id.sp_add_stat_block_code);
        List<String> list = helper.getListStatBlockCode(fileId);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _statBlockCode.setAdapter(dataAdapter);
    }

    public void addItemsOnVillageSpinner(String initializer, String fileId) {
        List<String> list = helper.getListVillage(fileId);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (initializer.equals("voter")) {
            village = findViewById(R.id.sp_add_village_v);
            village.setAdapter(dataAdapter);
        } else {
            _statBlockCode = findViewById(R.id.sp_add_stat_block_code);
            _statBlockCode.setAdapter(dataAdapter);
        }
    }

    public void addItemsOnPresentLocationSpinner(String fileId) {
        _presentLocation = (Spinner) findViewById(R.id.sp_add_present_location_v);
        List<String> list = helper.getListPresentLocation(fileId);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _presentLocation.setAdapter(dataAdapter);
    }

    public void addItemsOnPollingStationSpinner() {
        _ps = (Spinner) findViewById(R.id.sp_add_ps_v);
        List<String> list = helper.getListPollingStation();

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _ps.setAdapter(dataAdapter);
    }

    public void add_voter(View v) {

        Spinner spvoterps = findViewById(R.id.sp_add_ps_v);
        Spinner votergender = findViewById(R.id.et_add_gender);
        Spinner voterpoliticalperson = findViewById(R.id.sp_add_political_person);
        Spinner spvillage = findViewById(R.id.sp_add_village_v);
        Spinner sppresentlocation = findViewById(R.id.sp_add_present_location_v);
        Spinner statblockcode = findViewById(R.id.sp_add_stat_block_code_v);

        EditText voterserialno = findViewById(R.id.et_add_serial_no);
        EditText votergharanano = findViewById(R.id.et_add_gharana_no);
        EditText voterps = findViewById(R.id.et_add_ps);

        EditText votername = findViewById(R.id.et_add_name);
        EditText fathername = findViewById(R.id.et_add_father_name);
        EditText cnic = findViewById(R.id.et_add_cnic);
        EditText age = findViewById(R.id.et_add_age);
        EditText village = findViewById(R.id.et_add_village);

        EditText presentlocation = findViewById(R.id.et_add_present_loc);
        EditText politicalpersonname = findViewById(R.id.et_add_political_person);
        EditText mobileno = findViewById(R.id.et_add_mobile);

        if (voterserialno == null ||
                votergharanano == null ||
                voterps == null ||
                votergender == null ||
                votername == null ||
                fathername == null ||
                cnic == null ||
                age == null ||
                politicalpersonname == null ||
                mobileno == null ||
                statblockcode == null ||
                spvoterps == null
        ) return;

        voterserialno.setError(null);
        votergharanano.setError(null);
        votername.setError(null);
        fathername.setError(null);
        cnic.setError(null);
        age.setError(null);

        if (voterserialno.getText().toString().equals("")) {
            voterserialno.setError("Serial number field should not be empty.");
            return;
        }
        if (votergharanano.getText().toString().equals("")) {
            votergharanano.setError("Gharana number field should not be empty.");
            return;
        }
        if (votername.getText().toString().equals("")) {
            votername.setError("Name field should not be empty.");
            return;
        }
        if (fathername.getText().toString().equals("")) {
            fathername.setError("Father name field should not be empty.");
            return;
        }
        if (cnic.getText().toString().equals("")) {
            cnic.setError("CNIC field should not be empty.");
            return;
        }
        if (age.getText().toString().equals("")) {
            age.setError("Age field should not be empty.");
            return;
        }

        /*
        if (String.valueOf(statblockcode.getSelectedItem()).equals("")) return;
        if (String.valueOf(sppresentlocation.getSelectedItem()).equals("")) return;
        if (String.valueOf(spvoterps.getSelectedItem()).equals("")) return;
        if (String.valueOf(spvillage.getSelectedItem()).equals("")) return;
        if (String.valueOf(votergender.getSelectedItem()).equals("")) return;
        if (String.valueOf(voterpoliticalperson.getSelectedItem()).equals("")) return;
        */

        String presLoc = presentlocation.getText().toString().equals("") ?
                String.valueOf(sppresentlocation.getSelectedItem()) :
                presentlocation.getText().toString();

        String pollingStation = voterps.getText().toString().equals("") ?
                String.valueOf(spvoterps.getSelectedItem()) :
                voterps.getText().toString();

        String villageName = village.getText().toString().equals("") ?
                String.valueOf(spvillage.getSelectedItem()) :
                village.getText().toString();

        String politicalPerson = politicalpersonname.getText().toString().equals("") ?
                String.valueOf(voterpoliticalperson.getSelectedItem()) :
                politicalpersonname.getText().toString();

        if (helper.add_voter(
                voterserialno.getText().toString(),
                votergharanano.getText().toString(),
                pollingStation,
                String.valueOf(votergender.getSelectedItem()),
                votername.getText().toString(),
                fathername.getText().toString(),
                cnic.getText().toString(),
                age.getText().toString(),
                "",
                villageName,
                presLoc,
                politicalPerson,
                mobileno.getText().toString(),
                String.valueOf(statblockcode.getSelectedItem()))) {
            Toast.makeText(this, "Voter added successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else
            Toast.makeText(this, "Something went wrong while adding voter. Please try later!", Toast.LENGTH_SHORT).show();

    }

    public void add_region(View v) {

        EditText regionName = findViewById(R.id.et_add_region_name);
        if (regionName == null) return;

        if (regionName.getText().toString().equals("")) {
            regionName.setError("Region name should not be empty.");
            return;
        }

        if (helper.add_region(
                regionName.getText().toString())) {
            Toast.makeText(this, "Region added successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else
            Toast.makeText(this, "Something went wrong while adding region. Please try later!", Toast.LENGTH_SHORT).show();

    }

    public void add_user(View v) {

        String editablecol = null;

        CheckBox cbserialno = findViewById(R.id.cb_serial_no);
        CheckBox cbgharanano = findViewById(R.id.cb_gharana_no);
        CheckBox cbps = findViewById(R.id.cb_ps);
        CheckBox cbgender = findViewById(R.id.cb_gender);
        CheckBox cbname = findViewById(R.id.cb_name);
        CheckBox cbfathername = findViewById(R.id.cb_father_name);
        CheckBox cbcnic = findViewById(R.id.cb_cnic);
        CheckBox cbage = findViewById(R.id.cb_age);
        CheckBox cbhouseno = findViewById(R.id.cb_house_no);
        CheckBox cbvillage = findViewById(R.id.cb_village);
        CheckBox cbpresentlocation = findViewById(R.id.cb_present_location);
        CheckBox cbpoliticalpersonname = findViewById(R.id.cb_political_person);
        CheckBox cbmobileno = findViewById(R.id.cb_mobile_no);
        CheckBox cbstatblockcode = findViewById(R.id.cb_stat_block_code);

        editablecol = cbserialno.isChecked() ? "serial_no," : "";
        editablecol += cbgharanano.isChecked() ? "gharana_no," : "";

        editablecol += cbps.isChecked() ? "ps," : "";
        editablecol += cbgender.isChecked() ? "gender," : "";
        editablecol += cbname.isChecked() ? "name," : "";
        editablecol += cbfathername.isChecked() ? "father_name," : "";
        editablecol += cbcnic.isChecked() ? "cnic," : "";
        editablecol += cbage.isChecked() ? "age," : "";
        editablecol += cbhouseno.isChecked() ? "house_no," : "";
        editablecol += cbvillage.isChecked() ? "village," : "";
        editablecol += cbpresentlocation.isChecked() ? "present_location," : "";

        editablecol += cbpoliticalpersonname.isChecked() ? "political_person," : "";
        editablecol += cbmobileno.isChecked() ? "mobile_no," : "";
        editablecol += cbstatblockcode.isChecked() ? "stat_block_code," : "";

        if (editablecol.length() > 0)
            editablecol = editablecol.substring(0, editablecol.length() - 1);

        EditText username = findViewById(R.id.et_add_username);
        EditText email = findViewById(R.id.et_add_email_address);
        EditText password = findViewById(R.id.et_add_password);
        Spinner is_admin = findViewById(R.id.sp_add_is_admin);
        Spinner filter_option = findViewById(R.id.sp_select_filter_option);
        Spinner stat_block_code = findViewById(R.id.sp_add_stat_block_code);

        username.setError(null);
        email.setError(null);
        password.setError(null);

        if (username == null || email == null || password == null || is_admin == null || filter_option == null || stat_block_code == null)
            return;

        if (username.getText().toString().equals("")) {
            username.setError("Username field should not be empty.");
            return;
        }
        if (email.getText().toString().equals("")) {
            email.setError("User email field should not be empty.");
            return;
        }
        if (password.getText().toString().equals("")) {
            password.setError("Password field should not be empty.");
            return;
        }
        if (password.getText().toString().length() < 5) {
            password.setError("Password length must be greater than 4 digits.");
            return;
        }
        if (String.valueOf(is_admin.getSelectedItem()).equals("")) return;
        if (String.valueOf(filter_option.getSelectedItem()).equals(""))
            return;
        if (String.valueOf(stat_block_code.getSelectedItem()).equals(""))
            return;

        String statBlockCode = null;
        String village = null;
        String filterOption;


        int status;
        if (is_admin.getSelectedItem().equals("Yes")) {
            status = 1;

            statBlockCode = "";
            village = "";
            filterOption = "";
        } else {
            status = 0;
            filterOption = String.valueOf(filter_option.getSelectedItem());
            if (filterOption.equals("Stat Block Code"))
                statBlockCode = String.valueOf(stat_block_code.getSelectedItem());
            else village = String.valueOf(stat_block_code.getSelectedItem());
        }


        if (!helper.add_user(
                username.getText().toString(),
                email.getText().toString(),
                editablecol,
                statBlockCode,
                filterOption,
                village,
                password.getText().toString(),
                status)) {
            Toast.makeText(this, "Something went wrong while adding user. Please try later!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    //endregion

    //region override

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.title_parameter_activity);

        setContentView(R.layout.activity_parameter);

        helper = new DatabaseHelper(this);

        pref = getSharedPreferences("user", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("user", "");

        if (json == "") {
            return;
        }

        User us = gson.fromJson(json, User.class);

        if (us.getIs_admin() == 1) {
            fileId = "";
            option = "";
        } else {
            option = us.getFilter_option();
            if (option.equals("Village")) fileId = us.getVillage();
            else fileId = String.valueOf(us.getStat_block_code());
        }

        // First check button click
        Intent intent = super.getIntent();

        if (intent.hasExtra("referrer")) {

            String pageName = intent.getExtras().getString("referrer");
            if (pageName.equals("Add User")) {
                addItemsOnStatBlockCodeSpinner(fileId);
                View v_user = findViewById(R.id.v_add_user);
                v_user.setVisibility(View.VISIBLE);

            } else if (pageName.equals("Add Region")) {
                View v_region = findViewById(R.id.v_add_region);
                v_region.setVisibility(View.VISIBLE);

            } else if (pageName.equals("Add Voter")) {
                addItemsOnPoliticalPersonSpinner(option, fileId);
                addItemsOnPresentLocationSpinner(fileId);
                addItemsOnPollingStationSpinner();

                addItemsOnVillageSpinner("voter", fileId);
                if ((fileId.equals("") && option.equals("")) || option.equals("Stat Block Code"))
                    addItemsOnStatBlockCodeSpinnerVoter(fileId);
                else addItemsOnStatBlockCodeSpinnerByVillage(fileId);

                View v_voter = findViewById(R.id.v_add_voter);
                v_voter.setVisibility(View.VISIBLE);

            } else if (pageName.equals("Add Political Person")) {
                View v_political_person = findViewById(R.id.v_add_political_person);
                v_political_person.setVisibility(View.VISIBLE);

            }
        }

        _isAdmin = findViewById(R.id.sp_add_is_admin);
        filterOption = findViewById(R.id.sp_select_filter_option);

        _isAdmin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(
                    AdapterView<?> parent, View view, int position, long id) {

                TextView tv_sbc_user = findViewById(R.id.tv_sbc_user);
                TextView tv_filter_option = findViewById(R.id.tv_filter_option);
                Spinner sp_add_stat_block_code = findViewById(R.id.sp_add_stat_block_code);
                Spinner sp_filter_option = findViewById(R.id.sp_select_filter_option);
                View content = findViewById(R.id.ll_content_editable_inner);

                String val = _isAdmin.getItemAtPosition(position).toString();

                if (val.equals("Yes")) {

                    tv_sbc_user.setVisibility(View.GONE);
                    tv_filter_option.setVisibility(View.GONE);
                    sp_add_stat_block_code.setVisibility(View.GONE);
                    sp_filter_option.setVisibility(View.GONE);
                    content.setVisibility(View.GONE);

                } else {

                    tv_sbc_user.setVisibility(View.VISIBLE);
                    tv_filter_option.setVisibility(View.VISIBLE);
                    sp_add_stat_block_code.setVisibility(View.VISIBLE);
                    sp_filter_option.setVisibility(View.VISIBLE);
                    content.setVisibility(View.VISIBLE);
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        filterOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(
                    AdapterView<?> parent, View view, int position, long id) {

                TextView tv_sbc_user = findViewById(R.id.tv_sbc_user);
                Spinner sp_add_stat_block_code = findViewById(R.id.sp_add_stat_block_code);

                String val = filterOption.getItemAtPosition(position).toString();

                if (val.equals("Stat Block Code")) {

                    tv_sbc_user.setText("Stat Block Code: ");
                    addItemsOnStatBlockCodeSpinner("");
                    tv_sbc_user.setVisibility(View.VISIBLE);
                    sp_add_stat_block_code.setVisibility(View.VISIBLE);


                } else {
                    tv_sbc_user.setText("Village: ");
                    addItemsOnVillageSpinner("", "");
                    tv_sbc_user.setVisibility(View.VISIBLE);
                    sp_add_stat_block_code.setVisibility(View.VISIBLE);
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    //endregion
}
