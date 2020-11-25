package com.lawsgame.saintcyr.model;

public class Data {

    public static final float HITRATE_MELEE_BASE = 0.20f;
    public static final float HITRATE_EXHAUSTION_FACTOR = 0.5f;

    public static final float MORAL_DAMAGE_BASE_VALUE = 1f;
    public static final float MORAL_DAMAGE_CASUALTY_FACTOR = 2f;
    public static final float MORAL_DAMAGE_EXHAUSTION_FACTOR = 1f;

    public enum UnitCorps {
        INFANTRY,
        CAVALRY,
        ARTILLERY
    }

    public enum UnitAbility{
        SKIRMISHER;
    }

    public enum CombatFormation {
        LINE        (1.00f,0.00f, 1.0f,1.0f, 0, 0),
        SKIRMISHER  (1.00f,0.10f, 1.0f,1.0f, 0, 0),
        SQUARE      (0.25f,0.00f, 3.0f,0.5f, 0, 0),
        COLUMN      (0.50f,0.00f, 1.5f,1.5f, 0, 0),
        FOURRAGERE  (1.00f,0.05f, 1.0f,1.0f, 0, 0);

        private float fireHitRateFactor;
        private float rangeAvoid;
        private float chargeAvoid;
        private float combatSpeedFactor;
        private float campaignSpeedFactor;
        private float pursuitHitFactor;

        CombatFormation(float fireHitRateFactor, float rangeAvoid, float chargeAvoid, float combatSpeedFactor, float campaignSpeedFactor, float pursuitHitFactor) {
            this.fireHitRateFactor = fireHitRateFactor;
            this.rangeAvoid = rangeAvoid;
            this.chargeAvoid = chargeAvoid;
            this.combatSpeedFactor = combatSpeedFactor;
            this.campaignSpeedFactor = campaignSpeedFactor;
            this.pursuitHitFactor = pursuitHitFactor;
        }

        public float getFireHitRateFactor() { return fireHitRateFactor; }
        public float getRangeAvoid() { return rangeAvoid; }
        public float getChargeAvoid() { return chargeAvoid; }
        public float getCombatSpeedFactor() { return combatSpeedFactor; }
        public float getCampaignSpeedFactor() { return campaignSpeedFactor; }
        public float getPursuitHitFactor() { return pursuitHitFactor; }
    }

    public enum UnitType {
        FUSILIER(UnitCorps.INFANTRY ,600,5,13,20,70, 9,20, 5, 3, 0, 4,new UnitAbility[0]),
        VOLTIGEUR(UnitCorps.INFANTRY,500,5,15,20,90, 5,20, 3, 3, 0, 5,new UnitAbility[0]),
        CHASSEUR(UnitCorps.CAVALRY  ,300,5, 0, 0,  0,6, 25,21, 3, 0, 11,new UnitAbility[0])
        ;

        private final UnitCorps corps;
        private final int strength;
        private final int bravery;
        private final int fireAbility;
        private final int firePower;
        private final int range;
        private final int meleeAbility;
        private final int meleePower;
        private final int charge;
        private final int endurance;
        private final int armor;
        private final int speed;
        private final UnitAbility[] abilities;

        UnitType(UnitCorps corps, int strength, int bravery, int fireAbility, int firePower, int range, int meleeAbility, int meleePower, int charge, int endurance, int armor, int speed, UnitAbility[] abilities) {
            this.corps = corps;
            this.strength = strength;
            this.bravery = bravery;
            this.fireAbility = fireAbility;
            this.firePower = firePower;
            this.range = range;
            this.meleeAbility = meleeAbility;
            this.meleePower = meleePower;
            this.charge = charge;
            this.endurance = endurance;
            this.armor = armor;
            this.speed = speed;
            this.abilities = abilities;
        }

        public UnitCorps getCorps() {
            return corps;
        }
        public float getStrength() {
            return strength;
        }
        public float getBravery() {
            return bravery;
        }
        public float getFireAbility() {
            return fireAbility;
        }
        public float getFirePower() {
            return firePower;
        }
        public float getRange() {
            return range;
        }
        public float getMeleeAbility() {
            return meleeAbility;
        }
        public float getMeleePower() {
            return meleePower;
        }
        public float getCharge() {
            return charge;
        }
        public float getEndurance() {
            return endurance;
        }
        public float getArmor() {
            return armor;
        }
        public float getSpeed() {
            return speed;
        }
        public UnitAbility[] getAbilities() {
            return abilities;
        }
    }


}
