package com.lawsgame.saintcyr.model;


import com.lawsgame.saintcyr.model.models.Regiment;
import com.lawsgame.saintcyr.model.tools.MathTools;


public class BattleSystem {


    public static float calcRangeHitRate(Regiment regiment, Regiment opponent, boolean counterCharge) {
        float baseHR = regiment.getRangeHitRate(true, counterCharge);

        float spd = regiment.getType().getRangeWeapon().getSplashDamage();
        float splashDamage = MathTools.gauss(spd, (float)Math.sqrt(spd  - 1.0f));

        float vulnerability = opponent.getRangeVulnerability(true);
        return baseHR * splashDamage * vulnerability;
    }

    public static float calcRangeWoundRate(Regiment regiment, Regiment opponent, boolean  counterCharge){
        float baseWR = regiment.getRangeWoundRate(true, counterCharge);
        float armorRate = opponent.getRangeArmorRate(true);
        return baseWR * (1 - armorRate) ;
    }


    public static int calcVolleysAgainstCharge(Regiment charged, Regiment charging){
        return (int) (Data.COUNTER_CHARGE_SPEED_BASE -  charging.getCombatSpeed(true) / Data.COUNTER_CHARGE_SPEED_FACTOR);
    }

    public static int[] calcRangeExpectedDead(Regiment regiment, Regiment opponent, boolean counterCharge){
        float firepower = calcRangeHitRate(regiment, opponent, counterCharge) * calcRangeWoundRate(regiment, opponent, counterCharge);
        return MathTools.calcBinomialLawExpectation((int) regiment.getCurrentStrength(), firepower);
    }

    private static float calcMeleeHitRate(Regiment attacker, Regiment defender, boolean charge) {
        float baseHR = attacker.getMeleeHitRate(true, charge, attacker.getType().getArm());
        float avoidRate = defender.getMeleeAvoid(true, charge, attacker.getType().getArm());
        return baseHR * (1 - avoidRate);
    }


    private static float calcMeleeWoundRate(Regiment attacker, Regiment defender, boolean charge) {
        float baseWR = attacker.getMeleeWoundRate(true,charge, defender.getType().getArm());
        float armorRate = defender.getMeleeArmorRate(true);
        return  baseWR * (1 - armorRate);
    }


    public static int[] calcMeleeExpectedDead(Regiment regiment, Regiment opponent, boolean charge){
        float hitRate = calcMeleeHitRate(regiment, opponent, charge);
        float woundRate = calcMeleeWoundRate(regiment, opponent, charge);
        return MathTools.calcBinomialLawExpectation((int) regiment.getCurrentStrength(), hitRate * woundRate);
    }

    public static int calcMoralDamageGrossOutput(Regiment attacker, Regiment defender, int casualties, boolean melee){
        float strengthFactor = (1f - defender.getCurrentStrength() / defender.type().getStrength()) * Data.MORAL_DAMAGE_CASUALTY_FACTOR;
        float fatigueFactor = (defender.isExhausted()) ? Data.MORAL_DAMAGE_EXHAUSTION_FACTOR : 0f;
        float attackWeaponFactor = (melee) ? attacker.getType().getMeleeWeapon().getMoralImpact() : attacker.getType().getRangeWeapon().getMoralImpact();
        return (int) (casualties * (Data.MORAL_DAMAGE_BASE_VALUE + strengthFactor + fatigueFactor + attackWeaponFactor));
    }

    public static int calcDefenderDisciple(Regiment attacker, Regiment defender){
        return (int) defender.getDisciple();
    }

    public static int calcMoralDamage(Regiment attacker, Regiment defender, int casualties, boolean melee){
        int moralDamage = calcMoralDamageGrossOutput(attacker, defender, casualties, melee) - calcDefenderDisciple(attacker, defender);
        return (moralDamage < 1 ) ? 1 : moralDamage;
    }


    public static void doRangeEncounter(Regiment initiator, Regiment target){
        System.out.println("RANGE ENCOUNTER ("+initiator.getName()+")");
        logExpected(initiator, target, true, false);

        doRangeAttack(initiator, target, false);
        log(initiator, target);

    }

    public static void doRangeAttack(Regiment attacker, Regiment defender, boolean chargeStatus) {
        try {

            float hitRate = calcRangeHitRate(attacker, defender, chargeStatus);
            int hitOnDefSide = MathTools.doBinomialLaw((int) attacker.getCurrentStrength(), hitRate);
            System.out.printf("\nHR: %s => %s touched (%s)\n", hitRate, defender.getName(), hitOnDefSide);

            float woundRate = calcRangeWoundRate(attacker, defender, chargeStatus);
            int woundedOnDefSide = MathTools.doBinomialLaw(hitOnDefSide, woundRate);
            defender.setCurrentStrength(defender.getCurrentStrength()  - woundedOnDefSide);
            System.out.printf("WR: %s => %s wounded (%s) remaining (%s)\n", woundRate, defender.getName(), woundedOnDefSide, (int)defender.getCurrentStrength());

            if(defender.getCurrentStrength() <= 0){
                System.out.printf("\n%s is WIPED OUT\n", defender.getName());
            }else{
                int moralDamageGrossOutput = calcMoralDamageGrossOutput(attacker, defender, woundedOnDefSide, false);
                int moralResilience = calcDefenderDisciple(attacker, defender);
                int moralDamage = calcMoralDamage(attacker, defender, woundedOnDefSide, false);
                defender.setCurrentMoral(defender.getCurrentMoral() - moralDamage);
                System.out.printf("Moral damage inflicted (%s = %s - %s)\n ",moralDamage,  moralDamageGrossOutput, moralResilience);
                if(defender.isDemoralized()) {
                    System.out.printf("\n%s SURRENDERS\n", defender.getName());
                }else if(defender.isShaken()){
                    System.out.printf("\n%s RETREATS\n", defender.getName());
                }
            }
        } catch (MathTools.MathToolsException e) {
            e.printStackTrace();
        }
    }

