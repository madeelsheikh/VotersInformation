package com.example.votersinformation.Code.DataObjects;

public class User {

    private int user_id;
    private String email_address;
    private String user_name;
    private String password;
    private String editable_content;
    private String filter_option;
    private int stat_block_code;
    private String village;
    private int is_active;
    private int is_alive;
    private int is_admin;


    public User() {
    }

    public User(int user_id, String email_address, String user_name, String password, String editable_content, String filter_option, int stat_block_code, String village, int is_active, int is_alive, int is_admin) {
        this.user_id = user_id;
        this.email_address = email_address;
        this.user_name = user_name;
        this.password = password;
        this.editable_content = editable_content;
        this.filter_option = filter_option;
        this.stat_block_code = stat_block_code;
        this.village = village;
        this.is_active = is_active;
        this.is_alive = is_alive;
        this.is_admin = is_admin;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEditable_content() {
        return editable_content;
    }

    public void setEditable_content(String editable_content) {
        this.editable_content = editable_content;
    }

    public String getFilter_option() {
        return filter_option;
    }

    public void setFilter_option(String filter_option) {
        this.filter_option = filter_option;
    }

    public int getStat_block_code() {
        return stat_block_code;
    }

    public void setStat_block_code(int stat_block_code) {
        this.stat_block_code = stat_block_code;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public int getIs_alive() {
        return is_alive;
    }

    public void setIs_alive(int is_alive) {
        this.is_alive = is_alive;
    }

    public int getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(int is_admin) {
        this.is_admin = is_admin;
    }
}
