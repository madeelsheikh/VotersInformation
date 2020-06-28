package com.example.votersinformation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.votersinformation.Code.DataObjects.User;
import com.example.votersinformation.Code.DataObjects.Voter;
import com.example.votersinformation.Code.Database.DatabaseHelper;
import com.google.gson.Gson;

import java.util.List;

public class UpdationActivity extends AppCompatActivity {

    //region field(s)

    private Voter voter;
    private Spinner politicalPerson;
    private Spinner statBlockCode;
    private DatabaseHelper helper;
    private SharedPreferences pref;
    private String fileId;
    private String filterOption;

    //endregion

    //region method(s)

    public void addItemsOnPoliticalPersonSpinner(String name, String fileId, String option) {
        politicalPerson = findViewById(R.id.spUpdatePoliticalPerson);
        List<String> list = helper.getListPoliticalPerson(option, fileId);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        politicalPerson.setAdapter(dataAdapter);

        if (name != null) {
            int spinnerPosition = dataAdapter.getPosition(name);
            politicalPerson.setSelection(spinnerPosition);
        }
    }

    public void addItemsOnStatBlockCodeSpinnerVoter(String code, String fileId, String option) {
        statBlockCode = findViewById(R.id.spUpdateStatBlockCode);
        List<String> list = helper.getListStatBlockCodeUpdate(option, fileId);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statBlockCode.setAdapter(dataAdapter);

        if (code != null) {
            int spinnerPosition = dataAdapter.getPosition(code);
            statBlockCode.setSelection(spinnerPosition);
        }
    }

    public void update_voter_information(View v) {

        EditText etInfoSerialNo = findViewById(R.id.etUpdateSerialNo);
        EditText etInfoGharanaNo = findViewById(R.id.etUpdateGharanaNo);
        EditText etInfoPs = findViewById(R.id.etUpdatePs);
        EditText etInfoGender = findViewById(R.id.etUpdateGender);

        EditText etInfoName = findViewById(R.id.etUpdateName);
        EditText etInfoFatherName = findViewById(R.id.etUpdateFatherName);
        EditText etInfoCNIC = findViewById(R.id.etUpdateCNIC);
        EditText etInfoAge = findViewById(R.id.etUpdateAge);
        EditText etInfoHouseNo = findViewById(R.id.etUpdateHouseNo);
        EditText etInfoVillage = findViewById(R.id.etUpdateVillage);
        EditText etInfoPresentLoc = findViewById(R.id.etUpdatePresentLoc);
        Spinner spInfoPoliticalPerson = findViewById(R.id.spUpdatePoliticalPerson);
        EditText etInfoPoliticalPerson = findViewById(R.id.etUpdatePoliticalPersonMan);
        EditText etInfoMobileNo = findViewById(R.id.etUpdateMobile);
        Spinner spInfoStatBlockCode = findViewById(R.id.spUpdateStatBlockCode);
        Spinner spInfoStatus = findViewById(R.id.spUpdateStatus);

        int voterId = v.getId();

        if (
                etInfoSerialNo == null ||
                        etInfoGharanaNo == null ||
                        etInfoPs == null ||
                        etInfoGender == null ||
                        etInfoName == null ||
                        etInfoFatherName == null ||
                        etInfoCNIC == null ||
                        etInfoAge == null ||
                        etInfoHouseNo == null ||
                        etInfoVillage == null ||
                        etInfoPresentLoc == null ||
                        spInfoPoliticalPerson == null ||
                        etInfoPoliticalPerson == null ||
                        etInfoMobileNo == null ||
                        spInfoStatBlockCode == null ||
                        spInfoStatus == null
        ) return;

        if (etInfoSerialNo.getText().toString().equals("")) {
            etInfoSerialNo.setError("Serial number field should not be empty.");
            return;
        }
        /*
        if (etInfoGharanaNo.getText().toString().equals("")) {
            etInfoGharanaNo.setError("Gharana number field should not be empty.");
            return;
        }
        if (etInfoPs.getText().toString().equals("")) {
            etInfoPs.setError("PS field should not be empty.");
            return;
        }
        if (etInfoGender.getText().toString().equals("")) {
            etInfoGender.setError("Gender field should not be empty.");
            return;
        }
        */
        if (etInfoName.getText().toString().equals("")) {
            etInfoName.setError("Name field should not be empty.");
            return;
        }
        if (etInfoFatherName.getText().toString().equals("")) {
            etInfoFatherName.setError("Father name field should not be empty.");
            return;
        }
        /*
        if (etInfoCNIC.getText().toString().equals("")) {
            etInfoCNIC.setError("CNIC field should not be empty.");
            return;
        }
        if (etInfoAge.getText().toString().equals("")) {
            etInfoAge.setError("Age field should not be empty.");
            return;
        }
        if (etInfoHouseNo.getText().toString().equals("")) {
            etInfoHouseNo.setError("House number field should not be empty.");
            return;
        }
        if (etInfoVillage.getText().toString().equals("")) {
            etInfoVillage.setError("Village field should not be empty.");
            return;
        }
        if (etInfoPresentLoc.getText().toString().equals("")) {
            etInfoPresentLoc.setError("Present location field should not be empty.");
            return;
        }*/

        String manualPoliticalPersonName = etInfoPoliticalPerson.getText().toString();

        String ppName = String.valueOf(spInfoPoliticalPerson.getSelectedItem());

        if (!manualPoliticalPersonName.equals(""))
            ppName = etInfoPoliticalPerson.getText().toString();

        int status;
        if (spInfoStatus.getSelectedItem().equals("Alive")) {
            status = 1;
        } else {
            status = 0;
        }
        boolean res = helper.updateVoter(
                etInfoSerialNo.getText().toString(), etInfoGharanaNo.getText().toString(), etInfoPs.getText().toString(), etInfoGender.getText().toString(),
                etInfoName.getText().toString(), etInfoFatherName.getText().toString(), etInfoCNIC.getText().toString(),
                Integer.valueOf(etInfoAge.getText().toString()), Integer.valueOf(etInfoHouseNo.getText().toString()),
                etInfoVillage.getText().toString(), etInfoPresentLoc.getText().toString(), ppName,
                etInfoMobileNo.getText().toString(), String.valueOf(spInfoStatBlockCode.getSelectedItem()), status, voterId);

        if (res) {

            Toast.makeText(this, "Voter record updated successfully. To replicate data with other user please synchronize.", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "Something went wrong while updating voter record. Please try again!", Toast.LENGTH_LONG).show();
        }
        return;
    }

