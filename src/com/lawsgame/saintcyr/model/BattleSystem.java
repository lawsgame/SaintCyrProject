package com.lawsgame.saintcyr.model;


import com.lawsgame.saintcyr.model.models.Regiment;
import com.lawsgame.saintcyr.model.tools.MathTools;

public class BattleSystem {


    public static float calcRangeHitRate(Regiment regiment, Regiment opponent, boolean onCounterCharge) {

        float rawHitRate = regiment.getRangeHitRate(true);
        float numerousFactor = (float) Math.log10(Math.sqrt(opponent.getCurrentStrength()));
        rawHitRate *= numerousFactor;
        if(onCounterCharge){
            float discipleFactor = Math.min(2f, regiment.getDisciple() / 10.0f);
            rawHitRate *= (discipleFactor > 0) ? discipleFactor : 0;
        }

        float avoidRate = regiment.getRangeAvoid(true);

        return (rawHitRate  - avoidRate > 0.01f) ? rawHitRate  - avoidRate: 0.01f;
    }


    public static float calcRangeWoundRate(Regiment regiment, Regiment opponent, boolean onCounterCharge){
        float rawWoundRate = regiment.getRangeWoundRate(true);
        float resilienceRate = opponent.getWoundResilience(true);
        return (rawWoundRate - resilienceRate > 0.01f) ? rawWoundRate - resilienceRate : 0.01f ;
    }


    public static int calcVolleysAgainstCharge(Regiment charged, Regiment charging){
        return (int) (charged.type().getRange() / (5 * charging.getCombatSpeed(true)));
    }



    public static int[] calcRangeExpectedDead(Regiment regiment, Regiment opponent, boolean onCounterCharge){
        float firepower = calcRangeHitRate(regiment, opponent, onCounterCharge) * calcRangeWoundRate(regiment, opponent, onCounterCharge);
        return MathTools.calcBinomialLawExpectation((int)regiment.getCurrentStrength(), firepower);
    }


    private static float calcMeleeHitRate(Regiment attacker, Regiment defender, boolean onCharge) {
        float rawHitRate = attacker.getMeleeHitRate(true, onCharge);
        float avoidRate = defender.getMeleeAvoid(true, onCharge);
        return  (rawHitRate  - avoidRate > 0.01f) ? rawHitRate  - avoidRate: 0;
    }


    private static float calcMeleeWoundRate(Regiment attacker, Regiment defender, boolean onCharge) {
        float rawHitRate = attacker.getMeleeWoundRate(true, onCharge);
        float avoidRate = defender.getWoundResilience(true);
        return  (rawHitRate  - avoidRate > 0.01f) ? rawHitRate  - avoidRate: 0;
    }


    public static int[] calcMeleeExpectedDead(Regiment regiment, Regiment opponent, boolean onCharge){
        float hitRate = calcMeleeHitRate(regiment, opponent, onCharge);
        float woundRate = calcMeleeWoundRate(regiment, opponent, onCharge);
        return MathTools.calcBinomialLawExpectation((int) regiment.getCurrentStrength(), hitRate*woundRate);
    }

    public static int calcMoralDamageGrossOutput(Regiment attacker, Regiment defender, int casualties){
        float strengthFactor = (1f - defender.getCurrentStrength() / defender.type().getStrength()) * Data.MORAL_DAMAGE_CASUALTY_FACTOR;
        float fatigueFactor = (defender.isExhausted()) ? Data.MORAL_DAMAGE_EXHAUSTION_FACTOR : 0f;
        return (int) (casualties * (Data.MORAL_DAMAGE_BASE_VALUE + strengthFactor + fatigueFactor));
    }

    public static int calcMoralDamageResilience(Regiment attacker, Regiment defender){
        return (int) defender.getDisciple();
    }

    public static int calcMoralDamage(Regiment attacker, Regiment defender, int casualties){
        int moralDamage = calcMoralDamageGrossOutput(attacker, defender, casualties) - calcMoralDamageResilience(attacker, defender);
        return (moralDamage < 1 ) ? 1 : moralDamage;
    }


    public static void doRangeEncounter(Regiment initiator, Regiment target){
        System.out.println("RANGE ENCOUNTER ("+initiator.getName()+")");
        logExpected(initiator, target, true, false);

        doRangeAttack(initiator, target, false);
        log(initiator, target);

    }

