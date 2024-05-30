package com.company.missles;

import com.company.*;

import static com.company.Board.hero;

public class HeroOrb extends Missile {
    public HeroOrb(Creature root, double xPos, double yPos, double xVel, double yVel, double duration) {
        super(root, xPos, yPos, xVel, yVel, duration,1000);
        ENTITY_COLLIDING = true;
        TERRAIN_COLLIDING = true;
        MISSILE_ID = 2;
        hitboxHeight = 12;
        hitboxWidth = 12;
        setTexture();
    }

    @Override
    public void pop(){
        for(Creature c : Board.creatures){
            if(c!= root  && isColliding(c)){
                if(c.ENTITY_ID < 100) hero.getAbility(c.ENTITY_ID);
                c.takeDamage(hero.ATK,false);
                return;
            }
        }
    }

}