    public void delete_voter_information(View v) {

        int voterId = v.getId();

        boolean res = helper.deleteVoter(voterId);

        if (res) {

            Toast.makeText(this, "Voter deleted successfully. To replicate data with other user please synchronize.", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "Something went wrong while deleting voter record. Please try again!", Toast.LENGTH_LONG).show();
        }
        return;
    }

    //endregion

    //region override

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.title_updation_activity);

        setContentView(R.layout.activity_updation);

        helper = new DatabaseHelper(this);

        pref = getSharedPreferences("user", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("user", "");

        if (json == "") {
            return;
        }

        User us = gson.fromJson(json, User.class);

        filterOption = us.getFilter_option();

        if (filterOption != null) {
            if (filterOption.equals("Village")) fileId = us.getVillage();
            else fileId = String.valueOf(us.getStat_block_code());
        }

        if (us.getIs_admin() == 1) {
            fileId = "";
            filterOption = "";
        }

        int voterId = super.getIntent().getExtras().getInt("voter_id");

        voter = helper.getVoterInfo(voterId);

        EditText etInfoSerialNo = findViewById(R.id.etUpdateSerialNo);
        EditText etInfoGharanaNo = findViewById(R.id.etUpdateGharanaNo);
        EditText etInfoPs = findViewById(R.id.etUpdatePs);
        EditText etInfoGender = findViewById(R.id.etUpdateGender);

        EditText etInfoName = findViewById(R.id.etUpdateName);
        EditText etInfoFatherName = findViewById(R.id.etUpdateFatherName);
        EditText etInfoCNIC = findViewById(R.id.etUpdateCNIC);
        EditText etInfoAge = findViewById(R.id.etUpdateAge);
        EditText etInfoHouseNo = findViewById(R.id.etUpdateHouseNo);
        EditText etInfoVillage = findViewById(R.id.etUpdateVillage);
        EditText etInfoPresentLoc = findViewById(R.id.etUpdatePresentLoc);
        Spinner spInfoPoliticalPerson = findViewById(R.id.spUpdatePoliticalPerson);
        EditText etInfoMobileNo = findViewById(R.id.etUpdateMobile);
        Spinner spInfoStatBlockCode = findViewById(R.id.spUpdateStatBlockCode);
        Spinner spInfoStatus = findViewById(R.id.spUpdateStatus);

        Button btnUpdateRecord = findViewById(R.id.btn_update_record);
        Button btnDeleteRecord = findViewById(R.id.btn_delete_record);

        if (us.getEditable_content() != null) {
            String[] columnsSplitter = us.getEditable_content().split(",");

            etInfoSerialNo.setEnabled(false);
            etInfoGharanaNo.setEnabled(false);
            etInfoPs.setEnabled(false);
            etInfoGender.setEnabled(false);
            etInfoName.setEnabled(false);
            etInfoFatherName.setEnabled(false);
            etInfoCNIC.setEnabled(false);
            etInfoAge.setEnabled(false);
            etInfoHouseNo.setEnabled(false);
            etInfoVillage.setEnabled(false);
            etInfoPresentLoc.setEnabled(false);
            spInfoPoliticalPerson.setEnabled(false);
            etInfoMobileNo.setEnabled(false);
            spInfoStatBlockCode.setEnabled(false);

            for (int i = 0; i < columnsSplitter.length; i++) {

                if (columnsSplitter[i].contains("serial_no")) etInfoSerialNo.setEnabled(true);
                if (columnsSplitter[i].contains("gharana_no")) etInfoGharanaNo.setEnabled(true);
                if (columnsSplitter[i].contains("ps")) etInfoPs.setEnabled(true);
                if (columnsSplitter[i].contains("gender")) etInfoGender.setEnabled(true);

                if (columnsSplitter[i].contains("name")) etInfoName.setEnabled(true);
                if (columnsSplitter[i].contains("father_name")) etInfoFatherName.setEnabled(true);
                if (columnsSplitter[i].contains("cnic")) etInfoCNIC.setEnabled(true);
                if (columnsSplitter[i].contains("age")) etInfoAge.setEnabled(true);
                if (columnsSplitter[i].contains("house_no")) etInfoHouseNo.setEnabled(true);
                if (columnsSplitter[i].contains("village")) etInfoVillage.setEnabled(true);
                if (columnsSplitter[i].contains("present_location"))
                    etInfoPresentLoc.setEnabled(true);
                if (columnsSplitter[i].contains("political_person"))
                    spInfoPoliticalPerson.setEnabled(true);
                if (columnsSplitter[i].contains("mobile_no")) etInfoMobileNo.setEnabled(true);
                if (columnsSplitter[i].contains("stat_block_code"))
                    spInfoStatBlockCode.setEnabled(true);
            }
        }

        etInfoSerialNo.setText(voter.getSerial_no());
        etInfoGharanaNo.setText(voter.getGharan_no());
        etInfoPs.setText(voter.getPs());
        etInfoGender.setText(voter.getGender());

        etInfoName.setText(voter.getName());
        etInfoFatherName.setText(voter.getFather_name());
        etInfoCNIC.setText(voter.getCnic());
        etInfoAge.setText(String.valueOf(voter.getAge()));
        etInfoHouseNo.setText(String.valueOf(voter.getHouse_no()));
        etInfoVillage.setText(voter.getVillage());
        etInfoPresentLoc.setText(voter.getPresent_location());
        addItemsOnPoliticalPersonSpinner(voter.getPolitical_person_name(), fileId, filterOption);
        etInfoMobileNo.setText(voter.getMobile_no());
        addItemsOnStatBlockCodeSpinnerVoter(voter.getStat_block_code_name(), fileId, filterOption);
        btnUpdateRecord.setId(voter.getId());
        btnDeleteRecord.setId(voter.getId());
    }

    //endregion
}