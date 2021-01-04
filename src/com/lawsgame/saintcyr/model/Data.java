package com.lawsgame.saintcyr.model;

public class Data {

    public static final float HITRATE_EXHAUSTION_FACTOR = 0.5f;

    public static final float DIS_AUTH_FACTOR = 1f;
    public static final float MORAL_BRA_FACTOR = 3f;
    public static final float MORAL_CHA_FACTOR = 2f;
    public static final float MORAL_DAMAGE_BASE_VALUE = 1f;
    public static final float MORAL_DAMAGE_CASUALTY_FACTOR = 2f;
    public static final float MORAL_DAMAGE_EXHAUSTION_FACTOR = 1f;

    public static final float COUNTER_CHARGE_SPEED_FACTOR = 6f;
    public static final float COUNTER_CHARGE_SPEED_BASE = 3f;


    public enum Trait {

    }

    public enum OfficerRank {
        COLONEL,
        BRIGADE_GENERAL,
        DIVISION_GENERAL,
        CORPS_GENERAL;
    }

    public enum UnitRank {
        CORPS,
        DIVISION,
        BRIGADE,
        REGIMENT;
    }

    public enum UnitArm {
        INFANTRY,
        CAVALRY,
        ARTILLERY
    }

    public enum Weapon {
        NO_WEAPON(1,1,0, 1, 0, 0, 0, 0, 0, 0),
        RIFLE(1,1,12, 1, 0, 16, 0, 5, 5, 10),
        SABER(1,1,0, 1, 0, 25, 0, 0, 0, 0);

        private int rangeMax;
        private int rangeMin;
        private float firepower;
        private float splashDamage;
        private float moralImpact;
        private float meleePower;
        private float chargeVSInfantry;
        private float chargeVSCavalry;
        private float antiChargeVSInfantry;
        private float antiChargeVSCavalry;

        Weapon(int rangeMax, int rangeMin, float firepower, float damageArea, float moralImpact, float meleePower, float chargeVSInfantry, float chargeVSCavalry, float antiChargeVSInfantry, float antiChargeVSCavalry) {
            this.rangeMax = rangeMax;
            this.rangeMin = rangeMin;
            this.firepower = firepower;
            this.splashDamage = damageArea;
            this.moralImpact = moralImpact;
            this.meleePower = meleePower;
            this.chargeVSInfantry = chargeVSInfantry;
            this.chargeVSCavalry = chargeVSCavalry;
            this.antiChargeVSInfantry = antiChargeVSInfantry;
            this.antiChargeVSCavalry = antiChargeVSCavalry;
        }

        public boolean isRanged() { return firepower > 0; }

        public boolean isMelee() {
            return meleePower > 0;
        }

        public int getRangeMax() {
            return rangeMax;
        }

        public int getRangeMin() {
            return rangeMin;
        }

        public float getFirepower() {
            return firepower;
        }

        public float getSplashDamage() {
            return splashDamage;
        }

        public float getMoralImpact() {
            return moralImpact;
        }

        public float getMeleePower() {
            return meleePower;
        }

        public float getChargeVSInfantry() {
            return chargeVSInfantry;
        }

        public float getChargeVSCavalry() {
            return chargeVSCavalry;
        }

        public float getAntiChargeVSInfantry() {
            return antiChargeVSInfantry;
        }

        public float getAntiChargeVSCavalry() { return antiChargeVSCavalry; }
    }

    public enum CombatFormation {
        LINE        (1.00f,1.00f, 1.0f,1.0f, 0),
        SKIRMISHER  (1.00f,0.50f, 1.0f,1.0f, 0),
        SQUARE      (0.25f,2.00f, 3.0f,0.5f, 0),
        COLUMN      (0.50f,1.50f, 1.5f,1.5f, 0),
        FOURRAGERE  (1.00f,0.80f, 1.0f,1.0f, 0);

        private float fireFront;
        private float density;
        private float antiCav;
        private float maneuvrability;
        private float antiFlankManoeuvre;

        CombatFormation(float fireFront, float density, float antiCav, float maneuvrability, float antiFlankManoeuvre) {
            this.fireFront = fireFront;
            this.density = density;
            this.antiCav = antiCav;
            this.maneuvrability = maneuvrability;
            this.antiFlankManoeuvre = antiFlankManoeuvre;
        }

        public float getFireFront() { return fireFront; }
        public float getDensity() { return density; }
        public float getAntiCav() { return antiCav; }
        public float getManeuvrability() { return maneuvrability; }
        public float getAntiFlankManoeuvre() { return antiFlankManoeuvre; }
    }

    public enum UnitType {
        FUSILIER(UnitArm.INFANTRY ,600,5,16, 19,5, 3, 0, 4,  Weapon.RIFLE,Weapon.RIFLE, new Weapon[0]),
        VOLTIGEUR(UnitArm.INFANTRY,500,5,20, 15,3, 3, 0, 5,  Weapon.RIFLE,Weapon.RIFLE, new Weapon[0]),
        CHASSEUR(UnitArm.CAVALRY  ,300,5, 9,  22,14, 3, 3, 11, Weapon.SABER,Weapon.NO_WEAPON, new Weapon[0])
        ;

        private final UnitArm corps;
        private final int strength;
        private final int bravery;
        private final int fireAbility;
        private final int meleeAbility;
        private final int charge;
        private final int endurance;
        private final int armor;
        private final int speed;
        private Weapon meleeWeapon;
        private Weapon rangeWeapon;
        private Weapon[] secondaryWeapons;

        UnitType(UnitArm corps, int strength, int bravery, int fireAbility, int meleeAbility, int charge, int endurance, int armor, int speed
                , Weapon meleeWeapon, Weapon rangeWeapon, Weapon[] secondaryWeapons) {
            this.corps = corps;
            this.strength = strength;
            this.bravery = bravery;
            this.fireAbility = fireAbility;
            this.meleeAbility = meleeAbility;
            this.charge = charge;
            this.endurance = endurance;
            this.armor = armor;
            this.speed = speed;
            this.meleeWeapon = meleeWeapon;
            this.rangeWeapon = rangeWeapon;
            this.secondaryWeapons = secondaryWeapons;
        }

        public UnitArm getArm() {
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
        public float getMeleeAbility() {
            return meleeAbility;
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
        public Weapon getMeleeWeapon() { return meleeWeapon; }
        public Weapon getRangeWeapon() { return rangeWeapon; }
        public Weapon[] getSecondaryWeapons() { return secondaryWeapons; }
    }


}
