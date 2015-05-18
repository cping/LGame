package com.mygame;


public class Sword1 extends Thing
{

    Sword1(float x, float y)
    {
        height = 40;
        width = 40;
        this.x = x;
        this.y = y;
        state = 0;
    }

    public void update(Player player, int enemiesLeft)
    {
        if(player.hasSword)
            state = Thing.STATE_DEATH;
    }

    public void collision(Player player)
    {
        if(!player.hasSword)
        {
            if(player.rolling)
                player.state -= 4;
            player.rolling = false;
            player.hasSword = true;
            player.pickedUpSword = true;
        }
    }
}
