package com.company;

import com.company.entities.*;

public enum Creatures {
    HERO,
    LEPER,
    CORRUPTED_SOLIDER,
    FIRE_DRUID,
    SHADOW_STRAYER,
    WARLORD,
    NIGHTMARE,
    VICEROY_OF_DEATH;

    private static final Creatures[] creatures = Creatures.values();
    public static final int AMOUNT_OF_ENTITIES = 8;

    public static Creature getByID(int ID, double xPos, double yPos){
        switch (ID){
            case 1: return new Hero(xPos,yPos);
            case 2: return new Leper(xPos,yPos);
            case 3: return new CoruptedSolider(xPos,yPos);
            case 4: return new FireDruid(xPos,yPos);
            case 5: return new ShadowStrayer(xPos,yPos);
            case 6: return new Warlord(xPos,yPos);
            case 7: return new Nightmare(xPos,yPos);
            case 8: return new ViceroyOfDeath(xPos,yPos);
        }
        return null;
    }

}
