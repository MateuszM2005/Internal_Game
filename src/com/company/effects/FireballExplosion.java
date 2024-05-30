package com.company.effects;

import com.company.Effect;

public class FireballExplosion extends Effect {
    public FireballExplosion (double xPos, double yPos, int width, int height, boolean dir){
        super(xPos,yPos,width,height,dir, "src/textures/effects/fireball_explosion");
        duration = 0.3;
    }
}
