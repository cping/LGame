package com.mygame;

public class Item
{

    public static int MAX_FRAMES[];
    public static int MAX_FRAME_DELAY[];
    public static int STATE_NORMAL = 0;
    public static int STATE_DYING = 1;
    public static int STATE_DEATH = 2;
    int maxTime;
    boolean flicker;
    int frameDelay;
    int flickerTime;
    int height;
    int width;
    double x;
    double y;
    int state;
    int time;
    public Item()
    {
        maxTime = 300;
        flicker = false;
        frameDelay = 0;
        flickerTime = 0;
        height = 23;
        width = 20;
        x = 400 - width / 2;
        y = 300 - height / 2;
        state = 0;
        time = maxTime;
    }

    public void update(Player player1)
    {
    }

    public void flickerCheck()
    {
        if(time <= maxTime / 2 && state != STATE_DYING && state != STATE_DEATH)
        {
            state = STATE_DYING;
            flicker = false;
            flickerTime = maxTime / 2;
        }
    }

    public void collision(Player player1)
    {
    }


}
