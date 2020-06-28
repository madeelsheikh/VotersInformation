package com.example.votersinformation.Code.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.votersinformation.Code.DataObjects.FilterColumn;
import com.example.votersinformation.Code.DataObjects.PoliticalPersonsWithAffiliation;
import com.example.votersinformation.Code.DataObjects.User;
import com.example.votersinformation.Code.DataObjects.Voter;
import com.example.votersinformation.Code.DataObjects.VoterInformation;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class DatabaseHelper extends SQLiteOpenHelper {

    //region field(s)

    public static final String DBNAME = "voters-new.db";
    public static final String DBLOCATION = "/data/data/com.example.votersinformation/databases/";
    private Context mContext;
    private SQLiteDatabase mDatabase;

    //endregion

    //region constructor(s)

    public DatabaseHelper(Context context) {
        super(context, DBNAME, null, 1);
        this.mContext = context;
    }

    //endregion

    public void openDatabase() {
        String dbPath = mContext.getDatabasePath(DBNAME).getPath();
        if (mDatabase != null && mDatabase.isOpen()) {
            return;
        }
        mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void closeDatabase() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    //region User

    public User getUserToAuthenticate(String userName) {
        User us = null;

        openDatabase();
        Cursor cursor = mDatabase.rawQuery("select id, email, user_name, password, content_editable, filter_option, stat_block_code_id, village, is_active, is_alive, is_admin from users where user_name = ?", new String[]{userName});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            us = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7), cursor.getInt(8), cursor.getInt(9), cursor.getInt(10));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return us;
    }

    public User getUserById(int id) {
        User us = null;

        openDatabase();
        Cursor cursor = mDatabase.rawQuery("select id, email, user_name, password, content_editable, filter_option, stat_block_code_id, village, is_active, is_alive, is_admin from users where id = ?", new String[]{String.valueOf(id)});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            us = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7), cursor.getInt(8), cursor.getInt(9), cursor.getInt(10));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return us;
    }

    public ArrayList<User> getUsers(int status) {
        boolean res = false;
        ArrayList<User> users = new ArrayList<>();
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("select * from users where is_admin = 0 and is_alive = 1 and is_active=?", new String[]{String.valueOf(status)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            User us = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7), cursor.getInt(8), cursor.getInt(9), cursor.getInt(10));
            users.add(us);
            cursor.moveToNext();

        }
        cursor.close();
        closeDatabase();
        return users;
    }

    public boolean getUserWithUsername(String username) {
        boolean res = false;
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("select * from users where is_active = 1 and is_alive = 1 and user_name = ?", new String[]{username});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            res = true;
            break;
        }
        cursor.close();
        closeDatabase();
        return res;
    }

    public boolean getUserStatusById(int id) {
        boolean res = false;
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("select is_active from users where is_alive = 1 and id = ?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            int r = cursor.getInt(0);
            if (r == 0) res = false;
            else if (r == 1) res = true;
            break;

        }
        cursor.close();
        closeDatabase();
        return res;
    }

    public boolean setUserStatusById(int id, int status) {
        boolean res = false;
        openDatabase();
        mDatabase.execSQL("update users set is_active = ? where id = ?", new String[]{String.valueOf(status), String.valueOf(id)});
        closeDatabase();
        return res;
    }

    public boolean setUserEditableContentById(int id, String editableColumn, String filter_option, String statBlockCode, String village) {
        boolean res = false;

        int statBlockCodeId = 0;
        String sql = null;

        if (filter_option.equals("Stat Block Code")) {
            statBlockCodeId = getStatBlockCodeId(statBlockCode);
            try {
                openDatabase();
                mDatabase.execSQL("update users set content_editable = ?, stat_block_code_id = ?, filter_option = ? where id = ?", new String[]{editableColumn, String.valueOf(statBlockCodeId), filter_option, String.valueOf(id)});
                closeDatabase();
                res = true;
            } catch (Exception e) {

            }
        } else {
            try {
                openDatabase();
                mDatabase.execSQL("update users set content_editable = ?, village = ?, filter_option = ? where id = ?", new String[]{editableColumn, village, filter_option, String.valueOf(id)});
                closeDatabase();
                res = true;
            } catch (Exception e) {

            }
        }

        return res;
    }

    public boolean add_user(String username, String email, String content_editable, String stat_block_code, String filter_option, String village, String password, int is_admin) {

        if (getUserWithUsername(username)) {
            return false;
        }

        int stat_block_code_id = -1;

        if (filter_option.equals("Stat Block Code")) {
            stat_block_code_id = getStatBlockCodeId(stat_block_code);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());

        openDatabase();
        try {
            ContentValues keyValue = new ContentValues();
            keyValue.put("email", email);
            keyValue.put("user_name", username);
            keyValue.put("password", password);
            keyValue.put("content_editable", content_editable);
            keyValue.put("filter_option", filter_option);
            keyValue.put("stat_block_code_id", stat_block_code_id);
            keyValue.put("village", village);
            keyValue.put("is_admin", is_admin);
            keyValue.put("creation_date_time", date);
            keyValue.put("created_by", "System");

            mDatabase.insertOrThrow("users", null, keyValue);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //endregion

    //region Political Person

    public List<PoliticalPersonsWithAffiliation> getListPoliticalPersonWithAffiliationCount(String name, String filterOption) {
        PoliticalPersonsWithAffiliation politicalPersonWithAffiliation = null;
        List<PoliticalPersonsWithAffiliation> politicalPersonWithAffiliationList = new ArrayList<>();
        openDatabase();

        Cursor cursor = null;

        if (name.equals("")) {
            cursor = mDatabase.rawQuery("SELECT Id, Name, SUM( CASE WHEN [Voter Status] = 'Alive' THEN 1 ELSE 0 END ) as Alive," +
                    " SUM( CASE WHEN [Voter Status] = 'Dead' THEN 1 ELSE 0 END ) as Dead" +
                    " from" +
                    " (select political_person.id as 'Id', political_person.name as 'Name', case voter.is_alive When 1 Then 'Alive' Else 'Dead' end 'Voter Status'" +
                    " from voter" +
                    " join political_person on voter.political_person_id = political_person.id)" +
                    " group by Name", null);
        } else {

            if (filterOption.equals("Stat Block Code")) {

                cursor = mDatabase.rawQuery("SELECT Id, Name, SUM( CASE WHEN [Voter Status] = 'Alive' THEN 1 ELSE 0 END ) as Alive," +
                        " SUM( CASE WHEN [Voter Status] = 'Dead' THEN 1 ELSE 0 END ) as Dead" +
                        " from" +
                        " (select political_person.id as 'Id', political_person.name as 'Name', case voter.is_alive When 1 Then 'Alive' Else 'Dead' end 'Voter Status'" +
                        " from voter" +
                        " join political_person on voter.political_person_id = political_person.id" +
                        " join stat_block_code on voter.stat_block_code_id = stat_block_code.id where stat_block_code.id = ?)" +
                        " group by Name", new String[]{name});
            } else {
                cursor = mDatabase.rawQuery("SELECT Id, Name, SUM( CASE WHEN [Voter Status] = 'Alive' THEN 1 ELSE 0 END ) as Alive," +
                        " SUM( CASE WHEN [Voter Status] = 'Dead' THEN 1 ELSE 0 END ) as Dead" +
                        " from" +
                        " (select political_person.id as 'Id', political_person.name as 'Name', case voter.is_alive When 1 Then 'Alive' Else 'Dead' end 'Voter Status'" +
                        " from voter" +
                        " join political_person on voter.political_person_id = political_person.id" +
                        " where village = ?)" +
                        " group by Name", new String[]{name});
            }
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            politicalPersonWithAffiliation = new PoliticalPersonsWithAffiliation(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3));
            politicalPersonWithAffiliationList.add(politicalPersonWithAffiliation);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return politicalPersonWithAffiliationList;
    }

    public ArrayList<VoterInformation> getListVoterInformationWithPoliticalPersonFilter(int politicalPersonId, String filterOption, String name) {
        VoterInformation voterInfo = null;
        ArrayList<VoterInformation> voterInfoList = new ArrayList<>();
        Cursor cursor = null;

        openDatabase();

        if (filterOption.equals("") && name.equals(""))
            cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where political_person_id = ?", new String[]{String.valueOf(politicalPersonId)});
        else if (filterOption.equals("Stat Block Code"))
            cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where political_person_id = ? and stat_block_code_id = ?", new String[]{String.valueOf(politicalPersonId), name});
        else
            cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where political_person_id = ? and village = ?", new String[]{String.valueOf(politicalPersonId), name});


        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            voterInfo = new VoterInformation(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getInt(8), cursor.getInt(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13));
            voterInfoList.add(voterInfo);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return voterInfoList;
    }

    private int getPoliticalPersonId(String name) {

        int politicalPersonId = 0;
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("select id from political_person where name = ?", new String[]{name});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            politicalPersonId = cursor.getInt(0);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return politicalPersonId;
    }

    public boolean add_political_person(String political_person_name) {

        openDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());

        try {
            ContentValues keyValue = new ContentValues();
            keyValue.put("name", political_person_name);
            keyValue.put("creation_date_time", date);
            keyValue.put("created_by", "System");

            mDatabase.insertOrThrow("political_person", null, keyValue);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<String> getListPoliticalPerson(String option, String fileId) {

        ArrayList<String> politicalPersonArrayList = new ArrayList<>();

        openDatabase();
        Cursor cursor = null;
        if (fileId.equals("") && option.equals(""))
            cursor = mDatabase.rawQuery("select distinct(name) from political_person order by name asc", null);
        else if (option.equals("Stat Block Code"))
            cursor = mDatabase.rawQuery("select distinct(name) from political_person where id in (select political_person_id from voter where stat_block_code_id = ?) order by name asc", new String[]{fileId});
        else
            cursor = mDatabase.rawQuery("select distinct(name) from political_person where id in (select political_person_id from voter where village = ?) order by name asc", new String[]{fileId});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(0);
            politicalPersonArrayList.add(name);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return politicalPersonArrayList;
    }

    //endregion

    //region Voter

    public ArrayList<VoterInformation> getListVoterInformation(String name, String filterOption) {
        VoterInformation voterInfo = null;
        ArrayList<VoterInformation> voterInfoList = new ArrayList<>();

        openDatabase();

        Cursor cursor = null;

        if (name.equals("")) {
            cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name, cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter", null);
        } else {
            if (filterOption.equals("Stat Block Code"))
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where stat_block_code_id = ?", new String[]{name});
            else
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where village = ?", new String[]{name});
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            voterInfo = new VoterInformation(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getInt(8), cursor.getInt(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13));
            voterInfoList.add(voterInfo);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return voterInfoList;
    }

    public ArrayList<VoterInformation> getListVoterInformationWithFilter(String columnName, int id, String filterOption, String name) {
        VoterInformation voterInfo = null;
        ArrayList<VoterInformation> voterInfoList = new ArrayList<>();
        Cursor cursor = null;
        openDatabase();

        if (filterOption.equals("") && name.equals("")) {

            if (columnName.equals("S.No")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where serial_no = (select serial_no from voter where id =?)", new String[]{String.valueOf(id)});
            }

            if (columnName.equals("G.No")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where gharana_no = (select gharana_no from voter where id =?)", new String[]{String.valueOf(id)});
            }

            if (columnName.equals("PS")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where ps = (select ps from voter where id =?)", new String[]{String.valueOf(id)});
            }

            if (columnName.equals("Gender")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where gender = (select gender from voter where id =?)", new String[]{String.valueOf(id)});
            }

            if (columnName.equals("Name")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where name = (select name from voter where id =?)", new String[]{String.valueOf(id)});
            }

            if (columnName.equals("Father Name")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where father_name = (select father_name from voter where id =?)", new String[]{String.valueOf(id)});
            }

            if (columnName.equals("CNIC")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where cnic = (select cnic from voter where id =?)", new String[]{String.valueOf(id)});
            }

            if (columnName.equals("Village")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where village = (select village from voter where id =?)", new String[]{String.valueOf(id)});
            }

            if (columnName.equals("Present Location")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where present_location = (select present_location from voter where id =?)", new String[]{String.valueOf(id)});
            }

            if (columnName.equals("Political Person")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where political_person_id = (select political_person_id from voter where id =?)", new String[]{String.valueOf(id)});
            }

            if (columnName.equals("Stat Block Code")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where stat_block_code_id = (select stat_block_code_id from voter where id =?)", new String[]{String.valueOf(id)});
            }
        } else if (filterOption.equals("Stat Block Code")) {
            if (columnName.equals("S.No")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where serial_no = (select serial_no from voter where id =?) and stat_block_code_id = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("G.No")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where gharana_no = (select gharana_no from voter where id =?) and stat_block_code_id = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("PS")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where ps = (select ps from voter where id =?) and stat_block_code_id = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("Gender")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where gender = (select gender from voter where id =?) and stat_block_code_id = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("Name")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where name = (select name from voter where id =?) and stat_block_code_id = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("Father Name")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where father_name = (select father_name from voter where id =?) and stat_block_code_id = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("CNIC")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where cnic = (select cnic from voter where id =?) and stat_block_code_id = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("Village")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where village = (select village from voter where id =?) and stat_block_code_id = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("Present Location")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where present_location = (select present_location from voter where id =?) and stat_block_code_id = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("Political Person")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where political_person_id = (select political_person_id from voter where id =?) and stat_block_code_id = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("Stat Block Code")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where stat_block_code_id = (select stat_block_code_id from voter where id =?) and stat_block_code_id = ?", new String[]{String.valueOf(id), name});
            }
        } else {
            if (columnName.equals("S.No")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where serial_no = (select serial_no from voter where id =?) and village = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("G.No")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where gharana_no = (select gharana_no from voter where id =?) and village = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("PS")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where ps = (select ps from voter where id =?) and village = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("Gender")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where gender = (select gender from voter where id =?) and village = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("Name")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where name = (select name from voter where id =?) and village = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("Father Name")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where father_name = (select father_name from voter where id =?) and village = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("CNIC")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where cnic = (select cnic from voter where id =?) and village = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("Village")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where village = (select village from voter where id =?) and village = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("Present Location")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where present_location = (select present_location from voter where id =?) and village = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("Political Person")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where political_person_id = (select political_person_id from voter where id =?) and village = ?", new String[]{String.valueOf(id), name});
            }

            if (columnName.equals("Stat Block Code")) {
                cursor = mDatabase.rawQuery("SELECT id, serial_no, gharana_no, ps, gender, name, father_name,cnic, age, house_no, village, present_location, (select name from political_person where id = political_person_id) as 'affiliated_political_person', (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' FROM voter where stat_block_code_id = (select stat_block_code_id from voter where id =?) and village = ?", new String[]{String.valueOf(id), name});
            }
        }


        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            voterInfo = new VoterInformation(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getInt(8), cursor.getInt(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13));
            voterInfoList.add(voterInfo);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return voterInfoList;
    }

    public Voter getVoterInfo(int voterId) {
        Voter voter = null;
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("select id,serial_no,gharana_no,ps,gender,name,father_name,cnic,age,house_no,village,present_location,(select name from political_person where id = political_person_id) as 'political_person_name',mobile_no,(select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code_name',case when is_alive = 1 then 'Alive' else 'Dead' end as status from voter where id = ?", new String[]{(String.valueOf(voterId))});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            voter = new Voter(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getInt(8), cursor.getInt(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return voter;
    }

    public boolean add_voter(String serial_no, String gharana_no, String ps, String gender, String voter_name, String father_name, String cnic, String age, String house_no, String village, String present_location, String political_person_name, String mobile_no, String stat_block_code_name) {

        int politicalPersonId = getPoliticalPersonId(political_person_name);

        if (politicalPersonId == 0) {
            add_political_person(political_person_name);
            politicalPersonId = getPoliticalPersonId(political_person_name);
        }

        int statBlockCodeId = getStatBlockCodeId(stat_block_code_name);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());

        openDatabase();
        try {
            ContentValues keyValue = new ContentValues();
            keyValue.put("serial_no", serial_no);
            keyValue.put("gharana_no", gharana_no);
            keyValue.put("ps", ps);
            keyValue.put("gender", gender);
            keyValue.put("name", voter_name);
            keyValue.put("father_name", father_name);
            keyValue.put("cnic", cnic);
            keyValue.put("age", age);
            keyValue.put("house_no", house_no);
            keyValue.put("village", village);
            keyValue.put("present_location", present_location);
            keyValue.put("political_person_id", politicalPersonId);
            keyValue.put("mobile_no", mobile_no);
            keyValue.put("stat_block_code_id", statBlockCodeId);
            keyValue.put("creation_date_time", date);
            keyValue.put("created_by", "System");

            mDatabase.insertOrThrow("voter", null, keyValue);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateVoter(String serialNo, String gharanaNo, String ps, String gender, String name, String fatherName, String cnic, int age, int houseNo, String village, String presentLoc, String politicalPerson, String mobileNo, String statBlockCode, int status, int voterId) {

        int politicalPersonId = 0;

        politicalPersonId = getPoliticalPersonId(politicalPerson);

        if (politicalPersonId == 0) {
            add_political_person(politicalPerson);
            politicalPersonId = getPoliticalPersonId(politicalPerson);
        }

        int statBlockCodeId = getStatBlockCodeId(statBlockCode);

        openDatabase();
        try {
            ContentValues keyValue = new ContentValues();
            keyValue.put("serial_no", serialNo);
            keyValue.put("gharana_no", gharanaNo);
            keyValue.put("ps", ps);
            keyValue.put("gender", gender);

            keyValue.put("name", name);
            keyValue.put("father_name", fatherName);
            keyValue.put("cnic", cnic);
            keyValue.put("age", age);
            keyValue.put("house_no", houseNo);
            keyValue.put("present_location", presentLoc);

            keyValue.put("political_person_id", politicalPersonId);
            keyValue.put("mobile_no", mobileNo);
            keyValue.put("stat_block_code_id", statBlockCodeId);

            keyValue.put("is_alive", status);
            keyValue.put("modification_date_time", "date('now')");
            keyValue.put("modified_by", "System");

            mDatabase.update("voter", keyValue, "id = ?", new String[]{String.valueOf(voterId)});
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteVoter(int voterId) {

        openDatabase();
        try {

            mDatabase.delete("voter", "id = ?", new String[]{String.valueOf(voterId)});
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    //endregion

    //region Stat Block Code

    public String getStatBlockCodeName(int id) {

        String statBlockCode = null;
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("select stat_block_code from stat_block_code where id = ? limit 1", new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            statBlockCode = cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return statBlockCode;
    }

    private int getStatBlockCodeId(String name) {

        int statBlockCodeId = 0;
        openDatabase();
        Cursor cursor = mDatabase.rawQuery("select id from stat_block_code where stat_block_code = ?", new String[]{name});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            statBlockCodeId = cursor.getInt(0);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return statBlockCodeId;
    }

    public ArrayList<String> getListStatBlockCode(String fileId) {

        ArrayList<String> statBlockCodeArrayList = new ArrayList<>();

        openDatabase();
        Cursor cursor = null;

        if (fileId.equals(""))
            cursor = mDatabase.rawQuery("select distinct(stat_block_code) from stat_block_code where is_active = 1 order by stat_block_code asc", null);
        else
            cursor = mDatabase.rawQuery("select distinct(stat_block_code) from stat_block_code where is_active = 1 and id = ? order by stat_block_code asc", new String[]{fileId});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(0);
            statBlockCodeArrayList.add(name);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return statBlockCodeArrayList;
    }

    public ArrayList<String> getListStatBlockCodeUpdate(String option, String fileId) {

        ArrayList<String> statBlockCodeArrayList = new ArrayList<>();

        openDatabase();
        Cursor cursor = null;

        if (fileId.equals("") && option.equals(""))
            cursor = mDatabase.rawQuery("select distinct(stat_block_code) from stat_block_code where is_active = 1 order by stat_block_code asc", null);
        else if (option.equals("Stat Block Code"))
            cursor = mDatabase.rawQuery("select distinct(stat_block_code) from stat_block_code where is_active = 1 and id = ? order by stat_block_code asc", new String[]{fileId});
        else
            cursor = mDatabase.rawQuery("select distinct(stat_block_code) from stat_block_code where is_active = 1 and id in (select distinct(stat_block_code_id) from voter where village = ?) order by stat_block_code asc", new String[]{fileId});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(0);
            statBlockCodeArrayList.add(name);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return statBlockCodeArrayList;
    }

    public ArrayList<String> getListStatBlockCodeByVillage(String village) {

        ArrayList<String> statBlockCodeArrayList = new ArrayList<>();

        openDatabase();
        Cursor cursor = null;

        cursor = mDatabase.rawQuery("select distinct(select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code' from voter where village = ?  order by stat_block_code_id asc", new String[]{village});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(0);
            statBlockCodeArrayList.add(name);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return statBlockCodeArrayList;
    }

    public boolean add_region(String region_name) {

        openDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());

        try {
            ContentValues keyValue = new ContentValues();
            keyValue.put("stat_block_code", region_name);
            keyValue.put("creation_date_time", date);
            keyValue.put("created_by", "System");

            mDatabase.insertOrThrow("stat_block_code", null, keyValue);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //endregion

    //region Filters

    public List<FilterColumn> getListFilterColumn(String columnName, String file_id, String filterOption) {
        FilterColumn filterColumn = null;
        List<FilterColumn> filterColumnList = new ArrayList<>();

        Cursor cursor = null;
        openDatabase();

        if (file_id.equals("")) {

            if (columnName.equals("") || columnName.equals("S.No")) {
                cursor = mDatabase.rawQuery("select id, serial_no from voter group by serial_no order by 1 asc", null);
            }

            if (columnName.equals("G.No")) {
                cursor = mDatabase.rawQuery("select id, gharana_no from voter group by gharana_no order by 1 asc", null);
            }

            if (columnName.equals("PS")) {
                cursor = mDatabase.rawQuery("select id, ps from voter group by ps order by 1 asc", null);
            }

            if (columnName.equals("Gender")) {
                cursor = mDatabase.rawQuery("select id, gender from voter group by gender order by 1 asc", null);
            }

            if (columnName.equals("Name")) {
                cursor = mDatabase.rawQuery("select id, name from voter group by name order by 1 asc", null);
            }

            if (columnName.equals("Father Name")) {
                cursor = mDatabase.rawQuery("select id, father_name from voter group by father_name order by 1 asc", null);
            }

            if (columnName.equals("CNIC")) {
                cursor = mDatabase.rawQuery("select id, cnic from voter group by cnic order by 1 asc", null);
            }

            if (columnName.equals("Village")) {
                cursor = mDatabase.rawQuery("select id, village from voter group by village order by 1 asc", null);
            }

            if (columnName.equals("Present Location")) {
                cursor = mDatabase.rawQuery("select id, present_location from voter group by present_location order by 1 asc", null);
            }

            if (columnName.equals("Political Person")) {
                cursor = mDatabase.rawQuery("select id, (select name from political_person where id = political_person_id) as 'name' from voter group by political_person_id order by 1 asc", null);
            }

            if (columnName.equals("Stat Block Code")) {
                cursor = mDatabase.rawQuery("select id, (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code_name' from voter group by stat_block_code_id order by 1 asc", null);
            }
        } else {

            if (filterOption.equals("Stat Block Code")) {
                if (columnName.equals("") || columnName.equals("S.No")) {
                    cursor = mDatabase.rawQuery("select id, serial_no from voter where stat_block_code_id = ? group by serial_no order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("G.No")) {
                    cursor = mDatabase.rawQuery("select id, gharana_no from voter where stat_block_code_id = ? group by gharana_no order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("PS")) {
                    cursor = mDatabase.rawQuery("select id, ps from voter where stat_block_code_id = ? group by ps order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("Gender")) {
                    cursor = mDatabase.rawQuery("select id, gender from voter where stat_block_code_id = ? group by gender order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("Name")) {
                    cursor = mDatabase.rawQuery("select id, name from voter where stat_block_code_id = ? group by name order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("Father Name")) {
                    cursor = mDatabase.rawQuery("select id, father_name from voter where stat_block_code_id = ? group by father_name order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("CNIC")) {
                    cursor = mDatabase.rawQuery("select id, cnic from voter where stat_block_code_id = ? group by cnic order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("Village")) {
                    cursor = mDatabase.rawQuery("select id, village from voter where stat_block_code_id = ? group by village order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("Present Location")) {
                    cursor = mDatabase.rawQuery("select id, present_location from voter where stat_block_code_id = ? group by present_location order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("Political Person")) {
                    cursor = mDatabase.rawQuery("select id, (select name from political_person where id = political_person_id) as 'name' from voter where stat_block_code_id = ? group by political_person_id order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("Stat Block Code")) {
                    cursor = mDatabase.rawQuery("select id, (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code_name' from voter where stat_block_code_id = ? group by stat_block_code_id order by 1 asc", new String[]{file_id});
                }
            } else {
                if (columnName.equals("") || columnName.equals("S.No")) {
                    cursor = mDatabase.rawQuery("select id, serial_no from voter where village = ? group by serial_no order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("G.No")) {
                    cursor = mDatabase.rawQuery("select id, gharana_no from voter where village = ? group by gharana_no order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("PS")) {
                    cursor = mDatabase.rawQuery("select id, ps from voter where village = ? group by ps order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("Gender")) {
                    cursor = mDatabase.rawQuery("select id, gender from voter where village = ? group by gender order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("Name")) {
                    cursor = mDatabase.rawQuery("select id, name from voter where village = ? group by name order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("Father Name")) {
                    cursor = mDatabase.rawQuery("select id, father_name from voter where village = ? group by father_name order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("CNIC")) {
                    cursor = mDatabase.rawQuery("select id, cnic from voter where village = ? group by cnic order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("Village")) {
                    cursor = mDatabase.rawQuery("select id, village from voter where village = ? group by village order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("Present Location")) {
                    cursor = mDatabase.rawQuery("select id, present_location from voter where village = ? group by present_location order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("Political Person")) {
                    cursor = mDatabase.rawQuery("select id, (select name from political_person where id = political_person_id) as 'name' from voter where village = ? group by political_person_id order by 1 asc", new String[]{file_id});
                }
                if (columnName.equals("Stat Block Code")) {
                    cursor = mDatabase.rawQuery("select id, (select stat_block_code from stat_block_code where id = stat_block_code_id) as 'stat_block_code_name' from voter where village = ? group by stat_block_code_id order by 1 asc", new String[]{file_id});
                }
            }
        }


        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            filterColumn = new FilterColumn(cursor.getInt(0), cursor.getString(1));
            filterColumnList.add(filterColumn);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return filterColumnList;
    }

    //endregion

    // region Village

    public ArrayList<String> getListVillage(String villagename) {

        ArrayList<String> statBlockCodeArrayList = new ArrayList<>();

        openDatabase();
        Cursor cursor = null;

        if (villagename.equals(""))
            cursor = mDatabase.rawQuery("select distinct(village) from voter order by village asc", null);
        else
            cursor = mDatabase.rawQuery("select distinct(village) from voter where village = ? order by village asc", new String[]{villagename});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(0);
            statBlockCodeArrayList.add(name);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return statBlockCodeArrayList;
    }

    public ArrayList<String> getListPresentLocation(String villagename) {

        ArrayList<String> presentLocationArrayList = new ArrayList<>();

        openDatabase();
        Cursor cursor = null;

        cursor = mDatabase.rawQuery("select distinct(present_location) from voter order by present_location asc", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(0);
            presentLocationArrayList.add(name);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return presentLocationArrayList;
    }

    public ArrayList<String> getListPollingStation() {

        ArrayList<String> pollingStationArrayList = new ArrayList<>();

        openDatabase();
        Cursor cursor = null;

        cursor = mDatabase.rawQuery("select distinct(ps) from voter order by ps asc", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(0);
            pollingStationArrayList.add(name);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return pollingStationArrayList;
    }

    // endregion

    //region Export To Excel

    public boolean dataExport(File directory, String name) {

        openDatabase();

        Cursor c = null;
        c = mDatabase.rawQuery("select serial_no as 'Serial NO'," +
                "gharana_no as 'Gharana NO'," +
                "ps as 'PS'," +
                "gender as 'Gender'," +
                "name as 'Name'," +
                "father_name as 'Father Name'," +
                "cnic as 'National ID Card Number'," +
                "age as 'Age'," +
                "house_no as 'House Number'," +
                "village as 'Village'," +
                "present_location as 'Present Location'," +
                "mobile_no as 'Mobile Number'," +
                "CASE WHEN is_alive = 1 THEN 'Alive' ELSE 'Dead' END as 'Status'," +
                "(select name from political_person where id = political_person_id) as 'Political Person Name'," +
                "(select stat_block_code from stat_block_code where id = stat_block_code_id) as 'Stat Block Code' from voter", null);

        int row = 0;
        int col = 0;

        try {

            File file = new File(directory, name);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;

            try {
                workbook = Workbook.createWorkbook(file, wbSettings);
                //Excel sheet name. 0 represents first sheet
                WritableSheet sheet = workbook.createSheet("Voters List", 0);

                try {

                    sheet.addCell(new Label(0, 0, "Serial NO")); // column and row
                    sheet.addCell(new Label(1, 0, "Gharana NO"));
                    sheet.addCell(new Label(2, 0, "PS"));
                    sheet.addCell(new Label(3, 0, "Gender"));

                    sheet.addCell(new Label(4, 0, "Name"));
                    sheet.addCell(new Label(5, 0, "Father Name"));
                    sheet.addCell(new Label(6, 0, "CNIC"));
                    sheet.addCell(new Label(7, 0, "Age"));
                    sheet.addCell(new Label(8, 0, "House No."));
                    sheet.addCell(new Label(9, 0, "Village"));
                    sheet.addCell(new Label(10, 0, "Present Location"));
                    sheet.addCell(new Label(11, 0, "Mobile No."));
                    sheet.addCell(new Label(12, 0, "Status"));
                    sheet.addCell(new Label(13, 0, "Political Person"));
                    sheet.addCell(new Label(14, 0, "Stat Block Code"));

                    if (c.moveToFirst()) {
                        do {

                            String v_s_no = c.getString(c.getColumnIndex("Serial NO"));
                            String v_g_no = c.getString(c.getColumnIndex("Gharana NO"));
                            String v_ps = c.getString(c.getColumnIndex("PS"));
                            String v_gender = c.getString(c.getColumnIndex("Gender"));

                            String v_name = c.getString(c.getColumnIndex("Name"));
                            String v_f_name = c.getString(c.getColumnIndex("Father Name"));
                            String v_cnic = c.getString(c.getColumnIndex("National ID Card Number"));
                            String v_age = c.getString(c.getColumnIndex("Age"));
                            String v_h_no = c.getString(c.getColumnIndex("House Number"));
                            String v_village = c.getString(c.getColumnIndex("Village"));
                            String v_p_location = c.getString(c.getColumnIndex("Present Location"));
                            String v_m_no = c.getString(c.getColumnIndex("Mobile Number"));
                            String v_status = c.getString(c.getColumnIndex("Status"));
                            String v_p_person = c.getString(c.getColumnIndex("Political Person Name"));
                            String v_s_b_code = c.getString(c.getColumnIndex("Stat Block Code"));

                            int i = c.getPosition() + 1;

                            sheet.addCell(new Label(0, i, v_s_no));
                            sheet.addCell(new Label(1, i, v_g_no));
                            sheet.addCell(new Label(2, i, v_ps));
                            sheet.addCell(new Label(3, i, v_gender));

                            sheet.addCell(new Label(4, i, v_name));
                            sheet.addCell(new Label(5, i, v_f_name));
                            sheet.addCell(new Label(6, i, v_cnic));
                            sheet.addCell(new Label(7, i, v_age));
                            sheet.addCell(new Label(8, i, v_h_no));
                            sheet.addCell(new Label(9, i, v_village));
                            sheet.addCell(new Label(10, i, v_p_location));
                            sheet.addCell(new Label(11, i, v_m_no));
                            sheet.addCell(new Label(12, i, v_status));
                            sheet.addCell(new Label(13, i, v_p_person));
                            sheet.addCell(new Label(14, i, v_s_b_code));


                        } while (c.moveToNext());
                    }
                    //closing cursor
                    c.close();
                } catch (RowsExceededException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }

                try {
                    workbook.write();
                    workbook.close();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            closeDatabase();
            return false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //endregion

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}