    public static void doRangeAttack(Regiment attacker, Regiment defender, boolean onCharge) {
        try {
            float hitRate = calcRangeHitRate(attacker, defender, onCharge);
            int hitOnDefSide = MathTools.doBinomialLaw((int) attacker.getCurrentStrength(), hitRate);
            System.out.printf("\nHR: %s => %s touched (%s)\n", hitRate, defender.getName(), hitOnDefSide);

            float woundRate = calcRangeWoundRate(attacker, defender, onCharge);
            int woundedOnDefSide = MathTools.doBinomialLaw(hitOnDefSide, woundRate);
            defender.setCurrentStrength(defender.getCurrentStrength()  - woundedOnDefSide);
            System.out.printf("WR: %s => %s wounded (%s) remaining (%s)\n", woundRate, defender.getName(), woundedOnDefSide, (int)defender.getCurrentStrength());

            if(defender.getCurrentStrength() <= 0){
                System.out.printf("\n%s is WIPED OUT\n", defender.getName());
            }else{
                int moralDamageGrossOutput = calcMoralDamageGrossOutput(attacker, defender, woundedOnDefSide);
                int moralResilience = calcMoralDamageResilience(attacker, defender);
                int moralDamageNetOutput = calcMoralDamage(attacker, defender, woundedOnDefSide);
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

    public static void doMeleeEncounter(Regiment initiator, Regiment target, boolean onCharge){
        System.out.println("MELEE ENCOUNTER "+((onCharge)?"(charge)":""));

        logExpected(initiator, target, false, onCharge);

        if(target.hasRangeOptions() && onCharge){
            int nbVolleys = calcVolleysAgainstCharge(target, initiator);
            System.out.println("\nNumber of volleys: "+nbVolleys);
            int i = 0;
            while(i < nbVolleys && !initiator.isShaken()) {
                doRangeAttack(target, initiator, true);
                i++;
            }
            log(initiator, target);
        }

        if(!initiator.isShaken()) {
            doMeleeAttack(initiator, target, onCharge);
            log(initiator, target);

            if(!target.isShaken() && !onCharge){
                doMeleeAttack(target, initiator, onCharge);
                log(initiator, target);
            }
        }
    }


    public static void doMeleeAttack(Regiment attacker, Regiment defender, boolean onCharge){
        try {
            float hitRate = calcMeleeHitRate(attacker, defender, onCharge);
            int hitOnDefSide = MathTools.doBinomialLaw((int) attacker.getCurrentStrength(), hitRate);
            System.out.printf("\nHR: %s => %s touched (%s)\n", hitRate, defender.getName(), hitOnDefSide);

            float woundRate = calcMeleeWoundRate(attacker, defender, onCharge);
            int woundedOnDefSide = MathTools.doBinomialLaw(hitOnDefSide, woundRate);
            defender.setCurrentStrength( defender.getCurrentStrength() - woundedOnDefSide);
            System.out.printf("WR: %s => %s wounded (%s) remaining (%s)\n", attacker.getMeleeWoundRate(true, onCharge), defender.getName(), woundedOnDefSide, (int)defender.getCurrentStrength());

            if(defender.getCurrentStrength() <= 0){
                System.out.printf("\n%s is WIPED OUT\n", defender.getName());
            }else{
                int moralDamageGrossOutput = calcMoralDamageGrossOutput(attacker, defender, woundedOnDefSide);
                int moralResilience = calcMoralDamageResilience(attacker, defender);
                int moralDamageNetOutput = calcMoralDamage(attacker, defender, woundedOnDefSide);
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

    public static void logExpected(Regiment attacker, Regiment defender, boolean range, boolean onCharge){
        String attackerExp = "?";
        String defenderExp = "?";
        String counterChargeExp = "";
        if(range){
            int[] attexp = calcRangeExpectedDead(attacker, defender, false);
            int[] defexp = calcRangeExpectedDead(defender, attacker, false);
            attackerExp = String.format(" [%s %s %s]", attexp[0], attexp[1], attexp[2]);
            defenderExp = String.format(" [%s %s %s]", defexp[0], defexp[1], defexp[2]);
        }else{
            int[] attexp = calcMeleeExpectedDead(attacker, defender, onCharge);
            int[] defexp = calcMeleeExpectedDead(defender, attacker, false);
            attackerExp = String.format(" [%s %s %s]", attexp[0], attexp[1], attexp[2]);
            defenderExp = String.format(" [%s %s %s]", defexp[0], defexp[1], defexp[2]);
            if(onCharge){
                int[] ccexp = calcRangeExpectedDead(defender, attacker, true);
                counterChargeExp = String.format(" [%s %s %s] then", ccexp[0], ccexp[1], ccexp[2]);
            }
        }
        System.out.printf("\n\t%s > strength (%s) and moral (%s | %s => %s) %s\n"
                , attacker.getName()
                , (int) attacker.getCurrentStrength()
                , (int) attacker.getCurrentMoral()
                , (int) attacker.getMinMoral()
                , (int) attacker.getMoral()
                , attackerExp);
        System.out.printf("\t%s > strength (%s) and moral (%s | %s => %s) %s%s\n"
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
