package com.example.arpg_gles;

public class HexContainer extends Thing
{

    HexContainer(float x, float y)
    {
        height = 40;
        width = 40;
        this.x = x;
        this.y = y;
        state = 0;
    }

    public void update(Player player, int enemiesLeft)
    {
        if(state == Thing.STATE_NORMAL && player.hasHex1)
            state = Thing.STATE_DEATH;
    }

    public void collision(Player player)
    {
        if(player.hp > 0 && x + width > player.x && x < player.x + player.width && y + height > player.y && y < player.y + player.height)
        {
            player.healthLevel++;
            player.hasHex1 = true;
            player.hp = player.healthLevel;
            state = Thing.STATE_DEATH;
        }
    }
}
