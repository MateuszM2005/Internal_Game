package com.company.entities;

import com.company.Creature;
import com.company.GameController;

public class Leper extends Creature {
    public Leper(double xPos, double yPos){
        super(xPos,yPos);
        ENTITY_ID = 2;
        hitboxHeight = 32;
        hitboxWidth = 32;

        BASE_HP = 400;
        BASE_DEF = 100;
        BASE_ATK = 100;
        BASE_MS = 200;
        BASE_JUMP_HEIGHT = 150;

        MAX_HP = BASE_HP;
        CURRENT_HP = BASE_HP;
        DEF = BASE_DEF;
        ATK = BASE_ATK;
        MS = BASE_MS;
        JUMP_HEIGHT = BASE_JUMP_HEIGHT;

        PRIOTITY = 0;
        OFFENSE = 10;
        PROTECTION = 0;
        BACKLINE = false;

        TETHER_RANGE = 10;
        ATTACK_RANGE = 50;
        MAX_CD = cdLepers;
        setTexture();
    }

    @Override
    public void cast(){
        if(CD == 0) if(lepersTouch(target)) CD = MAX_CD;
    }
}
