package com.example.arpg_gles;

public class KeyPlayer extends Thing
{

    KeyPlayer(float x, float y)
    {
        height = 40;
        width = 40;
        this.x = x;
        this.y = y;
        state = 0;
    }

    public void update(Player player1, int i)
    {
    }

    public void collision(Player player)
    {
        if(state != Thing.STATE_DEATH)
        {
            player.keys++;
            state = Thing.STATE_DEATH;
        }
    }
}
