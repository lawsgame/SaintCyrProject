package com.lawsgame.saintcyr.model.models;

import com.lawsgame.saintcyr.model.Data;

import java.util.Arrays;

public class Regiment implements Model {

    private static Data.CombatFormation DEFAULT_FORMATION = Data.CombatFormation.COLUMN;

    // primary stats
    private String name;
    private Data.UnitRank rank;
    private Data.UnitType type;
    private Data.CombatFormation formation;
    private float currentStrength;
    private float currentMoral;
    private float currentEnergy;
    private Officer regimentCommander;
    private Officer commandingOfficer;


    private Regiment(String name, Data.UnitType unitType){
        this.type = unitType;
        this.name = name;
        this.setFormation(DEFAULT_FORMATION);
        this.setRank(Data.UnitRank.REGIMENT);
        this.currentStrength = 0;
        this.currentMoral = 0;
        this.currentEnergy = 0;
        this.regimentCommander = Officer.getJohnDoe();
        this.commandingOfficer = Officer.getJohnDoe();
    }

    public static Regiment create(String name, Data.UnitType unitType){
        Regiment instance = new Regiment(name, unitType);
        instance.rebuild();
        return instance;
    }

    public void rebuild(){
        this.currentMoral = getMoral();
        this.currentEnergy = getEnergy();
        this.currentStrength = (int)type.getStrength();
    }

    //***** $$$ GETTERS & SETTERS $$$ *****

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Data.UnitRank getRank() { return rank; }
    public void setRank(Data.UnitRank rank) { this.rank = rank; }
    public Data.UnitType type(){ return type; }
    public void setType(Data.UnitType type){ this.type = type; }
    public Data.CombatFormation getFormation() { return formation; }
    public void setFormation(Data.CombatFormation formation) { this.formation = formation; }
    public Data.UnitType getType() { return type; }
    public Officer getRegimentCommander() { return regimentCommander; }
    public void setRegimentCommander(Officer regimentCommander) { this.regimentCommander = regimentCommander; }
    public Officer getCommandingOfficer() { return commandingOfficer; }
    public void setCommandingOfficer(Officer commandingOfficer) { this.commandingOfficer = commandingOfficer; }
    public float getCurrentEnergy() {
        return currentEnergy;
    }
    public void setCurrentEnergy(float currentEnergy) {
        this.currentEnergy = currentEnergy;
    }

    public float getCurrentStrength() {
        return currentStrength;
    }

    public void setCurrentStrength(float currentStrength) {
        if(currentStrength > type.getStrength()){
            this.currentStrength = type.getStrength();
        }else if(currentStrength < 0){
            this.currentStrength = 0;
        }else{
            this.currentStrength = currentStrength;
        }
    }

    public float getCurrentMoral() {
        return currentMoral;
    }

    public void setCurrentMoral(float currentMoral) {
        if(currentMoral > getMoral()){
            this.currentMoral = getMoral();
        }else {
            this.currentMoral = currentMoral;
        }
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
        float formationFactor = 1.0f;
        if(current){
            formationFactor = formation.getManeuvrability();
        }
        return baseCS * formationFactor;
    }

    public float getEnergy(){
        return type.getEndurance();
    }

    public float getRangeHitRate(boolean current, boolean counterCharge) {
        float baseHR = type.getFireAbility();
        float charismaBonus = commandingOfficer.getCharisma();
        float formationFactor = 1.0f;
        float ccFactor = 1f;
        if(counterCharge){
            ccFactor = Math.min(1.0f, getDisciple()/20f);
        }
        float exhaustionFactor = 1f;
        if(current) {
            formationFactor = formation.getFireFront();
            exhaustionFactor = isExhausted() ? Data.HITRATE_EXHAUSTION_FACTOR : 1f;
        }
        return (baseHR + charismaBonus) * formationFactor * ccFactor * exhaustionFactor / 100.0f;
    }


    public float getRangeVulnerability(boolean current){
        float densityFactor = 1f;
        float numerousFactor = (float) Math.log10(Math.sqrt(currentStrength)) - 1f;
        if(current){
            densityFactor = formation.getDensity() ;
        }
        return (densityFactor + numerousFactor);
    }

    public float getRangeWoundRate(boolean current, boolean counterCharge) {
        float firePower = type.getRangeWeapon().getFirepower()/100.0f;
        return firePower;
    }

