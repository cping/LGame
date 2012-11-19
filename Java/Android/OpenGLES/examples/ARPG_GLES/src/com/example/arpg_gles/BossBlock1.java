package com.example.arpg_gles;

public class BossBlock1 extends Thing
{

    BossBlock1(float x, float y)
    {
        height = 100;
        width = 100;
        this.x = x;
        this.y = y;
    }

    public void update(Player player, int enemiesLeft)
    {
        if(state == Thing.STATE_NORMAL)
        {
            if(player.boss1killed)
                state = Thing.STATE_DEATH;
            collision(player);
        }
    }
}
