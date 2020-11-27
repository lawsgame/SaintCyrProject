package com.lawsgame.saintcyr.model.models;

import com.lawsgame.saintcyr.model.Data;

public class Regiment implements Model {

    // primary stats
    private String name;
    private Data.UnitType type;
    private Data.CombatFormation formation;
    private float currentStrength;
    private float currentMoral;
    private float currentFatigue;
    private Officer regimentCommander;
    private Officer commandingOfficer;


    private Regiment(String name, Data.UnitType unitType){
        this.type = unitType;
        this.name = name;
        this.formation = Data.CombatFormation.LINE;
        this.currentStrength = 0;
        this.currentMoral = 0;
        this.currentFatigue = 0;
    }

    public static Regiment create(String name, Data.UnitType unitType){
        Regiment instance = new Regiment(name, unitType);
        instance.regimentCommander = Officer.getJohnDoe();
        instance.commandingOfficer = Officer.getJohnDoe();
        instance.rebuild();
        return instance;
    }

    public void rebuild(){
        this.currentMoral = getMoral();
        this.currentStrength = (int)type.getStrength();
    }



    //***** $$$ GETTERS & SETTERS $$$ *****

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Data.UnitType type(){ return type; }
    public void setType(Data.UnitType type){ this.type = type; }
    public Data.CombatFormation getFormation() { return formation; }
    public void setFormation(Data.CombatFormation formation) { this.formation = formation; }
    public Officer getRegimentCommander() { return regimentCommander; }
    public void setRegimentCommander(Officer regimentCommander) { this.regimentCommander = regimentCommander; }
    public Officer getCommandingOfficer() { return commandingOfficer; }
    public void setCommandingOfficer(Officer commandingOfficer) { this.commandingOfficer = commandingOfficer; }

    public float getCurrentStrength() {
        return currentStrength;
    }

    public void setCurrentStrength(float currentStrength) {
        if(currentStrength < 0){
            this.currentStrength = 0;
        }else if (currentStrength > type.getStrength()){
            this.currentStrength = type.getStrength();
        }else {
            this.currentStrength = currentStrength;
        }
    }

    public float getCurrentMoral() {
        return currentMoral;
    }

    public void setCurrentMoral(float currentMoral) {
        if(currentMoral < getMinMoral()){
            this.currentMoral = getMinMoral();
        }else if(currentMoral >  getMoral()){
            this.currentMoral = getMoral();
        }else {
            this.currentMoral = currentMoral;
        }
    }

    public float getCurrentFatigue() {
        return currentFatigue;
    }

    public void setCurrentFatigue(float currentFatigue) {
        this.currentFatigue = currentFatigue;
    }



    //***** $$$ BUILT STATS $$$ *****

    public float getMoral(){
        return Data.MORAL_BRA_FACTOR * type.getBravery() + Data.MORAL_CHA_FACTOR * commandingOfficer.getCharisma();
    }

    public float getDisciple(){
        return type.getBravery() + Data.DIS_AUTH_FACTOR + commandingOfficer.getAuthority();
    }

    public float getCombatSpeed(boolean current){
        float baseCS = type.getSpeed();
        float formationFactor = formation.getCombatSpeedFactor();
        return baseCS * formationFactor;
    }

    public float getEnergy(boolean current){
        return type.getEndurance();
    }

    public float getRangeHitRate(boolean current, boolean onCounterCharge) {
        float baseHR = type.getFireAbility()/100.0f;
        float charismaBonus = commandingOfficer.getCharisma() / 100.0f;
        float exhaustionFactor = 1f;
        float ccFactor = 1f;
        if(onCounterCharge){
            ccFactor = Math.min(1.0f, getDisciple()/20f);
        }
        if(current) {
            exhaustionFactor = isExhausted() ? Data.HITRATE_EXHAUSTION_FACTOR : 1f;
        }
        return (baseHR + charismaBonus) * formation.getFireHitRateFactor() * ccFactor * exhaustionFactor ;
    }


    public float getRangeAvoid( boolean current){
        return formation.getRangeAvoid();
    }

    public float getRangeWoundRate(boolean current) { return type.getFirePower()/100.0f; }

    public float getMeleeHitRate(boolean current, boolean charge){
        float initialMeleeHR = (type.getMeleeAbility() + ((charge)? type.getCharge() : 0))/100.0f;
        float charismaBonus = commandingOfficer.getCharisma() / 100.0f;
        float exhaustionFactor = 1f;
        if(current) {
            exhaustionFactor = isExhausted() ? Data.HITRATE_EXHAUSTION_FACTOR : 1f;
        }
        return (initialMeleeHR + charismaBonus) * exhaustionFactor ;
    }

    public float getMeleeAvoid(boolean current, boolean chargedUpon){
        float baseAvoM = type.getMeleeAbility()/100.0f;
        float charismaBonus = commandingOfficer.getCharisma() / 100.0f;
        float counterChargeFactor = 1f;
        float exhaustionFactor = 1f;
        if(chargedUpon){
            counterChargeFactor = formation.getChargeAvoid();
        }
        if(current) {
            exhaustionFactor = isExhausted() ? Data.HITRATE_EXHAUSTION_FACTOR : 1f;
        }
        return (baseAvoM + charismaBonus) * exhaustionFactor * counterChargeFactor ;
    }

    public float getMeleeWoundRate(boolean current, boolean chargeOn){
        return (type.getMeleePower() + ((chargeOn)? type.getCharge() : 0))/100.0f;
    }

    public float getRangeArmorRate(boolean current){ return type.getArmor()/100.0f; }
    public float getMeleeArmorRate(boolean current){ return type.getArmor()/100.0f; }

    public int getFirePower(boolean current) { return (int) (((current) ? currentStrength : type.getStrength()) * getRangeHitRate(current, false) * getRangeWoundRate(current));}

    public int getMeleePower(boolean current, boolean chargeOn) {
        float str =  (current) ? currentStrength :  type.getStrength();
        return (int) (str * getMeleeHitRate(current, chargeOn) *getMeleeWoundRate(current, chargeOn));
    }

    public boolean hasRangeOptions(){ return type.getRange() != 0; }
    public boolean isShaken(){ return currentMoral <= 0;}
    public boolean isDemoralized() { return currentMoral == getMinMoral();}
    public boolean isExhausted() {return getEnergy(true) <= currentFatigue;}
    public float getMinMoral(){ return - getMoral() / 2f;}


    public String toLongString(){
        return String.format("%s [%s]\n\tHP:%s/%s\n\tMoral:%s/%s\n\tEnergy: %s/%s\n\tPower: %s - %s(%s)\n\tArmor: (%s/%s)\n\tLed by %s"
                , name
                , type.name()
                , (int) currentStrength
                , (int) type.getStrength()
                , (int) currentMoral
                , (int) getMoral()
                , (int) getEnergy(false)
                , (int) currentFatigue
                , getFirePower(false)
                , getMeleePower(false, false)
                , getMeleePower(false, true)
                , getRangeArmorRate(false)
                , getMeleeArmorRate(false)
                , regimentCommander.toString());
    }

    @Override
    public String toString() {
        return String.format("%s [%s] led by %s"
                , name
                , type.name()
                , regimentCommander.toString());
    }
}
