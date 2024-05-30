package com.company.entities;

import com.company.Creature;

import static com.company.Board.blockSize;

public class FireDruid extends Creature {
    public FireDruid(double xPos, double yPos){
        super(xPos,yPos);
        ENTITY_ID = 4;
        hitboxHeight = 32;
        hitboxWidth = 32;


        BASE_HP = 200;
        BASE_DEF = 0;
        BASE_ATK = 150;
        BASE_MS = 200;
        BASE_JUMP_HEIGHT = 150;

        MAX_HP = BASE_HP;
        CURRENT_HP = BASE_HP;
        DEF = BASE_DEF;
        ATK = BASE_ATK;
        MS = BASE_MS;
        JUMP_HEIGHT = BASE_JUMP_HEIGHT;

        PRIOTITY = 10;
        OFFENSE = 10;
        PROTECTION = 0;
        BACKLINE = true;
        TETHER_RANGE = 500;
        ATTACK_RANGE = 600;
        MAX_CD = cdFireball;
        setTexture();
    }
    @Override
    public void execute(){
        if(!isActive()){
            stop();
            target = null;
            aggro = false;
            hasDest = false;
            isWandering = false;
            return;
        }
        if(isInMD){
            dashMD();
            lowerCooldowns();
            return;
        }
        Platform platform = getPlatform();
        if(platform == null) return;

        if(!aggro || switchTarget()){
            findTarget();
            if(target != null){//if it has found target
                targetLastPlatform = target.getPlatform();
                if(targetLastPlatform != null && platform.comparePlatforms(targetLastPlatform)){//if the target is on the same platform
                    aggro = true;
                    hasDest = false;
                    findBattlePosition();
                }else { //if not
                    instructions = findPath();
                    if(instructions == null || instructions.empty){ //if target is unnacessible
                        aggro = false;
                        wander();
                    }else { //if it is accessible
                        aggro = true;
                        hasDest = false;
                        followInstructions();
                    }
                }
            } else { //if no target then wander
                aggro = false;
                wander();
            }
        } //it works

        if(aggro){
            if(getDistance(target) > TETHER_RANGE) {
                Platform temp = target.getPlatform();
                if (temp != null) {
                    if (!targetLastPlatform.comparePlatforms(temp)) { //target platform changed
                        targetLastPlatform = temp;
                        if (platform.comparePlatforms(targetLastPlatform)) {//if the target is on the same platform
                            hasDest = false;
                            instructions = null;
                            findBattlePosition();
                        } else { //if not
                            instructions = findPath();
                            if (instructions == null || instructions.empty) { //if target is unnacessible
                                aggro = false;
                                wander();
                            } else { //if it is accessible
                                hasDest = false;
                                followInstructions();
                            }
                        }
                    } else {
                        targetLastPlatform = temp;
                        if (platform.comparePlatforms(targetLastPlatform)) {
                            findBattlePosition();
                        } else if (instructions == null || instructions.empty){
                            aggro = false;
                            wander();
                        }else {
                            followInstructions();
                        }
                    }
                } else {
                    aggro = false;
                    wander();
                }
            }else {
                findBattlePosition();
            }
        }
        move();
        lowerCooldowns();
        if(getDistance(target) < ATTACK_RANGE && CD == 0 && isShotClear((int) (Math.abs(xPos-target.xPos)/blockSize),target.xPos,target.yPos,20,20)){
            if(isTargetEnemy) cast();
        }
    } //finally done

    @Override
    public void cast() {
        if(CD == 0){
            fireball(target.xPos,target.yPos);
            CD = MAX_CD;
        }
    }
}
