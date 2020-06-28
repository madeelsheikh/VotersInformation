package com.example.votersinformation.Code.DataObjects;

public class Voter {

    private int id;
    private String serial_no;
    private String gharan_no;
    private String ps;
    private String gender;
    private String name;
    private String father_name;
    private String cnic;
    private int age;
    private int house_no;
    private String village;
    private String present_location;
    private String political_person_name;
    private String mobile_no;
    private String stat_block_code_name;
    private String alive;

    public Voter(int id, String serial_no, String gharan_no, String ps, String gender, String name, String father_name, String cnic, int age, int house_no, String village, String present_location, String political_person_name, String mobile_no, String stat_block_code_name, String alive) {
        this.id = id;
        this.serial_no = serial_no;
        this.gharan_no = gharan_no;
        this.ps = ps;
        this.gender = gender;
        this.name = name;
        this.father_name = father_name;
        this.cnic = cnic;
        this.age = age;
        this.house_no = house_no;
        this.village = village;
        this.present_location = present_location;
        this.political_person_name = political_person_name;
        this.mobile_no = mobile_no;
        this.stat_block_code_name = stat_block_code_name;
        this.alive = alive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    public String getGharan_no() {
        return gharan_no;
    }

    public void setGharan_no(String gharan_no) {
        this.gharan_no = gharan_no;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFather_name() {
        return father_name;
    }

    public void setFather_name(String father_name) {
        this.father_name = father_name;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHouse_no() {
        return house_no;
    }

    public void setHouse_no(int house_no) {
        this.house_no = house_no;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getPresent_location() {
        return present_location;
    }

    public void setPresent_location(String present_location) {
        this.present_location = present_location;
    }

    public String getPolitical_person_name() {
        return political_person_name;
    }

    public void setPolitical_person_name(String political_person_name) {
        this.political_person_name = political_person_name;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getStat_block_code_name() {
        return stat_block_code_name;
    }

    public void setStat_block_code_name(String stat_block_code_name) {
        this.stat_block_code_name = stat_block_code_name;
    }

    public String getAlive() {
        return alive;
    }

    public void setAlive(String alive) {
        this.alive = alive;
    }
}
