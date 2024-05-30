package com.company.entities;

import com.company.Creature;

import static com.company.Board.hero;

public class ViceroyOfDeath extends Creature {
    public ViceroyOfDeath(double xPos, double yPos){
        super(xPos,yPos);
        ENTITY_ID = 8;
        hitboxHeight = 32;
        hitboxWidth = 32;


        BASE_HP = 1000;
        BASE_DEF = 150;
        BASE_ATK = 0;
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
        BACKLINE = true;
        TETHER_RANGE = 300;
        ATTACK_RANGE = 400;
        MAX_CD = 0;
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

        if(!aggro){
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
            if(target.CURRENT_HP/target.MAX_HP <= 0.4 || getDistance(target) < 300){
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
            }else {
                wander();
            }

        }
        move();
        lowerCooldowns();

        if(getDistance(hero) < ATTACK_RANGE && deathTotemCount == 0){
            cast();
        }
    }

    @Override
    public void cast() {
        if(deathTotemCount != 0) return;
        placeDeathTotem();
        deathTotemCount++;
    }
}
