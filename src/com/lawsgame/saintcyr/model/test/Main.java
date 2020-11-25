package com.lawsgame.saintcyr.model.test;

import com.lawsgame.saintcyr.model.BattleSystem;
import com.lawsgame.saintcyr.model.Data;
import com.lawsgame.saintcyr.model.models.Officer;
import com.lawsgame.saintcyr.model.models.Regiment;

public class Main {

    public static void main(String[] args) {
        Regiment fus1 = Regiment.create("1er  RdF", Data.UnitType.FUSILIER);
        fus1.setCommandingOfficer(Officer.create("Brune", 25,  Data.Rank.COLONEL, 1, 1, 0));
        fus1.setRegimentCommander(Officer.create("Brune", 25,  Data.Rank.COLONEL, 1, 1, 0));
        Regiment fus2 = Regiment.create("2ème RdF", Data.UnitType.FUSILIER);
        Regiment cav1 = Regiment.create("1er  RdC", Data.UnitType.CHASSEUR);
        Regiment vol1 = Regiment.create("1er  RdV", Data.UnitType.VOLTIGEUR);

        System.out.println("");
        System.out.println(fus1);
        System.out.println(fus2);
        System.out.println(cav1);
        System.out.println(vol1);


        //testEncounter(fus1, fus2);
        testEncounterAgainst3UnitType(fus1, fus2, cav1, vol1);
    }

    public static void testEncounterAgainst3UnitType(Regiment fus1, Regiment fus2, Regiment cav1, Regiment vol1 ){
        System.out.println("\n________________________________________________");
        System.out.println("\n ***$$$ PHASE: Voltigeur shoot Chasseur 1\n");
        BattleSystem.doRangeEncounter(vol1, cav1);

        vol1.rebuild();
        cav1.rebuild();

        System.out.println("\n________________________________________________");
        System.out.println("\n ***$$$ PHASE: Chasseur charge Voltigeur 1\n");
        BattleSystem.doMeleeEncounter(cav1,vol1, true);

        vol1.rebuild();
        cav1.rebuild();

        System.out.println("\n________________________________________________");
        System.out.println("\n ***$$$ PHASE: Chasseur charge Fusilier 1\n");
        BattleSystem.doMeleeEncounter(cav1,fus1, true);

        fus1.rebuild();
        cav1.rebuild();

        System.out.println("\n________________________________________________");
        System.out.println("\n ***$$$ PHASE: Chasseur fights Voltigeur in melee 1\n");
        BattleSystem.doMeleeEncounter(cav1,vol1, false);

        vol1.rebuild();
        cav1.rebuild();

        System.out.println("\n________________________________________________");
        System.out.println("\n ***$$$ PHASE: Chasseur fights Fusilier in melee 1\n");
        BattleSystem.doMeleeEncounter(cav1,fus1, false);
    }

    public static void testEncounter(Regiment bi1, Regiment bi2){
        System.out.println("\n________________________________________________");
        System.out.println("\n ***$$$ PHASE 1: range encounter 1\n");
        BattleSystem.doRangeEncounter(bi1, bi2);

        bi2.rebuild();
        bi1.rebuild();

        System.out.println("\n________________________________________________");
        System.out.println("***$$$ PHASE 1: charge encounter 1");
        BattleSystem.doMeleeEncounter(bi2, bi1, true);

        bi2.rebuild();
        bi1.rebuild();

        System.out.println("\n________________________________________________");
        System.out.println("\n ***$$$ PHASE 2: melee encounter 1\n");
        BattleSystem.doMeleeEncounter(bi2, bi1, false);

    }

}