    public static void doMeleeEncounter(Regiment initiator, Regiment target, boolean charge){
        System.out.println("MELEE ENCOUNTER "+((charge)?"(charge)":""));

        logExpected(initiator, target, false, charge);

        if(target.hasRangeOptions() && charge){
            int nbVolleys = calcVolleysAgainstCharge(target, initiator);
            System.out.println("\nNumber of volleys: "+nbVolleys);
            int i = 0;
            while(i < nbVolleys && !initiator.isShaken()) {
                doRangeAttack(target, initiator, charge);
                i++;
            }
            log(initiator, target);
        }

        if(!initiator.isShaken()) {
            doMeleeAttack(initiator, target, charge);
            log(initiator, target);

            if(!target.isShaken() && !charge){
                doMeleeAttack(target, initiator, charge);
                log(initiator, target);
            }
        }
    }


    public static void doMeleeAttack(Regiment attacker, Regiment defender, boolean charge){
        try {
            float hitRate = calcMeleeHitRate(attacker, defender, charge);
            int hitOnDefSide = MathTools.doBinomialLaw((int) attacker.getCurrentStrength(), hitRate);
            System.out.printf("\nHR: %s => %s touched (%s)\n", hitRate, defender.getName(), hitOnDefSide);

            float woundRate = calcMeleeWoundRate(attacker, defender, charge);
            int woundedOnDefSide = MathTools.doBinomialLaw(hitOnDefSide, woundRate);
            defender.setCurrentStrength( defender.getCurrentStrength() - woundedOnDefSide);
            System.out.printf("WR: %s => %s wounded (%s) remaining (%s)\n", attacker.getMeleeWoundRate(true, charge, defender.getType().getArm()), defender.getName(), woundedOnDefSide, (int)defender.getCurrentStrength());

            if(defender.getCurrentStrength() <= 0){
                System.out.printf("\n%s is WIPED OUT\n", defender.getName());
            }else{
                int moralDamageGrossOutput = calcMoralDamageGrossOutput(attacker, defender, woundedOnDefSide, true);
                int moralResilience = calcDefenderDisciple(attacker, defender);
                int moralDamageNetOutput = calcMoralDamage(attacker, defender, woundedOnDefSide, true);
                defender.setCurrentMoral(defender.getCurrentMoral() - moralDamageNetOutput);
                System.out.printf("Moral damage inflicted (%s = %s - %s)\n ",moralDamageNetOutput,  moralDamageGrossOutput, moralResilience);
                if(defender.isDemoralized()) {
                    System.out.printf("\n%s SURRENDERS\n", defender.getName());
                }else if(defender.isShaken()){
                    System.out.printf("\n%s RETREATS\n", defender.getName());
                }
            }
        } catch (MathTools.MathToolsException e) {
            e.printStackTrace();
        }
    }

    public static void logExpected(Regiment attacker, Regiment defender, boolean range, boolean charge){
        String attackerExp = "?";
        String defenderExp = "?";
        String counterChargeExp = "";
        if(range){
            int[] attexp = calcRangeExpectedDead(attacker, defender, false);
            int[] defexp = calcRangeExpectedDead(defender, attacker, false);
            attackerExp = String.format(" [%s %s %s]", attexp[0], attexp[1], attexp[2]);
            defenderExp = String.format(" [%s %s %s]", defexp[0], defexp[1], defexp[2]);
        }else{
            int[] attexp = calcMeleeExpectedDead(attacker, defender, charge);
            int[] defexp = calcMeleeExpectedDead(defender, attacker, false);
            attackerExp = String.format(" [%s %s %s]", attexp[0], attexp[1], attexp[2]);
            defenderExp = String.format(" [%s %s %s]", defexp[0], defexp[1], defexp[2]);
            if(charge){
                int[] ccexp = calcRangeExpectedDead(defender, attacker, true);
                counterChargeExp = String.format(" [%s %s %s] then", ccexp[0], ccexp[1], ccexp[2]);
            }
        }
        System.out.printf("\n\t%s > strength (%s) and moral (%s | %s > %s) %s\n"
                , attacker.getName()
                , (int) attacker.getCurrentStrength()
                , (int) attacker.getCurrentMoral()
                , (int) attacker.getMinMoral()
                , (int) attacker.getMoral()
                , attackerExp);
        System.out.printf("\t%s > strength (%s) and moral (%s | %s > %s) %s%s\n"
                , defender.getName()
                , (int) defender.getCurrentStrength()
                , (int) defender.getCurrentMoral()
                , (int) defender.getMinMoral()
                , (int) defender.getMoral()
                , counterChargeExp
                , defenderExp);
    }

    public static void log(Regiment r1, Regiment r2){
        System.out.printf("\n\t%s  | strength (%s) and moral (%s)\n", r1.getName(), (int) r1.getCurrentStrength(), (int) r1.getCurrentMoral());
        System.out.printf("\t%s  | strength (%s) and moral (%s)\n", r2.getName(), (int) r2.getCurrentStrength(), (int) r2.getCurrentMoral());
    }


}
