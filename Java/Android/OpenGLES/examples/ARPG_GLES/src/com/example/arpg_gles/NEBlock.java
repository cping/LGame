package com.example.arpg_gles;

public class NEBlock extends Thing
{

    NEBlock(float x, float y)
    {
        height = 50;
        width = 50;
        this.x = x;
        this.y = y;
        state = 0;
        solid = true;
    }

    public void update(Player player, int enemiesLeft)
    {
        if(state == Thing.STATE_NORMAL && enemiesLeft <= 0)
            state = Thing.STATE_DEATH;
    }
}
