package com.company.effects;

import com.company.Effect;

public class DeathSentence extends Effect {
    public DeathSentence(double xPos, double yPos, int width, int height, boolean dir){
        super(xPos,yPos,width,height,dir, "src/textures/effects/death_sentence");
        duration = 0.3;
    }
}
