package com.company.effects;

import com.company.Creature;
import com.company.Effect;

public class CryForWar extends Effect {
    Creature parent;
    double parentX;
    double parentY;
    public CryForWar (double xPos, double yPos, int width, int height, boolean dir, Creature parent){
        super(xPos,yPos,width,height,dir, "src/textures/effects/cry_for_war");
        duration = 0.3;
        this.parent = parent;
        parentX = parent.xPos;
        parentY = parent.yPos;
    }
    @Override
    public boolean execute() {
        xPos += parent.xPos - parentX;
        yPos += parent.yPos - parentY;
        image.setX(xPos);
        image.setY(yPos);
        parentX = parent.xPos;
        parentY = parent.yPos;
        return super.execute();
    }
}
