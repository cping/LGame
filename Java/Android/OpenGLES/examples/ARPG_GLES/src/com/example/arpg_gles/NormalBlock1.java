package com.example.arpg_gles;

public class NormalBlock1 extends Thing
{

    NormalBlock1(float x, float y)
    {
        height = 50;
        width = 50;
        this.x = x;
        this.y = y;
        state = 0;
        solid = true;
    }

    public void update(Player player1, int i)
    {
    }
}
