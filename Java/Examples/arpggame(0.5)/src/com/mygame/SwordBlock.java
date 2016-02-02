package com.mygame;

public class SwordBlock extends Thing
{

    SwordBlock(float x, float y)
    {
        height = 50;
        width = 50;
        this.x = x;
        this.y = y;
        solid = true;
    }

    public void update(Player player, int enemiesLeft)
    {
        if(state == Thing.STATE_NORMAL && player.hasSword)
            state = Thing.STATE_DEATH;
    }
}
