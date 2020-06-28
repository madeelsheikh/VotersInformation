package com.example.votersinformation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.votersinformation.Code.DataObjects.User;
import com.example.votersinformation.Code.Database.DatabaseHelper;

import java.util.List;

public class UserMaintenanceActivity extends AppCompatActivity {

    private DatabaseHelper helper;
    private User _user;
    private Spinner statBlockCode;
    private Spinner filterOption;

    public void update_user_editable_content(View v) {
        String editablecol = null;

        CheckBox cbserialno = findViewById(R.id.cb_main_serial_no);
        CheckBox cbgharanano = findViewById(R.id.cb_main_gharana_no);
        CheckBox cbps = findViewById(R.id.cb_main_ps);
        CheckBox cbgender = findViewById(R.id.cb_main_gender);
        CheckBox cbname = findViewById(R.id.cb_main_name);
        CheckBox cbfathername = findViewById(R.id.cb_main_father_name);
        CheckBox cbcnic = findViewById(R.id.cb_main_cnic);
        CheckBox cbage = findViewById(R.id.cb_main_age);
        CheckBox cbhouseno = findViewById(R.id.cb_main_house_no);
        CheckBox cbvillage = findViewById(R.id.cb_main_village);
        CheckBox cbpresentlocation = findViewById(R.id.cb_main_present_location);
        CheckBox cbpoliticalpersonname = findViewById(R.id.cb_main_political_person);
        CheckBox cbmobileno = findViewById(R.id.cb_main_mobile_no);
        CheckBox cbstatblockcode = findViewById(R.id.cb_main_stat_block_code);

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

        Spinner filterOption = findViewById(R.id.sp_main_filter_option);

        String code = null;
        String village = null;
        String option = String.valueOf(filterOption.getSelectedItem());

        if (option.equals("Stat Block Code"))
            code = String.valueOf(statBlockCode.getSelectedItem());
        else village = String.valueOf(statBlockCode.getSelectedItem());

        int user_id = v.getId();

        CheckBox status = findViewById(R.id.s_main_user_status);
        boolean switchState = status.isChecked();

        boolean isActive = helper.getUserStatusById(user_id);

        try {

            if (switchState != isActive) {
                if (switchState) helper.setUserStatusById(user_id, 1);
                else helper.setUserStatusById(user_id, 0);
            }

            if (helper.setUserEditableContentById(user_id, editablecol, option, code, village)) {
                Toast.makeText(this, "User record update successfully.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void addItemsOnStatBlockCodeSpinner(String code, String fileId) {
        statBlockCode = (Spinner) findViewById(R.id.sp_main_stat_block_code);
        List<String> list = helper.getListStatBlockCode(fileId);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statBlockCode.setAdapter(dataAdapter);

        if (code != null) {
            int spinnerPosition = dataAdapter.getPosition(code);
            statBlockCode.setSelection(spinnerPosition);
        }

    }

    public void addItemsOnVillageSpinner(String code, String fileId) {
        statBlockCode = (Spinner) findViewById(R.id.sp_main_stat_block_code);
        List<String> list = helper.getListVillage(fileId);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statBlockCode.setAdapter(dataAdapter);

        if (code != null) {
            int spinnerPosition = dataAdapter.getPosition(code);
            statBlockCode.setSelection(spinnerPosition);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("User Maintenance");
        setContentView(R.layout.activity_user_maintenance);

        int userId = 0;
        Intent intent = super.getIntent();

        if (intent.hasExtra("user_id")) {
            userId = intent.getExtras().getInt("user_id");
        }

        helper = new DatabaseHelper(this);
        _user = helper.getUserById(userId);

        TextView userName = findViewById(R.id.tv_main_user_name);
        TextView emailAddress = findViewById(R.id.tv_main_email_address);
        CheckBox status = findViewById(R.id.s_main_user_status);

        CheckBox cbserialno = findViewById(R.id.cb_main_serial_no);
        CheckBox cbgharanano = findViewById(R.id.cb_main_gharana_no);
        CheckBox cbps = findViewById(R.id.cb_main_ps);
        CheckBox cbgender = findViewById(R.id.cb_main_gender);
        CheckBox cbname = findViewById(R.id.cb_main_name);
        CheckBox cbfathername = findViewById(R.id.cb_main_father_name);
        CheckBox cbcnic = findViewById(R.id.cb_main_cnic);
        CheckBox cbage = findViewById(R.id.cb_main_age);
        CheckBox cbhouseno = findViewById(R.id.cb_main_house_no);
        CheckBox cbvillage = findViewById(R.id.cb_main_village);
        CheckBox cbpresentlocation = findViewById(R.id.cb_main_present_location);
        CheckBox cbpoliticalpersonname = findViewById(R.id.cb_main_political_person);
        CheckBox cbmobileno = findViewById(R.id.cb_main_mobile_no);
        CheckBox cbstatblockcode = findViewById(R.id.cb_main_stat_block_code);

        Button btnupdateusereditablecontent = findViewById(R.id.btn_save_user_update_data);

        if (userName != null) {
            userName.setText(_user.getUser_name());
        }
        if (emailAddress != null) {
            emailAddress.setText(_user.getEmail_address());
        }

        boolean switchIsChecked = false;
        if (_user.getIs_active() == 1)
            switchIsChecked = true;

        status.setChecked(switchIsChecked);

        if (_user.getEditable_content() != null) {
            String[] columnsSplitter = _user.getEditable_content().split(",");

            for (int i = 0; i < columnsSplitter.length; i++) {

                if (columnsSplitter[i].contains("serial_no")) cbserialno.setChecked(true);
                if (columnsSplitter[i].contains("gharana_no")) cbgharanano.setChecked(true);
                if (columnsSplitter[i].contains("ps")) cbps.setChecked(true);
                if (columnsSplitter[i].contains("gender")) cbgender.setChecked(true);

                if (columnsSplitter[i].contains("name")) cbname.setChecked(true);
                if (columnsSplitter[i].contains("father_name")) cbfathername.setChecked(true);
                if (columnsSplitter[i].contains("cnic")) cbcnic.setChecked(true);
                if (columnsSplitter[i].contains("age")) cbage.setChecked(true);
                if (columnsSplitter[i].contains("house_no")) cbhouseno.setChecked(true);
                if (columnsSplitter[i].contains("village")) cbvillage.setChecked(true);
                if (columnsSplitter[i].contains("present_location"))
                    cbpresentlocation.setChecked(true);
                if (columnsSplitter[i].contains("political_person"))
                    cbpoliticalpersonname.setChecked(true);
                if (columnsSplitter[i].contains("mobile_no")) cbmobileno.setChecked(true);
                if (columnsSplitter[i].contains("stat_block_code"))
                    cbstatblockcode.setChecked(true);
            }
        }

        String option = _user.getFilter_option();

        filterOption = findViewById(R.id.sp_main_filter_option);

        filterOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(
                    AdapterView<?> parent, View view, int position, long id) {

                String val = filterOption.getItemAtPosition(position).toString();

                if (val.equals("Stat Block Code")) {
                    addItemsOnStatBlockCodeSpinner(helper.getStatBlockCodeName(_user.getStat_block_code()), "");
                } else {
                    addItemsOnVillageSpinner(_user.getVillage(), "");
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (option != null && option.equals("Village")) {

            addItemsOnVillageSpinner(_user.getVillage(), "");
            filterOption.setSelection(((ArrayAdapter) filterOption.getAdapter()).getPosition("Village"));

        } else {

            addItemsOnStatBlockCodeSpinner(helper.getStatBlockCodeName(_user.getStat_block_code()), "");
            filterOption.setSelection(((ArrayAdapter) filterOption.getAdapter()).getPosition("Stat Block Code"));

        }

        if (btnupdateusereditablecontent != null) {
            btnupdateusereditablecontent.setId(_user.getUser_id());
        }

    }
}
