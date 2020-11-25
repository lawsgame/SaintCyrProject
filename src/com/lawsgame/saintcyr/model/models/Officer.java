package com.lawsgame.saintcyr.model.models;

import com.lawsgame.saintcyr.model.Data;

import java.util.Arrays;
import java.util.List;

public class Officer implements Model{
    private String name;
    private int age;
    private Data.Rank rank;

    private float charisma;
    private float authority;
    private int commandment;

    private List<Data.Trait> traits;

    private static final Officer JOHN_DOE = create("no officer", 0, Data.Rank.COLONEL, 0,0,0);

    public static Officer create(String name, int age, Data.Rank rank, float charisma, float authority, int commandment, Data.Trait... traits) {
        Officer officer = new Officer();
        officer.name = name;
        officer.age = age;
        officer.rank = rank;
        officer.charisma = charisma;
        officer.authority = authority;
        officer.commandment = commandment;
        officer.traits = Arrays.asList(traits);
        return officer;
    }

    public static Officer getJohnDoe(){
        return JOHN_DOE;
    }

    @Override
    public void rebuild() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Data.Rank getRank() {
        return rank;
    }

    public void setRank(Data.Rank rank) {
        this.rank = rank;
    }

    public float getCharisma() {
        return charisma;
    }

    public void setCharisma(float charisma) {
        this.charisma = charisma;
    }

    public float getAuthority() {
        return authority;
    }

    public void setAuthority(float authority) {
        this.authority = authority;
    }

    public int getCommandment() {
        return commandment;
    }

    public void setCommandment(int commandment) {
        this.commandment = commandment;
    }

    public List<Data.Trait> getTraits() {
        return traits;
    }

    public void setTraits(List<Data.Trait> traits) {
        this.traits = traits;
    }

    public String toString(){
        return String.format("%s (%s) [%s]: %s/%s/%s", name, age, rank, (int)charisma, (int)authority, (int)commandment);
    }
}
