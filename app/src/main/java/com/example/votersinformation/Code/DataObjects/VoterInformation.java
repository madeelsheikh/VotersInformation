package com.example.votersinformation.Code.DataObjects;

public class VoterInformation {

    private int id;

    private String serial;
    private String gharana;
    private String ps;
    private String gender;

    private String name;
    private String father_name;

    private String cnic;
    private int age;
    private int house;

    private String village;
    private String present_location;
    private String political_person_name;

    private String sbc;

    public VoterInformation(int id, String serial, String gharana, String ps, String gender, String name, String father_name, String cnic, int age, int house, String village, String present_location, String political_person_name, String sbc) {
        this.id = id;
        this.serial = serial;
        this.gharana = gharana;
        this.ps = ps;
        this.gender = gender;
        this.name = name;
        this.father_name = father_name;
        this.cnic = cnic;
        this.age = age;
        this.house = house;
        this.village = village;
        this.present_location = present_location;
        this.political_person_name = political_person_name;
        this.sbc = sbc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getGharana() {
        return gharana;
    }

    public void setGharana(String gharana) {
        this.gharana = gharana;
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

    public int getHouse() {
        return house;
    }

    public void setHouse(int house) {
        this.house = house;
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

    public String getSbc() {
        return sbc;
    }

    public void setSbc(String sbc) {
        this.sbc = sbc;
    }
}