    public float getMeleeHitRate(boolean current, boolean charge, Data.UnitArm oppArm){
        float initialMeleeHR = type.getMeleeAbility();
        float charismaBonus = commandingOfficer.getCharisma();
        float exhaustionFactor = 1f;
        if(current){
            exhaustionFactor = isExhausted() ? Data.HITRATE_EXHAUSTION_FACTOR : 1f;
        }
        return (initialMeleeHR + charismaBonus)  * exhaustionFactor  / 100.0f;
    }

    /**
     *
     * @param current
     * @param chargedUpon = {
     *               0: ongoing melee
     *               1: charge by infantry
     *               2: charge by cavalry
     * }
     * @return
     */
    public float getMeleeAvoid(boolean current, boolean chargedUpon, Data.UnitArm opponentArm){
        float baseAvoM = type.getMeleeAbility();
        float charismaBonus = commandingOfficer.getCharisma();
        float antiCavFormationBonus = 0f;
        float counterChargeBonus = 0f;
        if(chargedUpon){
            counterChargeBonus = (opponentArm == Data.UnitArm.INFANTRY) ?
                    type.getMeleeWeapon().getAntiChargeVSInfantry() :
                    type.getMeleeWeapon().getAntiChargeVSCavalry();
        }
        float exhaustionFactor = 1f;
        if(current){
            antiCavFormationBonus = formation.getAntiCav();
            exhaustionFactor = isExhausted() ? Data.HITRATE_EXHAUSTION_FACTOR : 1f;
        }
        return (baseAvoM + charismaBonus + antiCavFormationBonus + counterChargeBonus) * exhaustionFactor / 100.0f;
    }

    public float getMeleeWoundRate(boolean current, boolean charge, Data.UnitArm oppArm){
        float meleePower = type.getMeleeWeapon().getMeleePower();
        float chargeBonus = (charge) ? type.getCharge() : 0;
        if(charge){
            chargeBonus += (oppArm == Data.UnitArm.INFANTRY) ?
                    type.getMeleeWeapon().getChargeVSInfantry() :
                    type.getMeleeWeapon().getChargeVSCavalry();
        }
        return (meleePower + chargeBonus) /100.0f;
    }

    public float getRangeArmorRate(boolean current){ return type.getArmor()/100.0f; }
    public float getMeleeArmorRate(boolean current){ return type.getArmor()/100.0f; }

    public int getFirePower(boolean current, boolean counterCharge) {
        float str =  (current) ? currentStrength :  type.getStrength();
        float hitRate = getRangeHitRate(current, counterCharge);
        float woundRate = getRangeWoundRate(current, counterCharge);
        return (int) (str * hitRate * woundRate);
    }

    public int getMeleePower(boolean current, boolean charge, Data.UnitArm oppArm) {
        float str =  (current) ? currentStrength :  type.getStrength();
        float hitRate = getMeleeHitRate(current, charge, oppArm);
        float woundRate = getMeleeWoundRate(current, charge, oppArm);
        return (int) (str * hitRate * woundRate);
    }

    public boolean hasRangeOptions(){ return type.getRangeWeapon().isRanged(); }
    public boolean isShaken(){ return currentMoral <= 0;}
    public boolean isDemoralized() { return currentMoral == getMinMoral();}
    public boolean isExhausted() {return currentEnergy < 0;}
    public float getMinMoral(){ return - getMoral() / 2f;}

    public String toLongString(){
        return String.format("%s [%s]\n\tHP:%s/%s\n\tMoral:%s/%s\n\tEnergy: %s/%s\n\tFire power: %s (cc: %s)\n\t:Melee power: %s/%s (%s/%s)\n\tArmor: (%s/%s)\n\tLed by %s\n\tMelee weapon: %s\n\tRange weapon: %s\n\tSecondary weapon: %s"
                , name
                , type.name()
                , (int) currentStrength
                , (int) type.getStrength()
                , (int) currentMoral
                , (int) getMoral()
                , (int) currentEnergy
                , (int) getEnergy()
                , getFirePower(false, false)
                , getFirePower(false, true)
                , getMeleePower(false, false, Data.UnitArm.INFANTRY)
                , getMeleePower(false, false, Data.UnitArm.CAVALRY)
                , getMeleePower(false, true, Data.UnitArm.INFANTRY)
                , getMeleePower(false, true, Data.UnitArm.CAVALRY)
                , getRangeArmorRate(false)
                , getMeleeArmorRate(false)
                , regimentCommander.toString()
                , type.getMeleeWeapon()
                , type.getRangeWeapon()
                , Arrays.toString(type.getSecondaryWeapons()));
    }

    @Override
    public String toString() {
        return String.format("%s [%s] led by %s"
                , name
                , type.name()
                , regimentCommander.toString());
    }
}
