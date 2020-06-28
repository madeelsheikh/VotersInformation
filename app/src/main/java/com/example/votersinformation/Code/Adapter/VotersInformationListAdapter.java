package com.example.votersinformation.Code.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.votersinformation.Code.DataObjects.VoterInformation;
import com.example.votersinformation.R;

import java.util.ArrayList;

public class VotersInformationListAdapter extends ArrayAdapter<VoterInformation> {

    //region field(s)

    private LayoutInflater mInflater;
    private ArrayList<VoterInformation> voterInfo;
    private int mViewResourceId;

    //endregion

    //region constructor(s)

    public VotersInformationListAdapter(Context context, int textViewResourceId, ArrayList<VoterInformation> voterInfo) {
        super(context,textViewResourceId,voterInfo);
        this.voterInfo = voterInfo;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = textViewResourceId;
    }

    //endregion

    //region method(s)

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.i_lv_v,null);

        VoterInformation votInfo = voterInfo.get(position);

        if (votInfo != null) {
            /*TextView serial =  convertView.findViewById(R.id.tv_serial_no);
            TextView gharana =  convertView.findViewById(R.id.tv_gharana_no);
            TextView ps =  convertView.findViewById(R.id.tv_ps);
            TextView gender =  convertView.findViewById(R.id.tv_gender);*/

            TextView name =  convertView.findViewById(R.id.tv_name);
            TextView fatherName =  convertView.findViewById(R.id.tv_father_name);

            /*TextView cnic =  convertView.findViewById(R.id.tv_cnic);
            TextView age =  convertView.findViewById(R.id.tv_age);
            TextView house =  convertView.findViewById(R.id.tv_house_no);*/

            TextView village =  convertView.findViewById(R.id.tv_village);
            TextView present_location =  convertView.findViewById(R.id.tv_present_location);
            TextView politicalPerson =  convertView.findViewById(R.id.tv_affiliated_person);

            /*TextView sbc =  convertView.findViewById(R.id.tv_stat_block_code);*/

            Button btnView = convertView.findViewById(R.id.btn_view_detail);

            /*if (serial != null) {
                serial.setText(votInfo.getSerial());
            }
            if (gharana != null) {
                gharana.setText(votInfo.getGharana());
            }
            if (ps != null) {
                ps.setText(votInfo.getPs());
            }
            if (gender != null) {
                gender.setText(votInfo.getGender());
            }*/
            if (name != null) {
                name.setText(votInfo.getName());
            }
            if (fatherName != null) {
                fatherName.setText(votInfo.getFather_name());
            }
            /*if (cnic != null) {
                cnic.setText(votInfo.getCnic());
            }
            if (age != null) {
                age.setText(String.valueOf(votInfo.getAge()));
            }
            if (house != null) {
                house.setText(String.valueOf(votInfo.getHouse()));
            }*/
            if (village != null) {
                village.setText(votInfo.getVillage());
            }
            if (present_location != null) {
                present_location.setText(votInfo.getPresent_location());
            }
            if (politicalPerson != null) {
                politicalPerson.setText(votInfo.getPolitical_person_name());
            }
            /*if (sbc != null) {
                sbc.setText(votInfo.getSbc());
            }*/

            if(btnView != null)
            {
                btnView.setId(votInfo.getId());
            }
        }

        return convertView;
    }

    //endregion
}
