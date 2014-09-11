package com.example.arpg_gles;

public class BlueGem extends Item
{

    public BlueGem(float x, float y)
    {
        this.x = x;
        this.y = y;
        height = 46;
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
            if(player.gems < 999)
                player.gems += 5;
            state = Item.STATE_DEATH;
        }
    }
}
