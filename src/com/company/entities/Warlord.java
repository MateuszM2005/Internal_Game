package com.company.entities;

import com.company.Creature;

public class Warlord extends Creature {
    public Warlord(double xPos, double yPos){
        super(xPos,yPos);
        ENTITY_ID = 6;
        hitboxHeight = 56;
        hitboxWidth = 56;


        BASE_HP = 500;
        BASE_DEF = 100;
        BASE_ATK = 150;
        BASE_MS = 150;
        BASE_JUMP_HEIGHT = 150;

        MAX_HP = BASE_HP;
        CURRENT_HP = BASE_HP;
        DEF = BASE_DEF;
        ATK = BASE_ATK;
        MS = BASE_MS;
        JUMP_HEIGHT = BASE_JUMP_HEIGHT;

        PRIOTITY = 5;
        OFFENSE = 4;
        PROTECTION = 6;
        BACKLINE = false;
        TETHER_RANGE = 20;
        ATTACK_RANGE = 50;
        MAX_CD = cooldownCFW;
        setTexture();
    }

    @Override
    public void cast() {
        if(CD == 0){
            cryForWar();
            CD = MAX_CD;
        }

    }
}
