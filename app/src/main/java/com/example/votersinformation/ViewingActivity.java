package com.example.votersinformation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.votersinformation.Code.DataObjects.Voter;
import com.example.votersinformation.Code.Database.DatabaseHelper;

public class ViewingActivity extends AppCompatActivity {

    //region field(s)

    private Voter voterInfo;
    private DatabaseHelper helper;

    //endregion

    //region method(s)

    public void update_voter_detail(View v) {
        int voterId = v.getId();
        startActivity(new Intent(this, UpdationActivity.class).putExtra("voter_id", voterId));
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    //endregion

    //region override(s)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.title_viewing_activity);

        setContentView(R.layout.activity_viewing);

        Bundle extras = super.getIntent().getExtras();
        int voterId = extras.getInt("voter_id");
        boolean updateStatus = extras.getBoolean("update_status");

        if (updateStatus) {
            Toast.makeText(this, "Record updated successfully", Toast.LENGTH_SHORT).show();
        }

        helper = new DatabaseHelper(this);
        voterInfo = helper.getVoterInfo(voterId);

        TextView tvInfoSerialNo = findViewById(R.id.tv_voter_serial_no);
        TextView tvInfoGharanaNo = findViewById(R.id.tv_voter_gharana_no);
        TextView tvInfoPS = findViewById(R.id.tv_voter_ps);
        TextView tvInfoGender = findViewById(R.id.tv_voter_gender);

        TextView tvInfoName = findViewById(R.id.tv_voter_name);
        TextView tvInfoFatherName = findViewById(R.id.tv_voter_father_name);
        TextView tvInfoCNIC = findViewById(R.id.tv_voter_cnic);
        TextView tvInfoAge = findViewById(R.id.tv_voter_age);
        TextView tvInfoHouseNo = findViewById(R.id.tv_voter_house_no);
        TextView tvInfoVillage = findViewById(R.id.tv_voter_village);
        TextView tvInfoPresentLoc = findViewById(R.id.tv_voter_present_location);
        TextView tvInfoPoliticalPerson = findViewById(R.id.tv_voter_political_person);
        TextView tvInfoMobileNo = findViewById(R.id.tv_voter_mobile_no);
        TextView tvInfoStatBlockCode = findViewById(R.id.tv_voter_stat_block_code);
        TextView tvInfoStatus = findViewById(R.id.tv_voter_status);

        Button btnUpdate = findViewById(R.id.btn_update_voter);

        tvInfoSerialNo.setText(voterInfo.getSerial_no());
        tvInfoGharanaNo.setText(voterInfo.getGharan_no());
        tvInfoPS.setText(voterInfo.getPs());
        tvInfoGender.setText(voterInfo.getGender());

        tvInfoName.setText(voterInfo.getName());
        tvInfoFatherName.setText(voterInfo.getFather_name());
        tvInfoCNIC.setText(voterInfo.getCnic());
        tvInfoAge.setText(String.valueOf(voterInfo.getAge()));
        tvInfoHouseNo.setText(String.valueOf(voterInfo.getHouse_no()));

        tvInfoVillage.setText(voterInfo.getVillage());
        tvInfoPresentLoc.setText(voterInfo.getPresent_location());
        tvInfoPoliticalPerson.setText(voterInfo.getPolitical_person_name());
        tvInfoMobileNo.setText(voterInfo.getMobile_no());
        tvInfoStatBlockCode.setText(voterInfo.getStat_block_code_name());

        tvInfoStatus.setText(voterInfo.getAlive());
        btnUpdate.setId(voterInfo.getId());

    }

    //endregion
}
