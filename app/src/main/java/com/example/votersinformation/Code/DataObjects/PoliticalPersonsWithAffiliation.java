package com.example.votersinformation.Code.DataObjects;

public class PoliticalPersonsWithAffiliation {

    private int id;
    private String name;
    private int alive;
    private int dead;

    public PoliticalPersonsWithAffiliation(int id, String name, int alive, int dead) {
        this.id = id;
        this.name = name;
        this.alive = alive;
        this.dead = dead;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAlive() {
        return alive;
    }

    public void setAlive(int alive) {
        this.alive = alive;
    }

    public int getDead() {
        return dead;
    }

    public void setDead(int dead) {
        this.dead = dead;
    }

}
