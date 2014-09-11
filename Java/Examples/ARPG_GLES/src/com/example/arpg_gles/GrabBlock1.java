package com.example.arpg_gles;


public class GrabBlock1 extends Thing
{

    GrabBlock1(float x, float y)
    {
        height = 50;
        width = 50;
        this.x = x;
        this.y = y;
        moveable = true;
        solid = true;
    }

    public void update(Player player1, int i)
    {
    }
}
