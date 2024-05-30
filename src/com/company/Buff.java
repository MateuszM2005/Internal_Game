package com.company;

public class Buff {
    boolean REMOVE;
    double duration;
    int ARMOR;
    int ATK;
    Creature root;
    Buff(Creature root, double duration, int ARMOR, int ATK){
        Board.buffs.add(this);
        this.root = root;
        this.duration = duration;
        this.ARMOR = ARMOR;
        this.ATK = ATK;
        root.ATK += ATK;
        root.DEF = this.ARMOR;
    }
    public void tick(){
        duration -= 1.0/GameController.FPS;
        if(duration <= 0 && !REMOVE){
            root.ATK -= ATK;
            root.DEF -= ARMOR;
            REMOVE = true;
        }
    }
}
