package com.company.entities;

import com.company.Board;
import com.company.Creature;
import com.company.Entity;
import com.company.Painter;

import java.io.IOException;

import static com.company.Board.creatures;

public class DeathTotem extends Creature {
    Creature owner;
    public DeathTotem(double xPos, double yPos, Creature root){
        super(xPos,yPos);
        ENTITY_ID = 101;
        hitboxHeight = 40;
        hitboxWidth = 40;

        owner = root;
        owner.deathTotemCount++;

        BASE_HP = 1000;
        BASE_DEF = 50;
        BASE_ATK = 2;

        MAX_HP = BASE_HP;
        CURRENT_HP = BASE_HP;
        DEF = BASE_DEF;
        ATK = BASE_ATK;

        ATTACK_RANGE = 500;
        MAX_CD = 0.1;
        setTexture();
    }
    double heal_modifier = 0.5;
    @Override
    public void execute(){
        if(CD == 0){
            int healPool = 0;
            for(Creature c : creatures){
                if(c != owner && c!= this && getDistance(c) < ATTACK_RANGE){
                    c.takeDamage(ATK,true);
                    CD = MAX_CD;
                    healPool += ATK*heal_modifier;
                }
            }
            owner.CURRENT_HP = Math.min(owner.MAX_HP, owner.CURRENT_HP + healPool);
            if(owner == Board.hero){
                try {
                    Painter.paintHealthbar();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        lowerCooldowns();
    }

    @Override
    public void die() {
        owner.deathTotemCount--;
        super.die();

    }

    @Override
    public void cast() {

    }
}
