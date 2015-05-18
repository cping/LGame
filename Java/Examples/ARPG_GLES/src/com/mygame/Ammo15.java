package com.mygame;

public class Ammo15 extends Item
{

    public Ammo15(float x, float y)
    {
        this.x = x;
        this.y = y;
        height = 40;
        width = 40;
        maxTime = 300;
        time = maxTime;
    }

    public void update(Player player)
    {
        if(time > 0)
            time--;
        if(flickerTime > 0)
        {
            flickerTime--;
            if(!flicker)
                flicker = true;
            else
            if(flicker)
                flicker = false;
        }
        if(time <= 0)
            state = Item.STATE_DEATH;
        flickerCheck();
        collision(player);
    }

    public void collision(Player player)
    {
        if(state != STATE_DEATH && player.hp > 0 && x + width > player.x && x < (player.x + (float)player.width) && y + height > player.y && y < (player.y + (float)player.height))
        {
            if(player.bullets < 999)
                player.bullets += 15;
            state = Item.STATE_DEATH;
        }
    }
}
