package com.company.entities;

import com.company.Creature;


public class CoruptedSolider extends Creature {
    public CoruptedSolider(double xPos, double yPos){
        super(xPos,yPos);
        ENTITY_ID = 3;
        hitboxHeight = 32;
        hitboxWidth = 32;

        BASE_HP = 300;
        BASE_DEF = 100;
        BASE_ATK = 200;
        BASE_MS = 200;
        BASE_JUMP_HEIGHT = 150;

        MAX_HP = BASE_HP;
        CURRENT_HP = BASE_HP;
        DEF = BASE_DEF;
        ATK = BASE_ATK;
        MS = BASE_MS;
        JUMP_HEIGHT = BASE_JUMP_HEIGHT;

        PRIOTITY = 0;
        OFFENSE = 4;
        PROTECTION = 6;
        BACKLINE = false;
        TETHER_RANGE = 40;
        ATTACK_RANGE = 50;
        TETHER_ALLY_RANGE = 100;
        MAX_CD = cdSpear;
        setTexture();
    }


    @Override
    public void cast() {
        if(CD == 0)if (spearThrust(target.xPos)) CD = MAX_CD;
    }
}
