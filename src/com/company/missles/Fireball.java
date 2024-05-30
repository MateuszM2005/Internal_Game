package com.company.missles;

import com.company.Board;
import com.company.Creature;
import com.company.Missile;
import com.company.effects.FireballExplosion;

import static com.company.Board.hero;
import static com.company.GameController.FPS;

public class Fireball extends Missile {


    public Fireball(Creature root, double xPos, double yPos, double xTar, double yTar, double duration) {
        super(root, xPos, yPos, xTar, yTar, duration,1000);
        ENTITY_COLLIDING = true;
        TERRAIN_COLLIDING = true;
        MISSILE_ID = 3;
        hitboxHeight = 20;
        hitboxWidth = 20;
        setTexture();
    }
    @Override
    public boolean checkCollision(double xMovement, double yMovement){
        if(TERRAIN_COLLIDING){
            if(Math.abs(xMovement) < Math.abs(xVel/ FPS) || Math.abs(yMovement) < Math.abs(yVel/FPS)){
                return true;
            }
        }

        if(ENTITY_COLLIDING){
            if(root == hero){
                for( Creature c : Board.creatures){
                    if(c != root && isColliding(c)){
                        return true;
                    }
                }
            }else {
                return isColliding(hero);
            }

        }
        return false;
    }

    @Override
    public void pop(){
        if(root == hero){
            for(Creature c : Board.creatures){
                if(c.isColliding(xPos - root.explosionFB/2.0,yPos - root.explosionFB/2.0, root.explosionFB,root.explosionFB))
                    c.takeDamage((int) (root.ATK*root.modifierFB),false);
            }
        }else {
            if(hero.isColliding(xPos - root.explosionFB/2.0,yPos - root.explosionFB/2.0, root.explosionFB,root.explosionFB))
                hero.takeDamage((int) (root.ATK*root.modifierFB),false);
        }
        new FireballExplosion(xPos - root.explosionFB/2.0,yPos - root.explosionFB/2.0, root.explosionFB,root.explosionFB,false);
    }
}
